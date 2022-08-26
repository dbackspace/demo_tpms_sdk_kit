package com.difz.tpmsdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.difz.tpmsdemo.R;


/**
 * UnbindDialog
 * <p>
 * 创建时间：2015-12-4上午11:29:32
 *
 * @version 1.0.0
 */
public class CDialog extends Dialog {
    LayoutInflater inflater;
    View mView;

    public CDialog(Context context, View view) {
        super(context, R.style.DialogStyle);
        inflater = LayoutInflater.from(context);

        initView(view);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private void initView(View view) {
        if (view == null) return;

        mView = view;
        View btn = mView.findViewById(R.id.close_btn);
        this.setContentView(view);

        if (btn == null) return;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CDialog.this.dismiss();
            }
        });


    }


    public CDialog(Context context, int layid) {
        this(context, null);
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layid, null);
        initView(view);
    }

    public void show(String txt) {
        TextView tv = (TextView) mView.findViewById(R.id.txt_view);
        tv.setText(txt);
        super.show();
    }
}
