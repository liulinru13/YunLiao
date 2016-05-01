package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/4/19.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.mmrx.yunliao.R;
import com.mmrx.yunliao.SmsReceiver.SmsNotificationClickReceiver;
import com.mmrx.yunliao.model.Constant;

import java.io.IOException;

/**
 * 创建人: mmrx
 * 时间: 16/4/19下午1:47
 * 描述: 消息通知管理类
 */
public class YLNotificationManager {

    public static void onNewSmsReceived(Context context,Uri uri){
        if(uri == null)
            return;
        //获取应用中的设置
        SharedPreferences sp = SPUtil.getPreference(context);
        boolean voiceOn = sp.getBoolean(Constant.SP_SETTING_NOTICE_VOICE, true);
        boolean vibratOn = sp.getBoolean(Constant.SP_SETTING_NOTICE_VIBR, false);
        boolean notifiOn = sp.getBoolean(Constant.SP_SETTING_NOTICE_NOTIFI, false);

        if(voiceOn){
            playSmsVoice(context);
        }
        if(vibratOn){
            playSmsVibra(context);
        }
        if(notifiOn){
            showNotification(context,1,uri);
        }
    }

    public static void playSmsVoice(Context context){
        MediaPlayer mp = new MediaPlayer();
        mp.reset();
        try {
            mp.setDataSource(context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mp.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
        mp.start();
    }

    public static void playSmsVibra(Context context){
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    public static void showNotification(Context context,int id,Uri uri){
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Cursor cursor = context.getContentResolver().query(uri,new String[]{"address"},null,null,null);
        String addr = "";
        if(cursor != null && cursor.moveToFirst()){
            addr = cursor.getString(0);
        }
        try{
            int smallIconId = R.drawable.ic_launcher;
            Bitmap largeIcon = Glide.with(context).load(smallIconId).asBitmap().into(50,50).get();
            String info = "新短信";
            if(!addr.equals("")){
                info += " 来自"+addr;
            }
            //设置点击通知栏的动作为启动另外一个广播
            Intent broadcastIntent = new Intent();
            broadcastIntent.setClass(context, SmsNotificationClickReceiver.class);

            PendingIntent pendingIntent = PendingIntent.
                    getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setLargeIcon(largeIcon)//下拉后显示的图标
                    .setSmallIcon(smallIconId)
                    .setContentTitle("新短信")//下拉后显示的标题
                    .setContentText(info)//下拉后显示的内容
                    .setTicker(info)//未下拉时显示的内容
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            nm.notify(id,builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
