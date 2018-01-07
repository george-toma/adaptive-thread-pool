package com.github.sliding.adaptive.thread.pool.report;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author george-toma
 */
public class InMemoryReportHandler extends ReportHandler {
    /*create a smart data structure to hold needed data;
     * automatically remove older values;
     */
  //  private final Map<String>

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
