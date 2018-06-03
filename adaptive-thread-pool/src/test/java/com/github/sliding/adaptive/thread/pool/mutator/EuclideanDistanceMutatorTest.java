package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.management.Query;
import com.github.sliding.adaptive.thread.pool.management.task.TaskCommand;
import com.github.sliding.adaptive.thread.pool.management.task.TaskState;
import com.github.sliding.adaptive.thread.pool.management.worker.TaskWorkerCommand;
import com.github.sliding.adaptive.thread.pool.management.worker.TaskWorkerQuery;
import com.github.sliding.adaptive.thread.pool.management.worker.TaskWorkerState;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.repository.TaskMetricsRepository;
import com.github.sliding.adaptive.thread.pool.validation.logging.LogValidator;
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
    private TaskMetricsRepository taskMetricsRepository;

    @BeforeEach
    void setup() {
        LogValidator.clear();
        taskMetricsRepository = new TaskMetricsRepository(adaptiveThreadPoolId);
        TaskWorkerState taskWorkerState = new TaskWorkerState();
        taskWorkerCommand = new TaskWorkerCommand(adaptiveThreadPoolId,
                taskWorkerState, new TaskCommand(new TaskState(new SynchronousQueue<>())));
        taskWorkerQuery = new TaskWorkerQuery(adaptiveThreadPoolId, taskWorkerState);
        poolMutator = new EuclideanDistanceMutator(adaptiveThreadPoolId,
                taskWorkerCommand,
                taskWorkerQuery);
    }

    @AfterEach
    void tearDown() {
        taskWorkerCommand.clear();
        taskMetricsRepository.shutdownRepository();
    }

    @Test
    void test_mutateThreadPoolSize_when_noMutationDone() {
        //given

        //when
        poolMutator.mutateThreadPoolSize("non-existing-metric");
        //then
        LogValidator.assertLogEvent("Empty task metric received. Skip thread pool mutation", Level.WARN);
    }

    @Test
    void test_mutateThreadPoolSize_when_mutationToIncreaseDone() {
        //given
        String metricIdentifier = "metric-id";
        TaskMetrics.Builder taskIncreasePoolSize = createMetric(0L, 0L + 10L);
        taskMetricsRepository.store(metricIdentifier, taskIncreasePoolSize);

        //when
        poolMutator.mutateThreadPoolSize(metricIdentifier);

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
        taskMetricsRepository.store(taskIncreasePoolSize.identifier(), taskIncreasePoolSize);
        //when INCREASE case
        poolMutator.mutateThreadPoolSize(taskIncreasePoolSize.identifier());

        //then INCREASE case
        int expectedThreadPoolSize = poolMutator.getThreadPoolMutatorValue();
        Assertions.assertEquals(expectedThreadPoolSize, taskWorkerQuery.size());
        LogValidator.assertLogEvent(
                String.format("Starting to add new [%s] workers to thread pool", expectedThreadPoolSize), Level.INFO);

        //given DECREASE case
        TaskMetrics.Builder taskDecreasePoolSize = createMetric(timestamp + 10L, timestamp + 20);
        taskMetricsRepository.store(taskDecreasePoolSize.identifier(), taskDecreasePoolSize);

        //when DECREASE case
        poolMutator.mutateThreadPoolSize(taskDecreasePoolSize.identifier());

        //then DECREASE case
        Assertions.assertEquals(0, taskWorkerQuery.size());
        LogValidator.assertLogEvent(
                "Starting to remove [1] workers from thread pool", Level.INFO);

    }

    private TaskMetrics.Builder createMetric(long startingTime, long endingTime) {
        // poate tre sa pun long startingTime, long endingTime
        String uuid = UUID.randomUUID().toString();

        TaskMetrics.Builder builder = TaskMetrics.builder()
                .withIdentifier(uuid)
                .withTaskSubmissionCompletedTime(startingTime)
                .withTaskStartsExecutionTime(endingTime);
        return builder;
    }
}