package com.yuyuehao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class MainActivity extends AppCompatActivity implements NettyListener{


    private static final String TAG = "Main";
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        //startService(new Intent(this,MyService.class));
        NettyClientPool.getInstance().setListener(this);
        try {
            NettyClientPool.getInstance().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void send80(View view){
        final String ECHO_REQ = "Hello Netty.$_10000";
        asyncWriteMessage("192.168.53.61:10000",ECHO_REQ);


    }


    public void send88(View view){
        final String ECHO_REQ = "Hello Netty.$_10001";
        asyncWriteMessage("192.168.53.61:10001",ECHO_REQ);
    }

    public void Send(View view){
        final String str = editText.getText().toString();
        asyncWriteMessage("192.168.53.61:10002",str);
    }

    public void asyncWriteMessage(String  address, final String message) {
        final SimpleChannelPool pool = NettyClientPool.getInstance().getPool(address);
        Future<Channel> future = pool.acquire();
        // 获取到实例后发消息
        future.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Channel ch = (Channel) channelFuture.getNow();
                    StringBuffer sb = new StringBuffer();
                    sb.append(message);
                    sb.append("\n");
                    ch.writeAndFlush(sb.toString());
                    pool.release(ch);
                }
            }
        });
    }

    @Override
    public void onMessageResponse(String inetSocketAddress, ByteBuf data) {
        Log.i(TAG,inetSocketAddress+":"+data.toString(Charset.forName("utf-8")));
    }

    @Override
    public void onServiceStatusConnectChanged(String inetSocketAddress, int statusCode) {
        Log.i(TAG,inetSocketAddress+":"+"statusCode = "+statusCode);
    }
}
