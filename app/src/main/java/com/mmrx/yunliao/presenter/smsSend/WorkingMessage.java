package com.mmrx.yunliao.presenter.smsSend;

import android.content.Context;
import android.text.TextUtils;

import com.mmrx.yunliao.presenter.util.L;

/**
 * Created by mmrx on 16/5/6.
 */
public class WorkingMessage {
    private static final String TAG = "WorkingMessageLog";

    private Context mContext;

    public WorkingMessage(Context mContext) {
        this.mContext = mContext;
    }

    public void sendSmsWorker(String msgText, String[] semiSepRecipients, long threadId) {

        MessageSender sender = new SmsMessageSender(mContext, semiSepRecipients, msgText, threadId);
        try {
            sender.sendMessage(threadId);

            // Make sure this thread isn't over the limits in message count
        } catch (Exception e) {
            L.e(TAG, "Failed to send SMS message, threadId=" + threadId);
        }
//        mStatusListener.onMessageSent();
//        MmsWidgetProvider.notifyDatasetChanged(mActivity);
    }
}
