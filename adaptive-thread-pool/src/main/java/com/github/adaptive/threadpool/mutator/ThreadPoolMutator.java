package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.config.AdaptiveThreadPoolConfig;
import com.github.adaptive.threadpool.metric.TaskMetrics;

/**
 * @author george-toma
 */
public interface ThreadPoolMutator {


    /**
     * Returns the offset used to increase/decrease the thread pool size
     */
    default int getThreadPoolMutatorValue() {
        return AdaptiveThreadPoolConfig.DEFAULT_THREAD_POOL_MUTATOR_VALUE;
    }

    /**
     *
     */
    void mutateThreadPoolSize(TaskMetrics... taskMetrics);

    String getName();
}
