package com.github.sliding.adaptive.thread.pool.report.metric;

/**
 *
 * @author george-toma
 */
public class TaskMetrics {

    private long taskClientSubmissionTime;
    private long taskSubmissionCompletedTime;
    private long taskStartsExecutionTime;
    private long taskFinishedTime;

    private TaskMetrics(Builder builder) {
        this.taskClientSubmissionTime = builder.taskClientSubmissionTime;
        this.taskFinishedTime = builder.taskFinishedTime;
        this.taskStartsExecutionTime = builder.taskStartsExecutionTime;
        this.taskSubmissionCompletedTime = builder.taskSubmissionCompletedTime;
    }

    public long getTaskClientSubmissionTime() {
        return taskClientSubmissionTime;
    }

    public long getTaskSubmissionCompletedTime() {
        return taskSubmissionCompletedTime;
    }

    public long getTaskStartsExecutionTime() {
        return taskStartsExecutionTime;
    }

    public long getTaskFinishedTime() {
        return taskFinishedTime;
    }

    /**
     * @return
     * {@link TaskMetrics#taskStartsExecutionTime} - {@link TaskMetrics#taskSubmissionCompletedTime}
     */
    public long getTaskIdleTime() {
        return taskStartsExecutionTime - taskSubmissionCompletedTime;
    }

    /**
     * @return
     * {@link TaskMetrics#taskSubmissionCompletedTime} - {@link TaskMetrics#taskClientSubmissionTime}
     */
    public long getTaskResponseTime() {
        return taskSubmissionCompletedTime - taskClientSubmissionTime;
    }

    /**
     * @return
     * {@link TaskMetrics#taskFinishedTime} - {@link TaskMetrics#taskStartsExecutionTime}
     *
     */
    public long getTaskProcessingTime() {
        return taskFinishedTime - taskStartsExecutionTime;
    }

    /**
     * @return {@link TaskMetrics#getTaskIdleTime()} + {@link TaskMetrics#getTaskResponseTime()}
     * + {@link TaskMetrics#getTaskProcessingTime()}
     */
    public long getTaskTotalTime() {
        return getTaskIdleTime() + getTaskResponseTime() + getTaskProcessingTime();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long taskClientSubmissionTime;
        private long taskSubmissionCompletedTime;
        private long taskStartsExecutionTime;
        private long taskFinishedTime;

        private Builder() {
        }

        public Builder withTaskClientSubmissionTime(long timestamp) {
            this.taskClientSubmissionTime = timestamp;
            return this;
        }

        public Builder withTaskSubmissionCompletedTime(long timestamp) {
            this.taskSubmissionCompletedTime = timestamp;
            return this;
        }

        public Builder withTaskStartsExecutionTime(long timestamp) {
            this.taskStartsExecutionTime = timestamp;
            return this;
        }

        public Builder withTaskFinishedTime(long timestamp) {
            this.taskFinishedTime = timestamp;
            return this;
        }

        public TaskMetrics build() {
            return new TaskMetrics(this);
        }
    }
}
