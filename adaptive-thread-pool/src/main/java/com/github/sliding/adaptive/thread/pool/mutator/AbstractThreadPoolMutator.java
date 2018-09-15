package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.management.Query;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected final String name;
    protected final Command<TaskWorker> taskWorkerCommand;
    protected final Query query;

    public AbstractThreadPoolMutator(String name,
                                     Command<TaskWorker> taskWorkerCommand,
                                     Query query) {
        this.name = name;
        this.query = query;
        this.taskWorkerCommand = taskWorkerCommand;
    }

    public enum MutationState {
        INCREASE, DECREASE, STALE
    }
}
