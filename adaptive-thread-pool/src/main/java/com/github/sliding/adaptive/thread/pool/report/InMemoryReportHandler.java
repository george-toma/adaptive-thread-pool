package com.github.sliding.adaptive.thread.pool.report;

import com.github.sliding.adaptive.thread.pool.report.metric.TaskMetrics;
import com.github.sliding.adaptive.thread.pool.report.metric.ThreadPoolSystemMetrics;
import com.github.sliding.adaptive.thread.pool.report.momentum.ThreadPoolMomentum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author george-toma
 */
public class InMemoryReportHandler extends ReportHandler {

    /**
     *
     * @param numberOfMetrics Window_start param of sliding algorithm
     */
    public InMemoryReportHandler( int numberOfMetrics) {
        super(numberOfMetrics);
    }

    @Override
    public void addTaskMetrics(TaskMetrics metrics) {
        
    }

    @Override
    public void addSystemMetrics(ThreadPoolSystemMetrics metrics) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TaskMetrics> getLastTaskMetrics(int offset) {
        return null;
    }

    @Override
    public void getLastSystemMetrics(int offset) {


    }

    @Override
    public void getLastTaskMetrics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getLastSystemMetrics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, ThreadPoolMomentum> getBestMetricsValues(TimeUnit timeUnit, int unitValue, int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
