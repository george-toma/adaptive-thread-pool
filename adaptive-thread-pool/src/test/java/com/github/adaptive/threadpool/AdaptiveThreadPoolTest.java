package com.github.adaptive.threadpool;

public class AdaptiveThreadPoolTest {
//    private static AdaptiveThreadPool adaptiveThreadPool;
//
//    @BeforeEach
//    void setup() {
//        adaptiveThreadPool = new AdaptiveThreadPool();
//    }
//
//    @AfterAll
//    static void tearDown() {
//        adaptiveThreadPool.shutdown();
//    }
//
//    @Test
//    public void test_taskExecuted_Ok() throws InterruptedException {
//        //given
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        //when
//        adaptiveThreadPool.execute(new Task() {
//            @Override
//            public void run() {
//                System.out.println("Task executed in adaptive thread pool");
//                countDownLatch.countDown();
//            }
//        });
//
//        //then
//        Assertions.assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
//    }
//
//    @Test
//    public void test_threadPool_shutdown() {
//        //when
//        adaptiveThreadPool.shutdown();
//
//        //then
//        Assertions.assertTrue(adaptiveThreadPool.isShutdown());
//        Assertions.assertThrows(ShutdownThreadPoolException.class, () -> {
//            adaptiveThreadPool.execute(new Task() {
//                @Override
//                public void run() {
//                    System.out.println("I will fail");
//                }
//            });
//        });
//
//    }
//
//    @Test
//    public void test_threadPool_shutdownNow() {
//        //when
//        List<Task> remainingTasks = adaptiveThreadPool.shutdownNow();
//
//        //then
//        Assertions.assertTrue(adaptiveThreadPool.isShutdown());
//        Assertions.assertTrue(remainingTasks.isEmpty());
//        Assertions.assertThrows(ShutdownThreadPoolException.class, () -> {
//            adaptiveThreadPool.execute(new Task() {
//                @Override
//                public void run() {
//                    System.out.println("I will fail");
//                }
//            });
//        });
//    }
//
//    @Test()
//    public void test_threadPool_shutdownNow_withRemainingTasks() {
//        //asta e mai greu de testat caci nu se poate bloca numarul de thread-uri
//        // dar ar trebui sa fac un factory care sa foloseasca un dummy mutation , care nu face increase/decrease la thread
//        //pool
//        int cpuSize = Runtime.getRuntime().availableProcessors();
//
//        //given
//        //simulate tasks queue blockage
//        System.setProperty("thread.pool.mutator.value", "0");
//        //init thread pool with new configuration
//        adaptiveThreadPool = new AdaptiveThreadPool();
//        CountDownLatch countDownLatch = new CountDownLatch(cpuSize);
//        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
//            adaptiveThreadPool.execute(new Task() {
//                @Override
//                public void run() {
//                    System.out.println("Task executed");
//                    countDownLatch.countDown();
//                    try {
//                        TimeUnit.SECONDS.sleep(15);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//        adaptiveThreadPool.execute(new Task() {
//            @Override
//            public void run() {
//                System.out.println("Task 2 waiting to be executed");
//            }
//        });
//        //when
//        List<Task> remainingTasks = adaptiveThreadPool.shutdownNow();
//
//        //then
//        Assertions.assertTrue(adaptiveThreadPool.isShutdown());
//        Assertions.assertTrue(remainingTasks.size() == 1);
//        Assertions.assertThrows(ShutdownThreadPoolException.class, () -> {
//            adaptiveThreadPool.execute(new Task() {
//                @Override
//                public void run() {
//                    System.out.println("I will fail");
//                }
//            });
//        });
//    }
}
