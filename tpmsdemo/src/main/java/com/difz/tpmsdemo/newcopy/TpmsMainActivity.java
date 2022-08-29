package com.difz.tpmsdemo.newcopy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.difz.tpmsdemo.R;
import com.difz.tpmsdemo.newcopy.biz.Tpms;
import com.difz.tpmsdemo.newcopy.modle.DeviceOpenEvent;
import com.difz.tpmsdemo.newcopy.modle.TiresState;
import com.difz.tpmsdemo.newcopy.modle.TiresStateEvent;
import com.difz.tpmsdemo.newcopy.modle.TpmsDevErrorEvent;
import com.difz.tpmsdemo.newcopy.stddev.TpmsDataSrc;
import com.difz.tpmsdemo.newcopy.utils.Log;
import com.difz.tpmsdemo.newcopy.widget.PAlertDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* loaded from: classes.dex */
public class TpmsMainActivity extends Activity {
    @ViewInject(R.id.back_left_betta)
    TextView back_left_betta;
    @ViewInject(R.id.back_left_connect)
    TextView back_left_connect;
    @ViewInject(R.id.back_left_error)
    TextView back_left_error;
    @ViewInject(R.id.back_left_infos)
    LinearLayout back_left_infos;
    @ViewInject(R.id.back_left_pressure)
    TextView back_left_pressure;
    @ViewInject(R.id.back_left_temp)
    TextView back_left_temp;
    @ViewInject(R.id.back_right_betta)
    TextView back_right_betta;
    @ViewInject(R.id.back_right_connect)
    TextView back_right_connect;
    @ViewInject(R.id.back_right_error)
    TextView back_right_error;
    @ViewInject(R.id.back_right_infos)
    LinearLayout back_right_infos;
    @ViewInject(R.id.back_right_pressure)
    TextView back_right_pressure;
    @ViewInject(R.id.back_right_temp)
    TextView back_right_temp;
    @ViewInject(R.id.front_left_betta)
    TextView front_left_betta;
    @ViewInject(R.id.front_left_connect)
    TextView front_left_connect;
    @ViewInject(R.id.front_left_error)
    TextView front_left_error;
    @ViewInject(R.id.front_left_infos)
    LinearLayout front_left_infos;
    @ViewInject(R.id.front_left_pressure)
    TextView front_left_pressure;
    @ViewInject(R.id.front_left_temp)
    TextView front_left_temp;
    @ViewInject(R.id.front_right_betta)
    TextView front_right_betta;
    @ViewInject(R.id.front_right_connect)
    TextView front_right_connect;
    @ViewInject(R.id.front_right_error)
    TextView front_right_error;
    @ViewInject(R.id.front_right_infos)
    LinearLayout front_right_infos;
    @ViewInject(R.id.front_right_pressure)
    TextView front_right_pressure;
    @ViewInject(R.id.front_right_temp)
    TextView front_right_temp;
    @ViewInject(R.id.ll_sptires_contioner)
    LinearLayout ll_sptires_contioner;
    TiresState mBackLeft;
    TiresState mBackRight;
    TiresState mFrontLeft;
    TiresState mFrontRight;
    AlertDialog mPDlg;
    TiresState mSpareTire;
    Handler mSyncHandler;
    Tpms mTpms;
    @ViewInject(R.id.tv_sptires_betta)
    TextView tv_sptires_betta;
    @ViewInject(R.id.tv_sptires_error)
    TextView tv_sptires_error;
    @ViewInject(R.id.tv_sptires_pressure)
    TextView tv_sptires_pressure;
    @ViewInject(R.id.tv_sptires_temp)
    TextView tv_sptires_temp;
    private String TAG = "TpmsMainActivity";
    TpmsDataSrc datasrc = null;
    TpmsApplication app = null;
    Runnable mSyncRunAble = new Runnable() { // from class: com.tpms.view.TpmsMainActivity.1
        @Override // java.lang.Runnable
        public void run() {
            if (TpmsMainActivity.this.mPDlg.isShowing()) {
                TpmsMainActivity.this.mPDlg.dismiss();
                TpmsMainActivity tpmsMainActivity = TpmsMainActivity.this;
                Toast.makeText(tpmsMainActivity, tpmsMainActivity.getString(R.string.xingxiduqushibai), Toast.LENGTH_LONG).show();
            }
        }
    };
    private final BroadcastReceiver filterReceiver = new BroadcastReceiver() { // from class: com.tpms.view.TpmsMainActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String reason;
            String action = intent.getAction();
            if (!"android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || (reason = intent.getStringExtra("reason")) == null) {
                return;
            }
            reason.equals("homekey");
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        String act;
        super.onCreate(savedInstanceState);
        Intent inte = getIntent();
        if (inte != null && (act = inte.getAction()) != null && act.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        this.app = (TpmsApplication) getApplication();
        this.app.startTpms();
        this.mTpms = this.app.getTpms();
        this.datasrc = this.app.getDataSrc();
        this.mPDlg = PAlertDialog.showDiolg(this, getString(R.string.zhengzaiduquzhong));
        this.mSyncHandler = new Handler();
        this.mSyncHandler.postDelayed(this.mSyncRunAble, 14000L);
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            startActivity(intent);
        }
        if (!this.mTpms.isDevCheckOk()) {
            return;
        }
        this.mFrontLeft = this.mTpms.getFrontLeftState();
        this.front_left_pressure.setText(getPressure(this.mFrontLeft.AirPressure));
        this.front_left_error.setText(this.mFrontLeft.error);
        this.front_left_temp.setText(getTempString(this.mFrontLeft.Temperature));
        this.mFrontRight = this.app.getTpms().getFrontRightState();
        this.front_right_pressure.setText(getPressure(this.mFrontRight.AirPressure));
        this.front_right_error.setText(this.mFrontRight.error);
        this.front_right_temp.setText(getTempString(this.mFrontRight.Temperature));
        this.mBackRight = this.app.getTpms().getBackRightState();
        this.back_right_pressure.setText(getPressure(this.mBackRight.AirPressure));
        this.back_right_error.setText(this.mBackRight.error);
        this.back_right_temp.setText(getTempString(this.mBackRight.Temperature));
        this.mBackLeft = this.app.getTpms().getBackLeftState();
        this.back_left_pressure.setText(getPressure(this.mBackLeft.AirPressure));
        this.back_left_error.setText(this.mBackLeft.error);
        this.back_left_temp.setText(getTempString(this.mBackLeft.Temperature));
        boolean spret = this.app.getTpms().getSparetireEnable();
        if (spret) {
            this.ll_sptires_contioner.setVisibility(View.VISIBLE);
        } else {
            this.ll_sptires_contioner.setVisibility(View.INVISIBLE);
        }
        this.mSpareTire = this.app.getTpms().getSpareTire();
        this.tv_sptires_pressure.setText(getPressure(this.mSpareTire.AirPressure));
        this.tv_sptires_temp.setText(getTempString(this.mSpareTire.Temperature));
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent inte) {
        String act;
        super.onNewIntent(inte);
        if (inte != null && (act = inte.getAction()) != null && act.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        this.mTpms.closeFloatWindow();
        super.onStart();
    }

    public void onClick(View v) {
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mTpms.setForeground(true);
        this.mTpms.closeFloatWindow();
        boolean spret = this.app.getTpms().getSparetireEnable();
        if (spret) {
            this.ll_sptires_contioner.setVisibility(View.VISIBLE);
        } else {
            this.ll_sptires_contioner.setVisibility(View.INVISIBLE);
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        this.mTpms.setForeground(false);
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mPDlg;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        Handler handler = this.mSyncHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mSyncRunAble);
        }
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TiresStateEvent alarm) {
        if (!this.mTpms.isDevCheckOk()) {
            return;
        }
        this.mPDlg.dismiss();
        Log.i(this.TAG, "收到胎压装态数据");
        if (alarm.tires == 1 && this.mFrontLeft != null) {
            alarm.mState.TiresID = this.mFrontLeft.TiresID;
        } else if (alarm.tires == 2 && this.mFrontRight != null) {
            alarm.mState.TiresID = this.mFrontRight.TiresID;
        } else if (alarm.tires == 3 && this.mBackRight != null) {
            alarm.mState.TiresID = this.mBackRight.TiresID;
        } else if (alarm.tires == 0 && this.mBackLeft != null) {
            alarm.mState.TiresID = this.mBackLeft.TiresID;
        }
        int i = alarm.tires;
        int i2 = R.drawable.connect_error_l;
        if (i == 1) {
            this.mFrontLeft = this.app.getTpms().getFrontLeftState();
            this.front_left_pressure.setText(getPressure(this.mFrontLeft.AirPressure));
            this.front_left_temp.setText(getTempString(this.mFrontLeft.Temperature));
            TextView textView = this.front_left_connect;
            if (!alarm.mState.NoSignal) {
                i2 = R.drawable.connect_ok_l;
            }
            textView.setBackgroundResource(i2);
            updateView(alarm, this.front_left_error, this.front_left_infos, this.front_left_betta);
            return;
        }
        int i3 = alarm.tires;
        int i4 = R.drawable.connect_error_r;
        if (i3 == 2) {
            this.mFrontRight = this.app.getTpms().getFrontRightState();
            this.front_right_pressure.setText(getPressure(this.mFrontRight.AirPressure));
            this.front_right_temp.setText(getTempString(this.mFrontRight.Temperature));
            TextView textView2 = this.front_right_connect;
            if (!alarm.mState.NoSignal) {
                i4 = R.drawable.connect_ok_r;
            }
            textView2.setBackgroundResource(i4);
            updateView(alarm, this.front_right_error, this.front_right_infos, this.front_right_betta);
        } else if (alarm.tires == 3) {
            this.mBackRight = this.app.getTpms().getBackRightState();
            this.back_right_pressure.setText(getPressure(this.mBackRight.AirPressure));
            this.back_right_temp.setText(getTempString(this.mBackRight.Temperature));
            TextView textView3 = this.back_right_connect;
            if (!alarm.mState.NoSignal) {
                i4 = R.drawable.connect_ok_r;
            }
            textView3.setBackgroundResource(i4);
            updateView(alarm, this.back_right_error, this.back_right_infos, this.back_right_betta);
        } else if (alarm.tires == 0) {
            this.mBackLeft = this.app.getTpms().getBackLeftState();
            this.back_left_pressure.setText(getPressure(this.mBackLeft.AirPressure));
            this.back_left_temp.setText(getTempString(this.mBackLeft.Temperature));
            TextView textView4 = this.back_left_connect;
            if (!alarm.mState.NoSignal) {
                i2 = R.drawable.connect_ok_l;
            }
            textView4.setBackgroundResource(i2);
            updateView(alarm, this.back_left_error, this.back_left_infos, this.back_left_betta);
        } else if (alarm.tires == 5) {
            this.mSpareTire = this.app.getTpms().getSpareTire();
            this.tv_sptires_pressure.setText(getPressure(alarm.mState.AirPressure));
            this.tv_sptires_temp.setText(getTempString(alarm.mState.Temperature));
            updateView(alarm, this.tv_sptires_error, this.ll_sptires_contioner, this.tv_sptires_betta);
        }
    }

