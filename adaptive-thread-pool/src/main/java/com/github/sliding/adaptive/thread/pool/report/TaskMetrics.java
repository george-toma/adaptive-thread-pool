package com.github.sliding.adaptive.thread.pool.report;

/**
 *
 * @author george-toma
 */
public class TaskMetrics {

    private long taskSubmissionTime;
    private long taskAcceptanceTime;
    private long taskExecutionTime;
    private long taskFinishedTime;
    //the time between task submission completed and task execution
    private long taskIdleTime;
    //the time between client submit task and task submission complete
    private long taskResponseTime;
    //– the time between task starts execution and task finished
    private long taskProcessingTime;
    //taskIdleTime  + taskResponseTime  + taskProcessingTime
    private long taskTotalTime;

    class Builder {

    }
}
