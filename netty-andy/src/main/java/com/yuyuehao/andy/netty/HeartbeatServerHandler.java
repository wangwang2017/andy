package com.yuyuehao.andy.netty;

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * Created by Wang
 * on 2017-12-01
 */

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
            .unreleasableBuffer(Unpooled.copiedBuffer("heart beat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {  // 2
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = "";
            if (event.state() == IdleState.READER_IDLE) {
                type = "read idle";
            } else if (event.state() == IdleState.WRITER_IDLE) {
                type = "write idle";
            } else if (event.state() == IdleState.ALL_IDLE) {
                type = "all idle";
            }
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(
                    ChannelFutureListener.CLOSE_ON_FAILURE);  // 3
            Log.i("HeartbeatServerHandler" ,ctx.channel().remoteAddress()+"timeout：" + type);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
