package com.example.parking.information;


public class SocketServiceInfo {

    private static final String TAG = "SocketServiceInfo.class";


    // 服务器info
    public static final String HOST = "8.129.79.24";
    public static final int PORT = 6000;

    // 心跳包频率
    public static final long HEART_BEAT_RATE = 30 * 1000;

    // 服务器回复的tag
    public static final String MESSAGE_HEART = "RES_HEART";  // 心跳



    // 广播要通知的class
    public static final String ACTIVITY_LOGIN = "com.example.parking.LoginActivity";  // 登录页面
    public static final String ACTIVITY_REGISTER = "com.example.parking.registerActivity"; // 注册界面
    public static final String ACTIVITY_FUNC= "com.example.parking.functionActivity";
    public static final String ACTIVITY_FINDPWD = "com.example.parking.FindpasswdActivity";
    public static final String ACTIVITY_SELECTCAR = "com.example.parking.SelectCarportActivity"; // 预定车位界面



    //Intent 通信的 k-v
    public static final String KEY_LOGIN = "Login";  // 登录的head
    public static final String MESSAGE_LOGIN = "loginRes";  // 登录回复head

    public static final String KEY_FINDPWD = "FindPassWd";
    public static final String MESSAGE_FINDPWD = "findPassWdRes";

    public static final String KEY_REGISTER = "Register";
    public static final String MESSAGE_REGISTER = "registerRes"; // 注册回复

    public static final String KEY_SELECTCAR = "SelectCar";
    public static final String MESSAGE_SELECTCAR = "selectCarRes"; // 预定车位回复


}
