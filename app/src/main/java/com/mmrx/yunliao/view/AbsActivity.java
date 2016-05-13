package com.mmrx.yunliao.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.SPUtil;

/**
 * @author mmrx
 * 作为本工程下所有activity的父类
 * */
public abstract class AbsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        init();
    }
    /**
     * 初始化控件
     * */
    public abstract void init();

    /**
     * 设置当前应用主题
     */
    private void setTheme(){
//        Integer themeResId = (Integer)SPUtil.get(this, Constant.SP_F_THEME,Constant.SP_K_THEME,Constant.SP_TYPE_INT);
        //白天
        if((boolean)SPUtil.getPreference(this).getBoolean(Constant.SP_SETTING_MAIN_THEME,true)){
            setTheme(R.style.dayTheme);
        }else{
            setTheme(R.style.nightTheme);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
