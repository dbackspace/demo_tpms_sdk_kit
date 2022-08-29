package com.difz.tpmsdemo.newcopy.stddev;

import java.io.FileDescriptor;

/* loaded from: classes.dex */
public class OsWrap extends BaseWrap {
    private static String mPackName = "android.system.Os";

    public static void close(FileDescriptor fd) {
        try {
            Class[] paramsClass = {FileDescriptor.class};
            runRelMethod(mPackName, null, paramsClass, fd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}