package com.github.adaptive.threadpool.management.task;

public abstract class AbstractTaskManagement {
    protected TaskState taskState;

    public AbstractTaskManagement(TaskState taskState) {
        this.taskState = taskState;
    }
}
