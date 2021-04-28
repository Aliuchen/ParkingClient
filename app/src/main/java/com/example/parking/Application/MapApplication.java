package com.example.parking.Application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.parking.db.OrderInfo;
import com.example.parking.greendao.DaoMaster;
import com.example.parking.greendao.DaoSession;

import org.xutils.x;

import java.util.LinkedList;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MapApplication  extends Application {

    private static DaoSession daoSession;
    public static ConcurrentLinkedQueue<OrderInfo> orderInfoQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void onCreate() {

        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        x.Ext.init(this);
        //参数1：上下文
        //参数2：数据库名称
        //参数3：游标工厂类
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "parking_Client", null);
        //获取DataBase对象
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //创建DaoMaster对象，所需DateBase数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //创建DaoSession对象
        daoSession = daoMaster.newSession();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 主要是添加下面这句代码
        MultiDex.install(this);
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}
