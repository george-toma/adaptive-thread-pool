package com.github.sliding.adaptive.thread.pool.validation.logging;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.junit.jupiter.api.Assertions;

import java.util.Map;


public final class LogValidator {

    public static void assertLogEvent(String message, Level level) {
        InMemoryLogAppender inMemoryLogAppender = getTestingLogAppender();
        Assertions.assertTrue(inMemoryLogAppender.hasLogEvent(message, level),
                "Could not find log event");
    }

    private static InMemoryLogAppender getTestingLogAppender() {
        Map<String, Appender> appendersMap = ((org.apache.logging.log4j.core.Logger) LogManager.getLogger()).getAppenders();
        return (InMemoryLogAppender) appendersMap.get("InMemoryAppender");
    }

    public static void clear() {
        getTestingLogAppender().clear();
    }
}
