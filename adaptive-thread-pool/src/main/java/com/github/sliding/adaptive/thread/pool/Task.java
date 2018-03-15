package com.github.sliding.adaptive.thread.pool;

import java.util.UUID;

public abstract class Task implements Runnable {
    private final String identifier = UUID.randomUUID().toString();

    public String identifier() {
        return identifier;
    }
}
