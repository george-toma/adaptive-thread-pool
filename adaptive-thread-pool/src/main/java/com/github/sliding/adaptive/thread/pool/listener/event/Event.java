package com.github.sliding.adaptive.thread.pool.listener.event;

import com.github.sliding.adaptive.thread.pool.Timestamp;
import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;

public interface Event {
    long TIMESTAMP = Timestamp.getTimestamp();

    String getIdentifier();

    EventType getEventType();

    TaskWorker getTaskWorker();

    default long timestamp() {
        return TIMESTAMP;
    }
}
