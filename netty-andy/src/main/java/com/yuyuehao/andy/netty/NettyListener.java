package com.yuyuehao.andy.netty;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Wang
 * on 2017-11-24
 */

public interface NettyListener {

    public final static byte STATUS_CONNECT_SUCCESS = 1;

    public final static byte STATUS_CONNECT_CLOSED = 0;

    public final static byte STATUS_CONNECT_ERROR = -1;

    /**
     * 对消息的处理
     * @param data
     */
    void onMessageResponse(ChannelHandlerContext channelHandlerContext, ByteBuf data);



    /**
     * 当服务状态发生变化时触发
     */
    public void onServiceStatusConnectChanged(InetSocketAddress inetSocketAddress, int statusCode);
}
