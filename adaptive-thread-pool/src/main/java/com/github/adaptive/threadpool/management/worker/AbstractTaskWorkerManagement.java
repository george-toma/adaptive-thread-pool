package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.factory.thread.AdaptiveThreadFactory;
import com.github.adaptive.threadpool.factory.thread.AdaptiveThreadFactoryBuilder;

abstract class AbstractTaskWorkerManagement {
    protected final String threadPoolIdentifier;
    protected final AdaptiveThreadFactory adaptiveThreadFactory;
    protected TaskWorkerState taskWorkerState;


    public AbstractTaskWorkerManagement(String threadPoolIdentifier, TaskWorkerState taskWorkerState) {
        if (taskWorkerState == null || threadPoolIdentifier == null) {
            throw new NullPointerException();
        }
        this.taskWorkerState = taskWorkerState;
        this.adaptiveThreadFactory = AdaptiveThreadFactoryBuilder
                .builder()
                .withDaemon(false)
                .withThreadPoolIdentifier(threadPoolIdentifier)
                .withPriority(Thread.NORM_PRIORITY)
                .build();
        this.threadPoolIdentifier = threadPoolIdentifier;
    }

}
