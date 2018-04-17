package com.github.sliding.adaptive.thread.pool.queue;

import com.github.sliding.adaptive.thread.pool.Task;
import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;

import java.util.concurrent.BlockingQueue;

public class ThreadPoolQueueState {

    private final BlockingQueue<TaskWorker> threadsQueue;
    private final BlockingQueue<Task> tasksQueue;

    public ThreadPoolQueueState(BlockingQueue<TaskWorker> threadsQueue, BlockingQueue<Task> tasksQueue) {
        this.threadsQueue = threadsQueue;
        this.tasksQueue = tasksQueue;
    }

    BlockingQueue<TaskWorker> getThreadsQueue() {
        return threadsQueue;
    }

    BlockingQueue<Task> getTasksQueue() {
        return tasksQueue;
    }
}
