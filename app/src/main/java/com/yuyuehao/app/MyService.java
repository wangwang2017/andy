package com.yuyuehao.app;

import android.content.Intent;
import android.os.IBinder;

import com.yuyuehao.andy.service.NettyService;

public class MyService extends NettyService {
    public MyService() {

    }

    @Override
    protected void init() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void getMessageInfo(String json) {

    }

    @Override
    protected void getStatusInfo(int statusCode) {

    }

    @Override
    protected void getPublicNetWorkIp(String s) {

    }
}
