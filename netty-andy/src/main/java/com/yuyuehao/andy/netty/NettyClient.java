package com.yuyuehao.andy.netty;


import com.yuyuehao.andy.utils.Const;
import com.yuyuehao.andy.utils.LogUtils;

import java.io.UnsupportedEncodingException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.HashedWheelTimer;

/**
 * Created by Wang
 * on 2017-11-24
 */

public class NettyClient {

    private static NettyClient nettyClient = null;
    private NettyListener mNettyListener;
    private ConnectCallBack mConnectCallBack;
    private boolean isConnect = false;
    private volatile String host;
    private int reconnectNum = Integer.MAX_VALUE;
    private long reconnectIntervalTime = 5000;
    private volatile int port;
    private String charSet;
    private String packageName;
    private Bootstrap mBootstrap;
    protected final HashedWheelTimer timer = new HashedWheelTimer();
    private NioEventLoopGroup mEventLoopGroup;
    private Channel mChannel = null;
    private ChannelFuture future;
    private  ConnectionWatchdog watchdog;

    public static synchronized NettyClient getInstance(String host,int port,String charSet,String packageName){
        if (nettyClient == null){
            nettyClient = new NettyClient();
        }
        nettyClient.host = host;
        nettyClient.port = port;
        nettyClient.charSet = charSet;
        nettyClient.packageName = packageName;
        return nettyClient;
    }

    public static synchronized NettyClient getInstance(){
        if (nettyClient == null){
            nettyClient = new NettyClient();
        }
        nettyClient.host = nettyClient.getHost();
        nettyClient.port = nettyClient.getPort();
        nettyClient.charSet = nettyClient.getCharSet();
        nettyClient.packageName = nettyClient.getPackageName();
        return nettyClient;
    }

    public void initBootstrap(){
        LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,packageName+":初始化|"+host+":"+port,true);
        if (mEventLoopGroup == null) {
            mEventLoopGroup = new NioEventLoopGroup();
            mBootstrap = new Bootstrap();
            mBootstrap.group(mEventLoopGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class);
            watchdog = new ConnectionWatchdog(mConnectCallBack,mBootstrap,timer,port,host,Integer.MAX_VALUE) {
                @Override
                public ChannelHandler[] handlers() {
                    return new ChannelHandler[]{
                            this,
                            new LoggingHandler(LogLevel.INFO),
                            new NettyClientInitializer(mNettyListener)
                    };
                }
            };
        }
    }

    public void connect(int reconnectNum){
        watchdog.setRetryNumber(reconnectNum);
        watchdog.setHost(host);
        watchdog.setPort(port);
        watchdog.setCallBack(mConnectCallBack);
        ChannelFuture mChannelFuture = null;
        try {
            LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,packageName+":开始连接|"+host+":"+port,true);
            synchronized (mBootstrap){
                mBootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(watchdog.handlers());
                    }
                });
                mChannelFuture = mBootstrap.connect(host,port);
            }
            mChannelFuture.sync();
            setFuture(mChannelFuture);
        } catch (Exception e) {
            LogUtils.write(Const.Tag,LogUtils.LEVEL_ERROR,packageName+",Exception|"+host+":"+port+"|{"+e.toString()+"}",true);
            if (mChannelFuture != null){
                mChannelFuture.channel().closeFuture();
            }
            watchdog.startTimeout();
        }
    }



    public void closeConnection(){
        if (future != null && future.isSuccess()) {
            future.channel().close();
        }
    }

    public void shutDown() {
        LogUtils.write(Const.Tag,LogUtils.LEVEL_INFO,packageName+":断开连接|"+host+":"+port,true);
        if(mEventLoopGroup != null) {
            mEventLoopGroup.shutdownGracefully();
            mEventLoopGroup = null;
        }
    }



    public synchronized boolean sendMsgToServer(String json, ChannelFutureListener listener){
        StringBuffer sb = new StringBuffer(json);
        sb.append("\n");
        String realJson = sb.toString();
        boolean flag = future != null;
        if (flag){
            ByteBuf buf = null;
            try {
                byte[] data = realJson.getBytes(charSet);
                buf = Unpooled.copiedBuffer(data);
                if (future.channel().isWritable()) {
                    future.channel().writeAndFlush(buf).addListener(listener);
                }
            } catch (UnsupportedEncodingException e) {
                buf.release();
            }
        }
        return flag;
    }



    public void setHost(String host){
        this.host = host;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    public void setReconnectNum(int reconnectNum){
        this.reconnectNum = reconnectNum;
    }


    public void setReconnectIntervalTime(long reconnectIntervalTime){
        this.reconnectIntervalTime = reconnectIntervalTime;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    public boolean getConnectStatus(){
        return isConnect;
    }

    public void setConnectStatus(boolean status){
        this.isConnect = status;
    }

    public void setNettyListener(NettyListener listener){
        this.mNettyListener = listener;
    }

    public void setConnectCallBack(ConnectCallBack connectCallBack) {
        mConnectCallBack = connectCallBack;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
