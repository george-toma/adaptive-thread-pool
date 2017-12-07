package com.github.sliding.adaptive.thread.pool.mutator;

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author spykee
 */
public interface ThreadPoolMutator {

    int DEFAULT_THREAD_POOL_OFFSET = 1;

    /**
     * Returns the offset used to increase/decrease the thread pool size
     */
    default int getThreadPoolOffset() {
        return DEFAULT_THREAD_POOL_OFFSET;
    }

    /**
     *
     */
    void mutateThreadPoolSize(BlockingQueue<Runnable> threadsQueue);
}
