package com.github.adaptive.threadpool.flow.subscriber.concrete;

import com.github.adaptive.threadpool.config.AdaptiveThreadPoolConfig;
import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.management.PoolManagementFacade;
import com.github.adaptive.threadpool.metric.TaskMetrics;
import com.github.adaptive.threadpool.mutator.ThreadPoolMutator;
import com.github.adaptive.threadpool.flow.subscriber.EventSubscriber;
import com.github.adaptive.threadpool.mutator.ThreadPoolMutatorFactory;

import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPoolMutatorSubscriber extends EventSubscriber {
    private final ThreadPoolMutator threadPoolMutator;
    private final AtomicInteger eventsCounter = new AtomicInteger(
    AdaptiveThreadPoolConfig.THREAD_POOL_MUTATOR_ACTIVATION_SIZE);

    public ThreadPoolMutatorSubscriber(PoolManagementFacade poolManagementFacade) {
        this.threadPoolMutator = ThreadPoolMutatorFactory.threadMutator(
                AdaptiveThreadPoolConfig.MUTATOR_NAME, poolManagementFacade);
    }

    @Override
    public void onNext(Event event) {
        eventsCounter.decrementAndGet();

        if (eventsCounter.get() == 0) {
            eventsCounter.set(AdaptiveThreadPoolConfig.THREAD_POOL_MUTATOR_ACTIVATION_SIZE);
            TaskMetrics.Builder builder = TaskMetrics.builder()
                    .withMetrics(event.task());

            threadPoolMutator.mutateThreadPoolSize(builder.build());
        }
        super.onNext(event);

    }

    @Override
    public EventType eventType() {
        return EventType.TASK_STARTS_EXECUTION;
    }
}
