package com.github.sliding.adaptive.thread.pool.management.worker;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;

import java.util.ArrayDeque;
import java.util.Queue;

public final class TaskWorkerState {

    //FIXME reentrant read + write lock ?
    private Queue<TaskWorker> tasksWorkers = new ArrayDeque<>();

    public int size() {
        return tasksWorkers.size();
    }

    public Queue<TaskWorker> getTasksWorkers() {
        return tasksWorkers;
    }

    public void add(TaskWorker worker) {
        tasksWorkers.add(worker);
    }

    public TaskWorker poll() {
        return tasksWorkers.poll();
    }

    public void clear() {
        tasksWorkers.clear();
    }
}
