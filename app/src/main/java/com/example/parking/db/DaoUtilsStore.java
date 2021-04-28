package com.example.parking.db;

import com.example.parking.Application.MapApplication;
import com.example.parking.greendao.OrderDao;

public class DaoUtilsStore {
    private volatile static DaoUtilsStore instance = new DaoUtilsStore();
    private CommonDaoUtils<Order> mUserDaoUtils;

    public static DaoUtilsStore getInstance() {
        return instance;
    }

    private DaoUtilsStore() {

        OrderDao orderDao = MapApplication.getDaoSession().getOrderDao();
        mUserDaoUtils = new CommonDaoUtils<>(Order.class, orderDao);
    }

    public CommonDaoUtils<Order> getOrderDaoUtils() {
        return mUserDaoUtils;
    }

}
