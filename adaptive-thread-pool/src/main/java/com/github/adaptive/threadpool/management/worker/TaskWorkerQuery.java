package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.management.Query;

public final class TaskWorkerQuery extends AbstractTaskWorkerManagement implements Query {
    public TaskWorkerQuery(TaskWorkerState taskWorkerState) {
        super(taskWorkerState);
    }

    @Override
    public int size() {
        return taskWorkerState.size();
    }
}
