package com.github.sliding.adaptive.thread.pool.flow;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SharedEventPublisher {
    public final static String DEFAULT_EVENT_PUBLISHER = "default-subscriber-event-publisher";
    private final static Map<String, EventPublisher> REGISTRY = new ConcurrentHashMap<>(2, 0.9F, 2);

    public static EventPublisher safeLoad(String name) {
        EventPublisher publisher = REGISTRY.get(name);
        if (publisher == null) {
            publisher = new TaskEventPublisher();
            REGISTRY.put(name, publisher);
        }
        return publisher;
    }

    public static EventPublisher loadDefault() {
        return REGISTRY.get(DEFAULT_EVENT_PUBLISHER);
    }

    public static Optional<EventPublisher> load(String name) {
        return Optional.ofNullable(REGISTRY.get(name));
    }

    public static boolean store(EventPublisher taskEventPublisher, String name) {
        if (!load(name).isPresent()) {
            REGISTRY.put(name, taskEventPublisher);
            return true;
        }
        return false;
    }

    public static void remove(String name) {
        REGISTRY.remove(name);
    }
}
