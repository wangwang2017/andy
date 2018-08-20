package com.yuyuehao.andy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public abstract class NettyPoolService extends Service implements NettyListener{


    public NettyPoolService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NettyClientPool.getInstance().build();
            NettyClientPool.getInstance().setListener(this);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    protected abstract void init();


    public void asyncWriteMessage(InetSocketAddress address, final String message) {
        final SimpleChannelPool pool = NettyClientPool.getInstance().getPool(address);
        Future<Channel> future = pool.acquire();
        // 获取到实例后发消息
        future.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Channel ch = (Channel) channelFuture.getNow();
                    StringBuffer sb = new StringBuffer();
                    sb.append(message);
                    sb.append("\n");
                    ch.writeAndFlush(sb.toString());
                    pool.release(ch);
                }else{

                }
            }
        });
    }
}
