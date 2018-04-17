package com.github.sliding.adaptive.thread.pool.report.repository;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;

import java.util.Iterator;

public class TaskMetricsRepositoryState extends MetricsRepositoryState<String, TaskMetrics.Builder> {

    private final Cache<String, TaskMetrics.Builder> cache = new Cache2kBuilder<String, TaskMetrics.Builder>() {
    }
            .eternal(true)
            //.storeByReference(true)
            //LRU style
            .entryCapacity(REPOSITORY_SIZE)
            .disableStatistics(true)
            .build();

    public TaskMetricsRepositoryState(String name) {
        super(name);
    }

    @Override
    public Iterator<CacheEntry<String, TaskMetrics.Builder>> iterator() {
        return cache.entries().iterator();
    }

    @Override
    public TaskMetrics.Builder get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, TaskMetrics.Builder value) {
        cache.put(key, value);
    }

    @Override
    public void clear() {
        cache.close();
    }

    @Override
    public void close() {
        cache.close();
    }

    @Override
    public boolean isClosed() {
        return cache.isClosed();
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }
}
