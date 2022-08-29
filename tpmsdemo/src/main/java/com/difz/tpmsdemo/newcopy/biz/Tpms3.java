package com.difz.tpmsdemo.newcopy.biz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.difz.tpmsdemo.R;
import com.difz.tpmsdemo.newcopy.TpmsApplication;
import com.difz.tpmsdemo.newcopy.TpmsMainActivity;
import com.difz.tpmsdemo.newcopy.decode.FrameDecode3;
import com.difz.tpmsdemo.newcopy.encode.FrameEncode3;
import com.difz.tpmsdemo.newcopy.modle.AlarmAgrs;
import com.difz.tpmsdemo.newcopy.modle.AlarmCntrol;
import com.difz.tpmsdemo.newcopy.modle.HeartbeatEvent;
import com.difz.tpmsdemo.newcopy.modle.TimeSeedEvent;
import com.difz.tpmsdemo.newcopy.modle.TiresState;
import com.difz.tpmsdemo.newcopy.modle.TiresStateEvent;
import com.difz.tpmsdemo.newcopy.modle.TpmsDevErrorEvent;
import com.difz.tpmsdemo.newcopy.utils.Log;
import com.difz.tpmsdemo.newcopy.widget.CDialog2;
import com.difz.tpmsdemo.newcopy.widget.ClickToast;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/* loaded from: classes.dex */
public class Tpms3 extends Tpms {
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    static final int MaxHiPress = 350;
    static final int MaxLowPress = 240;
    static final int MinHiPress = 250;
    static final int MinLowPress = 100;
    private static final BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() { // from class: com.tpms.biz.Tpms3.8
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String reason = intent.getStringExtra("reason");
                Log.i("test", "reason..:" + reason);
                if (reason != null) {
                    reason.equals("homekey");
                }
            }
        }
    };
    Handler mDataCheckHander;
    int mErrorCount;
    byte time;
    String TAG = "Tpms3";
    AlarmCntrol mCurrentErrCtrl = null;
    int mZhuDongBaojin = 1;
    Handler mTimerCheckSeed = null;
    long startDataTime = -1;
    CDialog2 mConnectErrorDlg = null;
    Runnable mDataCheckTimer = new Runnable() { // from class: com.tpms.biz.Tpms3.1
        @Override // java.lang.Runnable
        public void run() {
            long CurTime = System.currentTimeMillis();
            long datTime = (CurTime - Tpms3.this.startDataTime) / 1000;
            String str = Tpms3.this.TAG;
            Log.i(str, "mDataCheckTimer startDataTime:" + Tpms3.this.startDataTime + ";datTime:" + datTime);
            if (Tpms3.this.startDataTime != -1 && datTime > 120) {
                Tpms3.this.showConnectErrDlg();
                Tpms3.this.showErrorNotifMsg();
                Tpms3 tpms3 = Tpms3.this;
                tpms3.startDataTime = -1L;
                tpms3.mDataCheckHander.postDelayed(Tpms3.this.mDataCheckTimer, 3000L);
                return;
            }
            Tpms3.this.mDataCheckHander.postDelayed(Tpms3.this.mDataCheckTimer, 3000L);
        }
    };
    View.OnClickListener btn_click = new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.7
        @Override // android.view.View.OnClickListener
        public void onClick(View arg0) {
            Tpms3.this.mCurrentErrCtrl.mTimeInterval = Long.parseLong((String) arg0.getTag());
            long time2 = System.currentTimeMillis() / 1000;
            Tpms3.this.mCurrentErrCtrl.mTimeStamp = time2;
            Log.i("ttimeout", "showTimeDialog...mTimeInterval:" + Tpms3.this.mCurrentErrCtrl.mTimeInterval);
            Tpms3.this.mTimedlg.hideCustomToast();
            Tpms3 tpms3 = Tpms3.this;
            tpms3.StopSound(tpms3.mCurrentErrCtrl.mErrorKey);
            Tpms3 tpms32 = Tpms3.this;
            tpms32.mErrorToast = null;
            tpms32.mTimedlg = null;
            if (Tpms3.homeListenerReceiver != null) {
                try {
                    Tpms3.this.app.unregisterReceiver(Tpms3.homeListenerReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Runnable mHeartbeat = new Runnable() { // from class: com.tpms.biz.Tpms3.9
        @Override // java.lang.Runnable
        public void run() {
            Tpms3.this.mencode.SendHeartbeat();
            Tpms3.this.mHeader.postDelayed(Tpms3.this.mHeartbeat, 3000L);
        }
    };
    Runnable CheckEncryptionTime = new Runnable() { // from class: com.tpms.biz.Tpms3.10
        @Override // java.lang.Runnable
        public void run() {
            if (!Tpms3.this.mIsSeedAckOk) {
                Log.i(Tpms3.this.TAG, "CheckEncryptionTime");
                Tpms3.this.showErrorDlg();
            }
        }
    };

    public Tpms3(TpmsApplication _app) {
        super(_app);
        this.time = (byte) 0;
        this.mErrorCount = 0;
        this.mErrorCount = 0;
        this.time = (byte) (System.currentTimeMillis() & 255);
    }

    @Override // com.tpms.biz.Tpms
    public void initCodes() {
        Log.i(this.TAG, "initCodes");
        this.mdecode = new FrameDecode3();
        this.mencode = new FrameEncode3(this.app);
        this.mencode.init(this.app);
        this.mdecode.init(this.app);
    }

    @Override // com.tpms.biz.Tpms
    public AlarmAgrs getAlarmAgrs() {
        return this.mAlarmAgrs;
    }

    @Override // com.tpms.biz.Tpms
    public void init() {
        if (this.mIsInit) {
            return;
        }
        Log.i(this.TAG, "init");
        this.mDataCheckHander = new Handler();
        this.mDataCheckHander.postDelayed(this.mDataCheckTimer, 3000L);
        initShakeHand();
        queryConfig();
        this.mIsInit = true;
    }

    @Override // com.tpms.biz.Tpms
    public void initShakeHand() {
        this.mIsSeedAckOk = false;
        this.mErrorCount = 0;
        shakeHand();
        if (this.mHeader == null) {
            this.mHeader = new Handler();
        }
        this.mHeader.removeCallbacks(this.mHeartbeat);
        this.mHeader.postDelayed(this.mHeartbeat, 1000L);
        this.startDataTime = System.currentTimeMillis();
    }

    @Override // com.tpms.biz.Tpms
    public void shakeHand() {
        Log.i(this.TAG, "shakeHand 握手,没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void queryVersion() {
        Log.i(this.TAG, "查协议版本号,没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void queryConfig() {
        Log.i(this.TAG, "queryConfig 气压上下限，温度上限,没有协议，apk实现");
    }

    @Override // com.tpms.biz.Tpms
    public int addHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值add");
        this.mHiPressStamp += 10;
        if (this.mHiPressStamp > 350) {
            this.mHiPressStamp = 350;
        }
        this.mPreferences.edit().putInt("mHiPressStamp", this.mHiPressStamp).commit();
        return this.mHiPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int decHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值dec");
        this.mHiPressStamp -= 10;
        if (this.mHiPressStamp < 250) {
            this.mHiPressStamp = 250;
        }
        this.mPreferences.edit().putInt("mHiPressStamp", this.mHiPressStamp).commit();
        return this.mHiPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int addLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值add");
        this.mLowPressStamp += 10;
        if (this.mLowPressStamp > MaxLowPress) {
            this.mLowPressStamp = MaxLowPress;
        }
        this.mPreferences.edit().putInt("mLowPressStamp", this.mLowPressStamp).commit();
        return this.mLowPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int decLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值dec");
        this.mLowPressStamp -= 10;
        if (this.mLowPressStamp < 100) {
            this.mLowPressStamp = 100;
        }
        this.mPreferences.edit().putInt("mLowPressStamp", this.mLowPressStamp).commit();
        return this.mLowPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int setGaoya(int yali) {
        Log.w(this.TAG, "设置最高压力阀值,没有协议");
        return (yali / 10) * 10;
    }

    @Override // com.tpms.biz.Tpms
    public int setDiya(int yali) {
        Log.w(this.TAG, "设置最高压力阀值，没有协议");
        return yali * 10;
    }

    @Override // com.tpms.biz.Tpms
    public void queryBackLeft() {
        Log.i(this.TAG, "查左后轮ID 没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void queryBackRight() {
        Log.i(this.TAG, "查右后轮ID  没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void queryFrontLeft() {
        Log.i(this.TAG, "查前左轮ID  没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void queryFrontRight() {
        Log.i(this.TAG, "查前右ID  没有协议");
    }

    @Override // com.tpms.biz.Tpms
    public void paireBackLeft() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对左后轮ID");
        this.mencode.paireBackLeft();
    }

    @Override // com.tpms.biz.Tpms
    public void paireBackRight() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对右后轮ID");
        this.mencode.paireBackRight();
    }

    @Override // com.tpms.biz.Tpms
    public void paireFrontLeft() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前左轮ID");
        this.mencode.paireFrontLeft();
    }

    @Override // com.tpms.biz.Tpms
    public void paireFrontRight() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前右ID");
        this.mencode.paireFrontRight();
    }

    @Override // com.tpms.biz.Tpms
    public void paireSpTired() {
        this.mIsPairedId = true;
        Log.i(this.TAG, "配对前右ID");
        this.mencode.paireSpTired();
    }

    @Override // com.tpms.biz.Tpms
    public void stopPaire() {
        this.mIsPairedId = false;
        Log.i(this.TAG, "stopPaire");
        this.mencode.stopPaire();
    }

    public void onEventMainThread(TimeSeedEvent ack) {
        byte ack0 = (byte) ((((((((this.time ^ 32) ^ 23) ^ 1) ^ (-122)) ^ 100) ^ 1) ^ (-126)) ^ 118);
        if (ack.mSeedAck == ack0) {
            Log.w(this.TAG, "ack.mSeedAck==ack0");
            this.mIsSeedAckOk = true;
            this.mErrorCount = 0;
            if (this.mErrorDlg != null) {
                this.mErrorDlg.hideCustomToast();
                this.mErrorDlg = null;
                return;
            }
            return;
        }
        Log.w(this.TAG, "ack.mSeedAck!=ack0");
        this.mErrorCount++;
        if (this.mErrorCount > 5) {
            showErrorDlg();
        }
    }

    @Override // com.tpms.biz.Tpms
    public void onEventMainThread(TiresStateEvent alarm) {
        if (alarm.tires == 1 && this.mFrontLeft != null) {
            alarm.mState.TiresID = this.mFrontLeft.TiresID;
        } else if (alarm.tires == 2 && this.mFrontRight != null) {
            alarm.mState.TiresID = this.mFrontRight.TiresID;
        } else if (alarm.tires == 3 && this.mBackRight != null) {
            alarm.mState.TiresID = this.mBackRight.TiresID;
        } else if (alarm.tires == 0 && this.mBackLeft != null) {
            alarm.mState.TiresID = this.mBackLeft.TiresID;
        } else if (alarm.tires == 5 && this.mSpareTire != null) {
            alarm.mState.TiresID = this.mSpareTire.TiresID;
            return;
        }
        Log.i(this.TAG, "onEventMainThread(TiresStateEvent alarm)");
        sendTimeSeed();
        hideConnectErrDlg();
        this.startDataTime = System.currentTimeMillis();
        CDialog2 cDialog2 = this.mConnectErrorDlg;
        if (cDialog2 != null) {
            cDialog2.hideCustomToast();
            this.mConnectErrorDlg = null;
        } else {
            this.mDataCheckHander.removeCallbacks(this.CheckEncryptionTime);
            this.mDataCheckHander.postDelayed(this.CheckEncryptionTime, 20000L);
        }
        String title = "";
        String tiresKey = "";
        if (alarm.tires == 1) {
            alarm.mState.mAlarmCntrols = this.mFrontLeft.mAlarmCntrols;
            this.mFrontLeft = alarm.mState;
            title = title + this.app.getResources().getString(R.string.zouqianluntai);
            tiresKey = tiresKey + "leftfront";
        } else if (alarm.tires == 2) {
            alarm.mState.mAlarmCntrols = this.mFrontRight.mAlarmCntrols;
            this.mFrontRight = alarm.mState;
            title = title + this.app.getResources().getString(R.string.youqianluntai);
            tiresKey = tiresKey + "rightfront";
        } else if (alarm.tires == 3) {
            alarm.mState.mAlarmCntrols = this.mBackRight.mAlarmCntrols;
            this.mBackRight = alarm.mState;
            title = title + this.app.getResources().getString(R.string.youhouluntai);
            tiresKey = tiresKey + "rightback";
            if (isAllTiresOk()) {
                if (!isDevCheckOk()) {
                    Log.i(this.TAG, "showNormalNotifMsg but checkerror");
                } else {
                    showNormalNotifMsg();
                }
            } else {
                showErrorNotifMsg();
            }
        } else if (alarm.tires == 0) {
            alarm.mState.mAlarmCntrols = this.mBackLeft.mAlarmCntrols;
            this.mBackLeft = alarm.mState;
            title = title + this.app.getResources().getString(R.string.zouhouluntai);
            tiresKey = tiresKey + "leftback";
        } else if (alarm.tires == 5) {
            alarm.mState.mAlarmCntrols = this.mSpareTire.mAlarmCntrols;
            this.mSpareTire = alarm.mState;
            title = title + this.app.getResources().getString(R.string.beitailuntai);
            tiresKey = tiresKey + "SpareTire";
        }
        if (getZhuDongBaojin() == 0) {
            return;
        }
        showAlarmDialog(tiresKey, title, alarm);
    }

    private boolean isAllTiresOk() {
        TiresState[] triess = {this.mFrontLeft, this.mBackLeft, this.mBackRight, this.mFrontRight};
        for (TiresState tries : triess) {
            if (!thisTiresOk(tries)) {
                return false;
            }
        }
        return true;
    }

    private boolean thisTiresOk(TiresState state) {
        return !state.NoSignal && !state.Leakage && state.AirPressure < this.mHiPressStamp && state.AirPressure > this.mLowPressStamp && state.Temperature < this.mHiTempStamp && !state.LowPower;
    }

    private boolean isTimeOut(String title, long curTime, String errorKey, TiresStateEvent tevent) {
        if (TextUtils.isEmpty(errorKey)) {
            return false;
        }
        TiresState state = tevent.mState;
        Map<String, AlarmCntrol> ctrl = state.mAlarmCntrols;
        AlarmCntrol Cntrol = ctrl.get(errorKey);
        if (Cntrol == null) {
            Log.i("ttimeout", title + errorKey + " no record is time out");
            return true;
        } else if (Cntrol.mTimeStamp == 0 || curTime - Cntrol.mTimeStamp > Cntrol.mTimeInterval) {
            Log.i("ttimeout", title + errorKey + ";dat time:" + (curTime - Cntrol.mTimeStamp) + ";Cntrol.mTimeStamp:" + Cntrol.mTimeStamp + ";Cntrol.mTimeInterval:" + Cntrol.mTimeInterval + ";curTime:" + curTime);
            Cntrol.mTimeInterval = 0L;
            return true;
        } else {
            Log.i("ttimeout", title + errorKey + " no time out;Cntrol.mTimeInterval:" + Cntrol.mTimeInterval);
            return false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:77:0x015b, code lost:
        r0 = "";
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void showAlarmDialog(String r24, String r25, TiresStateEvent r26) {
        /*
            Method dump skipped, instructions count: 632
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tpms.biz.Tpms3.showAlarmDialog(java.lang.String, java.lang.String, com.tpms.modle.TiresStateEvent):void");
    }

    private void IsOkClearInTimeAndCUI(TiresState state, String tires_key) {
        if (!state.NoSignal) {
            resetInTIme("NoSignal", state);
            clearUIAndSound(tires_key + "NoSignal");
        }
        if (!state.Leakage) {
            resetInTIme("Leakage", state);
            clearUIAndSound(tires_key + "Leakage");
        }
        if (state.AirPressure < this.mHiPressStamp) {
            resetInTIme("mHiPressStamp", state);
            clearUIAndSound(tires_key + "mHiPressStamp");
        }
        if (state.AirPressure > this.mLowPressStamp) {
            resetInTIme("mLowPressStamp", state);
            clearUIAndSound(tires_key + "mLowPressStamp");
        }
        if (state.Temperature < this.mHiTempStamp) {
            resetInTIme("mHiTempStamp", state);
            clearUIAndSound(tires_key + "mHiTempStamp");
        }
        if (!state.LowPower) {
            resetInTIme("LowPower", state);
            clearUIAndSound(tires_key + "LowPower");
        }
    }

    private void clearUIAndSound(String guid) {
        if (this.mErrorToast != null && this.mErrorToast.getGuid().equals(guid)) {
            Log.i("testtpms", "================:" + guid);
            this.mErrorToast.hideCustomToast();
            this.mErrorToast = null;
            if (this.mTimedlg != null) {
                this.mTimedlg.hideCustomToast();
                this.mTimedlg = null;
            }
        }
        StopSound(guid);
    }

    private void resetInTIme(String key, TiresState state) {
        AlarmCntrol ctrl = state.mAlarmCntrols.get(key);
        if (ctrl != null) {
            ctrl.mTimeInterval = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMainActivity() {
        Intent inte = new Intent(this.app, TpmsMainActivity.class);
        inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.app.startActivity(inte);
    }

    private void showErrorToast() {
        if (!isDevCheckOk()) {
            return;
        }
        this.mErrorToast = new ClickToast();
        View view = LayoutInflater.from(this.app.getApplicationContext()).inflate(R.layout.click_error_toast, (ViewGroup) null);
        view.findViewById(R.id.relativelayout).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Tpms3.this.startMainActivity();
            }
        });
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Tpms3.this.mErrorToast != null) {
                    Tpms3.this.mErrorToast.hideCustomToast();
                }
                Tpms3.this.showTimeDialog();
            }
        });
        this.mErrorToast.setGuid(this.mCurrentErrCtrl.mErrorKey);
        this.mErrorToast.initToast(this.app.getApplicationContext(), view, this.mCurrentErrCtrl.mError);
        this.mErrorToast.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorDlg() {
        if (this.mErrorDlg == null && !this.mIsPairedId) {
            String str = android.util.Log.getStackTraceString(new Throwable());
            Log.i(this.TAG, "showErrorDlg");
            Log.i(this.TAG, str);
            View view = LayoutInflater.from(this.app.getApplicationContext()).inflate(R.layout.error_dialog, (ViewGroup) null);
            this.mErrorDlg = CDialog2.makeToast(this.app, view, "");
            view.findViewById(R.id.btn_is_ok).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Tpms3.this.mErrorDlg.hideCustomToast();
                    Tpms3.this.mErrorDlg = null;
                }
            });
            showErrorNotifMsg2();
            this.mErrorDlg.show();
            EventBus.getDefault().post(new TpmsDevErrorEvent(0));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConnectErrDlg() {
        Log.i(this.TAG, "showConnectErrDlg1");
        if (this.mConnectErrorDlg != null || this.mIsPairedId) {
            return;
        }
        Log.i(this.TAG, "showConnectErrDlg2");
        View view = LayoutInflater.from(this.app.getApplicationContext()).inflate(R.layout.connect_error_dialog, (ViewGroup) null);
        this.mConnectErrorDlg = CDialog2.makeToast(this.app, view, "");
        view.findViewById(R.id.btn_is_ok).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Tpms3.this.mConnectErrorDlg.hideCustomToast();
                Tpms3.this.mConnectErrorDlg = null;
            }
        });
        this.mConnectErrorDlg.show();
    }

    private void hideConnectErrDlg() {
        CDialog2 cDialog2 = this.mConnectErrorDlg;
        if (cDialog2 == null) {
            return;
        }
        cDialog2.hideCustomToast();
        this.mConnectErrorDlg = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTimeDialog() {
        View view = LayoutInflater.from(this.app.getApplicationContext()).inflate(R.layout.time_dialog, (ViewGroup) null);
        this.mTimedlg = CDialog2.makeToast(this.app, view, "");
        View v0 = view.findViewById(R.id.mainbtn_0);
        View v1 = view.findViewById(R.id.mainbtn_1);
        View v2 = view.findViewById(R.id.mainbtn_2);
        View v3 = view.findViewById(R.id.mainbtn_3);
        v0.setOnClickListener(this.btn_click);
        v1.setOnClickListener(this.btn_click);
        v2.setOnClickListener(this.btn_click);
        v3.setOnClickListener(this.btn_click);
        view.findViewById(R.id.time_dialog_plane).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.biz.Tpms3.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Tpms3.this.mTimedlg != null) {
                    Tpms3.this.mTimedlg.hideCustomToast();
                }
                Tpms3.this.mErrorToast.hideCustomToast();
                Tpms3 tpms3 = Tpms3.this;
                tpms3.mErrorToast = null;
                tpms3.mTimedlg = null;
                if (Tpms3.homeListenerReceiver != null) {
                    try {
                        Tpms3.this.app.unregisterReceiver(Tpms3.homeListenerReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        IntentFilter homeFilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        this.app.registerReceiver(homeListenerReceiver, homeFilter);
        this.mTimedlg.show();
    }

    public void queryAllState() {
        Log.i(this.TAG, "queryAllState ");
    }

    public void onEventMainThread(HeartbeatEvent hb) {
        HeartbeatEventAck();
    }

    @Override // com.tpms.biz.Tpms
    protected void HeartbeatEventAck() {
        this.mencode.HeartbeatEventAck();
    }

    protected void Heartbeat() {
        this.mencode.HeartbeatEventAck();
    }

    @Override // com.tpms.biz.Tpms
    public void querySensorID() {
        this.mencode.querySensorID();
    }

    @Override // com.tpms.biz.Tpms
    public int setHiTempDef() {
        this.mHiTempStamp = 75;
        this.mPreferences.edit().putInt("mHiTempStamp", 75).commit();
        return this.mHiTempStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int setHiPressDef() {
        this.mHiPressStamp = 310;
        this.mPreferences.edit().putInt("mHiPressStamp", 310).commit();
        return this.mHiPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public int setLowPressDef() {
        this.mLowPressStamp = 180;
        this.mPreferences.edit().putInt("mLowPressStamp", 180).commit();
        return this.mLowPressStamp;
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeLeftFrontRightFront() {
        this.mencode.exchangeLeftFrontRightFront();
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeLeftFrontLeftBack() {
        this.mencode.exchangeLeftFrontLeftBack();
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeLeftFrontRightBack() {
        this.mencode.exchangeLeftFrontRightBack();
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeRightFrontLeftBack() {
        this.mencode.exchangeRightFrontLeftBack();
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeRightFrontRightBack() {
        this.mencode.exchangeRightFrontRightBack();
    }

    @Override // com.tpms.biz.Tpms
    public void exchangeLeftBackRightBack() {
        this.mencode.exchangeLeftBackRightBack();
    }

    @Override // com.tpms.biz.Tpms
    public void exchange_sp_fl() {
        this.mencode.exchange_sp_fl();
    }

    @Override // com.tpms.biz.Tpms
    public void exchange_sp_fr() {
        this.mencode.exchange_sp_fr();
    }

    @Override // com.tpms.biz.Tpms
    public void exchange_sp_bl() {
        this.mencode.exchange_sp_bl();
    }

    @Override // com.tpms.biz.Tpms
    public void exchange_sp_br() {
        this.mencode.exchange_sp_br();
    }

    @Override // com.tpms.biz.Tpms
    public void unInit() {
        if (!this.mIsInit) {
            return;
        }
        unintShakeHand();
        this.mDataCheckHander.removeCallbacks(this.mDataCheckTimer);
        super.unInit();
    }

    @Override // com.tpms.biz.Tpms
    public void unintShakeHand() {
        Log.i(this.TAG, "unintShakeHand");
        this.mIsSeedAckOk = false;
        this.mHeader.removeCallbacks(this.mHeartbeat);
        this.mErrorCount = 0;
        Handler handler = this.mTimerCheckSeed;
        if (handler != null) {
            handler.removeCallbacks(this.CheckEncryptionTime);
            this.mTimerCheckSeed = null;
        }
        this.startDataTime = -1L;
        showErrorNotifMsg();
    }

    public void sendTimeSeed() {
        String str = this.TAG;
        Log.i(str, "sendTimeSeed mIsSeedAckOk:" + this.mIsSeedAckOk);
        if (!this.mIsSeedAckOk) {
            this.mencode.SendEncryption(this.time);
        }
        if (this.mTimerCheckSeed == null) {
            this.mTimerCheckSeed = new Handler();
            this.mTimerCheckSeed.postDelayed(this.CheckEncryptionTime, 600000L);
        }
    }
}