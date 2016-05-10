package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.IFragmentListener;

import net.frakbot.jumpingbeans.JumpingBeans;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment
        implements IFragment{
    private final String tag = "sms_about_app_fragment";
    private IFragmentListener mListener;
    private ImageView mIcon;
    private TextView mThanks;
    private JumpingBeans jumpingBeans;
    public AboutAppFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        mIcon = (ImageView)view.findViewById(R.id.image);
        mThanks = (TextView)view.findViewById(R.id.text1);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
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
        return "关于应用";
    }

    @Override
    public void onForeground(Object obj) {
        init();
    }

    private void init(){
        if(mThanks != null && mIcon != null) {
            YoYo.with(Techniques.FadeIn)
                    .duration(2000)
                    .duration(1000)
                    .playOn(mIcon);

            jumpingBeans = JumpingBeans.with(mThanks)
                    .appendJumpingDots()
                    .build();
        }
    }

    @Override
    public void onBackground(Object obj) {
        if(jumpingBeans != null)
            jumpingBeans.stopJumping();
    }

    @Override
    public void doSomething(Object... objs) {

    }
}
