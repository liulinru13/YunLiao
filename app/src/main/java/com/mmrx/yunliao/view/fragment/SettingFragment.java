package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.prefs.MaterialDialogPreference;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.view.IFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener,
        IFragment{

    private String tag;
    private static final String key = "page";
    private final String TAG = "SettingFragmentLog";
    private IFragmentListener mListener;
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

    //远程控制界面
    private MaterialEditTextPreference mControl_clear_data;
    private MaterialEditTextPreference mControl_reset_loca;
    private MaterialEditTextPreference mControl_reset_enc;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(String page){
        Bundle bundle = new Bundle();
        if(page != null) {
            bundle.putString(key, page);
        }
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

//    public static SettingFragment newInstance(String page,IFragmentListener listener){
//        SettingFragment temp = newInstance(page);
//        temp.setFragmentListener(listener);
//        return temp;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preference);
//        MaterialDialog dialog;
        tag = getArguments().getString(key);
        addPreferencesFromResource(getPreferenceId(tag));
        init();
    }

    private void init() {
        //主设置界面
        mMain_notice = (Preference) findPreference(Constant.SP_SETTING_MAIN_NOTICE);
        mMain_theme = (SwitchPreference) findPreference(Constant.SP_SETTING_MAIN_THEME);
        mMain_private = (Preference) findPreference(Constant.SP_SETTING_MAIN_PRIVATE);
        mMain_control = (Preference) findPreference(Constant.SP_SETTING_MAIN_CONTROL);
        //隐私设置
        mPrivate_code = (Preference) findPreference(Constant.SP_SETTING_PRIV_CODE);
        mPrivvate_clear = (MaterialDialogPreference) findPreference(Constant.SP_SETTING_PRIV_CLEAR);
        //远程控制
        mControl_clear_data = (MaterialEditTextPreference) findPreference(Constant.SP_SETTING_CONTROL_QK);
        mControl_reset_loca = (MaterialEditTextPreference) findPreference(Constant.SP_SETTING_CONTROL_LOCA);
        mControl_reset_enc = (MaterialEditTextPreference) findPreference(Constant.SP_SETTING_CONTROL_ENC);
        //消息提醒
        //添加监听器
        if(mMain_notice != null)
            mMain_notice.setOnPreferenceClickListener(this);
        if(mMain_theme != null)
            mMain_theme.setOnPreferenceClickListener(this);
        if(mMain_private != null)
            mMain_private.setOnPreferenceClickListener(this);
        if(mMain_control != null)
            mMain_control.setOnPreferenceClickListener(this);
        if(mPrivate_code != null)
            mPrivate_code.setOnPreferenceClickListener(this);
        if(mPrivvate_clear != null)
            mPrivvate_clear.setOnPreferenceClickListener(this);
        if(mControl_clear_data != null)
            mControl_clear_data.setOnPreferenceClickListener(this);
        if(mControl_reset_loca != null)
            mControl_reset_loca.setOnPreferenceClickListener(this);
        if(mControl_reset_enc != null)
            mControl_reset_enc.setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        final String key = preference.getKey();
        L.i(TAG,key);
        switch (key){
            case Constant.SP_SETTING_MAIN_NOTICE:
                mListener.onFragmentChanged(Constant.SETTING,key);
                break;
            case Constant.SP_SETTING_MAIN_PRIVATE:
                mListener.onFragmentChanged(Constant.SETTING,key);
                break;
            case Constant.SP_SETTING_MAIN_CONTROL:
                mListener.onFragmentChanged(Constant.SETTING,key);
                break;
        }
        return false;
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

    @Override
    public void setFragmentListener(IFragmentListener listener) {
        this.mListener = listener;
    }

    @Override
    public String getFragmentTag() {
        return null;
    }
}
