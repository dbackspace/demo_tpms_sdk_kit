package com.difz.tpmsdemo.newcopy.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.difz.tpmsdemo.R;

/* loaded from: classes.dex */
public class SoundPoolCtrl {
    protected static int playerCount = 0;
    AudioManager mAudioM;
    protected Context mcont;
    private int playerId = 0;
    String TAG = "SoundPoolCtrl";
    boolean isPlayer = false;
    String mGuid = "";
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.tpms.utils.SoundPoolCtrl.1
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == -2) {
                Log.i(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == -3) {
                String str = SoundPoolCtrl.this.TAG;
                Log.d(str, "有应用申请了短焦点 我压低声音  AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:" + focusChange);
            } else if (focusChange == 1) {
                Log.d(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_GAIN");
            } else if (focusChange == -1) {
                Log.d(SoundPoolCtrl.this.TAG, "AUDIOFOCUS_LOSS");
            } else if (focusChange == 1) {
                Log.d(SoundPoolCtrl.this.TAG, "永久获取媒体焦点（播放音乐）现在没有播放 AUDIOFOCUS_REQUEST_GRANTED");
            } else {
                String str2 = SoundPoolCtrl.this.TAG;
                Log.i(str2, "focusChange:" + focusChange);
            }
        }
    };
    private SoundPool soundPool = new SoundPool(10, 3, 100);

    public SoundPoolCtrl(Context cont) {
        this.mAudioM = null;
        this.mcont = cont;
        this.soundPool.load(cont, R.raw.alarm, 1);
        this.mAudioM = (AudioManager) cont.getSystemService("audio");
    }

    public void player(String guid) {
        String str = this.TAG;
        Log.i(str, "player isPlayer:" + this.isPlayer + ";guid:" + guid);
        if (this.isPlayer) {
            return;
        }
        this.mAudioM.requestAudioFocus(this.afChangeListener, 3, 2);
        this.playerId = this.soundPool.play(1, 15.0f, 15.0f, 1, -1, 1.0f);
        this.mGuid = guid;
        this.isPlayer = true;
    }

    public String getSoundGuid() {
        return this.mGuid;
    }

    public void stop(String guid) {
        String str = this.TAG;
        Log.i(str, "stop isPlayer:" + this.isPlayer + ";guid:" + guid);
        if (!this.isPlayer) {
            return;
        }
        if (TextUtils.isEmpty(guid) || guid.equals(this.mGuid)) {
            try {
                this.soundPool.stop(this.playerId);
                this.playerId = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.mAudioM.abandonAudioFocus(this.afChangeListener);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.isPlayer = false;
            this.mGuid = "";
        }
    }
}