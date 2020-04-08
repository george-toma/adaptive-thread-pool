package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.management.VoidCommandAdder;
import com.github.adaptive.threadpool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public final class TaskWorkerCommand extends AbstractTaskWorkerManagement implements Command<TaskWorker>,
        VoidCommandAdder<Integer> {

    private static final RuntimePermission shutdownPerm
            = new RuntimePermission("modifyThread");
    private final Command<Task> taskCommand;
    private final ReentrantLock shutdownAccessLock = new ReentrantLock();

    public TaskWorkerCommand(TaskWorkerState taskWorkerState, Command<Task> taskCommand) {
        super(taskWorkerState);
        this.taskCommand = taskCommand;
    }

    @Override
    public void add(Integer counter) {
        for (int i = 0; i < counter; i++) {
            startNewWorker();
        }
    }

    private void startNewWorker() {
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
