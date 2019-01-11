package com.yuyuehao.andy.netty;

/**
 * Created by Wang
 * on 2019-01-10
 */

public class NettyResponse {

    private String mUUID;

    private String ip;

    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
