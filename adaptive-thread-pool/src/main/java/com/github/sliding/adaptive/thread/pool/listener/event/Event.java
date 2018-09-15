package com.github.sliding.adaptive.thread.pool.listener.event;

import com.github.sliding.adaptive.thread.pool.task.Task;

public interface Event {


    EventType getEventType();
    Task task();
    long getTimestamp();
}
