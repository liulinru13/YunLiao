package com.mmrx.yunliao.view.fragment;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialDialogPreference;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.presenter.util.MyToast;
import com.mmrx.yunliao.presenter.util.SPUtil;
import com.mmrx.yunliao.view.IFragmentListener;

import net.frakbot.jumpingbeans.JumpingBeans;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener,
        IFragment{

    private String tag;
    private static final String key = "page";
    private final int CLEAN_DATA_FINISHED  = 1;
    private final int ENCODE_DB = 2;

    private final String TAG = "SettingFragmentLog";
    private String mTitle;
    private IFragmentOnScreen mListener;
    //主设置界面
    private Preference mMain_notice;
    private SwitchPreference mMain_theme;
    private SwitchPreference mMain_encode;
    private SwitchPreference mMain_encode_show;
    private SwitchPreference mMain_lock;
    private Preference mMain_private;
    private Preference mMain_control;
    //短信通知设置界面

    //隐私设置界面
    private Preference mPrivate_code;
    private MaterialDialogPreference mPrivvate_clear;
    private SwitchPreference mPrivate_show;

    private MaterialDialog mDialog;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            switch (msg.what){
                case CLEAN_DATA_FINISHED:
                    if(mDialog != null){
                        mDialog.dismiss();
                    }
                    break;
                case ENCODE_DB:
                    boolean encode = SPUtil.getPreference(SettingFragment.this.getActivity()).getBoolean(Constant.SP_SETTING_MAIN_ENCODE_SWITCH,false);
                    //加密
                    if(encode) {
                        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //短信数据库
                                MiddlewareProxy.getInstance().encodeSmsAll(SettingFragment.this.getActivity());
                                //云聊数据库
                                MiddlewareProxy.getInstance().encodeGroupSmsAll();
                                SPUtil.put(SettingFragment.this.getActivity(), Constant.SP_F_CONTROL, Constant.SP_K_ENC, true);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MiddlewareProxy.getInstance().notifyAllSmsObserver(Constant.FLAT_SMS_REFRESH);
                                    }
                                });
                            }
                        });
                    }
                    //解密
                    else{
                        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //短信数据库
                                MiddlewareProxy.getInstance().decodeSmsAll(SettingFragment.this.getActivity());
                                //云聊数据库
                                MiddlewareProxy.getInstance().decodeGroupSmsAll();
                                SPUtil.put(SettingFragment.this.getActivity(), Constant.SP_F_CONTROL, Constant.SP_K_ENC, true);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MiddlewareProxy.getInstance().notifyAllSmsObserver(Constant.FLAT_SMS_REFRESH);
                                    }
                                });
                            }
                        });
                    }
                    break;
            }
        }
    };

    //远程控制界面
//    private MaterialEditTextPreference mControl_clear_data;
//    private MaterialEditTextPreference mControl_reset_loca;
//    private MaterialEditTextPreference mControl_reset_enc;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(String page){
        Bundle bundle = new Bundle();
        if(page != null) {
            bundle.putString(key, page);
        }
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.chooseFragmentTitleByTag(page);
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getString(key);
        addPreferencesFromResource(getPreferenceId(tag));
//        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        if(tag.equals(Constant.S_MAIN)) {
            //主设置界面
            mMain_notice = (Preference) findPreference(Constant.SP_SETTING_MAIN_NOTICE);
            mMain_theme = (SwitchPreference) findPreference(Constant.SP_SETTING_MAIN_THEME);
            mMain_private = (Preference) findPreference(Constant.SP_SETTING_MAIN_PRIVATE);
            mMain_control = (Preference) findPreference(Constant.SP_SETTING_MAIN_CONTROL);

            mMain_encode = (SwitchPreference) findPreference(Constant.SP_SETTING_MAIN_ENCODE_SWITCH);
            mMain_encode_show = (SwitchPreference) findPreference(Constant.SP_SETTING_MAIN_ENCODE_SHOW);

            if((boolean)SPUtil.get(getActivity().getApplicationContext(),Constant.SP_F_CONTROL,Constant.SP_K_ENC,false)){
                mMain_encode.setDefaultValue(true);
                mMain_encode_show.setDefaultValue(true);
            }

            mMain_notice.setOnPreferenceClickListener(this);
//            mMain_theme.setOnPreferenceClickListener(this);
            mMain_private.setOnPreferenceClickListener(this);
            mMain_control.setOnPreferenceClickListener(this);
