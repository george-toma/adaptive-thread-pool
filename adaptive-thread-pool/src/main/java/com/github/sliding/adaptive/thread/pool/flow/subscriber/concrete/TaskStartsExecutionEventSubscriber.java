package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.EventFlowConstant;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.TaskMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

public class TaskStartsExecutionEventSubscriber extends EventSubscriber {
    TaskMetricsRepository taskMetricsRepository = SharedMetricsRepository.loadDefault();

    @Override
    public void onNext(Event event) {
        TaskMetrics.Builder builder = taskMetricsRepository.load(event.getIdentifier());
        builder.withTaskStartsExecutionTime(event.timestamp());
        //request 1 message
        subscription.request(EventFlowConstant.NUMBER_OF_MESSAGES_TO_DEMAND);
    }
}
