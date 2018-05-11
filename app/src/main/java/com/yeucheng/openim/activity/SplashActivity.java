package com.yeucheng.openim.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.TextView;

import com.yeucheng.openim.R;
import com.yeucheng.openim.base.AbsBaseActivity;
import com.yeucheng.openim.base.XmppConnection;
import com.yeucheng.openim.service.IMService;
import com.yeucheng.openim.service.PushService;
import com.yeucheng.openim.sp.SharedPreferencesUtils;
import com.yeucheng.openim.sp.Sp_Save;
import com.yeucheng.openim.util.Threadutils;
import com.yeucheng.openim.util.ToastUtils;

import org.jivesoftware.smack.XMPPException;

import java.nio.file.Path;

public class SplashActivity extends AbsBaseActivity {

    @Override
    protected int setLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        Threadutils.runinThread(new Runnable() {
            @Override
            public void run() {
                //停留3秒
                SystemClock.sleep(3000);
                //跳转登录界面
                String userStr = (String) SharedPreferencesUtils.getParam(mContext, Sp_Save.LOGINUSERNAME,
                        "");
                String pwdStr = (String) SharedPreferencesUtils.getParam(mContext, Sp_Save
                                .LOGINPASSWORD,
                        "");
                if (!TextUtils.isEmpty(userStr) && !TextUtils.isEmpty(pwdStr)) {
                    try {
                        XmppConnection.getConnection().login(userStr, pwdStr);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        ToastUtils.showToast(mContext, "登陆失败!");
                    }
                    //启动IMService
                    Intent imServiceIntent = new Intent(SplashActivity.this, IMService
                            .class);
                    startService(imServiceIntent);
                    //启动PushService
                    Intent pushServiceIntent = new Intent(SplashActivity.this,
                            PushService.class);
                    startService(pushServiceIntent);
                    MainActivity.jumpMainActivity(mContext);
                } else {
                    LoginActivity.jumpLoginActivity(mContext);
                }
                finish();
            }
        });
    }
}
