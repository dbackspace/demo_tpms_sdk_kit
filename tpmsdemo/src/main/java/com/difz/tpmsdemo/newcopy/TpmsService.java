package com.difz.tpmsdemo.newcopy;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.difz.tpmsdemo.R;

import androidx.core.app.NotificationCompat;

/* loaded from: classes.dex */
public class TpmsService extends Service {
    private static final String NOTIFI_CLICK_ACTION = "com.syt.tpms.action.NOTIFI_CLICK_ACTION";
    private static final int SERVICE_NOTIFICATION_ID = 112;
    private static final String TAG = "TpmsService";
    Runnable getCurentWindow = new Runnable() { // from class: com.syt.tmps.TpmsService.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityManager am = (ActivityManager) TpmsService.this.getSystemService("activity");
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.d("TestService", "pkg:" + cn.getPackageName());
                Log.d("TestService", "cls:" + cn.getClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Handler().postDelayed(TpmsService.this.getCurentWindow, 2000L);
        }
    };

    @Override // android.app.Service
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        ((TpmsApplication) getApplication()).attachService(this);
        startForeground(SERVICE_NOTIFICATION_ID, getForegroundNotification());
    }

    private Notification getForegroundNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("com.dfz.tpms", "tpms", 4);
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(this, TpmsMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, SERVICE_NOTIFICATION_ID, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, "com.dfz.tpms").setContentTitle("胎压").setContentText("正在运行").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notif_ok).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notif_ok)).setContentIntent(pendingIntent).build();
        return notification;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}