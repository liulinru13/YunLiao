package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.IFragmentListener;

/**
 * 显示短信内容的fragment
 */
public class SmsEditFragment extends Fragment
    implements IFragment{

    private final String tag = "sms_edit_fragment";
    private IFragmentListener mListener;

    public SmsEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_edit, container, false);
    }

    @Override
    public void setFragmentListener(IFragmentListener listener) {
        this.mListener = listener;
    }


    @Override
    public String getFragmentTag() {
        return tag;
    }

    @Override
    public String getFragmentTitle() {
        return "姓名";
    }
}
