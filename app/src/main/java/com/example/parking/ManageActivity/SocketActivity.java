package com.example.parking.ManageActivity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.parking.com.example.IOSocket.SocketService;
import com.example.parking.interfaces.ICommunication;

import java.util.LinkedList;
import java.util.List;

public abstract class SocketActivity extends BaseActivity {

    private static final String TAG = "SocketActivity";

    public MessageBackReciver mReciver;     // 监听到广播回调函数
    private IntentFilter mIntentFilter;     // 广播过滤器
    private Intent mServiceIntent;
    private LocalBroadcastManager localBroadcastManager;
    private List<String> listAction = new LinkedList<>();    // 存放监听的action


    //标记是否已经进行了服务绑定与全局消息注册
    private boolean flag;

    //通过调用该接口中的方法来实现数据发送(2)

    public ICommunication iCommunication;

    // bind 成功之后会回调此函数
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            iCommunication = (ICommunication) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iCommunication = null;
        }
    };

    public void addList(String msg) {

        listAction.add(msg);
    }

    @Override
    protected void onStart() {

        flag = false;
        if(mReciver != null) {

            flag = true;
            initSocket();
            // 注册广播
            localBroadcastManager.registerReceiver(mReciver,mIntentFilter);
            // 绑定服务
            bindService(mServiceIntent,conn,BIND_ABOVE_CLIENT);
        }

        super.onStart();
    }

    @Override
    protected void onDestroy() {

        if( flag ) {
            //解除绑定
            unbindService(conn);
            localBroadcastManager.unregisterReceiver(mReciver);
        }
        super.onDestroy();
    }

    public void initSocket() {

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mServiceIntent = new Intent(this, SocketService.class);
        mIntentFilter = new IntentFilter();
        //mIntentFilter.addAction(SocketServiceInfo.ACTIVITY_LOGIN);

        for(String v : listAction) {
            mIntentFilter.addAction(v);
        }



    }

    // 广播接收者
    public abstract class MessageBackReciver extends BroadcastReceiver {

        // 当接收到广播 此方法被回调
        @Override
        public abstract void onReceive(Context context, Intent intent);
    }
}
