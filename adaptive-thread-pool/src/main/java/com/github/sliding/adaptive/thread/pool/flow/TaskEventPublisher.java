package com.github.sliding.adaptive.thread.pool.flow;

import java.util.concurrent.Executors;

final class TaskEventPublisher extends EventPublisher {
    public TaskEventPublisher() {
        super(Executors.newSingleThreadExecutor());
    }
}
