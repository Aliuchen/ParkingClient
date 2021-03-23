package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.IOSocket.SocketService;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.concurrent.ExecutionException;

import Function.PackRequest;

public class FindpasswdActivity extends SocketActivity {

    private static final String TAG = "FindpasswdActivity";

    private Button btn_findPwd;
    private EditText et_tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //设置是否显示标题栏
        setShowTitle(false);
        //设置是否显示状态栏
        setShowStatusBar(true);
        //是否允许屏幕旋转
        setAllowScreenRoate(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_findpasswd;
    }

    @Override
    protected void initView() {

        /*
        btn_findPwd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                String telNumber = et_tel.getText().toString();
                if(telNumber.length() != 11) {
                    showToast("请输入正确的电话号码!");
                }else {

                    FindPassWd.FindPwdRequest findPwdRequest = FindPassWd.FindPwdRequest.newBuilder().setTel(telNumber).build();
                    byte[] request = findPwdRequest.toByteArray();
                    byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_FINDPWD,request);

                    if(!iCommunication.sendMessage(toSend)) {

                        showToast("初始化网路失败!");

                    }
                }

            }
        });

         */

        btn_findPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telNumber = et_tel.getText().toString();
                if(telNumber.length() != 11) {
                    showToast("请输入正确的电话号码!");
                }else {

                    FindPassWd.FindPwdRequest findPwdRequest = FindPassWd.FindPwdRequest.newBuilder().setTel(telNumber).build();
                    byte[] request = findPwdRequest.toByteArray();
                    byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_FINDPWD,request);


                        if(!iCommunication.sendMessage(toSend)) {

                            showToast("初始化网路失败!");

                        }
                }

            }
        });
    }

    @Override
    protected void initData() {


        et_tel = (EditText)findViewById(R.id.et_username);
        btn_findPwd =(Button)findViewById(R.id.btn_find);

        addList(SocketServiceInfo.ACTIVITY_FINDPWD);

        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_FINDPWD)) {
                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_FINDPWD);

                    try{
                        FindPassWd.FindPwdResponse findPwdResponse = FindPassWd.FindPwdResponse.newBuilder().mergeFrom(lastResult).build();

                        int code = findPwdResponse.getCode();
                        if(code == 1) {
                            showToast("密码已经发送到该号码绑定的邮箱中");
                        }else if(code == 2 ) {
                            showToast("该用户未注册");
                        }else if(code == 3){
                            showToast("服务器错误");
                        }else  {
                            showToast("密码找回失败");
                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
