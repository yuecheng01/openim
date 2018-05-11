package com.yeucheng.openim.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yeucheng.openim.base.XmppConnection;
import com.yeucheng.openim.provider.ContactsProvider;
import com.yeucheng.openim.provider.SmsContentProvider;
import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.PinyinUtil;
import com.yeucheng.openim.util.Threadutils;
import com.yeucheng.openim.util.ToastUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/9.
 */

public class IMService extends Service {
    //联系人对象
    private Roster mRoster;
    //同步联系人监听
    private RosterListener mRosterListener;
    //消息管理者
    private ChatManager mChatManager;
    //消息内容列表改变监听
    private MyMessageListener mMyMessageListener = new MyMessageListener();
    //管理消息创建监听
    private MyChatManagerListener mMyChatManagerListener = new MyChatManagerListener();
    //当前聊天
    private Chat mCurrentChat;

    private Map<String, Chat> mChatMap = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        /**
         * 返回IMService实例
         *
         * @return
         */
        public IMService getIMService() {
            return IMService.this;
        }
    }

    @Override
    public void onCreate() {
        Threadutils.runinThread(new Runnable() {
            @Override
            public void run() {
                //--------------------同步联系人 begin----------------------
                //得到联系人
                //需要连接对象
                mRoster = XmppConnection.getConnection().getRoster();
                //得到所有联系人.
                final Collection<RosterEntry> entries = mRoster.getEntries();
                //打印
                for (RosterEntry e :
                        entries) {
                    LogUtils.d("tag", e.toString());
                    LogUtils.d("tag", e.getUser());//jid account
                    LogUtils.d("tag", e.getName());//nickname 别名
//            LogUtils.d("tag",e.getStatus());
//            LogUtils.d("tag",e.getType());
                }
                //监听联系人的变化
                mRosterListener = new MyRosterListener();
                mRoster.addRosterListener(mRosterListener);
                for (RosterEntry entry :
                        entries) {
                    syncRoster(entry);
                }
                //--------------------同步联系人  end----------------------


                //--------------------创建消息管理者  添加监听 begin----------------------

                //1,获取消息管理者
                if (mChatManager == null) {
                    mChatManager = XmppConnection.getConnection().getChatManager();
                }
                mChatManager.addChatListener(mMyChatManagerListener);
                //--------------------创建消息管理者  添加监听 end----------------------
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //移除listener
        if (mRoster != null && mRosterListener != null) {
            mRoster.removeRosterListener(mRosterListener);
        }
        if (mCurrentChat != null && mMyMessageListener != null) {
            mCurrentChat.removeMessageListener(mMyMessageListener);
        }
        super.onDestroy();
    }

    private class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> collection) {//添加联系人
            //更新数据库
            for (String c : collection) {
                RosterEntry entry = mRoster.getEntry(c);
                //要么更新,要么插入
                syncRoster(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {//更新联系人
            //更新数据库
            for (String c : collection) {
                RosterEntry entry = mRoster.getEntry(c);
                //要么更新,要么插入
                syncRoster(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {//删除联系人
            //更新数据库
            for (String account : collection) {
                //执行删除操作
                getContentResolver().delete(ContactsProvider.URI_CONTACT, "account = ?", new
                        String[]{account});
            }
        }

        @Override
        public void presenceChanged(Presence presence) {//在线状态变化

        }
    }

    /**
     * 消息监听
     */
    private class MyMessageListener implements MessageListener {
        @Override
        public void processMessage(Chat chat, Message message) {
            LogUtils.d("IMService getmessage", message.getBody());
            //收到消息,保存消息
            String participant = chat.getParticipant();
            if (participant != null && participant.contains("@")) {
                participant = participant.substring(0, participant.lastIndexOf("@"));
            }
            LogUtils.d("participant--->:", participant);
            saveMessage(participant, message);
        }
    }

    /**
     * 消息管理器监听
     */
    private class MyChatManagerListener implements ChatManagerListener {
        @Override
        public void chatCreated(Chat chat, boolean b) {
            //判断chat是否存在map里面.
            String participant = chat.getParticipant();
            LogUtils.d("IMService participant", participant);
            if (participant != null && participant.contains("@")) {
                participant = participant.substring(0, participant.lastIndexOf("@"));
            }
            if (!mChatMap.containsKey(participant)) {
                //保存chat
                mChatMap.put(participant, chat);
                chat.addMessageListener(mMyMessageListener);
            }
            if (b) {
                LogUtils.d("chatcreat", "------------我创建chatCreated---------");
                //admin@desktop-e7jclfj
            } else {
                LogUtils.d("chatcreat", "------------别人创建chatCreated---------");
                //admin@desktop-e7jclfj/Spark 2.8.3.960
            }
        }
    }

    private void syncRoster(RosterEntry entry) {
        ContentValues values = new ContentValues();
        String account = entry.getUser();
        String nickName = entry.getName();
        if (account != null && account.contains("@")) {
            account = account.substring(0, account.lastIndexOf("@"));
        }
        values.put("account", account);
        //处理nickName;
        if (nickName == null || "".equals(nickName)) {
            nickName = account;
        }
        values.put("nickname", nickName);

        values.put("avatar", "0");

        values.put("pinyin", PinyinUtil.getPinyin(nickName));
        //先更新后插入
        int updateCount = getContentResolver().update(ContactsProvider.URI_CONTACT,
                values, "account =" +
                        " ?", new String[]{account});
        if (updateCount <= 0) {//没有更新的记录
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }

    /**
     * 保存消息
     *
     * @param sessionAccount
     * @param message
     */
    private void saveMessage(String sessionAccount, Message message) {
        //如果空消息就不保存
        if (message.getBody().equals("") || message.getBody() == null) {
            return;
        }
        ContentValues values = new ContentValues();
        String fromAccount = message.getFrom();
        String toAccount = message.getTo();
        LogUtils.d("saveMessage之前,打印发,收消息的人信息", "from:" + fromAccount + ";to:" + toAccount);
        if (fromAccount != null && fromAccount.contains("@")) {
            fromAccount = fromAccount.substring(0, fromAccount.lastIndexOf("@"));
        }
        if (toAccount != null && toAccount.contains("@")) {
            toAccount = toAccount.substring(0, toAccount.lastIndexOf("@"));
        }
        LogUtils.d("saveMessage之前,打印发,收消息的人信息,截取掉后缀", "from:" + fromAccount + ";to:" + toAccount);
        values.put("from_account", fromAccount);
        values.put("to_account", toAccount);
        values.put("body", message.getBody());
        values.put("status", "offline");
        values.put("type", message.getType().name());
        values.put("time", System.currentTimeMillis());
        values.put("session_account", sessionAccount);
        getApplicationContext().getContentResolver().insert(SmsContentProvider.URI_SMS, values);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(Message message) {
        try {
            //判断chat是否已经创建
            String toAccount = message.getTo();
            if (toAccount != null && toAccount.contains("@")) {
                toAccount = toAccount.substring(0, toAccount.lastIndexOf("@"));
            }
            LogUtils.d("message.getTo()", message.getTo());
            if (mChatMap.containsKey(toAccount)) {
                mCurrentChat = mChatMap.get(toAccount);
            } else {
                mCurrentChat = mChatManager.createChat(toAccount, mMyMessageListener);
                mChatMap.put(toAccount, mCurrentChat);
            }
            //发送消息
            mCurrentChat.sendMessage(message);
            //保存消息
            saveMessage(toAccount, message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
