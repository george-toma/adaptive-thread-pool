package com.github.sliding.adaptive.thread.pool.report;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.metric.ThreadPoolSystemMetrics;
import com.github.sliding.adaptive.thread.pool.report.momentum.ThreadPoolMomentum;

import java.time.format.DateTimeFormatter;
import java.util.List;
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

    public abstract List<TaskMetrics> getLastTaskMetrics(int offset);

    public abstract void getLastSystemMetrics(int offset);

    public abstract void getLastTaskMetrics();

    public abstract void getLastSystemMetrics();


    /**
     * Returns the datetime and the values with the most optimal thread pool
     * setting.
     */
    public abstract Map<String, ThreadPoolMomentum> getBestMetricsValues(TimeUnit timeUnit, final int unitValue, final int offset);
}
