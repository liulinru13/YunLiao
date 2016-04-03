package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/3/7.
 */

import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

import com.mmrx.yunliao.model.GroupSmsDBhelper;
import com.mmrx.yunliao.model.SmsDBhelper;
import com.mmrx.yunliao.presenter.IClean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
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

    private ExecutorService mCacheThreadPool;

    private SimpleDateFormat mSimpleDateFormat;//格式 "yyyy-MM-dd hh:mm:ss"
    private MiddlewareProxy(){
        mDialogFactory = new CustomDialog();
        mSmsDBhelper = SmsDBhelper.getInstance();
        mGroupSmsDBhelper = GroupSmsDBhelper.getInstance();
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
     * 显示对话框
     * @param manager fragment管理器
     * @param tag 管理的标签
     * @param title 标题
     * @param message 对话框内容
     * @param listener 监听器
     * */
    public void createDialog(FragmentManager manager,String tag,String title,
                             String message, CustomDialog.CustomDialogListener listener){
        mDialogFactory.newInstance(title,message,listener).show(manager,tag);
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

    @Override
    public void clear() {

    }
}
