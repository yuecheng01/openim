package com.yeucheng.openim.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yeucheng.openim.base.XmppConnection;
import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.ToastUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.Collection;

/**
 * Created by Administrator on 2018/5/11.
 */

public class PushService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.d("PushService", "-------------PushService----onCreate-----------------");
        XmppConnection.getConnection().addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                ToastUtils.showToast(getApplicationContext(), packet.toXML());
                Message message = (Message) packet;
                String body = message.getBody();
                ToastUtils.showToast(getApplicationContext(), body);
            }
        }, null);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.d("PushService", "--------------PushService---onDestroy-----------------");
        super.onDestroy();
    }
}
