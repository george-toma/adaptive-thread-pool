package com.github.sliding.adaptive.thread.pool.report;

import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SharedMetricsRepository {

    private final static Map<String, TaskMetricsRepository> REGISTRY = new ConcurrentHashMap<>(2, 0.9F, 2);

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
