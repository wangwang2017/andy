package com.yuyuehao.andy.netty;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * Created by Wang
 * on 2018-08-30
 */

public class MyChannelPool implements ChannelPool {

    @Override
    public Future<Channel> acquire() {
        return null;
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        return null;
    }

    @Override
    public Future<Void> release(Channel channel) {
        return null;
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return null;
    }

    @Override
    public void close() {

    }
}
