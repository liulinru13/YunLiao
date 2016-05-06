package com.mmrx.yunliao.SmsReceiver;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.provider.SyncStateContract;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.mmrx.yunliao.model.Constant;
import com.mmrx.yunliao.model.exception.MmsException;
import com.mmrx.yunliao.presenter.smsSend.MessageUtils;
import com.mmrx.yunliao.presenter.smsSend.SmsMessageSender;
import com.mmrx.yunliao.presenter.smsSend.SmsSingleRecipientSender;
import com.mmrx.yunliao.presenter.util.EncodeDecodeUtil;
import com.mmrx.yunliao.presenter.util.L;
import com.mmrx.yunliao.presenter.util.MiddlewareProxy;
import com.mmrx.yunliao.presenter.util.SPUtil;
import com.mmrx.yunliao.presenter.util.YLNotificationManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmsService extends Service {

    private static final String TAG = "SmsServiceLog";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELEVERED = "android.provider.Telephony.SMS_DELIVER";
    public static final String ACTION_SEND_MESSAGE = "com.mmrx.yunliao.SmsReceiver.SEND_MESSAGE";
    public static final String ACTION_SERVICE_STATE_CHANGED = "com.mmrx.yunliao.SmsReceiver.SERVICE_STATE";
    public static final String EXTRA_MESSAGE_SENT_SEND_NEXT ="SendNextMsg";
    public static final String MESSAGE_SENT_ACTION = "com.mmrx.yunliao.SmsReceiver.MESSAGE_SENT_ACTION";
    private ServiceHandler mServiceHandler;
    public Handler mToastHandler = new Handler();
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

    // 和SEND_PROJECTION.是对应的
    private static final int SEND_COLUMN_ID         = 0;
    private static final int SEND_COLUMN_THREAD_ID  = 1;
    private static final int SEND_COLUMN_ADDRESS    = 2;
    private static final int SEND_COLUMN_BODY       = 3;
    private static final int SEND_COLUMN_STATUS     = 4;

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
                    Uri uri = handleSmsReceived(intent,error);
                    if(uri != null) {
                        event = Constant.FLAG_SMS_NEW_RECEIVED;
                        YLNotificationManager.onNewSmsReceived(SmsService.this, uri);
                    }
                }
                /*
                * 处理Sms发送handleSmsSent，用于发送多条信息的时候。
                * 第一条由ACTION_SEND_MESSAGE处理，剩下是该action处理。从待发送消息队列中取得消息，并按次序逐个发送；
                * */
                if (ACTION_SEND_MESSAGE.equals(intent.getAction())) {
                    handleSmsSent(intent, error);
                }
