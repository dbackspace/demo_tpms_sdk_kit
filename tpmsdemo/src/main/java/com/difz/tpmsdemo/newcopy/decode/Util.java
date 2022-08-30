package com.difz.tpmsdemo.newcopy.decode;

/* loaded from: classes.dex */
public class Util {
    public static final String byteToUpperString(byte bt) {
        String sTemp = Integer.toHexString(bt & 255);
        if (sTemp.length() == 1) {
            sTemp = 0 + sTemp;
        }
        return sTemp.toUpperCase();
    }
}