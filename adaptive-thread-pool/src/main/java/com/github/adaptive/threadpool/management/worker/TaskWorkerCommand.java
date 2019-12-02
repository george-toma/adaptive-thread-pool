package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public final class TaskWorkerCommand extends AbstractTaskWorkerManagement implements Command<TaskWorker> {
    /**
     * Permission required for callers of shutdown and shutdownNow.
     * We additionally require (see checkShutdownAccess) that callers
     * have permission to actually interrupt threads in the worker set
     * (as governed by Thread.interrupt, which relies on
     * ThreadGroup.checkAccess, which in turn relies on
     * SecurityManager.checkAccess). Shutdowns are attempted only if
     * these checks pass.
     * <p>
     * All actual invocations of Thread.interrupt (see
     * interruptIdleWorkers and interruptWorkers) ignore
     * SecurityExceptions, meaning that the attempted interrupts
     * silently fail. In the case of shutdown, they should not fail
     * unless the SecurityManager has inconsistent policies, sometimes
     * allowing access to a thread and sometimes not. In such cases,
     * failure to actually interrupt threads may disable or delay full
     * termination. Other uses of interruptIdleWorkers are advisory,
     * and failure to actually interrupt will merely delay response to
     * configuration changes so is not handled exceptionally.
     */
    private static final RuntimePermission shutdownPerm =
            new RuntimePermission("modifyThread");
    private final Command<Task> taskCommand;
    private final ReentrantLock shutdownAccessLock = new ReentrantLock();

    public TaskWorkerCommand(String threadPoolIdentifier, TaskWorkerState taskWorkerState, Command<Task> taskCommand) {
        super(threadPoolIdentifier, taskWorkerState);
        this.taskCommand = taskCommand;
    }

    @Override
    public void add(TaskWorker... worker) {
        if (worker == null || worker.length == 1) {
            startNewWorker();
        } else {
            for (TaskWorker worker1 : worker) {
                startNewWorker();
            }
        }
    }

    private void startNewWorker() {
        //FIXME Visitor pattern ? I need a pattern here
        TaskWorker taskWorker = (TaskWorker) adaptiveThreadFactory.newThread(taskCommand);
        taskWorkerState.add(taskWorker);
        taskWorker.start();
    }


    private Optional<TaskWorker> interruptWorker() {
        TaskWorker taskWorker = taskWorkerState.poll();
        if (taskWorker != null) {
            log.info("Evicted worker from thread pool [{}]", taskWorker.getName());
            taskWorker.stopWorker(true);
            taskWorker.interrupt();
        }
        return Optional.ofNullable(taskWorker);    
    }

    private void interruptWorkers() {
        for (int i = 0; i < taskWorkerState.size(); i++) {
            interruptWorker();
        }
    }

    @Override
    public List<TaskWorker> shutdown() {
        checkShutdownAccess();
        interruptWorkers();
        clear();
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        taskWorkerState.clear();
    }

    @Override
    public TaskWorker remove() {
        return taskWorkerState.poll();
    }

    @Override
    public void remove(int numberOfWorkers) {
        if (numberOfWorkers <= 0) {
            return;
        }
        for (int i = 0; i < numberOfWorkers; i++) {
           TaskWorker taskWorker = remove();
           taskWorker.stopWorker(true);
        }
    }

    /**
     * If there is a security manager, makes sure caller has
     * permission to shut down threads in general (see shutdownPerm).
     * If this passes, additionally makes sure the caller is allowed
     * to interrupt each worker thread. This might not be true even if
     * first check passed, if the SecurityManager treats some threads
     * specially.
     */
    private void checkShutdownAccess() {

        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            shutdownAccessLock.lock();
            try {
                Queue<TaskWorker> workers = taskWorkerState.getTasksWorkers();
                for (TaskWorker worker : workers) {
                    security.checkAccess(worker);
                }
            } finally {
                shutdownAccessLock.unlock();
            }
        }
    }
}
