package com.example.parking.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Order {

    @Id(autoincrement = true)
    private Long id;                // id


    @Unique
    @NotNull
    private String OrderId;       // 订单id

    private String userName;       // 用户名
    private String location;       // 位置
    private String startReserveTime;    // 开始预定时间
    private String startParkTime;       // 开始停车时间
    private String endTime;             // 结束时间
    private int parkTag;                // 停车标记   1 未停车  2  已停车 3 已离开
    private int payTag;                 // 支付标记   0 未支付  1 支付
    private double consume;
    @Generated(hash = 866886468)
    public Order(Long id, @NotNull String OrderId, String userName, String location,
            String startReserveTime, String startParkTime, String endTime,
            int parkTag, int payTag, double consume) {
        this.id = id;
        this.OrderId = OrderId;
        this.userName = userName;
        this.location = location;
        this.startReserveTime = startReserveTime;
        this.startParkTime = startParkTime;
        this.endTime = endTime;
        this.parkTag = parkTag;
        this.payTag = payTag;
        this.consume = consume;
    }
    @Generated(hash = 1105174599)
    public Order() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOrderId() {
        return this.OrderId;
    }
    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getStartReserveTime() {
        return this.startReserveTime;
    }
    public void setStartReserveTime(String startReserveTime) {
        this.startReserveTime = startReserveTime;
    }
    public String getStartParkTime() {
        return this.startParkTime;
    }
    public void setStartParkTime(String startParkTime) {
        this.startParkTime = startParkTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public int getParkTag() {
        return this.parkTag;
    }
    public void setParkTag(int parkTag) {
        this.parkTag = parkTag;
    }
    public int getPayTag() {
        return this.payTag;
    }
    public void setPayTag(int payTag) {
        this.payTag = payTag;
    }
    public double getConsume() {
        return this.consume;
    }
    public void setConsume(double consume) {
        this.consume = consume;
    }
}
