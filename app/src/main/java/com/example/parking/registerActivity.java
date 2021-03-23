package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.sql.BatchUpdateException;
import java.util.concurrent.ExecutionException;

import Function.PackRequest;

public class registerActivity extends SocketActivity {

    private static final String TAG = "registerActivity";
    private EditText et_userName;
    private EditText et_userPassWd_1;
    private EditText et_userPasswd_2;
    private EditText et_mail;
    private EditText et_auth;

    private Button btn_register;

    private com.againstsky.verificationcode.VerificationCodeView codeView;




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
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {

        btn_register.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) throws ExecutionException, InterruptedException {

                String uName = et_userName.getText().toString();
                String uPassWd1 = et_userPassWd_1.getText().toString();
                String uPassWd2 = et_userPasswd_2.getText().toString();
                String uMail = et_mail.getText().toString();
                String uAuth = et_auth.getText().toString();
                if(uName == null) {
                    showToast("账号不能为空");
                }else if(uPassWd1.isEmpty() || uPassWd2.isEmpty()) {
                    showToast("密码不能为空");
                }else if(!uPassWd1.equals(uPassWd2)) {
                    showToast("两次密码不一致");
                }else if(uAuth.isEmpty()) {
                    showToast("验证码为空");
                }else if(!uAuth.equals(codeView.getVerificationCode())) {
                    showToast("验证码不对");
                } else {

                    btn_register.setText("正在注册");

                    if(iCommunication == null) {

                        showToast("初始化失败");

                    }else {
                        RegisterProto.RegisterRequest registerRequest = RegisterProto.RegisterRequest.newBuilder().setUsername(uName)
                                .setPassword(uPassWd1).setUsermail(uMail).build();

                        byte[] request = registerRequest.toByteArray();
                        byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_REGISTER,request);

                        Log.i(TAG,"toSend message is"+toSend.toString());

                        if(!iCommunication.sendMessage(toSend)) {

                            showToast("初始化网路失败!");

                        }

                    }
                }

            }
        });
    }

    @Override
    protected void initData() {

        codeView = (com.againstsky.verificationcode.VerificationCodeView) findViewById(R.id.code_view);

        codeView.setPointInterfere(true);
        codeView.setLineInterfere(true);
        codeView.setLineInterfereQuantity(8);  // 线数量
        codeView.setPointInterfereQuantity(200);  // 点数量

        codeView.setMatchCase(false);  // 区分大小写

        et_userName = (EditText)findViewById(R.id.et_username);
        et_userPassWd_1 = (EditText)findViewById(R.id.et_password_1);
        et_userPasswd_2 = (EditText)findViewById(R.id.et_password_2);
        et_mail = (EditText)findViewById(R.id.et_mail);

        et_auth = (EditText)findViewById(R.id.et_auth);

        btn_register = (Button)findViewById(R.id.btn_login);

        addList(SocketServiceInfo.ACTIVITY_REGISTER);


        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_REGISTER)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_REGISTER);

                    try{

                        RegisterProto.RegisterResponse response = RegisterProto.RegisterResponse.newBuilder().mergeFrom(lastResult).build();

                        showToast(response.getInfo());

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                }

            }
        } ;

    }
}
