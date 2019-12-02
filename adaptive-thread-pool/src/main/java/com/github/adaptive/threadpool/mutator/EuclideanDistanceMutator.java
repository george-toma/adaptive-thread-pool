/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adaptive.threadpool.mutator;

import com.github.adaptive.threadpool.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.locks.ReentrantLock;


@Log4j2
public final class EuclideanDistanceMutator extends AbstractThreadPoolMutator {
    private static final double POSITIVE_DIFF_PERCENTAGE = 0.10D;
    private static final double NEGATIVE_DIFF_PERCENTAGE = -0.10D;

    private static final double IDEAL_SIMILARITY_SCORE = 1.0D;
    private final static double EUCLIDEAN_ARGUMENT_POWER = 2.0D;
    //(1% - error)
    private final ReentrantLock reentrantLock = new ReentrantLock();
    /*
     * 1. Make ideal point value which is composed of :
     * - current metric.TASK_SUBMISSION_COMPLETED_TIME, and 5% of it as TASK_STARTS_EXECUTION
     * 2. calculate euclidian distance between ideal value and current metric
     * 3. calculate similarity score, 1/1+d(m1,m2)
     * 4. if very dissimilar then a threshold, increase, otherwise decrease or keep it as it is.
     *
     */
    private volatile long previousTaskStartsExecutionTime = 0L;
    private volatile long previousTaskSubmissionCompletedTime = 0L;
    private double previousSimilarityScore = IDEAL_SIMILARITY_SCORE;

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

            if (similarityDifference >= POSITIVE_DIFF_PERCENTAGE) {
                evictWorkers();
            } else if (similarityDifference <= (NEGATIVE_DIFF_PERCENTAGE)) {
                addWorkers();
            } else {
                log.info("No thread pool size adjustment done for task [id: {}]", taskId);
            }
            //set previous state
            previousSimilarityScore = currentSimilarityScore;
            previousTaskSubmissionCompletedTime = metric.getTaskSubmissionCompletedTime();
            previousTaskStartsExecutionTime = metric.getTaskStartsExecutionTime();

        } finally {
            reentrantLock.unlock();
        }
    }

    private double computeEuclidianScore(final double taskSubmissionCompletedTime, final double taskStartsExecutionTime) {
        final double euclideanDistance =
                Math.sqrt(Math.pow(previousTaskSubmissionCompletedTime - taskSubmissionCompletedTime, EUCLIDEAN_ARGUMENT_POWER)
                        + Math.pow(previousTaskStartsExecutionTime - taskStartsExecutionTime, EUCLIDEAN_ARGUMENT_POWER));

        return 1.0D / (1.0D + euclideanDistance);
    }

    private void evictWorkers() {
        final int poolSize = query.size();
        log.info("Starting to remove [{}] workers from thread pool [currentSize: {}, newSize: {}]",
                getThreadPoolMutatorValue(), poolSize, poolSize - getThreadPoolMutatorValue());
        taskWorkerCommand.remove(getThreadPoolMutatorValue());
    }

    private void addWorkers() {
        final int poolSize = query.size();
        log.info("Starting to add new [{}] workers to thread pool [currentSize: {}, newSize: {}]",
                getThreadPoolMutatorValue(), poolSize, poolSize + getThreadPoolMutatorValue());
        for (int i = 0; i < getThreadPoolMutatorValue(); i++) {
            taskWorkerCommand.add(null);
        }
    }

    private double getSimilarityDifference(double currentSimilarity) {
        return (currentSimilarity - previousSimilarityScore) / previousSimilarityScore;
    }

    @Override
    public String getName() {
        return "euclidean";
    }
}
