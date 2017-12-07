package com.github.sliding.adaptive.thread.pool;

import com.github.sliding.adaptive.thread.pool.factory.AdaptiveThreadFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Adaptive thread pool executor implementation.
 *
 * @author george-toma
 */
public class AdaptiveThreadPool extends AbstractExecutorService {

    /**
     * Lock held on access to workers set and related bookkeeping. While we
     * could use a concurrent set of some sort, it turns out to be generally
     * preferable to use a lock. Among the reasons is that this serializes
     * interruptIdleWorkers, which avoids unnecessary interrupt storms,
     * especially during shutdown. Otherwise exiting threads would concurrently
     * interrupt those that have not yet interrupted. It also simplifies some of
     * the associated statistics bookkeeping of largestPoolSize etc. We also
     * hold mainLock on shutdown and shutdownNow, for the sake of ensuring
     * workers set is stable while separately checking permission to interrupt
     * and actually interrupting.
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();

    private final long keepAliveTime;
    private final BlockingQueue<Runnable> tasksQueue;
    private final BlockingQueue<Runnable> threadsQueue;
    private int offsetStartingThreads;
    private final ThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedExecutionHandler;

    /**
     * Creates a new {@code AdaptiveThreadPool} with the given initial
     * parameters and default thread factory and rejected execution handler. It
     * may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param offsetStartingThreads the number of threads used to start the
     * thread pool. Default value is 1.
     * @param tasksQueue the queue to use for holding tasks before they are
     * executed. This queue will hold only the {@code Runnable} tasks submitted
     * by the {@code execute} method.
     */
    public AdaptiveThreadPool(int offsetStartingThreads, long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> tasksQueue,
            RejectedExecutionHandler handler) {
        this(offsetStartingThreads, keepAliveTime, unit, tasksQueue,
                new AdaptiveThreadFactory(), handler);
    }

    public AdaptiveThreadPool() {
        this(1, 500, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new AdaptiveThreadFactory(), null
        );
    }

    private AdaptiveThreadPool(int offsetStartingThreads, long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> tasksQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        if (offsetStartingThreads <= 0) {
            offsetStartingThreads = 1;
        }
        if (tasksQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.tasksQueue = tasksQueue;
        this.offsetStartingThreads = offsetStartingThreads;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.rejectedExecutionHandler = handler;
        this.threadsQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isShutdown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        
        addTaskToQueue(command);
        //content
        
        executeTask();
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Thread t, Runnable r) {
    }

    private void executeTask() {
        boolean workerStarted = false;
        boolean workerAdded = false;

    }

    private void addTaskToQueue(Runnable command) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class Worker {

    }
}
