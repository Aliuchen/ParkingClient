package com.example.parking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.parking.ManageActivity.BaseActivity;
import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.IOSocket.SocketService;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import Function.PackRequest;

public class LoginActivity extends SocketActivity {


    private static final String TAG = "LoginActivity";
    private EditText et_userName;
    private EditText et_passWord;
    private EditText et_auth;


    private Button  btn_login;
    private Button  btn_forgetPassword;
    private Button  btn_register;

    private static String err_infoUserName_NULL = "手机号为空！";
    private static String err_infoPassWord_NULL = "密码为空！";
    private static String err_infoAuth_NULL = "验证码为空！";
    private static String err_infoAuth = "验证码错误！";
    private static String err_loginFail = "账号或密码错误！";
    private static String err_noRegister = "该手机号为注册!";
    private static String ok_login = "登录成功";
    private static String err_init = "初始化失败";


    private String uName;
    private String uPassword;
    private static FileOutputStream out;


    private Intent intent;

    private com.againstsky.verificationcode.VerificationCodeView codeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

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
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

        btn_login.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) throws ExecutionException, InterruptedException {

                uName = et_userName.getText().toString();
                uPassword = et_passWord.getText().toString();
                String uAuth = et_auth.getText().toString();

                    if (uName.isEmpty()) {

                        showToast(err_infoUserName_NULL);

                    } else if (uPassword.isEmpty()) {

                        showToast(err_infoPassWord_NULL);

                    } else if (uAuth.isEmpty()) {

                        showToast(err_infoAuth_NULL);

                    } else  if(uAuth.equals(codeView.getVerificationCode())) {

                        btn_login.setText("正在登录 ");

                        if(iCommunication == null) {

                            showToast(err_init);
                            btn_login.setText("登录");
                        }else {

                            LoginProto.LoginRequest loginRequest = LoginProto.LoginRequest.newBuilder()
                                    .setUsername(uName).setPassword(uPassword).build();
                            byte[] request = loginRequest.toByteArray();


                            byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_LOGIN,request);

                            Log.i(TAG,"toSend message is"+toSend.toString());

                            if(!iCommunication.sendMessage(toSend)) {

                                showToast("初始化网路失败!");

                            }

                        }
                    }
                }

        });

        btn_forgetPassword.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view)  {
                Intent intent = new Intent(LoginActivity.this, FindpasswdActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view)  {
                Intent intent = new Intent(LoginActivity.this, registerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {


        et_userName = (EditText) findViewById(R.id.et_username);
        et_passWord = (EditText) findViewById(R.id.et_password);
        et_auth = (EditText) findViewById(R.id.et_auth);


        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_forgetPassword = (Button) findViewById(R.id.btn_forgetPassword);

        codeView = (com.againstsky.verificationcode.VerificationCodeView) findViewById(R.id.code_view);

        codeView.setPointInterfere(true);
        codeView.setLineInterfere(true);
        codeView.setLineInterfereQuantity(8);  // 线数量
        codeView.setPointInterfereQuantity(200);  // 点数量

        codeView.setMatchCase(false);  // 区分大小写


        addList(SocketServiceInfo.ACTIVITY_LOGIN);
        //给全局消息接收器赋值
        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(SocketServiceInfo.ACTIVITY_LOGIN)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_LOGIN);


                    try {
                        LoginProto.LoginResponse response = LoginProto.LoginResponse.newBuilder().mergeFrom(lastResult).build();

                        if(response.getCode() == 1 ) {

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(LoginActivity.this, FSActivity.class);
                                    startActivity(intent);
                                }
                            });

                            thread.start();
                            try {

                                String msg = "\r\n";
                                out = openFileOutput("userInfo",MODE_PRIVATE);
                                out.write(uName.getBytes());
                                out.write(msg.getBytes());
                                out.write(uPassword.getBytes());
                                out.flush();
                                out.close();
                            } catch (FileNotFoundException e) {

                                e.printStackTrace();

                            } catch (IOException e_1) {

                                e_1.printStackTrace();
                            }
                            LoginActivity.this.finish();

                        }
                        showToast(response.getInfo());
                        if(response.getCode() != 1) {
                            btn_login.setText("登录");
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

    }
}
