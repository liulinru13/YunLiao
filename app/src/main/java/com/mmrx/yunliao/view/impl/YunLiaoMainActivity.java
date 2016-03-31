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

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.AbsActivity;

import com.daimajia.androidanimations.library.*;
import com.mmrx.yunliao.view.fragment.SettingFragment;
import com.mmrx.yunliao.view.fragment.SmsEditFragment;
import com.mmrx.yunliao.view.fragment.SmsListFragment;

import java.util.Stack;


public class YunLiaoMainActivity extends AbsActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

//    private final String NULL = "NULL";
    private final String LIST = "FRAGMENT_LIST";
    private final String EDIT = "FRAGMENT_EDIT";
    private final String SETTING = "FRAGMENT_SETTING";

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
        setFragmentSelection(LIST);

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
//                        .setAction("Action", null).show();
                setFragmentSelection(EDIT);
                break;
            default:
                break;
        }
    }

    /**
     * 根据参数选择显示的fragment
     * @param selection
     */
    private void setFragmentSelection(final String selection){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mFragmentStack.push(selection);
        hideAllFragment(transaction);
        switch (selection){
            case LIST:
                if(mListFragment == null) {
                    mListFragment = new SmsListFragment();
                    transaction.add(R.id.fragment_container, mListFragment, LIST);
                }else{
                    transaction.show(mListFragment);
                }
                mFloatBn.setVisibility(View.VISIBLE);
                break;
            case EDIT:
                if(mEditFragment == null){
                    mEditFragment = new SmsEditFragment();
                    transaction.add(R.id.fragment_container,mEditFragment,EDIT);
                }else{
                    transaction.show(mEditFragment);
                }
                mFloatBn.setVisibility(View.GONE);
                break;
            case SETTING:
                if(mSettingFragment == null){
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.fragment_container,mSettingFragment,SETTING);
                }else{
                    transaction.show(mSettingFragment);
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
        if(mFragmentStack.isEmpty() || mFragmentStack.size() < 2) {
            return null;
        }
        mFragmentStack.pop();
        return mFragmentStack.pop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //如果侧边栏是开启状态,则关闭侧边栏
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
            //当前是编辑短信的显示状态,则需要回退到短信列表而不是退出应用
            String tag = null;
            if((tag = getLastFragmentTag())!= null){
                setFragmentSelection(tag);
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
            setFragmentSelection(SETTING);
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
}
