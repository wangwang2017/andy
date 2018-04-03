package com.yuyuehao.andy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.yuyuehao.andy.service.NettyService;
import com.yuyuehao.andy.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Wang
 * on 2018-04-02
 */

public abstract class NettyActivity extends Activity{

    private NettyService mNettyService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mNettyService = ((NettyService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mNettyService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        init();
        toBind();
    }

    private void toBind(){
        Intent bindIntent = new Intent(NettyActivity.this,NettyService.class);
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    protected abstract void init();


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent){
        String json = messageEvent.getMessage();
        if (messageEvent.getType() == 0){
            getMessageInfo(json);
        }else if (messageEvent.getType() == 1){
            getStatusInfo(json);
        }
    }





    protected abstract void getStatusInfo(String json);

    protected abstract void getMessageInfo(String json);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }
}
