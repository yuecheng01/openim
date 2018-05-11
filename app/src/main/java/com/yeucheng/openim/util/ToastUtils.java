package com.yeucheng.openim.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/5/8.
 */

public class ToastUtils {
    public static void showToast(final Context context, final String text){
        Threadutils.runinUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
