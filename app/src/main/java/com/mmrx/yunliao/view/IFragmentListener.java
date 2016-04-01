package com.mmrx.yunliao.view;

/**
 * 用于监听fragment,Activity和Fragment通讯接口
 * Created by mmrx on 16/4/1.
 */
public interface IFragmentListener {
    /**
     * Fragment的回调方法,用于通知Activity切换页面
     * @param fragment fragment类型
     * @param fragmentType 具体页面,可以为null
     */
    void onFragmentChanged(String fragment,String fragmentType);
}
