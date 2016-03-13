package com.mmrx.yunliao.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.bean.group.SmsCroupUserBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadSend;
import com.mmrx.yunliao.model.bean.sms.SmsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.model.exception.YlDBisNullExcetption;
import com.mmrx.yunliao.presenter.IClean;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MyToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmrx on 16/3/8.
 * 操作sms数据库的辅助类
 */
public class SmsDBhelper implements IClean{
    private final String TAG = "SmsDBhelperLog";
    private static SmsDBhelper ourInstance = new SmsDBhelper();
    private YlDBhelper mYlDbHelper;
    public static SmsDBhelper getInstance() {
        return ourInstance;
    }

    private SmsDBhelper() {
    }

    public void initDB(Context context){
        this.mYlDbHelper = new YlDBhelper(context);
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

        L.i(TAG, "deleteSMSById" + sms.get_id());
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
    public void writeSmsToDB(Context context,long date,int read
            ,int type,String address,String body){
        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("address",address);
        values.put("body",body);
        values.put("read",read);
        values.put("type", type);
        context.getContentResolver().insert(Uri.parse(getUriByType(type)),values);
    }

    /*=========================================群组短信=======================================*/



    public void writeGroupSms(SmsGroupThreadSend sendInfo) throws Exception{
        SQLiteDatabase db = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.mDb;
            db.beginTransaction();
            //创建一个新的threads记录
            int threadId = getLastId(Contant.DATABASE_YL_GROUP_THREADS_TABLE) +1;
            db.execSQL("INSERT INTO sms_group_threads_table "
                +"VALUES ("+ threadId + ",0);");
            //插入会话信息,bean中仅有date和body信息
            SmsGroupBean groupBean = sendInfo.getmGroupBean();
            db.execSQL("INSERT INTO sms_group_table "
                + "(date,body,status,thread_id) "
                + "VALUES ("
                + groupBean.getDate_long()
                + "," + groupBean.getBody()
                + ",0"
                + "," + threadId);
            //获取刚刚插入的记录的_id
            int groupId = getLastId(Contant.DATABASE_YL_GROUP_TABLE);
            //插入用户记录
            for(SmsCroupUserBean userBean : sendInfo.getmGroupUsersList()){
                writeGroupUserInfo(userBean,groupId);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally {
            if(db != null){
                db.endTransaction();
            }
        }
    }

    /**
     * 获取指定表的最新row的_id
     * @param table
     * @return
     * @throws Exception
     */
    private int getLastId(String table) throws Exception{
        isDBnull();
        SQLiteDatabase db = this.mYlDbHelper.mDb;
        Cursor cursor = db.rawQuery("select max(_id) from "+table, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    /**
     * 向群发短信数据库写入接收人信息
     * @param userInfo
     * @param groupId
     * @throws Exception
     */
    private void writeGroupUserInfo(SmsCroupUserBean userInfo,int groupId) throws Exception{
        try{
            isDBnull();
            SQLiteDatabase db = this.mYlDbHelper.mDb;
            db.execSQL("INSERT INTO sms_user_table (address,person,group_id) "
                    + "VALUES ("
                    + userInfo.getAddress()
                    + "," + userInfo.getPerson()
                    + "," + groupId + ");");

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * 根据type来获取sms存储的uri
     * @param type
     * @return
     */
    public final String getUriByType(final int type){
        switch (type){
            case 1:
                return Contant.SMS_INBOX_URI;
            case 2:
                return Contant.SMS_SEND_URI;
            case 3:
                return Contant.SMS_DRAFT_URI;
            case 4:
                return Contant.SMS_OUTBOX_URI;
            case 5:
                return Contant.SMS_FAILED_URI;
            case 6:
                return Contant.SMS_QUEUED_URI;
            case 0:
            default:
                return Contant.SMS_URI_ALL;
        }
    }

    /**
     * 判断群发记录数据库实例是否存在
     * @return
     * @throws Exception
     */
    private void isDBnull() throws YlDBisNullExcetption{
        if(this.mYlDbHelper.mDb == null)
            throw new YlDBisNullExcetption();
    }

    @Override
    public void clear(){
        if(this.mYlDbHelper != null && this.mYlDbHelper.mDb.isOpen()) {
            this.mYlDbHelper.mDb.close();
        }
    }

    class YlDBhelper{
        final String DATABASE_PATH = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + "/"
                + Contant.PACKAGE_NAME;

//        Context mContext;
        SQLiteDatabase mDb;
        public YlDBhelper(Context context){
//            this.mContext = context;
            this.mDb = initDB(context);
        }
        /**
         * @param context
         * 初始化应用数据库,如果不存在相应数据库,则将db文件导入
         * */
        SQLiteDatabase initDB(Context context){
            try{
                String dbFileName = DATABASE_PATH + "/" + Contant.DATABASE_NAME;
                File dir = new File(DATABASE_PATH);
                if(!dir.exists()){
                    dir.mkdir();
                }
                if(!(new File(dbFileName)).exists()){
                    InputStream is = context.getResources().openRawResource(R.raw.ylsmsdb);
                    FileOutputStream fos = new FileOutputStream(dbFileName);
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while((count = is.read(buffer)) > 0){
                        fos.write(buffer,0,count);
                    }
                    fos.close();
                    is.close();
                }
                return SQLiteDatabase.openOrCreateDatabase(dbFileName,null);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            return null;
        }//

    }//

}
