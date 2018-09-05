package com.yuyuehao.andy.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;

/**
 * Created by Wang
 * on 2018-08-30
 */

public class MyChannelPoolMap extends AbstractChannelPoolMap<String,SimpleChannelPool> {

    private Bootstrap mBootstrap;
    private NettyListener mNettyListener;
    private int thread_num;


    public MyChannelPoolMap(Bootstrap mBootstrap,NettyListener mNettyListener,int thread_num){
        this.mBootstrap = mBootstrap;
        this.mNettyListener = mNettyListener;
        this.thread_num = thread_num;
    }






    @Override
    protected SimpleChannelPool newPool(String string) {
        InetSocketAddress socketAddress = null;
        if (string.contains(":")){
            String[] info = string.split(":");
            socketAddress =  InetSocketAddress.createUnresolved(info[0],Integer.valueOf(info[1]));
        }
        return new FixedChannelPool(mBootstrap.remoteAddress(socketAddress), new BaseChannelPoolHandler(mNettyListener), ChannelHealthChecker.ACTIVE,FixedChannelPool.AcquireTimeoutAction.FAIL,3000,thread_num,thread_num);
    }
}
