package com.github.sliding.adaptive.thread.pool.report;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author spykee
 */
public abstract class ReportHandler {

    private final TimeUnit metricExpierationUnit;
    private final int metricExpirationValue;

    public ReportHandler(TimeUnit metricExpierationUnit, int metricExpirationValue) {
        this.metricExpierationUnit = metricExpierationUnit;
        this.metricExpirationValue = metricExpirationValue;
    }

    public abstract void addTaskMetrics(TaskMetrics metrics);

    public abstract void addSystemMetrics(ThreadPoolSystemMetrics metrics);

    public abstract void getLastTaskMetrics(int offset);

    public abstract void getLastSystemMetrics(int offset);

    public abstract void getLastTaskMetric();

    public abstract void getLastSystemMetric();

    /**
     * Returns the datetime and the values with the most optimal thread pool
     * setting.
     */
    public abstract Map<String, ThreadPoolMomentum> getBestMetricsValues(TimeUnit timeUnit, final int unitValue, final int offset);
}
