package com.mmrx.yunliao.view;

import android.content.res.Resources;
import android.view.View;

/**
 * Created by mmrx on 16/3/18.
 * 改变主题的接口,实现该接口表明可以动态改变当前的主题
 * 而不需要重新启动activity
 */
public interface IThemeChange {

    /**
     * 获取当前的view对象
     * @return
     */
    public View getView();

    /**
     * 设置主题
     * @param themeId
     */
    public void setTheme(Resources.Theme themeId);
}
