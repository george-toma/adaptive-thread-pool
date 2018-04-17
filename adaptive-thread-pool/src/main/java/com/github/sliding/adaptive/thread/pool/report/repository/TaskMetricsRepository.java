package com.github.sliding.adaptive.thread.pool.report.repository;

import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import org.cache2k.CacheEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class TaskMetricsRepository {
    private final MetricsRepositoryState repositoryState;


    public TaskMetricsRepository(String repositoryName) {
        repositoryState = new TaskMetricsRepositoryState(repositoryName);
        SharedMetricsRepository.store(this, repositoryName);
    }

    public Optional<TaskMetrics.Builder> loadTaskBuilder(String identifier) {
        if (!repositoryState.isClosed()) {
            return Optional.ofNullable((TaskMetrics.Builder) repositoryState.get(identifier));
        } else {
            return Optional.empty();
        }
    }

    public void store(String identifier, TaskMetrics.Builder taskMetrics) {
        if (!repositoryState.isClosed()) {
            repositoryState.put(identifier, taskMetrics);
        }
    }


    public TaskMetrics load() {
        TaskMetrics taskMetrics = null;
        if (!repositoryState.isClosed()) {
            Iterator<CacheEntry<String, TaskMetrics.Builder>> iterator = (Iterator<CacheEntry<String, TaskMetrics.Builder>>)
                    repositoryState.iterator();
            while (iterator.hasNext()) {
                TaskMetrics.Builder builder = iterator.next().getValue();
                if (builder.isComplete()) {
                    taskMetrics = builder.build();
                    iterator.remove();
                }
            }
        }
        return taskMetrics;
    }

    public TaskMetrics loadTaskMetric(String metricIdentifier) {
        if (!repositoryState.isClosed()) {
            TaskMetrics.Builder builder = (TaskMetrics.Builder) repositoryState.get(metricIdentifier);
            TaskMetrics taskMetrics = builder.build();
            repositoryState.remove(metricIdentifier);
            return taskMetrics;
        }
        return null;
    }

    public List<TaskMetrics> load(int offset) {
        if (!repositoryState.isClosed()) {
            List<TaskMetrics> tasks = new ArrayList<>(offset);
            Iterator<CacheEntry<String, TaskMetrics.Builder>> iterator = (Iterator<CacheEntry<String, TaskMetrics.Builder>>)
                    repositoryState.iterator();
            while (iterator.hasNext()) {
                if (tasks.size() == offset) {
                    break;
                }
                TaskMetrics.Builder builder = iterator.next().getValue();
                if (builder.isComplete()) {
                    tasks.add(iterator.next().getValue().build());
                    iterator.remove();
                }
            }
            return tasks;
        }
        return null;
    }

    public void shutdownRepository() {
        repositoryState.clear();
        repositoryState.close();
        SharedMetricsRepository.remove(repositoryState.name());
    }
}
