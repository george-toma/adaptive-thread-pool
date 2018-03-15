package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.TaskMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

@Deprecated(forRemoval = true)
public class TaskEventSubscriber extends EventSubscriber {
    TaskMetricsRepository taskMetricsRepository = SharedMetricsRepository.loadDefault();

    @Override
    public void onNext(Event event) {
        TaskMetrics.Builder builder = taskMetricsRepository.load(event.getIdentifier());
        builder.withTaskStartsExecutionTime(event.timestamp());
        //request 1 message
        subscription.request(1);
    }
}
