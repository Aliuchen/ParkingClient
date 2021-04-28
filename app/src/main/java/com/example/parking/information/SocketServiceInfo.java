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
    //public static final String ACTIVITY_FUNC= "com.example.parking.functionActivity";
    public static final String ACTIVITY_FINDPWD = "com.example.parking.FindpasswdActivity";
    public static final String ACTIVITY_SELECTCAR = "com.example.parking.SelectCarportActivity"; // 预定车位界面
    public static final String ACTIVITY_USERINFOSET = "com.example.parking.UserInfoSetActivity";  // 信息设置页面
    public static final String ACTIVITY_LOADUSERINFO = "com.example.parking.FSActivity";      // 加载用户信息
    public static final String ACTIVITY_RECHARGEMONEY = "com.example.parking.PayActivity" ; // 支付页面
    public static final String ACTIVITY_SUBMITORDER = "com.example.parking.parkParticularsActivity"; // 提交订单





    //Intent 通信的 k-v
    public static final String KEY_LOGIN = "Login";  // 登录的head
    public static final String MESSAGE_LOGIN = "loginRes";  // 登录回复head

    public static final String KEY_FINDPWD = "FindPassWd";
    public static final String MESSAGE_FINDPWD = "findPassWdRes";

    public static final String KEY_REGISTER = "Register";
    public static final String MESSAGE_REGISTER = "registerRes"; // 注册回复

    public static final String KEY_SELECTCAR = "SelectCarFirst";
    public static final String MESSAGE_SELECTCAR = "selectCarFirstRes"; // 预定车位初始化回复

    public static final String KEY_SELECTCAREND = "OrderCarEnd";
    public static final String MESSAGE_SELECTCAREND = "OrderCarEndRes"; // 预定车位预定回复

    public static final String KEY_ISSETPAYPWD = "IsSetPayPwd";
    public static final String MESSAGE_ISSETPAYPWD = "IsSetPayPwdRes";   // 是否设置支付密码

    public static final String KEY_INFOSET = "UserInfoSet";
    public static final String MESSAGE_INFOSET = "UserInfoSetRes";     // 个人信息设置


    public static final String KEY_LOADINFO = "LoadUserInfo";
    public static final String MESSAGE_LOADINFO = "LoadUserInfoRes";   //  加载用户信息

    public static final String KEY_PAY = "payMoney";
    public static final String MESSAGE_PAY = "payMoneyRes";    // 充值

    public static final String KEY_SUBMIT = "submitOrder";
    public static final String MESSAGE_SUBMIT = "submitOrderRes"; // 提交订单


}
