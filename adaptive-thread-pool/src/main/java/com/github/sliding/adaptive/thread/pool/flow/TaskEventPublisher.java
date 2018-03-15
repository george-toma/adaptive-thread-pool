package com.github.sliding.adaptive.thread.pool.flow;

import java.util.concurrent.Executor;

public final class TaskEventPublisher extends EventPublisher {

    public TaskEventPublisher(Executor executor, int maxBufferCapacity, String name) {
        super(executor, maxBufferCapacity, name);
    }

    public TaskEventPublisher() {
        super();
        SharedEventPublisher.store(this, SharedEventPublisher.DEFAULT_EVENT_PUBLISHER);
    }

    public TaskEventPublisher(Executor executor, int maxBufferCapacity) {
        super(executor, maxBufferCapacity);
        SharedEventPublisher.store(this, SharedEventPublisher.DEFAULT_EVENT_PUBLISHER);
    }
}
