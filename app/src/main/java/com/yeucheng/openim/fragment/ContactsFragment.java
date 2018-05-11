package com.yeucheng.openim.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yeucheng.openim.R;
import com.yeucheng.openim.activity.ChatActivity;
import com.yeucheng.openim.adapter.ContactsListAdapter;
import com.yeucheng.openim.base.AbsBaseFragment;
import com.yeucheng.openim.provider.ContactsProvider;
import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.Threadutils;
import com.yeucheng.openim.util.inject.FindView;


/**
 * 会画fragment
 * Created by Administrator on 2018/5/8.
 */

public class ContactsFragment extends AbsBaseFragment {
    @FindView(R.id.contacts_list)
    private ListView mContactListView;
    private ContactsListAdapter mAdapter;

    @Override
    protected int getlayoutID() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void init() {
        registerContentObserver();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initData() {
        setOrUpdateAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setOrUpdateAdapter() {
        //判断mAdapter是否为空
        if (mAdapter != null) {
            //刷新mAdapter
            mAdapter.getCursor().requery();
            return;
        }
        //开启线程,同步花名册
        Threadutils.runinThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                //d对应查询记录
                final Cursor cursor = mActivity.getContentResolver().query(ContactsProvider
                        .URI_CONTACT, new String[]{"id as _id","account","nickname","pinyin",
                                "avatar"},
                        null, null,
                        null,
                        null);
                //如没有数据
                if (cursor.getCount() <= 0) {
                    return;
                }
                //设置adapter,更新UI.
                Threadutils.runinUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new ContactsListAdapter(mActivity, cursor);
                        mContactListView.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    @Override
    protected void initListener() {
        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(i);
                String account = cursor.getString(cursor.getColumnIndex("account"));
                LogUtils.d("ContactsFragment----account--->",account);
                String nickName = cursor.getString(cursor.getColumnIndex("nickname"));
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra(ChatActivity.ACCOUNT,account);
                intent.putExtra(ChatActivity.NICKNAME,nickName);
                startActivity(intent);
            }
        });
    }

    //****************监听数据库变化*****************************
    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    //注册监听
    public void registerContentObserver() {
        mActivity.getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT,
                true, mMyContentObserver);
    }

    //反注册监听
    public void unRegisterContentObserver() {
        mActivity.getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            setOrUpdateAdapter();
        }
    }
    //****************监听数据库变化*****************************


    @Override
    public void onDestroy() {
        unRegisterContentObserver();
        super.onDestroy();
    }
}
