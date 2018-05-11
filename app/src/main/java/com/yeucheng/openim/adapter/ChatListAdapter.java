package com.yeucheng.openim.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeucheng.openim.R;


/**
 * Created by Administrator on 2018/5/9.
 */

public class ChatListAdapter extends CursorAdapter {

    private Cursor c;

    public ChatListAdapter(Context context, Cursor c) {
        super(context, c);
        this.c = c;
    }

    //if converView == null 执行newView
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.item_contact, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = view.findViewById(R.id.avatar);
        TextView accountView = view.findViewById(R.id.account);
        TextView body = view.findViewById(R.id.nickname);
        String account = cursor.getString(c.getColumnIndex("session_account"));
        String content = cursor.getString(c.getColumnIndex("body"));
        accountView.setText(account);
        body.setText(content);
    }
}
