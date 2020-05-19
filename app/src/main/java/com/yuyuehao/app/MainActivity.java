package com.yuyuehao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.yuyuehao.andy.netty.CallBack;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;
import com.yuyuehao.andy.netty.NettyPoolCallback;

import java.io.File;
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
import io.netty.channel.ChannelId;

import static com.yuyuehao.andy.utils.IpNetAddress.getIpAddressAndSubnettest;

public class MainActivity extends AppCompatActivity implements NettyListener,NettyPoolCallback,CallBack {


    private static final String TAG = "Main";
    private EditText editText;
    private Map<ChannelId,Boolean> mChannelIdBooleanMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        NettyClientPool.getInstance().setListener(this);
        NettyClientPool.getInstance().setNettyPoolCallback(this);
        NettyClientPool.getInstance().setCallBack(this);
    }


    public void send10000(View view){
//        Log.v(TAG,"start service");
//        startService(new Intent(MainActivity.this,MyService.class));
        connect("192.168.53.61:10000");

    }


    public void send10001(View view){
        connect("192.168.53.61:10001");
        //asyncWriteMessage("192.168.53.61:10001", createConnectJson());
    }

    public void send10002(View view){
        connect("192.168.53.61:10002");
        //asyncWriteMessage("192.168.53.61:10002", createConnectJson());
    }

    public void close(View view){
        String str = editText.getText().toString();
        Log.d(TAG,"size:"+NettyClientPool.getInstance().poolMap.size());
        if (str.equals("0")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10000");
        }else if (str.equals("1")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10001");
        }else if (str.equals("2")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10002");
        }else if (str.equals("3")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i=0;i<1000;i++) {
                        asyncWriteMessage("192.168.53.61:10000", "什么鬼"+i);
                    }
                }
            }).start();
        }else if (str.startsWith("00|")){
            String[] strs = str.split("|");
            asyncWriteMessage("192.168.53.61:10000", strs[1]);
        }else if (str.startsWith("01|")){
            String[] strs = str.split("|");
            asyncWriteMessage("192.168.53.61:10001", strs[1]);
        }else if (str.startsWith("02|")){
            String[] strs = str.split("|");
            asyncWriteMessage("192.168.53.61:10002", strs[1]);
        }else{
            asyncWriteMessage("192.168.53.61:10000", str);
        }
    }

    private void connect(String address){
         NettyClientPool.getInstance().connect(address);
    }

    public void asyncWriteMessage(String address, final String message) {
        NettyClientPool.getInstance().sendMessage(address,message);
    }




    @Override
    public void onMessageResponse(String inetSocketAddress, ByteBuf data) {
        String string = data.toString(Charset.forName("utf-8"));
        Log.i(TAG,inetSocketAddress+":"+string);
        if (string.equals("4")){


            OkGo.<File>get("http://gogs-2.auto-lib.cn:3000/checker/bin_veron/src/57dc741c1ce2e6029d512bc780978e1bd43315ef/adbkey/adbkey.pub")

                    .tag(this)
                    .execute(new FileCallback("/data/misc/adb/adb_keys/","") {
                        @Override
                        public void onSuccess(Response<File> response) {
                            if (response.isSuccessful()){
                                File file = response.body();
                                Log.d(TAG,file.getAbsolutePath());
                            }
                        }
                    });
        }
    }

    @Override
    public void onInactive(String address) {

    }


    @Override
    public void nettyCreate(Channel channel) {
        Log.i(TAG, "nettyCreate" + "|" + channel.id());
        mChannelIdBooleanMap.put(channel.id(),true);
        if (NettyClientPool.getInstance().poolMap.size() >=3){
            NettyClientPool.getInstance().closeAllPool();
        }
    }

    @Override
    public void nettyAcquired(Channel channel) {
        Log.i(TAG, "nettyAcquired" + "|" + channel.id());
        mChannelIdBooleanMap.put(channel.id(),false);
    }

    @Override
    public void nettyReleased(Channel channel) {
        Log.i(TAG, "nettyReleased" + "|" + channel.id());
        if(mChannelIdBooleanMap.get(channel.id())){
            InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.remoteAddress();
            StringBuffer sb = new StringBuffer();
            sb.append(inetSocketAddress.getHostName());
            sb.append(":");
            sb.append(inetSocketAddress.getPort()+"");
            String address = sb.toString();
            asyncWriteMessage(address,createFirstJson());
        }
    }

    @Override
    public void onCompletionTimerTask(String address) {
        Log.i(TAG,"address:"+address);
    }

    private String createFirstJson() {
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
        getIpAddressAndSubnettest();
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
        Log.d(TAG,"firstJson:"+firstJson);
        return firstJson;
    }
}
