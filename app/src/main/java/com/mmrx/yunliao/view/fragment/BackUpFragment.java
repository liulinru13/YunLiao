package com.mmrx.yunliao.view.fragment;


import android.content.Intent;
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
import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.BackupFileMaker;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.presenter.util.MyToast;
import com.mmrx.yunliao.presenter.util.SPUtil;
import com.mmrx.yunliao.view.IFragmentListener;
import com.mmrx.yunliao.view.impl.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    @BindString(R.string.url_upload)
    String urlUpload;

    private MaterialDialog mDialog;

    private IFragmentListener mListener;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mDialog != null &&mDialog.isShowing()){
                mDialog.dismiss();
            }
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

    private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("text/plain");

    @Override
    public void onClick(View v) {
        String accStr = (String)SPUtil.get(this.getActivity(), Constant.SP_F_SETTING_CODE, Constant.SP_K_ACC, "");
//        String pwdStr = (String)SPUtil.get(this.getActivity(), Constant.SP_F_SETTING_CODE,Constant.SP_K_PWD,"");
        if(accStr.equals("")){
            MyToast.showShort(this.getActivity(),"数据备份与恢复前请登录");
            Intent intent = new Intent();
            intent.setClass(this.getActivity(),LoginActivity.class);
            startActivity(intent);
            return;
        }
        switch (v.getId()){
            case R.id.back_up_constacts_tv:
                createDialog("正在向服务器备份通讯录,请稍等");
                MiddlewareProxy.getInstance().backupContacts(this.getActivity());
                request(1,accStr);
                break;
            case R.id.back_up_sms_tv:
                createDialog("正在向服务器备份短信数据,请稍等");
                MiddlewareProxy.getInstance().backupSms(this.getActivity());
                try {
                    request(2,accStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.recover_constacts_tv:
                createDialog("正在从服务器恢复通讯录,请稍等");
                mHandler.sendMessageDelayed(new Message(), 2000);
                break;
            case R.id.recover_sms_tv:
                createDialog("正在从服务器恢复短信数据,请稍等");
                mHandler.sendMessageDelayed(new Message(), 2000);
                break;
        }
    }

    private void request(int type,String user){
        MThread thread = new MThread(type,user);
        thread.start();
    }

    class MThread extends Thread{
        int type;
        String user;
        public MThread(int type,String user){
            this.type = type;
            this.user = user;
        }
        private final OkHttpClient client = new OkHttpClient();
        public void run() {
            // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
            try{
                MultipartBody requestBody = new MultipartBody.Builder("AaB03x")
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("files", null, new MultipartBody.Builder("BbC04y")
                                .addPart(null,
                                        RequestBody.create(null, new File(BackupFileMaker.getPath(BackUpFragment.this.getActivity())+"/"+ (type==2?BackupFileMaker.smsFileName:BackupFileMaker.conFileName))))
                                .build())
                        .addFormDataPart("file_type", "" + type)
                        .addFormDataPart("user",user)
                        .build();
                Request request = new Request.Builder()
                        .url(urlUpload)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful())
                    mHandler.sendMessage(new Message());
                else{
                    MyToast.showShort(BackUpFragment.this.getContext(),"服务器错误,请稍后再试");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
