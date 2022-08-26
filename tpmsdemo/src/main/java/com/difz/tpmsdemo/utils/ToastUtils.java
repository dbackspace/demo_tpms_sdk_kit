package com.difz.tpmsdemo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast sToast;

    public static void toast(Context context, String message, boolean isToastLong) {
        if (sToast != null) sToast.cancel();
        sToast = Toast.makeText(context, message, isToastLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        sToast.show();
    }
}
