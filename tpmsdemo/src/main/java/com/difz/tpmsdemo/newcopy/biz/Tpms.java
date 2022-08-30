package com.difz.tpmsdemo.newcopy.biz;

import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.difz.tpmsdemo.newcopy.TpmsApplication;
import com.difz.tpmsdemo.newcopy.decode.FrameDecode;
import com.difz.tpmsdemo.newcopy.encode.FrameEncode;
import com.difz.tpmsdemo.newcopy.encode.Util;
import com.difz.tpmsdemo.newcopy.modle.AlarmAgrs;
import com.difz.tpmsdemo.newcopy.modle.AlarmCntrol;
import com.difz.tpmsdemo.newcopy.modle.PaireIDOkEvent;
import com.difz.tpmsdemo.newcopy.modle.QueryIDOkEvent;
import com.difz.tpmsdemo.newcopy.modle.ShakeHands;
import com.difz.tpmsdemo.newcopy.modle.TiresState;
import com.difz.tpmsdemo.newcopy.modle.TiresStateEvent;
import com.difz.tpmsdemo.newcopy.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class Tpms {
    private static final String BOOT_COMPLATE = "android.intent.action.BOOT_COMPLETED";
    TpmsApplication app;
    Handler mHeader;
    protected SharedPreferences mPreferences;
    String TAG = "Tpms";
    Util Util = null;
    FrameEncode mencode = null;
    FrameDecode mdecode = null;
    AlarmAgrs mAlarmAgrs = new AlarmAgrs();
    boolean mIsInit = false;
    int mZhuDongBaojin = 1;
    String mWenduDanwei = "℃";
    String mYaliDanwei = "Bar";
    int mHiTempStamp = 75;
    int mHiPressStamp = 310;
    int mLowPressStamp = 180;
    boolean mIsSeedAckOk = true;
    boolean mIsPairedId = false;
    Runnable getTpmsState = new Runnable() { // from class: com.tpms.biz.Tpms.1
        @Override // java.lang.Runnable
        public void run() {
            new Handler().postDelayed(Tpms.this.getTpmsState, 3000L);
        }
    };
    boolean mbForeground = false;
    TiresState mFrontLeft = new TiresState();
    TiresState mFrontRight = new TiresState();
    TiresState mBackLeft = new TiresState();
    TiresState mBackRight = new TiresState();
    TiresState mSpareTire = new TiresState();

    public Tpms(TpmsApplication _app) {
        this.app = null;
        this.mPreferences = _app.getSharedPreferences("setting", 0);
        initData();
        EventBus.getDefault().register(this);
        this.app = _app;
    }

    private void initData() {
        this.mHiTempStamp = getHiTemp();
        this.mHiPressStamp = getHiPress();
        this.mLowPressStamp = getLowPress();
    }

    public FrameDecode getDecode() {
        return this.mdecode;
    }

    public AlarmAgrs getAlarmAgrs() {
        return this.mAlarmAgrs;
    }

    public void initCodes() {
        this.mencode = new FrameEncode(this.app);
        this.mencode.init(this.app);
        this.mdecode = new FrameDecode();
        this.mdecode.init(this.app);
    }

    public void init() {
        Log.i(this.TAG, "握手");
        if (this.mIsInit) {
            return;
        }
        shakeHand();
        queryConfig();
        this.mIsInit = true;
    }

    public void unintShakeHand() {
    }

    public void initShakeHand() {
    }

    public void unInit() {
        if (!this.mIsInit) {
            return;
        }
        clearAlarmCntrol();
        EventBus.getDefault().unregister(this);
        this.mIsInit = false;
    }

    private void clearAlarmCntrol() {
        this.mFrontLeft.mAlarmCntrols = new HashMap();
        this.mFrontRight.mAlarmCntrols = new HashMap();
        this.mBackLeft.mAlarmCntrols = new HashMap();
        this.mBackRight.mAlarmCntrols = new HashMap();
        this.mSpareTire.mAlarmCntrols = new HashMap();
    }

    public void shakeHand() {
        Log.i(this.TAG, "shakeHand 握手");
        byte[] frame = {-86, -79, -95, 7, 17, 0, 20};
        this.mencode.send(frame);
    }

    public void queryVersion() {
        Log.i(this.TAG, "查协议版本号");
        byte[] frame = {-86, -79, -95, 7, -127, 0, -124};
        this.mencode.send(frame);
    }

    public void queryConfig() {
        byte[] testQuery = {-86, -79, -95, 7, 33, 0, 36};
        this.mencode.send(testQuery);
    }

    protected void delayMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int setWendu(int wendu) {
        Log.i(this.TAG, "设置最高温度阀值");
        byte[] frame = {-86, -79, -95, 8, 49, 2, (byte) wendu, -124};
        this.mencode.send(frame);
        return wendu;
    }

    public int getHiTemp() {
        return this.mPreferences.getInt("mHiTempStamp", 75);
    }

    public int addHiTemp() {
        Log.i(this.TAG, "设置最高温度阀值");
        this.mHiTempStamp++;
        if (this.mHiTempStamp > 100) {
            this.mHiTempStamp = 100;
        }
        this.mPreferences.edit().putInt("mHiTempStamp", this.mHiTempStamp).commit();
        return this.mHiTempStamp;
    }

    public int decHiTemp() {
        Log.i(this.TAG, "设置最高温度阀值");
        this.mHiTempStamp--;
        if (this.mHiTempStamp < 50) {
            this.mHiTempStamp = 50;
        }
        this.mPreferences.edit().putInt("mHiTempStamp", this.mHiTempStamp).commit();
        return this.mHiTempStamp;
    }

    public String getWenduDanwei() {
        return this.mPreferences.getString("mWenduDanwei", "℃");
    }

    public String setNextWenduDanwei() {
        if (this.mWenduDanwei == "℃") {
            this.mWenduDanwei = "℉";
        } else {
            this.mWenduDanwei = "℃";
        }
        this.mPreferences.edit().putString("mWenduDanwei", this.mWenduDanwei).commit();
        return this.mWenduDanwei;
    }

    public int setGaoya(int yali) {
        int yali2 = yali / 10;
        Log.i(this.TAG, "设置最高压力阀值");
        byte[] frame = {-86, -79, -95, 8, 49, 0, (byte) yali2, -124};
        this.mencode.send(frame);
        return yali2 * 10;
    }

    public int setDiya(int yali) {
        int yali2 = yali / 10;
        Log.i(this.TAG, "设置最高压力阀值");
        byte[] frame = {-86, -79, -95, 8, 49, 1, (byte) yali2, -124};
        this.mencode.send(frame);
        return yali2 * 10;
    }

    public String getYaliDanwei() {
        return this.mPreferences.getString("mYaliDanwei", this.mYaliDanwei);
    }

    public String setNextYaliDanwei() {
        if (this.mYaliDanwei.equals("Bar")) {
            this.mYaliDanwei = "Psi";
        } else if (this.mYaliDanwei.equals("Psi")) {
            this.mYaliDanwei = "Kpa";
        } else {
            this.mYaliDanwei = "Bar";
        }
        this.mPreferences.edit().putString("mYaliDanwei", this.mYaliDanwei).commit();
        return this.mYaliDanwei;
    }

    public String setPreYaliDanwei() {
        if (this.mYaliDanwei.equals("Bar")) {
            this.mYaliDanwei = "Kpa";
        } else if (this.mYaliDanwei.equals("Psi")) {
            this.mYaliDanwei = "Bar";
        } else {
            this.mYaliDanwei = "Psi";
        }
        this.mPreferences.edit().putString("mYaliDanwei", this.mYaliDanwei).commit();
        return this.mYaliDanwei;
    }

    public void setZhuDongBaojin(int zhudong) {
        this.mZhuDongBaojin = zhudong;
    }

    public int getZhuDongBaojin() {
        return this.mZhuDongBaojin;
    }

    public void queryBackLeft() {
        Log.i(this.TAG, "查左后轮ID");
        byte[] frame = {-86, -79, -95, 7, 65, 0, -124};
        this.mencode.send(frame);
    }

    public void queryBackRight() {
        Log.i(this.TAG, "查右后轮ID");
        byte[] frame = {-86, -79, -95, 7, 65, 3, -124};
        this.mencode.send(frame);
    }

    public void queryFrontLeft() {
        Log.i(this.TAG, "查前左轮ID");
        byte[] frame = {-86, -79, -95, 7, 65, 1, -124};
        this.mencode.send(frame);
    }

    public void queryFrontRight() {
        Log.i(this.TAG, "查前右ID");
        byte[] frame = {-86, -79, -95, 7, 65, 2, -124};
        this.mencode.send(frame);
    }

    public void paireBackLeft() {
        Log.i(this.TAG, "配对左后轮ID");
        byte[] frame = {-86, -79, -95, 7, 97, 0, -124};
        this.mencode.send(frame);
    }

    public void paireBackRight() {
        Log.i(this.TAG, "配对右后轮ID");
        byte[] frame = {-86, -79, -95, 7, 97, 3, -124};
        this.mencode.send(frame);
    }

    public void paireFrontLeft() {
        Log.i(this.TAG, "配对前左轮ID");
        byte[] frame = {-86, -79, -95, 7, 97, 1, -124};
        this.mencode.send(frame);
    }

    public void paireFrontRight() {
        Log.i(this.TAG, "配对前右ID");
        byte[] frame = {-86, -79, -95, 7, 97, 2, -124};
        this.mencode.send(frame);
    }

    public void paireSpTired() {
    }

    public void stopPaire() {
        Log.i(this.TAG, "stopPaire");
    }

    public TiresState getFrontLeftState() {
        return this.mFrontLeft;
    }

    public TiresState getFrontRightState() {
        return this.mFrontRight;
    }

    public TiresState getBackLeftState() {
        return this.mBackLeft;
    }

    public TiresState getBackRightState() {
        return this.mBackRight;
    }

    public TiresState getSpareTire() {
        return this.mSpareTire;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(AlarmAgrs args) {
        this.mAlarmAgrs = args;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PaireIDOkEvent id) {
        if (id.tires == 1) {
            this.mFrontLeft.TiresID = id.mID;
        } else if (id.tires == 2) {
            this.mFrontRight.TiresID = id.mID;
        } else if (id.tires == 3) {
            this.mBackRight.TiresID = id.mID;
        } else if (id.tires == 0) {
            this.mBackLeft.TiresID = id.mID;
        } else if (id.tires == 5) {
            this.mSpareTire.TiresID = id.mID;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(QueryIDOkEvent id) {
        if (id.tires == 1) {
            this.mFrontLeft.TiresID = id.mID;
        } else if (id.tires == 2) {
            this.mFrontRight.TiresID = id.mID;
        } else if (id.tires == 3) {
            this.mBackRight.TiresID = id.mID;
        } else if (id.tires == 0) {
            this.mBackLeft.TiresID = id.mID;
        } else if (id.tires == 5) {
            this.mSpareTire.TiresID = id.mID;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
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
        }
        String title = "您的";
        if (alarm.tires == 1) {
            this.mFrontLeft = alarm.mState;
            title = title + "前左轮";
        } else if (alarm.tires == 2) {
            this.mFrontRight = alarm.mState;
            title = title + "前右轮";
        } else if (alarm.tires == 3) {
            this.mBackRight = alarm.mState;
            title = title + "后右轮";
        } else if (alarm.tires == 0) {
            this.mBackLeft = alarm.mState;
            title = title + "后左轮";
        } else if (alarm.tires == 5) {
            this.mSpareTire = alarm.mState;
            title = title + "备胎";
        }
        if (alarm.mState.error.isEmpty() || getZhuDongBaojin() == 0) {
            return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queryAllState() {
        byte[] frame = {-86, -79, -95, 7, 113, 0, 4};
        this.mencode.send(frame);
        byte[] frame1 = {-86, -79, -95, 7, 113, 1, 4};
        this.mencode.send(frame1);
        byte[] frame2 = {-86, -79, -95, 7, 113, 2, 4};
        this.mencode.send(frame2);
        byte[] frame3 = {-86, -79, -95, 7, 113, 3, 4};
        this.mencode.send(frame3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ShakeHands sh) {
        if (sh.mShakeHandOK == 0) {
            queryBackLeft();
            queryBackRight();
            queryConfig();
            queryFrontLeft();
            queryFrontRight();
            new Handler().postDelayed(new Runnable() { // from class: com.tpms.biz.Tpms.3
                @Override // java.lang.Runnable
                public void run() {
                    Tpms.this.queryAllState();
                }
            }, 300L);
            return;
        }
        new Handler().postDelayed(new Runnable() { // from class: com.tpms.biz.Tpms.4
            @Override // java.lang.Runnable
            public void run() {
                Tpms.this.shakeHand();
            }
        }, 3000L);
    }

    public void setShowUiEnable(boolean enable) {
        this.mPreferences.edit().putBoolean("ShowUiEnable", enable).commit();
    }

    public boolean getShowUiEnable() {
        return this.mPreferences.getBoolean("ShowUiEnable", true);
    }

    public boolean getSoundWarringEnable() {
        boolean bret = this.mPreferences.getBoolean("SoundWarringEnable", true);
        String str = this.TAG;
        Log.i(str, "getSoundWarringEnable:" + bret);
        return bret;
    }

    public void setSparetireEnable(boolean enable) {
        this.mPreferences.edit().putBoolean("SparetireEnable", enable).commit();
    }

    public boolean getBettaWarringEnable() {
        return this.mPreferences.getBoolean("BettaWarringEnable", true);
    }

    public boolean getConnectWarringEnable() {
        return this.mPreferences.getBoolean("ConnectWarringEnable", true);
    }

    public boolean getSparetireEnable() {
        return this.mPreferences.getBoolean("SparetireEnable", false);
    }

    protected void HeartbeatEventAck() {
    }

    public void querySensorID() {
    }

    public int setHiTempDef() {
        return 0;
    }

    public int setHiPressDef() {
        return 0;
    }

    public int setLowPressDef() {
        return 0;
    }

    public int getHiPress() {
        return this.mPreferences.getInt("mHiPressStamp", 310);
    }

    public int getLowPress() {
        return this.mPreferences.getInt("mLowPressStamp", 180);
    }

    public String getPressString(int press) {
        String str = getYaliDanwei();
        DecimalFormat df = new DecimalFormat("######0.00");
        if (str.equals("Kpa")) {
            return "" + press;
        } else if (str.equals("Bar")) {
            return df.format(press / 100.0f);
        } else {
            if (str.equals("Psi")) {
                DecimalFormat df2 = new DecimalFormat("######0.0");
                return df2.format(press / 6.895f);
            }
            return "error";
        }
    }

    public String getTempString(int temp) {
        String str = getWenduDanwei();
        DecimalFormat df = new DecimalFormat("######0.00");
        if (str.equals("℃")) {
            return "" + temp;
        }
        double d = temp;
        Double.isNaN(d);
        return df.format((d * 1.8d) + 32.0d);
    }

    public int addHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值add");
        return this.mHiPressStamp;
    }

    public int decHiPressStamp() {
        Log.i(this.TAG, "设置最高高压阀值dec");
        return this.mHiPressStamp;
    }

    public int addLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值add");
        return this.mLowPressStamp;
    }

    public int decLowPressStamp() {
        Log.i(this.TAG, "设置最低低压度阀值dec");
        return this.mLowPressStamp;
    }

    public void exchangeLeftFrontRightFront() {
    }

    public void exchangeLeftFrontLeftBack() {
    }

    public void exchangeLeftFrontRightBack() {
    }

    public void exchangeRightFrontLeftBack() {
    }

    public void exchangeRightFrontRightBack() {
    }

    public void exchangeLeftBackRightBack() {
    }

    public void exchange_sp_fl() {
    }

    public void exchange_sp_fr() {
    }

    public void exchange_sp_bl() {
    }

    public void exchange_sp_br() {
    }

    public void setForeground(boolean bForeground) {
        this.mbForeground = bForeground;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isRunningForeground() {
        return this.mbForeground;
    }

    public void resetAll() {
        this.mPreferences.edit().clear().commit();
        clearAlarmCntrol();
        this.mHiTempStamp = 75;
        this.mHiPressStamp = 310;
        this.mLowPressStamp = 180;
        Log.i(this.TAG, "reset tpms");
        this.mencode.reset_dev();
        Util.Sleep(100L);
        this.mencode.reset_dev();
    }

    public boolean isAllOk() {
        return isokTires(this.mFrontLeft.mAlarmCntrols) && isokTires(this.mFrontRight.mAlarmCntrols) && isokTires(this.mBackLeft.mAlarmCntrols) && isokTires(this.mBackRight.mAlarmCntrols) && isokTires(this.mSpareTire.mAlarmCntrols);
    }

    private boolean isokTires(Map<String, AlarmCntrol> tiresTate) {
        for (AlarmCntrol ac : tiresTate.values()) {
            if (!TextUtils.isEmpty(ac.mError)) {
                return false;
            }
            String str = this.TAG;
            Log.i(str, "isokTires ? ac.mError:" + ac.mError);
        }
        return true;
    }

    public boolean isDevCheckOk() {
        String str = this.TAG;
        Log.i(str, "isDevCheckOk:" + this.mIsSeedAckOk);
        return this.mIsSeedAckOk;
    }
}