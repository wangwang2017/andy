package com.yuyuehao.andy.netty;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by Wang
 * on 2018-08-15
 */
@ChannelHandler.Sharable
public  class Watchdog extends ChannelInboundHandlerAdapter implements TimerTask {

    private Timer timer;
    private boolean reconnect;
    private InetSocketAddress mInetSocketAddress;
    private AbstractChannelPoolMap<InetSocketAddress,SimpleChannelPool> poolMap;

    public Watchdog(Timer timer,boolean reconnect,AbstractChannelPoolMap<InetSocketAddress,SimpleChannelPool> poolMap ){
        this.timer = timer;
        this.reconnect = reconnect;
        this.poolMap = poolMap;
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
            timer.newTimeout(this,5, TimeUnit.SECONDS);
        }
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (mInetSocketAddress != null){
            if (poolMap.contains(mInetSocketAddress)){
                Log.d("1","old size:"+poolMap.size());
                poolMap.remove(mInetSocketAddress);
                Log.d("1","new size:"+poolMap.size());
            }
            final SimpleChannelPool pool = poolMap.get(mInetSocketAddress);
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
