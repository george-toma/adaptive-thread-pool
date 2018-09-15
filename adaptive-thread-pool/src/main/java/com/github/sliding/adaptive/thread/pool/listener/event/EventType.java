package com.github.sliding.adaptive.thread.pool.listener.event;

public enum EventType {

    TASK_CLIENT_SUBMISSION_TIME(0),
    TASK_SUBMISSION_COMPLETED_TIME(1),
    TASK_STARTS_EXECUTION(2),
    TASK_FINISHED_TIME(3);

    private final int order;

    EventType(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }
}
