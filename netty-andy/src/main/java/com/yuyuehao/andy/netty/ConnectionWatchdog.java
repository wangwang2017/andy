package com.yuyuehao.andy.netty;


import com.yuyuehao.andy.utils.Const;
import com.yuyuehao.andy.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

/**
 * Created by Wang
 * on 2018-06-05
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask,ChannelHandlerHolder{

    private final Bootstrap mBootstrap;
    private final Timer mTimer;
    private final int port;
    private final String host;
    private volatile boolean reconnect = true;



    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect) {
        mBootstrap = bootstrap;
        mTimer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,NettyClient.getInstance().getPackageName()+":连接成功|"+host+":"+port,true);
        ctx.fireChannelActive();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,NettyClient.getInstance().getPackageName()+":连接失败|"+host+":"+port,true);
        ctx.fireChannelInactive();
        if (reconnect){
            startTimeout();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }



    public void startTimeout(){
        LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,NettyClient.getInstance().getPackageName()+":重新开始连接|"+host+":"+port,true);
        mTimer.newTimeout(this,15, TimeUnit.SECONDS);
    }


    @Override
    public void run(Timeout timeout) throws Exception {
        final ChannelFuture future;
        synchronized (mBootstrap){
            mBootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(handlers());
                }
            });
           future = mBootstrap.connect(host,port);
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()){
                    LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,NettyClient.getInstance().getPackageName()+":重新连接成功|"+host+":"+port,true);
                    NettyClient.getInstance().setFuture(channelFuture);
                }else{
                    LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,NettyClient.getInstance().getPackageName()+":重新连接失败|"+host+":"+port,true);
                    future.channel().closeFuture();
                    startTimeout();
                }
            }
        });
    }

}
