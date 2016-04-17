package com.mmrx.yunliao.view.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.SmsReceiver.ISmsObserver;
import com.mmrx.yunliao.model.bean.EmptySmsListBean;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.presenter.IContentPresenter;
import com.mmrx.yunliao.presenter.SmsListPresenter;
import com.mmrx.yunliao.presenter.adapter.SmsListAdapter;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.view.IFragmentListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 显示短信列表的fragment
 */
public class SmsListFragment extends Fragment
    implements IFragment,AdapterView.OnItemClickListener {

    private final String tag = "sms_list_fragment";
    private IContentPresenter mPresenter;
    private IFragmentListener mListener;
    public SmsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms_list, container, false);
        mPresenter = new SmsListPresenter(this,view);
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
            MiddlewareProxy.getInstance().setOnSmsChangedListener(tag,(ISmsObserver)mPresenter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter instanceof ISmsObserver){
            MiddlewareProxy.getInstance().removeSmsChangedListener(tag);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        return "信息";
    }
}
