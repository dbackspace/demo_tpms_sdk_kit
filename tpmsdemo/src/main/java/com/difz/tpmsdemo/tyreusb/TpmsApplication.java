package com.difz.tpmsdemo.tyreusb;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Process;

import com.difz.tpmsdemo.tyreusb.biz.Tpms;
import com.difz.tpmsdemo.tyreusb.biz.Tpms3;
import com.difz.tpmsdemo.tyreusb.stddev.TpmsDataSrc;
import com.difz.tpmsdemo.tyreusb.stddev.TpmsDataSrcUsb;
import com.difz.tpmsdemo.tyreusb.utils.Log;

/* loaded from: classes.dex */
public class TpmsApplication extends Application {
    private Tpms tpms;
    public String TAG = TpmsApplication.class.getSimpleName();
    TpmsDataSrc datasrc = null;
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
        if (this.datasrc == null) {
            this.datasrc = new TpmsDataSrcUsb(this);
            this.datasrc.init();
            this.tpms = new Tpms3(this);
            this.tpms.initCodes();
            this.tpms.init();
            this.datasrc.setBufferFrame(this.tpms.getDecode().getPackBufferFrame());
        }
        this.datasrc.start();
        this.tpms.initShakeHand();
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