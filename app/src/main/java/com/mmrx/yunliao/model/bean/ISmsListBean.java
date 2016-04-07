package com.mmrx.yunliao.model.bean;

/**
 * Created by mmrx on 16/4/5.
 * 显示在短信列表的数据结构接口
 */

public interface ISmsListBean extends Comparable<ISmsListBean>{
    /**
     * 获取联系人(们)
     * @return
     */
    String getContacts();

    /**
     * 获取最新内容摘要
     * @return
     */
    String getSnippet();

    /**
     * 获取显示时间
     * @return
     */
    String getDate();

    /**
     * 获取以long形式显示的时间
     * @return
     */
    long getDateLong();

    /**
     * 是否已经阅读
     * @return
     */
    boolean isRead();

    /**
     * 获取实现类的类型
     * @return
     */
    Class getClassType();
}
