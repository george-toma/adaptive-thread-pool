package com.github.adaptive.threadpool.task;

import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.Timestamp;

public interface Measurable {
    Long[] metricsValues = new Long[EventType.values().length];

    default long readMetric(EventType eventType) {
        Long value = metricsValues[eventType.ordinal()];
        if (value == null) {
            return 0L;
        }
        return value;
    }

    default void writeMetric(EventType eventType) {

        metricsValues[eventType.ordinal()] = Timestamp.getTimestamp();
    }
}
