package com.example.parking;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.bean.WeatherBean;
import com.example.parking.com.example.style.SlideMenu;
import com.example.parking.information.SocketServiceInfo;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import Function.PackRequest;


public class FSActivity extends SocketActivity  {


    private static final String TAG = "FSActivity";

    private AttributeSet mySet;
    public static boolean flag = true;


    private ImageView mIvMore;
    private SlideMenu slideMenu;

    // 温度  城市  当天天气  风向  温度范围  时间
    private TextView tempTv,cityTv,conditionTv,windTv,tempRangeTv,dateTv;
    // 天气图标
    private ImageView dayIv ;

    private TextView parking;

    private TextView carInfo;

    private TextView reserve;

    public LocationClient mLocationClient = null;

    private ImageView wallet;  // 钱包
    private ImageView member;   // 会员
    private ImageView myCar;     // 我的汽车
    private ImageView historyOrder;  // 历史订单
    private ImageView personInfo;  // 个人信息
    private ImageView personSet;   // 个人设置
    private String userTelNum;
    private TextView personTelNum;
    /*
    private String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    private String url2 = "&output=json&ak=v3mVu1BW6ROKH7Xl0Gp6sDNAyhpQ1PU2";


     */

    private TextView userBalance;
    private TextView userMember;
    private TextView userCar;

    private String keyUserName = "";
    private String keyUserId = "";
    private String keyUserCar = "";
    private String keyUserCarId = "";
    private String keyUserMember = "";
    private String keyUserBalance = "";
    private String keyUserMail = "";


    private String cityName;
    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置是否显示标题栏
        setShowTitle(false);
        //设置是否显示状态栏
        setShowStatusBar(true);
        //是否允许屏幕旋转
        setAllowScreenRoate(true);
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener); //注册监听函数
        initLocation();
        mLocationClient.start();//调用LocationClient的start()方法，便可发起定位请求


    }

    @Override
    protected int initLayout() {
        return R.layout.activity_f_s;
    }

    @Override
    protected void initView() {



        // 点击侧滑
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(keyUserName.isEmpty()) {
                    flag = true;
                }

                if(flag) {


                    if(iCommunication == null) {
                        showToast("初始化失败");
                    }else  {

                        PersonInfoProto.PersonInfoRequest  infoRequest = PersonInfoProto.PersonInfoRequest.newBuilder()
                                .setTelNum(userTelNum).build();
                        byte[] request = infoRequest.toByteArray();
                        byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_LOADINFO,request);

                        Log.i(TAG,"toSend message is"+toSend.toString());

                        if(!iCommunication.sendMessage(toSend)) {

                            showToast("初始化网路失败!");

                        }

                        flag = false;
                    }

                }

                slideMenu.switchMenu();

            }
        });

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FSActivity.this, FindCarportActivity.class);
                startActivity(intent);
            }
        });

        carInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FSActivity.this, parkParticularsActivity.class);
                intent.putExtra("telNum",userTelNum);
                //Intent intent = new Intent(FSActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("预定");

                Intent intent = new Intent(FSActivity.this, ReserveActivity.class);
                intent.putExtra("telNum",userTelNum);
                startActivity(intent);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FSActivity.this, PayActivity.class);
                intent.putExtra("telNum",userTelNum);
                startActivityForResult(intent,101);
            }
        });

        // 会员
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        myCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        historyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FSActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
        });


        personInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FSActivity.this, UserInfoActivity.class);
                intent.putExtra("userName",keyUserName);
                intent.putExtra("userTel",userTelNum);
                intent.putExtra("userCar",keyUserCar);
                intent.putExtra("userCarId",keyUserCarId);
                intent.putExtra("userId",keyUserId);
                intent.putExtra("userMail",keyUserMail);

                startActivity(intent);
            }
        });

        personSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FSActivity.this, UserInfoSetActivity.class);
                intent.putExtra("telNum",userTelNum);
                startActivity(intent);
            }
        });






    }

    @Override
    protected void initData() {

        mIvMore = findViewById(R.id.main_iv_person);
        slideMenu = findViewById(R.id.slideMenu);

        tempTv = findViewById(R.id.frag_tv_currenttemp);
        cityTv = findViewById(R.id.frag_tv_city);
        conditionTv = findViewById(R.id.frag_tv_condition);
        windTv = findViewById(R.id.frag_tv_wind);
        tempRangeTv = findViewById(R.id.frag_tv_temprange);
        dateTv = findViewById(R.id.frag_tv_date);
        dayIv = findViewById(R.id.frag_iv_today);

        parking = findViewById(R.id.find_car);

        carInfo = findViewById(R.id.car_info);

        reserve = findViewById(R.id.market);

        wallet = findViewById(R.id.money_goto);
        member = findViewById(R.id.vip_goto);
        myCar = findViewById(R.id.car_goto);
        historyOrder = findViewById(R.id.order_goto);
        personInfo = findViewById(R.id.person_goto);
        personSet = findViewById(R.id.set_goto);

        personTelNum = findViewById(R.id.user_name);
        userBalance = findViewById(R.id.menu_money);
        userMember = findViewById(R.id.menu_vip);
        userCar = findViewById(R.id.menu_car);



        Intent intent = getIntent();
        userTelNum = intent.getStringExtra("telNum");

        if(userTelNum != null) {
            personTelNum.setText(userTelNum);
        }

        addList(SocketServiceInfo.ACTIVITY_LOADUSERINFO);
        mReciver = new MessageBackReciver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_LOADUSERINFO)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_LOADINFO);

                    try {
                        PersonInfoProto.PersonInfoResponse  infoResponse = PersonInfoProto.PersonInfoResponse.newBuilder().mergeFrom(lastResult).build();
                        if(infoResponse.getCode() == 2) {

                            showToast("您还未设置个人信息");

                        } else  {



                            Log.i(TAG,"userName : "+infoResponse.getPersonName()+"userId : "+infoResponse.getUserId()+"carName: "+
                                    infoResponse.getCarName()+"carID : "+infoResponse.getCarId()+"member : "+infoResponse.getMember()
                            +"balance ："+infoResponse.getBalance());

                            keyUserName = infoResponse.getPersonName();
                            keyUserMember = infoResponse.getMember();
                            keyUserBalance = infoResponse.getBalance();
                            double money = Double.parseDouble(keyUserBalance);
                            DecimalFormat df = new DecimalFormat("0.00");
                            keyUserBalance = df.format(money);
                            keyUserCar = infoResponse.getCarName();
                            keyUserCarId = infoResponse.getCarId();
                            keyUserId = infoResponse.getUserId();
                            keyUserMail = infoResponse.getUserMail();

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                personTelNum.setText(keyUserName+"|"+userTelNum);
                                userBalance.setText(keyUserBalance);
                                userMember.setText(keyUserMember);
                                userCar.setText(keyUserCar);
                            }
                        });
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }


                }

            }
        };






    }


    /*
    private void loadData(String path) {


        RequestParams params = new RequestParams(path);
        x.http().get(params,this);


    }

     */

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /**可选，设置定位模式，默认高精度LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；LocationMode. Device_Sensors：仅使用设备；*/

        option.setCoorType("gcj02gcj02");
        /**可选，设置返回经纬度坐标类型，默认gcj02gcj02：国测局坐标；bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；
         海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标*/

        option.setScanSpan(60000);
        /**可选，设置发起定位请求的间隔，int类型，单位ms如果设置为0，则代表单次定位，即仅定位一次，默认为0如果设置非0，需设置1000ms以上才有效*/

        option.setOpenGps(true);
        /**可选，设置是否使用gps，默认false使用高精度和仅用设备两种定位模式的，参数必须设置为true*/

        option.setLocationNotify(true);
