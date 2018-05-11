package com.yeucheng.openim.db;

import android.provider.BaseColumns;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/5/10.
 */

public class Sms extends DataSupport{
    /**
     * 表结构
     * <p>
     * from_account ; 发送者
     * to_account;    接收者
     * body;            消息内容
     * status           发送状态
     * type             消息类型
     * time             发送时间
     * session_account  会话ID
     */
    @Column(nullable = false) // 非空
    private long _id;
    private String from_account;
    private String to_account;
    private String body;
    private String status;
    private String type;
    private String time;
    private String session_account;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getFrom_account() {
        return from_account;
    }

    public void setFrom_account(String from_account) {
        this.from_account = from_account;
    }

    public String getTo_account() {
        return to_account;
    }

    public void setTo_account(String to_account) {
        this.to_account = to_account;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSession_account() {
        return session_account;
    }

    public void setSession_account(String session_account) {
        this.session_account = session_account;
    }
}
