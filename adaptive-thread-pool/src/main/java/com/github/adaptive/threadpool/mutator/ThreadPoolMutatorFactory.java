package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.management.PoolManagementFacade;
import com.github.adaptive.threadpool.management.Query;
import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.management.worker.TaskWorkerCommand;
import com.github.adaptive.threadpool.mutator.exception.NotSupportedMutator;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class ThreadPoolMutatorFactory {

    private final static Map<String, AbstractThreadPoolMutator> MUTATOR = new HashMap<>();

    static {
        ServiceLoader<AbstractThreadPoolMutator> loader = ServiceLoader.load(AbstractThreadPoolMutator.class);
        for (AbstractThreadPoolMutator mutator : loader) {
            MUTATOR.put(mutator.getName(), mutator);
        }
    }

    public final static AbstractThreadPoolMutator threadMutator(String name, PoolManagementFacade poolManagementFacade) {
        Query taskWorkerQuery = poolManagementFacade.doQuery(PoolManagementFacade.ManagementType.TASK_WORKET_QUERY);
        Command<TaskWorker> taskWorkerCommand = poolManagementFacade.doManagement(PoolManagementFacade.ManagementType.TASK_WORKER_COMMAND);

        AbstractThreadPoolMutator poolMutator = MUTATOR.get(name);
        if (poolMutator == null) {
            throw new NotSupportedMutator("No thread poll mutator for " + name);
        }
        poolMutator.setQuery(taskWorkerQuery);
        poolMutator.setTaskWorkerCommand((TaskWorkerCommand) taskWorkerCommand);
        return poolMutator;
    }
}
