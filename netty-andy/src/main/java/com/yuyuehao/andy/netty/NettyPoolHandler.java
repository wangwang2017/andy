package com.yuyuehao.andy.netty;

import android.util.Log;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created by Wang
 * on 2018-08-15
 */
@ChannelHandler.Sharable
public class NettyPoolHandler extends SimpleChannelInboundHandler<ByteBuf>{

    private NettyListener nettyListener;
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("\n", CharsetUtil.UTF_8));
    public NettyPoolHandler(NettyListener nettyListener) {
        this.nettyListener = nettyListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        Log.d("1",address+" is active");
        nettyListener.onServiceStatusConnectChanged(address,NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        Log.d("1",address+" is inactive");
        nettyListener.onServiceStatusConnectChanged(address,NettyListener.STATUS_CONNECT_CLOSED);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(
                        ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        Log.d("1",address+" is exception");
        nettyListener.onServiceStatusConnectChanged(address,NettyListener.STATUS_CONNECT_ERROR);
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf o) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        nettyListener.onMessageResponse(address,o);
    }
}
