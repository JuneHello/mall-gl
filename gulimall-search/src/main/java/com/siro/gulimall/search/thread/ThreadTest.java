package com.siro.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * 多线程
 * @author Starsea
 * @date 2022-04-23 15:30
 */
public class ThreadTest {

    public static ExecutorService service = Executors.newFixedThreadPool(10);

    /**
     * 1. 继承Thread
     * 2. 实现Runnable接口
     * 3. 实现Callable接口 + FutureTask（可以拿到返回结果。可以处理异常）
     * 4. 线程池
     *      给线程池直接提交任务。
     *      1、创建
     *          1）Executors
     *          2）原生创建线程池
     *
     * 区别：
     *      1、2不能得到返回值，3可以得到返回值
     *      1、2、3不能控制资源
     *      4可以控制资源
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");

        //继承Thread
//        Thread01 thread01 = new Thread01();
//        thread01.start();

        //实现Runnable接口
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();

        //实现Callable接口 + FutureTask
//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
//        Integer integer = futureTask.get();//阻塞等待整个线程执行完成，获取返回结果

        //线程池
        //我们以后业务代码里面，以上三种启动线程的方式都不用。【将所有的多线程异步任务都交给线程池执行】
        //当前系统中池只有一两个，每个异步任务，提交给线程池让他自己去执行就行
//        service.execute(new Runnable01());

        //使用原生创建线程池方式
        /**
         * 七大参数说明：
         * int corePoolSize：核心线程数[一直存在 除非设置allowCoreThreadTimeOut]；线程池创建好以后就准备就绪的线程数量，就等待来接受异步任务去执行。
         * int maximumPoolSize：最大线程数量；控制资源并发
         * long keepAliveTime：存活时间；如果当前的线程数量大于核心数量。
         *          释放空闲的线程（keepAliveTime-corePoolSize）。只要线程空闲大于指定的keepAliveTime。
         * TimeUnit unit：时间单位；
         * BlockingQueue<Runnable> workQueue：阻塞队列；如果任务有很多，就会将目前多的任务放在队列里面。只要有线程空闲，就会去队列里面取出新的任务继续执行。
         * ThreadFactory threadFactory：线程的创建工厂；
         * RejectedExecutionHandler handler：如果队列满了，按照我们指定的拒绝策略拒绝执行任务。
         *
         * 工作顺序：
         *  1、线程池创建，准备好 core 数量的核心线程，准备接受任务
         *  2、新的任务进来，用 core 准备好的空闲线程执行。
         *      (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队 列获取任务执行
         *      (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
         *      (3) 、max 都执行好了。Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自 动销毁。最终保持到 core 大小
         *      (4) 、如果线程数开到了 max 的数量，还有新任务进来，就会使用 reject 指定的拒绝策 略进行处理
         *  3、所有的线程创建都是由指定的 factory 创建的。
         *
         *      new LinkedBlockingDeque<>()：默认是Integer的最大值，可能出现内存不够。
         *
         *  面试： 一个线程池 core 7； max 20 ，queue：50，100 并发进来怎么分配的；
         *  先有 7 个能直接得到执行，接下来 50 个进入队列排队，在多开 13 个继续执行。现在 70 个 被安排上了。剩下 30 个默认拒绝策略。
         *  如果不想抛弃还要执行，可以使用 CallerRunsPolicy 同步方式执行。
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        // 使用Executors创建线程池
//        Executors.newCachedThreadPool(); core是0，所有都可回收。
//        Executors.newFixedThreadPool(); 固定大小，core=max; 都不可回收。
//        Executors.newScheduledThreadPool(); 定时任务的线程池。
//        Executors.newSingleThreadExecutor(); 单线程的线程池。

        System.out.println("main...end...");
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 /2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 /2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 /2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

}
