package com.yuyuehao.andy.netty;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Wang
 * on 2019-01-15
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private NettyListener mNettyListener;

    public NettyClientHandler(NettyListener mNettyListener){
        this.mNettyListener = mNettyListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        mNettyListener.onMessageResponse(address,byteBuf);
    }
}
