package com.github.sliding.adaptive.thread.pool.task;

import com.github.sliding.adaptive.thread.pool.Timestamp;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;

public interface Measurable {
    Long[] metricsValues = new Long[EventType.values().length];

    default long readMetric(EventType eventType) {
        Long value = metricsValues[eventType.order()];
        if (value == null) {
            return 0L;
        }
        return value;
    }

    default void writeMetric(EventType eventType) {

        metricsValues[eventType.order()] = Timestamp.getTimestamp();
    }
}
