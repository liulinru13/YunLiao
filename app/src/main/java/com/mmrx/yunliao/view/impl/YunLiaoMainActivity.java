package com.mmrx.yunliao.view.impl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.view.AbsActivity;

import com.daimajia.androidanimations.library.*;
import com.mmrx.yunliao.view.IFragmentListener;
import com.mmrx.yunliao.view.fragment.SettingFragment;
import com.mmrx.yunliao.view.fragment.SmsEditFragment;
import com.mmrx.yunliao.view.fragment.SmsListFragment;

import java.util.Stack;

import butterknife.Bind;


public class YunLiaoMainActivity extends AbsActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        IFragmentListener{

//    private String mPreFragment;
    private Stack<String> mFragmentStack;
    private SmsListFragment mListFragment;
    private SmsEditFragment mEditFragment;
    private SettingFragment mSettingFragment;

    private FragmentManager mFragmentManager;

    private FloatingActionButton mFloatBn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_liao_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFloatBn = (FloatingActionButton) findViewById(R.id.fab);
        mFloatBn.setOnClickListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //显示短信列表页面
        setFragmentSelection(Constant.LIST,null);
    }

    @Override
    public void init() {
        this.mFragmentManager = getFragmentManager();
        this.mFragmentStack = new Stack<String>();
//        this.mListFragment = new SmsListFragment();
//        this.mEditFragment = new SmsEditFragment();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();null
                setFragmentSelection(Constant.EDIT,null);
                break;
            default:
                break;
        }
    }

    /**
     * 根据参数选择显示的fragment
     * @param selection
     * @param settingType 设置界面的的类型,当前一个参数非SETTING时,可以为null
     */
    private void setFragmentSelection(final String selection,String settingType){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //后退是使用两套策略
        // 非子设置界面时(settingType == null),使用栈来存储,防止重复入栈
        // 子设置界面时(settingType != null),使用FragmentTransaction自己的回退栈来解决.
        if(settingType == null &&(mFragmentStack.isEmpty() || !mFragmentStack.peek().equals(selection)))
            mFragmentStack.push(selection);
        hideAllFragment(transaction);
        switch (selection){
            case Constant.LIST:
                if(mListFragment == null) {
                    mListFragment = new SmsListFragment();
                    mListFragment.setFragmentListener(this);
                    transaction.add(R.id.fragment_container, mListFragment, Constant.LIST);
                }else{
                    transaction.show(mListFragment);
                }
                mFloatBn.setVisibility(View.VISIBLE);
                break;
            case Constant.EDIT:
                if(mEditFragment == null){
                    mEditFragment = new SmsEditFragment();
                    mEditFragment.setFragmentListener(this);
                    transaction.add(R.id.fragment_container,mEditFragment,Constant.EDIT);
                }else{
                    transaction.show(mEditFragment);
                }
                mFloatBn.setVisibility(View.GONE);
                break;
            case Constant.SETTING:
                if(settingType == null){
                    if(mSettingFragment == null){
                        mSettingFragment = SettingFragment.newInstance(Constant.S_MAIN);
                        mSettingFragment.setFragmentListener(this);
                        transaction.add(R.id.fragment_container,mSettingFragment,Constant.SETTING);
                    }else{
                        transaction.show(mSettingFragment);
                    }
                }
                //这里应该是从主要设置界面进入的
                else{
                    transaction.replace(R.id.fragment_container,SettingFragment.newInstance(settingType,this));
                    transaction.addToBackStack(null);
                }
                mFloatBn.setVisibility(View.GONE);
                break;
        }
        transaction.commit();

    }

    /**
     * 隐藏所有fragment
     * */
    private void hideAllFragment(FragmentTransaction transaction){
        if(mEditFragment != null)
            transaction.hide(mEditFragment);
        if(mListFragment != null)
            transaction.hide(mListFragment);
        if(mSettingFragment != null)
            transaction.hide(mSettingFragment);
    }
    //当前栈顶的tag是当前页面的tag,之前页面的tag是第二次pop的内容
    private String getLastFragmentTag(){
        if((mFragmentStack.isEmpty() || mFragmentStack.size() < 2)) {
            return null;
        }
        mFragmentStack.pop();
        return mFragmentStack.pop();
    }

    /**
     * 判断三个主要的fragment是否都不显示
     * 当返回值为true时,说明此时的回退策略应该是使用FragmentTransaction的回退栈
     * 而不是自定义的stack
     * @return
     */
    private boolean isAllFragmentHide(){
        boolean isHide =
                (mEditFragment == null || !mEditFragment.isVisible())
                && (mListFragment == null || !mListFragment.isVisible())
                && (mSettingFragment == null || !mSettingFragment.isVisible());
        return isHide;
    }

    /**
     * 回退判断策略
     * 1.先判断侧边栏
     * 2.判断FragmentManager的回退栈
     * 3.判断自己维护的回退栈mFragmentStack
     * 4.系统默认的返回操作
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //如果侧边栏是开启状态,则关闭侧边栏
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        //list,edit,主setting页面都隐藏,则说明
        else if(isAllFragmentHide()){

            mFragmentManager.popBackStack();
            //回退后,所有的fragment会同时显示,需要仅显示mFragmentStack栈顶的页面
            if(!mFragmentStack.isEmpty()){
                setFragmentSelection(mFragmentStack.peek(),null);
            }
            return;
        }else{
            //当前是编辑短信的显示状态,则需要回退到短信列表而不是退出应用
            String tag = null;
            if((tag = getLastFragmentTag())!= null){
                setFragmentSelection(tag,null);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.yun_liao_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //备份
        if (id == R.id.nav_back_up) {

        }
        //设置
        else if (id == R.id.nav_setting) {
            setFragmentSelection(Constant.SETTING,null);
        }
        //关于应用
        else if (id == R.id.nav_app) {

        }
        //关于作者
        else if (id == R.id.nav_author) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentChanged(String fragment, String fragmentType) {
        setFragmentSelection(fragment,fragmentType);
    }
}
