/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.factory.TaskWorker;
import com.github.sliding.adaptive.thread.pool.factory.thread.AdaptiveThreadFactory;
import com.github.sliding.adaptive.thread.pool.report.InMemoryReportHandler;
import com.github.sliding.adaptive.thread.pool.report.SharedMetricsRepository;
import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author george-toma
 */
public class DefaultThreadPoolMutator extends AbstractThreadPoolMutator {
    public DefaultThreadPoolMutator(AdaptiveThreadFactory threadFactory) {
        super(new InMemoryReportHandler(10),
                threadFactory);
    }

    @Override
    public void mutateThreadPoolSize(BlockingQueue<TaskWorker> threadsQueue) {
        // List<TaskMetrics> metric = reportHandler.getLastTaskMetrics(HISTORIC_METRICS_NUMBER);
        List<TaskMetrics> metrics = SharedMetricsRepository.loadDefault().load(HISTORIC_METRICS_NUMBER);
        if (metrics.isEmpty()) {
            return;
        }
        //increase
        threadsQueue.offer((TaskWorker) threadFactory.newThread(() -> {
            System.out.println("New thread added");
        }));
    }

}
