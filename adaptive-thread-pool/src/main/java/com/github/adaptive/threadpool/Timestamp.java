package com.github.adaptive.threadpool;

import java.time.Clock;

/**
 * @author spykee
 */
public final class Timestamp {
    public final static long getTimestamp() {
        return Clock.systemDefaultZone().millis();
    }
}
