package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/3/7.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mmrx.yunliao.SmsReceiver.ISmsObserver;
import com.mmrx.yunliao.model.bean.contacts.ContactsBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.model.db.ContactsDBhelper;
import com.mmrx.yunliao.model.db.GroupSmsDBhelper;
import com.mmrx.yunliao.model.db.SmsDBhelper;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.IClean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建人: mmrx
 * 时间: 16/3/7上午10:12
 * 描述: 中间件,提供可复用的功能接口以及数据库相关操作
 */
public class MiddlewareProxy implements IClean{
    private static MiddlewareProxy mInstance;

    private HashMap<String,ISmsObserver> mSmsObserverMap;

    private CustomDialog mDialogFactory;//对话框工厂
    private SmsDBhelper mSmsDBhelper;//sms数据库操作类
    private GroupSmsDBhelper mGroupSmsDBhelper;//群发sms数据库操作类
    private ContactsDBhelper mContactsDBhelper;//联系人数据库操作类

    private ExecutorService mCacheThreadPool;

    private SimpleDateFormat mSimpleDateFormat;//格式 "yyyy-MM-dd hh:mm:ss"

    private boolean isInit = false;
    private MiddlewareProxy(){
        mDialogFactory = new CustomDialog();
        mSmsDBhelper = SmsDBhelper.getInstance();
        mGroupSmsDBhelper = GroupSmsDBhelper.getInstance();
        mContactsDBhelper = ContactsDBhelper.getInstance();
        mCacheThreadPool = Executors.newCachedThreadPool();
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mSmsObserverMap = new HashMap<String,ISmsObserver>();
    }

    public static MiddlewareProxy getInstance(){
        if(mInstance == null){
            mInstance = new MiddlewareProxy();
        }
        return mInstance;
    }

    /**
     * 该方法在应用开始时被调用,初始化需要Context对象的资源
     * @param context
     */
    public void init(Context context){
        if(context == null)
            return;
        mGroupSmsDBhelper.initDB(context);
        isInit = true;
    }


    public boolean isInit(){
        return isInit;
    }
    /**
     * 添加sms监听
     * @param key
     * @param observer
     */
    public void setOnSmsChangedListener(String key,ISmsObserver observer){
        if(this.mSmsObserverMap.containsKey(key)){
            this.mSmsObserverMap.remove(key);
            this.mSmsObserverMap.put(key,observer);
        }
        else{
            this.mSmsObserverMap.put(key,observer);
        }
    }

    /**
     * 移除sms监听
     * @param key
     */
    public void removeSmsChangedListener(String key){
        if(this.mSmsObserverMap.containsKey(key)){
            this.mSmsObserverMap.remove(key);
        }
    }

    public HashMap<String,ISmsObserver> getmSmsObserverMap(){
        return this.mSmsObserverMap;
    }

    public void notifyAllSmsObserver(int event){
        HashMap<String,ISmsObserver> map = getmSmsObserverMap();
        if(map != null && map.size() > 0){
            for(Map.Entry<String,ISmsObserver> entry : map.entrySet()){
                if(entry.getValue() != null){
                    entry.getValue().onSmsNoticed(event);
                }
            }
        }//end if
    }

