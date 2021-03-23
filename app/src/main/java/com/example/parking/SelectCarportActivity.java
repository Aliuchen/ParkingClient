package com.example.parking;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.SeatTable;
import com.example.parking.information.SocketServiceInfo;

import Function.PackRequest;


public class SelectCarportActivity extends SocketActivity {

    private SeatTable seatView;
    private String key = null;
    private TextView info;
    private Button reserve;

    private int carRow;
    private int carLine;

    private String longitude = null;
    private String latitude = null;

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

        // seatView.setData(8,15);

    }

    @Override
    protected void initData() {


        seatView = findViewById(R.id.seatView);
        info = findViewById(R.id.tx_info);
        reserve = findViewById(R.id.btn_reserve);
        reserve.setVisibility(View.GONE);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        longitude = intent.getStringExtra("longitude");
        latitude = intent.getStringExtra("latitude");

        seatView.setScreenName(key);
        seatView.setMaxSelected(1);//设置最多选中
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
                /*
                if(row==6&&column==6){
                    return true;
                }
                return false;

                 */
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
                reserve.setVisibility(View.VISIBLE);

                return  null;
            }

        });


        addList(SocketServiceInfo.ACTIVITY_SELECTCAR);

        SelectCarProto.CarRequest carRequest = SelectCarProto.CarRequest.newBuilder().setLongitude(longitude).setLatitude(latitude).build();

        byte[] request = carRequest.toByteArray();

        byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_SELECTCAR,request);

        if(!iCommunication.sendMessage(toSend)) {

            showToast("初始化网路失败!");

        }

        //给全局消息接收器赋值
        mReciver = new MessageBackReciver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(SocketServiceInfo.ACTIVITY_SELECTCAR)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_SELECTCAR);

                }
            }
        };
    }

}