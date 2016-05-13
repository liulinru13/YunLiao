package com.mmrx.yunliao.presenter;/**
 * Created by mmrx on 16/5/3.
 */

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.mmrx.yunliao.presenter.util.MyToast;

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
    private EditText mAddrText;
    private Button mSendBn;
    private List<SmsBean> mList;

    private SmsThread thread;
    private WorkingMessage wm;

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
        mAddrText = (EditText)mView.findViewById(R.id.sms_edit_addr_view);
        wm = new WorkingMessage(mSmsEditFragment.getActivity());
        mSendBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = mAddrText.getText().toString();
                String msg = mEditText.getText().toString();
                if(mAddrText.getVisibility() != View.VISIBLE){
                    if(thread != null)
                        addr = thread.getmThreasInfo().getAddresses();
                    else
                        addr = "";
                }
                if(check(addr,msg)) {
                    wm.sendSmsWorker(msg, new String[]{addr}, MessageUtils.getOrCreateThreadId(mSmsEditFragment.getActivity(), addr));
                }

            }
        });

        mList = new ArrayList<SmsBean>();

        mAdapter = new SmsEditAdapter(mSmsEditFragment.getActivity(),mList);

        mListView.setAdapter(mAdapter);
    }

    private boolean check(String addr,String msg){
        if(TextUtils.isEmpty(addr)){
            MyToast.showLong(mSmsEditFragment.getActivity(),"请填写正确接收人地址");
            return false;
        }
        if(TextUtils.isEmpty(msg)){
            MyToast.showLong(mSmsEditFragment.getActivity(),"发送信息内容不能为空");
            return false;
        }
        return true;
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
                mAddrText.setVisibility(View.GONE);
                thread = (SmsThread)obj;
                mEditText.setText("");
                getData(thread);
            }
        }else{
            thread = null;
            mAddrText.setVisibility(View.VISIBLE);
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
