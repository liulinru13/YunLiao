package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

/**
 * 创建人: mmrx
 * 时间: 16/3/10上午11:54
 * 描述:  群发短信数据结构,对应ylsmsdb.db中表sms_group_table
 */
public class SmsGroupBean {

    private int     _id;//表主键
    private long    date_long;//时间 long
    private String   date_str;//时间,带格式
    private String   body;//内容
    private int     status;//状态 已发送-1,草稿0,正在发送1,发送失败2
    private int     thread_id;//会话id



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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }
}
