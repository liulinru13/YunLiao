package com.mmrx.yunliao.model;

/**
 * Created by mmrx on 16/3/8.
 * 操作sms数据库的辅助类
 */
public class SmsDBhelper {
    private static SmsDBhelper ourInstance = new SmsDBhelper();

    public static SmsDBhelper getInstance() {
        return ourInstance;
    }

    private SmsDBhelper() {
    }

}
