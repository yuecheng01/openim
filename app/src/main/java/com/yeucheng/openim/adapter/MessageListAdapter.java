package com.yeucheng.openim.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yeucheng.openim.R;
import com.yeucheng.openim.sp.SharedPreferencesUtils;
import com.yeucheng.openim.sp.Sp_Save;
import com.yeucheng.openim.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/5/10.
 */

public class MessageListAdapter extends CursorAdapter {
    private Cursor mCursor;
    private Context mContext;
    private static final int RECEIVE = 0;
    private static final int SEND = 1;

    public MessageListAdapter(Context context, Cursor c) {
        super(context, c);
        this.mCursor = c;
        this.mContext = context;
    }

    public MessageListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mCursor = c;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        mCursor.moveToPosition(position);
        String account = mCursor.getString(mCursor.getColumnIndex("from_account"));
        if (account != null && account.contains("@")) {
            account = account.substring(0, account.lastIndexOf("@"));
        }
        LogUtils.d("--------MessageListAdapter fromaccount------------", account);

        //接收:当前的账号不是创建者
        //发送
        if (SharedPreferencesUtils.getParam(mContext, Sp_Save.LOGINUSERNAME, "").equals
                (account)) {
            return SEND;
        } else {
            return RECEIVE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        switch (getItemViewType(position)) {
            case RECEIVE:
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(mContext, R.layout.item_receive_layout, null);
                    convertView.setTag(viewHolder);
                    //viewHolder赋值
                    viewHolder.mTimeStatus = convertView.findViewById(R.id.time);
                    viewHolder.mMsgContent = convertView.findViewById(R.id.messagecontent);
                    viewHolder.mNickName = convertView.findViewById(R.id.nickname);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //绑定数据
                bindData(viewHolder, position);
                break;

            case SEND:
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(mContext, R.layout.item_send_layout, null);
                    convertView.setTag(viewHolder);
                    //viewHolder赋值
                    viewHolder.mTimeStatus = convertView.findViewById(R.id.time);
                    viewHolder.mMsgContent = convertView.findViewById(R.id.messagecontent);
                    viewHolder.mNickName = convertView.findViewById(R.id.nickname);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //绑定数据
                bindData(viewHolder, position);
                break;
            default:
                break;
        }
        return super.getView(position, convertView, parent);
    }

    private void bindData(ViewHolder viewHolder, int position) {
        //绑定数据
        mCursor.moveToPosition(position);
        String time = mCursor.getString(mCursor.getColumnIndex("time"));
        String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long
                .parseLong(time)));
        String body = mCursor.getString(mCursor.getColumnIndex("body"));
        String nickName = mCursor.getString(mCursor.getColumnIndex("from_account"));
        if (nickName != null && nickName.contains("@")) {
            nickName = nickName.substring(0, nickName.lastIndexOf("@"));
        }
        LogUtils.d("MessageListAdapter nickname-------:",nickName);
        viewHolder.mTimeStatus.setText(formatTime);
        viewHolder.mMsgContent.setText(body);
        viewHolder.mNickName.setText(nickName);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    class ViewHolder {
        public TextView mTimeStatus;
        public TextView mMsgContent;
        public TextView mNickName;
    }
}
