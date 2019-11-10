package com.github.adaptive.threadpool.listener.event.task;

import com.github.adaptive.threadpool.Timestamp;
import com.github.adaptive.threadpool.listener.event.Event;
import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.task.Task;

import java.util.Objects;

public class TaskEvent implements Event {
    private final EventType eventType;
    private final long timestamp;
    private final Task task;

    public TaskEvent(EventType eventType, long timestamp, Task task) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.task = task;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public Task task() {
        return task;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEvent taskEvent = (TaskEvent) o;
        return timestamp == taskEvent.timestamp &&
                eventType == taskEvent.eventType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(eventType, timestamp);
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "eventType=" + eventType +
                '}';
    }

    public static class Builder {

        private EventType eventType;
        private Task task;
        public static Builder describedAs() {
            return new Builder();
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder task(Task task){
            this.task = task;
            return this;
        }


        public Event createEvent() {
            if (eventType == null) {
                throw new IllegalStateException("One of the mandatory parameters is empty [eventType,taskId]");
            }
            return new TaskEvent(eventType, Timestamp.getTimestamp(),task);
        }
    }
}
