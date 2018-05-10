package com.yuyuehao.andy.netty;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
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

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyListener listener;

    private int WRITE_WAIT_SECOND = 10;

    private int READ_WAIT_SECOND = 13;



    public NettyClientInitializer(NettyListener listener) {
        this.listener = listener;
    }


    @Override
    protected void initChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();
            //        SslContext sslCtx = SslContextBuilder.forClient()
            //                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            //        pipeline.addLast(sslCtx.newHandler(ch.alloc()));//开启SSL
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(30 * 1024, Delimiters.lineDelimiter()));
            pipeline.addLast(new IdleStateHandler(10, 20, 30, TimeUnit.SECONDS));
            pipeline.addLast(new NettyClientHandler(listener));
            pipeline.addLast(new StringDecoder(Charset.forName(NettyClient.getInstance().getCharSet())));
            pipeline.addLast(new StringEncoder(Charset.forName(NettyClient.getInstance().getCharSet())));

    }



}
