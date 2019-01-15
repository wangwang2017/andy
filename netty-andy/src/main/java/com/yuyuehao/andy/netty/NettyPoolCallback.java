package com.yuyuehao.andy.netty;

import io.netty.channel.Channel;

/**
 * Created by Wang
 * on 2019-01-14
 */

public interface NettyPoolCallback {

    void nettyCreate(Channel channel);

    void nettyAcquired(Channel channel);

    void nettyReleased(Channel channel);
}
