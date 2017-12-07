package com.github.sliding.adaptive.thread.pool.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author george-toma
 */
public class AdaptiveThreadFactory implements ThreadFactory {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String THREAD_NAME_PREFIX = "worker-thread-";
  private final AtomicInteger threadsCounter = new AtomicInteger();

  @Override
  public Thread newThread(Runnable task) {
    final int threadNumber = threadsCounter.incrementAndGet();
    Thread workerThread = new Thread(task, THREAD_NAME_PREFIX + threadNumber);
    workerThread.setUncaughtExceptionHandler((thread, throwable) -> {
      logger.error("Thread [name: {}]",
              thread.getName(), throwable);
    });

    return workerThread;
  }

}
