package com.github.sliding.adaptive.thread.pool.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author george-toma
 */
public abstract class ReportHandler {

    protected final int numberOfMetrics;
    protected final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ReportHandler(int numberOfMetrics) {
        this.numberOfMetrics = numberOfMetrics;
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
