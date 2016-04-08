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
public class BackUpFragment extends Fragment
    implements IFragment{

    private final String tag = "back_up_fragment";
    private final String TAG = "BackUpFragmentLog";

    private IFragmentListener mListener;

    public BackUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_back_up, container, false);
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
        return "备份";
    }
}