//               else if (SMS_DELIVER_ACTION.equals(action)) {
//                    handleSmsReceived(intent, error);
//                }
                /*
                * 系统启动完成后BOOT_COMPLETED，把发件箱(outbox)中的消息移动到发送队列(QueuedBox)，
                * 然后开始发送队列中的消息，最后调用blockingUpdateNewMessageIndicator()方法更新状态栏消息指示图标；
                * */
                else if (ACTION_BOOT_COMPLETED.equals(action)) {
                    handleBootCompleted();
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
            MiddlewareProxy.getInstance().notifyAllSmsObserver(event);
        }
    }

    private void handleSmsSent(Intent intent, int error) {
        Uri uri = intent.getData();
        mSending = false;
        boolean sendNextMsg = intent.getBooleanExtra(EXTRA_MESSAGE_SENT_SEND_NEXT, false);

//        if (LogTag.DEBUG_SEND) {
//            Log.v(TAG, "handleSmsSent uri: " + uri + " sendNextMsg: " + sendNextMsg +
//                    " mResultCode: " + mResultCode +
//                    " = " + translateResultCode(mResultCode) + " error: " + error);
//        }

        if (mResultCode == Activity.RESULT_OK) {
//            if (LogTag.DEBUG_SEND || Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//                Log.v(TAG, "handleSmsSent move message to sent folder uri: " + uri);
//            }
            if (!MessageUtils.moveMessageToFolder(getApplicationContext(), uri, 2, error)) {//type 2 sent
//                Log.e(TAG, "handleSmsSent: failed to move message " + uri + " to sent folder");
            }
            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }

            // Update the notification for failed messages since they may be deleted.
//            MessagingNotification.nonBlockingUpdateSendFailedNotification(this);
        } else if ((mResultCode == SmsManager.RESULT_ERROR_RADIO_OFF) ||
                (mResultCode == SmsManager.RESULT_ERROR_NO_SERVICE)) {
//            if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//                Log.v(TAG, "handleSmsSent: no service, queuing message w/ uri: " + uri);
//            }
            // We got an error with no service or no radio. Register for state changes so
            // when the status of the connection/radio changes, we can try to send the
            // queued up messages.
            registerForServiceStateChanges();
            // We couldn't send the message, put in the queue to retry later.
            MessageUtils.moveMessageToFolder(this, uri, 6, error);//Sms.MESSAGE_TYPE_QUEUED
            mToastHandler.post(new Runnable() {
                public void run() {
                    Toast.makeText(SmsService.this,"Currently can\\'t send your message. It will be sent when the service becomes available.",
                            Toast.LENGTH_SHORT).show();
                }
            });
//        }
//        else if (mResultCode == SmsManager.RESULT_ERROR_FDN_CHECK_FAILURE) {
//            messageFailedToSend(uri, mResultCode);
//            mToastHandler.post(new Runnable() {
//                public void run() {
//                    Toast.makeText(SmsReceiverService.this, getString(R.string.fdn_check_failure),
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
        } else {
            messageFailedToSend(uri, error);
            if (sendNextMsg) {
                sendFirstQueuedMessage();
            }
        }
    }

    private void messageFailedToSend(Uri uri, int error) {
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) || LogTag.DEBUG_SEND) {
//            Log.v(TAG, "messageFailedToSend msg failed uri: " + uri + " error: " + error);
//        }
        MessageUtils.moveMessageToFolder(this, uri, 5, error);//Sms.MESSAGE_TYPE_FAILED
//        MessagingNotification.notifySendFailed(getApplicationContext(), true);
    }

    /**
     * 处理新短信接收
     * @param intent
     * @param error
     */
    private Uri handleSmsReceived(Intent intent, int error) {
        SmsMessage[] msgs = getMessagesFromIntent(intent);
        String format = intent.getStringExtra("format");
        SmsFilter filter = new SmsFilter(this);
        //对接收到的短信进行过滤
        if((msgs = filter.doFilter(msgs)) == null){
            return null;
        }
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
        return messageUri;

    }

    /**
     * 在系统重启后处理在发送列表中的短信
     */
    private void handleBootCompleted() {
        // Some messages may get stuck in the outbox. At this point, they're probably irrelevant
        // to the user, so mark them as failed and notify the user, who can then decide whether to
        // resend them manually.
        int numMoved = moveOutboxMessagesToFailedBox();
        if (numMoved > 0) {
//            MessagingNotification.notifySendFailed(getApplicationContext(), true);
        }

        // Send any queued messages that were waiting from before the reboot.
        sendFirstQueuedMessage();

        // Called off of the UI thread so ok to block.
//        MessagingNotification.blockingUpdateNewMessageIndicator(
//                this, MessagingNotification.THREAD_ALL, false);
    }

    /**
     * Move all messages that are in the outbox to the failed state and set them to unread.
     * @return The number of messages that were actually moved
     * 将发件箱的短信全部移动到发送失败的列表中去.
     */
    private int moveOutboxMessagesToFailedBox() {
        ContentValues values = new ContentValues(3);

        values.put("type", 5);
        values.put("error_code", SmsManager.RESULT_ERROR_GENERIC_FAILURE);
        values.put("read", Integer.valueOf(0));

//        int messageCount = SqliteWrapper.update(
//                getApplicationContext(), getContentResolver(), Outbox.CONTENT_URI,
//                values, "type = " + Sms.MESSAGE_TYPE_OUTBOX, null);
        int messageCount = getApplicationContext().getContentResolver()
                .update(Uri.parse(Constant.SMS_OUTBOX_URI), values, "type = 4", null);

//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) || LogTag.DEBUG_SEND) {
//            Log.v(TAG, "moveOutboxMessagesToFailedBox messageCount: " + messageCount);
//        }
        return messageCount;
    }

    /**
     * 对发件箱中第一条短信进行发送操作
     */
    public synchronized void sendFirstQueuedMessage() {
        boolean success = true;
        // get all the queued messages from the database
        final Uri uri = Uri.parse(Constant.SMS_QUEUED_URI);
        ContentResolver resolver = getContentResolver();
//        Cursor c = SqliteWrapper.query(this, resolver, uri,
//                SEND_PROJECTION, null, null, "date ASC");   // date ASC so we send out in
        Cursor c = resolver.query(uri,SEND_PROJECTION,null,null,"date ASC");
        // same order the user tried
        // to send messages.
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    String msgText = c.getString(SEND_COLUMN_BODY);
                    String address = c.getString(SEND_COLUMN_ADDRESS);
                    int threadId = c.getInt(SEND_COLUMN_THREAD_ID);
                    int status = c.getInt(SEND_COLUMN_STATUS);

                    int msgId = c.getInt(SEND_COLUMN_ID);
                    Uri msgUri = ContentUris.withAppendedId(Uri.parse(Constant.SMS_URI_ALL), msgId);

                    SmsMessageSender sender = new SmsSingleRecipientSender(this,
                            address, msgText, threadId, status == Sms.STATUS_PENDING,
                            msgUri);

                    try {
                        sender.sendMessage(-1l);;
                        mSending = true;
                    } catch (MmsException e) {
                        e.printStackTrace();
                        mSending = false;
                        messageFailedToSend(msgUri, SmsManager.RESULT_ERROR_GENERIC_FAILURE);
                        success = false;
                        // Sending current message fails. Try to send more pending messages
                        // if there is any.
                        sendBroadcast(new Intent(SmsService.ACTION_SEND_MESSAGE,
                                null,
                                this,
                                SmsReceiver.class));
                    }
                }
            } finally {
                c.close();
            }
        }
        if (success) {
            // We successfully sent all the messages in the queue. We don't need to
            // be notified of any service changes any longer.
            unRegisterForServiceStateChanges();
        }
    }

    /**
     * 将短信保存在数据库中
     * @param context
     * @param msgs
     * @param error
     * @param format
     * @return
     */
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
     * 取出除了短信内容以外的所有信息放入ContentValues对象中返回
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

    /**
     * 内容替换 \f替换为\n
     * @param s
     * @return
     */
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

        if(SPUtil.getPreference(context).getBoolean(Constant.SP_SETTING_MAIN_ENCODE_SWITCH,false)){
            String body = values.getAsString("body");
            values.put("body", EncodeDecodeUtil.getInstance().encode(body));
        }

        // Make sure we've got a thread id so after the insert we'll be able to delete
        // excess messages.
        Long threadId = values.getAsLong(Sms.THREAD_ID);
        String address = values.getAsString(Sms.ADDRESS);

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
            threadId = MessageUtils.getOrCreateThreadId(context, address);
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



    /**
     * 从intent中获取所有的短信信息,封装到SmsMessage数组中返回
     * @param intent
     * @return
     */
    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        String format = intent.getStringExtra("format");
        //这个是sim卡的编号
//        int subId = intent.getIntExtra(PhoneConstants.SUBSCRIPTION_KEY,
//                SubscriptionManager.getDefaultSmsSubId());

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = SmsMessage.createFromPdu(pdu);
//            msgs[i].setSubId(subId);
        }
        return msgs;
    }



    private void registerForServiceStateChanges() {
        Context context = getApplicationContext();
        unRegisterForServiceStateChanges();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) || LogTag.DEBUG_SEND) {
//            Log.v(TAG, "registerForServiceStateChanges");
//        }

        context.registerReceiver(SmsReceiver.getInstance(), intentFilter);
    }

    private void unRegisterForServiceStateChanges() {
//        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE) || LogTag.DEBUG_SEND) {
//            Log.v(TAG, "unRegisterForServiceStateChanges");
//        }
        try {
            Context context = getApplicationContext();
            context.unregisterReceiver(SmsReceiver.getInstance());
        } catch (IllegalArgumentException e) {
            // Allow un-matched register-unregister calls
        }
    }




}


