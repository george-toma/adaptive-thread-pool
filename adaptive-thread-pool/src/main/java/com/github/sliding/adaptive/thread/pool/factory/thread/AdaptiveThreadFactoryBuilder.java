package com.github.sliding.adaptive.thread.pool.factory.thread;


public final class AdaptiveThreadFactoryBuilder {

    private static final String THREAD_NAME_PREFIX = "adaptive-worker-thread-";
    private String namePrefix = THREAD_NAME_PREFIX;
    private boolean isDaemon = false;
    private int priority = Thread.NORM_PRIORITY;
    private String threadPoolIdentifier;

    private AdaptiveThreadFactoryBuilder() {
    }

    public static AdaptiveThreadFactoryBuilder builder() {
        return new AdaptiveThreadFactoryBuilder();
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public boolean isDaemon() {
        return isDaemon;
    }

    public int getPriority() {
        return priority;
    }

    public String getThreadPoolIdentifier() {
        return threadPoolIdentifier;
    }

    public AdaptiveThreadFactoryBuilder withNamePrefix(String namePrefix) {
        if (namePrefix == null) {
            namePrefix = THREAD_NAME_PREFIX;
        }
        this.namePrefix = namePrefix;
        return this;
    }

    public AdaptiveThreadFactoryBuilder withDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }


    public AdaptiveThreadFactoryBuilder withThreadPoolIdentifier(String threadPoolIdentifier) {
        this.threadPoolIdentifier = threadPoolIdentifier;
        return this;
    }

    public AdaptiveThreadFactoryBuilder withPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format(
                    "Thread priority (%s) must be <= %s", priority,
                    Thread.MAX_PRIORITY));
        }

        this.priority = priority;
        return this;
    }

    public AdaptiveThreadFactory build() {
        return new AdaptiveThreadFactory(this);
    }


}
