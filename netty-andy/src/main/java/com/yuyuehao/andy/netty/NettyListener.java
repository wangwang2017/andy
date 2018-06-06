package com.yuyuehao.andy.netty;

import io.netty.buffer.ByteBuf;

/**
 * Created by Wang
 * on 2017-11-24
 */

public interface NettyListener {

    public final static byte STATUS_CONNECT_SUCCESS = 1;

    public final static byte STATUS_CONNECT_CLOSED = 0;

    public final static byte STATUS_CONNECT_ERROR = 0;

    /**
     * 对消息的处理
     * @param data
     */
    void onMessageResponse(ByteBuf data);



    /**
     * 当服务状态发生变化时触发
     */
    public void onServiceStatusConnectChanged(int statusCode);
}
