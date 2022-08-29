package com.difz.tpmsdemo.newcopy.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.difz.tpmsdemo.R;

/* loaded from: classes.dex */
public class SoundPoolCtrl2 extends SoundPoolCtrl {
    String TAG = "SoundPoolCtrl2";
    MediaPlayer mediaPlayer;

    public SoundPoolCtrl2(Context cont) {
        super(cont);
        if (this.mediaPlayer == null) {
            this.mediaPlayer = MediaPlayer.create(cont, (int) R.raw.alarm);
        }
    }

    @Override // com.tpms.utils.SoundPoolCtrl
    public void player(String guid) {
        Log.i(this.TAG, "player isPlayer:" + this.isPlayer + ";guid:" + guid);
        if (this.isPlayer) {
            playerCount++;
            if (playerCount % 10 == 0) {
                stopPlayer();
                startPlayer();
                return;
            }
            return;
        }
        startPlayer();
        this.mGuid = guid;
        this.isPlayer = true;
    }

    private void startPlayer() {
        this.mediaPlayer.start();
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.tpms.utils.SoundPoolCtrl2.1
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer arg0) {
                if (!SoundPoolCtrl2.this.isPlayer) {
                    Log.i(SoundPoolCtrl2.this.TAG, "is over");
                    return;
                }
                SoundPoolCtrl2.this.mediaPlayer.start();
                SoundPoolCtrl2.this.mediaPlayer.setLooping(true);
            }
        });
        try {
            Intent inte = new Intent("com.ts.audio.tpms.open");
            this.mcont.sendBroadcast(inte);
            this.mAudioM.requestAudioFocus(this.afChangeListener, 3, 2);
            Log.i(this.TAG, "requestAudioFocus");
        } catch (Exception e) {
            String str = this.TAG;
            Log.i(str, "requestAudioFocus :" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override // com.tpms.utils.SoundPoolCtrl
    public void stop(String guid) {
        String str = this.TAG;
        Log.i(str, "stop isPlayer:" + this.isPlayer + ";guid:" + guid);
        if (!this.isPlayer) {
            return;
        }
        if (TextUtils.isEmpty(guid) || guid.equals(this.mGuid)) {
            stopPlayer();
            this.mGuid = "";
        }
    }

    private void stopPlayer() {
        this.isPlayer = false;
        try {
            this.mediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent inte = new Intent("com.ts.audio.tpms.close");
            this.mcont.sendBroadcast(inte);
            this.mAudioM.abandonAudioFocus(this.afChangeListener);
            Log.i(this.TAG, "abandonAudioFocus");
        } catch (Exception e2) {
            e2.printStackTrace();
            String str = this.TAG;
            Log.i(str, "abandonAudioFocus :" + e2.getMessage());
        }
    }
}