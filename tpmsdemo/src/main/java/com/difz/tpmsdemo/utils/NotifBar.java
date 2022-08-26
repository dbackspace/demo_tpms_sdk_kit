package com.difz.tpmsdemo.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.difz.tpmsdemo.R;
import com.difz.tpmsdemo.newcopy.TpmsApplication;
import com.difz.tpmsdemo.newcopy.TpmsMainActivity;
import com.tpms.utils.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * carete by www.difengze.com
 * mail : 19chang19@163.com
 * on 2020/12/17
 */
public class NotifBar {

    private static String TAG = "NotifBar";

    private static int mNotificationState = -1;



    public static synchronized void showNormalNotif(Context context)
    {

//        if (!BuildConfig.ENABLE_NOTIF)
//            return;


        if (mNotificationState == 1) {
            return;
        }
        String CHANNEL_ID = "com.dfz.tpms";
        String CHANNEL_NAME = "tpms";
//support应至少为24,compileSdkVersion必须为24
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

        if (areNotificationsEnabled) {

            Log.i(TAG, "拥有通知的权限");

            //Intent clickIntent = new Intent(); //点击通知之后要发送的广播
            int id = (int) (System.currentTimeMillis() / 1000);
            //clickIntent.setAction("com.tpms.view.tpmsmain");
            Intent clickIntent = new Intent(context, TpmsMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,112, clickIntent, 0);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //String channelId = "channelId" + System.currentTimeMillis();
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId(CHANNEL_ID);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.zhuangtailantaiyazhengchang)));
//                .setBigContentTitle("不普通的标题头，不普通的标题头不普通的标题头不普通的标题头不普通的标题头")
            }

            builder
                    .setContentTitle(context.getString(R.string.zhuangtailantaiya))
//                    .setContentText("这个是个PT长度的CONTENT")
                    .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notif_ok))
                    .setSmallIcon(R.drawable.ic_notif_ok)
                    .setGroupSummary(false)// 强制设置不要折叠
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis());
            //manager.notify(112, builder.build());
            Notification notification = builder.build();

            try
            {
                TpmsApplication app = (TpmsApplication)context.getApplicationContext();
                app.getTpmsServices().startForeground(112, notification);
            } catch (Exception e) {
                //notificationManager.notify(112, notification);
                e.printStackTrace();
            }
            mNotificationState = 1;
        } else {
            Log.w(TAG, "没有通知的权限");
        }


    }

    public static synchronized void showErrorNotifMsg(Context context)
    {
//        if (!BuildConfig.ENABLE_NOTIF)
//            return;

        if (mNotificationState == 0) {
            return;
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.cancelAll();
        String CHANNEL_ID = "com.dfz.tpms";
        String CHANNEL_NAME = "tpms";


        //Intent clickIntent = new Intent(); //点击通知之后要发送的广播
        Intent clickIntent = new Intent(context, TpmsMainActivity.class);
        int id = (int) (System.currentTimeMillis() / 1000);
        //clickIntent.setAction("com.tpms.view.tpmsmain");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,112, clickIntent, 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //String channelId = "channelId" + System.currentTimeMillis();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.ztltaiyayichang)));
//                .setBigContentTitle("不普通的标题头，不普通的标题头不普通的标题头不普通的标题头不普通的标题头")
        }

        builder
                .setContentTitle(context.getString(R.string.zhuangtailantaiya))
//                    .setContentText("这个是个PT长度的CONTENT")
                .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notif_error))
                .setSmallIcon(R.drawable.ic_notif_error)
                .setGroupSummary(false)// 强制设置不要折叠
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());
        //manager.notify(112, builder.build());
        Notification notification = builder.build();

        try
        {
            TpmsApplication app = (TpmsApplication)context.getApplicationContext();
            app.getTpmsServices().startForeground(112, notification);
        } catch (Exception e) {
            //notificationManager.notify(112, notification);
            e.printStackTrace();
        }

        mNotificationState = 0;

    }


    /***
     * 在有些android 10.0还是会悬挂出来
     * @param context
     * @return
     */
    public static synchronized Notification getNormalNotif(Context context)
    {

//        if (!BuildConfig.ENABLE_NOTIF)
//        {
//            return null;
//        }
        if(mNotificationState == 1)
        {
           return null;
        }

        String CHANNEL_ID = "com.dfz.tpms";
        String CHANNEL_NAME = "tpms";
//support应至少为24,compileSdkVersion必须为24
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

        if (areNotificationsEnabled) {

            Log.i(TAG, "拥有通知的权限");

            //Intent clickIntent = new Intent(); //点击通知之后要发送的广播
            int id = (int) (System.currentTimeMillis() / 1000);
            //clickIntent.setAction("com.tpms.view.tpmsmain");
            Intent clickIntent = new Intent(context, TpmsMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,112, clickIntent, 0);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //String channelId = "channelId" + System.currentTimeMillis();
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId(CHANNEL_ID);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.zhuangtailantaiyazhengchang)));
//                .setBigContentTitle("不普通的标题头，不普通的标题头不普通的标题头不普通的标题头不普通的标题头")
            }

            builder
                    .setContentTitle(context.getString(R.string.zhuangtailantaiya))
//                    .setContentText("这个是个PT长度的CONTENT")
                    .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notif_ok))
                    .setSmallIcon(R.drawable.ic_notif_ok)
                    .setGroupSummary(false)// 强制设置不要折叠
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis());
            mNotificationState = 1;
            return builder.build();
            //manager.notify(112, builder.build());
        } else {
            Log.w(TAG, "没有通知的权限");
        }

        return null;
    }
}
