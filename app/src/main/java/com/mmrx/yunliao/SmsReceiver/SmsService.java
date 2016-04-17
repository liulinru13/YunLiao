package com.mmrx.yunliao.SmsReceiver;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.provider.SyncStateContract;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;

import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmsService extends Service {

    private static final String TAG = "SmsServiceLog";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELEVERED = "android.provider.Telephony.SMS_DELIVER";
    public static final String ACTION_SEND_MESSAGE = "com.android.mms.transaction.SEND_MESSAGE";
    public static final String ACTION_SERVICE_STATE_CHANGED = "android.intent.action.SERVICE_STATE";
    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;

    private boolean mSending;
    private int mResultCode;

    private static final String[] SEND_PROJECTION = new String[] {
            "_id",     //0
            "thread_id",  //1
            "address",    //2
            "body",       //3
            "status",     //4

    };

    private final static String[] REPLACE_PROJECTION = new String[] {
            "_id",     //0
            "address",    //1
            "protocol" //2
    };

    public SmsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Temporarily removed for this duplicate message track down.

        mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;

        if (mResultCode != 0) {
//            L.v(TAG, "onStart: #" + startId + " mResultCode: " + mResultCode +
//                    " = " + translateResultCode(mResultCode));
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int serviceId = msg.arg1;
            Intent intent = (Intent)msg.obj;
            int event = Constant.FLAG;
            if (intent != null) {
                String action = intent.getAction();

                int error = intent.getIntExtra("errorCode", 0);

                /*
                * 处理Sms接收handleSmsReceived，从Intent中取得消息对象，直接显示给用户并保持到数据库中。
                * */
                if(SMS_RECEIVED.equals(intent.getAction())){
                    handleSmsReceived(intent,error);
                    event = Constant.FLAG_SMS_NEW_RECEIVED;
                }
                /*
                * 处理Sms发送handleSmsSent，用于发送多条信息的时候。
                * 第一条由ACTION_SEND_MESSAGE处理，剩下是该action处理。从待发送消息队列中取得消息，并按次序逐个发送；
                * */
                if (ACTION_SEND_MESSAGE.equals(intent.getAction())) {
//                    handleSmsSent(intent, error);
                }
// else if (SMS_DELIVER_ACTION.equals(action)) {
//                    handleSmsReceived(intent, error);
//                }
                /*
                * 系统启动完成后BOOT_COMPLETED，把发件箱(outbox)中的消息移动到发送队列(QueuedBox)，
                * 然后开始发送队列中的消息，最后调用blockingUpdateNewMessageIndicator()方法更新状态栏消息指示图标；
                * */
                else if (ACTION_BOOT_COMPLETED.equals(action)) {
//                    handleBootCompleted();
                }
                /*
                * 处理通讯网络状态改变handleServiceStateChanged，用户从无信号状态进入有信息号状态后，继续执行发送任务的情况；
                * */
               else if (ACTION_SERVICE_STATE_CHANGED.equals(action)) {
//                    handleServiceStateChanged(intent);
                }
//                else if (ACTION_SEND_MESSAGE.endsWith(action)) {
//                    handleSendMessage();
//                } else if (ACTION_SEND_INACTIVE_MESSAGE.equals(action)) {
//                    handleSendInactiveMessage();
//                }
            }
            // NOTE: We MUST not call stopSelf() directly, since we need to
            // make sure the wake lock acquired by AlertReceiver is released.
            SmsReceiver.finishStartingService(SmsService.this, serviceId);
            //通知所有监听短信变化的监听器
            HashMap<String,ISmsObserver> map = MiddlewareProxy.getInstance().getmSmsObserverMap();
            if(map != null && map.size() > 0){
                for(Map.Entry<String,ISmsObserver> entry : map.entrySet()){
                    if(entry.getValue() != null){
                        entry.getValue().onSmsNoticed(event);
                    }
                }
            }//end if
        }
    }

    private void handleSmsReceived(Intent intent, int error) {
        SmsMessage[] msgs = getMessagesFromIntent(intent);
        String format = intent.getStringExtra("format");
        Uri messageUri = insertMessage(this, msgs, error, format);

//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) || LogTag.DEBUG_SEND) {
            SmsMessage sms = msgs[0];
            L.v(TAG, "handleSmsReceived" + (sms.isReplace() ? "(replace)" : "") +
                    " messageUri: " + messageUri +
                    ", address: " + sms.getOriginatingAddress() +
                    ", body: " + sms.getMessageBody());
//        }

        if (messageUri != null) {
//            long threadId = MessagingNotification.getSmsThreadId(this, messageUri);
//            // Called off of the UI thread so ok to block.
////            Log.d(TAG, "handleSmsReceived messageUri: " + messageUri + " threadId: " + threadId);
//            MessagingNotification.blockingUpdateNewMessageIndicator(this, threadId, false);
        }
    }

    private Uri insertMessage(Context context, SmsMessage[] msgs, int error, String format) {
        // Build the helper classes to parse the messages.
        SmsMessage sms = msgs[0];

        if (sms.getMessageClass() == SmsMessage.MessageClass.CLASS_0) {
//            displayClassZeroMessage(context, sms, format);
            return null;
        } else if (sms.isReplace()) {
            return replaceMessage(context, msgs, error);
        } else {
            return storeMessage(context, msgs, error);
        }
    }

    /**
     * Extract all the content values except the body from an SMS
     * message.
     */
    private ContentValues extractContentValues(SmsMessage sms) {
        // Store the message in the content provider.
        ContentValues values = new ContentValues();

        values.put("address", sms.getDisplayOriginatingAddress());

        // Use now for the timestamp to avoid confusion with clock
        // drift between the handset and the SMSC.
        // Check to make sure the system is giving us a non-bogus time.
        Calendar buildDate = new GregorianCalendar(2011, 8, 18);    // 18 Sep 2011
        Calendar nowDate = new GregorianCalendar();
        long now = System.currentTimeMillis();
        nowDate.setTimeInMillis(now);

        if (nowDate.before(buildDate)) {
            // It looks like our system clock isn't set yet because the current time right now
            // is before an arbitrary time we made this build. Instead of inserting a bogus
            // receive time in this case, use the timestamp of when the message was sent.
            now = sms.getTimestampMillis();
        }

        values.put("date", new Long(now));
        values.put("date_sent", Long.valueOf(sms.getTimestampMillis()));
        values.put("protocol", sms.getProtocolIdentifier());
        values.put("read", 0);
        values.put("seen", 0);
        if (sms.getPseudoSubject().length() > 0) {
            values.put("subject", sms.getPseudoSubject());
        }
        values.put("reply_path_present", sms.isReplyPathPresent() ? 1 : 0);
        values.put("service_center", sms.getServiceCenterAddress());
        return values;
    }

    /**
     * This method is used if this is a "replace short message" SMS.
     * We find any existing message that matches the incoming
     * message's originating address and protocol identifier.  If
     * there is one, we replace its fields with those of the new
     * message.  Otherwise, we store the new message as usual.
     *
     * See TS 23.040 9.2.3.9.
     */
    private Uri replaceMessage(Context context, SmsMessage[] msgs, int error) {
        SmsMessage sms = msgs[0];
        ContentValues values = extractContentValues(sms);
        values.put(Sms.ERROR_CODE, error);
        int pduCount = msgs.length;

        if (pduCount == 1) {
            // There is only one part, so grab the body directly.
            values.put("body", replaceFormFeeds(sms.getDisplayMessageBody()));
        } else {
            // Build up the body from the parts.
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < pduCount; i++) {
                sms = msgs[i];
//                if (sms.mWrappedSmsMessage != null) {
                body.append(sms.getDisplayMessageBody());
//                }
            }
            values.put("body", replaceFormFeeds(body.toString()));
        }

        ContentResolver resolver = context.getContentResolver();
        String originatingAddress = sms.getOriginatingAddress();
        int protocolIdentifier = sms.getProtocolIdentifier();
        String selection =
                Sms.ADDRESS + " = ? AND " +
                        Sms.PROTOCOL + " = ?";
        String[] selectionArgs = new String[] {
                originatingAddress, Integer.toString(protocolIdentifier)
        };

