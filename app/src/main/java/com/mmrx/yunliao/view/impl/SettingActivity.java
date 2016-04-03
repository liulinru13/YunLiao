package com.mmrx.yunliao.view.impl;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.FragmentPresenter;
import com.mmrx.yunliao.view.AbsActivity;
import com.mmrx.yunliao.view.IFragmentListener;
import com.mmrx.yunliao.view.fragment.SettingFragment;

public class SettingActivity extends AbsActivity
        implements IFragmentListener{

    private SettingFragment mFmain,mFcontrol,mFprivate,mFnotice;
    private FragmentPresenter mPresenter;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mToolBar= (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("设置");
        setSupportActionBar(mToolBar);
        mPresenter.fragmentSelection_replace(Constant.S_MAIN, true);

    }

    @Override
    public void init() {
        mPresenter = new FragmentPresenter(this,this.getFragmentManager(),
                R.id.setting_container,this);
        mFmain = SettingFragment.newInstance(Constant.S_MAIN);
        mFcontrol = SettingFragment.newInstance(Constant.SP_SETTING_MAIN_CONTROL);
        mFprivate = SettingFragment.newInstance(Constant.SP_SETTING_MAIN_PRIVATE);
        mFnotice = SettingFragment.newInstance(Constant.SP_SETTING_MAIN_NOTICE);
        mPresenter.putFragment(new String[]{
                        Constant.S_MAIN
                        ,Constant.SP_SETTING_MAIN_CONTROL
                        ,Constant.SP_SETTING_MAIN_NOTICE
                        ,Constant.SP_SETTING_MAIN_PRIVATE},
                mFmain,mFcontrol,mFnotice,mFprivate);
    }

    @Override
    public void onFragmentChanged(String fragment, String fragmentType) {
        mPresenter.fragmentSelection_replace(fragmentType,false);
    }

    @Override
    public void changeTitle(String title) {
        mToolBar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if(mPresenter.back())
            return;

        super.onBackPressed();
    }
}
