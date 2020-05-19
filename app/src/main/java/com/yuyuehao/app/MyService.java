package com.yuyuehao.app;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.service.NettyPoolService;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import static com.yuyuehao.andy.utils.IpNetAddress.getIpAddressAndSubnettest;

public class MyService extends NettyPoolService {

    private Map<String,Boolean> isInit = new HashMap<>();
    private String addr1 = "10.0.0.42:13000";


    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void init() {
        NettyClientPool.getInstance().setListener(this);
        NettyClientPool.getInstance().setCallBack(this);
        NettyClientPool.getInstance().setNettyPoolCallback(this);
        connect(addr1);

    }

    private void sendMessage(String address,String message){
        NettyClientPool.getInstance().sendMessage(address, message);
    }

    private void connect(String address){
        NettyClientPool.getInstance().connect(address);
    }

    @Override
    protected void getPublicNetWorkIp(String s) {
        Log.i("MyService","getPublicNetWorkIp:"+s);
    }

    @Override
    public void onCompletionTimerTask(String address) {
        Log.i("MyService","onCompletionTimerTask:"+address);
    }

    @Override
    public void nettyCreate(Channel channel) {
        Log.i("MyService","nettyCreate:"+channel.id());
        isInit.put(addr1,true);

    }

    @Override
    public void nettyAcquired(Channel channel) {
        Log.i("MyService","nettyAcquired:"+channel.id());
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        isInit.put(address,false);
    }

    @Override
    public void nettyReleased(Channel channel) {
        Log.i("MyService","nettyReleased:"+channel.id());
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        StringBuffer sb = new StringBuffer();
        sb.append(inetSocketAddress.getHostName());
        sb.append(":");
        sb.append(inetSocketAddress.getPort()+"");
        String address = sb.toString();
        if (channel.isActive() && isInit.get(address) != null && isInit.get(address)){
            isInit.put(address,false);
            sendMessage(address,createConnectJson());
        }
    }

    @Override
    public void onMessageResponse(String address, ByteBuf data) {
        Log.i("MyService","onMessageResponse|"+address+"|"+data.toString(Charset.forName("utf-8")));
    }

    @Override
    public void onInactive(String address) {

    }


    private String createConnectJson(){
        LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
        map.put("record","red.connect");
        String mark = new Random().nextInt(10000)+100000+"";
        map.put("mark",mark);
        map.put("co_equipment_uid", "y-1-1-111111");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        map.put("datetime",date);
        map.put("data1","red");
        map.put("data2","veron");
        String data3 = getIpAddressAndSubnettest();
        map.put("data3",data3);
        map.put("data4","undefined");
        StringBuffer sb = new StringBuffer();
        sb.append(map.get("record"));
        sb.append(map.get("mark"));
        sb.append(map.get("co_equipment_uid"));
        sb.append(map.get("datetime"));
        sb.append(map.get("data1"));
        sb.append(map.get("data2"));
        sb.append(map.get("data3"));
        sb.append(map.get("data4"));
        sb.append("abc");
        String check_sum = MD5Utils.getStringMD5(sb.toString());
        map.put("check_sum",check_sum);
        String firstJson = new Gson().toJson(map);
        Log.d("1",firstJson);
        return firstJson;
    }


}
