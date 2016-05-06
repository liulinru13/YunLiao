package com.mmrx.yunliao.presenter.smsSend;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import com.mmrx.yunliao.SmsReceiver.MessageStatusReceiver;
import com.mmrx.yunliao.SmsReceiver.SmsReceiver;
import com.mmrx.yunliao.SmsReceiver.SmsService;
import com.mmrx.yunliao.model.exception.MmsException;
import com.mmrx.yunliao.presenter.util.L;

import java.util.ArrayList;

public class SmsSingleRecipientSender extends SmsMessageSender {

    private final boolean mRequestDeliveryReport;
    private String mDest;
    private Uri mUri;
    private static final String TAG = "SmsSingleRecipientSender";

    public SmsSingleRecipientSender(Context context, String dest, String msgText, long threadId,
            boolean requestDeliveryReport, Uri uri) {
        super(context, null, msgText, threadId);
        mRequestDeliveryReport = requestDeliveryReport;
        mDest = dest;
        mUri = uri;
    }

    public boolean sendMessage(long token) throws MmsException {
        L.v(TAG, "sendMessage token: " + token);
        if (mMessageText == null) {
            // Don't try to send an empty message, and destination should be just
            // one.
            throw new MmsException("Null message body or have multiple destinations.");
        }
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> messages = null;
        messages = smsManager.divideMessage(mMessageText);
            // remove spaces and dashes from destination number
            // (e.g. "801 555 1212" -> "8015551212")
            // (e.g. "+8211-123-4567" -> "+82111234567")
        mDest = PhoneNumberUtils.stripSeparators(mDest);
//        mDest = Conversation.verifySingleRecipient(mContext, mThreadId, mDest);

        int messageCount = messages.size();

        if (messageCount == 0) {
            // Don't try to send an empty message.
            throw new MmsException("SmsMessageSender.sendMessage: divideMessage returned " +
                    "empty messages. Original message is \"" + mMessageText + "\"");
        }
        // out_box
        boolean moved = MessageUtils.moveMessageToFolder(mContext, mUri, 4, 0);
        if (!moved) {
            throw new MmsException("SmsMessageSender.sendMessage: couldn't move message " +
                    "to outbox: " + mUri);
        }
        L.v(TAG, "sendMessage mDest: " + mDest + " mRequestDeliveryReport: " +
                    mRequestDeliveryReport);


        ArrayList<PendingIntent> deliveryIntents =  new ArrayList<PendingIntent>(messageCount);
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(messageCount);
        for (int i = 0; i < messageCount; i++) {
            if (mRequestDeliveryReport && (i == (messageCount - 1))) {
                // TODO: Fix: It should not be necessary to
                // specify the class in this intent.  Doing that
                // unnecessarily limits customizability.
                deliveryIntents.add(PendingIntent.getBroadcast(
                        mContext, 0,
                        new Intent(
                                MessageStatusReceiver.MESSAGE_STATUS_RECEIVED_ACTION,
                                mUri,
                                mContext,
                                MessageStatusReceiver.class),
                                0));
            } else {
                deliveryIntents.add(null);
            }
            Intent intent  = new Intent(SmsService.MESSAGE_SENT_ACTION,
                    mUri,
                    mContext,
                    SmsReceiver.class);

            int requestCode = 0;
            if (i == messageCount -1) {
                // Changing the requestCode so that a different pending intent
                // is created for the last fragment with
                // EXTRA_MESSAGE_SENT_SEND_NEXT set to true.
                requestCode = 1;
                intent.putExtra(SmsService.EXTRA_MESSAGE_SENT_SEND_NEXT, true);
            }
            L.v(TAG, "sendMessage sendIntent: " + intent);

            sentIntents.add(PendingIntent.getBroadcast(mContext, requestCode, intent, 0));
        }
        try {
            smsManager.sendMultipartTextMessage(mDest, mServiceCenter, messages, sentIntents, deliveryIntents);
        } catch (Exception ex) {
            L.e(TAG, "SmsMessageSender.sendMessage: caught");
            throw new MmsException("SmsMessageSender.sendMessage: caught " + ex +
                    " from SmsManager.sendTextMessage()");
        }
        return false;
    }

    private void log(String msg) {
        L.d(TAG, "[SmsSingleRecipientSender] " + msg);
    }
}
