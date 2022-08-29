package com.difz.tpmsdemo.newcopy.widget;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.difz.tpmsdemo.R;

/* loaded from: classes.dex */
public class CDialog2 {
    private boolean closeBtnEvent = false;
    private Context mContext;
    private WindowManager.LayoutParams mParams;
    private int mStartX;
    private int mStartY;
    private Button mTvAddress;
    private View mView;
    private WindowManager mWM;

    public void initToast(Context context, View groupview, String txt) {
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService("window");
        this.mParams = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.mParams;
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.format = -3;
        if (Build.VERSION.SDK_INT >= 23) {
            this.mParams.type = 2038;
        } else {
            this.mParams.type = 2003;
        }
        this.mParams.flags = 136;
        this.mView = groupview;
        if (this.closeBtnEvent) {
            this.mView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() { // from class: com.tpms.widget.CDialog2.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    CDialog2.this.hideCustomToast();
                }
            });
        }
        this.mView.setLayoutParams(this.mParams);
    }

    public static CDialog2 makeToast(Context cont, View view, String txt) {
        CDialog2 toast = new CDialog2();
        toast.initToast(cont, view, txt);
        return toast;
    }

    public static CDialog2 makeToast(Context cont, int layoutid, String txt) {
        CDialog2 toast = new CDialog2();
        toast.closeBtnEvent = true;
        View view = LayoutInflater.from(cont).inflate(layoutid, (ViewGroup) null);
        toast.initToast(cont, view, txt);
        return toast;
    }

    public void show() {
        this.mWM.addView(this.mView, this.mParams);
    }

    public void hideCustomToast() {
        View view = this.mView;
        if (view != null) {
            if (view.getParent() != null) {
                this.mWM.removeView(this.mView);
            }
            this.mView = null;
        }
    }
}