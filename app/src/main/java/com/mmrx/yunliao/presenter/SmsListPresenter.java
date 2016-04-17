package com.mmrx.yunliao.presenter;/**
 * Created by mmrx on 16/4/7.
 */

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.SmsReceiver.ISmsObserver;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.bean.EmptySmsListBean;
import com.mmrx.yunliao.model.bean.ISmsListBean;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.mmrx.yunliao.presenter.adapter.SmsListAdapter;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 创建人: mmrx
 * 时间: 16/4/7下午12:31
 * 描述: 负责短信列表页面展示管理的Presenter
 */
public class SmsListPresenter implements IContentPresenter,
        AdapterView.OnItemClickListener,
        SmsListAdapter.ISwipeButtonClickListener,
        ISmsObserver{

    private final int UPDATE_LIST = 0;//更新listView

    private Fragment mSmsListFragment;
    private View mView;

    SwipeListView mListView;
    private SmsListAdapter mAdapter;
    private List<ISmsListBean> mList;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_LIST:
                    mList.clear();
                    mList.addAll((List<ISmsListBean>) msg.obj);
                    mAdapter.notifyDataSetChanged();
            }
        }
    };

    public SmsListPresenter(Fragment fragment,View view){
        ButterKnife.bind(fragment,view);
        this.mView = view;
        this.mSmsListFragment = fragment;
    }

    /**
     * 初始化组件
     */
    @Override
    public void initComponent(){
        //先使用空内容填充页面
        makeEmptyList();
        mAdapter = new SmsListAdapter(this.mSmsListFragment.getActivity(),mList,this);
        mListView = (SwipeListView)mView.findViewById(R.id.sms_list_fragment_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        //异步请求数据
        getData();
    }


    private void makeEmptyList(){
        mList = new ArrayList<ISmsListBean>();
        for(int i=0;i<7;i++){
            mList.add(new EmptySmsListBean());
        }
    }

    /**
     * 线程获取短信数据
     */
    private void getData(){
        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<ISmsListBean> list = MiddlewareProxy.getInstance()
                        .getSmsThreadList(SmsListPresenter.this.mSmsListFragment.getActivity());
                list.addAll(MiddlewareProxy.getInstance()
                        .getSmsGroupThreadList(SmsListPresenter.this.mSmsListFragment.getActivity()));
                Collections.sort(list);
                if(list.size() > 0){
                    Message msg = new Message();
                    msg.what = UPDATE_LIST;
                    msg.obj = list;
                    mHandler.sendMessage(msg);
                }

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDeleteBnClicked(int position) {
        if(MiddlewareProxy.getInstance().deleteSmsThread(
                this.mSmsListFragment.getActivity(), mList.get(position))){
            mList.remove(position);
            mListView.closeAnimate(position);
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onMarkBnClicked(int position) {
        if(MiddlewareProxy.getInstance()
                .setSmsThreadsRead(this.mSmsListFragment.getActivity(),
                        mList.get(position))) {
            ((SmsThreadBean) mList.get(position)).setRead(true);
            mListView.closeAnimate(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSmsNoticed(int event) {
        if(event == Constant.FLAG_SMS_NEW_RECEIVED){
            getData();
        }
    }
}
