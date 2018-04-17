package com.github.sliding.adaptive.thread.pool.flow;

public final class TaskEventPublisher extends EventPublisher {

    public TaskEventPublisher(String eventPublisherName) {
        super(eventPublisherName);
        SharedEventPublisher.store(this, this.eventPublisherName);

    }

    @Override
    public void close() {
        super.close();
        SharedEventPublisher.remove(eventPublisherName);
    }

}
