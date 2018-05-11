package com.yeucheng.openim.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yeucheng.openim.util.inject.FindView;
import com.yeucheng.openim.util.inject.OnClick;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

public class LoginActivity extends AbsBaseActivity {
    @FindView(R.id.et_user)
    private EditText mEt_User;
    @FindView(R.id.et_user)
    private EditText mEt_Pwd;
    @FindView(R.id.bt_login)
    private Button mBt_Login;
    @FindView(R.id.tv_register)
    private TextView mRegister;
    private String HOST = "192.168.0.109";//主机IP
    private int PORT = 5222;

    public static void jumpLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        SpannableString spannableString = new SpannableString("还没有账号?去注册吧!");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
            }

            @Override
            public void onClick(View view) {
                RegisterActivity.jumpRegisterActivity(mContext);
            }
        }, 7, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mRegister.setHighlightColor(Color.GRAY);
        mRegister.setText(spannableString);
        mRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.bt_login})
    private void onLoginClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                final String userName = mEt_User.getText().toString();
                final String passWord = mEt_Pwd.getText().toString();
                //判断用户名非空
                if (TextUtils.isEmpty(userName)) {
                    mEt_User.setError("用户名不能为空");
                    return;
                }
                //判断密码非空
                if (TextUtils.isEmpty(passWord)) {
                    mEt_Pwd.setError("密码不能为空");
                    return;
                }
                Threadutils.runinThread(new Runnable() {
                    @Override
                    public void run() {
                        // 连接服务器，用户登录
                        try {
                            XmppConnection.getConnection().login(userName, passWord);
                            // 连接服务器成功，更改在线状态
                            Presence presence = new Presence(Presence.Type.available);
                            XmppConnection.getConnection().sendPacket(presence);
                            ToastUtils.showToast(mContext, "登陆成功!");
                            SharedPreferencesUtils.setParam(mContext, Sp_Save.LOGINUSERNAME, userName);
                            SharedPreferencesUtils.setParam(mContext, Sp_Save.LOGINPASSWORD, passWord);
                            //启动IMService
                            Intent imServiceIntent = new Intent(LoginActivity.this, IMService
                                    .class);
                            startService(imServiceIntent);
                            //启动PushService
                            Intent pushServiceIntent = new Intent(LoginActivity.this,
                                    PushService.class);
                            startService(pushServiceIntent);
                            MainActivity.jumpMainActivity(mContext);
                            finish();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(mContext, "登陆失败!");
                        }

                    }
                });
                break;
        }
    }
}
