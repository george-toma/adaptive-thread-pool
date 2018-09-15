package com.github.sliding.adaptive.thread.pool.management.task;

import com.github.sliding.adaptive.thread.pool.task.Task;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Log4j2
public final class TaskState {

    private final BlockingQueue<Task> tasksQueue;

    public TaskState(BlockingQueue<Task> tasksQueue) {
        this.tasksQueue = tasksQueue;
    }

    public int size() {
        return tasksQueue.size();
    }

    public void clear() {
        tasksQueue.clear();
    }

    public Task poll() {
        return tasksQueue.poll();
    }

    public List<Task> drainTask() {
        final List<Task> notProcessedTasks = new ArrayList<>(size());
        tasksQueue.drainTo(notProcessedTasks);

        return notProcessedTasks;
    }

    public void put(Task task) {
        try {
            tasksQueue.put(task);
        } catch (InterruptedException ex) {
            log.warn("Could not put task into management", ex);
        }
    }
}
