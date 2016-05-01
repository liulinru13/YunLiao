package com.mmrx.yunliao.view.fragment;

import com.mmrx.yunliao.view.IFragmentListener;

/**
 * Fragment需要实现的接口,保证实现类能够和IFragmentListener的实现类进行通讯
 * Created by mmrx on 16/4/1.
 */
public interface IFragment {
    void setFragmentListener(IFragmentListener listener);
    String getFragmentTag();
    String getFragmentTitle();
    void onForeground();
    void onBackground();
}
