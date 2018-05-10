package com.yuyuehao.andy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Wang
 * on 2017-11-24
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private NettyListener listener;



    public NettyClientHandler(NettyListener listener){
        this.listener = listener;
    }




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(true);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(false);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);
        NettyClient.getInstance().reconnect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg != null){
            listener.onMessageResponse(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        NettyClient.getInstance().disconnect();
        cause.printStackTrace();
    }
}
