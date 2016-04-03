package com.mmrx.yunliao.view.fragment;

import com.mmrx.yunliao.view.IFragmentListener;

/**
 * Created by mmrx on 16/4/3.
 * 继承IFragmentListener的方法
 * 实现该接口表示当前fragment显示在屏幕上时会告知宿主类,自己是当前屏幕上显示的fragment
 * 仅适合在生命周期方法调用情况下判断,show/hide方式不适用
 */
public interface IFragmentOnScreen extends IFragmentListener{

    void onTheScreen(IFragment fragment);
}
