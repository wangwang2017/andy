package com.yuyuehao.app;

import android.util.Log;

import com.google.gson.Gson;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.service.NettyPoolService;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import static com.yuyuehao.andy.utils.IpNetAddress.getIpAddressAndSubnettest;

public class MyService extends NettyPoolService implements TimerTask{

    public InetSocketAddress addr1 = new InetSocketAddress("192.168.53.16", 8080);
    public InetSocketAddress addr2 = new InetSocketAddress("192.168.53.16", 8888);
    private InetSocketAddress errorAddress = null;
    private boolean key = false;
    private final HashedWheelTimer timer = new HashedWheelTimer();
    private InetSocketAddress error_inetSocketAddress  = null;


    @Override
    public void onMessageResponse(ChannelHandlerContext channelHandlerContext, ByteBuf data) {
        Log.d("1","ctx:"+channelHandlerContext.channel().remoteAddress().toString()+"   "+data.toString(Charset.forName("utf-8"))+", NettyPoolService");
    }

    @Override
    public void onServiceStatusConnectChanged(final InetSocketAddress inetSocketAddress, int statusCode) {

        if (statusCode == 0){
            error_inetSocketAddress = inetSocketAddress;
            timer.newTimeout(this,5, TimeUnit.SECONDS);
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
        return firstJson;
    }

    @Override
    protected void init() {
        firstSendMessage(addr1);
        firstSendMessage(addr2);
    }


    @Override
    public void run(Timeout timeout) throws Exception {
        if (error_inetSocketAddress != null){
            Log.d("1","no connect");
            firstSendMessage(error_inetSocketAddress);
        }
    }

    public void firstSendMessage(InetSocketAddress address) {
        final SimpleChannelPool pool = NettyClientPool.getInstance().getPool(address);
        Future<Channel> future = pool.acquire();
        // 获取到实例后发消息
        future.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Channel ch = (Channel) channelFuture.getNow();
                    StringBuffer sb = new StringBuffer();
                    sb.append(createConnectJson());
                    sb.append("\n");
                    ch.writeAndFlush(sb.toString());
                    pool.release(ch);
                }else{
                    timer.newTimeout(MyService.this,5,TimeUnit.SECONDS);
                }
            }
        });
    }
}
