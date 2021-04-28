package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.parking.ManageActivity.SocketActivity;

public class UserInfoActivity extends SocketActivity {

    private final static String TAG = "UserInfoActivity";

    private TextView personName;
    private TextView personTel;
    private TextView personCar;
    private TextView personCarId;
    private TextView personId;
    private TextView personMail;

    private String userName;
    private String userTel;
    private String userCar;
    private String userCarId;
    private String userId;
    private String userMail;



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
        return R.layout.activity_user_info2;
    }

    @Override
    protected void initView() {

        personName.setText(userName);
        personTel.setText(userTel);
        personCar.setText(userCar);
        personCarId.setText(userCarId);
        personId.setText(userId);
        personMail.setText(userMail);
    }

    @Override
    protected void initData() {

        personName = findViewById(R.id.user_name);
        personTel = findViewById(R.id.set_name);
        personCar = findViewById(R.id.car_name);
        personCarId = findViewById(R.id.car_num);
        personId = findViewById(R.id.person_id);
        personMail = findViewById(R.id.user_mail);

        Intent intent = getIntent();

        userName = intent.getStringExtra("userName");
        userTel = intent.getStringExtra("userTel");
        userCar = intent.getStringExtra("userCar");
        userCarId = intent.getStringExtra("userCarId");
        userId = intent.getStringExtra("userId");
        userMail = intent.getStringExtra("userMail");


    }
}