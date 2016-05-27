package com.mmrx.yunliao;/**
 * Created by mmrx on 16/3/8.
 */

import android.app.Application;
import android.content.res.Configuration;

import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 创建人: mmrx
 * 时间: 16/3/8下午2:37
 * 描述:
 */
public class YunLiaoAppliaction extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient;
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                        //其他配置
                .build();

        OkHttpUtils.getInstance(okHttpClient);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        MiddlewareProxy.getInstance()
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
