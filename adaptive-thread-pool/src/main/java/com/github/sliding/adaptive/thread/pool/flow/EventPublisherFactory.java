package com.github.sliding.adaptive.thread.pool.flow;

public enum EventPublisherFactory {
    TASK_EVENT( new TaskEventPublisher());

    private final EventPublisher eventPublisher;

    EventPublisherFactory(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }
}
