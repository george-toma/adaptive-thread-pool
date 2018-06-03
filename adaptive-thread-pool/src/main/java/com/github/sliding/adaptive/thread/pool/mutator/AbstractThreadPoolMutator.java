package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.management.Query;
import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected final String name;
    protected final TaskMetricsRepository metricsRepositoryQuery;
    protected final Command<TaskWorker> taskWorkerCommand;
    protected final Query query;

    public AbstractThreadPoolMutator(String name,
                                     Command<TaskWorker> taskWorkerCommand,
                                     Query query) {
        this.name = name;
        this.query = query;
        this.taskWorkerCommand = taskWorkerCommand;
        metricsRepositoryQuery = SharedMetricsRepository.load(name).get();
    }

    public enum MutationState {
        INCREASE, DECREASE, STALE
    }
}
