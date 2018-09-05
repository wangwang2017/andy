package com.yuyuehao.andy.netty;

import com.yuyuehao.andy.utils.Const;
import com.yuyuehao.andy.utils.LogUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created by Wang
 * on 2017-11-24
 */


@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private NettyListener listener;
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("\n", CharsetUtil.UTF_8));


    public NettyClientHandler(NettyListener listener){
        this.listener = listener;
    }




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(true);
        listener.onServiceStatusConnectChanged(null,NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyClient.getInstance().setConnectStatus(false);

        listener.onServiceStatusConnectChanged(null,NettyListener.STATUS_CONNECT_CLOSED);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ((ByteBuf)msg != null){
            listener.onMessageResponse(null,(ByteBuf)msg);
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyClient.getInstance().setConnectStatus(false);
        listener.onServiceStatusConnectChanged(null,NettyListener.STATUS_CONNECT_ERROR);
        LogUtils.write(Const.Tag,LogUtils.LEVEL_ERROR,NettyClient.getInstance().getPackageName()+",Exception|"+NettyClient.getInstance().getHost()+
                ":"+NettyClient.getInstance().getPort()+"|{"+cause.getMessage()+"}",true);
        ctx.channel().closeFuture();
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
}
