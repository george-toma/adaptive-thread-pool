package com.github.sliding.adaptive.thread.pool.mutator;

/**
 * @author george-toma
 */
public interface ThreadPoolMutator {

    int DEFAULT_THREAD_POOL_MUTATOR_VALUE = System.getProperty("thread.pool.mutator.value") == null ? 1 :
            Integer.parseInt(System.getProperty("thread.pool.mutator.value"));

    /**
     * Returns the offset used to increase/decrease the thread pool size
     */
    default int getThreadPoolMutatorValue() {
        return DEFAULT_THREAD_POOL_MUTATOR_VALUE;
    }

    /**
     *
     */
    void mutateThreadPoolSize(String... metricsIdentifiers);

    void increaseThreadPool();
}
