package com.yuyuehao.andy.netty;

import com.yuyuehao.andy.utils.LogUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by Wang
 * on 2018-08-21
 */

public class ConnectThread implements TimerTask{


    private Timer mTimer;
    private String address;
    private int count;
    private CallBack callBack;

    public ConnectThread(String address, Timer mTimer, int count,CallBack callBack){
        this.mTimer = mTimer;
        this.address = address;
        this.count = count;
        this.callBack = callBack;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        final FixedChannelPool pool =  NettyClientPool.getInstance().poolMap.get(address);
        final Future<Channel> f = pool.acquire();
        f.addListener(new FutureListener<Channel>(){
           @Override
           public void operationComplete(Future<Channel> channelFuture) throws Exception {
               if (channelFuture.isSuccess()){
                   LogUtils.write("NettyPoolServer",LogUtils.LEVEL_INFO,address.toString()+" connect successful.",true);
                   pool.release(f.getNow());
                   return;
               }else{
                  if (count >0){
                      Random rd = new Random();
                      int seconds = rd.nextInt(11) + 15;
                      mTimer.newTimeout(ConnectThread.this, seconds, TimeUnit.SECONDS);
                      count--;
                      LogUtils.write("NettyPoolServer", LogUtils.LEVEL_INFO, address.toString() + " connect failed," + seconds + " seconds reconnect,count = "+count, true);
                  }else{
                      LogUtils.write("NettyPoolServer", LogUtils.LEVEL_INFO, address.toString() + " connect failed,circulation end.", true);
                      callBack.onCompletionTimerTask(address);
                  }
               }
           }
        });
    }
}
