package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.EventFlowConstant;
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
        log.info("Finished received event [{}]", event);
        Optional<TaskMetrics.Builder> builder = loadTaskMetrics(event);
        if (builder.isPresent()) {
            builder.get()
                    .withTaskFinishedTime(event.timestamp());
            subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
        }
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_FINISHED_TIME;
    }
}