package com.github.sliding.adaptive.thread.pool.factory.thread;


import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public final class AdaptiveThreadFactory {

    private final AtomicInteger threadsCounter = new AtomicInteger();
    private final String namePrefix;
    private final boolean isDaemon;
    private final int priority;
    private final String threadPoolIdentifier;

    public AdaptiveThreadFactory(AdaptiveThreadFactoryBuilder threadFactoryBuilder) {
        this.isDaemon = threadFactoryBuilder.isDaemon();
        this.namePrefix = threadFactoryBuilder.getNamePrefix();
        this.priority = threadFactoryBuilder.getPriority();
        this.threadPoolIdentifier = threadFactoryBuilder.getThreadPoolIdentifier();
    }

    public Thread newThread(Command<Task> taskCommand) {
        final int threadNumber = threadsCounter.incrementAndGet();
        Thread workerThread = new TaskWorker(threadPoolIdentifier, taskCommand,
                namePrefix + "-" + threadNumber);

        workerThread.setUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Thread [name: {}]",
                    thread.getName(), throwable);
        });

        workerThread.setDaemon(isDaemon);
        workerThread.setPriority(priority);

        return workerThread;
    }


}
