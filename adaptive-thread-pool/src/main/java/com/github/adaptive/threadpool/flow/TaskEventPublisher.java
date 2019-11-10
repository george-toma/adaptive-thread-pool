package com.github.adaptive.threadpool.flow;

import java.util.concurrent.Executors;

final class TaskEventPublisher extends EventPublisher {
    public TaskEventPublisher() {
        super(Executors.newSingleThreadExecutor());
    }
}
