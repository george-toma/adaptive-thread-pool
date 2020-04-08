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

    private final PoolManagementFacade poolManagementFacade;
    private final Command<TaskWorker> taskWorkerCommand;
    private final Command<Task> taskCommand;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock shutdownReadLock = readWriteLock.readLock();
    private final Lock shutdownWriteLock = readWriteLock.writeLock();
    private final EventPublisher eventPublisher;
    private State currentState = RUNNING;

    AdaptiveThreadPool() {
        this(new SynchronousQueue<>());
    }

    AdaptiveThreadPool(BlockingQueue<Task> tasksQueue) {

        eventPublisher = TASK_EVENT.getEventPublisher();
        poolManagementFacade = new PoolManagementFacade(tasksQueue);
        taskWorkerCommand = poolManagementFacade.doManagement(TASK_WORKER_COMMAND);
        taskCommand = poolManagementFacade.doManagement(TASK_COMMAND);

        initEventFlow();
        initDefaultThreads();
    }

    private void initDefaultThreads() {
        final int cpuSize = getRuntime().availableProcessors();
        //FIXME broken polymorphism here
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

   
    public void shutdown() {
        shutdownNow();
    }

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
     * Executes the given command at some time in the future. The command will be
     * execute in a pooled thread.
     *
     * @param task the runnable task
     * @throws ShutdownThreadPoolException if the thread pool is shutdown
     * @throws NullPointerException if {@code task} is null
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
     * {@code RUNNING}: Accept new tasks and process queued tasks {@code STOP}:
     * Don't accept new tasks, don't process queued tasks, and interrupt
     * in-progress tasks TIDYING: All tasks have terminated, workerCount is
     * zero, the thread transitioning to state TIDYING will run the terminated()
     * hook method TERMINATED: terminated() has completed
     * <p>
     * The state transitions are:
     * <p>
     * RUNNING -> STOP On invocation of shutdown(). STOP -> TIDYING When both
     * queue and pool are empty TIDYING -> TERMINATED When the terminated() hook
     * method has completed
     * <p>
     * Threads waiting in awaitTermination() will return when the state reaches
     * TERMINATED.
     */
    public enum State {
        RUNNING,
        STOP,
        TIDYING,
        TERMINATED
    }
}
