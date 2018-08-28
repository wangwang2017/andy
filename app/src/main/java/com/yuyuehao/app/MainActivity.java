package com.yuyuehao.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.yuyuehao.andy.netty.NettyClientPool;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class MainActivity extends AppCompatActivity {


    public InetSocketAddress addr1 = new InetSocketAddress("192.168.53.16", 8080);
    public InetSocketAddress addr2 = new InetSocketAddress("192.168.53.16", 8888);
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        startService(new Intent(this,MyService.class));
    }


    public void send80(View view){
//        final String ECHO_REQ = "Hello Netty.$_80";
//        final SimpleChannelPool pool = NettyClientPool.getInstance().poolMap.get(addr1);
//        Future<Channel> f = pool.acquire();
//        f.addListener(new GenericFutureListener<Future<? super Channel>>() {
//           @Override
//           public void operationComplete(Future<? super Channel> future) throws Exception {
//               Channel ch = (Channel) future.getNow();
//               ch.writeAndFlush(ECHO_REQ);
//               pool.release(ch);
//           }
//        });
        LogData log = new LogData("100221","send","大叔大婶的范围发生的教科书积分","收到货发神经的咖啡机我就发十几分");
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = ContentData.TableData.CONTENT_URI;
        contentResolver.insert(uri,CirContentProvider.insertLogData(log));
    }


    public void send88(View view){
        final String ECHO_REQ = "Hello Netty.$_88";
        final SimpleChannelPool pool = NettyClientPool.getInstance().poolMap.get(addr2);
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
                final SimpleChannelPool pool = NettyClientPool.getInstance().poolMap.get(addr2);
                pool.close();
            }else {
                final SimpleChannelPool pool = NettyClientPool.getInstance().poolMap.get(addr1);
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
