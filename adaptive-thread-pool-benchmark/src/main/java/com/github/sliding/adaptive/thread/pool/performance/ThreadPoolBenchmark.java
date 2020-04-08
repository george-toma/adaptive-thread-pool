/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.sliding.adaptive.thread.pool.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class ThreadPoolBenchmark {

    @Param({"10"})
    public int base;

    @Param({"1"})
    public int times;

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    ExecutorService forkJoinPool = Executors.newWorkStealingPool();
    CountDownLatch countDownLatch;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ThreadPoolBenchmark.class.getSimpleName())
                .verbosity(VerboseMode.EXTRA)
                .forks(2)
                .build();

        new Runner(opt).run();

    }

    @Setup(Level.Invocation)
    public void doSetup() {
        countDownLatch = new CountDownLatch(base);
    }

    @TearDown(Level.Trial)
    public void doTearDown() {
        System.out.println("TearDow");
        cachedThreadPool.shutdown();
    }


//    @Benchmark
//    public void measureFixedThreadPool(ThreadPoolState threadPoolState) throws InterruptedException {
//
//        //given
//        ExecutorService threadPool = threadPoolState.fixedThreadPool;
//
//        //when
//        executeInThreadPool(threadPoolState, threadPool);
//        threadPool.shutdown();
//
//        //then
//        threadPoolState.countDownLatch.await();
//    }
//
//
//    @Benchmark
//    public void measureForkJoinThreadPool(ThreadPoolState threadPoolState) throws InterruptedException {
//
//        //given
//        ExecutorService threadPool = threadPoolState.forkJoinPool;
//
//        //when
//        executeInThreadPool(threadPoolState, threadPool);
//
//        //then
//        threadPoolState.countDownLatch.await();
//    }
//
//    @Benchmark
//    public void measureAdaptiveThreadPool() {
//
//
//    }

    @Benchmark
    public void measureCachedThreadPool(Blackhole blackhole) throws InterruptedException {
        //given
        ExecutorService threadPool = cachedThreadPool;

        //when
        executeInThreadPool(threadPool, blackhole);

        //then
        countDownLatch.await();
    }

    private void executeInThreadPool(ExecutorService executorService, Blackhole blackhole) {
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                Random random = new Random();
                BigDecimal obj = new BigDecimal(random.nextInt(10));
                obj = obj.multiply(new BigDecimal(random.nextInt(10)));
                double sin = Math.sin(obj.doubleValue());
                blackhole.consume(sin);
                countDownLatch.countDown();
            });
        }
    }
}
