package com.github.sliding.adaptive.thread.pool.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.*;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@Threads(4)
public class BlockingQueueBenchmark {

    //to have different states dependending on case
    @Param("1000")
    int iterations;
    //Sync vs array vs linked queue


    BlockingQueue<String> arrayQueue;
    BlockingQueue<String> linkedQueue;
    BlockingQueue<String> syncQueue;


    @Setup(Level.Iteration)
    public void doSetup() {
        arrayQueue = new ArrayBlockingQueue<>(iterations);
        linkedQueue = new LinkedBlockingDeque<>();
        syncQueue = new SynchronousQueue<>();
    }

    @TearDown(Level.Iteration)
    public void doTearDown() {
        arrayQueue.clear();
        linkedQueue.clear();
        syncQueue.clear();
    }

    @Benchmark
    public void measureArrayBlockingQueue_PutMethod_1(Blackhole blackhole) throws InterruptedException {
        for (int i = 0; i < iterations; i++) {
            arrayQueue.put(String.valueOf(i));
        }
    }

    @Benchmark
    public void measureArrayBlockingQueue_TakeMethod_1() throws InterruptedException {
        arrayQueue.take();
    }

    @Benchmark
    public void measureArrayBlockingQueue_IterationMethod_1() {

    }

}
