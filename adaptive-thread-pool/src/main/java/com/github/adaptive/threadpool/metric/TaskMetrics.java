package com.github.adaptive.threadpool.metric;

import com.github.adaptive.threadpool.listener.event.EventType;
import com.github.adaptive.threadpool.task.Task;

import java.util.Objects;

/**
 * @author george-toma
 */
public final class TaskMetrics {

    private final long taskClientSubmissionTime;
    private final long taskSubmissionCompletedTime;
    private final long taskStartsExecutionTime;
    private final long taskFinishedTime;
    private final String taskId;

    private TaskMetrics(Builder builder) {
        this.taskClientSubmissionTime = builder.taskClientSubmissionTime;
        this.taskFinishedTime = builder.taskFinishedTime;
        this.taskStartsExecutionTime = builder.taskStartsExecutionTime;
        this.taskSubmissionCompletedTime = builder.taskSubmissionCompletedTime;
        this.taskId = builder.taskId();
    }

    public String getTaskId() {
        return taskId;
    }

    public static Builder builder() {
        return new Builder();
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
        return taskClientSubmissionTime == that.taskClientSubmissionTime &&
                taskSubmissionCompletedTime == that.taskSubmissionCompletedTime &&
                taskStartsExecutionTime == that.taskStartsExecutionTime &&
                taskFinishedTime == that.taskFinishedTime;}

    @Override
    public int hashCode() {

        return Objects.hash(taskClientSubmissionTime, taskSubmissionCompletedTime, taskStartsExecutionTime, taskFinishedTime);
    }

    public static class Builder {

        private long taskClientSubmissionTime;
        private long taskSubmissionCompletedTime;
        private long taskStartsExecutionTime;
        private long taskFinishedTime;
        private String taskId;

        private Builder() {
        }



        public String taskId() {
            return taskId;
        }

        public Builder withTaskSubmissionCompletedTime(long timestamp) {
            this.taskSubmissionCompletedTime = timestamp;
            return this;
        }
        public Builder withMetrics(Task task){
            for(EventType eventType:EventType.values()){
                final long value = task.readMetric(eventType);
                switch (eventType){
                    case TASK_FINISHED_TIME:this.taskFinishedTime = value;break;
                    case TASK_CLIENT_SUBMISSION_TIME:this.taskClientSubmissionTime = value;break;
                    case TASK_STARTS_EXECUTION:this.taskStartsExecutionTime = value;break;
                    case TASK_SUBMISSION_COMPLETED_TIME:this.taskSubmissionCompletedTime = value;break;
                }
            }
            return this;
        }
        public Builder withTaskStartsExecutionTime(long timestamp) {
            this.taskStartsExecutionTime = timestamp;
            return this;
        }

        public Builder withTaskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public TaskMetrics build() {
            return new TaskMetrics(this);
        }
    }
}
