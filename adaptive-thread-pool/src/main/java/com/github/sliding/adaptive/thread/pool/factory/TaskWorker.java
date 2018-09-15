package com.github.sliding.adaptive.thread.pool.factory;

import com.github.sliding.adaptive.thread.pool.flow.EventPublisherFactory;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.listener.event.task.TaskEvent;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class TaskWorker extends Thread {

    private final String threadPoolIdentifier;
    private final Command<Task> taskCommand;

    /**
     * {@inheritDoc}
     */
    public TaskWorker(String threadPoolIdentifier, Command<Task> taskCommand, String threadName) {
        super(threadName);
        this.threadPoolIdentifier = threadPoolIdentifier;
        this.taskCommand = taskCommand;
    }

    @Override
    public void run() {
        Task task = null;
        //avoid threads leaking
        try {
            //check for shutdown
            while (!isInterrupted()) {
                Optional<Task> taskOptional = Optional.ofNullable(taskCommand.remove());
                if (taskOptional.isPresent()) {
                    task = taskOptional.get();
                    beforeExecute(task);
                    task.run();
                    afterExecute(task);
                }
            }

        } catch (RuntimeException ex) {
            log.warn("Could not execute task [{}]", task, ex);
            //do not rethrow the exception to avoid leaking threads from pool
            //TODO push this exception to an exceptionHandler
        } finally {
            log.info("Adaptive thread [{}] was interrupted", getName());
        }
    }

    private void afterExecute(Task task) {
        task.writeMetric(EventType.TASK_FINISHED_TIME);

        EventPublisherFactory.TASK_EVENT
                .getEventPublisher()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_FINISHED_TIME)
                        .createEvent());

    }

    private void beforeExecute(Task task) {
        task.writeMetric(EventType.TASK_STARTS_EXECUTION);

        EventPublisherFactory.TASK_EVENT
                .getEventPublisher()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_STARTS_EXECUTION)
                        .createEvent());

    }
}