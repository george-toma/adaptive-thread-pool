package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.validation.logging.LogValidator;
import com.github.adaptive.threadpool.factory.TaskWorker;
import com.github.adaptive.threadpool.management.Command;
import com.github.adaptive.threadpool.management.Query;
import com.github.adaptive.threadpool.management.task.TaskCommand;
import com.github.adaptive.threadpool.management.task.TaskState;
import com.github.adaptive.threadpool.management.worker.TaskWorkerCommand;
import com.github.adaptive.threadpool.management.worker.TaskWorkerQuery;
import com.github.adaptive.threadpool.management.worker.TaskWorkerState;
import com.github.adaptive.threadpool.metric.TaskMetrics;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.SynchronousQueue;

class EuclideanDistanceMutatorTest {
    private final String adaptiveThreadPoolId = "test-adaptiveThreadPoolId";
    private Command<TaskWorker> taskWorkerCommand;
    private Query taskWorkerQuery;
    private AbstractThreadPoolMutator poolMutator;

    @BeforeEach
    void setup() {
        LogValidator.clear();
        TaskWorkerState taskWorkerState = new TaskWorkerState();
        taskWorkerCommand = new TaskWorkerCommand(adaptiveThreadPoolId,
                taskWorkerState, new TaskCommand(new TaskState(new SynchronousQueue<>())));
        taskWorkerQuery = new TaskWorkerQuery(adaptiveThreadPoolId, taskWorkerState);
        poolMutator = new EuclideanDistanceMutator();
        poolMutator.setQuery(taskWorkerQuery);
        poolMutator.setTaskWorkerCommand(taskWorkerCommand);
    }

    @AfterEach
    void tearDown() {
        taskWorkerCommand.clear();
    }

    @Test
    void test_mutateThreadPoolSize_when_noMutationDone() {
        //given

        //when
        poolMutator.mutateThreadPoolSize(null);
        //then
        LogValidator.assertLogEvent("Empty task metric taskId received. Skip thread pool mutation", Level.WARN);
    }

    @Test
    void test_mutateThreadPoolSize_when_mutationToIncreaseDone() {
        //given
        TaskMetrics.Builder taskIncreasePoolSize = createMetric(0L, 0L + 10L);

        //when
        poolMutator.mutateThreadPoolSize(taskIncreasePoolSize.build());

        //then
        final int expectedThreadPoolSize = poolMutator.getThreadPoolMutatorValue();
        Assertions.assertEquals(expectedThreadPoolSize, taskWorkerQuery.size());
        LogValidator.assertLogEvent(
                String.format("Starting to add new [%s] workers to thread pool", expectedThreadPoolSize), Level.INFO);
    }

    @Test
    void test_mutateThreadPoolSize_when_mutationToDecreaseDone() {
        long timestamp = 0L;
        //given INCREASE CASE
        TaskMetrics.Builder taskIncreasePoolSize = createMetric(timestamp, timestamp + 10L);
        //when INCREASE case
        poolMutator.mutateThreadPoolSize(taskIncreasePoolSize.build());

        //then INCREASE case
        int expectedThreadPoolSize = poolMutator.getThreadPoolMutatorValue();
        Assertions.assertEquals(expectedThreadPoolSize, taskWorkerQuery.size());
        LogValidator.assertLogEvent(
                String.format("Starting to add new [%s] workers to thread pool", expectedThreadPoolSize), Level.INFO);

        //given DECREASE case
        TaskMetrics.Builder taskDecreasePoolSize = createMetric(timestamp + 10L, timestamp + 20);

        //when DECREASE case
        poolMutator.mutateThreadPoolSize(taskDecreasePoolSize.build());

        //then DECREASE case
        Assertions.assertEquals(0, taskWorkerQuery.size());
        LogValidator.assertLogEvent(
                "Starting to remove [1] workers from thread pool", Level.INFO);

    }

    private TaskMetrics.Builder createMetric(long startingTime, long endingTime) {
        // poate tre sa pun long startingTime, long endingTime
        String uuid = UUID.randomUUID().toString();

        TaskMetrics.Builder builder = TaskMetrics.builder()
                .withTaskId(uuid)
                .withTaskSubmissionCompletedTime(startingTime)
                .withTaskStartsExecutionTime(endingTime);
        return builder;
    }
}