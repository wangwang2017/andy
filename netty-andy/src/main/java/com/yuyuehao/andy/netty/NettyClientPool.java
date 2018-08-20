package com.yuyuehao.andy.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;

/**
 * Created by Wang
 * on 2018-08-15
 */

public class NettyClientPool {

    final EventLoopGroup mEventLoopGroup = new NioEventLoopGroup();
    final Bootstrap mBootstrap = new Bootstrap();
    protected final HashedWheelTimer timer = new HashedWheelTimer();
    private static final int Thread_Num = Runtime.getRuntime().availableProcessors();
    private static NettyClientPool mNettyClientPool = null;
    private NettyListener mNettyListener;

    public AbstractChannelPoolMap<InetSocketAddress,SimpleChannelPool> poolMap;

    public static synchronized NettyClientPool getInstance(){
        if (mNettyClientPool == null){
            mNettyClientPool = new NettyClientPool();
        }
        return mNettyClientPool;
    }

    public void build() throws Exception{
        mBootstrap.group(mEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress socketAddress) {
                return new FixedChannelPool(mBootstrap.remoteAddress(socketAddress),new BaseChannelPoolHandler(mNettyListener),Thread_Num);
            }
        };
    }

    public void setListener(NettyListener listener){
        this.mNettyListener = listener;
    }

    public SimpleChannelPool getPool(InetSocketAddress inetSocketAddress){
        return mNettyClientPool.poolMap.get(inetSocketAddress);
    }

}
