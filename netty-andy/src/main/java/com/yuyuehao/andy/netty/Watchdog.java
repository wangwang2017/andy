package com.yuyuehao.andy.netty;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by Wang
 * on 2018-08-15
 */
@ChannelHandler.Sharable
public  class Watchdog extends ChannelInboundHandlerAdapter implements TimerTask {

    private final HashedWheelTimer timer = new HashedWheelTimer();
    private boolean reconnect;
    private InetSocketAddress mInetSocketAddress;

    public Watchdog(boolean reconnect){
        this.reconnect = reconnect;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }




    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        mInetSocketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        startTimeOut();

    }


    private void startTimeOut(){
        if (reconnect){
            Random rd = new Random();
            int seconds = rd.nextInt(18)+3;
            timer.newTimeout(this,seconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (mInetSocketAddress != null){
            final SimpleChannelPool pool = NettyClientPool.getInstance().getPool(mInetSocketAddress);
            Future<Channel> f =  pool.acquire();
            f.addListener(new FutureListener<Channel>(){
                @Override
                public void operationComplete(Future<Channel> channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Channel ch = channelFuture.getNow();
                        Log.d("1","连接成功");
                        pool.release(ch);
                    }else{
                        pool.close();
                        startTimeOut();
                        Log.d("1","连接失败");
                    }
                }
            });
        }
    }
}
