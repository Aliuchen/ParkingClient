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

import java.util.concurrent.ExecutionException;

import Function.PackRequest;

public class UserInfoSetActivity extends SocketActivity {

    private final static String TAG = "UserInfoSetActivity";

    private String userTel;

    private EditText personName;    // 姓名
    private EditText personTel;     // 手机号
    private EditText personId;     // 身份证号
    private EditText personCarName; // 车名
    private EditText personCarId;    // 车牌
    private EditText payPwd1;  // 支付密码1
    private EditText payPwd2;  // 支付密码2

    private Button infoVerify;     // button 确认

    private String userName;
    private String userId;
    private String carName;
    private String carId;
    private String pwd1;
    private String pwd2;
    private String tel;



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
        return R.layout.activity_user_info_set;
    }

    @Override
    protected void initView() {

        infoVerify.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) throws RemoteException, ExecutionException, InterruptedException {
                userName = personName.getText().toString();
                userId = personId.getText().toString();
                carName = personCarName.getText().toString();
                carId = personCarId.getText().toString();
                pwd1 = payPwd1.getText().toString();
                pwd2 = payPwd2.getText().toString();
                tel = personTel.getText().toString();

                if (userName.isEmpty()) {
                    showToast("请输入用户名");
                } else if (tel.isEmpty()) {
                    showToast("请输入手机号");
                } else if (userId.isEmpty()) {
                    showToast("请输入身份证号");
                } else if (carName.isEmpty()) {
                    showToast("请输入车名");
                } else if (carId.isEmpty()) {
                    showToast("请输入车名");
                } else if (pwd1.isEmpty()) {
                    showToast("请输入支付密码");
                } else if (pwd2.isEmpty()) {
                    showToast("请重复支付密码");
                } else if (!pwd1.equals(pwd2)) {
                    showToast("两次密码不一致");
                }else  {

                    if(iCommunication == null) {

                        showToast("初始化未成功");

                    } else  {

                        PersonInfoSetProto.PersonInfoSetRequest setRequest = PersonInfoSetProto.PersonInfoSetRequest.newBuilder().setUserTel(userTel)
                                .setPersonName(userName).setPersonTelNum(tel).setPersonId(userId)
                                .setCarName(carName).setCarId(carId).setPayPwd(pwd1).build();


                        byte[] request = setRequest.toByteArray();
                        byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_INFOSET,request);
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

        personName = findViewById(R.id.set_name);
        personTel = findViewById(R.id.tel_num);
        personId = findViewById(R.id.set_id);
        personCarName = findViewById(R.id.set_carName);
        personCarId = findViewById(R.id.set_carId);
        payPwd1 = findViewById(R.id.set_paypwd1);
        payPwd2 = findViewById(R.id.set_paypwd2);

        infoVerify = findViewById(R.id.btn_verify);

        Intent intent = getIntent();
        userTel = intent.getStringExtra("telNum");

        if(userTel != null) {
            personTel.setText(userTel);
        }


        addList(SocketServiceInfo.ACTIVITY_USERINFOSET);
        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_USERINFOSET)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_INFOSET);

                    try {
                        PersonInfoSetProto.PersonInfoSetResponse setResponse = PersonInfoSetProto.PersonInfoSetResponse.newBuilder().mergeFrom(lastResult).build();

                        if(setResponse.getCode() == 1) {

                            showToast(setResponse.getInfo());
                            FSActivity.flag = true;

                        }else  if(setResponse.getCode() == 2) {

                            showToast(setResponse.getInfo());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }


                }

            }
        };


    }
}