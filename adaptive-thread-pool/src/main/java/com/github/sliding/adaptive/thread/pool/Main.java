
package com.github.sliding.adaptive.thread.pool;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author george-toma
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        AdaptiveThreadPool adaptiveThreadPool = new AdaptiveThreadPool();
        // adaptiveThreadPool.shutdown();


        ///####################################

        for (int i = 0; i < 1_000; i++)
            Executors.newCachedThreadPool().execute(new Task() {
                @Override
                public void run() {
                    System.out.println("Hello Task Static!! " + LocalDateTime.now());
                }
            });
            TimeUnit.MINUTES.sleep(1);

        for (int i = 0; i < 1_000; i++)
            adaptiveThreadPool.execute(new Task() {
                @Override
                public void run() {
                    System.out.println("Hello Task Adaptive!! "+ LocalDateTime.now());
                }
            });



    }
}
