package com.difz.tpmsdemo.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.difz.tpmsdemo.R;
import com.tpms.utils.Log;


public class PAlertDialog {

    private static String TAG = "PAlertDialog";

    public static AlertDialog showDiolg(Context context, String txt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_dialog, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_txt_wait);
        if (tv != null) {
            if (!TextUtils.isEmpty(txt)) {
                tv.setText(txt);
            }
        }

        Builder builder = new Builder(context);
        builder.setView(view);
        builder.create();
        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                Log.i(TAG, "onCancel");
            }
        });
        AlertDialog dlg = builder.show();

        return dlg;
    }

    public static ProgressDialog showProgress(Context context, String str1,
                                              String str2) {
        return ProgressDialog.show(context, str1, str2, true, false);
    }

}
