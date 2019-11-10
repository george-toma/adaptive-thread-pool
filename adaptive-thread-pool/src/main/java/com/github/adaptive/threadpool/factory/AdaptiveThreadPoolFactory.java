package com.github.adaptive.threadpool.factory;

import com.github.adaptive.threadpool.AdaptiveThreadPool;
import com.github.adaptive.threadpool.task.Task;

import java.util.concurrent.BlockingQueue;

public final class AdaptiveThreadPoolFactory {

    public Builder describedAs() {

        return new Builder();
    }

    public static class Builder {

        private boolean defaultThreadPool;
        private BlockingQueue<Task> tasksQueue;

        public Builder defaultThreadPool() {
            defaultThreadPool = true;
            return this;
        }

        public Builder withEnvVariable(String name, String value) {
            System.setProperty(name, value);
            return this;
        }

        public AdaptiveThreadPool createThreadPool() {
            if (defaultThreadPool) {
                return new AdaptiveThreadPool();
            } else {
                return new AdaptiveThreadPool();
            }
        }
    }
}
