package com.difz.tpmsdemo.tyreusb.modle;

public class HeartbeatEvent {
    public int mShakeHandOK;

    public HeartbeatEvent(int state) {
        this.mShakeHandOK = 0;
        this.mShakeHandOK = state;
    }
}