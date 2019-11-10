package com.github.adaptive.threadpool.listener.event;

import com.github.adaptive.threadpool.task.Task;

public interface Event {


    EventType getEventType();
    Task task();
    long getTimestamp();
}
