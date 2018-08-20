package com.yuyuehao.andy.netty;

import android.util.Log;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by Wang
 * on 2018-08-15
 */
@ChannelHandler.Sharable
public class BaseChannelPoolHandler implements ChannelPoolHandler {




    private NettyListener mNettyListener;

    public BaseChannelPoolHandler(NettyListener mNettyListener){

        this.mNettyListener = mNettyListener;
    }

    /**
     * 因为是裸的channel，所以需要给他配置上编解码器
     * 只需要配置一次就可以，因为channel会被还回到池里
     */
    @Override
    public void channelCreated(Channel channel) throws Exception {
        SocketChannel ch= (SocketChannel)channel;
        ch.config().setKeepAlive(true);
        ch.config().setTcpNoDelay(true);
        channel.pipeline()
                .addLast(new NettyPoolClientInitializer(mNettyListener));
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        Log.d("1",ch.id()+ " released");
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        Log.d("1",ch.id()+ " acquired");
    }



}
