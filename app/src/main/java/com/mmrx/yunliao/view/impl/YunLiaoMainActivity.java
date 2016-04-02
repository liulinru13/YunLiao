package com.mmrx.yunliao.view.impl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.mmrx.yunliao.presenter.FragmentPresenter;
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

    private SmsListFragment mListFragment;
    private SmsEditFragment mEditFragment;
    private FragmentPresenter mPresenter;
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
        mPresenter.putFragment(mListFragment,mEditFragment);
        mPresenter.fragmentSelection_show_hide(mListFragment.getFragmentTag());
    }

    @Override
    public void init() {
        mPresenter = new FragmentPresenter(this,this.getFragmentManager(),
                R.id.fragment_container,this);
        mListFragment = new SmsListFragment();
        mEditFragment = new SmsEditFragment();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();null
//                setFragmentSelection(Constant.EDIT,null);
                mPresenter.fragmentSelection_show_hide(mEditFragment.getFragmentTag());
                mFloatBn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
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
        if(this.mPresenter.back())
            return;
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
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
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
//        setFragmentSelection(fragment,fragmentType);
        if(fragment.equals(mListFragment.getFragmentTag()))
            mFloatBn.setVisibility(View.VISIBLE);
        else
            mFloatBn.setVisibility(View.GONE);
    }
}
