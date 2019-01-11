package com.yuyuehao.andy.netty;

public interface CallBack {

   void onCompletionTimerTask(String inetSocketAddress);

   void onConnectSuccessful(String inetSocketAddress);
}