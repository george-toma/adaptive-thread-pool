package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class TaskFinishedSubscriber extends EventSubscriber {

    public TaskFinishedSubscriber(String threadPoolIdentifier) {
        super(threadPoolIdentifier, null);
    }

    @Override
    public void onNext(Event event) {
        Optional<TaskMetrics.Builder> builder = loadTaskMetrics(event);
        if (builder.isPresent()) {
            builder.get()
                    .withTaskFinishedTime(event.timestamp());
            log.info("Task processing time [starting: {}, ending: {}]",
                    builder.get().getTaskStartsExecutionTime(),
                    builder.get().getTaskFinishedTime());
            removeTaskMetric(event, event.getIdentifier());
        }
        super.onNext(event);
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_FINISHED_TIME;
    }
}