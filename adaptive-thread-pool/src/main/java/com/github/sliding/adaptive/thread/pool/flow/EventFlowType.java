package com.github.sliding.adaptive.thread.pool.flow;

import com.github.sliding.adaptive.thread.pool.flow.processor.EventFilterProcessor;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskClientSubmissionTimeSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskFinishedSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskStartsExecutionEventSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskSubmissionCompletedTimeSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;

import java.util.function.Predicate;

public enum EventFlowType {
    TASK_FINISHED_TIME(event -> {
        return EventType.TASK_FINISHED_TIME.equals(event.getEventType());
    }) {
        public EventSubscriber subscriber(String identifier, ThreadPoolMutator threadPoolMutator) {
            return new TaskFinishedSubscriber(identifier);
        }

        @Override
        public EventType eventType() {
            return EventType.TASK_FINISHED_TIME;
        }
    },
    TASK_CLIENT_SUBMISSION_TIME(event -> {
        return EventType.TASK_CLIENT_SUBMISSION_TIME.equals(event.getEventType());
    }) {
        public EventSubscriber subscriber(String identifier, ThreadPoolMutator threadPoolMutator) {
            return new TaskClientSubmissionTimeSubscriber(identifier);
        }

        @Override
        public EventType eventType() {
            return EventType.TASK_CLIENT_SUBMISSION_TIME;
        }
    },
    TASK_STARTS_EXECUTION(event -> {
        return EventType.TASK_STARTS_EXECUTION.equals(event.getEventType());
    }) {
        @Override
        public EventType eventType() {
            return EventType.TASK_STARTS_EXECUTION;
        }

        @Override
        public EventSubscriber subscriber(String identifier, ThreadPoolMutator threadPoolMutator) {
            return new TaskStartsExecutionEventSubscriber(identifier, threadPoolMutator);
        }
    },
    TASK_SUBMISSION_COMPLETED_TIME(event -> {
        return EventType.TASK_SUBMISSION_COMPLETED_TIME.equals(event.getEventType());
    }) {
        @Override
        public EventSubscriber subscriber(String identifier, ThreadPoolMutator threadPoolMutator) {
            return new TaskSubmissionCompletedTimeSubscriber(identifier);
        }

        @Override
        public EventType eventType() {
            return EventType.TASK_SUBMISSION_COMPLETED_TIME;
        }
    };


    private final Predicate<Event> predicate;

    EventFlowType(Predicate<Event> predicate) {
        this.predicate = predicate;
    }

    public EventFilterProcessor processor() {
        return new EventFilterProcessor(predicate);
    }

    public abstract EventSubscriber subscriber(String identifier, ThreadPoolMutator threadPoolMutator);

    public abstract EventType eventType();
}
