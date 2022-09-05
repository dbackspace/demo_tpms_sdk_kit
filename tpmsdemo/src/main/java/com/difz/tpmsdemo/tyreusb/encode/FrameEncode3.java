package com.difz.tpmsdemo.tyreusb.encode;

import android.content.Context;

import com.difz.tpmsdemo.tyreusb.TpmsApplication;
import com.difz.tpmsdemo.tyreusb.utils.Log;

import java.util.HashMap;

/* loaded from: classes.dex */
public class FrameEncode3 extends FrameEncode {
    private static final HashMap<String, String> activityMap = new HashMap<>();
    String mOldHintTxt = "";
    PackBufferFrameEn3 FrameEn = null;
    private String TAG = "FrameEncode3";

    public FrameEncode3(TpmsApplication _app) {
        super(_app);
    }

    @Override // com.tpms.encode.FrameEncode
    public void init(Context ctx) {
        super.init(ctx);
        this.FrameEn = new PackBufferFrameEn3(this.theApp);
    }

    @Override // com.tpms.encode.FrameEncode
    protected void send(String content) {
    }

    @Override // com.tpms.encode.FrameEncode
    public void send(byte[] frame) {
        this.FrameEn.send(frame);
        sleep(60L);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.tpms.encode.FrameEncode
    public void HeartbeatEventAck() {
        byte[] frame = {85, -86, 6, 0, 0, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void SendHeartbeat() {
        byte[] frame = {85, -86, 6, 25, 0, -32};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void SendEncryption(byte seed) {
        byte[] frame = {85, -86, 6, 91, seed, -32};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void paireBackLeft() {
        Log.i(this.TAG, "配对后左轮ID");
        byte[] frame = {85, -86, 6, 1, 16, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void paireBackRight() {
        Log.i(this.TAG, "配对右后轮ID");
        byte[] frame = {85, -86, 6, 1, 17, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void paireFrontLeft() {
        Log.i(this.TAG, "配对前左轮ID");
        byte[] frame = {85, -86, 6, 1, 0, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void paireFrontRight() {
        Log.i(this.TAG, "配对前右ID");
        byte[] frame = {85, -86, 6, 1, 1, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void paireSpTired() {
        Log.i(this.TAG, "配对备胎ID");
        byte[] frame = {85, -86, 6, 1, 5, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void querySensorID() {
        Log.i(this.TAG, "querySensorID");
        byte[] frame = {85, -86, 6, 7, 0, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void stopPaire() {
        Log.i(this.TAG, "stopPaire");
        byte[] frame = {85, -86, 6, 6, 0, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeLeftFrontRightFront() {
        Log.i(this.TAG, "exchangeLeftFrontRightFront");
        byte[] frame = {85, -86, 7, 3, 0, 1, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeLeftFrontLeftBack() {
        Log.i(this.TAG, "exchangeLeftFrontLeftBack");
        byte[] frame = {85, -86, 7, 3, 0, 16, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeLeftFrontRightBack() {
        Log.i(this.TAG, "exchangeLeftFrontRightBack");
        byte[] frame = {85, -86, 7, 3, 0, 17, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeRightFrontLeftBack() {
        Log.i(this.TAG, "exchangeRightFrontLeftBack");
        byte[] frame = {85, -86, 7, 3, 1, 16, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeRightFrontRightBack() {
        Log.i(this.TAG, "exchangeRightFrontRightBack");
        byte[] frame = {85, -86, 7, 3, 1, 17, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchangeLeftBackRightBack() {
        Log.i(this.TAG, "exchangeLeftBackRightBack");
        byte[] frame = {85, -86, 7, 3, 16, 17, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchange_sp_fl() {
        byte[] frame = {85, -86, 7, 3, 0, 5, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchange_sp_fr() {
        byte[] frame = {85, -86, 7, 3, 1, 5, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchange_sp_bl() {
        byte[] frame = {85, -86, 7, 3, 16, 5, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void exchange_sp_br() {
        byte[] frame = {85, -86, 7, 3, 17, 5, 0};
        this.FrameEn.send(frame);
    }

    @Override // com.tpms.encode.FrameEncode
    public void reset_dev() {
        byte[] frame = {85, -86, 6, 88, 85, -32};
        this.FrameEn.send(frame);
    }
}