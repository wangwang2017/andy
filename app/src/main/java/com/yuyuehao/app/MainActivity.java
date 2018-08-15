package com.yuyuehao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.yuyuehao.andy.netty.NettyPoolClient;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class MainActivity extends AppCompatActivity {

    private NettyPoolClient client;
    public InetSocketAddress addr1 = new InetSocketAddress("192.168.53.16", 8081);
    public InetSocketAddress addr2 = new InetSocketAddress("192.168.53.16", 8888);
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        client = new NettyPoolClient();

        try {
            client.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void send80(View view){
        final String ECHO_REQ = "Hello Netty.$_80";
        final SimpleChannelPool pool = client.poolMap.get(addr1);
        Future<Channel> f = pool.acquire();
        f.addListener(new GenericFutureListener<Future<? super Channel>>() {
           @Override
           public void operationComplete(Future<? super Channel> future) throws Exception {
               Channel ch = (Channel) future.getNow();
               ch.writeAndFlush(ECHO_REQ);
               pool.release(ch);
           }
        });


    }


    public void send88(View view){
        final String ECHO_REQ = "Hello Netty.$_88";
        final SimpleChannelPool pool = client.poolMap.get(addr2);
        Future<Channel> f = pool.acquire();
        f.addListener(new GenericFutureListener<Future<? super Channel>>() {
            @Override
            public void operationComplete(Future<? super Channel> future) throws Exception {
                Channel ch = (Channel) future.getNow();
                ch.writeAndFlush(ECHO_REQ);
                pool.release(ch);
            }
        });




    }

    public void Send(View view){

        final String str = editText.getText().toString();

        if (str.length() !=0){
            if (str.startsWith("1")){
                final SimpleChannelPool pool = client.poolMap.get(addr2);
                pool.close();
            }else {
                final SimpleChannelPool pool = client.poolMap.get(addr1);
                Future<Channel> f = pool.acquire();
                f.addListener(new GenericFutureListener<Future<? super Channel>>() {
                    @Override
                    public void operationComplete(Future<? super Channel> future) throws Exception {
                        Channel ch = (Channel)future.getNow();
                        ch.writeAndFlush("dasdfasdfa");
                        pool.release(ch);
                    }
                });

            }
        }
    }

}
