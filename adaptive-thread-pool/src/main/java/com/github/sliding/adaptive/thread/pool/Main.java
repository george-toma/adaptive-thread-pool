
package com.github.sliding.adaptive.thread.pool;

/**
 * @author george-toma
 */
public class Main {
    public static void main(String[] args) {
        AdaptiveThreadPool adaptiveThreadPool = new AdaptiveThreadPool();
        adaptiveThreadPool.execute(new Task() {
            @Override
            public void run() {
                System.out.println("Hello Adaptive!! ");
            }
        });
        adaptiveThreadPool.shutdown();

        AdaptiveThreadPool adaptiveThreadPool2 = new AdaptiveThreadPool();
        adaptiveThreadPool2.execute(new Task() {
            @Override
            public void run() {
                System.out.println("Hello Adaptive 2!! ");
            }
        });
        adaptiveThreadPool2.shutdown();


        adaptiveThreadPool2 = new AdaptiveThreadPool();
        adaptiveThreadPool2.execute(new Task() {
            @Override
            public void run() {
                System.out.println("Hello Adaptive 3!! ");
            }
        });
        adaptiveThreadPool2.shutdown();


        adaptiveThreadPool2 = new AdaptiveThreadPool();
        adaptiveThreadPool2.execute(new Task() {
            @Override
            public void run() {
                System.out.println("Hello Adaptive 4!! ");
            }
        });
        adaptiveThreadPool2.shutdown();


    }
}
