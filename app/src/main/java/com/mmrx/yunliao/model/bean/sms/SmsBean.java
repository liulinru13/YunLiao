package com.mmrx.yunliao.model.bean.sms;/**
 * Created by mmrx on 16/3/8.
 */

import android.database.Cursor;

import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午12:51
 * 描述:  sms数据库表实体对象
 */
public class SmsBean {
    private int     _id;//短信序号
    private int     thread_id;//对话的序号,与同一手机号互发短信,其序号是相同的
    private String   address;//发件人地址,即手机号
    private int     person;//发件人,通讯录中的id,陌生人则为null
    private long     date_long;//日期,long型
    private String   date_str;//日期,带格式的
    private long     date_sent_long;//发送日期,long型
    private String   date_sent_str;//发送日期,格式
    private boolean   read;//是否阅读,0未读,1已读,这里用boolean来表示
    private int     status;//短信状态,-1接收,0 complete,64 pending
    private String   subject;//题目
    /*type
        ALL    = 0;
        INBOX  = 1;
        SENT   = 2;
        DRAFT  = 3;
        OUTBOX = 4;
        FAILED = 5;
        QUEUED = 6;  */
    private int     type;
    private String   body;//短信内容
    private int     locked;//是否上锁,默认为0
    public SmsBean() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public long getDate_long() {
        return date_long;
    }

    public void setDate_long(long date_long) {
        this.date_long = date_long;
        this.date_str = MiddlewareProxy.getInstance().dateFormat(this.date_long);
    }

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }

    public long getDate_sent_long() {
        return date_sent_long;
    }

    public void setDate_sent_long(long date_sent_long) {
        this.date_sent_long = date_sent_long;
        this.date_sent_str = MiddlewareProxy.getInstance().dateFormat(this.date_sent_long);
    }

    public String getDate_sent_str() {
        return date_sent_str;
    }

    public void setDate_sent_str(String date_sent_str) {
        this.date_sent_str = date_sent_str;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setRead(int read) {
        this.read = read==1;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
