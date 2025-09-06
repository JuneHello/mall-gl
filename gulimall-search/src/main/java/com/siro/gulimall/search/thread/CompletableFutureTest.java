package com.siro.gulimall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步 - CompletableFuture
 * @author Starsea
 * @date 2022-04-23 16:55
 */
public class CompletableFutureTest {

    public static ExecutorService service = Executors.newFixedThreadPool(10);

    /**
     * 创建启动异步任务
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");

//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, service);

        //计算完成时的回调方法：方法完成后的感知
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).whenComplete((res,exception) -> {
//            //虽然能得到异常信息，但是没法修改返回数据。
//            System.out.println("异步任务成功完成了...结果是：" + res + ";异常是" + exception);
//        }).exceptionally(throwable -> {
//            //可以感知异常，同时返回默认值。
//            return 10;
//        });

        //handle方法完成后的处理
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).handle((res,thr) -> {
//            if (res != null) {
//                return res*2;
//            }
//            if (thr != null) {
//                return 0;
//            }
//            return 1;
//        });

//        Integer integer = future.get();//得到返回值

        /**
         * 线程串行化方法：
         * 1.thenRunAsync：不能获取到上一步的执行结果，无返回值
         * .thenRunAsync(() -> {
         *             System.out.println("任务2启动了...");
         *         }, service);
         * 2.thenAcceptAsync：能接受上一步结果，但没有返回值
         * .thenAcceptAsync(res -> {
         *             System.out.println("任务2启动了..." + res);
         *         }, service);
         * 3.thenApplyAsync：能接受上一步结果，有返回值
         * .thenApplyAsync(res -> {
         *             System.out.println("任务2启动了..." + res);
         *             return "hello" + res;
         *         }, service);
         */
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).thenApplyAsync(res -> {
//            System.out.println("任务2启动了..." + res);
//            return "hello" + res;
//        }, service);

        //两任务组合 - 都要完成
//        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("任务1结束：" + i);
//            return i;
//        }, service);
//        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程：" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务2结束...");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello";
//        }, service);
        // 不能感知到前两个结果，无返回值
//        future1.runAfterBothAsync(future2,() -> {
//            System.out.println("任务3开始...");
//        },service);
        //能感知到前两个结果，无返回值
//        future1.thenAcceptBothAsync(future2,(f1,f2) -> {
//            System.out.println("任务3开始..." + f1 + "->" + f2);
//        },service);
        //能感知到前两个结果，有返回值
//        CompletableFuture<String> future = future1.thenCombineAsync(future2, (f1, f2) -> {
//            return f1 + ": " + f2 + "-> haha";
//        }, service);
//        System.out.println("future = " + future.get());

        //两任务组合 - 一个完成
        //不感知结果，自己无返回值
//        future1.runAfterEitherAsync(future2,() -> {
//            System.out.println("任务3开始...");
//        }, service);
        //感知结果，自己无返回值
//        future1.acceptEitherAsync(future2,(res) -> {
//            System.out.println("任务3开始..." + res);
//        },service);
        //感知结果，自己有返回值
//        CompletableFuture<String> future = future1.applyToEitherAsync(future2, (res) -> {
//            return res.toString() + " -> haha";
//        }, service);
//        System.out.println("future = " + future.get());

        //多任务组合
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息");
            return "hello.jpg";
        },service);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("查询商品的属性");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "黑色256g";
        },service);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        },service);
        //等待所有任务完成
        CompletableFuture<Void> future = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        future.get();
        System.out.println(futureImg.get() + "->" + futureAttr.get() + "->" + futureDesc.get());
        //只有一个任务完成
//        CompletableFuture<Object> future = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
//        future.get();
//        System.out.println(future.get());

        System.out.println("main...end...");
    }
}
