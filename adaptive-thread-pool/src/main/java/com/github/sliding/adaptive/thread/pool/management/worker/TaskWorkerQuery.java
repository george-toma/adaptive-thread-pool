package com.github.sliding.adaptive.thread.pool.management.worker;

import com.github.sliding.adaptive.thread.pool.management.Query;

public final class TaskWorkerQuery extends AbstractTaskWorkerManagement implements Query {
    public TaskWorkerQuery(String identifier, TaskWorkerState taskWorkerState) {
        super(identifier, taskWorkerState);
    }

    @Override
    public int size() {
        return taskWorkerState.size();
    }
}
