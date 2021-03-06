package com.mmrx.yunliao.model.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.model.bean.sms.SmsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.presenter.util.EncodeDecodeUtil;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmrx on 16/3/8.
 * 操作sms数据库的辅助类
 */
public class SmsDBhelper{
    private final String TAG = "SmsDBhelperLog";
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
    public synchronized List<ISmsListBean> queryAllSmsThreads(Context context){
        List<ISmsListBean> list = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(Constant.SMS_THREADS_URI),
                new String[]{"_id", "date", "message_count", "recipient_ids", "snippet", "read", "type"},
                "type=0", null, null);
        //是否加密存储
        boolean isEncode = SPUtil.getPreference(context).getBoolean(Constant.SP_SETTING_MAIN_ENCODE_SWITCH, false);
        //是否加密显示
        boolean isEncodeShow = SPUtil.getPreference(context).getBoolean(Constant.SP_SETTING_MAIN_ENCODE_SHOW,false);
        if(cursor != null){
            list = new ArrayList<ISmsListBean>();
            while(cursor.moveToNext()){
                SmsThreadBean bean = null;
                //需要进行解密显示
                if(isEncode && !isEncodeShow){
                    bean = new SmsThreadBean(cursor.getInt(0), cursor.getLong(1),
                            cursor.getInt(2), cursor.getString(3), EncodeDecodeUtil.getInstance().decode(cursor.getString(4)),
                            cursor.getInt(5), cursor.getInt(6));
                }
                else {
                    bean = new SmsThreadBean(cursor.getInt(0), cursor.getLong(1),
                            cursor.getInt(2), cursor.getString(3), cursor.getString(4),
                            cursor.getInt(5), cursor.getInt(6));
                }
                //如果同一个会话具有多个发送地址
                if(bean.getRecipient_ids().contains(" ")){
                    String[] addrs = bean.getRecipient_ids().split(" ");
                    StringBuilder sb = new StringBuilder();
                    for(String str:addrs){
                        sb.append(str+" ");
                    }
                    bean.setAddresses(querySmsAddressByThreadId(context, sb.toString(), resolver));
                }
                else{
                    bean.setAddresses(querySmsAddressByThreadId(context, bean.getRecipient_ids(), resolver));
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
    public synchronized boolean deleteSmsThreadById(Context context,SmsThreadBean bean){
        L.i(TAG,"deleteSmsThreadById is " + bean.get_id());
        ContentResolver resolver = context.getContentResolver();
        //在该threadId下的所有短信记录均未被锁定的前提下才能删除
        if(resolver != null && !smsIsLocked(resolver,bean)) {
            context.getContentResolver().delete(Uri.parse(Constant.SMS_URI_ALL),
                    "thread_id=?", new String[]{bean.get_id() + ""});
            context.getContentResolver().delete(Uri.withAppendedPath(Uri.parse(Constant.SMS_THREADS), bean.get_id() + ""), null, null);
            return true;
        }
        return false;
    }

    /**
     * 根据recipient_ids来获得sms的地址(手机号)
     * @param context
     * @param id 所要查询的会话的recipient_ids
     * @param resolver_ ContentResolver类对象,可为null
     * @return sms的地址,如果不存在,返回值为null
     * */
    private synchronized String querySmsAddressByThreadId(Context context,String id,ContentResolver resolver_){
        if(id == null)
            return null;
        ContentResolver resolver = resolver_;
        String result = null;
        if(resolver == null){
            resolver = context.getContentResolver();
        }
        Cursor cursor = resolver.query(Uri.parse(Constant.SMS_ADDRESS_URI),
                null, "_id=?", new String[]{id}, null);
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
    public synchronized boolean deleteSMSById(Context context,SmsBean sms){

        L.i(TAG, "deleteSMSById" + sms.get_id());
        if(sms.getLocked() == 0)
            return context.getContentResolver().delete(Uri.parse(Constant.SMS_URI_ALL),
                "_id=?", new String[]{sms.get_id()+""}) > 0;
        return false;
//        MyToast.showLong(context, "删除失败" + sms.get_id() + "\n");
    }

    /***
     * 将短信写入数据库中
     * @param context
     * @param date 时间 long
     * @param read 是否阅读
     * @param type
     * @param address 短信发送者地址
     * @param body 内容
     *
     */
//    public synchronized void insertSmsToDB(Context context,long date,int read
//            ,int type,String address,String body){
//        ContentValues values = new ContentValues();
//        values.put("date",date);
//        values.put("address",address);
//        values.put("body",body);
//        values.put("read", read);
//        values.put("type", type);
//        context.getContentResolver().insert(Uri.parse(getUriByType(type)), values);
//    }

    /**
     * 将smsBean实体插入到数据库中
     * @param context
     * @param bean
     * @param values
     */
    public synchronized void insertSmsToDb(Context context,SmsBean bean,ContentValues values){
        if(values == null){
            values = new ContentValues();
        }
        values.put("address",bean.getAddress());
        values.put("body",bean.getBody());
        values.put("date", bean.getDate_long());
        values.put("locked",bean.getLocked());
        values.put("status",bean.getStatus());
        values.put("subject",bean.getSubject());
        values.put("type",bean.getType());

        context.getContentResolver().insert(Uri.parse(getUriByType(1)), values);
    }

    /**
     * 清空短信数据库
     * @param context
     */
    public synchronized boolean deleteAllSms(Context context){
        return context.getContentResolver().delete(Uri.parse(Constant.SMS_URI_ALL), null, null)>0;
    }

    /**
     * 根据threadID获取全部sms会话记录,根据时间升序排序
     * @param context
     * @param threadBean
     * @return 会话的list
     */
    public synchronized List<SmsBean> queryAllSmsByThreadId(Context context,SmsThreadBean threadBean){
        List<SmsBean> list = null;
        ContentResolver resolver = context.getContentResolver();
        if(resolver != null && threadBean != null){
            Cursor cursor = resolver.query(Uri.parse(getUriByType(0)),
                    new String[]{"_id","thread_id","address",
                            "person","date","date_sent",
                            "read","status","type",
                            "subject","body","locked"},
                    "thread_id = ?",new String[]{threadBean.get_id()+""},"date ASC");
            if(cursor != null){
                list = new ArrayList<SmsBean>();
                while (cursor.moveToNext()){
                    SmsBean bean = new SmsBean();

                    bean.set_id(cursor.getInt(0));
                    bean.setThread_id(cursor.getInt(1));
                    bean.setAddress(cursor.getString(2));
                    bean.setPerson(cursor.getInt(3));
                    bean.setDate_long(cursor.getLong(4));
                    bean.setDate_sent_long(cursor.getLong(5));
                    bean.setRead(cursor.getInt(6));
                    bean.setStatus(cursor.getInt(7));
                    bean.setType(cursor.getInt(8));
                    bean.setSubject(cursor.getString(9));
                    bean.setBody(cursor.getString(10));
                    bean.setLocked(cursor.getInt(11));

                    list.add(bean);
                }
            }
        }
        return list;
    }

    /**
     * 获取所有短信记录
     * @param context
     * @return
     */
    public List<SmsThread> getAllSmsRecord(Context context){
        List<SmsThread> result = new ArrayList<SmsThread>();

        List<ISmsListBean>  threads = queryAllSmsThreads(context);
        for(ISmsListBean listBean : threads){
            SmsThreadBean threadBean = (SmsThreadBean)listBean;

            SmsThread thread = new SmsThread();
            thread.setmThreasInfo(threadBean);
            thread.setmSmsList(queryAllSmsByThreadId(context,threadBean));
            result.add(thread);
        }
        return result;
    }

    /**
     * 更新sms记录locked字段的信息
     * @param context
     * @param smsBean
     * @param isLocked
     * @return
     */
    public synchronized boolean updateSmsLockState(Context context,SmsBean smsBean,boolean isLocked){
        ContentValues values = new ContentValues();
        values.put("locked",isLocked?1:0);
        return context.getContentResolver().update(Uri.parse(getUriByType(0)),
                values,"_id = ?",new String[]{smsBean.get_id()+""}) > 0;
    }

    /**
     * 更新threads的阅读状态
     * @param context
     * @param bean
     * @param read
     * @return
     */
    public boolean updateSmsThreadReadState(Context context,SmsThreadBean bean,boolean read){
        return updateSmsReadState(context,bean.get_id(),read);
    }

    /**
     * md5 加密
     * @param context
     */
    public synchronized void encodeSms(Context context){
        ContentResolver resolver = context.getContentResolver();
        EncodeDecodeUtil util = EncodeDecodeUtil.getInstance();
        Uri uri = Uri.parse(Constant.SMS_URI_ALL);
        int id;
        String body;
        int date;
        ContentValues values = new ContentValues();
        Cursor cursor = resolver.query(uri,
                new String[]{"_id", "body","date"}, null, null, null);
        while (cursor.moveToNext()){
            values.clear();
            id = cursor.getInt(0);
            body = cursor.getString(1);
            date = cursor.getInt(2);
            values.put("body",util.encode(body));
            values.put("date",date);
            L.i(TAG, "encode body = " + values.get("body"));
            resolver.update(Uri.withAppendedPath(uri,id+""),values,null,null);
        }
        cursor.close();
    }

    /**
     * md5 解密
     * @param context
     */
    public synchronized void decodeSms(Context context){
        ContentResolver resolver = context.getContentResolver();
        EncodeDecodeUtil util = EncodeDecodeUtil.getInstance();
        Uri uri = Uri.parse(Constant.SMS_URI_ALL);
        int id;
        String body;
        int date;
        ContentValues values = new ContentValues();
        Cursor cursor = resolver.query(uri,
                new String[]{"_id", "body","date"}, null, null, null);
        while (cursor.moveToNext()){
            values.clear();
            id = cursor.getInt(0);
            body = cursor.getString(1);
            date = cursor.getInt(2);
            values.put("body", util.decode(body));
            values.put("date",date);
            resolver.update(Uri.withAppendedPath(uri,id+""),values,null,null);
        }
        cursor.close();
    }

    private boolean updateSmsReadState(Context context,int threadId,boolean read){
        ContentValues values = new ContentValues();
        values.put("read", read ? 1 : 0);
        return context.getContentResolver()
                .update(Uri.parse(Constant.SMS_URI_ALL),
                        values, "thread_id=?", new String[]{threadId + ""}) > 0;
    }

    /**
     * 辅助方法,根据threadId判断其中是否有被锁定的信息记录
     * @param resolver
     * @param threadBean
     * @return
     */
    private boolean smsIsLocked(ContentResolver resolver,SmsThreadBean threadBean){
        if(resolver == null)
            return false;
//        Cursor cursor = resolver.query(Uri.parse(getUriByType(0)),
//                new String[]{"locked"},"thread_id = ?",
//                new String[]{threadBean.get_id()+""},null);
//        if(cursor != null){
//            while (cursor.moveToNext()){
//                if(cursor.getInt(0) == 1)
//                    return true;
//            }
        Cursor cursor = resolver.query(Uri.parse(getUriByType(0)),
                new String[]{"COUNT(*)"},"locked = 1 and thread_id = ?",
                new String[]{threadBean.get_id()+""},null);
        if(cursor != null && cursor.moveToFirst()){
            return cursor.getInt(0) > 0;
        }
        return false;
    }

    /***
     * 辅助方法,根据type来获取sms存储的uri
     * @param type
     * @return
     */
    private final String getUriByType(final int type){
        switch (type){
            case 1:
                return Constant.SMS_INBOX_URI;
            case 2:
                return Constant.SMS_SEND_URI;
            case 3:
                return Constant.SMS_DRAFT_URI;
            case 4:
                return Constant.SMS_OUTBOX_URI;
            case 5:
                return Constant.SMS_FAILED_URI;
            case 6:
                return Constant.SMS_QUEUED_URI;
            case 0:
            default:
                return Constant.SMS_URI_ALL;
        }
    }




}
