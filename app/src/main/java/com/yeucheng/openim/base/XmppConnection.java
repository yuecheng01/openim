package com.yeucheng.openim.base;

import com.yeucheng.openim.util.Threadutils;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by Administrator on 2018/5/8.
 */

public class XmppConnection {

    public static Connection mConnection;
    private static final String SERVER_HOST = "192.168.0.109";
    private static final int SERVER_PORT = 5222;

    public static Connection getConnection() {
        if (null == mConnection) {
            try {
                XMPPConnection.DEBUG_ENABLED = true;
                //配置文件  参数（服务地地址，端口号，域）
                ConnectionConfiguration conConfig = new ConnectionConfiguration(
                        SERVER_HOST, SERVER_PORT);
                //设置断网重连 默认为true
                conConfig.setReconnectionAllowed(true);
                //设置登录状态 true-为在线
                conConfig.setSendPresence(true);
                //设置不需要SAS验证
                conConfig.setSASLAuthenticationEnabled(true);
                //开启连接
                mConnection = new XMPPConnection(conConfig);
                mConnection.connect();
                //添加额外配置信息
//        configureConnection();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        if (null != mConnection && !mConnection.isConnected()) {
            try {
                mConnection.connect();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        return mConnection;
    }
}
