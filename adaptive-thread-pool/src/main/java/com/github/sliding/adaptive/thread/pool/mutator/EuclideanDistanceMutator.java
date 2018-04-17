/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactory;
import com.github.sliding.adaptive.thread.pool.queue.ThreadPoolQueueManagement;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author george-toma
 */
@Log4j2
public class EuclideanDistanceMutator extends AbstractThreadPoolMutator {
    public static final double FIVE_PERCENTAGE = 0.05D;
    private static final double IDEAL_SIMILARITY_SCORE = 1.0D;
    private final static double EUCLIDIAN_ARGUMENT_POWER = 2.0D;
    /*
     * how will calculate ?
     * 1. Make ideal point value which is composed of :
     * - current metric.TASK_SUBMISSION_COMPLETED_TIME, and 5% of it as TASK_STARTS_EXECUTION
     * 2. calculate euclidian distance between ideal value and current metric
     * 3. calculate similarity score, 1/1+d(m1,m2)
     * 4. if very dissimilar than a threshold, increase, otherwise decrease or keep it as it is.
     *
     */
    private final long previousTaskStartsExecutionTime = 0L;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private volatile double previousSimilarityScore = IDEAL_SIMILARITY_SCORE;

    public EuclideanDistanceMutator(String threadPoolMutatorName,
                                    AdaptiveThreadFactory threadFactory,
                                    ThreadPoolQueueManagement threadPoolQueueManagement) {
        super(threadPoolMutatorName, threadFactory, threadPoolQueueManagement);
    }

    @Override
    public void mutateThreadPoolSize(String... metricsIdentifiers) {
        if (metricsIdentifiers == null && metricsIdentifiers.length == 0) {
            log.warn("Empty task metric identifier received. Skip thread pool mutation");
            return;
        }
        final String metricIdentifier = metricsIdentifiers[0];
        TaskMetrics metrics = metricsRepositoryQuery.loadMetric(name, metricIdentifier);
        if (metrics == null) {
            log.warn("Empty task metric received. Skip thread pool mutation");
            return;
        }
        log.info("Starting mutating thread pool size for metric [{}]", metricIdentifier);
        final int previousThreadPoolSize = threadPoolQueueManagement.threadPoolSize();
        final long previousTaskSubmissionCompletedTime = metrics.getTaskSubmissionCompletedTime();
        reentrantLock.lock();
        try {
            final double euclideanDistance =
                    Math.sqrt(Math.pow(previousTaskSubmissionCompletedTime - metrics.getTaskSubmissionCompletedTime(), EUCLIDIAN_ARGUMENT_POWER)
                            + Math.pow(previousTaskStartsExecutionTime - metrics.getTaskStartsExecutionTime(), EUCLIDIAN_ARGUMENT_POWER));
            final double currentSimilarityScore = 1.0D / (1.0D + euclideanDistance);
            final double similarityDifference = getSimilarityDifference(currentSimilarityScore);
            if ((similarityDifference) >= FIVE_PERCENTAGE) {
                if (currentSimilarityScore > previousSimilarityScore) {
                    threadPoolQueueManagement.removeWorkers(getThreadPoolMutatorValue());
                } else {
                    threadPoolQueueManagement.addWorkers(getThreadPoolMutatorValue());
                }
            } else {
                log.debug("No thread pool size adjustment done");
            }
            previousSimilarityScore = currentSimilarityScore;
        } finally {
            reentrantLock.unlock();
            log.info("Thread pool size [previous : {}, current: {}]", previousThreadPoolSize,
                    threadPoolQueueManagement.threadPoolSize());
        }
    }

    @Override
    public void increaseThreadPool() {
        final int previousThreadPoolSize = threadPoolQueueManagement.threadPoolSize();
        threadPoolQueueManagement.addWorkers(getThreadPoolMutatorValue());
        log.info("Thread pool size [previous : {}, current: {}]", previousThreadPoolSize,
                threadPoolQueueManagement.threadPoolSize());
    }

    private double getSimilarityDifference(double currentSimilarity) {
        return (Math.abs(currentSimilarity - previousSimilarityScore)) / previousSimilarityScore;
    }

}
