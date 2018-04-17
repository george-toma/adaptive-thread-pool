package com.github.sliding.adaptive.thread.pool.report.repository;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import org.cache2k.CacheEntry;

import java.util.Iterator;

public abstract class MetricsRepositoryState<K, V> implements RepositoryState<K, V> {
    protected final int REPOSITORY_SIZE = System.getProperty("thread.metric") == null
            ? 4 : Integer.parseInt(System.getProperty("thread.metric"));
    protected String name;

    public MetricsRepositoryState(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    public abstract Iterator<CacheEntry<String, TaskMetrics.Builder>> iterator();
}
