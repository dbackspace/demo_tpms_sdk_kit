package com.difz.tpmsdemo.newcopy;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Process;

import com.difz.tpmssdk.BKReceiver;
import com.difz.tpmssdk.biz.Tpms;
import com.difz.tpmssdk.biz.Tpms3;
import com.std.dev.TpmsDataSrc;
import com.std.dev.TpmsDataSrcUsb;
import com.tpms.utils.Log;

/* loaded from: classes.dex */
public class TpmsApplication extends Application {
    private Service mAppService;
    private Tpms tpms;
    public String TAG = TpmsApplication.class.getSimpleName();
    TpmsDataSrc datasrc = null;
    BKReceiver mReceive = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.syt.tmps.TpmsApplication.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                if (device != null) {
                    String name = device.getDeviceName();
                    int did = device.getDeviceId();
                    String str = TpmsApplication.this.TAG;
                    Log.i(str, "==================================name:" + name + ";did:" + did);
                    if (TpmsApplication.this.datasrc == null) {
                        Log.i(TpmsApplication.this.TAG, "datasrc==null");
                    } else if (name.equals(TpmsApplication.this.datasrc.getDevName())) {
                        Log.i(TpmsApplication.this.TAG, "kill safe");
                        Process.myPid();
                        TpmsApplication.this.stopTpms();
                    }
                }
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                Log.e(TpmsApplication.this.TAG, " ACTION_USB_ACCESSORY_ATTACHED usb 插入");
                TpmsApplication.this.startTpms();
            }
        }
    };

    public void attachService(Service service) {
        this.mAppService = service;
    }

    public Service getTpmsServices() {
        return this.mAppService;
    }

    public TpmsApplication() {
        String str = this.TAG;
        Log.i(str, "BTApplication tid:" + Thread.currentThread().getId());
    }

    public TpmsDataSrc getDataSrc() {
        return this.datasrc;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Log.init(this);
        Log.setLogToFile(false);
        String str = this.TAG;
        Log.i(str, "App is onCreate tid:" + Thread.currentThread().getId());
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, TpmsService.class));
        } else {
            startService(new Intent(this, TpmsService.class));
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        registerReceiver(this.mReceiver, filter);
        startTpms();
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
        String str = this.TAG;
        Log.i(str, "App is onTerminate tid:" + Thread.currentThread().getId());
    }

    public Tpms getTpms() {
        return this.tpms;
    }

    public void startTpms() {
        Log.i(this.TAG, "startTpms");
        this.tpms = Tpms3.getInstant();
        this.tpms.initSrc(this, null);
        this.tpms.startTpms();
    }

    public void stopTpms() {
        Log.i(this.TAG, "stopTpms");
        TpmsDataSrc tpmsDataSrc = this.datasrc;
        if (tpmsDataSrc != null) {
            tpmsDataSrc.stop();
        }
        Tpms tpms = this.tpms;
        if (tpms != null) {
            tpms.unintShakeHand();
        }
    }
}