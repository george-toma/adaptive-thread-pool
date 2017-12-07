package com.github.sliding.adaptive.thread.pool.report;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author spykee
 */
public class InMemoryReportHandler extends ReportHandler {

    public InMemoryReportHandler(TimeUnit metricExpierationUnit, int metricExpirationValue) {
        super(metricExpierationUnit, metricExpirationValue);
    }

    @Override
    public void addTaskMetrics(TaskMetrics metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSystemMetrics(ThreadPoolSystemMetrics metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getLastTaskMetrics(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getLastSystemMetrics(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getLastTaskMetric() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getLastSystemMetric() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, ThreadPoolMomentum> getBestMetricsValues(TimeUnit timeUnit, int unitValue, int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
