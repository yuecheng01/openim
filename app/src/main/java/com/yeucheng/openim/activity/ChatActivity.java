package com.yeucheng.openim.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yeucheng.openim.R;
import com.yeucheng.openim.adapter.MessageListAdapter;
import com.yeucheng.openim.base.AbsBaseActivity;
import com.yeucheng.openim.provider.SmsContentProvider;
import com.yeucheng.openim.service.IMService;
import com.yeucheng.openim.sp.SharedPreferencesUtils;
import com.yeucheng.openim.sp.Sp_Save;
import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.Threadutils;
import com.yeucheng.openim.util.inject.FindView;
import com.yeucheng.openim.util.inject.OnClick;

import org.jivesoftware.smack.packet.Message;

public class ChatActivity extends AbsBaseActivity {
    @FindView(R.id.main_title)
    private TextView mTvChatTitle;
    @FindView(R.id.chat_message_list)
    private ListView mLvMessage;
    @FindView(R.id.chat_input)
    private EditText mEtInput;
    @FindView(R.id.chat_send)
    private Button mBtnSend;

    public static final String ACCOUNT = "clickAccount";
    public static final String NICKNAME = "clickNickName";
    private String mAccount;
    private String mNickName;
    private MessageListAdapter mAdapter;
    private IMService mIMService;
    private MyServerConnection mMyServerConnection = new MyServerConnection();

    @Override
    protected int setLayoutID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void init() {
        //注册监听
        registerContentObserver();
        //绑定服务
        Intent intent = new Intent(mContext, IMService.class);
        bindService(intent, mMyServerConnection, BIND_AUTO_CREATE);
        //得到传过来的值
        getIntentData();
        //设置标题
        mTvChatTitle.setText(mNickName);
        //mLvMessage加载数据
        setAdapterOrNotify();
    }

    private void setAdapterOrNotify() {
        if (mAdapter != null) {
            //更新
            mAdapter.getCursor().requery();
            mLvMessage.setSelection(mAdapter.getCount() - 1);
            return;
        }
        Threadutils.runinThread(new Runnable() {
            @Override
            public void run() {
                String accountF = (String) SharedPreferencesUtils.getParam(mContext, Sp_Save
                        .LOGINUSERNAME, "");
                LogUtils.d("ChatActivity 根据account条件查询消息记录", "accountF:" + accountF + ",mAccount:" + mAccount);
                final Cursor cursor = getContentResolver().query(SmsContentProvider.URI_SMS, new String[]{"id as" +
                                " _id", "from_account", "to_account", "body", "status", "type", "time", "session_account"},
                        "(from_account = ? and to_account = ?) or " +
                                "(from_account = ? and to_account = ?)", new String[]{accountF
                                , mAccount, mAccount, accountF
                        }, "time asc");
                if (cursor.getCount() < 1) {
                    return;
                }
                Threadutils.runinUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new MessageListAdapter(mContext, cursor);
                        mLvMessage.setAdapter(mAdapter);
                        mLvMessage.setSelection(mAdapter.getCount() - 1);
                    }
                });
            }
        });
    }

    private void getIntentData() {
        mAccount = getIntent().getStringExtra(ACCOUNT);
        mNickName = getIntent().getStringExtra(NICKNAME);
    }

    @OnClick({R.id.chat_send, R.id.main_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_back:
                finish();
                break;
            case R.id.chat_send:
                Threadutils.runinThread(new Runnable() {
                    @Override
                    public void run() {
                        //3,初始化消息
                        Message message = new Message();
                        message.setFrom(String.valueOf(SharedPreferencesUtils.getParam(mContext, Sp_Save.LOGINUSERNAME,
                                "")));
                        message.setTo(mAccount);
                        message.setBody(mEtInput.getText().toString());
                        message.setType(Message.Type.chat);
                        //调用发送消息方法发送消息
                        mIMService.sendMessage(message);
                        Threadutils.runinUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mEtInput.setText("");
                            }
                        });
                    }
                });
                break;
        }
    }


    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    //注册监听
    public void registerContentObserver() {
        getContentResolver().registerContentObserver(SmsContentProvider.URI_SMS, true,
                mMyContentObserver);
    }

    //反注册监听
    public void unRegisterContentObserver() {
        getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        //接收数据改变
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            setAdapterOrNotify();
            super.onChange(selfChange, uri);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterContentObserver();
        //解绑服务
        if (mMyServerConnection != null) {
            unbindService(mMyServerConnection);
        }
        super.onDestroy();
    }

    class MyServerConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtils.d("IMServiceConnect", "------onServiceConnected-------");
            IMService.MyBinder myBinder = (IMService.MyBinder) iBinder;
            mIMService = myBinder.getIMService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.d("IMServiceConnect", "------onServiceDisconnected-------");
        }
    }
}
