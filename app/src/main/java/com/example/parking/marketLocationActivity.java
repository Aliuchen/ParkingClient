package com.example.parking;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.parking.ManageActivity.SocketActivity;


public class marketLocationActivity extends SocketActivity implements OnGetGeoCoderResultListener {

    private final static String TAG = "marketLocationActivity";

    private TextView location;
    private Button reserve;
    private BaiduMap mBaiduMap = null;
    private MapView mMapView = null;

    private GeoCoder mSearch = null;


    private String city = null;
    private String key = null;
    private String dis = null;

    private String longitude = null;
    private String latitude = null;

    private BitmapDescriptor mbitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);


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
        return R.layout.activity_market_location;
    }

    @Override
    protected void initView() {

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SelectCarportActivity.class);
                intent.putExtra("key",key);
                intent.putExtra("longitude",longitude);
                intent.putExtra("latitude",latitude);
                startActivity(intent);
            }
        });

        location.setText(city+dis+key);

        searchProcess();

    }

    @Override
    protected void initData() {



        location = findViewById(R.id.tx_location);
        reserve = findViewById(R.id.btn_SelectCarPort);
       // reserve.setVisibility(View.GONE);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");

        dis = intent.getStringExtra("dis");
        key = intent.getStringExtra("key");

        int index = city.indexOf("=");
        city = city.substring(index+1);
        index = dis.indexOf("=");
        dis = dis.substring(index+1);
        index = key.indexOf("=");
        key = key.substring(index+1);


        showToast(city+key);



    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
          showToast("抱歉未找到结果");
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(mbitmap));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

        longitude = String.valueOf(result.getLocation().longitude);
        latitude = String.valueOf(result.getLocation().latitude);
        Log.i(TAG,"经度"+result.getLocation().longitude+ "  纬度"+result.getLocation().latitude);
        //showToast("经度"+result.getLocation().latitude+ "  纬度"+result.getLocation().longitude);

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18.5f).build()));
    }



    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    private void searchProcess() {

        if(city == null || key == null) {
            showToast("没有目的地");
            return;
        }
        mSearch.geocode(new GeoCodeOption()
                .city(city)// 城市
                .address(key)); // 地址
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mbitmap.recycle();
        // 释放检索对象
        mSearch.destroy();
        // 清除所有图层
        mBaiduMap.clear();
        // 在activity执行onDestroy时必须调用mMapView. onDestroy ()
        mMapView.onDestroy();
    }
}