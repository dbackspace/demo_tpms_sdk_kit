package com.difz.tpmsdemo.newcopy.stddev;

import com.difz.tpmsdemo.newcopy.utils.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

/* loaded from: classes.dex */
public class BaseWrap {
    private static final String TAG = "BaseWrap";

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T> T runRelMethod(String packageName, Object instance, Class[] paramsClass, Object... params) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[3];
        String methodName = e.getMethodName();
        try {
            Class cls = Class.forName(packageName);
            Log.d(TAG, "class name is : " + cls.getName());
            if (cls != null) {
                new HashMap();
                Method method = cls.getMethod(methodName, paramsClass);
                if (method != null) {
                    T tmp = (T) method.invoke(instance, params);
                    return tmp;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}