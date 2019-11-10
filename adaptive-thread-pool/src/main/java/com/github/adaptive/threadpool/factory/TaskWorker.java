package com.github.adaptive.threadpool.factory;

import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.listener.event.task.TaskEvent;
import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.flow.EventPublisherFactory;
import com.github.adaptive.threadpool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class TaskWorker extends Thread {

    private final Command<Task> taskCommand;

    private boolean stopWorker;

    /**
     * {@inheritDoc}
     */
    public TaskWorker(Command<Task> taskCommand, String threadName) {
        super(threadName);
        this.taskCommand = taskCommand;
    }

    @Override
    public void run() {
        Task task = null;
        //avoid threads leaking
        try {
            //check for shutdown
            while (!isInterrupted()) {
                if (stopWorker) {
                    break;
                }
                Optional<Task> taskOptional = Optional.ofNullable(taskCommand.remove());
                if (taskOptional.isPresent()) {
                    task = taskOptional.get();
                    beforeExecute(task);
                    task.run();
                    afterExecute(task);
                }
            }

        } catch (Exception ex) {
            log.warn("Could not execute task [{}]", task, ex);
            //do not rethrow the exception to avoid leaking threads from pool
            //TODO push this exception to an exceptionHandler
        } finally {
            log.info("Adaptive thread [{}] was interrupted", getName());
        }
    }

    public void stopWorker(boolean stopWorker) {
        this.stopWorker = stopWorker;
    }

    private void afterExecute(Task task) {
        task.writeMetric(EventType.TASK_FINISHED_TIME);

        EventPublisherFactory.TASK_EVENT
                .getEventPublisher()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_FINISHED_TIME)
                        .task(task)
                        .createEvent());

    }

    private void beforeExecute(Task task) {
        task.writeMetric(EventType.TASK_STARTS_EXECUTION);

        EventPublisherFactory.TASK_EVENT
                .getEventPublisher()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_STARTS_EXECUTION)
                        .task(task)
                        .createEvent());

    }

}
