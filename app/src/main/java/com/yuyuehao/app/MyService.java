package com.yuyuehao.app;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yuyuehao.andy.netty.NettyClient;
import com.yuyuehao.andy.service.NettyService;
import com.yuyuehao.andy.utils.Verify;

import java.util.ArrayList;
import java.util.List;

public class MyService extends NettyService {

    private List<String> list = new ArrayList<>();
    public MyService() {
    }

    @Override
    protected void init() {
        NettyClient.getInstance("192.168.53.16",13000,"utf-8","canise");
        NettyClient.getInstance().initBootstrap();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void getMessageInfo(String json) {
        Log.d("1",json);
        if (json.contains("|")) {
            String[] strings = json.split("\\|");
            if (strings.length >=3){
                if (Verify.isDigit(strings[2])){
                    NettyClient.getInstance().closeConnection();
                    NettyClient.getInstance().setHost(strings[1]);
                    NettyClient.getInstance().setPort(Integer.valueOf(strings[2]));
                    NettyClient.getInstance().connect(3);
                }
            }
        }else{
            Log.d("1","未进去");
        }
    }

    @Override
    protected void getStatusInfo(int statusCode) {

    }

    @Override
    protected void getPublicNetWorkIp(String s) {

    }


    @Override
    public void completionConnectNumber(String ip) {
        Log.d("1",ip);
    }
}
