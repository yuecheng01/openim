package com.yeucheng.openim.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yeucheng.openim.util.LogUtils;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2018/5/9.
 */

public class ContactsProvider extends ContentProvider {
    //主基地值的常量
    public static final String AUTHORITIES = ContactsProvider.class.getCanonicalName();//得到一个类的完整路径
    public static final int CONTACT = 1;
    //地址匹配对象
    static UriMatcher URIMATCHER;
    //对应联系人表的一个uri常量
    public static Uri URI_CONTACT = Uri.parse("content://" + AUTHORITIES + "/contact");

    static {
        URIMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        //添加一个匹配规则
        URIMATCHER.addURI(AUTHORITIES, "/contact", CONTACT);
        //content://com.yeucheng.openim.provider.ContactsProvider/contact -->CONTACT
    }

    @Override
    public boolean onCreate() {
        return true;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //数据存到sqlite数据库,
        int code = URIMATCHER.match(uri);
        switch (code) {
            case CONTACT:
                //新插入的id
                long id = LitePal.getDatabase().insert("Contact", "", contentValues);
                if (id != -1) {
                    LogUtils.d(getClass().getSimpleName(), "*****insert success!****");
                    //拼接最新的uri
                    //content://com.yeucheng.openim.provider.ContactsProvider/contact/id
                    uri = ContentUris.withAppendedId(uri, id);
                    //通知数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int code = URIMATCHER.match(uri);
        int deleteCount = 0;
        switch (code) {
            case CONTACT:
                //影响的行数
                deleteCount = LitePal.getDatabase().delete("Contact", s, strings);
                if (deleteCount > 0) {
                    LogUtils.d(getClass().getSimpleName(), "*****delete success!****");
                    //通知数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int code = URIMATCHER.match(uri);
        int updateCount = 0;
        switch (code) {
            case CONTACT:
                //影响的行数
                updateCount =  LitePal.getDatabase().update("Contact", contentValues, s, strings);
                if (updateCount > 0) {
                    LogUtils.d(getClass().getSimpleName(), "*****update success!****");
                    //通知数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,
                            null);
                }
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;
        int code = URIMATCHER.match(uri);
        switch (code) {
            case CONTACT:
                cursor =  LitePal.getDatabase().query("Contact", strings, s, strings1, null, null, s1);
                LogUtils.d(getClass().getSimpleName(), "*****query success!****");
            break;
        }
        return cursor;
    }
}
