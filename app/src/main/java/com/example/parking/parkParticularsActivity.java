package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.parking.Application.MapApplication;
import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.PayPwdEditText;
import com.example.parking.db.DaoUtilsStore;
import com.example.parking.db.Order;
import com.example.parking.db.OrderInfo;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import Function.PackRequest;

public class parkParticularsActivity extends SocketActivity {

    private static final String TAG = "parkParticularsActivity";

    private TextView area;
    private TextView location;
    private TextView payStat;
    private TextView startReserveTime;
    private TextView startParkTime;
    private TextView endTime;

    private Button unsubscribe;
    private Button park;
    private Button pay;

    private Dialog  walletDialog;

    private double  money = 0.00;


    private String userTel;
    private OrderInfo orderInfo;

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
        return R.layout.activity_park_particulars;
    }

    @Override
    protected void initView() {

        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long currentTime = System.currentTimeMillis();
                long STime = orderInfo.getReserveTime();
                setCurrentMoney(currentTime,STime);
                orderInfo.setParkTime(currentTime);
                orderInfo.setLeaveTime(currentTime);
                orderInfo.setParkTag(1);
                showEditPayPwdDialog();
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                long STime = orderInfo.getReserveTime();
                setCurrentMoney(currentTime,STime);
                orderInfo.setParkTime(currentTime);
                orderInfo.setParkTag(2);
                startParkTime.setText(orderInfo.getStartParkTime());
                unsubscribe.setEnabled(false);
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long currentTime = System.currentTimeMillis();
                long PTime = orderInfo.getParkTime();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime)-TimeUnit.MILLISECONDS.toMinutes(PTime);
                Log.i(TAG,"minutes is" + minutes+"parkTime is "+PTime);
                money =  minutes*0.1;
                orderInfo.setLeaveTime(currentTime);
                orderInfo.setParkTag(3);
                showEditPayPwdDialog();
                park.setEnabled(false);

            }
        });

    }

    @Override
    protected void initData() {

        area = findViewById(R.id.rcv_title);
        location = findViewById(R.id.rcv_location);
        payStat = findViewById(R.id.rcv_zt);
        startReserveTime = findViewById(R.id.startReserveTime);
        startParkTime = findViewById(R.id.startParkTime);
        endTime = findViewById(R.id.endTime);


        unsubscribe = findViewById(R.id.btn_unsubscribe);
        park = findViewById(R.id.btn_park);
        pay = findViewById(R.id.rcv_dd);

        Intent intent = getIntent();
        userTel = intent.getStringExtra("telNum");

        if(MapApplication.orderInfoQueue.isEmpty()) {

            unsubscribe.setVisibility(View.INVISIBLE);
            park.setVisibility(View.INVISIBLE);
            pay.setVisibility(View.INVISIBLE);
        } else  {

            orderInfo = MapApplication.orderInfoQueue.peek();
            area.setText(orderInfo.getArea());
            location.setText(orderInfo.getLocation());
            startReserveTime.setText(orderInfo.getStartReserveTime());
            unsubscribe.setVisibility(View.VISIBLE);
            park.setVisibility(View.VISIBLE);
            pay.setVisibility(View.VISIBLE);
        }

        addList(SocketServiceInfo.ACTIVITY_SUBMITORDER);
        mReciver = new MessageBackReciver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_SUBMITORDER)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_SUBMIT);

                    try{

                        OrderProto.OrderSubmitResponse orderSubmitResponse = OrderProto.OrderSubmitResponse.newBuilder().mergeFrom(lastResult).build();
                        if(orderSubmitResponse.getCode() == 1) {

                            orderInfo.setConsume(money);
                            orderInfo.setPayTag(1);

                            Order order = new Order();
                            order.setOrderId(orderInfo.getOrderId());
                            order.setUserName(userTel);
                            order.setLocation(orderInfo.getArea());
                            order.setStartParkTime(orderInfo.getStartParkTime());
                            order.setStartReserveTime(orderInfo.getStartReserveTime());
                            order.setEndTime(orderInfo.getEndTime());
                            order.setParkTag(orderInfo.getParkTag());
                            order.setPayTag(orderInfo.getPayTag());
                            order.setConsume(orderInfo.getConsume());
                            DaoUtilsStore.getInstance().getOrderDaoUtils().insert(order);
                            if(MapApplication.orderInfoQueue.poll() == null) {

                                showToast("订单本地缓存失败");
                            }
                        }

                        showToast(orderSubmitResponse.getInfo());

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

    }

    //将此方法放在按钮的点击事件中即可弹出输入支付密码页面
    private void showEditPayPwdDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_et_paypwd, null);
        walletDialog = new Dialog(this, R.style.walletFrameWindowStyle);
        walletDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final Window window = walletDialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        //紧贴软键盘弹出
        wl.gravity = Gravity.BOTTOM;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        walletDialog.onWindowAttributesChanged(wl);
        walletDialog.setCanceledOnTouchOutside(false);
        walletDialog.show();
        final TextView tx_money = view.findViewById(R.id.tv_money);
        DecimalFormat df = new DecimalFormat("0.00");
        double total = orderInfo.getConsume()+money;
        tx_money.setText(df.format(total));
        final PayPwdEditText ppet = (PayPwdEditText) view.findViewById(R.id.dialog_ppet);
        //调用initStyle方法创建你需要设置的样式
        ppet.initStyle(R.drawable.edit_num_bg, 6, 0.33f, R.color.yellow, R.color.yellow, 30);
        ppet.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener() {
            @Override
            public void onFinish(String str) {//密码输入完后的回调

                //手动收起软键盘
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ppet.getWindowToken(), 0);
                //支付密码输入框消失
                walletDialog.dismiss();

                startParkTime.setText(orderInfo.getStartParkTime());
                endTime.setText(orderInfo.getEndTime());

                OrderProto.OrderSubmitRequest orderSubmitRequest = OrderProto.OrderSubmitRequest.newBuilder()
                        .setUserTel(userTel).setOrderId(orderInfo.getOrderId()).setLocation(orderInfo.getArea())
                        .setStartReserveTime(orderInfo.getStartReserveTime()).setStartParkingTime(orderInfo.getStartParkTime())
                        .setEndTime(orderInfo.getEndTime()).setParkTag(orderInfo.getParkTag()).setPayTag(orderInfo.getPayTag())
                        .setConsume(orderInfo.getConsume()+money).setPwd(str).build();

                byte[] request = orderSubmitRequest.toByteArray();
                byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_SUBMIT,request);

                Log.i(TAG,"toSend message is"+toSend.toString());

                if(!iCommunication.sendMessage(toSend)) {

                    showToast("初始化网路失败!");

                }

            }
        });
        //延迟弹起软键盘，使PayPwdEditText获取焦点
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ppet.setFocus();
            }
        }, 100);
    }
    private void setCurrentMoney (long currentTime,long STime) {


        double _money  = 0.00;
        if(orderInfo.isPay(STime,currentTime,1)) {

            _money = 2.00;
            orderInfo.setConsume(_money);
        }
    }
}