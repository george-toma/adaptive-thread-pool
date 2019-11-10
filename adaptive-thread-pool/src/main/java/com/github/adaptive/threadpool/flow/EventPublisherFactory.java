package com.github.adaptive.threadpool.flow;

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
