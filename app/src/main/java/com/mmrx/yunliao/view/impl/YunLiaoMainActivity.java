package com.mmrx.yunliao.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.bean.group.SmsGroupThread;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.presenter.FragmentPresenter;
import com.mmrx.yunliao.presenter.util.BackupFileMaker;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.presenter.util.SPUtil;
import com.mmrx.yunliao.presenter.util.XmlWritter;
import com.mmrx.yunliao.view.AbsActivity;
import com.mmrx.yunliao.view.IFragmentListener;
import com.mmrx.yunliao.view.fragment.BackUpFragment;
import com.mmrx.yunliao.view.fragment.SmsEditFragment;
import com.mmrx.yunliao.view.fragment.SmsListFragment;

import java.util.List;

import butterknife.ButterKnife;


public class YunLiaoMainActivity extends AbsActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        IFragmentListener{

    private SmsListFragment mListFragment;
    private SmsEditFragment mEditFragment;
    private BackUpFragment mBackUpFragment;

    private FragmentPresenter mPresenter;
    private FloatingActionButton mFloatBn;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;

    private View mLoginLayout;
    private View mAccLayout;

    private TextView mLoginBn;
    private TextView mExitBn;
    private TextView mAccTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_liao_main);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("信息");
        setSupportActionBar(mToolbar);

        mFloatBn = (FloatingActionButton) findViewById(R.id.fab);
        mFloatBn.setOnClickListener(this);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        mAccLayout = navigationView.getHeaderView(0).findViewById(R.id.header_account_layout);
        mLoginLayout = navigationView.getHeaderView(0).findViewById(R.id.header_login_layout);

        mLoginBn = (TextView)mLoginLayout.findViewById(R.id.header_login_bn);
        mExitBn = (TextView)mAccLayout.findViewById(R.id.header_exit_bn);
        mAccTv = (TextView)mAccLayout.findViewById(R.id.account_tv);
        mLoginBn.setOnClickListener(this);
        mExitBn.setOnClickListener(this);

        //显示短信列表页面
        mPresenter.putFragment(mListFragment, mEditFragment,mBackUpFragment);
        mPresenter.fragmentSelection_show_hide(mListFragment.getFragmentTag(), null);
        signCheck();
    }

    @Override
    public void init() {
        mPresenter = new FragmentPresenter(this,this.getFragmentManager(),
                R.id.fragment_container,this);
        mListFragment = new SmsListFragment();
        mEditFragment = new SmsEditFragment();
        mBackUpFragment = new BackUpFragment();
    }

    private void signCheck(){
        String acc = (String)SPUtil.get(this, Constant.SP_F_SETTING_CODE,Constant.SP_K_ACC,"");
        //未登录
        if(acc.equals("")){
            mAccLayout.setVisibility(View.GONE);
            mLoginLayout.setVisibility(View.VISIBLE);
        }else{
            mAccLayout.setVisibility(View.VISIBLE);
            mLoginLayout.setVisibility(View.GONE);
            mAccTv.setText(acc);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
//                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();null
//                setFragmentSelection(Constant.EDIT,null);
                mPresenter.fragmentSelection_show_hide(mEditFragment.getFragmentTag(),null);
                mFloatBn.setVisibility(View.GONE);
                break;
            //登录
            case R.id.header_login_bn:
                mDrawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent();
                intent.setClass(this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.header_exit_bn:
                SPUtil.put(this, Constant.SP_F_SETTING_CODE,Constant.SP_K_ACC,"");
                SPUtil.put(this, Constant.SP_F_SETTING_CODE,Constant.SP_K_PWD,"");
                signCheck();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MiddlewareProxy.getInstance().checkDefaultSmsApp(this);
        signCheck();
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
            mDrawer.closeDrawer(GravityCompat.START);
            mPresenter.fragmentSelection_show_hide(mBackUpFragment.getFragmentTag(),null);
        }
        //设置
        else if (id == R.id.nav_setting) {
            mDrawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
        }
        //关于应用
        else if (id == R.id.nav_app) {

        }
        //关于作者
        else if (id == R.id.nav_author) {

        }


        return true;
    }

    @Override
    public void onFragmentChanged(String fragment, Object obj) {
        if(fragment.equals(mListFragment.getFragmentTag())) {
            mFloatBn.setVisibility(View.VISIBLE);
            //点击列表的事件
            if(obj != null){
//                Object param = null;
//                if(obj instanceof SmsThreadBean){
//                    SmsThread bean = new SmsThread();
//                    bean.setmThreasInfo((SmsThreadBean)obj);
//                    param = bean;
//                }else if(obj instanceof SmsGroupThreadsBean){
//                    SmsGroupThread bean = new SmsGroupThread();
//                    bean.setmGroupInfo((SmsGroupThreadsBean)obj);
//                    param = bean;
//                }
                mPresenter.fragmentSelection_show_hide(mEditFragment.getFragmentTag(),obj);
            }
        }
        else {
            mFloatBn.setVisibility(View.GONE);
        }


    }

    @Override
    public void changeTitle(String title) {
        mToolbar.setTitle(title);
    }
}
