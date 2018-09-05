package com.yuyuehao.app;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.yuyuehao.andy.service.NettyPoolService;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import static com.yuyuehao.andy.utils.IpNetAddress.getIpAddressAndSubnettest;

public class MyService extends NettyPoolService {

    private List<String> list = new ArrayList<>();
    private List<InetSocketAddress> list1 = new ArrayList<>();
    private boolean connect = true;


    private final static int Next = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Next){
                Log.d("1",(String)msg.obj);
                asyncWriteMessage((String)msg.obj,createConnectJson(),3);
            }
        }
    };


    @Override
    public void onMessageResponse(String inetSocketAddress, ByteBuf data) {
        Log.d("1","receiver:"+inetSocketAddress);

        String json = data.toString(CharsetUtil.UTF_8);
        Log.d("1","json:"+json);
        for (int i = 0; i <list.size() ; i++) {
            Log.d("1","list-<"+i+">:"+list.get(i).toString());
        }
        if (connect) {
            if (list.contains(inetSocketAddress)) {
                if (json.contains("ok")){

                }else{
                    String strs[] = json.split("\\|");
                    Log.d("1","new,"+strs[1]+":"+strs[2]);
                    closePoolConnection(inetSocketAddress);
                    InetSocketAddress address1 = InetSocketAddress.createUnresolved(strs[1], Integer.valueOf(strs[2]));
                    if (!list.contains(address1)) {
                        list.add(address1.toString());
                    }
                    asyncWriteMessage(address1.toString(),createConnectJson(),3);
                }
            }
        }
    }



    @Override
    public void onServiceStatusConnectChanged(final String inetSocketAddress, int statusCode) {
        if (statusCode == 0){
            Log.d("1",inetSocketAddress+" failed.");
            try {
                if (list.contains(inetSocketAddress)) {
                    if (list.size() <= 1) {
                        closePoolConnection(inetSocketAddress);
                        Thread.sleep(5000);
                        asyncWriteMessage(inetSocketAddress, createConnectJson(), Integer.MAX_VALUE);
                    } else {
                        int a = list.indexOf(inetSocketAddress);
                        Random rd = new Random();
                        int second = rd.nextInt(21)*1000;
                        Message msg = Message.obtain();
                        msg.what = Next;
                        if (list.size()-1 > a){
                            Log.d("1",second+",next:"+list.get(a+1).toString());
                            msg.obj = list.get(a+1);
                        }else{
                            Log.d("1",second+",start:"+list.get(0).toString());
                            msg.obj = list.get(0);
                        }
                        mHandler.sendMessageDelayed(msg,second);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else if (statusCode == 1){
            Log.d("1","address:"+inetSocketAddress+",success.");
        }else if (statusCode == -1){
            Log.d("1","address:"+inetSocketAddress+",exception.");
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
        String address = "192.168.53.16:13000";
        asyncWriteMessage(address,createConnectJson(),Integer.MAX_VALUE);
        if (!list.contains(address)) {
            list.add(address);
        }
    }

    @Override
    protected void getPublicNetWorkIp(String s) {
        Log.d("1","s:"+s);
    }


    @Override
    public void onCompletionTimerTask(String inetSocketAddress) {
        Log.d("1","size:"+list.size());
        if (list.contains(inetSocketAddress)){
            int i = list.indexOf(inetSocketAddress);
            Random rd = new Random();
            int second = rd.nextInt(21)*1000;
            Message msg = Message.obtain();
            msg.what = Next;
            if (list.size()-1 > i){
                Log.d("1",second+",next:"+list.get(i+1).toString());
                msg.obj = list.get(i+1);
            }else{
                Log.d("1",second+",start:"+list.get(0).toString());
                msg.obj = list.get(0);
            }
            mHandler.sendMessageDelayed(msg,second);
        }
    }
}
