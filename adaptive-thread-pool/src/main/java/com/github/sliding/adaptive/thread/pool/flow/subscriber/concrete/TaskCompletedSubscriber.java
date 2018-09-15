package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TaskCompletedSubscriber extends EventSubscriber {

    public TaskCompletedSubscriber(String threadPoolIdentifier) {
        super(threadPoolIdentifier, null);
    }

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