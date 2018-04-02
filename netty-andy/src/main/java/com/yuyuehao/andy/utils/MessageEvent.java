package com.yuyuehao.andy.utils;

/**
 * Created by Wang
 * on 2017-11-30
 */

public class MessageEvent {

    private String message;
    private int type;

    public MessageEvent(int type, String message){
        this.type = type;
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public int getType(){
        return type;
    }
}
