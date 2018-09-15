/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.management.Command;
import com.github.sliding.adaptive.thread.pool.management.Query;
import com.github.sliding.adaptive.thread.pool.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author george-toma
 *
 * <p>
 * Note: Contended annotation does not work on user classpath by default and only work for classes on bootclasspath.
 * Add -XX:-RestrictContended VM argument on JVM startup.
 * </p>
 */
@Log4j2
public final class EuclideanDistanceMutator extends AbstractThreadPoolMutator {
    private static final double FIVE_PERCENTAGE = 0.05D;
    private static final double IDEAL_SIMILARITY_SCORE = 1.0D;
    private final static double EUCLIDEAN_ARGUMENT_POWER = 2.0D;
    //(1% - error)
    private final static double THRESHOLD_NO_MUTATION = 0.009D;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    /*
     * 1. Make ideal point value which is composed of :
     * - current metric.TASK_SUBMISSION_COMPLETED_TIME, and 5% of it as TASK_STARTS_EXECUTION
     * 2. calculate euclidian distance between ideal value and current metric
     * 3. calculate similarity score, 1/1+d(m1,m2)
     * 4. if very dissimilar than a threshold, increase, otherwise decrease or keep it as it is.
     *
     */
    private volatile long previousTaskStartsExecutionTime = 0L;
    private volatile long previousTaskSubmissionCompletedTime = 0L;
    private double previousSimilarityScore = IDEAL_SIMILARITY_SCORE;

    private MutationState mutationState = MutationState.STALE;

    public EuclideanDistanceMutator(String threadPoolMutatorName,
                                    Command<TaskWorker> taskWorkerCommand,
                                    Query query) {
        super(threadPoolMutatorName, taskWorkerCommand, query);
    }

    @Override
    public void mutateThreadPoolSize(TaskMetrics... taskMetrics) {
        if (taskMetrics == null || taskMetrics.length == 0) {
            log.warn("Empty task metric taskId received. Skip thread pool mutation");
            return;
        }
        final TaskMetrics metric = taskMetrics[0];
        final String taskId = metric.getTaskId();
        log.info("Starting mutating thread pool size for task [id: {}]", taskId);
        reentrantLock.lock();
        try {
            final double currentSimilarityScore = computeEuclidianScore(metric.getTaskSubmissionCompletedTime(),
                    metric.getTaskStartsExecutionTime());
            final double similarityDifference = getSimilarityDifference(currentSimilarityScore);

            log.info("Score [previous: {}, current: {}, diff: {}]",
                    previousSimilarityScore, currentSimilarityScore, similarityDifference);

            if (similarityDifference >= FIVE_PERCENTAGE) {
                if (currentSimilarityScore < previousSimilarityScore && (mutationState == MutationState.INCREASE)) {
                    evictWorkers();
                } else {
                    addWorkers();
                }
            } else if (similarityDifference <= THRESHOLD_NO_MUTATION) {
                log.info("No thread pool size adjustment done for task [id: {}]", taskId);
            } else {
                addWorkers();
            }
            //set previous state
            previousSimilarityScore = currentSimilarityScore;
            previousTaskSubmissionCompletedTime = metric.getTaskSubmissionCompletedTime();
            previousTaskStartsExecutionTime = metric.getTaskStartsExecutionTime();

        } finally {
            reentrantLock.unlock();
            final int currentPoolSize = query.size();
            log.info("Thread pool size [current: {}]",
                    currentPoolSize);
        }
    }

    private double computeEuclidianScore(final double taskSubmissionCompletedTime, final double taskStartsExecutionTime) {
        final double euclideanDistance =
                Math.sqrt(Math.pow(previousTaskSubmissionCompletedTime - taskSubmissionCompletedTime, EUCLIDEAN_ARGUMENT_POWER)
                        + Math.pow(previousTaskStartsExecutionTime - taskStartsExecutionTime, EUCLIDEAN_ARGUMENT_POWER));

        return 1.0D / (1.0D + euclideanDistance);
    }

    private void evictWorkers() {
        taskWorkerCommand.remove(getThreadPoolMutatorValue());
        log.info("Starting to remove [{}] workers from thread pool", getThreadPoolMutatorValue());
        mutationState = MutationState.DECREASE;
    }

    private void addWorkers() {
        log.info("Starting to add new [{}] workers to thread pool", getThreadPoolMutatorValue());
        //FIXME array
        for (int i = 0; i < getThreadPoolMutatorValue(); i++) {
            taskWorkerCommand.add(null);
        }
        mutationState = MutationState.INCREASE;
    }

    private double getSimilarityDifference(double currentSimilarity) {
        return (Math.abs(currentSimilarity - previousSimilarityScore)) / previousSimilarityScore;
    }
}
