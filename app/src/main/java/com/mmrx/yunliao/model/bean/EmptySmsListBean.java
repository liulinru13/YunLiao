package com.mmrx.yunliao.model.bean;/**
 * Created by mmrx on 16/4/5.
 */

/**
 * 创建人: mmrx
 * 时间: 16/4/5下午3:17
 * 描述: 无数据情况下列表内容填充数据结构
 */
public class EmptySmsListBean implements ISmsListBean{
    @Override
    public String getContacts() {
        return "--";
    }

    @Override
    public String getDate() {
        return "-/-/-";
    }

    @Override
    public String getSnippet() {
        return "--";
    }

    @Override
    public Class getClassType() {
        return null;
    }

    @Override
    public boolean isRead() {
        return true;
    }

    @Override
    public long getDateLong() {
        return 0;
    }
}
