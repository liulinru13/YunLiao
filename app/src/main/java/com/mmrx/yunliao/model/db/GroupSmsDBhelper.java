package com.mmrx.yunliao.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.model.bean.group.SmsCroupUserBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadSend;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadsBean;
import com.mmrx.yunliao.model.exception.YlDBisNullExcetption;
import com.mmrx.yunliao.presenter.util.EncodeDecodeUtil;
import com.mmrx.yunliao.presenter.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/3/17下午3:09
 * 描述: 负责与群发短信数据库交互的类
 */
public class GroupSmsDBhelper {
    private final String TAG = "GroupSmsDBhelperLog";
    private static GroupSmsDBhelper instance = new GroupSmsDBhelper();
    private YlDBhelper mYlDbHelper;

    private GroupSmsDBhelper(){}

    public static GroupSmsDBhelper getInstance(){
        return instance;
    }

    public void initDB(Context context){
        if(this.mYlDbHelper == null) {
            this.mYlDbHelper = new YlDBhelper(context, Constant.DATABASE_NAME, null, 1);
            getAllTableName();
        }
    }

    /**
     * 向数据库中写入新的群发记录
     * @param sendInfo
     * @throws Exception
     */
    public synchronized void insertNewGroupSms(SmsGroupThreadSend sendInfo) throws Exception{
        SQLiteDatabase db = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            db.beginTransaction();
            //创建一个新的threads记录
            int threadId = getLastId(Constant.DATABASE_YL_GROUP_THREADS_TABLE) +1;
            db.execSQL("INSERT INTO sms_group_threads_table "
                    + "VALUES (" + threadId + ",0);");
            //插入会话信息,bean中仅有date和body信息
            SmsGroupBean groupBean = sendInfo.getmGroupBean();
            insertGroupBean(db,groupBean,threadId);
            //获取刚刚插入的记录的_id
            int groupId = getLastId(Constant.DATABASE_YL_GROUP_TABLE);
            //插入用户记录
            for(SmsCroupUserBean userBean : sendInfo.getmGroupUsersList()){
                writeGroupUserInfo(userBean,groupId);
            }
            db.setTransactionSuccessful();
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
     * 向已有的群发记录中插入一条记录
     * @param sendInfo
     * @throws Exception
     */
    public synchronized void insertSingleGroupSms(SmsGroupThreadSend sendInfo) throws Exception{
        SQLiteDatabase db = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            db.beginTransaction();
            //插入会话信息,bean中有date和body,thread_id信息
            SmsGroupBean groupBean = sendInfo.getmGroupBean();
            insertGroupBean(db,groupBean,null);
            //获取刚刚插入的记录的_id
            int groupId = getLastId(Constant.DATABASE_YL_GROUP_TABLE);
            //插入用户记录
            for(SmsCroupUserBean userBean : sendInfo.getmGroupUsersList()){
                writeGroupUserInfo(userBean,groupId);
            }
            db.setTransactionSuccessful();
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
     * 删除数据库内群发短信的某组记录
     * @param threadsBean
     */
    public synchronized boolean deleteGroupSmsThread(SmsGroupThreadsBean threadsBean){
        SQLiteDatabase db = null;
        boolean result = false;
        try{
            isDBnull();
            if(smsIsLocked(threadsBean))
                return false;
            db = this.mYlDbHelper.getWritableDatabase();
            result = db.delete(Constant.DATABASE_YL_GROUP_THREADS_TABLE,"_id = ?",new String[]{threadsBean.get_id()+""}) > 0;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除数据库内群发短信的某条记录
     * @param groupBean
     */
    public synchronized boolean deleteGroupSmsThread(SmsGroupBean groupBean){
        SQLiteDatabase db = null;
        boolean result = false;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            //删除失败
            if(db.delete(Constant.DATABASE_YL_GROUP_TABLE,"_id = ? and locked = 0",new String[]{groupBean.get_id()+""}) == 0)
                return result;
            //还需要判断下,删除该条记录后,该记录所属的thread是否为空了,如果为空,就需要把对应的thread也一并删除
            Cursor cursor = db.rawQuery("SELECT count(*) FROM " + Constant.DATABASE_YL_GROUP_TABLE + " WHERE thread_id = ?"
                    , new String[]{groupBean.getThread_id() + ""});
            if(cursor != null && cursor.moveToFirst()){
                if(cursor.getInt(0) == 0){
                    db.delete(Constant.DATABASE_YL_GROUP_THREADS_TABLE,"_id = ?",new String[]{groupBean.getThread_id()+""});
                }
            }
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public synchronized boolean deleteAll(){
        SQLiteDatabase db = null;
        boolean result = false;
        try {
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            db.beginTransaction();
            db.execSQL("DELETE FROM sms_user_table");
            db.execSQL("update sqlite_sequence set seq=0 where name='sms_user_table'");

            db.execSQL("DELETE FROM sms_group_table");
            db.execSQL("update sqlite_sequence set seq=0 where name='sms_group_table'");

            db.execSQL("DELETE FROM sms_group_threads_table");
            db.execSQL("update sqlite_sequence set seq=0 where name='sms_group_threads_table'");

            db.setTransactionSuccessful();
            result = true;
            //update sqlite_sequence set seq=0 where name='表名';
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null)
                db.endTransaction();
        }

        return result;
    }

    /**
     * 更新群发短信数据库中短信发送状态
     * @param userBean
     * @param status
     * @return 是否成功更新
     */
    public synchronized boolean updateGroupSmsStatus(SmsCroupUserBean userBean,Constant.SMS_GROUP_SATTUS status){
        SQLiteDatabase db = null;
        int row = 0;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("status",status.getValues());
            row = db.update(Constant.DATABASE_YL_GROUP_USER_TABLE, cv, "_id = ?", new String[]{userBean.get_id()+""});
        }catch (Exception e){
            e.printStackTrace();
        }
        return row >0 ;
    }

    /**
     * 更新群发短信数据库中单条记录的锁定状态
     * @param groupBean
     * @param isLocked
     * @return
     */
    public synchronized boolean updateSmsLockState(SmsGroupBean groupBean,boolean isLocked){
        SQLiteDatabase db = null;
        int row = 0;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("locked",isLocked?1:0);
            row = db.update(Constant.DATABASE_YL_GROUP_TABLE, cv, "_id = ?", new String[]{groupBean.get_id()+""});
        }catch (Exception e){
            e.printStackTrace();
        }
        return row >0 ;
    }

    /**
     * 获取群发记录中的thread数组
     * @return
     */
    public synchronized List<ISmsListBean> getAllGroupThread(){
        List<ISmsListBean> list = null;
        SQLiteDatabase db = null;
        Cursor cursor = null,cursor_person = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.getReadableDatabase();
            list = new ArrayList<ISmsListBean>();
            cursor = db.rawQuery("SELECT * FROM sms_group_threads_table", null);
            while (cursor.moveToNext()){
                SmsGroupThreadsBean bean = new SmsGroupThreadsBean();
                bean.set_id(cursor.getInt(0));
                bean.setMessage_count(cursor.getInt(1));
                bean.setDate_long(cursor.getLong(2));
                bean.setSnippet(cursor.getString(3));

                cursor_person = db.rawQuery("SELECT address FROM sms_group_table,sms_user_table "
                        + "WHERE sms_group_table._id = ? "
                        + "AND sms_user_table.group_id = sms_group_table.thread_id", new String[]{bean.get_id() + ""});
                StringBuilder sb_addr = new StringBuilder();
                while (cursor_person.moveToNext()){
                    sb_addr.append(cursor_person.getInt(0));
                    sb_addr.append(",");
                }
                cursor_person.close();
                bean.setAddress(sb_addr.toString().substring(0, sb_addr.length() - 1));
                list.add(bean);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
            if(cursor_person != null)
                cursor_person.close();
        }

        return list;
    }

    /**
     * utf8 加密
     */
    public synchronized void encodeSms(){
        SQLiteDatabase db = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            db.beginTransaction();
            int id;
            String body;
            Cursor cursor = db.rawQuery("SELECT _id,body FROM sms_group_table",null,null);
            ContentValues values = new ContentValues();
            EncodeDecodeUtil util = EncodeDecodeUtil.getInstance();
            while (cursor.moveToNext()){
                values.clear();
                id = cursor.getInt(0);
                body = cursor.getString(1);
                values.put("body",util.encode(body));
                db.update("sms_group_table",values,"_id="+id,null);
            }
            cursor.close();
            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null)
                db.endTransaction();
        }
    }

