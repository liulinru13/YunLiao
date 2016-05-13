package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.IFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAuthorFragment extends Fragment
        implements IFragment{

    private final String tag = "sms_about_auth_fragment";
    private IFragmentListener mListener;

    public AboutAuthorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_about_author, container, false);
        return view;
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
        return "关于作者";
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
