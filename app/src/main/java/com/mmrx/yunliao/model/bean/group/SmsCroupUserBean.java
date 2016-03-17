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
    private int     status;//状态 已发送-1,草稿0,正在发送1,发送失败2

    public SmsCroupUserBean(int _id, String address, int person,
                            int group_id, int status) {
        this._id = _id;
        this.address = address;
        this.person = person;
        this.group_id = group_id;
        this.status = status;
    }

    public SmsCroupUserBean(String address, int person) {
        this.address = address;
        this.person = person;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
