package com.difz.tpmsdemo.tyreusb.modle;

public class TpmsDevErrorEvent {
    public int mErrorCode;

    public TpmsDevErrorEvent(int state) {
        this.mErrorCode = 0;
        this.mErrorCode = state;
    }
}