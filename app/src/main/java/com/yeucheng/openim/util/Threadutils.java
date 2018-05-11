package com.yeucheng.openim.util;

import android.os.Handler;

/**
 * Created by Administrator on 2018/5/8.
 */

public class Threadutils {
    /**
     * 运行在子线程
     * @param task
     */
    public static void runinThread(Runnable task){
        new Thread(task).start();
    }
    private static Handler mHandler = new Handler();

    /**
     * 运行在主线程
     * @param task
     */
    public static void runinUIThread(Runnable task){
        mHandler.post(task);
    }

}
