package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.management.Query;
import com.github.adaptive.threadpool.factory.TaskWorker;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected Command<TaskWorker> taskWorkerCommand;
    protected Query query;

    public Command<TaskWorker> getTaskWorkerCommand() {
        return taskWorkerCommand;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setTaskWorkerCommand(Command<TaskWorker> taskWorkerCommand) {
        this.taskWorkerCommand = taskWorkerCommand;
    }

    public enum MutationState {
        INCREASE, DECREASE, STALE
    }
}
