package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/13.
 */

import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/3/13下午2:13
 * 描述: 待发送的群发类型短信的数据结构,其中涉及数据库中的_id,阅读状态等信息都为空
 * 只做发送时的数据结构,不做显示时的数据结构
 */
public class SmsGroupThreadSend {

    private List<SmsCroupUserBean>  mGroupUsersList;
    private SmsGroupBean        mGroupBean;

    public List<SmsCroupUserBean> getmGroupUsersList() {
        return mGroupUsersList;
    }

    public void setmGroupUsersList(List<SmsCroupUserBean> mGroupUsersList) {
        this.mGroupUsersList = mGroupUsersList;
    }

    public SmsGroupBean getmGroupBean() {
        return mGroupBean;
    }

    public void setmGroupBean(SmsGroupBean mGroupBean) {
        this.mGroupBean = mGroupBean;
    }
}
