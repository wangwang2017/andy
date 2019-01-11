package com.yuyuehao.app;

/**
 * Created by Wang
 * on 2019-01-10
 */

public class NettyRequest {

    private String mUUID;

    private String ip;

    private String message;

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
