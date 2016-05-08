package com.mmrx.yunliao.view.impl;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.SPUtil;
import com.mmrx.yunliao.view.AbsActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AbsActivity implements View.OnClickListener{

    @Bind(R.id.login_acc)
    EditText mAccEt;
    @Bind(R.id.login_pwd)
    EditText mPwdEt;

    @Bind(R.id.login_acc_notice_tv)
    TextView mAccNotTv;
    @Bind(R.id.login_pwd_notice_tv)
    TextView mPwdNotTv;

    @Bind(R.id.login_login_bn)
    Button mLoginBn;
    @Bind(R.id.login_regis_bn)
    Button mRegisBn;
    private final String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginBn.setOnClickListener(this);
        mRegisBn.setOnClickListener(this);
    }

    @Override
    public void init() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_login_bn:
                if (!chekcInput())
                    return;
                break;
            case R.id.login_regis_bn:
                if (!chekcInput())
                    return;
                break;
        }

        signSuccessful();
    }

    private void signSuccessful(){
        SPUtil.put(this, Constant.SP_F_SETTING_CODE,Constant.SP_K_ACC,mAccEt.getText().toString());
        SPUtil.put(this, Constant.SP_F_SETTING_CODE,Constant.SP_K_PWD,mPwdEt.getText().toString());
        finish();
    }

    private void request(){}

    private boolean chekcInput(){
        String acc = mAccEt.getText().toString();
        String pwd = mPwdEt.getText().toString();
        return emptyCheck(acc, mAccEt, mAccNotTv, "请输入账户名称")
                && emptyCheck(pwd, mPwdEt, mPwdNotTv, "请输入密码")
                && matchCheck(acc,mAccEt,mAccNotTv,"账户格式不正确,请重新输入")
                && matchCheck(pwd,mPwdEt,mPwdNotTv,"密码格式不正确,请重新输入");

    }

    private boolean emptyCheck(String input,View view,TextView noticeView,String notice){
        if(TextUtils.isEmpty(input)) {
            shake(view);
            noticeView.setVisibility(View.VISIBLE);
            noticeView.setText(notice);
            return false;
        }
        return true;
    }

    private boolean matchCheck(String input,EditText view,TextView noticeView,String notice){
        if(!match(reg, input)) {
            shake(view);
            mAccEt.setText("");
            mPwdEt.setText("");
            noticeView.setVisibility(View.VISIBLE);
            noticeView.setText(notice);
            return false;
        }
        return true;
    }

    private void shake(View view){
        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .playOn(view);
    }

    private boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