    private void updateView(TiresStateEvent tevent, TextView nerror, LinearLayout infos, TextView betta) {
        String title = "";
        TiresState state = tevent.mState;
        String errorKey = "";
        if (state.NoSignal) {
            title = title + getString(R.string.lianjieyichang);
            errorKey = "NoSignal";
        } else if (state.Leakage) {
            title = title + getString(R.string.louqizhong);
            errorKey = "Leakage";
        } else if (state.AirPressure > this.mTpms.getHiPress()) {
            title = title + getString(R.string.taiyaguogao);
            errorKey = "mHiPressStamp";
        } else if (state.AirPressure < this.mTpms.getLowPress()) {
            title = title + getString(R.string.taiyaguodi);
            errorKey = "mLowPressStamp";
        } else if (state.Temperature > this.mTpms.getHiTemp()) {
            title = title + getString(R.string.wengduguogao);
            errorKey = "mHiTempStamp";
        } else if (state.LowPower) {
            title = title + getString(R.string.dianyaguodi);
            errorKey = "LowPower";
        }
        betta.setBackgroundResource(state.LowPower ? R.drawable.bettawaring : R.drawable.bettaok);
        if (TextUtils.isEmpty(errorKey)) {
            Log.i(this.TAG, "无告警");
            nerror.setText(R.string.taiyazhengchang);
            infos.getBackground().setLevel(1);
            return;
        }
        nerror.setText(title);
        infos.getBackground().setLevel(0);
    }

    @OnClick({R.id.btn_paire_id})
    public void btn_paire_id(View v) {
//        Intent inte = new Intent(this, PaireIDActivity.class);
//        startActivity(inte);
    }

    @OnClick({R.id.btn_tpms_set})
    public void btn_tpms_set(View v) {
//        Intent inte = new Intent(this, SetActivity.class);
//        startActivity(inte);
    }

    String getPressure(int val) {
        return this.mTpms.getPressString(val) + this.mTpms.getYaliDanwei();
    }

    String getTempString(int val) {
        return this.mTpms.getTempString(val) + this.mTpms.getWenduDanwei();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DeviceOpenEvent event) {
        boolean z = event.mOpen;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TpmsDevErrorEvent error) {
        finish();
    }
}