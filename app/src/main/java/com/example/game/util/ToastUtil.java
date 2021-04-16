package com.example.game.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    //防止多次连续点击导致提示时间过长
    public static Toast toast;
    public static void showMag(Context context,String msg){
        if (toast == null){
            toast = Toast.makeText(context,msg,Toast.LENGTH_LONG);
        }else{
            toast.setText(msg);
        }
        toast.show();
    };
}
