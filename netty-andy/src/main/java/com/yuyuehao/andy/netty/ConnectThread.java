package com.yuyuehao.andy.netty;

import com.yuyuehao.andy.utils.LogUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
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
    private String message;
    private String address;
    private int count;
    private CallBack callBack;

    public ConnectThread(String address, Timer mTimer, String message, int count,CallBack callBack){
        this.mTimer = mTimer;
        this.message = message;
        this.address = address;
        this.count = count;
        this.callBack = callBack;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        final SimpleChannelPool pool =  NettyClientPool.getInstance().getPool(address);
        Future<Channel> f = pool.acquire();
        f.addListener(new FutureListener<Channel>(){
            @Override
            public void operationComplete(Future<Channel> channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Channel ch = channelFuture.getNow();
                    StringBuffer sb = new StringBuffer();
                    sb.append(message);
                    sb.append("\n");
                    ch.writeAndFlush(sb.toString());
                    LogUtils.write("NettyPoolServer",LogUtils.LEVEL_INFO,address.toString()+" connect successful.",true);
                    pool.release(ch);
                }else{
                    Random rd = new Random();
                    int seconds = rd.nextInt(11) + 10;
                    if (count == 0){
                        callBack.onCompletionTimerTask(address);
                        LogUtils.write("NettyPoolServer", LogUtils.LEVEL_INFO, address.toString() + " connect failed,circulation end.", true);
                    }
                    if (count >0 && count <= 2) {
                        count--;
                        mTimer.newTimeout(ConnectThread.this, seconds, TimeUnit.SECONDS);
                        LogUtils.write("NettyPoolServer", LogUtils.LEVEL_INFO, address.toString() + " connect failed," + seconds + " seconds reconnect.", true);
                    }

                    if (count>6){
                        mTimer.newTimeout(ConnectThread.this, seconds, TimeUnit.SECONDS);
                        LogUtils.write("NettyPoolServer", LogUtils.LEVEL_INFO, address.toString() + " connect failed," + seconds + " seconds reconnect.", true);
                    }

                }
            }
        });
    }








}
