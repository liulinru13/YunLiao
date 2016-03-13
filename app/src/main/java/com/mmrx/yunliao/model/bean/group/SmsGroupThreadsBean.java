package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:10
 * 描述: 群发短信会话数据结构,对应对应ylsmsdb.db中表sms_group_threads_table
 */
public class SmsGroupThreadsBean {
    private int     _id;//主键id
    private int     message_count;//消息数量

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
}
