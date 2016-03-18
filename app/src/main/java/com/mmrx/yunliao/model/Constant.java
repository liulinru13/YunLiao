package com.mmrx.yunliao.model;/**
 * Created by mmrx on 16/3/8.
 */

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午1:25
 * 描述:  常量字段类
 */
public class Constant {
    /*********sms相关********/
    //短信表uri
    public static final String SMS_URI_ALL = "content://sms/";
    //threads表uri
    public static final String SMS_THREADS_URI = "content://mms-sms/conversations?simple=true";
    //Canonical_address表uri
    public static final String SMS_ADDRESS_URI = "content://mms-sms/canonical-addresses";
    //sms收件箱
    public static final String SMS_SEND_URI = "content://sms/sent";
    //sms发件箱
    public static final String SMS_INBOX_URI = "content://sms/inbox";
    //sms发送失败的
    public static final String SMS_FAILED_URI = "content://sms/failed";
    //sms
    public static final String SMS_DRAFT_URI = "content://sms/draft";
    //sms
    public static final String SMS_OUTBOX_URI = "content://sms/outbox";
    //sms
    public static final String SMS_QUEUED_URI = "content://sms/queued";

    //群发短信状态的枚举
    public enum SMS_GROUP_SATTUS {
        SENT(-1),//已发送
        WRITTING(0),//草稿
        SENDING(1),//正在发送
        FAILED(2);//发送失败
        private int values;
        SMS_GROUP_SATTUS(int values){
            this.values = values;
        }

        public int getValues() {
            return values;
        }
    };
    /*********路径相关********/
    public static final String PACKAGE_NAME = "com.mmrx.yunliao";


    /*********数据库相关********/
    public static final String DATABASE_NAME = "ylsmsdb.db";
    public static final String DATABASE_YL_GROUP_THREADS_TABLE = "sms_group_threads_table";
    public static final String DATABASE_YL_GROUP_TABLE = "sms_group_table";
    public static final String DATABASE_YL_GROUP_USER_TABLE = "sms_group_user_table";

    /*********SP库相关********/
    //类型
    public static final String SP_TYPE_STR = "STRING";
    public static final Integer SP_TYPE_INT = 1;
    public static final Float SP_TYPE_FLOAT = 1.0f;
    public static final Double SP_TYPE_DOUBLE = 1.0;
    public static final Long SP_TYPE_LONG = 1l;
    public static final Boolean SP_TYPE_BOOLEAN = true;
    //文件名
    public static final String SP_F_THEME = "sp_file_name_theme";//保存主题信息的sp文件名

    //key值
    public static final String SP_K_THEME = "sp_key_theme";//主题的key值

    /*********异常相关********/
    public static final String EXCEPTION_YLDB_IS_NULL = "云聊群发记录数据库实例为null";


}
