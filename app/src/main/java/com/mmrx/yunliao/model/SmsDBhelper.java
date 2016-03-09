package com.mmrx.yunliao.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mmrx.yunliao.model.bean.SmsBean;
import com.mmrx.yunliao.model.bean.SmsThreadBean;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmrx on 16/3/8.
 * 操作sms数据库的辅助类
 */
public class SmsDBhelper {
    private static final String TAG = "SmsDBhelperLog";
    private static SmsDBhelper ourInstance = new SmsDBhelper();

    public static SmsDBhelper getInstance() {
        return ourInstance;
    }

    private SmsDBhelper() {
    }

    /**
     * @param context 此处传入的应该是YunLiaoApplication对象
     * @return List SmsThreadBean集合
     * 从数据库中读取所有的短信会话记录
     * */
    public List<SmsThreadBean> readAllSmsThreads(Context context){
        List<SmsThreadBean> list = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(Contant.SMS_THREADS_URI),
                new String[]{"_id","date","message_count","recipient_ids","snippet","read","type"},
                "type=0",null,null);

        if(cursor != null){
            list = new ArrayList<SmsThreadBean>();
            while(cursor.moveToNext()){
                SmsThreadBean bean = new SmsThreadBean(cursor.getInt(0),cursor.getLong(1),
                        cursor.getInt(2),cursor.getString(3),cursor.getString(4),
                        cursor.getInt(5),cursor.getInt(6));
                //如果同一个会话具有多个发送地址
                if(bean.getRecipient_ids().contains(" ")){
                    String[] addrs = bean.getRecipient_ids().split(" ");
                    StringBuilder sb = new StringBuilder();
                    for(String str:addrs){
                        sb.append(str+" ");
                    }
                    bean.setAddresses(readSmsAddressByThreadId(context,sb.toString(),resolver));
                }
                else{
                    bean.setAddresses(readSmsAddressByThreadId(context,bean.getRecipient_ids(),resolver));
                }
//
                L.i(TAG,bean.toString()+"\n");
                list.add(bean);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * @param context
     * @param bean 要删除的thread数据结构
     * @return 是否删除成功
     * 根据所给的thread数据结构删除数据库中对应的记录
     * */
    public boolean deleteSmsThreadById(Context context,SmsThreadBean bean){
        L.i(TAG,"deleteSmsThreadById is " + bean.get_id());
        int result = 0;
        try
        {
            result = context.getContentResolver().delete(Uri.parse(Contant.SMS_URI_ALL),"thread_id=?", new String[]{bean.get_id()+""});
        }
        catch (Exception e)
        {
            MyToast.showLong(context, "删除失败" + bean.get_id() + "\n" + e.toString());
            e.printStackTrace();
            return false;
        }
        return result == 1;
    }

    /**
     * @param context
     * @param id 所要查询的会话的recipient_ids
     * @param resolver_ ContentResolver类对象,可为null
     * @return sms的地址,如果不存在,返回值为null
     * 根据recipient_ids来获得sms的地址(手机号)
     * */
    public String readSmsAddressByThreadId(Context context,String id,ContentResolver resolver_){
        if(id == null)
            return null;
        ContentResolver resolver = resolver_;
        String result = null;
        if(resolver == null){
            resolver = context.getContentResolver();
        }
        Cursor cursor = resolver.query(Uri.parse(Contant.SMS_ADDRESS_URI),
                null,"_id=?",new String[]{id},null);
        if(cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("address"));
            cursor.close();
        }
        return result;
    }

    /**
     * @param context
     * @param sms 短信的结构体
     * @return 操作结果
     * 根据id来删除某一条短信
     * */
    public boolean deleteSMSById(Context context,SmsBean sms){

        L.i(TAG,"deleteSMSById" + sms.get_id());
        try
        {
            context.getContentResolver().delete(Uri.parse(Contant.SMS_URI_ALL), "_id=?", new String[]{sms.get_id()+""});
        }
        catch (Exception e)
        {
            MyToast.showLong(context, "删除失败" + sms.get_id() + "\n" + e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
