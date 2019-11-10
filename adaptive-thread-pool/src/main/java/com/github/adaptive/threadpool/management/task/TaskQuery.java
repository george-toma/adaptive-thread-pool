package com.github.adaptive.threadpool.management.task;

import com.github.adaptive.threadpool.management.Query;

public class TaskQuery extends AbstractTaskManagement implements Query {
    public TaskQuery(TaskState taskState) {
        super(taskState);
    }

    @Override
    public int size() {
        return taskState.size();
    }
}
