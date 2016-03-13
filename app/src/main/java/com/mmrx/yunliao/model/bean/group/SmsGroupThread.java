package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:18
 * 描述: 群发短信数据结构,
 *  包含smsGroupBean,smsGroupThreadsBean,
 *  smsGroupUserBean信息,从数据库中获取到的
 */
public class SmsGroupThread {
    private SmsGroupThreadsBean     mGroupInfo;
    private List<SmsGroupBean>     mGroupSmsList;
    private List<SmsCroupUserBean>   mGroupUsersList;

    public SmsGroupThreadsBean getmGroupInfo() {
        return mGroupInfo;
    }

    public void setmGroupInfo(SmsGroupThreadsBean mGroupInfo) {
        this.mGroupInfo = mGroupInfo;
    }

    public List<SmsGroupBean> getmGroupSmsList() {
        return mGroupSmsList;
    }

    public void setmGroupSmsList(List<SmsGroupBean> mGroupSmsList) {
        this.mGroupSmsList = mGroupSmsList;
    }

    public List<SmsCroupUserBean> getmGroupUsersList() {
        return mGroupUsersList;
    }

    public void setmGroupUsersList(List<SmsCroupUserBean> mGroupUsersList) {
        this.mGroupUsersList = mGroupUsersList;
    }

    /**
     * 获取用户列表的 address 列表
     * @return
     */
    public String getThreadUsersAddr(){
        StringBuilder sb;
        if(!this.mGroupUsersList.isEmpty()){
            sb = new StringBuilder();
            for(SmsCroupUserBean user:this.mGroupUsersList){
                sb.append(user.getAddress()).append(" ");
            }
            return sb.toString().trim();

        }
        return null;
    }
}
