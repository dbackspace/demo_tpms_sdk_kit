package com.difz.tpmsdemo.widget;


import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.difz.tpmsdemo.R;


public class ClickToast {
    private Context mContext;
    private WindowManager mWM;
    private View mView;
    private Button mTvAddress;
    private int mStartX;
    private int mStartY;
    private WindowManager.LayoutParams mParams;
    private boolean closeBtnEvent = false;

    private String guid = "";

    public String getGuid() {
        return guid;
    }

    public void setGuid(String id) {
        guid = id;
    }

    /**
     * 初始化toast
     *
     * @param context
     */
    public void initToast(Context context, View groupview, String txt) {
        mContext = context;
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.x = 0;
        mParams.y = 0;
        mParams.format = PixelFormat.TRANSLUCENT; // 显示效果
        // 如果设置为这个值:TYPE_SYSTEM_ALERT,那么
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//6.0
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            mParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //==mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        // |WindowManager.LayoutParams.FLAG_FULLSCREEN
        // |WindowManager.LayoutParams. FLAG_WATCH_OUTSIDE_TOUCH;
        // 不允许获得焦点,toast的通性
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // 不允许接收触摸事件
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mView = groupview;

        if (closeBtnEvent == true) {
            mView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideCustomToast();
                }
            });
        }
        TextView tv = (TextView) mView.findViewById(R.id.txt_view);
        tv.setText(txt);

        mView.setLayoutParams(mParams);
        // mView.setAnimation(R.animator.ani);

        mView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mParams.x;
                        paramY = mParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mParams.x = paramX + dx;
                        mParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        mWM.updateViewLayout(mView, mParams);
                        break;
                }
                return true;
            }
        });


    }

    public static ClickToast makeToast(Context cont, View view, String txt) {
        ClickToast toast = new ClickToast();
        toast.initToast(cont, view, txt);

        return toast;
    }

    public static ClickToast makeToast(Context cont, int layoutid, String txt) {
        ClickToast toast = new ClickToast();
        toast.closeBtnEvent = true;
        View view = LayoutInflater.from(cont).inflate(layoutid, null);
        toast.initToast(cont, view, txt);

        return toast;
    }


    /**
     * 显示toast
     *
     * @param context
     * @param address
     */
//	public void showCustomToast(Context context, View gr,String txt) {
//		initToast(context,gr,txt);
//
//		mWM.addView(mView, mParams);
//	}
    public void show() {
        mWM.addView(mView, mParams);
    }

    /**
     * 隐藏toast
     */
    public void hideCustomToast() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mView = null;
        }
    }
}
