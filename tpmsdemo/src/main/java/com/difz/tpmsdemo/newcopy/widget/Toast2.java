package com.difz.tpmsdemo.newcopy.widget;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/* loaded from: classes.dex */
public abstract class Toast2 {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private static Handler handler = new Handler();
    private static Runnable run = new Runnable() { // from class: com.tpms.widget.Toast2.1
        @Override // java.lang.Runnable
        public void run() {
            Toast2.toast.cancel();
        }
    };
    private static Toast toast;

    private static void toast(Context ctx, CharSequence msg, int duration) {
        handler.removeCallbacks(run);
        if (duration == 0) {
            duration = 1000;
        } else if (duration == 1) {
            duration = 3000;
        }
        Toast toast2 = toast;
        if (toast2 != null) {
            toast2.setText(msg);
        } else {
            toast = Toast.makeText(ctx, msg, duration);
        }
        handler.postDelayed(run, duration);
        toast.show();
    }

    public static void show(Context ctx, CharSequence msg, int duration) throws NullPointerException {
        if (ctx == null) {
            throw new NullPointerException("The ctx is null!");
        }
        if (duration < 0) {
            duration = 0;
        }
        toast(ctx, msg, duration);
    }

    public static void show(Context ctx, int resId, int duration) throws NullPointerException {
        if (ctx == null) {
            throw new NullPointerException("The ctx is null!");
        }
        if (duration < 0) {
            duration = 0;
        }
        toast(ctx, ctx.getResources().getString(resId), duration);
    }
}