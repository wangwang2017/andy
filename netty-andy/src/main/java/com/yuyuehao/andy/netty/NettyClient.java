package com.yuyuehao.andy.netty;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by Wang
 * on 2019-01-15
 */

public class NettyClient {

    private String address;

    private static NettyClient mNettyClient = null;

    private static final EventLoopGroup group = new NioEventLoopGroup();

    private Bootstrap mBootstrap = new Bootstrap();

    private Map<String,SocketChannel> mChannelHashMap = new HashMap<>();

    private NettyListener mNettyListener;

    public NettyClient(){
        mBootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPIDLE,60);
                        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPINTVL,15);
                        pipeline.channel().config().setOption(EpollChannelOption.TCP_KEEPCNT,4);
                        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(30 * 1024, Delimiters.lineDelimiter()));
                        pipeline.addLast(new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new NettyClientHandler(mNettyListener));
                        pipeline.addLast(new StringEncoder(Charset.forName("utf-8")));
                        pipeline.addLast(new StringDecoder(Charset.forName("utf-8")));
                    }
                });
    }


    public void setNettyListener(NettyListener nettyListener) {
        mNettyListener = nettyListener;
    }

    public static synchronized NettyClient getInstance(){
        if (mNettyClient == null){
            mNettyClient = new NettyClient();
        }
        return mNettyClient;
    }

    public void connect(final String address) {

        if (!address.contains(":")){
            return;
        }
        if (!TextUtils.isDigitsOnly(address.split(":")[1])) {
            return;
        }
        String host = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);
        try {
            ChannelFuture f = mBootstrap.connect(host,port).sync();
            if (f.isSuccess()){
                SocketChannel socketChannel = (SocketChannel) f.channel();
                mChannelHashMap.put(address,socketChannel);
            }
            f.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void sendMessage(String address, final String message){
        if (!mChannelHashMap.containsKey(address)){
            return;
        }
        SocketChannel mChannel = mChannelHashMap.get(address);
        if (!mChannel.isActive()){
            mChannelHashMap.remove(address);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append("\n");
        mChannel.writeAndFlush(Unpooled.copiedBuffer(sb.toString().getBytes(CharsetUtil.UTF_8)),);
    }

    public void closeChannel(String address){
        if (!mChannelHashMap.containsKey(address)){
            return;
        }
        Channel mChannel = mChannelHashMap.get(address);
        mChannel.closeFuture();
        mChannelHashMap.remove(address);

    }

}
