package com.github.sliding.adaptive.thread.pool.factory.thread;


import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public final class AdaptiveThreadFactory implements ThreadFactory {

    private final AtomicInteger threadsCounter = new AtomicInteger();
    private final String namePrefix;
    private final boolean isDaemon;
    private final int priority;
    private final String threadPoolIdentifier;

    public AdaptiveThreadFactory(String threadPoolIdentifier, String namePrefix, int priority, boolean isDaemon) {
        this.isDaemon = isDaemon;
        this.namePrefix = namePrefix;
        this.priority = priority;
        this.threadPoolIdentifier = threadPoolIdentifier;
    }

    @Override
    public Thread newThread(Runnable task) {
        final int threadNumber = threadsCounter.incrementAndGet();
        Thread workerThread = new TaskWorker(threadPoolIdentifier);

        workerThread.setUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Thread [name: {}]",
                    thread.getName(), throwable);
        });

        workerThread.setName(namePrefix + "-" + threadNumber);
        workerThread.setDaemon(isDaemon);
        workerThread.setPriority(priority);

        return workerThread;
    }


}