    /**
     * 检查默认短信应用是否是本应用
     * @param context
     */
    public void checkDefaultSmsApp(final Context context){
        if(Build.VERSION.SDK_INT >=19) {
            final String myPackageName = context.getPackageName();
            final String smsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            if (!smsPackageName.equals(myPackageName)) {
                createDialog(((Activity) context).getFragmentManager(), "dialog", "提示"
                        , "当前应用非系统默认短信应用,正常使用需设置为系统默认短信应用,是否设置为默认短信应用?"
                        , new CustomDialog.CustomDialogListener() {
                    @Override
                    public void doNegativeClick() {

                    }

                    @Override
                    public void doPositiveClick() {
                        Intent intent =
                                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                myPackageName);
                        context.startActivity(intent);
                    }
                });
            }
        }//end if
    }

    /**
     * 显示对话框
     * @param manager fragment管理器
     * @param tag 管理的标签
     * @param title 标题
     * @param message 对话框内容
     * @param listener 监听器
     * */
    public void createDialog(FragmentManager manager,String tag,String title,
                             String message, CustomDialog.CustomDialogListener listener){
        mDialogFactory.newInstance(title,message,listener).show(manager, tag);
    }

    /**
     * 显示对话框
     * @param manager fragment管理器
     * @param tag 管理的标签
     * @param title 标题
     * @param message 对话框内容
     * @param listener 监听器
     * @param posiShow 确定按钮是否显示
     * @param negaShwo 取消按钮是否显示
     * */
    public void createDialog(FragmentManager manager,String tag,String title,
                             String message, CustomDialog.CustomDialogListener listener,
                             boolean posiShow,boolean negaShwo){
        mDialogFactory.newInstance(title,message,listener,posiShow,negaShwo).show(manager,tag);
    }

    /**
     * 显示对话框
     * @param manager fragment管理器
     * @param tag 管理的标签
     * @param title 标题
     * @param message 对话框内容
     * @param contentView 自定义布局内容
     * @param listener 监听器
     * */
    public void createDialog(FragmentManager manager,String tag,String title,
                             String message, View contentView,
                             CustomDialog.CustomDialogListener listener){
        mDialogFactory.newInstance(title,message,contentView,listener).show(manager,tag);
    }

    /**
     * 显示对话框
     * @param manager fragment管理器
     * @param tag 管理的标签
     * @param title 标题
     * @param message 对话框内容
     * @param contentView 自定义布局内容
     * @param listener 监听器
     * @param posiShow 确定按钮是否显示
     * @param negaShwo 取消按钮是否显示
     * */
    public void createDialog(FragmentManager manager,String tag,String title,
                             String message, View contentView,
                             CustomDialog.CustomDialogListener listener,
                             boolean posiShow,boolean negaShwo){
        mDialogFactory.newInstance(title,message,contentView,listener,posiShow,negaShwo).show(manager,tag);
    }

    public CustomDialog getDialogFactory() {
        return mDialogFactory;
    }

    /**
     * @param date long型的日期
     * @return yyyy-MM-dd hh:mm:ss 格式的日期
     * */
    public String dateFormat(Long date){
        if(date == null)
            return null;
        Date temp = new Date(date);
        return mSimpleDateFormat==null? null : mSimpleDateFormat.format(temp);
    }

    public long getCurrentSystemTimeInLong(){
        return System.currentTimeMillis();
    }

    public ExecutorService getCacheThreadPool() {
        return mCacheThreadPool;
    }

    /*===================短信操作===================*/

    /**
     * 获取短信的thread数组
     * @param context
     * @return
     */
    public List<ISmsListBean> getSmsThreadList(Context context){
        List<ISmsListBean> list = this.mSmsDBhelper.queryAllSmsThreads(context);
        if(list != null && list.size() >0){
            //获取联系人信息
            for(ISmsListBean bean : list){
                SmsThreadBean stb = (SmsThreadBean)bean;
                String displayName = this.mContactsDBhelper.getPeopleNameFromPerson(context, stb.getAddresses());
                stb.setContact(displayName);

            }
        }
        return list;
    }

    /**
     * 获取所有的群发记录数组
     * @param context
     * @return
     */
    public List<ISmsListBean> getSmsGroupThreadList(Context context){
        List<ISmsListBean> list = this.mGroupSmsDBhelper.getAllGroupThread();
        List<ISmsListBean> listRes = new ArrayList<ISmsListBean>();
        if(list != null && list.size() >0){
            //获取联系人信息
            for(ISmsListBean bean : list){
                SmsGroupThreadsBean sgtb = (SmsGroupThreadsBean)bean;
                String[] addrs = sgtb.getAddress().split(",");
                if(addrs.length != 0) {
                    StringBuilder sb = new StringBuilder();
                    for(String str:addrs) {
                        sb.append(this.mContactsDBhelper.getPeopleNameFromPerson(context,str));
                        sb.append(",");

                    }
                    sgtb.setContacts(sb.toString().substring(0,sb.length()-1));
                }else{
                    sgtb.setContacts(sgtb.getAddress());
                }
                listRes.add(bean);
            }
        }
        return listRes;
    }

    /**
     * 删除短信trhead
     * @param context
     * @param bean
     * @return
     */
    public boolean deleteSmsThread(Context context,ISmsListBean bean){
        boolean popUpDialog = false;
        if(bean instanceof SmsThreadBean){
            popUpDialog = !mSmsDBhelper.deleteSmsThreadById(context,(SmsThreadBean)bean);
        }else if(bean instanceof SmsGroupThreadsBean){
            popUpDialog = !mGroupSmsDBhelper.deleteGroupSmsThread((SmsGroupThreadsBean)bean);
        }

        if(popUpDialog){
            new MaterialDialog.Builder(context)
                    .title("删除失败")
                    .content("记录内含有锁定状态的消息记录,请检查后再试")
                    .negativeText("确定")
                    .show();
        }
        return !popUpDialog;
    }

    /**
     * 设置thread是否已读
     * @param context
     * @param bean
     * @return
     */
    public boolean setSmsThreadsRead(Context context,ISmsListBean bean){
        if(bean instanceof SmsThreadBean){
            return mSmsDBhelper.updateSmsThreadReadState(context,(SmsThreadBean)bean,true);
        }else if(bean instanceof SmsGroupThreadsBean){
            return false;
        }
        return false;
    }

    public boolean deleteGroupSmsAll(){
        return this.mGroupSmsDBhelper.deleteAll();
    }

    public synchronized void encodeSmsAll(Context context){
        this.mSmsDBhelper.encodeSms(context);
    }

    public synchronized void decodeSmsAll(Context context){
        this.mSmsDBhelper.decodeSms(context);
    }

    public synchronized void encodeGroupSmsAll(){
        this.mGroupSmsDBhelper.encodeSms();
    }

    public synchronized void decodeGroupSmsAll(){
        this.mGroupSmsDBhelper.decodeSms();
    }


    /*===================联系人操作===================*/
    public List<ContactsBean> getAllContacts(Context context){
        return this.mContactsDBhelper.getAllContactsList(context);
    }

//    public

    @Override
    public void clear() {

    }
}