//            mMain_encode.setOnPreferenceClickListener(this);
//            mMain_encode_show.setOnPreferenceClickListener(this);
            mMain_encode.setOnPreferenceChangeListener(this);
            mMain_theme.setOnPreferenceChangeListener(this);

        }else if(tag.equals(Constant.SP_SETTING_MAIN_PRIVATE)) {
            //隐私设置
            mPrivate_code = (Preference) findPreference(Constant.SP_SETTING_PRIV_CODE);
            mPrivvate_clear = (MaterialDialogPreference) findPreference(Constant.SP_SETTING_PRIV_CLEAR);

            mPrivate_code.setOnPreferenceClickListener(this);
            mPrivvate_clear.setOnPreferenceClickListener(this);
        }else if(tag.equals(Constant.SP_SETTING_MAIN_CONTROL)) {
            //远程控制设置
            SharedPreferences sp = SPUtil.getPreference(this.getActivity());
            final String qkStr = sp.getString(Constant.SP_SETTING_CONTROL_QK, "qk");
            final String locaStr = sp.getString(Constant.SP_SETTING_CONTROL_LOCA,"loca");
            final String encStr = sp.getString(Constant.SP_SETTING_CONTROL_ENC,"enc");


            MaterialEditTextPreference preference = (MaterialEditTextPreference)findPreference(Constant.SP_SETTING_CONTROL_QK);
            preference.setSummary("默认命令为"
                    + " " + qkStr + "_隐私密码\\n不填写内容为默认命令");
            preference.setDialogMessage("请输入新的命令格式来替换"+qkStr);

            MaterialEditTextPreference preference1 = (MaterialEditTextPreference)findPreference(Constant.SP_SETTING_CONTROL_LOCA);
            preference1.setSummary("默认命令为"
                    + " " + locaStr + "_隐私密码_接受手机号\\n不填写内容为默认命令");
            preference1.setDialogMessage("请输入新的命令格式来替换"+locaStr);

            MaterialEditTextPreference preference2 = (MaterialEditTextPreference)findPreference(Constant.SP_SETTING_CONTROL_ENC);
            preference2.setSummary("默认命令为"
                    + " " + encStr + "_隐私密码\\n不填写内容为默认命令");
            preference2.setDialogMessage("请输入新的命令格式来替换"+encStr);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉宿主,自己现在是在屏幕上显示
        this.mListener.onTheScreen(this);
    }

    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        final String key = preference.getKey();
        L.i(TAG, key);
        switch (key){
//            case Constant.SP_SETTING_MAIN_THEME:
//
//                break;
            case Constant.SP_SETTING_MAIN_NOTICE:
                mListener.onFragmentChanged(Constant.SETTING,key);
                break;
            case Constant.SP_SETTING_MAIN_PRIVATE:
                mListener.onFragmentChanged(Constant.SETTING,key);
                break;
            case Constant.SP_SETTING_MAIN_CONTROL:
                mListener.onFragmentChanged(Constant.SETTING, key);
                break;
            //隐私设置中清空数据选项
            case Constant.SP_SETTING_PRIV_CLEAR:
                popSettingPrivDataCleanDialog();
                break;
            //修改隐私密码
            case Constant.SP_SETTING_PRIV_CODE:
                popSettingPrivResetPw();
                break;

        }
        return false;
    }


    @Override
    public boolean onPreferenceChange(android.preference.Preference preference, Object newValue) {
        final String key = preference.getKey();
        L.i(TAG, key);
        switch (key){
            case Constant.SP_SETTING_MAIN_THEME:
                //1 true 白天 2 false夜晚
                break;
            //加密存储
            case Constant.SP_SETTING_MAIN_ENCODE_SWITCH:

//                break;
//            //加密显示
//            case Constant.SP_SETTING_MAIN_ENCODE_SHOW:
                mHandler.removeMessages(ENCODE_DB);
                Message msg = new Message();
                msg.what = ENCODE_DB;
                msg.obj = (boolean)newValue;
                mHandler.sendMessageDelayed(msg, 1500);
                break;
        }
        return true;
    }

    private void popSettingPrivResetPw(){
        final View view = LayoutInflater.from(SettingFragment
                .this.getActivity())
                .inflate(R.layout.layout_custom_dialog_reset_priv_code_view, null);
        new MaterialDialog.Builder(SettingFragment.this.getActivity())
                .title(null)
                .content(null)
                .customView(view, false)
                .autoDismiss(false)
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        EditText pwETOld = (EditText) view.findViewById(R.id.custom_dialog_pw_old);
                        EditText pwETNew = (EditText) view.findViewById(R.id.custom_dialog_pw_new);
                        String pwO = pwETOld.getText().toString(),
                                pwN = pwETNew.getText().toString();
                        //输入框内无内容
                        if (pwN.equals("") || pwO.equals("")) {
                            showTost("请输入新旧密码");
                            return;
                        }
                        //获取保存的隐私密码
                        String oldPw = (String) SPUtil.get(getActivity().getApplication(),
                                Constant.SP_F_SETTING_CODE,
                                Constant.SP_K_SETTING_CODE, "");
                        //没有设置过密码,两次输入一致则设为密码
                        if (oldPw.equals("")) {
                            if (pwO.equals(pwN)) {
                                SPUtil.put(getActivity().getApplication(),
                                        Constant.SP_F_SETTING_CODE,
                                        Constant.SP_K_SETTING_CODE, pwN);
                                showTost("密码初始化成功");
                                dialog.dismiss();
                            } else {
                                showTost("密码初始化失败,请检查两次输入是否一致");
                                return;
                            }
                        }
                        //之前设置过密码,密码一致
                        else if (oldPw.equals(pwO)) {
                            SPUtil.put(getActivity().getApplication(),
                                    Constant.SP_F_SETTING_CODE,
                                    Constant.SP_K_SETTING_CODE, pwN);
                            showTost("密码重置成功");
                            dialog.dismiss();
                        }
                        //之前设置过密码,密码不一致
                        else {
                            showTost("密码不匹配,请重新输入或者取消修改");
                            return;
                        }
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).build().show();
    }

    private void showTost(String str){
        MyToast.showShort(SettingFragment.this.getActivity(), str);
    }

    /**
     * 弹出隐私设置中清空数据选项的对话框
     */
    private void popSettingPrivDataCleanDialog(){
        final MaterialDialog dialog = ((MaterialDialog)mPrivvate_clear.getDialog());
        if(dialog != null) {
            //确定按钮的监听
            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            View view = LayoutInflater.from(SettingFragment
                                    .this.getActivity())
                                    .inflate(R.layout.layout_custom_dialog_waiting_view,null);
                            TextView tv = (TextView)view.findViewById(R.id.custom_dialog_waiting_tv);
                            tv.setText("正在清理数据,请稍等");
                            JumpingBeans.with(tv).appendJumpingDots().build();
//                            mDialog = (Dialog)MiddlewareProxy.getInstance()
//                                    .getDialogFactory().newInstance(null, null,view,null,false,false);
//                            mDialog.show(SettingFragment.this.getFragmentManager(),tag);
                            mDialog = new MaterialDialog.Builder(SettingFragment.this.getActivity())
                                    .title(null)
                                    .content(null)
                                    .customView(view,false)
                                    .build();
                            mDialog.show();
//                            mDialog.getActionButton(DialogAction.POSITIVE).setVisibility(View.GONE);
//                            mDialog.getActionButton(DialogAction.NEGATIVE).setVisibility(View.GONE);
                            //调用数据清理的方法
                            MiddlewareProxy.getInstance().decodeSmsAll(SettingFragment.this.getActivity());
                            MiddlewareProxy.getInstance().decodeGroupSmsAll();
                            dialog.dismiss();
                            Message msg = new Message();
                            msg.what = CLEAN_DATA_FINISHED;
                            mHandler.sendMessageDelayed(msg,3000);
                        }
                    });

                }
            });
        }
    }

    /**
     * 根据tag来选择显示的设置页面是什么
     * @param tag
     * @return
     */
    private int getPreferenceId(final String tag){
        switch (tag){
            case Constant.SP_SETTING_MAIN_CONTROL:
                return R.xml.control_pref;
            case Constant.SP_SETTING_MAIN_NOTICE:
                return R.xml.notice_pref;
            case Constant.SP_SETTING_MAIN_PRIVATE:
                return R.xml.private_pref;
            default:
                return R.xml.preference;
        }
    }

    private void chooseFragmentTitleByTag(final String tag){
        switch (tag){
            case Constant.SP_SETTING_MAIN_CONTROL:
                mTitle = "远程控制设置";
                break;
            case Constant.SP_SETTING_MAIN_NOTICE:
                mTitle = "消息通知设置";
                break;
            case Constant.SP_SETTING_MAIN_PRIVATE:
                mTitle = "隐私设置";
                break;
            default:
                mTitle = "设置";
                break;
        }
    }

    @Override
    public void setFragmentListener(IFragmentListener listener) {
        if(listener instanceof IFragmentOnScreen)
            this.mListener = (IFragmentOnScreen)listener;
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public String getFragmentTitle() {
        return mTitle;
    }

    @Override
    public void onForeground(Object obj) {

    }

    @Override
    public void onBackground(Object obj) {

    }

    @Override
    public void doSomething(Object... objs) {

    }
}