/**可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false*/

        option.setIgnoreKillProcess(true);
        /**定位SDK内部是一个service，并放到了独立进程。设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)*/

        option.SetIgnoreCacheException(false);
        /**可选，设置是否收集Crash信息，默认收集，即参数为false*/
        option.setIsNeedAltitude(true);/**设置海拔高度*/

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        /**可选，7.2版本新增能力如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/

        option.setEnableSimulateGps(false);
        /**可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false*/

        option.setIsNeedAddress(true);
        /**可选，设置是否需要地址信息，默认不需要*/

        mLocationClient.setLocOption(option);
        /**mLocationClient为第二步初始化过的LocationClient对象需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用*/

    }





    private void parseShowData(String result) {

        WeatherBean weatherBean = new Gson().fromJson(result,WeatherBean.class);
        WeatherBean.ResultsBean resultsBean = weatherBean.getResults().get(0);

        // 设置textview
        dateTv.setText(weatherBean.getDate());
        cityTv.setText(resultsBean.getCurrentCity());

        // 设置今天的天气情况
        WeatherBean.ResultsBean.WeatherDataBean today = resultsBean.getWeather_data().get(0);
        windTv.setText(today.getWind());
        tempRangeTv.setText(today.getTemperature());
        conditionTv.setText(today.getWeather());

        // 获取实时天气温度
        String [] split = today.getDate().split("：");
        String todayTemp = split[1].replace(")","");
        tempTv.setText(todayTemp+"℃");

        // 设置天气图片
        Picasso.with((this)).load(today.getDayPictureUrl()).into(dayIv);

    }


    /*
    @Override
    public void onSuccess(String result) {

        showToast("成功");

        parseShowData(result);
    }


    @Override
    public void onError(Throwable ex, boolean isOnCallback) {


        String msg = ex.getMessage();

        Log.e(TAG,"err is" +msg);
        showToast("msg");
    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }

     */



    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明


            cityName = location.getCity();
            String District = location.getDistrict();


            cityTv.setText(District);
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            /*
            String url = url1+cityName+url2;

            loadData(url);

             */

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 101) {


            try{


                String money = data.getStringExtra("balance");

                if(money != null) {

                    keyUserBalance = money;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            userBalance.setText(keyUserBalance);
                        }
                    });

                }

            } catch (NullPointerException e) {

                e.printStackTrace();
            }
        }

    }
}