//        Cursor cursor = SqliteWrapper.query(context, resolver, Constant.SMS_INBOX_URI,
//                REPLACE_PROJECTION, selection, selectionArgs, null);
        Cursor cursor = resolver.query(Uri.parse(Constant.SMS_INBOX_URI),
                REPLACE_PROJECTION, selection, selectionArgs, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long messageId = cursor.getLong(0);
                    Uri messageUri = ContentUris.withAppendedId(
                            Uri.parse(Constant.SMS_URI_ALL), messageId);

//                    SqliteWrapper.update(context, resolver, messageUri,
//                            values, null, null);
                    resolver.update(messageUri,values,null,null);
                    return messageUri;
                }
            } finally {
                cursor.close();
            }
        }
        return storeMessage(context, msgs, error);
    }

    public static String replaceFormFeeds(String s) {
        // Some providers send formfeeds in their messages. Convert those formfeeds to newlines.
        return s == null ? "" : s.replace('\f', '\n');
    }

//    private static int count = 0;

    private Uri storeMessage(Context context, SmsMessage[] msgs, int error) {
        SmsMessage sms = msgs[0];

        // Store the message in the content provider.
        ContentValues values = extractContentValues(sms);
        values.put(Sms.ERROR_CODE, error);
        int pduCount = msgs.length;

        if (pduCount == 1) {
            // There is only one part, so grab the body directly.
            values.put("body", replaceFormFeeds(sms.getDisplayMessageBody()));
        } else {
            // Build up the body from the parts.
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < pduCount; i++) {
                sms = msgs[i];
//                if (sms.mWrappedSmsMessage != null) {
                    body.append(sms.getDisplayMessageBody());
//                }
            }
            values.put("body", replaceFormFeeds(body.toString()));
        }

        // Make sure we've got a thread id so after the insert we'll be able to delete
        // excess messages.
        Long threadId = values.getAsLong(Sms.THREAD_ID);
        String address = values.getAsString(Sms.ADDRESS);

        // Code for debugging and easy injection of short codes, non email addresses, etc.
        // See Contact.isAlphaNumber() for further comments and results.
