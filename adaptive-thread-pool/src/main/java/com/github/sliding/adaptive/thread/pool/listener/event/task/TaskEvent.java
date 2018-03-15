package com.github.sliding.adaptive.thread.pool.listener.event.task;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.listener.event.Event;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;

import java.util.Objects;

public class TaskEvent implements Event {
    private final EventType eventType;
    private final String identifier;
    private final TaskWorker taskWorker;

    private TaskEvent(EventType eventType, String identifier) {
        this.eventType = eventType;
        this.identifier = identifier;
        taskWorker = null;
    }

    private TaskEvent(EventType eventType, String identifier, TaskWorker taskWorker) {
        this.eventType = eventType;
        this.identifier = identifier;
        this.taskWorker = taskWorker;
    }


    public EventType getEventType() {
        return eventType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TaskWorker getTaskWorker() {
        return taskWorker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEvent taskEvent = (TaskEvent) o;
        return eventType == taskEvent.eventType &&
                Objects.equals(identifier, taskEvent.identifier);
    }

    @Override
    public int hashCode() {

        return Objects.hash(eventType, identifier);
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "eventType=" + eventType +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    public static class Builder {

        private EventType eventType;
        private String identifier;
        private TaskWorker taskWorker;

        public static Builder describedAs() {
            return new Builder();
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder taskWorker(TaskWorker taskWorker) {
            this.taskWorker = taskWorker;
            return this;
        }


        public Event createEvent() {
            if (eventType == null || identifier == null) {
                throw new IllegalStateException("One of the mandatory parameters is empty [eventType,identifier]");
            }
            return new TaskEvent(eventType, identifier, taskWorker);
        }
    }
}
