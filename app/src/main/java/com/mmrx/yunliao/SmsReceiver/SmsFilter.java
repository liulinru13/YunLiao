package com.mmrx.yunliao.SmsReceiver;

import android.content.Context;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * 创建人: mmrx
 * 时间: 16/4/19下午5:21
 * 描述: 短信内容过滤
 *    包括 远程控制命令过滤,数据加密过滤
 */
public class SmsFilter {

    private Context mContext;
    private SmsContentFilter mContentFilter;//远程控制过滤器
    //private SmsEnCodeFilter mEncodeFilter;//数据加密过滤器
    public SmsFilter(Context context){
        this.mContext = context;
        this.mContentFilter = new SmsContentFilter(context);
    }

    public SmsMessage[] doFilter(SmsMessage[] msgs){
        //是远程控制命令
        if(mContentFilter.messageFilter(msgs[0])){
            //是否保存远程控制命令
            return null;
        }
        //数据加密

        return msgs;
    }
}
