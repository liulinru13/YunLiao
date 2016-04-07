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

import com.mmrx.yunliao.model.bean.group.SmsGroupThreadsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.model.db.ContactsDBhelper;
import com.mmrx.yunliao.model.db.GroupSmsDBhelper;
import com.mmrx.yunliao.model.db.SmsDBhelper;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.IClean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建人: mmrx
 * 时间: 16/3/7上午10:12
 * 描述: 中间件,提供可复用的功能接口以及数据库相关操作
 */
public class MiddlewareProxy implements IClean{
    private static MiddlewareProxy mInstance;

    private CustomDialog mDialogFactory;//对话框工厂
    private SmsDBhelper mSmsDBhelper;//sms数据库操作类
    private GroupSmsDBhelper mGroupSmsDBhelper;//群发sms数据库操作类
    private ContactsDBhelper mContactsDBhelper;//联系人数据库操作类

    private ExecutorService mCacheThreadPool;

    private SimpleDateFormat mSimpleDateFormat;//格式 "yyyy-MM-dd hh:mm:ss"
    private MiddlewareProxy(){
        mDialogFactory = new CustomDialog();
        mSmsDBhelper = SmsDBhelper.getInstance();
        mGroupSmsDBhelper = GroupSmsDBhelper.getInstance();
        mContactsDBhelper = ContactsDBhelper.getInstance();
        mCacheThreadPool = Executors.newCachedThreadPool();
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
                if(displayName == null)
                    stb.setContact(stb.getAddresses());
                else
                    stb.setContact(displayName);
            }
        }
        return list;
    }

    public List<ISmsListBean> getSmsGroupThreadList(Context context){
        List<ISmsListBean> list = this.mGroupSmsDBhelper.getAllGroupThread();
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
            }
        }


        return list;
    }

    @Override
    public void clear() {

    }
}
