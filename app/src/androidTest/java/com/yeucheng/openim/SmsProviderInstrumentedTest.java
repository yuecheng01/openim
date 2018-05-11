package com.yeucheng.openim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yeucheng.openim.provider.ContactsProvider;
import com.yeucheng.openim.provider.SmsContentProvider;
import com.yeucheng.openim.util.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Administrator on 2018/5/9.
 */
@RunWith(AndroidJUnit4.class)
public class SmsProviderInstrumentedTest {

    @Test
    public void testInsert() {
        /*from_account;
        to_account;
        body;
        status;
        type;
        time;
        session_account;*/
        ContentValues values = new ContentValues();
        values.put("from_account", "hello");
        values.put("to_account", "admin");
        values.put("body", "今晚约吗");
        values.put("status", "offline");
        values.put("type", "chat");
        values.put("time", System.currentTimeMillis());
        values.put("session_account", "");
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().insert(SmsContentProvider.URI_SMS, values);
    }
    @Test
    public void testDelete() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().delete(SmsContentProvider.URI_SMS, "from_account = ?", new
                String[]{"hello"});
    }
    @Test
    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put("from_account", "hello");
        values.put("to_account", "admin");
        values.put("body", "今晚约吗,好久没见到你了");
        values.put("status", "offline");
        values.put("type", "chat");
        values.put("time", System.currentTimeMillis());
        values.put("session_account", "");
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().update(SmsContentProvider.URI_SMS, values, "from_account = ?", new
                String[]{"hello"});
    }
    @Test
    public void testQuery() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Cursor cursor = appContext.getContentResolver().query(SmsContentProvider.URI_SMS, null,
                null,
                null, null,
                null);
        int clumcount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            for (int i = 0; i < clumcount; i++) {
                LogUtils.d("query", cursor.getString(i));
            }
        }
    }
}
