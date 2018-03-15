package com.github.sliding.adaptive.thread.pool.factory;

import com.github.sliding.adaptive.thread.pool.Task;
import com.github.sliding.adaptive.thread.pool.flow.SharedEventPublisher;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.listener.event.task.TaskEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TaskWorker extends Thread {

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task == null) {
            return;
        }
        //avoid threads leaking
        try {
            //check for shutdown
            if (!isInterrupted()) {
                task.run();
            }

        } catch (RuntimeException | Error ex) {
            log.warn("Could not execute subscriber [{}]", task, ex);
        } finally {
            final String identifier = task.identifier();
            task = null;
            SharedEventPublisher.loadDefault()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_FINISHED_TIME)
                            .taskWorker(this)
                            .identifier(identifier)
                            .createEvent());
        }
    }
}