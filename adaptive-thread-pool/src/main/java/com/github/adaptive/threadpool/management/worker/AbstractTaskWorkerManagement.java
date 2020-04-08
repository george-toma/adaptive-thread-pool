package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.factory.thread.AdaptiveThreadFactory;
import com.github.adaptive.threadpool.factory.thread.AdaptiveThreadFactoryBuilder;

abstract class AbstractTaskWorkerManagement {
    protected final AdaptiveThreadFactory adaptiveThreadFactory;
    protected TaskWorkerState taskWorkerState;


    public AbstractTaskWorkerManagement(TaskWorkerState taskWorkerState) {
        if (taskWorkerState == null) {
            throw new NullPointerException();
        }
        this.taskWorkerState = taskWorkerState;
        this.adaptiveThreadFactory = AdaptiveThreadFactoryBuilder
                .builder()
                .withDaemon(false)
                .withPriority(Thread.NORM_PRIORITY)
                .build();
    }

}
