package com.yuyuehao.app;

import android.util.Log;

import com.google.gson.Gson;
import com.yuyuehao.andy.service.NettyPoolService;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.yuyuehao.andy.utils.IpNetAddress.getIpAddressAndSubnettest;

public class MyService extends NettyPoolService {

    //public InetSocketAddress addr1 = new InetSocketAddress("192.168.53.16", 13000);
    public InetSocketAddress addr2 = new InetSocketAddress("192.168.53.16", 13020);
    private boolean key = false;



    @Override
    public void onMessageResponse(ChannelHandlerContext channelHandlerContext, ByteBuf data) {
        Log.d("1","ctx:"+channelHandlerContext.channel().remoteAddress().toString()+"   "+data.toString(Charset.forName("utf-8"))+", NettyPoolService");
        if (channelHandlerContext.channel().remoteAddress().toString().contains("13000")){
            if (data.toString(Charset.forName("utf-8")).startsWith("1")){
                asyncWriteMessage(addr2,createConnectJson());
                asyncWriteMessage(InetSocketAddress.createUnresolved("192.168.53.16",13000),"successful");
            }else if (data.toString(Charset.forName("utf-8")).startsWith("2")){
                closePoolConnection(addr2);
            }
        }

    }



    @Override
    public void onServiceStatusConnectChanged(final InetSocketAddress inetSocketAddress, int statusCode) {
        if (statusCode == 0){
            try {
                closePoolConnection(inetSocketAddress);
                Thread.sleep(5000);
                Log.d("1","ctx:"+inetSocketAddress.toString());
                asyncWriteMessage(inetSocketAddress,createConnectJson());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

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

    @Override
    protected void init() {
       asyncWriteMessage(InetSocketAddress.createUnresolved("192.168.53.16",13000),createConnectJson());
    }

    @Override
    protected void getPublicNetWorkIp(String s) {
        Log.d("1","s:"+s);
    }


}
