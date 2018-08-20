package com.yuyuehao.andy.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.yuyuehao.andy.netty.NettyClient;
import com.yuyuehao.andy.netty.NettyListener;
import com.yuyuehao.andy.utils.Const;
import com.yuyuehao.andy.utils.IpNetAddress;
import com.yuyuehao.andy.utils.LogUtils;
import com.yuyuehao.andy.utils.Verify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;


/**
 * Created by Wang
 * on 2017-11-24
 */

public abstract class NettyService extends Service implements NettyListener {

    private Task task;
    public  String packageName;



    @Override
    public void onCreate() {
        super.onCreate();
        setTCP_Params(60,15,4);
    }

    private void setTCP_Params(int keepidlte,int keepintvl,int keepcnt){
        if (keepidlte >0 && keepcnt>0 && keepcnt>0) {
            suExecWait("echo " + keepidlte + " > /proc/sys/net/ipv4/tcp_keepalive_time\n", null);
            suExecWait("echo " + keepintvl + " > /proc/sys/net/ipv4/tcp_keepalive_intvl\n", null);
            suExecWait("echo " + keepcnt + " > /proc/sys/net/ipv4/tcp_keepalive_probes\n", null);
        }
        LogUtils.write(Const.Tag, LogUtils.LEVEL_INFO,"TCP_KEEPIDLTE:"+suExecWait("cat /proc/sys/net/ipv4/tcp_keepalive_time\n",null),true);
        LogUtils.write(Const.Tag, LogUtils.LEVEL_INFO,"TCP_KEEPINTVL:"+suExecWait("cat /proc/sys/net/ipv4/tcp_keepalive_intvl\n",null),true);
        LogUtils.write(Const.Tag, LogUtils.LEVEL_INFO,"TCP_KEEPCNT:"+suExecWait("cat /proc/sys/net/ipv4/tcp_keepalive_probes\n",null),true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = new Task();
        task.execute("http://ip.6655.com/ip.aspx");
        init();
        NettyClient.getInstance().setListener(this);
        packageName = NettyClient.getInstance().getPackageName();
        connect();
        return START_STICKY;
    }

    protected abstract void init();


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onMessageResponse(ChannelHandlerContext channelHandlerContext, ByteBuf data) {
        String json = null;
        json = data.toString(Charset.forName(NettyClient.getInstance().getCharSet()));
        if (Verify.isJson(json)){
            getMessageInfo(json);
        } else if(json.equals("\n") || json.equals("\r") || json.equals("\n\r") || json.equals("")){

        }else{
            LogUtils.write(Const.Tag, LogUtils.LEVEL_ERROR, packageName+":Error Json,"+json, true);
            NettyClient.getInstance().sendMsgToServer("{\"error\":\"bad_request\"}", new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                }
            });
        }
    }

    protected abstract void getMessageInfo(String json);

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
    public void onServiceStatusConnectChanged(InetSocketAddress inetSocketAddress, int statusCode) {
        getStatusInfo(statusCode);
        
    }

    protected abstract void getStatusInfo(int statusCode);



    class Task extends AsyncTask<String, Integer, String>{


        @Override
        protected String doInBackground(String... params) {
            return  IpNetAddress.getNetIp(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("undefined")) {
                getPublicNetWorkIp(s);
            }
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    protected abstract void getPublicNetWorkIp(String s);



    private static String suExecWait(String cmd, File file){
        if (cmd == null || (cmd = cmd.trim()).length() ==0){
            return null;
        }
        if (file == null){
            file = new File("/");
        }
        OutputStream out = null;
        InputStream in = null;
        InputStream err = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("su",null,file);
            StringBuffer inString = new StringBuffer();
            StringBuffer errString = new StringBuffer();
            out = process.getOutputStream();

            out.write(cmd.endsWith("\n") ? cmd.getBytes():(cmd+"\n").getBytes());
            out.write(new byte[]{'e','x','i','t','\n'});

            in = process.getInputStream();
            err = process.getErrorStream();

            process.waitFor();

            while(in.available()>0){
                inString.append((char)in.read());
            }
            while(err.available()>0){
                errString.append((char)err.read());
            }
            return  inString.toString();
        }catch (Exception e){
            return null;
        }finally {
            try {
                if(out != null) {
                    out.close();
                }
                if (in != null){
                    in.close();
                }
                if (err != null) {
                    err.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.write(Const.Tag,LogUtils.LEVEL_ERROR,packageName+":NettyService is destroy,disconnect.",true);
        NettyClient.getInstance().shutDown();
        task.onCancelled();
    }

}
