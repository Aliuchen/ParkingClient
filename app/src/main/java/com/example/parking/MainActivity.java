package com.example.parking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.content.Intent;
import android.util.Log;


import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.IOSocket.SocketService;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Function.PackRequest;


public class MainActivity extends SocketActivity {


    private boolean isPermissionRequested;
    private FileInputStream in;
    private BufferedReader bufferedReader;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //设置是否显示标题栏
        setShowTitle(false);
        //设置是否显示状态栏
        setShowStatusBar(true);
        //是否允许屏幕旋转
        setAllowScreenRoate(true);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        requestPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(this ,SocketService.class));
        } else {
            context.startService(new Intent(context, SocketService.class));
        }

        Log.i(TAG,"startService.......");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //执行在主线程
                //启动主页面

                try {

                    String uName = null;
                    String uPassword = null;
                    in = MainActivity.this.openFileInput("userInfo");
                    bufferedReader = new BufferedReader(new InputStreamReader(in));
                    uName = bufferedReader.readLine();
                    uPassword = bufferedReader.readLine();

                    bufferedReader.close();
                    in.close();

                    if(iCommunication == null) {

                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        //关闭当前页面
                        finish();
                    } else {
                        LoginProto.LoginRequest loginRequest = LoginProto.LoginRequest.newBuilder()
                                .setUsername(uName).setPassword(uPassword).build();
                        byte[] request = loginRequest.toByteArray();

                        byte[] toSend = PackRequest.packMsg("Login",request);

                        if(!iCommunication.sendMessage(toSend)){

                            showToast("初始化网络失败");
                        }

                    }


                } catch (FileNotFoundException e) {

                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    //关闭当前页面
                    finish();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        },2000);

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {



        addList(SocketServiceInfo.ACTIVITY_LOGIN);

        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(SocketServiceInfo.ACTIVITY_LOGIN)) {

                    byte[] lastResult = intent.getByteArrayExtra("loginRes");

                    try {
                        LoginProto.LoginResponse response = LoginProto.LoginResponse.newBuilder().mergeFrom(lastResult).build();

                        if(response.getCode() == 1 ) {

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(MainActivity.this, FSActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            thread.start();
                        } else {
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            //关闭当前页面
                            finish();

                        }

                    }catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.FOREGROUND_SERVICE
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }
}