//        switch (count++ % 8) {
//            case 0: address = "AB12"; break;
//            case 1: address = "12"; break;
//            case 2: address = "Jello123"; break;
//            case 3: address = "T-Mobile"; break;
//            case 4: address = "Mobile1"; break;
//            case 5: address = "Dogs77"; break;
//            case 6: address = "****1"; break;
//            case 7: address = "#4#5#6#"; break;
//        }

        if (!TextUtils.isEmpty(address)) {
//            Contact cacheContact = Contact.get(address,true);
//            MiddlewareProxy.getInstance().get
//            if (cacheContact != null) {
//                address = cacheContact.getNumber();
//            }
        } else {
//            address = getString(R.string.unknown_sender);
            values.put(Sms.ADDRESS, "Unknown sender");
        }

        if (((threadId == null) || (threadId == 0)) && (address != null)) {
//            threadId = Conversation.getOrCreateThreadId(context, address);
            threadId = getOrCreateThreadId(context, address);
            values.put(Sms.THREAD_ID, threadId);
        }

        ContentResolver resolver = context.getContentResolver();

//        Uri insertedUri = SqliteWrapper.insert(context, resolver, Constant.SMS_INBOX_URI, values);
        Uri insertedUri = resolver.insert(Uri.parse(Constant.SMS_INBOX_URI),values);
        // Now make sure we're not over the limit in stored messages
//        Recycler.getSmsRecycler().deleteOldMessagesByThreadId(context, threadId);
//        MmsWidgetProvider.notifyDatasetChanged(context);

        return insertedUri;
    }

    public long getOrCreateThreadId(
            Context context, String recipient) {
        Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();


        uriBuilder.appendQueryParameter("recipient", recipient);

        Uri uri = uriBuilder.build();
        //if (DEBUG) Rlog.v(TAG, "getOrCreateThreadId uri: " + uri);

//        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
//                uri, ID_PROJECTION, null, null, null);
        Cursor cursor = context.getContentResolver().query(uri,new String[]{"_id"},null,null,null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(0);
                } else {
                    L.e(TAG, "getOrCreateThreadId returned no rows!");
                }
            } finally {
                cursor.close();
            }
        }

        L.e(TAG, "getOrCreateThreadId failed with " + recipient + " recipients");
        throw new IllegalArgumentException("Unable to find or allocate a thread ID.");
    }

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        String format = intent.getStringExtra("format");
//        int subId = intent.getIntExtra(PhoneConstants.SUBSCRIPTION_KEY,
//                SubscriptionManager.getDefaultSmsSubId());

//        Rlog.v(TAG, " getMessagesFromIntent sub_id : " + subId);

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = SmsMessage.createFromPdu(pdu);
//            msgs[i].setSubId(subId);
        }
        return msgs;
    }

}


