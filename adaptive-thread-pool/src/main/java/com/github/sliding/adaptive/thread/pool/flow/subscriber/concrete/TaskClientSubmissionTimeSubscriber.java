package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.EventFlowConstant;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

import java.util.Optional;

public class TaskClientSubmissionTimeSubscriber extends EventSubscriber {

    public TaskClientSubmissionTimeSubscriber(String threadPoolIdentifier) {
        super(threadPoolIdentifier, null);
    }

    @Override
    public void onNext(Event event) {
        super.onNext(event);
        Optional<TaskMetrics.Builder> builder = loadTaskMetrics(event);
        if (builder.isPresent()) {
            builder.get()
                    .withTaskClientSubmissionTime(event.timestamp());
            //request new messages
            subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
        }
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_CLIENT_SUBMISSION_TIME;
    }
}