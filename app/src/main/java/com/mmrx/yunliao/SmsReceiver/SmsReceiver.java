package com.mmrx.yunliao.SmsReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Telephony;


/**
 * Handle incoming SMSes.  Just dispatches the work off to a Service.
 */
public class SmsReceiver extends BroadcastReceiver {
    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;
    private static SmsReceiver sInstance;

    public static SmsReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new SmsReceiver();
        }
        return sInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onReceiveWithPrivilege(context, intent, false);
    }

    protected void onReceiveWithPrivilege(Context context, Intent intent, boolean privileged) {
        // If 'privileged' is false, it means that the intent was delivered to the base
        // no-permissions receiver class.  If we get an SMS_RECEIVED message that way, it
        // means someone has tried to spoof the message by delivering it outside the normal
        // permission-checked route, so we just ignore it.
        if (!privileged && intent.getAction().equals(Telephony.Sms.Intents.SMS_DELIVER_ACTION)) {
            return;
        }

        intent.setClass(context, SmsService.class);
        intent.putExtra("result", getResultCode());
        beginStartingService(context, intent);
    }

    // N.B.: <code>beginStartingService</code> and
    // <code>finishStartingService</code> were copied from
    // <code>com.android.calendar.AlertReceiver</code>.  We should
    // factor them out or, even better, improve the API for starting
    // services under wake locks.

    /**
     * Start the service to process the current event notifications, acquiring
     * the wake lock before returning to ensure that the service will run.
     */
    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager pm =
                        (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "StartingAlertService");
                mStartingService.setReferenceCounted(false);
            }
            mStartingService.acquire();
            context.startService(intent);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishStartingService(Service service, int startId) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    mStartingService.release();
                }
            }
        }
    }
}


//public class SmsReceiver extends BroadcastReceiver {
//    private final String TAG = "SmsReceiverLog";
//    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//    private static final String SMS_DELEVERED = "android.provider.Telephony.SMS_DELIVER";
//    public SmsReceiver() {
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        // an Intent broadcast.
//        parseIntent(context,intent);
//    }
//
//    @TargetApi(23)
//    private void parseIntent(Context context,Intent intent){
//        if (intent.getAction().equals(SMS_RECEIVED)) {
//            SmsManager sms = SmsManager.getDefault();
//            Bundle bundle = intent.getExtras();
//            String fromat = intent.getStringExtra("format");
//            if (bundle != null) {
//                Object[] pdus = (Object[]) bundle.get("pdus");
//                SmsMessage[] messages = new SmsMessage[pdus.length];
//                for (int i = 0; i < pdus.length; i++) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], fromat);
//                    }
//                    else{
//                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                    }
//                }
//                for (SmsMessage message : messages){
//                    String msg = message.getMessageBody();
//                    String to = message.getOriginatingAddress();
//                    L.i(TAG,msg+" " + to);
//                }
//
//            }
//        }
//    }
//
//}
