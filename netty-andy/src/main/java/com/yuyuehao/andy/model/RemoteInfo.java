package com.yuyuehao.andy.model;

/**
 * Created by Wang
 * on 2018-08-07
 */

public class RemoteInfo {


    private String address;
    private int port;

    public RemoteInfo(){

    }
    public RemoteInfo(String address, int port) {
        this.address = address;
        this.port = port;

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}
