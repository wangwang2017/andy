package com.yuyuehao.andy.netty;

import android.util.Log;

import java.net.InetSocketAddress;
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

public class ConnectThread implements TimerTask {


    private Timer mTimer;
    private String message;
    private InetSocketAddress address;

    public ConnectThread(InetSocketAddress address, Timer mTimer, String message){
        this.mTimer = mTimer;
        this.message = message;
        this.address = address;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        final SimpleChannelPool pool =  NettyClientPool.getInstance().getPool(address);
        Future<Channel> f = pool.acquire();
        Log.d("1","-36");
        f.addListener(new FutureListener<Channel>(){
            @Override
            public void operationComplete(Future<Channel> channelFuture) throws Exception {
                Log.d("1","-40");
                if (channelFuture.isSuccess()) {
                    Channel ch = channelFuture.getNow();
                    StringBuffer sb = new StringBuffer();
                    sb.append(message);
                    sb.append("\n");
                    ch.writeAndFlush(sb.toString());
                    Log.d("1","连接成功");
                    pool.release(ch);
                }else{
                    Random rd = new Random();
                    int seconds = rd.nextInt(18)+3;
                    mTimer.newTimeout(ConnectThread.this,seconds, TimeUnit.SECONDS);
                    Log.d("1","连接失败:"+seconds+"秒");
                }
            }
        });
    }








}
