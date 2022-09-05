package com.difz.tpmsdemo.tyreusb.encode;

import com.difz.tpmsdemo.tyreusb.TpmsApplication;
import com.difz.tpmsdemo.tyreusb.utils.Log;
import com.difz.tpmsdemo.tyreusb.utils.SLOG;

/* loaded from: classes.dex */
public class PackBufferFrameEn3 extends PackBufferFrameEn {
    String TAG = PackBufferFrameEn3.class.getSimpleName();

    public PackBufferFrameEn3(TpmsApplication app) {
        super(app);
    }

    @Override // com.tpms.encode.PackBufferFrameEn
    protected byte calcCC(byte[] buf) {
        int datalen = buf[2];
        byte calc = buf[0];
        for (int i = 1; i < datalen - 1; i++) {
            calc = (byte) (buf[i] ^ calc);
        }
        byte cc = calc;
        String str = this.TAG;
        Log.i(str, "cc:" + SLOG.byteToHexString(cc));
        return cc;
    }
}