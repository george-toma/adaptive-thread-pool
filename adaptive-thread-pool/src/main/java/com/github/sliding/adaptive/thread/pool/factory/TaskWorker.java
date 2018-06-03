package com.github.sliding.adaptive.thread.pool.factory;

import com.github.sliding.adaptive.thread.pool.Task;
import com.github.sliding.adaptive.thread.pool.flow.EventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.SharedEventPublisher;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.listener.event.task.TaskEvent;
import com.github.sliding.adaptive.thread.pool.management.Command;
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
                }
            }

        } catch (RuntimeException ex) {
            log.warn("Could not execute task [{}]", task, ex);
            //do not rethrow the exception to avoid leaking threads from pool
            //TODO push this exception to an exceptionHandler
        } finally {
            log.info("Thread [{}] was interrupted", getName());
            if (task != null) {
                afterExecute(task);
            }
        }
    }

    private void afterExecute(Task task) {
        final String identifier = task.identifier();
        Optional<EventPublisher> eventPublisher = SharedEventPublisher.load(threadPoolIdentifier);
        if (eventPublisher.isPresent()) {
            eventPublisher.get()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_FINISHED_TIME)
                            .taskWorker(this)
                            .identifier(identifier)
                            .createEvent());
        }
    }

    private void beforeExecute(Task task) {
        Optional<EventPublisher> eventPublisher = SharedEventPublisher.load(threadPoolIdentifier);
        if (eventPublisher.isPresent()) {
            eventPublisher.get()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_STARTS_EXECUTION)
                            .identifier(task.identifier())
                            .createEvent());
        }
    }
}