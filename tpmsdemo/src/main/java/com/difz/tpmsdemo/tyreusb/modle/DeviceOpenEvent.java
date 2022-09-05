package com.difz.tpmsdemo.tyreusb.modle;

public class DeviceOpenEvent {
    public boolean mOpen;

    public DeviceOpenEvent(boolean state) {
        this.mOpen = false;
        this.mOpen = state;
    }
}