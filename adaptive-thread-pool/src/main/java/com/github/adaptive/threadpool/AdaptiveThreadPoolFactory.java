package com.github.adaptive.threadpool;

import java.util.concurrent.LinkedBlockingQueue;

public final class AdaptiveThreadPoolFactory {

    public Builder describedAs() {

        return new Builder();
    }

    public static class Builder {

        private boolean defaultThreadPool;

        public Builder defaultThreadPool() {
            defaultThreadPool = true;
            return this;
        }

        public Builder withSystemVariable(String name, String value) {
            System.setProperty(name, value);
            return this;
        }

        public AdaptiveThreadPool createUnboundedThreadPool(){
            return new AdaptiveThreadPool(new LinkedBlockingQueue<>());
        }
        public AdaptiveThreadPool createSyncThreadPool() {
            return new AdaptiveThreadPool();
        }
    }
}
