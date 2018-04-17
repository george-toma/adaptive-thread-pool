package com.github.sliding.adaptive.thread.pool;

import com.github.sliding.adaptive.thread.pool.exception.ShutdownThreadPoolException;
import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactory;
import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactoryBuilder;
import com.github.sliding.adaptive.thread.pool.flow.EventFlowType;
import com.github.sliding.adaptive.thread.pool.flow.EventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.SharedEventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.TaskEventPublisher;
import com.github.sliding.adaptive.thread.pool.flow.processor.EventFilterProcessor;
import com.github.sliding.adaptive.thread.pool.flow.subscriber.EventSubscriber;
import com.github.sliding.adaptive.thread.pool.listener.event.EventType;
import com.github.sliding.adaptive.thread.pool.listener.event.task.TaskEvent;
import com.github.sliding.adaptive.thread.pool.mutator.EuclideanDistanceMutator;
import com.github.sliding.adaptive.thread.pool.mutator.ThreadPoolMutator;
import com.github.sliding.adaptive.thread.pool.queue.ThreadPoolQueueManagement;
import com.github.sliding.adaptive.thread.pool.queue.ThreadPoolQueueState;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    private final String threadPoolIdentifier;
    private final ThreadPoolQueueManagement threadPoolQueueManagement;
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
    private int offsetStartingThreads;
    private final AdaptiveThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private final ThreadPoolMutator threadPoolMutator;
    private final TaskMetricsRepository taskMetricsRepository;
    private final EventPublisher eventPublisher;
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
    public AdaptiveThreadPool(int offsetStartingThreads,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Task> tasksQueue,
                              RejectedExecutionHandler handler) {
        this(offsetStartingThreads, keepAliveTime, unit, tasksQueue,
                handler, UUID.randomUUID().toString());
    }

    public AdaptiveThreadPool() {
        this(1, 500, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), null, UUID.randomUUID().toString());
    }

    private AdaptiveThreadPool(int offsetStartingThreads,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Task> tasksQueue,
                               RejectedExecutionHandler handler,
                               String threadPoolIdentifier) {
        if (offsetStartingThreads <= 0) {
            offsetStartingThreads = 1;
        }
        //FIXME  "|| handler == null" on if
        if (tasksQueue == null || threadPoolIdentifier == null) {
            throw new NullPointerException();
        }

        this.threadPoolIdentifier = threadPoolIdentifier;
        this.offsetStartingThreads = offsetStartingThreads;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = AdaptiveThreadFactoryBuilder.buildDefault(threadPoolIdentifier);
        this.rejectedExecutionHandler = handler;
        eventPublisher = new TaskEventPublisher(threadPoolIdentifier);
        taskMetricsRepository = new TaskMetricsRepository(threadPoolIdentifier);
        ThreadPoolQueueState queueState = new ThreadPoolQueueState(new LinkedBlockingQueue<>(),
                tasksQueue);
        threadPoolQueueManagement = new ThreadPoolQueueManagement(threadFactory, queueState);
        this.threadPoolMutator = new EuclideanDistanceMutator(threadPoolIdentifier,
                threadFactory, threadPoolQueueManagement);
        initEventFlow();
        initDefaultThreads();
    }

    private void initDefaultThreads() {
        final int cpuSize = Runtime.getRuntime().availableProcessors();
        threadPoolQueueManagement.addWorkers(cpuSize);
    }

    private void initEventFlow() {
        for (EventFlowType eventFlowType : EventFlowType.values()) {
            EventFilterProcessor processor = eventFlowType.processor();
            EventSubscriber subscriber = eventFlowType.subscriber(threadPoolIdentifier, threadPoolMutator);
            eventPublisher.subscribe(processor);
            processor.subscribe(subscriber);
        }
//        final EventFilterProcessor eventFilterTaskFinishedProcessor =
//                EventFlowType.TASK_FINISHED_TIME.processor();
//        final EventFilterProcessor eventFilterTaskClientSubmissionTimeProcessor =
//                EventFlowType.TASK_CLIENT_SUBMISSION_TIME.processor();
//
//        final EventSubscriber taskFinishedSubscriber = EventFlowType.TASK_FINISHED_TIME.subscriber(threadPoolIdentifier);
//        final EventSubscriber taskClientSubmissionTimeSubscriber = EventFlowType.TASK_CLIENT_SUBMISSION_TIME.subscriber(threadPoolIdentifier);
//        final EventSubscriber taskStartsExecutionEventSubscriber = EventFlowType.TASK_STARTS_EXECUTION.subscriber(threadPoolIdentifier);
//        final EventSubscriber taskSubmissionCompletedTimeSubscriber = EventFlowType.TASK_SUBMISSION_COMPLETED_TIME.subscriber(threadPoolIdentifier);
//
//        eventPublisher.subscribe(eventFilterTaskFinishedProcessor);
//        eventPublisher.subscribe(eventFilterTaskClientSubmissionTimeProcessor);
//        eventFilterTaskFinishedProcessor.subscribe(taskFinishedSubscriber);
//        eventFilterTaskClientSubmissionTimeProcessor.subscribe(taskClientSubmissionTimeSubscriber);
    }

    public void shutdown() {
        //do not accept any other new tasks
        shutdown.set(Boolean.TRUE);
        threadPoolQueueManagement.clear();
        taskMetricsRepository.shutdownRepository();
        eventPublisher.shutdown();

    }

    public List<Runnable> shutdownNow() {
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
        //FIXME - I migh need a readWrite reentrant lock here too for shutdown
        if (isShutdown()) {
            log.debug("Adaptive thread pool is shutdown");
            throw new ShutdownThreadPoolException("Cannot operate on a shutdown thread pool");
        } else {
            //TODO make metrics events to be done via AOP with annotations
            // @MetricMutate(EventType.myEvent)
            storeTaskMetrics(task);
            beforeStoreTask(task);
            addTaskToQueue(task);
            executeTask();
        }

    }

    private void storeTaskMetrics(Task task) {
        TaskMetrics.Builder taskMetricsBuilder = TaskMetrics
                .builder()
                .withIdentifier(task.identifier());
        taskMetricsRepository.store(task.identifier(), taskMetricsBuilder);
    }

    private void beforeStoreTask(Task task) {
        Optional<EventPublisher> eventPublisher = SharedEventPublisher.load(threadPoolIdentifier);
        if (eventPublisher.isPresent()) {
            eventPublisher.get()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_CLIENT_SUBMISSION_TIME)
                            .identifier(task.identifier())
                            .createEvent());

        }
    }

    private void afterStoringTask(Task task) {
        Optional<EventPublisher> eventPublisher = SharedEventPublisher.load(threadPoolIdentifier);
        if (eventPublisher.isPresent()) {
            eventPublisher.get()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_SUBMISSION_COMPLETED_TIME)
                            .identifier(task.identifier())
                            .createEvent());
        }
    }

    private void beforeExecute(Task task) {
        Optional<EventPublisher> eventPublisher = SharedEventPublisher.load(threadPoolIdentifier);
        if (eventPublisher.isPresent()) {
            eventPublisher.get()
                    .submit(TaskEvent.Builder
                            .describedAs()
                            .eventType(EventType.TASK_STARTS_EXECUTION)
                            .identifier(task.identifier())
                            .createEvent());
        }
    }

    private void executeTask() {
        while (!Thread.interrupted() || isShutdown()) {
            TaskWorker worker = null;
            try {
                worker = threadPoolQueueManagement.pollTask(500L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                log.warn("Could not pollTask worker", ex);
            }
            //no worker available
            if (worker == null) {
                threadPoolMutator.increaseThreadPool();
                //TODO sa adaug in proiect ce am vorbit cu Ionut
            } else {
                Optional<Task> task = Optional.ofNullable(threadPoolQueueManagement.pollTask());
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
        threadPoolQueueManagement.addTask(command);
        afterStoringTask(command);
    }


}
