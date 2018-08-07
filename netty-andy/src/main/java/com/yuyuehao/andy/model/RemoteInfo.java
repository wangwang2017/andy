package com.yuyuehao.andy.model;

/**
 * Created by Wang
 * on 2018-08-07
 */

public class RemoteInfo {


    private String address;
    private String port;
    private String charset;


    public RemoteInfo(){

    }

    public RemoteInfo(String address, String port, String charset) {
        this.address = address;
        this.port = port;
        this.charset = charset;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }


}
