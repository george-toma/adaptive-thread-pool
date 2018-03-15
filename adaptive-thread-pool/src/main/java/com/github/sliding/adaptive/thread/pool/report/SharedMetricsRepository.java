package com.github.sliding.adaptive.thread.pool.report;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SharedMetricsRepository {

    public final static String DEFAULT_METRICS_MANAGER = "default-metric-manager";
    private final static Map<String, TaskMetricsRepository> REGISTRY = new ConcurrentHashMap<>(2, 0.9F, 2);

    public static TaskMetricsRepository safeLoad(String name) {
        TaskMetricsRepository publisher = REGISTRY.get(name);
        if (publisher == null) {
            publisher = new TaskMetricsRepository();
            REGISTRY.putIfAbsent(name, publisher);
        }
        return publisher;
    }


    public static TaskMetricsRepository loadDefault() {
        return REGISTRY.get(DEFAULT_METRICS_MANAGER);
    }

    public static Optional<TaskMetricsRepository> load(String name) {
        return Optional.ofNullable(REGISTRY.get(name));
    }

    public static boolean store(TaskMetricsRepository taskMetricsRepository, String name) {
        if (!load(name).isPresent()) {
            REGISTRY.put(name, taskMetricsRepository);
            return true;
        }
        return false;
    }

    public static void remove(String name) {
        REGISTRY.remove(name);
    }

}
