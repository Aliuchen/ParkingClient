package com.example.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.PayPwdEditText;
import com.example.parking.information.SocketServiceInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.text.DecimalFormat;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Function.PackRequest;

public class PayActivity extends SocketActivity {

    private final static String TAG = "PayActivity";

    //private ReentrantLock  lock = new ReentrantLock();
   // private Condition condition = lock.newCondition();
    private Button btn_affirm;

    private Dialog  walletDialog;

    private String userTelNum;
    private String money;
    private String balance = "0.00";

    private TextView tv_moneyTel;
    private TextView tv_money;
    private TextView tv_50;
    private TextView tv_100;
    private TextView tv_200;
    private TextView tv_500;
    private TextView tv_1000;
    private EditText et_money;



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
        return R.layout.activity_pay;
    }

    @Override
    protected void initView() {

        btn_affirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                money =  tv_money.getText().toString();
                Log.i(TAG,"money is " + money);
                showEditPayPwdDialog();

            }
        });

        tv_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_money.setText("50");
            }
        });

        tv_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_money.setText("100");
            }
        });

        tv_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_money.setText("200");
            }
        });

        tv_500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_money.setText("500");
            }
        });

        tv_1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_money.setText("1000");
            }
        });

        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                tv_money.setText(s.toString());

            }
        });

    }

    @Override
    protected void initData() {

        btn_affirm = findViewById(R.id.tvPay);
        tv_moneyTel = findViewById(R.id.tx_moneyTel);
        tv_money = findViewById(R.id.tv_recharge_money);
        tv_50 = findViewById(R.id.tx_50);
        tv_100 = findViewById(R.id.tx_100);
        tv_200 = findViewById(R.id.tx_200);
        tv_500 = findViewById(R.id.tx_500);
        tv_1000 = findViewById(R.id.tx_1000);
        et_money = findViewById(R.id.et_custom);

        Intent intent = getIntent();
        userTelNum = intent.getStringExtra("telNum");

        if(userTelNum != null) {
            tv_moneyTel.setText(userTelNum);
        }

        addList(SocketServiceInfo.ACTIVITY_RECHARGEMONEY);

        mReciver = new MessageBackReciver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if(action.equals(SocketServiceInfo.ACTIVITY_RECHARGEMONEY)) {

                    byte[] lastResult = intent.getByteArrayExtra(SocketServiceInfo.MESSAGE_PAY);

                    try {
                        RechargeProto.RechargeResponse rechargeResponse = RechargeProto.RechargeResponse.newBuilder().mergeFrom(lastResult).build();
                        Log.i(TAG,"code is "+rechargeResponse.getCode()+"info is "+rechargeResponse.getInfo());
                        if(rechargeResponse.getCode() == 1) {
                            Intent data = new Intent();
                            balance = rechargeResponse.getBalance();
                            double tmp = Double.parseDouble(balance);
                            DecimalFormat df = new DecimalFormat("0.00");

                            Log.i(TAG,"balance is "+balance);
                            balance = df.format(tmp);
                            data.putExtra("balance",balance);
                            PayActivity.this.setResult(101,data);
                        }
                        showToast(rechargeResponse.getInfo());
                        //condition.signal();
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
        tx_money.setText(tv_money.getText().toString());
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

                RechargeProto.RechargeRequest rechargeRequest = RechargeProto.RechargeRequest.newBuilder()
                        .setUserTelNum(userTelNum).setMoney(Double.valueOf(money)).setPayPwd(str).build();

                byte[] request = rechargeRequest.toByteArray();
                byte[] toSend = PackRequest.packMsg(SocketServiceInfo.KEY_PAY,request);
                Log.i(TAG,"toSend message is"+toSend.toString());

                if(!iCommunication.sendMessage(toSend)) {

                    showToast("初始化网路失败!");

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitIO();
                    }
                });

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

    private void waitIO()  {

            //lock.lock();
            final PopupWindow popupWindow = new PopupWindow();
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup,null);
            popupWindow.setContentView(view);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);

           // condition.await();
            popupWindow.dismiss();
           // lock.unlock();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(balance.equals("0.00")) {
            Intent data = new Intent();
            data.putExtra("balance","0.00");
            PayActivity.this.setResult(101,data);
        }
    }

}