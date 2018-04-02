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
            //        pipeline.addLast(new LoggingHandler(LogLevel.INFO));//开启日志，可以设置日志等级
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(30 * 1024, Delimiters.lineDelimiter()));
            pipeline.addLast(new IdleStateHandler(30, 40, 60, TimeUnit.SECONDS));

            //pipeline.addLast(new HeartbeatServerHandler());
            pipeline.addLast(new NettyClientHandler(listener));
            //        pipeline.addLast(new ProtobufVarint32FrameDecoder());// 解码(处理半包)
            //        pipeline.addLast(new LineBasedFrameDecoder(2048));
            //        pipeline.addLast(new FixedLengthFrameDecoder(1024*5));
            //        pipeline.addLast(new DelimiterBasedFrameDecoder(1024*5, Delimiters.lineDelimiter()));
            //        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());//加长度
            pipeline.addLast(new StringDecoder(Charset.forName(NettyClient.getInstance().getCharSet())));
            pipeline.addLast(new StringEncoder(Charset.forName(NettyClient.getInstance().getCharSet())));

    }



}
