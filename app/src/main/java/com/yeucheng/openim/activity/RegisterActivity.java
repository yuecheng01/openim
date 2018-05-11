package com.yeucheng.openim.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yeucheng.openim.R;
import com.yeucheng.openim.base.AbsBaseActivity;
import com.yeucheng.openim.base.XmppConnection;
import com.yeucheng.openim.util.Threadutils;
import com.yeucheng.openim.util.ToastUtils;
import com.yeucheng.openim.util.inject.FindView;
import com.yeucheng.openim.util.inject.OnClick;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

public class RegisterActivity extends AbsBaseActivity {
    @FindView(R.id.et_email)
    private EditText mEt_Email;
    @FindView(R.id.et_user)
    private EditText mEt_User;
    @FindView(R.id.et_nickname)
    private EditText mEt_NickName;
    @FindView(R.id.et_password)
    private EditText mEt_Pwd;
    @FindView(R.id.bt_register)
    private Button mBt_Register;

    @Override
    protected int setLayoutID() {
        return R.layout.activity_register;
    }
    public static void jumpRegisterActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void init() {

    }

    /**
     * 点击事件
     * @param view viewId
     */
    @OnClick({R.id.bt_register})
    public void registerClick(View view){
        switch (view.getId()){
            case R.id.bt_register:
                final String userEmail = mEt_Email.getText().toString();
                final String userUserName = mEt_User.getText().toString();
                final String userNickName = mEt_NickName.getText().toString();
                final String userPassWord = mEt_Pwd.getText().toString();
                Threadutils.runinThread(new Runnable() {
                    @Override
                    public void run() {
                        Registration reg = new Registration();
                        //设置类型
                        reg.setType(IQ.Type.SET);
                        //发送到服务器
                        reg.setTo(XmppConnection.getConnection().getServiceName());
                        //设置用户名
                        reg.setUsername(userUserName);
                        //设置密码
                        reg.setPassword(userPassWord);

                        //设置其余属性 不填可能会报500异常 连接不到服务器 asmack一个Bug
                        //设置昵称（其余属性）
                        reg.addAttribute("name", userNickName);
                        //设置邮箱（其余属性）
                        reg.addAttribute("email", userEmail);
                        //设置android端注册
                        reg.addAttribute("android", "geolo_createUser_android");
                        //创建包过滤器
                        PacketFilter filter = new AndFilter(new PacketIDFilter(reg
                                .getPacketID()), new PacketTypeFilter(IQ.class));
                        //创建包收集器
                        PacketCollector collector = XmppConnection.getConnection()
                                .createPacketCollector(filter);
                        //发送包
                        XmppConnection.getConnection().sendPacket(reg);
                        //获取返回信息
                        IQ result = (IQ) collector.nextResult(SmackConfiguration
                                .getPacketReplyTimeout());
                        // 停止请求results（是否成功的结果）
                        collector.cancel();
                        //通过返回信息判断
                        if (result == null) {   //无返回，连接不到服务器
                        } else if (result.getType() == IQ.Type.ERROR) {     //错误状态
                            if (result.getError().toString()
                                    .equalsIgnoreCase("conflict(409)")) {   //账户存在 409判断
                            } else {
                            }
                        } else if (result.getType() == IQ.Type.RESULT) {//注册成功跳转登录
                            ToastUtils.showToast(mContext,"注册成功!");
                            LoginActivity.jumpLoginActivity(mContext);
                        }
                    }
                });
                break;
        }
    }
}
