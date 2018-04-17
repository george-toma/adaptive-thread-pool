package com.github.sliding.adaptive.thread.pool.queue;

import com.github.sliding.adaptive.thread.pool.Task;
import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Log4j2
public final class ThreadPoolQueueManagement {
    private final ThreadFactory threadFactory;
    private final ThreadPoolQueueState queueState;

    public ThreadPoolQueueManagement(ThreadFactory threadFactory, ThreadPoolQueueState queueState) {
        this.threadFactory = threadFactory;
        this.queueState = queueState;
    }

    public int threadPoolSize() {
        return queueState.getThreadsQueue().size();
    }

    public TaskWorker pollTask(long time, TimeUnit timeUnit) throws InterruptedException {
        return queueState.getThreadsQueue().poll(time, timeUnit);
    }

    public Task pollTask() {
        return queueState.getTasksQueue().poll();
    }

    public void addTask(Task task) {
        queueState.getTasksQueue().add(task);
    }

    public void addWorker() {
        TaskWorker taskWorker = (TaskWorker) threadFactory.newThread(null);
        queueState.getThreadsQueue().offer(taskWorker);
        log.info("Added new worker to thread pool [{}]", taskWorker.getName());
    }

    public void addWorkers(int numberOfThreads) {
        if (numberOfThreads < 0) {
            return;
        }
        for (int i = 0; i < numberOfThreads; i++) {
            addWorker();
        }
    }

    public void removeWorker() {
        TaskWorker taskWorker = queueState.getThreadsQueue().poll();
        log.info("Removed worker from thread pool [{}]", taskWorker.getName());
    }

    public void removeWorkers(int numberOfThreads) {
        if (numberOfThreads < 0) {
            return;
        }
        for (int i = 0; i < numberOfThreads; i++) {
            removeWorker();
        }
    }

    public void removeTask() {
        queueState.getTasksQueue().poll();
    }

    public void removeTasks(int numberOfTasks) {
        if (numberOfTasks < 0) {
            return;
        }
        for (int i = 0; i < numberOfTasks; i++) {
            removeTask();
        }
    }

    public void clear() {
        queueState.getTasksQueue().clear();
        queueState.getThreadsQueue().clear();
    }
}
