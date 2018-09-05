package com.yuyuehao.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {


    //public InetSocketAddress addr1 = new InetSocketAddress("monitor.ailib.net.cn", 13000);
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
        final String ECHO_REQ = "Hello Netty.$_80";



    }


    public void send88(View view){
        final String ECHO_REQ = "Hello Netty.$_88";

    }

    public void Send(View view){

        final String str = editText.getText().toString();

        if (str.length() !=0){
            if (str.startsWith("1")){
                //final SimpleChannelPool pool = NettyClientPool.getInstance().poolMap.get(addr2);
               //pool.close();
            }else {
                //asyncWriteMessage(addr1,"sdfasdfasdfa");
            }
        }
    }

    public void asyncWriteMessage(final InetSocketAddress address, final String message) {

    }

}
