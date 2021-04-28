package com.example.parking.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OrderInfo {


    private String OrderId;       // 订单id
    private String userName;       // 用户名
    private String location;       // 位置
    private String startReserveTime;    // 开始预定时间
    private String startParkTime;       // 开始停车时间
    private String endTime;             // 结束时间
    private int parkTag;                // 停车标记   1 未停车  2  已停车 3 已离开
    private int payTag;                 // 支付标记   0 未支付  1 支付
    private double consume;             // 消费金额

    private long reserveTime;
    private long parkTime;
    private long leaveTime;
    private String area;

    public OrderInfo (String id,String name,String _location,String _area,long startTime) {

        this.OrderId = id;
        this.userName = name;
        this.location = _location;
        this.area = _area;
        this.reserveTime = startTime;
        parkTag = 0;
        this.startReserveTime = setFormatterTime(startTime);
        this.consume = 0.00;

    }

    public void setConsume(double consume) {
        this.consume += consume;
    }



    public void setParkTag(int parkTag) {
        this.parkTag = parkTag;
    }

    public void setPayTag(int payTag) {
        this.payTag = payTag;
    }


    public void setLeaveTime(long leaveTime) {

        this.leaveTime = leaveTime;
        this.endTime = setFormatterTime(leaveTime);
    }

    public void setParkTime(long parkTime) {
        this.parkTime = parkTime;
        this.startParkTime = setFormatterTime(parkTime);
    }

    public String setFormatterTime(long time) {

        if(time < 0) {
            return "";
        }


        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return  formatter.format(date);
    }

    public boolean isPay(long firstTime,long _endTime,long result) {

        long minuteF = TimeUnit.MILLISECONDS.toMinutes(firstTime);
        long minuteE = TimeUnit.MILLISECONDS.toMinutes(_endTime);

        return minuteE - minuteF >= result ? true : false;

    }

    public double getConsume() {
        return consume;
    }

    public int getParkTag() {
        return parkTag;
    }

    public int getPayTag() {
        return payTag;
    }

    public long getLeaveTime() {
        return leaveTime;
    }

    public long getParkTime() {
        return parkTime;
    }

    public long getReserveTime() {
        return reserveTime;
    }

    public String getArea() {
        return area;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getOrderId() {
        return OrderId;
    }

    public String getStartParkTime() {
        return startParkTime;
    }

    public String getStartReserveTime() {
        return startReserveTime;
    }

    public String getUserName() {
        return userName;
    }

    public void toDB() {


    }
}
