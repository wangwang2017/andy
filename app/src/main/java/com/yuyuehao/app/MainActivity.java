package com.yuyuehao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yuyuehao.andy.netty.NettyClient;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;

public class MainActivity extends AppCompatActivity implements NettyListener {


    private static final String TAG = "Main";
    private EditText editText;
    private static final String ECHO_REQ = "\n";
    private boolean isSendKey;
    private ChannelId channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        NettyClient.getInstance().setNettyListener(this);

    }


    public void send10000(View view){
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
        Log.d("andy","size:"+NettyClientPool.getInstance().poolMap.size());
        if (str.equals("0")){
           NettyClient.getInstance().closeChannel("192.168.53.61:10000");
        }else if (str.equals("1")){
            NettyClient.getInstance().closeChannel("192.168.53.61:10001");
        }else if (str.equals("2")){
            NettyClient.getInstance().closeChannel("192.168.53.61:10002");
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
        NettyClient.getInstance().connect(address);
    }

    public void asyncWriteMessage(String address, final String message) {
        NettyClient.getInstance().sendMessage(address, message, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()){
                    Toast.makeText(MainActivity.this,"send ok",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"send failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    @Override
    public void onMessageResponse(String inetSocketAddress, ByteBuf data) {
        String string = data.toString(Charset.forName("utf-8"));
        Log.i(TAG,inetSocketAddress+":"+string);
        if (string.equals("4")){
            asyncWriteMessage(inetSocketAddress,"response");
        }
    }

}
