package com.yuyuehao.andy.netty;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * Created by Wang
 * on 2018-08-15
 */

public class NettyClientPool {

    private static final EventLoopGroup mEventLoopGroup = new NioEventLoopGroup();
    private static final Bootstrap mBootstrap = new Bootstrap();
    private static final int Thread_Num = Runtime.getRuntime().availableProcessors();
    private static NettyClientPool mNettyClientPool = null;
    private NettyListener mNettyListener;
    private NettyPoolCallback mNettyPoolCallback;
    private CallBack mCallBack;
    public final HashedWheelTimer mTimer = new HashedWheelTimer();

    public AbstractChannelPoolMap<String,FixedChannelPool> poolMap;

    public static synchronized NettyClientPool getInstance(){
        if (mNettyClientPool == null){
            mNettyClientPool = new NettyClientPool();
        }
        return mNettyClientPool;
    }


    public NettyClientPool(){
        mBootstrap.group(mEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,true);

        poolMap = new AbstractChannelPoolMap<String,FixedChannelPool>(){

            @Override
            protected FixedChannelPool newPool(String s) {
                InetSocketAddress socketAddress = null;
                if (s.contains(":")){
                    String[] info = s.split(":");
                    socketAddress =  InetSocketAddress.createUnresolved(info[0],Integer.valueOf(info[1]));
                }
                return new FixedChannelPool(mBootstrap.remoteAddress(socketAddress),new BaseChannelPoolHandler(mNettyListener,mNettyPoolCallback),Thread_Num);
            }
        };

    }

    public void setListener(NettyListener listener){
        this.mNettyListener = listener;
    }

    public void setNettyPoolCallback(NettyPoolCallback callback){
        this.mNettyPoolCallback = callback;
    }

    public void setCallBack(CallBack callback){
        this.mCallBack = callback;
    }

    public void closeAll(){
        if (poolMap != null){
            if (poolMap.size() != 0){
                poolMap.close();
            }
        }
        if (mEventLoopGroup != null){
            mEventLoopGroup.shutdownGracefully();
        }
    }

    public void connect(final String address){
        final FixedChannelPool pool = poolMap.get(address);
        synchronized (pool) {
            final Future<Channel> future = pool.acquire();
            // 获取到实例后发消息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Channel channel = null;
                    try {
                         channel = future.get(15, TimeUnit.SECONDS);
                         pool.release(channel);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }finally {
                        if (channel == null){
                            Random rd = new Random();
                            int seconds = rd.nextInt(11) + 15;
                            Log.d("NettyClientPool","finally address:"+address +",seconds:"+seconds);
                            mTimer.newTimeout(new ConnectThread(address,mTimer,3,mCallBack),seconds,TimeUnit.SECONDS);
                        }
                    }
                }
            }).start();
        }
    }

    public void sendMessage(final String address, final String message){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final FixedChannelPool pool = poolMap.get(address);
        synchronized (pool) {
            Future<Channel> future = pool.acquire();
            // 获取到实例后发消息
            future.addListener(new FutureListener<Channel>() {
                @Override
                public void operationComplete(Future<Channel> channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        Channel ch =  channelFuture.getNow();
                        StringBuilder sb = new StringBuilder();
                        sb.append(message);
                        sb.append("\n");
                        ch.writeAndFlush(sb.toString());
                        pool.release(ch);
                    }else{
                        Random rd = new Random();
                        int seconds = rd.nextInt(11) + 15;
                        Log.d("NettyClientPool","finally address:"+address +",seconds:"+seconds);
                        mTimer.newTimeout(new ConnectThread(address,mTimer,Integer.MAX_VALUE,mCallBack),seconds,TimeUnit.SECONDS);
                    }
                }
            });
        }
    }
}
