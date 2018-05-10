package com.yuyuehao.andy.netty;


import com.yuyuehao.andy.utils.LogUtils;

import java.io.UnsupportedEncodingException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Wang
 * on 2017-11-24
 */

public class NettyClient {

    private static NettyClient nettyClient = null;
    private EventLoopGroup mEventLoopGroup = null;
    private NettyListener mNettyListener;
    private Channel mChannel;
    private boolean isConnect = false;
    private String host;
    private int reconnectNum = Integer.MAX_VALUE;
    private long reconnectIntervalTime = 5000;
    private int port;
    private Bootstrap mBootstrap = null;
    private String charSet;

    public static synchronized NettyClient getInstance(String host,int port,String charSet){
        if (nettyClient == null){
            nettyClient = new NettyClient();
        }
        nettyClient.host = host;
        nettyClient.port = port;
        nettyClient.charSet = charSet;
        return nettyClient;
    }

    public static synchronized NettyClient getInstance(){
        if (nettyClient == null){
            nettyClient = new NettyClient();
        }
        nettyClient.host = nettyClient.getHost();
        nettyClient.port = nettyClient.getPort();
        nettyClient.charSet = nettyClient.getCharSet();
        return nettyClient;
    }

    public synchronized NettyClient connect(){
        if (!isConnect){
            if (mEventLoopGroup == null){
                mEventLoopGroup = new NioEventLoopGroup();
            }
            if (mBootstrap == null) {
                mBootstrap = new Bootstrap().group(mEventLoopGroup)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioSocketChannel.class)
                        .handler(new NettyClientInitializer(mNettyListener));
            }
            try{
                 mBootstrap.connect(host,port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()){
                            isConnect = true;
                            mChannel = future.channel();
                        }else{
                            LogUtils.write("NettyClient",LogUtils.LEVEL_ERROR,"Future chanel is close,so disconnect.",true);
                            disconnect();
                            isConnect = false;
                        }
                    }
                }).sync();
            }catch (Exception e){
                e.printStackTrace();
                mNettyListener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                reconnect();
            }
        }
        return this;
    }

    public void disconnect() {
        if (mChannel != null){
            mChannel.disconnect();
            mChannel.close();
        }
    }

    public void shutDown(){
        if (mEventLoopGroup != null){
            mEventLoopGroup.shutdownGracefully();
            mBootstrap = null;
        }
    }

    public void reconnect(){
        if (reconnectNum >0 && !isConnect){
            reconnectNum--;
            try{
                Thread.sleep(reconnectIntervalTime);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            LogUtils.write("NettyClient",LogUtils.LEVEL_INFO,"Reconnect.",true);
            disconnect();
            connect();
        }else{
            LogUtils.write("NettyClient",LogUtils.LEVEL_ERROR,"NettyClient is not reconnect number,so disconnect.",true);
            disconnect();
            if (mEventLoopGroup != null){
                mEventLoopGroup.shutdownGracefully();
            }
        }
    }

    public boolean sendMsgToServer(String json, ChannelFutureListener listener){
        StringBuffer sb = new StringBuffer(json);
        sb.append("\n");
        String realJson = sb.toString();
        boolean flag = mChannel != null && isConnect;
        if (flag){
            ByteBuf buf = null;
            try {
                byte[] data = realJson.getBytes(charSet);
                buf = Unpooled.copiedBuffer(data);
                mChannel.writeAndFlush(buf).addListener(listener);
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

    public boolean getConnectStatus(){
        return isConnect;
    }

    public void setConnectStatus(boolean status){
        this.isConnect = status;
    }

    public void setListener(NettyListener listener){
        this.mNettyListener = listener;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }
}
