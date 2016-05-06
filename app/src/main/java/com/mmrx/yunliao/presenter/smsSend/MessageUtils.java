package com.mmrx.yunliao.presenter.smsSend;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mmrx.yunliao.presenter.util.L;

/**
 * Created by mmrx on 16/5/6.
 */
public class MessageUtils {

    private static final String TAG = "MessageUtilsLog";

    public static boolean moveMessageToFolder(Context context, Uri uri, int folder, int error) {
        if (uri == null) {
            return false;
        }

        boolean markAsUnread = false;
        boolean markAsRead = false;
        switch(folder) {
            case 1://inbox
            case 3://draft
                break;
            case 4://outbox
            case 2://sent
                markAsRead = true;
                break;
            case 5://failed
            case 6://queued
                markAsUnread = true;
                break;
            default:
                return false;
        }

        ContentValues values = new ContentValues(3);

        values.put("type", folder);
        if (markAsUnread) {
            values.put("read", 0);
        } else if (markAsRead) {
            values.put("read", 1);
        }
        values.put("error_code", error);
        return 1 == context.getContentResolver().update(uri, values,null,null);
    }


    /**
     * 通过联系人电话号码.来获取或者新建一个threadId返回
     * @param context
     * @param recipient
     * @return
     */
    public static long getOrCreateThreadId(
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

    public static Uri addMessageToUri(ContentResolver resolver,
                                      Uri uri, String address, String body, String subject,
                                      Long date, boolean read, boolean deliveryReport, long threadId) {
        ContentValues values = new ContentValues(8);
//
//        values.put(SUBSCRIPTION_ID, subId);
        values.put("address", address);
        if (date != null) {
            values.put("date", date);
        }
        values.put("read", read ? Integer.valueOf(1) : Integer.valueOf(0));
        values.put("subject", subject);
        values.put("body", body);
        if (deliveryReport) {
            values.put("status", 32);
        }
        if (threadId != -1L) {
            values.put("thread_id", threadId);
        }
        return resolver.insert(uri, values);
    }


}
