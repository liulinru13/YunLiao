package com.mmrx.yunliao.SmsReceiver;/**
 * Created by mmrx on 16/4/19.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.telephony.SmsMessage;

import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.presenter.util.SPUtil;

/**
 * 创建人: mmrx
 * 时间: 16/4/19下午4:51
 * 描述: 过滤短信内容,短信内容是否有远程控制命令
 */
public class SmsContentFilter {

    private final String TAG = "SmsContentFilterLog";
    private Context context;
    private boolean remoteControlAllocated;
    private final String privaPwd;
    private final String qkStr;
    private final String locaStr;
    private final String encStr;
    private enum SmsAction{
        SMS_ACTION_QK,//清空数据
        SMS_ACTION_LOCA,//获取设备位置
        SMS_ACTION_ENC,//加密数据
        SMS_ACTION_NULL//非远程控制命令
    }

    public SmsContentFilter(Context context){
        SharedPreferences sp = SPUtil.getPreference(context);
        remoteControlAllocated = sp.getBoolean("pref_contr_switch",false);
        qkStr = sp.getString(Constant.SP_SETTING_CONTROL_QK, "qk");
        locaStr = sp.getString(Constant.SP_SETTING_CONTROL_LOCA,"loca");
        encStr = sp.getString(Constant.SP_SETTING_CONTROL_ENC,"enc");
        this.context = context;
        privaPwd = (String)SPUtil.get(context, Constant.SP_F_SETTING_CODE,Constant.SP_K_SETTING_CODE,"");
    }
    public boolean messageFilter(SmsMessage msg){
        //远程控制开关是关闭的
        if(msg == null || !remoteControlAllocated){
            return false;
        }
        return doAction(getSmsAction(msg.getDisplayMessageBody()),msg.getDisplayMessageBody());
    }

    /**
     * 判断字符串是否是远程控制命令的格式
     * @param body
     * @return
     */
    private SmsAction getSmsAction(String body){
        //清空 qk_隐私密码
        if(body.equals(qkStr+"_"+privaPwd))
            return SmsAction.SMS_ACTION_QK;
        //获取位置 loca_隐私密码_接受手机号
        if(body.startsWith(locaStr+"_"+privaPwd+"_"))
            return SmsAction.SMS_ACTION_LOCA;
        //加密数据
        if(body.equals(encStr + "_" + privaPwd))
            return SmsAction.SMS_ACTION_ENC;

        return SmsAction.SMS_ACTION_NULL;
    }

    private boolean doAction(SmsAction action,final String msg){
        switch (action){
            case SMS_ACTION_QK:
                actionQK(context);
                return true;
            case SMS_ACTION_LOCA:
                actionLOCA(context,msg);
                return true;
            case SMS_ACTION_ENC:
                actionENC(context);
                return true;
            case SMS_ACTION_NULL:
                return false;
            default:
                return false;
        }
    }

    private void actionQK(final Context context){
        if(!MiddlewareProxy.getInstance().isInit()){
            MiddlewareProxy.getInstance().init(context);
        }

        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //清空系统短信数据库
                MiddlewareProxy.getInstance().deleteSmsAll(context);
                //清空云聊数据库
                MiddlewareProxy.getInstance().deleteGroupSmsAll();
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MiddlewareProxy.getInstance().notifyAllSmsObserver(Constant.FLAT_SMS_REFRESH);
                    }
                });
            }
        });
    }

    private void actionLOCA(final Context context,final String msg){
        String[] args = msg.split("_");
        if(args.length == 3) {
            L.i(TAG, args[2]);
            SPUtil.put(context.getApplicationContext(), Constant.SP_F_CONTROL, Constant.SP_K_LOCATION, args[2]);
            Intent intent = new Intent();
            intent.setClass(context, LocationService.class);
            context.startService(intent);
        }
    }

    /**
     * 以utf-8的形式给字符串进行转码
     * @param context
     */
    private void actionENC(final Context context){
        if(!MiddlewareProxy.getInstance().isInit()){
            MiddlewareProxy.getInstance().init(context);
        }

        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //短信数据库
                MiddlewareProxy.getInstance().encodeSmsAll(context);
                //云聊数据库
                MiddlewareProxy.getInstance().encodeGroupSmsAll();
                SPUtil.put(context, Constant.SP_F_CONTROL, Constant.SP_K_ENC, true);
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MiddlewareProxy.getInstance().notifyAllSmsObserver(Constant.FLAT_SMS_REFRESH);
                    }
                });
            }
        });
    }

}
