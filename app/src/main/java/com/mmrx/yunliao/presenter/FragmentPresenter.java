package com.mmrx.yunliao.presenter;/**
 * Created by mmrx on 16/4/2.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.view.IFragmentListener;
import com.mmrx.yunliao.view.fragment.IFragment;
import com.mmrx.yunliao.view.fragment.IFragmentOnScreen;
import com.mmrx.yunliao.view.fragment.SettingFragment;
import com.mmrx.yunliao.view.fragment.SmsEditFragment;
import com.mmrx.yunliao.view.fragment.SmsListFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 创建人: mmrx
 * 时间: 16/4/2下午2:06
 * 描述: 负责管理Fragment的切换
 */
public class FragmentPresenter implements IFragmentListener,IFragmentOnScreen{
    private FragmentManager mFmanager;

    private Stack<String> mFragmentStack;//回退栈
    private HashMap<String,Fragment> mFragmentMap;//
    private IFragmentListener listener;
    private int mContainerId;

    private Context mContext;

    public FragmentPresenter(Context context,FragmentManager manager,
                             int containerId,IFragmentListener listener){
        this.mContext = context;
        this.mFmanager = manager;
        this.listener = listener;
        this.mContainerId = containerId;
        mFragmentStack = new Stack<String>();
        mFragmentMap = new HashMap<String,Fragment>();
    }

    /**
     * 填充需要管理的Fragment
     * @param args
     */
    public void putFragment(IFragment ...args){
        if(mFragmentMap != null){
            for(IFragment fragment : args){
                mFragmentMap.put(fragment.getFragmentTag(),(Fragment)fragment);
                fragment.setFragmentListener(this);
            }
        }
    }

    /**
     * 填充需要管理的Fragment
     * @param tags tag数组
     * @param args fragment数组
     */
    public void putFragment(String[] tags,IFragment ...args){
        if(mFragmentMap != null && tags.length == args.length){
            int index = 0;
            for(IFragment fragment : args){
                mFragmentMap.put(tags[index++],(Fragment)fragment);
                fragment.setFragmentListener(this);
            }
        }
    }

    /**
     * 通过隐藏所有fragment,显示其中一个的方式来进行Fragment的切换
     * @param tag
     * @param obj 显示前需要做的操作的参数
     */
    public void fragmentSelection_show_hide(String tag,Object obj){

        //保证相同的fragment不会多次出现在栈顶
        if(mFragmentStack.isEmpty() || !mFragmentStack.peek().equals(tag))
            mFragmentStack.push(tag);

        FragmentTransaction transaction = this.mFmanager.beginTransaction();

        hideAllFragment(transaction);
        Fragment fragment = this.mFragmentMap.get(tag);
        if(!hasFragmentByTag(tag)){
            transaction.add(mContainerId, fragment, tag);
        }else{
            transaction.show(fragment);
        }
        if(fragment instanceof IFragment){
            ((IFragment) fragment).onForeground(obj);
        }
        transaction.commit();
        //通过show/hide来进行fragment的切换时,需要手动更新activity上的title
        setTitle((IFragment) fragment);
        listener.onFragmentChanged(tag,null);
    }



    /**
     * 通过replace的方式来进行Fragment的切换
     * @param tag fragment的tag
     * @param isFirst 是否是第一次调用该方法,初始化界面时应该为true,其他填false
     */
    public void fragmentSelection_replace(String tag,boolean isFirst){
        FragmentTransaction transaction = this.mFmanager.beginTransaction();
        Fragment fragment = this.mFragmentMap.get(tag);
        if(isFirst){
            transaction.add(this.mContainerId,fragment,tag);
        }else{
            transaction.replace(this.mContainerId,fragment,tag);
            transaction.addToBackStack(null);
        }
        if(fragment instanceof IFragment){
            ((IFragment) fragment).onForeground(null);
        }
        transaction.commit();
        //通过replace进行fragment切换时,涉及到fragment生命周期方法的调用,
        //所以可以监听哪个fragment的onStart方法,判断是否在当前屏幕上进行显示
        //不需要显式调用下面的这个来设置title
//        setTitle((IFragment) fragment);
    }

    private void setTitle(IFragment fragment){
        listener.changeTitle(fragment.getFragmentTitle());
    }

    /**
     * 隐藏所有已经被add了的fragment
     * @param transaction
     */
    public void hideAllFragment(FragmentTransaction transaction){
        if(this.mFragmentMap != null) {
            for (Map.Entry<String, Fragment> entry : this.mFragmentMap.entrySet()) {
                //该fragment已经被添加到页面上
                if (hasFragmentByTag(entry.getKey())) {
                    Fragment fragment = entry.getValue();
                    if (fragment instanceof IFragment)
                        ((IFragment) fragment).onBackground(null);
                    transaction.hide(fragment);
                }
            }
        }
    }

    /**
     * 获取上一次显示的页面的tag
     * 当前栈顶的tag是当前页面的tag,之前页面的tag是第二次pop的内容
     * 该方法仅在使用 fragmentSelection_show_hide 的前提下使用
     * @return
     */
    private String getLastFragmentTag(){
        if((mFragmentStack.isEmpty() || mFragmentStack.size() < 2)) {
            return null;
        }
        mFragmentStack.pop();
        return mFragmentStack.pop();
    }

    /**
     * 当前是否含有标签为tag的fragment
     * @param tag
     * @return
     */
    private boolean hasFragmentByTag(String tag){
        return this.mFmanager.findFragmentByTag(tag) != null;
    }

    @Override
    public void onFragmentChanged(String fragment, Object obj) {
        this.listener.onFragmentChanged(fragment,obj);
    }

    @Override
    public void changeTitle(String title) {
        listener.changeTitle(title);
    }

    @Override
    public void onTheScreen(IFragment fragment) {
        listener.changeTitle(fragment.getFragmentTitle());
    }

    public boolean back(){
        //根据调用的不同的fragment切换方法不同,回退方式也分为两种
        //针对hide-show的回退方式,使用自定义的回退栈
        String tag = null;
        if((tag = getLastFragmentTag())!= null){
            fragmentSelection_show_hide(tag,null);
            return true;
        }
        //针对replace的方式,使用自带的回退栈
        return mFmanager.popBackStackImmediate();
    }
}
