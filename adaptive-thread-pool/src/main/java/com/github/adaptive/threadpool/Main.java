package com.github.adaptive.threadpool;

import com.github.adaptive.threadpool.task.Task;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author george-toma
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        AdaptiveThreadPool adaptiveThreadPool = new AdaptiveThreadPool();
        // adaptiveThreadPool.shutdown();


        ///####################################

//        for (int i = 0; i < 1_000; i++)
//            Executors.newCachedThreadPool().execute(new Task() {
//                @Override
//                public void run() {
//                    System.out.println("Hello Task Static!! " + LocalDateTime.now());
//                }
//            });
//            TimeUnit.MINUTES.sleep(1);

        for (int i = 0; i < 1000; i++) {
            adaptiveThreadPool.execute(new Task() {
                @Override
                public void run() {
                    System.out.println("Hello Task Adaptive!! " + LocalDateTime.now());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                    Math.sqrt(new Random().nextDouble());
                }
            });
            // TimeUnit.SECONDS.sleep(2);
        }
        TimeUnit.DAYS.sleep(1);

    }
}
