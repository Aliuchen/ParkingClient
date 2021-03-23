package com.example.parking.com.example.style.com.example.util;


import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    private static Toast mToast;

    public static void showInfo(Context context,String info) {

        if(mToast == null) {

            mToast = Toast.makeText(context,info,Toast.LENGTH_SHORT);
        }else {

            mToast.setText(info);
        }

        mToast.show();


    }
}
