package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.parking.db.DaoUtilsStore;
import com.example.parking.db.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class test extends AppCompatActivity {
    private static  final String TAG = "test";

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.test_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = new Order();

                order.setOrderId("1234567");

                order.setUserName("15591485221");
                order.setLocation("赛格");
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String time = formatter.format(date);
                order.setStartParkTime(time);
                order.setStartReserveTime(time);
                order.setEndTime(time);
                order.setParkTag(1);
                order.setPayTag(1);
                order.setConsume(0.00);

                DaoUtilsStore.getInstance().getOrderDaoUtils().insert(order);
                Log.i(TAG,"插入数据");
            }
        });
    }
}