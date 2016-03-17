package com.mmrx.yunliao.view.impl;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.mmrx.yunliao.R;
import com.mmrx.yunliao.model.SmsDBhelper;
import com.mmrx.yunliao.model.bean.group.SmsCroupUserBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupBean;
import com.mmrx.yunliao.model.bean.group.SmsGroupThreadSend;
import com.mmrx.yunliao.presenter.util.CustomDialog;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.view.AbsActivity;

import java.util.ArrayList;

public class MainActivity extends AbsActivity {
    private final String TAG = "mainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @TargetApi(19)
    @Override
    public void init() {
        MiddlewareProxy middlewareProxy = MiddlewareProxy.getInstance();
        middlewareProxy.init(getApplication());
        middlewareProxy.createDialog(getFragmentManager(), "dialog", "", "123", new CustomDialog.CustomDialogListener() {
            @Override
            public void doNegativeClick() {
//

                final String myPackageName = getPackageName();
                final String smsPackageName = Telephony.Sms.getDefaultSmsPackage(MainActivity.this);
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
//                SmsGroupThreadSend send = new SmsGroupThreadSend();
//                send.setmGroupBean(new SmsGroupBean(1l, "insertt"));
//                ArrayList<SmsCroupUserBean> list = new ArrayList<SmsCroupUserBean>();
//                for (int i = 0; i < 3; i++) {
//                    list.add(new SmsCroupUserBean("123 " + i, 666));
//                }
//                send.setmGroupUsersList(list);
//                try {
//                    SmsDBhelper.getInstance().insertNewGroupSms(send);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
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
