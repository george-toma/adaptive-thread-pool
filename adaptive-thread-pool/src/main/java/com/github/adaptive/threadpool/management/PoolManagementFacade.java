package com.github.adaptive.threadpool.management;

import com.github.adaptive.threadpool.management.task.TaskCommand;
import com.github.adaptive.threadpool.management.task.TaskQuery;
import com.github.adaptive.threadpool.management.task.TaskState;
import com.github.adaptive.threadpool.management.worker.TaskWorkerCommand;
import com.github.adaptive.threadpool.management.worker.TaskWorkerQuery;
import com.github.adaptive.threadpool.management.worker.TaskWorkerState;
import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.task.Task;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public final class PoolManagementFacade {
    private final Command<TaskWorker> workerCommand;
    private final Command<Task> taskCommand;
    private final Query workerQuery;
    private final Query taskQuery;

    public PoolManagementFacade(String identifier, BlockingQueue<Task> tasks) {
        TaskWorkerState taskWorkerState = new TaskWorkerState();
        TaskState taskState = new TaskState(tasks);
        this.taskCommand = new TaskCommand(taskState);
        this.taskQuery = new TaskQuery(taskState);
        this.workerCommand = new TaskWorkerCommand(identifier, taskWorkerState, taskCommand);
        this.workerQuery = new TaskWorkerQuery(identifier, taskWorkerState);
    }

    public <T extends Command> T doManagement(ManagementType managementType) {

        switch (managementType) {
            case TASK_WORKER_COMMAND:
                return (T) workerCommand;
            case TASK_COMMAND:
                return (T) taskCommand;
        }
        return null;
    }

    public <T extends Query> T doQuery(ManagementType managementType) {

        switch (managementType) {
            case TASK_WORKET_QUERY:
                return (T) workerQuery;
            case TASK_QUERY:
                return (T) taskQuery;
        }
        return null;
    }

    public List<Task> shutdownAll() {
        workerCommand.shutdown();
        return taskCommand.shutdown();
    }

    public enum ManagementType {
        TASK_WORKER_COMMAND,
        TASK_WORKET_QUERY,
        TASK_QUERY,
        TASK_COMMAND
    }
}
