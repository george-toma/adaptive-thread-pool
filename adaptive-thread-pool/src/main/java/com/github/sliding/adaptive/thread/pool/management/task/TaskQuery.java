package com.github.sliding.adaptive.thread.pool.management.task;

import com.github.sliding.adaptive.thread.pool.management.Query;

public class TaskQuery extends AbstractTaskManagement implements Query {
    public TaskQuery(TaskState taskState) {
        super(taskState);
    }

    @Override
    public int size() {
        return taskState.size();
    }
}
