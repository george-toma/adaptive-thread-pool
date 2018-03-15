package com.github.sliding.adaptive.thread.pool.mutator;

import com.github.sliding.adaptive.thread.pool.report.ReportHandler;

import java.util.concurrent.ThreadFactory;

public abstract class AbstractThreadPoolMutator implements ThreadPoolMutator {
    protected final ReportHandler reportHandler;
    protected final ThreadFactory threadFactory;
    protected final int HISTORIC_METRICS_NUMBER = 3;
    ;

    public AbstractThreadPoolMutator(ReportHandler reportHandler, ThreadFactory threadFactory) {
        if (reportHandler == null) {
            throw new NullPointerException();
        }
        this.reportHandler = reportHandler;
        this.threadFactory = threadFactory;
    }

    public ReportHandler getReportHandler() {
        return reportHandler;
    }
}
