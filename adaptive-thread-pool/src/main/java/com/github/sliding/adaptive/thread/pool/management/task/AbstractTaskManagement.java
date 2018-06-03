package com.github.sliding.adaptive.thread.pool.management.task;

public abstract class AbstractTaskManagement {
    protected TaskState taskState;

    public AbstractTaskManagement(TaskState taskState) {
        this.taskState = taskState;
    }
}
