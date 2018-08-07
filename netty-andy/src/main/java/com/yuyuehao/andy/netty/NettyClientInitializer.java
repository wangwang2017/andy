package com.yuyuehao.andy.netty;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by Wang
 * on 2017-11-24
 */

@ChannelHandler.Sharable
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyListener listener;


    public NettyClientInitializer(NettyListener listener) {
        this.listener = listener;
    }


    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPIDLE,60);
        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPINTVL,15);
        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPCNT,4);
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(30 * 1024, Delimiters.lineDelimiter()));
        pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new NettyClientHandler(listener));
        pipeline.addLast(new StringDecoder(Charset.forName(NettyClient.getInstance().getCharSet())));
        pipeline.addLast(new StringEncoder(Charset.forName(NettyClient.getInstance().getCharSet())));
    }




}
