package com.yeucheng.openim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.yeucheng.openim.db.Contact;
import com.yeucheng.openim.provider.ContactsProvider;
import com.yeucheng.openim.util.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Administrator on 2018/5/9.
 */
@RunWith(AndroidJUnit4.class)
public class ContactProviderInstrumentedTest {

    @Test
    public void testInsert() {
        /*String _id;
        String account;
        String nickname;
        String avatar;
        String pinyin;*/
        ContentValues values = new ContentValues();
        values.put("account", "yeucheng@yuecehng.com");
        values.put("nickname", "yeucheng@yuecehng.com");
        values.put("avatar", "0");
        values.put("pinyin", "yeucheng");
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
    }
    @Test
    public void testDelete() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().delete(ContactsProvider.URI_CONTACT, "account = ?", new
                String[]{"yeucheng@yuecehng.com"});
    }
    @Test
    public void testUpdate() {
        ContentValues values = new ContentValues();
        values.put("account", "yeucheng@yuecehng.com");
        values.put("nickname", "我是悦城");
        values.put("avatar", "0");
        values.put("pinyin", "woshiyuecheng");
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.getContentResolver().update(ContactsProvider.URI_CONTACT, values, "account = " +
                "?", new String[]{"yeucheng@yuecehng.com"});
    }
    @Test
    public void testQuery() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Cursor cursor = appContext.getContentResolver().query(ContactsProvider.URI_CONTACT, null,
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
