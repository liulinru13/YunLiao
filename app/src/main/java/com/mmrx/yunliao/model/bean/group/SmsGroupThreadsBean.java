package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:10
 * 描述: 群发短信会话数据结构,对应对应ylsmsdb.db中表sms_group_threads_table
 */
public class SmsGroupThreadsBean implements ISmsListBean {
    private int     _id;//主键id
    private int     message_count;//消息数量
    private long    date_long;//最新会话的时间,long型
    private String   date_str;//同上,string型
    private String   snippet;//最新会话内容
    private String   address;//电话号码,以,隔开
    private String   contacts;//联系人显示名,以,隔开

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMessage_count() {
        return message_count;
    }

    public void setMessage_count(int message_count) {
        this.message_count = message_count;
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

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getContacts() {
        return this.contacts == null ? this.address : this.contacts;
    }

    @Override
    public String getDate() {
        return getDate_str();
    }

    @Override
    public boolean isRead() {
        return false;
    }

    @Override
    public Class getClassType() {
        return SmsGroupThreadsBean.class;
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
