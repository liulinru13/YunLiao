package com.mmrx.yunliao.SmsReceiver;

/**
 * Created by mmrx on 16/4/7.
 * 实现该接口表示需要及时收到新短信到达的事件通知
 * 该接口的实现类需要在Service里注册
 */
public interface ISmsObserver {
    /**
     * 接口未完成
     */
    void onSmsComing();
}
