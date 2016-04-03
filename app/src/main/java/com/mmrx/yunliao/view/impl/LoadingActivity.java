package com.mmrx.yunliao.view.impl;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.presenter.util.CustomDialog;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.view.AbsActivity;

public class LoadingActivity extends AbsActivity {
    private final String TAG = "mainActivity";
    private final int SET_DEFAULT_SMS_APP = 1;
    private final int ENTER_APP = 2;
    private boolean enter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == SET_DEFAULT_SMS_APP){
                enter = true;
            }else if(enter && msg.what == ENTER_APP){
                Intent intent = new Intent(LoadingActivity.this,YunLiaoMainActivity.class);
                startActivity(intent);
                LoadingActivity.this.finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }
    @TargetApi(19)
    @Override
    public void init() {
        final MiddlewareProxy middlewareProxy = MiddlewareProxy.getInstance();
        middlewareProxy.getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                middlewareProxy.init(LoadingActivity.this.getApplication());
                if(Build.VERSION.SDK_INT >=19) {
                    final String myPackageName = getPackageName();
                    final String smsPackageName = Telephony.Sms.getDefaultSmsPackage(LoadingActivity.this);
                    if (!smsPackageName.equals(myPackageName)) {
                        middlewareProxy.createDialog(getFragmentManager(), "dialog", "提示"
                                , "当前应用非系统默认短信应用,正常使用需设置为系统默认短信应用,是否设置为默认短信应用?"
                                , new CustomDialog.CustomDialogListener() {

                            @Override
                            public void doNegativeClick() {

                                L.i(TAG, "package name is " + myPackageName);
                                if (!smsPackageName.equals(myPackageName)) {
                                    Intent intent =
                                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                            myPackageName);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void doPositiveClick() {
                                Intent intent =
                                        new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                        myPackageName);
                                startActivity(intent);
                                Message msg = new Message();
                                msg.what = SET_DEFAULT_SMS_APP;
                                mHandler.sendMessage(msg);
                            }
                        });
                    }else{
                        enter = true;
                    }
                }//end if
                else{
                    enter = true;
                }
                //完成初始化,延时两秒进入主界面
                Message msg = new Message();
                msg.what = ENTER_APP;
                mHandler.sendMessageDelayed(msg, 2000);
            }//end run
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
