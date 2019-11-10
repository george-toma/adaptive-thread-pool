package com.github.adaptive.threadpool.flow;

import com.github.adaptive.threadpool.flow.processor.EventFilterProcessor;
import com.github.adaptive.threadpool.flow.subscriber.EventSubscriber;
import com.github.adaptive.threadpool.flow.subscriber.concrete.TaskCompletedSubscriber;
import com.github.adaptive.threadpool.flow.subscriber.concrete.ThreadPoolMutatorSubscriber;
import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.management.PoolManagementFacade;

import java.util.function.Predicate;

public enum EventFlowType {
    TASK_FINISHED_TIME(event -> {
        return EventType.TASK_FINISHED_TIME.equals(event.getEventType());
    }) {
        public EventSubscriber subscriber(Object... parameters) {
            return new TaskCompletedSubscriber();
        }

        @Override
        public EventType eventType() {
            return EventType.TASK_FINISHED_TIME;
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
        public EventSubscriber subscriber(Object... parameters) {
                if(parameters == null || !PoolManagementFacade.class.isAssignableFrom(parameters[0].getClass())){
                    throw new  IllegalArgumentException("PoolManagementFacade object needed");
                }
            return new  ThreadPoolMutatorSubscriber((PoolManagementFacade) parameters[0]);
        }
    };

    private final Predicate<Event> predicate;

    EventFlowType(Predicate<Event> predicate) {
        this.predicate = predicate;
    }

    public EventFilterProcessor processor() {
        return new EventFilterProcessor(predicate);
    }

    public abstract EventSubscriber subscriber(Object... parameters);

    public abstract EventType eventType();
}
