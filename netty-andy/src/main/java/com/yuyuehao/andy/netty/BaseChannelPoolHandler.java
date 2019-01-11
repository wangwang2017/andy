package com.yuyuehao.andy.netty;

import android.util.Log;

import com.yuyuehao.andy.utils.LogUtils;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by Wang
 * on 2018-08-15
 */



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

        Log.d("NettyPoolServer", "channelCreated" + "|" + channel.id());
        SocketChannel ch = (SocketChannel) channel;
        ch.config().setKeepAlive(true);
        ch.config().setTcpNoDelay(true);
        channel.pipeline()
                .addLast(new NettyPoolClientInitializer(mNettyListener));
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        LogUtils.write("NettyPoolServer",LogUtils.LEVEL_INFO,ch.id()+":"+ch.remoteAddress().toString()+ " released",true);
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        LogUtils.write("NettyPoolServer",LogUtils.LEVEL_INFO,ch.id()+":"+ch.remoteAddress().toString()+ " acquired",true);
    }

}
