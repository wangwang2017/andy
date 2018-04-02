package com.yuyuehao.andy.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

import com.yuyuehao.andy.netty.NettyClient;
import com.yuyuehao.andy.netty.NettyListener;
import com.yuyuehao.andy.utils.LogUtils;
import com.yuyuehao.andy.utils.MessageEvent;
import com.yuyuehao.andy.utils.Verify;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


/**
 * Created by Wang
 * on 2017-11-24
 */

public class NettyService extends Service implements NettyListener {
//    private WeakReference<Context> mContext;
    private NetworkReceiver receiver;
    private final IBinder binder = new MyBinder();
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new NetworkReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(String.valueOf(ConnectivityManager.TYPE_ETHERNET));
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        NettyClient.getInstance().disconnect();
        sendBroadcast(new Intent("com.yuyuehao.canise.receiver.nettyservice.destroy"));
        LogUtils.write("NettyService",LogUtils.LEVEL_ERROR,"Service is unbind.",true);
        return super.onUnbind(intent);
    }


    public class MyBinder extends Binder {
        public NettyService getService(){
            return NettyService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        NettyClient.getInstance().setListener(this);
        connect();
        return binder;
    }

    @Override
    public void onMessageResponse(ByteBuf data) {
        String json = null;
        json = data.toString(Charset.forName(NettyClient.getInstance().getCharSet()));
        LogUtils.write("NettyService",LogUtils.LEVEL_INFO,"receiver:"+json,true);
        if (Verify.isJson(json)){
            EventBus.getDefault().post(new MessageEvent(0,json));
        }else{
            LogUtils.write("NettyService",LogUtils.LEVEL_INFO,"receiver:is error json",true);
            NettyClient.getInstance().sendMsgToServer("receiver:is error json", new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                }
            });
        }

    }

    private void connect(){
        if (!NettyClient.getInstance().getConnectStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance().connect();//连接服务器
                }
            }).start();
        }
    }



    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        if (statusCode == NettyListener.STATUS_CONNECT_SUCCESS) {
            EventBus.getDefault().post(new MessageEvent(1,"ok"));
            LogUtils.write("NettyService",LogUtils.LEVEL_INFO,"Connect Success",true);
        } else {
            EventBus.getDefault().post(new MessageEvent(1,"error"));
            LogUtils.write("NettyService",LogUtils.LEVEL_INFO,"Connect Defeat",true);

        }
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (NetworkInfo.State.CONNECTED == info.getState()) {
                    connect();
                } else {
                    LogUtils.write("NettyService",LogUtils.LEVEL_ERROR,"Network is disconnect.",true);
                    NettyClient.getInstance().disconnect();
                }
            }
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        NettyClient.getInstance().setReconnectNum(0);
        LogUtils.write("NettyService",LogUtils.LEVEL_ERROR,"Service is destroy,disconnect.",true);
        NettyClient.getInstance().disconnect();
        sendBroadcast(new Intent("com.yuyuehao.canise.receiver.nettyservice.destroy"));
    }

}
