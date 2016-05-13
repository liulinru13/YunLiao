package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.view.IFragmentListener;

import net.frakbot.jumpingbeans.JumpingBeans;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackUpFragment extends Fragment
    implements IFragment,View.OnClickListener{

    private final String tag = "back_up_fragment";
    private final String TAG = "BackUpFragmentLog";

    @Bind(R.id.back_up_constacts_tv)
    TextView mBackUpContactsTv;
    @Bind(R.id.back_up_sms_tv)
    TextView mBackUpSmsTv;
    @Bind(R.id.recover_constacts_tv)
    TextView mRecoverContactsTv;
    @Bind(R.id.recover_sms_tv)
    TextView mRecoverSmsTv;

    private MaterialDialog mDialog;

    private IFragmentListener mListener;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public BackUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_back_up, container, false);
        ButterKnife.bind(this, view);
        mBackUpContactsTv.setOnClickListener(this);
        mBackUpSmsTv.setOnClickListener(this);
        mRecoverContactsTv.setOnClickListener(this);
        mRecoverSmsTv.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_up_constacts_tv:
                createDialog("正在向服务器备份通讯录,请稍等");
                break;
            case R.id.back_up_sms_tv:
                createDialog("正在向服务器备份短信数据,请稍等");
                break;
            case R.id.recover_constacts_tv:
                createDialog("正在从服务器恢复通讯录,请稍等");
                break;
            case R.id.recover_sms_tv:
                createDialog("正在从服务器恢复短信数据,请稍等");
                break;
        }
    }

    private void createDialog(String str){

        View view = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.layout_custom_dialog_waiting_view,null);
        TextView tv = (TextView)view.findViewById(R.id.custom_dialog_waiting_tv);
        ImageView imageView = (ImageView)view.findViewById(R.id.dialog_icon);
        imageView.setVisibility(View.VISIBLE);
        Glide.with(getActivity())
                .load(R.drawable.waiting_icon)
                .into(imageView);
        tv.setText(str);
        JumpingBeans.with(tv).appendJumpingDots().build();
//                            mDialog = (Dialog)MiddlewareProxy.getInstance()
//                                    .getDialogFactory().newInstance(null, null,view,null,false,false);
//                            mDialog.show(SettingFragment.this.getFragmentManager(),tag);
        mDialog = new MaterialDialog.Builder(this.getActivity())
                .title(null)
                .content(null)
                .autoDismiss(false)
                .customView(view, false)
                .build();
        mDialog.show();
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
