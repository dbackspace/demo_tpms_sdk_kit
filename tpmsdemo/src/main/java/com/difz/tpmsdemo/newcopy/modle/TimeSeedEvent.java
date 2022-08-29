package com.difz.tpmsdemo.newcopy.modle;

public class TimeSeedEvent {
    public int mSeedAck;

    public TimeSeedEvent(int state) {
        this.mSeedAck = 0;
        this.mSeedAck = state;
    }
}