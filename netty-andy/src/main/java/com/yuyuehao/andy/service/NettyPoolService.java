package com.yuyuehao.andy.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.yuyuehao.andy.netty.CallBack;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;
import com.yuyuehao.andy.netty.NettyPoolCallback;
import com.yuyuehao.andy.utils.Const;
import com.yuyuehao.andy.utils.IpNetAddress;
import com.yuyuehao.andy.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.netty.util.HashedWheelTimer;

public abstract class NettyPoolService extends Service implements NettyListener,CallBack,NettyPoolCallback{


    private HashedWheelTimer timer = new HashedWheelTimer();
    private Task task;
    public NettyPoolService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        task = new Task();
        task.execute("http://ip.6655.com/ip.aspx");
        setTCP_Params(60,15,4);
        try {
            NettyClientPool.getInstance().setListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        init();
        return super.onStartCommand(intent, flags, startId);
    }


    protected abstract void init();



    public void closeChannel(String address){
        if (NettyClientPool.getInstance().poolMap.contains(address)){
            NettyClientPool.getInstance().poolMap.remove(address);
        }
    }


    class Task extends AsyncTask<String, Integer, String> {


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
        NettyClientPool.getInstance().closeGroup();
    }
}
