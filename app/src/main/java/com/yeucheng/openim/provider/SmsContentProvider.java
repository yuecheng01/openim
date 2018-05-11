package com.yeucheng.openim.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yeucheng.openim.util.LogUtils;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2018/5/10.
 */

public class SmsContentProvider extends ContentProvider {

    private static final String AUTHORITIES = SmsContentProvider.class.getCanonicalName();
    static UriMatcher mUriMatcher;

    private static final int SMS = 1;
    private static final int SESSION = 2;

    public static Uri URI_SMS = Uri.parse("content://" + AUTHORITIES + "/sms");

    public static Uri URI_SESSON = Uri.parse("content://" + AUTHORITIES + "/session");

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //添加匹配规则
        mUriMatcher.addURI(AUTHORITIES, "/sms", SMS);
        mUriMatcher.addURI(AUTHORITIES, "/session", SESSION);
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

    //====================================CRUD BEGIN====================================//
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (mUriMatcher.match(uri)) {
            case SMS:
                long id = LitePal.getDatabase().insert("sms", "", contentValues);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(uri, id);
                    LogUtils.d("smscurd", "----------------insert success--------------");
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsContentProvider.URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleteCount = 0;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                deleteCount = LitePal.getDatabase().delete("sms", s, strings);
                if (deleteCount > 0) {
                    LogUtils.d("smscurd", "----------------delete success--------------");
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsContentProvider.URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updateCount = 0;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                updateCount = LitePal.getDatabase().update("sms", contentValues, s, strings);
                if (updateCount > 0) {
                    LogUtils.d("smscurd", "----------------update success--------------");
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsContentProvider.URI_SMS, null);
                }
                break;
            default:
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case SMS:
                cursor = LitePal.getDatabase().query("sms", strings, s, strings1, null, null, s1);
                LogUtils.d("smscurd", "----------------query success--------------");
                break;
            case SESSION:
                //"id as _id", "from_account", "to_account", "body","status", "type", "time", "session_account"
                //select * from(select * from sms where from_account = ? or to_account = ? orderby time asc) group by session_account
                cursor = LitePal.getDatabase().rawQuery("SELECT id AS _id,from_account," +
                        "to_account,status,body,type,time,session_account FROM" +
                        "(SELECT * FROM " + "sms " +
                        "WHERE from_account = ? OR to_account = ? ORDER BY time ASC) " +
                        "GROUP BY session_account", strings1);
                break;
            default:
                break;
        }
        return cursor;
    }
    //====================================CRUD END====================================//
}
