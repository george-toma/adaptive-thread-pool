package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.queue.ThreadPoolQueueManagement;
import com.github.sliding.adaptive.thread.pool.report.MetricsRepositoryQuery;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected final ThreadFactory threadFactory;
    protected final String name;
    protected final MetricsRepositoryQuery metricsRepositoryQuery = new MetricsRepositoryQuery();
    protected final ThreadPoolQueueManagement threadPoolQueueManagement;

    public AbstractThreadPoolMutator(String name, ThreadFactory threadFactory, ThreadPoolQueueManagement threadPoolQueueManagement) {
        this.name = name;
        this.threadFactory = threadFactory;
        this.threadPoolQueueManagement = threadPoolQueueManagement;
    }
}
