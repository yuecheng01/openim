package com.yeucheng.openim.base;

import android.app.Application;

import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.Threadutils;
import org.litepal.LitePal;


/**
 * Created by Administrator on 2018/5/8.
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //litepal数据库操作框架
        LitePal.initialize(this);
        //开启日志打印
        LogUtils.setDeBug(true);
        //链接XMPPServer
        connectXmppServer();
    }

    private void connectXmppServer() {
        Threadutils.runinThread(new Runnable() {
            @Override
            public void run() {
                XmppConnection.getConnection();
            }
        });
    }
}