    /**
     * utf8 解密
     */
    public synchronized void decodeSms(){
        SQLiteDatabase db = null;
        try{
            isDBnull();
            db = this.mYlDbHelper.getWritableDatabase();
            db.beginTransaction();
            int id;
            String body;
            Cursor cursor = db.rawQuery("SELECT _id,body FROM sms_group_table",null,null);
            ContentValues values = new ContentValues();
            EncodeDecodeUtil util = EncodeDecodeUtil.getInstance();
            while (cursor.moveToNext()){
                values.clear();
                id = cursor.getInt(0);
                body = cursor.getString(1);
                values.put("body",util.decode(body));
                db.update("sms_group_table",values,"_id="+id,null);
            }
            cursor.close();
            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db != null)
                db.endTransaction();
        }
    }

    /**
     * 辅助方法,判断SmsGroupThreadsBean所属的短信记录中是否有被锁定的内容
     * @param groupThreadsBean
     * @return
     */
    private boolean smsIsLocked(SmsGroupThreadsBean groupThreadsBean){
        SQLiteDatabase db = null;
        boolean result = false;
        try{
            isDBnull();
            db = this.mYlDbHelper.getReadableDatabase();
            Cursor cursor = db.query(Constant.DATABASE_YL_GROUP_TABLE,
                        new String[]{"COUNT(*)"}, "locked = 1 and thread_id = ?",
                        new String[]{groupThreadsBean.get_id() + ""}, null, null, null);
            if(cursor != null && cursor.moveToFirst()){
                result = cursor.getInt(0) > 0;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 辅助方法,插入群发会话记录
     * @param db
     * @param groupBean
     * @param threadId 如果为null,则使用groupBean中的thread_id
     */
    private void insertGroupBean(SQLiteDatabase db,SmsGroupBean groupBean,Integer threadId){
        db.execSQL("INSERT INTO sms_group_table "
                + "(date,body,thread_id) "
                + "VALUES ("
                + groupBean.getDate_long()
                + ", '" + groupBean.getBody() + "'"
                + "," + (threadId == null ? groupBean.getThread_id() : threadId)
                + ");");
    }

    /**
     * 辅助方法,获取指定表的最新row的_id
     * @param table
     * @return
     * @throws Exception
     */
    private int getLastId(String table) throws Exception{
        isDBnull();
        SQLiteDatabase db = this.mYlDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max(_id) from "+table, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    /**
     * 辅助方法,向群发短信数据库写入接收人信息
     * @param userInfo
     * @param groupId
     * @throws Exception
     */
    private void writeGroupUserInfo(SmsCroupUserBean userInfo,int groupId) throws Exception{
        try{
            isDBnull();
            SQLiteDatabase db = this.mYlDbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO sms_user_table (address,person,group_id) "
                    + "VALUES ("
                    + "'" + userInfo.getAddress() + "'"
                    + "," + userInfo.getPerson()
                    + "," + groupId + ");");

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 判断群发记录数据库实例是否存在
     * @return
     * @throws Exception
     */
    private void isDBnull() throws YlDBisNullExcetption {
        if(this.mYlDbHelper == null)
            throw new YlDBisNullExcetption();
    }

    private void getAllTableName(){
        try {
            isDBnull();
        }catch (Exception e){
            e.printStackTrace();
        }
        Cursor cursor = this.mYlDbHelper.getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' order by name", null);
        StringBuilder sb = new StringBuilder();
        while (cursor.moveToNext()){
            sb.append(cursor.getString(0)+"\n");
        }
        L.i(TAG, sb.toString());
    }

//    class YlDBhelper{
//        final String DATABASE_PATH = "/data"
//                + Environment.getDataDirectory().getAbsolutePath()
//                + "/"
//                + Constant.PACKAGE_NAME;
//
////        Context mContext;
//        SQLiteDatabase mDb;
//        public YlDBhelper(Context context){
////            this.mContext = context;
//            this.mDb = initDB(context);
//        }
//        /**
//         * @param context
//         * 初始化应用数据库,如果不存在相应数据库,则将db文件导入
//         * */
//        SQLiteDatabase initDB(Context context){
//            try{
//                String dbFileName = DATABASE_PATH + "/" + Constant.DATABASE_NAME;
//                File dir = new File(DATABASE_PATH);
//                if(!dir.exists()){
//                    dir.mkdir();
//                }
//                if(!(new File(dbFileName)).exists()){
//                    InputStream is = context.getResources().openRawResource(R.raw.ylsmsdb);
//                    FileOutputStream fos = new FileOutputStream(dbFileName);
//                    byte[] buffer = new byte[8192];
//                    int count = 0;
//                    while((count = is.read(buffer)) > 0){
//                        fos.write(buffer,0,count);
//                    }
//                    fos.close();
//                    is.close();
//                }
//                return SQLiteDatabase.openOrCreateDatabase(dbFileName,null);
//
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }catch (IOException e1){
//                e1.printStackTrace();
//            }
//            return null;
//        }//
//
//    }//

    /**
     * 群聊数据库辅助类
     * 负责创建数据库和获取操作实例
     */
    class YlDBhelper extends SQLiteOpenHelper {

        //数据库版本
        private static final int VERSION = 1;

        public YlDBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context,name,factory,version);
        }

        public YlDBhelper(Context context, String name, int version){
            super(context,name,null,version);
        }

        public YlDBhelper(Context context, String name){
            super(context,name,null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //sms_group_threads_table
            db.execSQL("create table if not exists sms_group_threads_table("
                    + "_id INTEGER PRIMARY KEY NOT NULL,"
                    + "message_count integer DEFAULT 0,"
                    + "date integer,"
                    + "snippet text"
                    + ");");

            //sms_group_table
            db.execSQL("create table if not exists sms_group_table("
                    + "_id integer PRIMARY KEY NOT NULL,"
                    + "date integer,"
                    + "body text,"
//                    + "status integer DEFAULT -1,"
                    + "thread_id integer NOT NULL,"
                    + "locked integer DEFAULT 0,"
                    + "FOREIGN KEY (thread_id) REFERENCES sms_group_threads_table (_id));");

            //sms_user_table
            db.execSQL("create table if not exists sms_user_table("
                    + "_id integer PRIMARY KEY NOT NULL,"
                    + "address text,"
                    + "person integer,"
                    + "group_id integer NOT NULL,"
                    + "status integer DEFAULT "
                    + Constant.SMS_GROUP_SATTUS.WRITTING.getValues() + ","//默认状态为草稿
                    + "FOREIGN KEY (group_id) REFERENCES sms_group_table (_id));");

            //Triggers structure for table sms_group_table
            //在删除sms_group_table表中内容后,sms_group_threads_table表中对应记录减一
            db.execSQL("create trigger if not exists sms_group_delete "
                    + "AFTER DELETE ON sms_group_table "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE sms_group_threads_table "
                    + "SET message_count=message_count -1 "
                    + "WHERE _id = new.thread_id; "
                    + "END;");
            //在sms_group_table表中添加内容后,sms_group_threads_table表中对应记录加一
            db.execSQL("create trigger if not exists sms_group_update_or_insert "
                    + "AFTER INSERT ON sms_group_table "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE sms_group_threads_table "
                    + "SET message_count=message_count + 1,"
                    + "date = new.date,"
                    + "snippet = new.body "
                    + "WHERE _id = new.thread_id; "
                    + "END;");
            //sms_group_table表中删除某条记录之前,删除表sms_user_table中相关联联系人记录
            db.execSQL("create trigger if not exists sms_group_users_delete "
                    + "BEFORE DELETE ON sms_group_table "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "DELETE FROM sms_user_table "
                    + "WHERE group_id = OLD._id; "
                    + "END;");

            //Triggers structure for table sms_group_threads_table
            //sms_group_threads_table表中删除相关内容之前,删除sms_group_table表中相关联的内容
            db.execSQL("create trigger if not exists sms_group_threads_delete "
                    + "BEFORE DELETE ON sms_group_threads_table "
                    + "FOR EACH ROW "
                    + "BEGIN "
                    + "DELETE FROM sms_group_table "
                    + "WHERE thread_id = OLD._id; "
                    + "END;");


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
