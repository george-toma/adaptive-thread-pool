package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

import java.util.Optional;

public class TaskStartsExecutionEventSubscriber extends EventSubscriber {

    public TaskStartsExecutionEventSubscriber(String threadPoolIdentifier, ThreadPoolMutator threadPoolMutator) {
        super(threadPoolIdentifier, threadPoolMutator);
    }

    @Override
    public void onNext(Event event) {
        Optional<TaskMetrics.Builder> builder = loadTaskMetrics(event);
        if (builder.isPresent()) {
            builder.get()
                    .withTaskStartsExecutionTime(event.timestamp())
                    .complete(true);
            threadPoolMutator.mutateThreadPoolSize(event.getIdentifier());
        }
        super.onNext(event);
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_STARTS_EXECUTION;
    }
}
