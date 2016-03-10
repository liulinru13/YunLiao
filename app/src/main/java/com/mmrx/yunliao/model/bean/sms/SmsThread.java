package com.mmrx.yunliao.model.bean.sms;/**
 * Created by mmrx on 16/3/10.
 */

import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:17
 * 描述: 短信会话数据结构,包含smsBean和smsTtreadBean信息
 */
public class SmsThread {
    private List<SmsBean> mSmsList;
    private SmsThreadBean mThreasInfo;

    public SmsThread() {
    }

    public SmsThread(List<SmsBean> mSmsList, SmsThreadBean mThreasInfo) {
        this.mSmsList = mSmsList;
        this.mThreasInfo = mThreasInfo;
    }

    public List<SmsBean> getmSmsList() {
        return mSmsList;
    }

    public void setmSmsList(List<SmsBean> mSmsList) {
        this.mSmsList = mSmsList;
    }

    public SmsThreadBean getmThreasInfo() {
        return mThreasInfo;
    }

    public void setmThreasInfo(SmsThreadBean mThreasInfo) {
        this.mThreasInfo = mThreasInfo;
    }
}
