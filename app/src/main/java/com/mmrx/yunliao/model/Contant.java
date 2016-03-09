package com.mmrx.yunliao.model;/**
 * Created by mmrx on 16/3/8.
 */

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午1:25
 * 描述:  常量字段类
 */
public class Contant {
    /*********sms相关********/
    //短信表uri
    public static final String SMS_URI_ALL = "content://sms/";
    //threads表uri
    public static final String SMS_THREADS_URI = "content://mms-sms/conversations?simple=true";
    //Canonical_address表uri
    public static final String SMS_ADDRESS_URI = "content://mms-sms/canonical-addresses";

}
