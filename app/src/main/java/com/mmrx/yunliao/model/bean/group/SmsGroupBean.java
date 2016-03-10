package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

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
}
