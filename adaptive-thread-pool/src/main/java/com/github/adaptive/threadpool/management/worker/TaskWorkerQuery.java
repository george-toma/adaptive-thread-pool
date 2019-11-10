package com.github.adaptive.threadpool.management.worker;

import com.github.adaptive.threadpool.management.Query;

public final class TaskWorkerQuery extends AbstractTaskWorkerManagement implements Query {
    public TaskWorkerQuery(String identifier, TaskWorkerState taskWorkerState) {
        super(identifier, taskWorkerState);
    }

    @Override
    public int size() {
        return taskWorkerState.size();
    }
}
