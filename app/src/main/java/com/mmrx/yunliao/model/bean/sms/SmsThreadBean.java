package com.mmrx.yunliao.model.bean.sms;/**
 * Created by mmrx on 16/3/8.
 */

import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午1:51
 * 描述:  和同一个号码往来的短信作为一个smsThread存于数据库中
 *     显示为最新的时间和内容,表threads的实体对象
 */
public class SmsThreadBean implements ISmsListBean{
    private int     _id;//数据库序号
    private long    date_long;//最新会话的时间,long型
    private String   date_str;//同上,string型
    private int     message_count;//会话中短信数量
    private String   recipient_ids;//表canonical_addresses 序号
    private String   addresses;//联系人电话
    private String   snippet;//最新会话内容
    private String   contact;//联系人备注
    private boolean  read;//是否阅读,0未读,1已读,这里用boolean来表示
    /*type
        ALL    = 0;
        INBOX  = 1;
        SENT   = 2;
        DRAFT  = 3;
        OUTBOX = 4;
        FAILED = 5;
        QUEUED = 6;  */
    private int     type;
    public SmsThreadBean() {
    }

    public SmsThreadBean(int _id, long date_long,int message_count,
                         String recipient_ids, String snippet,
                         int read, int type) {
        this._id = _id;
        setDate_long(date_long);
        this.message_count = message_count;
        this.recipient_ids = recipient_ids;
        this.snippet = snippet;
        setRead(read);
        this.type = type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
    }

    public String getRecipient_ids() {
        return recipient_ids;
    }

    public void setRecipient_ids(String recipient_ids) {
        this.recipient_ids = recipient_ids;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "SmsThreadBean{" +
                "_id=" + _id +
                ", date_long=" + date_long +
                ", date_str='" + date_str + '\'' +
                ", message_count=" + message_count +
                ", recipient_ids='" + recipient_ids + '\'' +
                ", addresses='" + addresses + '\'' +
                ", snippet='" + snippet + '\'' +
                ", read=" + read +
                ", type=" + type +
                '}';
    }

    @Override
    public String getContacts() {
        return this.contact.equals("null") ? this.addresses : this.contact;
    }

    @Override
    public String getDate() {
        return getDate_str();
    }

    @Override
    public Class getClassType() {
        return SmsThreadBean.class;
    }

    @Override
    public long getDateLong() {
        return getDate_long();
    }

    @Override
    public int compareTo(ISmsListBean another) {
        if(this.getDateLong() > another.getDateLong())
            return -1;
        else
            return 1;
    }
}
