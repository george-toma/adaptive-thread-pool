package com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete;

import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.metric.TaskMetrics;

public class ThreadPoolMutatorSubscriber extends EventSubscriber {

    public ThreadPoolMutatorSubscriber(String threadPoolIdentifier, ThreadPoolMutator threadPoolMutator) {
        super(threadPoolIdentifier, threadPoolMutator);
    }

    @Override
    public void onNext(Event event) {

    TaskMetrics.Builder builder=  TaskMetrics.builder()
                    .withMetrics(event.task());

            threadPoolMutator.mutateThreadPoolSize(builder.build());

        super.onNext(event);
    }

    @Override
    public EventType eventType() {
        return EventType.TASK_STARTS_EXECUTION;
    }
}
