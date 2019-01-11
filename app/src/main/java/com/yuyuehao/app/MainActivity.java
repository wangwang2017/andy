package com.yuyuehao.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.yuyuehao.andy.netty.ChannelCtx;
import com.yuyuehao.andy.netty.NettyClientPool;
import com.yuyuehao.andy.netty.NettyListener;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class MainActivity extends AppCompatActivity implements NettyListener{


    private static final String TAG = "Main";
    private EditText editText;
    private static final String ECHO_REQ = "\n";
    private ChannelCtx mChannelCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        NettyClientPool.getInstance().setListener(this);
    }


    public void send10000(View view){
        asyncWriteMessage("192.168.53.61:10000","");
    }


    public void send10001(View view){
        asyncWriteMessage("192.168.53.61:10001","");
    }

    public void send10002(View view){
        asyncWriteMessage("192.168.53.61:10002","");
    }

    public void close(View view){
        String str = editText.getText().toString();
        Log.d("andy","size:"+NettyClientPool.getInstance().poolMap.size());
        if (str.equals("0")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10000");
        }else if (str.equals("1")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10001");
        }else if (str.equals("2")){
            NettyClientPool.getInstance().poolMap.remove("192.168.53.61:10002");
        }else if (str.equals("3")){
            for (int i=0;i<1000;i++) {
                asyncWriteMessage("192.168.53.61:10000", "什么鬼"+i);
            }
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

    public void asyncWriteMessage(String address, final String message) {
        NettyClientPool.getInstance().sendMessage(address,message);
    }




    @Override
    public void onMessageResponse(String inetSocketAddress, ByteBuf data) {
        String string = data.toString(Charset.forName("utf-8"));
        Log.i(TAG,inetSocketAddress+":"+string);
        if (string.equals("4")){
            asyncWriteMessage(inetSocketAddress,"response");
        }
    }

    @Override
    public void onServiceStatusConnectChanged(String inetSocketAddress, int statusCode) {
        Log.i(TAG,inetSocketAddress+":"+"statusCode = "+statusCode);
    }
}
