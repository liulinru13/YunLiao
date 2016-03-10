package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:05
 * 描述:  群发短信参与者信息数据结构
 *      对应ylsmsdb.db中表sms_user_table
 */
public class SmsCroupUserBean {
    private int     _id;//主键id
    private String   address;//地址
    private int     person;//发件人,通讯录中的id,陌生人则为null
    private int     group_id;//所属会话id
    private int     status;//状态,已发送-1,发送失败0
}
