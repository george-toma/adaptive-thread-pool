package com.github.sliding.adaptive.thread.pool.report;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;

import java.util.List;
import java.util.Optional;

public final class MetricsRepositoryQuery {

    public List<TaskMetrics> loadMetrics(String identifier, int size) {
        Optional<TaskMetricsRepository> metricsRepository = SharedMetricsRepository.load(identifier);
        if (metricsRepository.isPresent()) {
            return metricsRepository.get().load(size);
        }
        return null;
    }

    public TaskMetrics loadMetric(String identifier, String metricIdentifier) {
        Optional<TaskMetricsRepository> metricsRepository = SharedMetricsRepository.load(identifier);
        if (metricsRepository.isPresent()) {
            return metricsRepository.get().loadTaskMetric(metricIdentifier);
        }
        return null;
    }
}
