package com.yeucheng.openim.util;

import android.util.Log;

/**
 * Created by yuecheng on 2017/10/29.
 */

public class LogUtils {
    private static int v = 0;
    private static int d = 1;
    private static int i = 2;
    private static int w = 3;
    private static int e = 4;
    private static int TAG = 5;

    public static void setDeBug(boolean state) {
        if (state) TAG = -1;
    }

    public static void v(String tag, Object msg) {
        if (v > TAG) {
            Log.v(tag, "" + msg);
        }
    }

    public static void d(String tag, Object msg) {
        if (d > TAG) {
            Log.d(tag, "" + msg);
        }
    }

    public static void i(String tag, Object msg) {
        if (i > TAG) {
            Log.i(tag, "" + msg);
        }
    }

    public static void w(String tag, Object msg) {
        if (w > TAG) {
            Log.w(tag, "" + msg);
        }
    }

    public static void e(String tag, Object msg) {
        if (e > TAG) {
            Log.e(tag, "" + msg);
        }
    }

    public static void simpleLog(Object msg) {
        if (e > TAG) {
            Log.e("TestLog", "" + msg);
        }
    }
}
