package com.mmrx.yunliao.SmsReceiver;

/**
 * Created by mmrx on 16/4/7.
 * 实现该接口表示需要及时收到新短信到达的事件通知
 * 该接口的实现类需要在Service里注册
 */
public interface ISmsObserver {

    /**
     * 用于接收短信事件
     * @param event 在Constant类中定义
     */
    void onSmsNoticed(int event);
}
