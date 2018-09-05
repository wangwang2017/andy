package com.yuyuehao.andy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Wang
 * on 2018-08-15
 */

public class NettyClientPool {

    final EventLoopGroup mEventLoopGroup = new NioEventLoopGroup();
    final Bootstrap mBootstrap = new Bootstrap();
    private static final int Thread_Num = Runtime.getRuntime().availableProcessors();
    private static NettyClientPool mNettyClientPool = null;
    private NettyListener mNettyListener;

    public MyChannelPoolMap poolMap;

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

        poolMap = new MyChannelPoolMap(mBootstrap,mNettyListener,Thread_Num);

    }

    public void setListener(NettyListener listener){
        this.mNettyListener = listener;
    }

    public SimpleChannelPool getPool(String remoteInfo){
        return mNettyClientPool.poolMap.get(remoteInfo);
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

}
