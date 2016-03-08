package com.mmrx.yunliao;/**
 * Created by mmrx on 16/3/8.
 */

import android.app.Application;
import android.content.res.Configuration;

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午2:37
 * 描述:
 */
public class YunLiaoAppliaction extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
