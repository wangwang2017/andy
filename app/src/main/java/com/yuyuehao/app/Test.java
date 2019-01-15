package com.yuyuehao.app;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Wang
 * on 2019-01-14
 */

public class Test implements Runnable{


    static final CountDownLatch countDownLatch = new CountDownLatch(10);
    static final Test t = new Test();
    @Override
    public void run()
    {
        try
        {
            Thread.sleep(2000);
            System.out.println("complete");
            countDownLatch.countDown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++)
        {
            executorService.execute(t);
        }
        countDownLatch.await();
        System.out.println("end");
        executorService.shutdown();
    }

}
