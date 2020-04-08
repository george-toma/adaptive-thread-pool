package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.management.Query;
import com.github.adaptive.threadpool.management.worker.TaskWorkerCommand;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected  TaskWorkerCommand taskWorkerCommand;
    protected  Query query;
   
    public void setQuery(Query query) {
        this.query = query;
    }

    public void setTaskWorkerCommand(TaskWorkerCommand taskWorkerCommand) {
        this.taskWorkerCommand = taskWorkerCommand;
    }

    public enum MutationState {
        INCREASE, DECREASE, STALE
    }
}
