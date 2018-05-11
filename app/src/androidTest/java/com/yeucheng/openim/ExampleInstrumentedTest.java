package com.yeucheng.openim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yeucheng.openim.provider.ContactsProvider;
import com.yeucheng.openim.util.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.yeucheng.openim", appContext.getPackageName());
    }
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
