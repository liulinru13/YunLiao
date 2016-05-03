package com.mmrx.yunliao.presenter;

/**
 * Created by mmrx on 16/4/7.
 * 作为内容提供者的Presenter必须要实现的接口
 * 定义和view进行内容上的交互方法
 */
public interface IContentPresenter {

    /**
     * 初始化view控件
     */
    void initComponent();

    /**
     * 刷新页面
     */
    void refreshView(Object object);
}
