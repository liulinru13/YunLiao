package com.mmrx.yunliao.model.bean.group;/**
 * Created by mmrx on 16/3/10.
 */

import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/3/10下午12:18
 * 描述: 群发短信数据结构,
 *  包含smsGroupBean,smsGroupThreadsBean,
 *  smsGroupUserBean信息
 */
public class SmsGroupThread {
    private SmsGroupThreadsBean     mGroupInfo;
    private List<SmsGroupBean>     mGroupSmsList;
    private List<SmsCroupUserBean>   mGroupUsersList;

}
