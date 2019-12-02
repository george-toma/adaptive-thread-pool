package com.github.adaptive.threadpool;

import static com.github.adaptive.threadpool.AdaptiveThreadPool.State.RUNNING;
import com.github.adaptive.threadpool.exception.ShutdownThreadPoolException;
import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.flow.EventFlowType;
import static com.github.adaptive.threadpool.flow.EventFlowType.values;
import com.github.adaptive.threadpool.flow.EventPublisher;
import static com.github.adaptive.threadpool.flow.EventPublisherFactory.TASK_EVENT;
import com.github.adaptive.threadpool.flow.processor.EventFilterProcessor;
import com.github.adaptive.threadpool.flow.subscriber.EventSubscriber;
import static com.github.adaptive.threadpool.listener.event.EventType.TASK_CLIENT_SUBMISSION_TIME;
import static com.github.adaptive.threadpool.listener.event.EventType.TASK_SUBMISSION_COMPLETED_TIME;
import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.management.PoolManagementFacade;
import static com.github.adaptive.threadpool.management.PoolManagementFacade.ManagementType.TASK_COMMAND;
import static com.github.adaptive.threadpool.management.PoolManagementFacade.ManagementType.TASK_WORKER_COMMAND;
import com.github.adaptive.threadpool.task.Task;
import static java.lang.Runtime.getRuntime;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import static java.util.UUID.randomUUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Adaptive thread pool executor implementation.
 *
 * @author george-toma
 */
@Log4j2
public class AdaptiveThreadPool {

    private final String threadPoolIdentifier;
    private final PoolManagementFacade poolManagementFacade;
    private final Command<TaskWorker> taskWorkerCommand;
    private final Command<Task> taskCommand;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock shutdownReadLock = readWriteLock.readLock();
    private final Lock shutdownWriteLock = readWriteLock.writeLock();
    private final EventPublisher eventPublisher;
    private State currentState = RUNNING;

    /**
     * Creates a new {@code AdaptiveThreadPool} with the given initial
     * parameters and default thread factory and rejected execution handler. It
     * may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param tasksQueue the management to use for holding tasks before they are
     *                   executed. This management will hold only the {@code {@link Task}} tasks submitted
     *                   by the {@code execute} method.
     */
    public AdaptiveThreadPool(BlockingQueue<Task> tasksQueue) {
        this(tasksQueue, null, randomUUID().toString());
    }

    public AdaptiveThreadPool() {
        this(new SynchronousQueue<>(), null, randomUUID().toString());
    }

    private AdaptiveThreadPool(BlockingQueue<Task> tasksQueue,
                               RejectedExecutionHandler handler,
                               String threadPoolIdentifier) {

        this.threadPoolIdentifier = threadPoolIdentifier;
        eventPublisher = TASK_EVENT.getEventPublisher();

        poolManagementFacade = new PoolManagementFacade(threadPoolIdentifier, tasksQueue);
        taskWorkerCommand = poolManagementFacade.doManagement(TASK_WORKER_COMMAND);
        taskCommand = poolManagementFacade.doManagement(TASK_COMMAND);

        initEventFlow();
        initDefaultThreads();
    }

    private void initDefaultThreads() {
        final int cpuSize = getRuntime().availableProcessors();
        taskWorkerCommand.add(new TaskWorker[cpuSize]);
    }

    private void initEventFlow() {
        for (EventFlowType eventFlowType : values()) {
            EventFilterProcessor processor = eventFlowType.processor();
            EventSubscriber subscriber = eventFlowType.subscriber(poolManagementFacade);
            eventPublisher.subscribe(processor);
            processor.subscribe(subscriber);
        }
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.
     *
     * @throws SecurityException
     */
    public void shutdown() {
        shutdownNow();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.These tasks are drained (removed)
     * from the task management upon return from this method.<p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  This implementation
     * interrupts tasks via {@link Thread#interrupt}; any task that
     * fails to respond to interrupts may never terminate.
     *
     * @return not processed {@link Task}
     * @throws SecurityException
     */
    public List<Task> shutdownNow() {
        List<Task> tasks;
        shutdownWriteLock.lock();
        try {
            advanceRunState(State.STOP);
            tasks = poolManagementFacade.shutdownAll();
            eventPublisher.shutdown();
        } finally {
            shutdownWriteLock.unlock();
        }
        return tasks;
    }

    private void advanceRunState(State state) {
        currentState = state;
    }

    public boolean isShutdown() {
        shutdownReadLock.lock();
        try {
            return currentState == State.STOP
                    || currentState == State.TERMINATED
                    || currentState == State.TIDYING;
        } finally {
            shutdownReadLock.unlock();
        }
    }

    public boolean isTerminated() {
        return currentState == State.TERMINATED;
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a pooled thread.
     *
     * @param task the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution
     * @throws NullPointerException       if {@code task} is null
     */
    public void execute(Task task) {
        if (task == null) {
            throw new NullPointerException();
        }
        if (isShutdown()) {
            throw new ShutdownThreadPoolException("Cannot operate on a shutdown thread pool");
        } else {
            beforeStoreTask(task);
            addTaskToQueue(task);
        }
    }


    private void beforeStoreTask(Task task) {
        task.writeMetric(TASK_CLIENT_SUBMISSION_TIME);
    }

    private void afterStoringTask(Task task) {
        task.writeMetric(TASK_SUBMISSION_COMPLETED_TIME);
    }

    private void addTaskToQueue(Task command) {
        taskCommand.add(command);
        afterStoringTask(command);
    }

    /**
     * The {@link State} provides the main lifecycle control, taking on values:
     * <p>
     * {@code RUNNING}:  Accept new tasks and process queued tasks
     * {@code STOP}:     Don't accept new tasks, don't process queued tasks,
     * and interrupt in-progress tasks
     * TIDYING:  All tasks have terminated, workerCount is zero,
     * the thread transitioning to state TIDYING
     * will run the terminated() hook method
     * TERMINATED: terminated() has completed
     * <p>
     * The state transitions are:
     * <p>
     * RUNNING -> STOP
     * On invocation of shutdown().
     * STOP -> TIDYING
     * When both queue and pool are empty
     * TIDYING -> TERMINATED
     * When the terminated() hook method has completed
     * <p>
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     */
    public enum State {
        RUNNING,
        STOP,
        TIDYING,
        TERMINATED
    }
}
