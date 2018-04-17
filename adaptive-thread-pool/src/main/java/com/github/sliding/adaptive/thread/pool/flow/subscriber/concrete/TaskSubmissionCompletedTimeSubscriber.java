package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.EventFlowConstant;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

import java.util.Optional;

public class TaskSubmissionCompletedTimeSubscriber extends EventSubscriber {

    public TaskSubmissionCompletedTimeSubscriber(String threadPoolIdentifier) {
        super(threadPoolIdentifier, null);
    }

    @Override
    public void onNext(Event event) {
        Optional<TaskMetrics.Builder> builder = loadTaskMetrics(event);
        if (builder.isPresent()) {
            builder.get()
                    .withTaskSubmissionCompletedTime(event.timestamp());
            subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
        }
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_SUBMISSION_COMPLETED_TIME;
    }
}
