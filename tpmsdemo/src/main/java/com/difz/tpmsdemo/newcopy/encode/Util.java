package com.difz.tpmsdemo.newcopy.encode;

/* loaded from: classes.dex */
public class Util {
    public static void Sleep(Long ms) {
        try {
            Thread.sleep(ms.longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}