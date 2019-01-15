package com.yuyuehao.andy.netty;

import io.netty.buffer.ByteBuf;

/**
 * Created by Wang
 * on 2017-11-24
 */

public interface NettyListener {


    /**
     * 对消息的处理
     * @param data
     */
    void onMessageResponse(String address, ByteBuf data);

    /**
     * 当服务状态发生变化时触发
     */

}
