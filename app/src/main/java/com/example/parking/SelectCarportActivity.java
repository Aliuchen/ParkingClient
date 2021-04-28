package com.example.parking;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.parking.Application.MapApplication;
import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.SeatTable;
import com.example.parking.db.OrderInfo;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Function.PackRequest;


public class SelectCarportActivity extends SocketActivity {

    private final static String TAG = "SelectCarportActivity";

    private SeatTable seatView;
    private String key = null;
    private TextView info;
    private Button reserve;
    private Button load;

    private int carRow = -1;
    private int carLine = -1;

    private int totalRow = 0;
    private int totalLine = 15;

    private String longitude = null;
    private String latitude = null;
    private String userTel = null;

    private int carNum = 0;

    private byte[] toSend;
    private byte[] isSet;

    private boolean flag = false;

    private Map<Integer,List<Integer>> locationMap;

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
        return R.layout.activity_select_carport;
    }

    @Override
    protected void initView() {

         //seatView.setData(8,15);

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!flag) {

                    showToast("您还为设置支付密码，请前往个人中心设置");

                } else  {

                    if(carRow == -1 || carLine == -1) {

                        showToast("还未选择车位");

                    } else if(!MapApplication.orderInfoQueue.isEmpty()) {

                        showToast("您有一辆车正在停用!");

                    } else {



                        String tmp[] = getLocation(carRow,carLine);

                        Log.i(TAG,"send carRow is   "+carRow+"carLine is   "+ carLine);

                        OrderCarProto.OrderRequest request = OrderCarProto.OrderRequest.newBuilder()
                                .setLongitude(longitude).setLatitude(latitude).setArea(tmp[0]).setLocation(tmp[1]).build();

                        byte[] requestSend = request.toByteArray();
                        byte[] requestToSend = PackRequest.packMsg(SocketServiceInfo.KEY_SELECTCAREND,requestSend);

                        Log.i(TAG,"toSend message is"+requestToSend.toString());

                        if(!iCommunication.sendMessage(requestToSend)) {

                            showToast("初始化网路失败!");

                        }

                    }
                }

            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!iCommunication.sendMessage(isSet)) {

                    showToast("初始化网路失败!");

                }

                if(!iCommunication.sendMessage(toSend)) {

                    showToast("初始化网路失败!");

                }
            }
        });


    }

    @Override
    protected void initData() {


        locationMap = new HashMap<>();
        seatView = findViewById(R.id.seatView);
        info = findViewById(R.id.tx_info);
        reserve = findViewById(R.id.btn_reserve);
        //reserve.setVisibility(View.GONE);
        reserve.setVisibility(View.INVISIBLE);
        load = findViewById(R.id.btn_load);
        load.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        longitude = intent.getStringExtra("longitude");
        latitude = intent.getStringExtra("latitude");
        userTel = intent.getStringExtra("telNum");

        Log.i(TAG,"经度"+longitude+"纬度"+latitude);

        seatView.setScreenName(key);
        seatView.setMaxSelected(1);//设置最多选中


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                seatView.setSeatChecker(new SeatTable.SeatChecker() {

                    // 设置不可用的
                    @Override
                    public boolean isValidSeat(int row, int column) {

                        return true;
                    }

                    @Override
                    public boolean isSold(int row, int column) {



                //return false;

                        return valIsExist(row,column);

                    }



                    @Override
                    public void checked(int row, int column) {

                    }

                    @Override
                    public void unCheck(int row, int column) {

                    }

                    @Override
                    public String[] checkedSeatTxt(int row, int column) {

                        carRow = row;
                        carLine = column;
                        String tmp[] = getLocation(row,column);
                        info.setText(tmp[0]+tmp[1]+"号");
                        /*
                        if(row == 0 || row == 1) {
                            if(row == 0) {
                                info.setText(areaN[0]+col+"号");
                                area = areaN[0];
                            }else {
                                info.setText(areaN[0]+colu+"号");
                                area = areaN[0];
                            }
                        } else if(row == 2 || row == 3) {
                            if(row == 2) {
                                info.setText(areaN[1]+col+"号");
                                area = areaN[1];
                            }else {
                                info.setText(areaN[1]+colu+"号");
                                area = areaN[1];
                            }

                        } else if(row == 4 || row == 5) {
                            if(row == 4) {
                                info.setText(areaN[2]+col+"号");
                                area = areaN[2];
                            }else {
                                info.setText(areaN[2]+colu+"号");
                                area = areaN[2];
                            }

                        }else {
                            if(row == 6) {
                                info.setText(areaN[3]+col+"号");
                                area = areaN[3];
                            }else {
                                info.setText(areaN[3]+colu+"号");
                                area = areaN[3];
                            }

                        }
                        */

                        return  null;
                    }

                });

            }

        });

        /*
        Thread thread = new Thread(new Runnable(){


            @Override
            public void run() {

                seatView.setSeatChecker(new SeatTable.SeatChecker() {

                    @Override
                    public boolean isValidSeat(int row, int column) {
                        if(column==2) {
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public boolean isSold(int row, int column) {

                        if(!locationMap.isEmpty()) {
                            if(getValFromMap(row) != -1) {
                                if (column == getValFromMap(row)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public void checked(int row, int column) {

                    }

                    @Override
                    public void unCheck(int row, int column) {

                    }

                    @Override
                    public String[] checkedSeatTxt(int row, int column) {

                        carRow = row;
                        carLine = column;
                        int col = column+1;
                        int colu = column+16;
                        if(row == 0 || row == 1) {
                            if(row == 0) {
                                info.setText("A区"+col+"号");
                            }else {
                                info.setText("A区"+colu+"号");
                            }
                        } else if(row == 2 || row == 3) {
                            if(row == 2) {
                                info.setText("B区"+col+"号");
                            }else {
                                info.setText("B区"+colu+"号");
                            }

                        } else if(row == 4 || row == 5) {
                            if(row == 4) {
                                info.setText("C区"+col+"号");
                            }else {
                                info.setText("C区"+colu+"号");
                            }

                        }else {
                            if(row == 6) {
                                info.setText("D区"+col+"号");
                            }else {
                                info.setText("D区"+colu+"号");
                            }

                        }

                        return  null;
                    }

                });

            }
        });

        thread.start();

         */

        /*
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                locationMap.put(5,5);

            }
        });
        thread1.start();

         */

        addList(SocketServiceInfo.ACTIVITY_SELECTCAR);

        //给全局消息接收器赋值
        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(SocketServiceInfo.ACTIVITY_SELECTCAR)) {

                    byte[] lastResultFirst = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_SELECTCAR);
                    byte[] lastResultEnd = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_SELECTCAREND);
                    byte[] lastResultIsSetPay = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_ISSETPAYPWD);
                    if(lastResultFirst != null &&lastResultFirst.length > 0) {

                        try{

                            SelectCarProto.CarResponse response = SelectCarProto.CarResponse.newBuilder().mergeFrom(lastResultFirst).build();
                            carNum = response.getCarNum();
                            totalRow = carNum/15;
                            if(response.getCode() == 1) {

                                List<SelectCarProto.CarInfo> carInfoList = response.getInfoList();
                                for(SelectCarProto.CarInfo info : carInfoList) {
                                    Log.i(TAG,"area is"+info.getArea());
                                    if(info.getArea().equals("A")) {
                                        List<Integer> list = info.getLocationList();
                                        for (Integer i : list) {
                                            valToMap((i-1)/15,(i-1)%15);
                                        }
                                    }else  if(info.getArea().equals("B")) {
                                        List<Integer> list = info.getLocationList();
                                        for (Integer i : list) {
                                            valToMap(((i-1)/15)+2,(i-1)%15);
                                        }

                                    } else if(info.getArea().equals("C")) {
                                        List<Integer> list = info.getLocationList();
                                        for (Integer i : list) {
                                            valToMap(((i-1)/15)+4,(i-1)%15);
                                        }

                                    }else {
                                        List<Integer> list = info.getLocationList();
                                        for (Integer i : list) {
                                            valToMap(((i-1)/15)+6,(i-1)%15);
                                        }

                                    }
                                }

                            }
                            Log.i(TAG,"code is "+response.getCode());
                            Log.i(TAG,"carNum is"+carNum+"totalRow is "+totalRow);
                            /*
                            Thread thread2 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    setSeatView();
                                }
                            });
                            thread2.start();

                             */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setSeatView();
                                }
                            });
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        intent.removeExtra(SocketServiceInfo.MESSAGE_SELECTCAR);
                    }

                    if(lastResultEnd != null && lastResultEnd.length > 0) {

                        try {
                            final OrderCarProto.OrderResponse response = OrderCarProto.OrderResponse.newBuilder().mergeFrom(lastResultEnd).build();

                            if(response.getCode() == 1) {

                                createOrderInfo(carRow,carLine);
                                valToMap(carRow,carLine);
                                showToast(response.getInfo());

                            } else if(response.getCode() == 2) {

                                valToMap(carRow,carLine);

                                showToast(response.getInfo());
                            }else if(response.getCode() == 3) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int tmp[] = getRowLine(response.getArea(),response.getLocation());
                                        valToMap(tmp[0],tmp[1]);
                                    }
                                });
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }

                    }

                    if(lastResultIsSetPay != null && lastResultIsSetPay.length > 0) {

                        try {
                            IsSetPayPwdProto.IsSetPayPwdResponse response = IsSetPayPwdProto.IsSetPayPwdResponse.newBuilder().mergeFrom(lastResultIsSetPay).build();

                            if(response.getIsSet() == 1) {
                                flag = true;
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        SelectCarProto.CarRequest carRequest = SelectCarProto.CarRequest.newBuilder().setLongitude(longitude).setLatitude(latitude).build();

        byte[] request = carRequest.toByteArray();

        toSend = PackRequest.packMsg(SocketServiceInfo.KEY_SELECTCAR,request);

        Log.i(TAG,"message size is"+toSend.length);

        IsSetPayPwdProto.IsSetPayPwdRequest payPwdRequest = IsSetPayPwdProto.IsSetPayPwdRequest.newBuilder().setUserTel(userTel).build();

        request = payPwdRequest.toByteArray();
        isSet = PackRequest.packMsg(SocketServiceInfo.KEY_ISSETPAYPWD,request);
    }

    private void valToMap(int row,int line) {

        synchronized (locationMap) {
            if(locationMap.get(row) != null) {
                List<Integer> list = locationMap.get(row);
                list.add(line);
            }else {
                List<Integer> list = new LinkedList<>();
                list.add(line);
                locationMap.put(row,list);
            }
        }

    }


    private void setSeatView() {

        seatView.setData(totalRow,totalLine);

        load.setVisibility(View.GONE);
        reserve.setVisibility(View.VISIBLE);
    }

    private String [] getLocation(int row, int col) {
        String tmp[] = {"A","1"};

        if(row == 0 || row == 1) {
            if(row == 0) {
                tmp[0] = "A";
                tmp[1] = String.valueOf(col+1);

            }else {

                tmp[0] = "A";
                tmp[1] = String.valueOf(col+16);

            }
        } else if(row == 2 || row == 3) {

            if (row == 2) {

                tmp[0] = "B";
                tmp[1] = String.valueOf(col + 1);

            } else {

                tmp[0] = "B";
                tmp[1] = String.valueOf(col + 16);

            }
        }else if(row == 4 || row == 5) {
            if(row == 4) {
                tmp[0] = "C";
                tmp[1] = String.valueOf(col + 1);
            }else {
                tmp[0] = "C";
                tmp[1] = String.valueOf(col + 16);
            }

        }else {
            if(row == 6) {
                tmp[0] = "D";
                tmp[1] = String.valueOf(col + 1);
            }else {
                tmp[0] = "D";
                tmp[1] = String.valueOf(col + 16);
            }

        }


        return tmp;



    }

    private  int [] getRowLine(String area,String location) {
        int tmp[] = {1,1};

        Integer i = new Integer(location);
        int num = i.intValue();

        if(area.equals("A")) {
            if(num < 16) {
                tmp[0] = 0;
                tmp[1] = num-1;
            } else {
                tmp[0] = 1;
                tmp[1] = num - 16;
            }
        } else if(area.equals("B")) {
            if(num < 16) {
                tmp[0] = 2;
                tmp[1] = num-1;
            } else {
                tmp[0] = 3;
                tmp[1] = num - 16;
            }
        }else if(area.equals("C")) {
            if(num < 16) {
                tmp[0] = 4;
                tmp[1] = num-1;
            } else {
                tmp[0] = 5;
                tmp[1] = num - 16;
            }
        }else if(area.equals("D")) {
            if(num < 16) {
                tmp[0] = 6;
                tmp[1] = num-1;
            } else {
                tmp[0] = 7;
                tmp[1] = num - 16;
            }
        }
        return tmp;

    }

    private boolean valIsExist(int col , int line) {

        synchronized (locationMap) {
            if(locationMap.get(col) != null) {
                List<Integer> list = locationMap.get(col);

                for(Integer v : list) {
                    if(v.equals(line)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void createOrderInfo(int row,int line) {

        String tmp[] = getLocation(row,line);
        String area = tmp[0]+"区"+tmp[1]+"号";

        // String id,String name,String _location,String startTime

        long currentTime = System.currentTimeMillis();
        String id = userTel+Long.toString(currentTime);
        Log.i(TAG,"id is  "+id +"area is "+area);
        String location = key;
        OrderInfo orderInfo = new OrderInfo(id,userTel,area,location,currentTime);
        MapApplication.orderInfoQueue.add(orderInfo);

    }

}