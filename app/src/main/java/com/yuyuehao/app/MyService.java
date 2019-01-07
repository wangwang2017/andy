package com.yuyuehao.app;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yuyuehao.andy.netty.NettyClient;
import com.yuyuehao.andy.service.NettyService;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class MyService extends NettyService {

    private List<String> list = new ArrayList<>();
    private static final String TAG = "MyService";
    public MyService() {
    }

    @Override
    protected void init() {
        NettyClient.getInstance("192.168.53.61",13000,"utf-8","canise");
        NettyClient.getInstance().initBootstrap();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void getMessageInfo(String json) {
        Log.i(TAG,json);
        if (json.equals("1")){
            NettyClient.getInstance().sendMsgToServer("2\n", new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        Log.i(TAG,"success");
                    }
                }
            });
        }
    }

    @Override
    protected void getStatusInfo(int statusCode) {
        Log.i(TAG,"statusCode:"+statusCode);
    }

    @Override
    protected void getPublicNetWorkIp(String s) {

    }


    @Override
    public void completionConnectNumber(String ip) {

    }
}
