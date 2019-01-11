package com.yuyuehao.andy.netty;

import io.netty.channel.Channel;

/**
 * Created by Wang
 * on 2019-01-11
 */

public class ChannelCtx {

    private Channel mChannel;
    private String ip;
    private boolean isDone;

    public void setDone(boolean done) {
        isDone = done;
    }

    public Channel getChannel() {
        return mChannel;
    }

    public void setChannel(Channel channel) {
        mChannel = channel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isDone() {
        return isDone;
    }
}
