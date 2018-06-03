package com.github.sliding.adaptive.thread.pool.report.metric;

import java.util.Objects;

/**
 * @author george-toma
 */
public class TaskMetrics {

    private long taskClientSubmissionTime;
    private long taskSubmissionCompletedTime;
    private long taskStartsExecutionTime;
    private long taskFinishedTime;
    private final String identifier;
    private boolean isComplete;

    private TaskMetrics(Builder builder) {
        this.taskClientSubmissionTime = builder.taskClientSubmissionTime;
        this.taskFinishedTime = builder.taskFinishedTime;
        this.taskStartsExecutionTime = builder.taskStartsExecutionTime;
        this.taskSubmissionCompletedTime = builder.taskSubmissionCompletedTime;
        this.isComplete = builder.isComplete;
        this.identifier = builder.identifier;
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

    public boolean isComplete() {
        return isComplete;
    }

    /**
     * @return {@link TaskMetrics#taskStartsExecutionTime} - {@link TaskMetrics#taskSubmissionCompletedTime}
     */
    public long getTaskIdleTime() {
        return taskStartsExecutionTime - taskSubmissionCompletedTime;
    }

    /**
     * @return {@link TaskMetrics#taskSubmissionCompletedTime} - {@link TaskMetrics#taskClientSubmissionTime}
     */
    public long getTaskResponseTime() {
        return taskSubmissionCompletedTime - taskClientSubmissionTime;
    }

    /**
     * @return {@link TaskMetrics#taskFinishedTime} - {@link TaskMetrics#taskStartsExecutionTime}
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskMetrics that = (TaskMetrics) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long taskClientSubmissionTime;
        private long taskSubmissionCompletedTime;
        private long taskStartsExecutionTime;
        private long taskFinishedTime;
        private boolean isComplete;
        private String identifier;

        private Builder() {
        }

        public Builder complete(boolean isComplete) {
            this.isComplete = isComplete;
            return this;
        }

        public String identifier() {
            return identifier;
        }

        public boolean isComplete() {
            return isComplete;
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

        public long getTaskStartsExecutionTime() {
            return taskStartsExecutionTime;
        }

        public long getTaskFinishedTime() {
            return taskFinishedTime;
        }

        public Builder withTaskFinishedTime(long timestamp) {
            this.taskFinishedTime = timestamp;
            return this;
        }

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public TaskMetrics build() {
            return new TaskMetrics(this);
        }
    }
}
