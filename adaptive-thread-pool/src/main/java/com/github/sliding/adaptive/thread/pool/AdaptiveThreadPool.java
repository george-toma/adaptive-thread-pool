package com.github.sliding.adaptive.thread.pool;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactory;
import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactoryBuilder;
import com.github.sliding.adaptive.thread.pool.flow.EventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.SharedEventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.TaskEventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.processor.EventFilterProcessor;
import com.github.sliding.adaptive.thread.pool.flow.processor.EventFilterProcessorFactory;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskClientSubmissionTimeSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskFinishedSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskStartsExecutionEventSubscriber;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.concrete.TaskSubmissionCompletedTimeSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.listener.event.task.TaskEvent;
import com.github.sliding.adaptive.thread.pool.mutator.DefaultThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.report.InMemoryReportHandler;
import com.github.sliding.adaptive.thread.pool.report.ReportHandler;
import com.github.sliding.adaptive.thread.pool.report.TaskMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Adaptive thread pool executor implementation.
 *
 * @author george-toma
 */
@Log4j2
public class AdaptiveThreadPool {

    private final BlockingQueue<Task> tasksQueue;
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
    private final BlockingQueue<TaskWorker> threadsQueue;
    //keep last 30 metric
    private final ReportHandler reportHandler = new InMemoryReportHandler(30);
    private int offsetStartingThreads;
    private final ThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private final ThreadPoolMutator threadPoolMutator;
    private final TaskMetricsRepository taskMetricsRepository = new TaskMetricsRepository();
    private final EventPublisher eventPublisher = new TaskEventPublisher();
    private AtomicBoolean shutdown = new AtomicBoolean(Boolean.FALSE);

    /**
     * Creates a new {@code AdaptiveThreadPool} with the given initial
     * parameters and default thread factory and rejected execution handler. It
     * may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param offsetStartingThreads the number of threads used to start the
     *                              thread pool. Default value is 1.
     * @param tasksQueue            the queue to use for holding tasks before they are
     *                              executed. This queue will hold only the {@code Runnable} tasks submitted
     *                              by the {@code execute} method.
     */
    public AdaptiveThreadPool(int offsetStartingThreads, long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Task> tasksQueue,
                              RejectedExecutionHandler handler,
                              final AdaptiveThreadFactory threadFactory) {
        this(offsetStartingThreads, keepAliveTime, unit, tasksQueue,
                threadFactory, handler);
    }

    public AdaptiveThreadPool() {
        this(1, 500, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), AdaptiveThreadFactoryBuilder.buildDefault(), null);
    }

    private AdaptiveThreadPool(int offsetStartingThreads, long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Task> tasksQueue,
                               AdaptiveThreadFactory threadFactory,
                               RejectedExecutionHandler handler) {
        if (offsetStartingThreads <= 0) {
            offsetStartingThreads = 1;
        }
        //FIXME  "|| handler == null" on if
        if (tasksQueue == null || threadFactory == null) {
            throw new NullPointerException();
        }
        this.tasksQueue = tasksQueue;
        this.offsetStartingThreads = offsetStartingThreads;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.rejectedExecutionHandler = handler;
        this.threadsQueue = new LinkedBlockingQueue<>();
        this.threadPoolMutator = new DefaultThreadPoolMutator(threadFactory);
        initEventFlow();
        initDefaultThreads();
    }

    private void initDefaultThreads() {
        final int cpuSize = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < cpuSize; i++) {
            threadsQueue.offer((TaskWorker) threadFactory.newThread(null));
        }
    }

    private void initEventFlow() {

        final EventFilterProcessor eventFilterTaskFinishedProcessor =
                EventFilterProcessorFactory.TASK_FINISHED_TIME.processor();
        final EventFilterProcessor eventFilterTaskClientSubmissionTimeProcessor =
                EventFilterProcessorFactory.TASK_CLIENT_SUBMISSION_TIME.processor();


        final EventSubscriber taskFinishedSubscriber = new TaskFinishedSubscriber();
        final EventSubscriber taskClientSubmissionTimeSubscriber = new TaskClientSubmissionTimeSubscriber();
        final EventSubscriber taskStartsExecutionEventSubscriber = new TaskStartsExecutionEventSubscriber();
        final EventSubscriber taskSubmissionCompletedTimeSubscriber = new TaskSubmissionCompletedTimeSubscriber();

        eventPublisher.subscribe(eventFilterTaskFinishedProcessor);
        eventPublisher.subscribe(eventFilterTaskClientSubmissionTimeProcessor);
        eventFilterTaskFinishedProcessor.subscribe(taskFinishedSubscriber);
        eventFilterTaskClientSubmissionTimeProcessor.subscribe(taskClientSubmissionTimeSubscriber);
    }

    public void shutdown() {
        //do not accept any other new tasks
        shutdown.set(Boolean.TRUE);
        tasksQueue.clear();
        threadsQueue.clear();
        taskMetricsRepository.shutdownCache();
        eventPublisher.close();

    }

    public List<Runnable> shutdownNow() {
        //do not accept any other new tasks
        shutdown.set(Boolean.TRUE);
        return null;
    }

    public boolean isShutdown() {
        return shutdown.get();
    }

    public boolean isTerminated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void execute(Task task) {
        if (task == null) {
            throw new NullPointerException();
        }
        if (isShutdown()) {
            log.debug("Adaptive thread pool is shutdown");
        } else {
            storeTaskMetrics(task);
            beforeStoreTask(task);
            addTaskToQueue(task);
            executeTask();
        }

    }

    private void storeTaskMetrics(Task task) {
        TaskMetrics.Builder taskMetricsBuilder = TaskMetrics
                .builder();
        taskMetricsRepository.store(task.identifier(), taskMetricsBuilder);
    }

    private void beforeStoreTask(Task task) {
        SharedEventPublisher.loadDefault()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_CLIENT_SUBMISSION_TIME)
                        .identifier(task.identifier())
                        .createEvent());
    }

    private void afterStoringTask(Task task) {

        SharedEventPublisher.loadDefault()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_SUBMISSION_COMPLETED_TIME)
                        .identifier(task.identifier())
                        .createEvent());
    }

    private void beforeExecute(Task task) {
        SharedEventPublisher.loadDefault()
                .submit(TaskEvent.Builder
                        .describedAs()
                        .eventType(EventType.TASK_STARTS_EXECUTION)
                        .identifier(task.identifier())
                        .createEvent());
    }

    private void executeTask() {
        while (!Thread.interrupted() || isShutdown()) {
            TaskWorker worker = null;
            try {
                worker = threadsQueue.poll(100L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                log.warn("Could not poll worker", ex);
            }
            //no worker available
            if (worker == null) {
                threadPoolMutator.mutateThreadPoolSize(threadsQueue);
                //TODO sa adaug in proiect ce am vorbit cu Ionut
            } else {
                Optional<Task> task = Optional.ofNullable(tasksQueue.poll());
                if (task.isPresent()) {
                    beforeExecute(task.get());
                    worker.setTask(task.get());
                    worker.start();
                } else {
                    break;
                }
            }

        }

    }

    private void addTaskToQueue(Task command) {
        tasksQueue.add(command);
        afterStoringTask(command);
    }


}
