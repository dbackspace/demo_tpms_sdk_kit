package com.difz.tpmsdemo.tyreusb.modle;

public class TimeSeedEvent {
    public int mSeedAck;

    public TimeSeedEvent(int state) {
        this.mSeedAck = 0;
        this.mSeedAck = state;
    }
}