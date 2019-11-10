/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adaptive.threadpool.config;

/**
 *
 * @author spykee
 */
public final class AdaptiveThreadPoolConfig {

    public final static int AGGREGATION_LIMIT = System.getProperty("thread.pool.mutation.aggregationSize") == null ? Runtime.getRuntime().availableProcessors() / 2
            : Integer.parseInt(System.getProperty("thread.pool.mutation.aggregationSize"));
    public final static String MUTATOR_NAME = System.getProperty("thread.pool.mutation.name") == null
            ? "euclidean"
            : System.getProperty("thread.pool.mutation.name");

    public static final int THREAD_POOL_MUTATOR_ACTIVATION_SIZE = System.getProperty("thread.pool.mutation.backPressureEventsLimit") == null ? Runtime.getRuntime().availableProcessors() * 2
            : Integer.parseInt(System.getProperty("thread.pool.mutation.backPressureEventsLimit"));

    public static final int DEFAULT_THREAD_POOL_MUTATOR_VALUE = System.getProperty("thread.pool.mutator.value") == null ? 1
            : Integer.parseInt(System.getProperty("thread.pool.mutator.value"));
}
