package com.github.adaptive.threadpool.flow.subscriber.concrete;

import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.flow.subscriber.EventSubscriber;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class TaskCompletedSubscriber extends EventSubscriber {

    @Override
    public void onNext(Event event) {
        final long taskStartsExecutionTime = event.task().readMetric(EventType.TASK_STARTS_EXECUTION);
        final long taskFinishedTime = event.task().readMetric(EventType.TASK_FINISHED_TIME);

            log.info("Task processing time [starting: {}, ending: {}]",
                    taskStartsExecutionTime,
                    taskFinishedTime);
        super.onNext(event);
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_FINISHED_TIME;
    }
}