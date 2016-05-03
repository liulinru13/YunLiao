package com.mmrx.yunliao.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.SmsReceiver.ISmsObserver;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupThread;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.presenter.IContentPresenter;
import com.mmrx.yunliao.presenter.SmsEditPresenter;
import com.mmrx.yunliao.presenter.SmsListPresenter;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.view.IFragmentListener;

/**
 * 显示短信内容的fragment
 */
public class SmsEditFragment extends Fragment
    implements IFragment{

    private final String tag = "sms_edit_fragment";
    private IContentPresenter mPresenter;
    private IFragmentListener mListener;

    private SmsThread smsThread;
    private SmsGroupThread smsGroupThread;

    public SmsEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms_edit, container, false);
        mPresenter = new SmsEditPresenter(this,view);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mPresenter.initComponent();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(mPresenter instanceof ISmsObserver){
            MiddlewareProxy.getInstance().setOnSmsChangedListener(tag, (ISmsObserver) mPresenter);
        }
        if(smsThread != null)
            mPresenter.refreshView(smsThread);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter instanceof ISmsObserver){
            MiddlewareProxy.getInstance().removeSmsChangedListener(tag);
        }
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
        if(smsThread != null){
            return smsThread.getmThreasInfo().getContacts();
        }
        else if(smsGroupThread != null){
            return smsGroupThread.getmGroupInfo().getContacts();
        }
        return "新短信";
    }

    @Override
    public void onForeground(Object obj) {
        if(obj != null){
            if(obj instanceof SmsThreadBean){
                SmsThread bean = new SmsThread();
                bean.setmThreasInfo((SmsThreadBean)obj);
                smsThread = bean;
                smsGroupThread = null;
                if(mPresenter != null)
                    mPresenter.refreshView(smsThread);
            }else if(obj instanceof SmsGroupThreadsBean){
                SmsGroupThread bean = new SmsGroupThread();
                bean.setmGroupInfo((SmsGroupThreadsBean)obj);
                smsThread = null;
                smsGroupThread = bean;
            }
        }
    }


    @Override
    public void onBackground(Object obj) {

    }

    @Override
    public void doSomething(Object... objs) {

    }
}
