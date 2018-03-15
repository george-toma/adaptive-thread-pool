package com.github.sliding.adaptive.thread.pool.report;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public final class TaskMetricsRepository {

    private final int CACHE_SIZE = System.getProperty("thread.metric") == null
            ? 4 : Integer.parseInt(System.getProperty("thread.metric"));

    private final Cache<String, TaskMetrics.Builder> cache = new Cache2kBuilder<String, TaskMetrics.Builder>() {
    }
            .eternal(true)
            //.storeByReference(true)
            //LRU style
            .entryCapacity(CACHE_SIZE)
            .disableStatistics(true)
            .build();

    private final String cacheName;

    public TaskMetricsRepository() {
        cacheName = SharedMetricsRepository.DEFAULT_METRICS_MANAGER;
        SharedMetricsRepository.store(this, cacheName);
    }

    public TaskMetrics.Builder load(String identifier) {
        return cache.get(identifier);
    }

    public ConcurrentMap<String, TaskMetrics.Builder> getAll() {
        return cache.asMap();
    }

    public void store(String identifier, TaskMetrics.Builder taskMetrics) {
        cache.put(identifier, taskMetrics);
    }

    public List<TaskMetrics> load(int offset) {
        List<TaskMetrics> tasks = new ArrayList<>(offset);
        Iterator<CacheEntry<String, TaskMetrics.Builder>> iterator = cache.entries().iterator();
        while (iterator.hasNext()) {
            if (tasks.size() == offset) {
                break;
            }
            tasks.add(iterator.next().getValue().build());
        }
        return tasks;
    }

    public void shutdownCache() {
        cache.clear();
        cache.close();
        SharedMetricsRepository.remove(cacheName);
    }
}
