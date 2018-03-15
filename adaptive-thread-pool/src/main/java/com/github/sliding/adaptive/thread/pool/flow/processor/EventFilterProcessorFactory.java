package com.github.sliding.adaptive.thread.pool.flow.processor;

import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;

import java.util.function.Predicate;

public enum EventFilterProcessorFactory {
    TASK_FINISHED_TIME(event -> {
        return EventType.TASK_FINISHED_TIME.equals(event.getEventType());
    }),
    TASK_CLIENT_SUBMISSION_TIME(event -> {
        return EventType.TASK_CLIENT_SUBMISSION_TIME.equals(event.getEventType());
    });

    private final Predicate<Event> predicate;

    EventFilterProcessorFactory(Predicate<Event> predicate) {
        this.predicate = predicate;
    }

    public EventFilterProcessor processor() {
        return new EventFilterProcessor(predicate);
    }
}
