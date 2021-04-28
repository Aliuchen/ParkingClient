package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.SeatTable;

public class LocationActivity extends SocketActivity {

    private SeatTable seatA;
    private SeatTable seatB;
    private SeatTable seatC;
    private SeatTable seatD;

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
        return R.layout.activity_location;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

        seatA = findViewById(R.id.seatViewA);
        seatB = findViewById(R.id.seatViewB);
        seatC = findViewById(R.id.seatViewC);
        seatD = findViewById(R.id.seatViewD);

        seatA.setScreenName("A区");
        seatB.setScreenName("B区");
        seatC.setScreenName("C区");
        seatD.setScreenName("D区");

        seatA.setMaxSelected(1);
        seatB.setMaxSelected(1);
        seatC.setMaxSelected(1);
        seatD.setMaxSelected(1);

        seatA.setSeatChecker(new SeatTable.SeatChecker(){

            @Override
            public boolean isValidSeat(int row, int column) {
                return false;
            }

            @Override
            public boolean isSold(int row, int column) {
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
                return new String[0];
            }
        });
        seatB.setSeatChecker(new SeatTable.SeatChecker(){

            @Override
            public boolean isValidSeat(int row, int column) {
                return false;
            }

            @Override
            public boolean isSold(int row, int column) {
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
                return new String[0];
            }
        });

        seatC.setSeatChecker(new SeatTable.SeatChecker(){

            @Override
            public boolean isValidSeat(int row, int column) {
                return false;
            }

            @Override
            public boolean isSold(int row, int column) {
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
                return new String[0];
            }
        });
        seatD.setSeatChecker(new SeatTable.SeatChecker(){

            @Override
            public boolean isValidSeat(int row, int column) {
                return false;
            }

            @Override
            public boolean isSold(int row, int column) {
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
                return new String[0];
            }
        });

        seatA.setData(4,3);
        seatB.setData(4,3);
        seatC.setData(4,3);
        seatD.setData(4,3);
    }
}