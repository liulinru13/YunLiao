package com.mmrx.yunliao.presenter;/**
 * Created by mmrx on 16/5/3.
 */

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.SmsReceiver.ISmsObserver;
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.bean.sms.SmsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.presenter.adapter.SmsEditAdapter;
import com.mmrx.yunliao.presenter.smsSend.MessageUtils;
import com.mmrx.yunliao.presenter.smsSend.WorkingMessage;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建人: mmrx
 * 时间: 16/5/3下午12:36
 * 描述: 管理短信编辑页面的
 */
public class SmsEditPresenter implements IContentPresenter,
        ISmsObserver {

    private final int REFRESH_PAGE = 1;

    private Fragment mSmsEditFragment;
    private View mView;
    private SmsEditAdapter mAdapter;

    private ListView mListView;
    private EditText mEditText;
    private Button mSendBn;
    private List<SmsBean> mList;

    private SmsThread thread;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_PAGE:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public SmsEditPresenter(Fragment fragment,View view) {
        this.mSmsEditFragment = fragment;
        this.mView = view;
    }

    @Override
    public void initComponent() {
        mListView = (ListView)mView.findViewById(R.id.sms_edit_list_view);
        mEditText = (EditText)mView.findViewById(R.id.sms_edit_edit_view);
        mSendBn = (Button)mView.findViewById(R.id.sms_edit_edit_send);

        mSendBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkingMessage wm = new WorkingMessage(mSmsEditFragment.getActivity());
                wm.sendSmsWorker("hello",new String[]{"5554"}, MessageUtils.getOrCreateThreadId(mSmsEditFragment.getActivity(),"5554"));
            }
        });

        mList = new ArrayList<SmsBean>();

        mAdapter = new SmsEditAdapter(mSmsEditFragment.getActivity(),mList);

        mListView.setAdapter(mAdapter);
    }

    private void getData(SmsThread bean){
        MiddlewareProxy.getInstance().getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
//                thread.getmThreasInfo()
                thread.setmSmsList(MiddlewareProxy.getInstance().queryAllSmsByThreadId(mSmsEditFragment.getActivity(), thread.getmThreasInfo()));

                mList.addAll(thread.getmSmsList());
                Message msg = new Message();
                msg.what = REFRESH_PAGE;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void refreshView(Object obj) {
        mList.clear();
        mAdapter.notifyDataSetChanged();
        if(obj != null){
            if(obj instanceof SmsThread){
                thread = (SmsThread)obj;
                mEditText.setText("");
                getData(thread);
            }
        }
    }

    @Override
    public void onSmsNoticed(int event) {
        if(event == Constant.FLAG_SMS_NEW_RECEIVED || event == Constant.FLAT_SMS_REFRESH){
            if(thread != null)
                getData(thread);
        }
    }
}
