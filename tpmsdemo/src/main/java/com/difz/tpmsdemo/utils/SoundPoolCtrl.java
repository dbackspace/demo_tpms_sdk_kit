package com.difz.tpmsdemo.utils;


import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.SoundPool;
import android.text.TextUtils;
import com.tpms.utils.Log;

import com.difz.tpmsdemo.BuildConfig;
import com.difz.tpmsdemo.R;



public class SoundPoolCtrl {
    private SoundPool soundPool;
    private int playerId = 0;
    AudioManager mAudioM = null;
    String TAG = "SoundPoolCtrl";
    boolean isPlayer = false;

    String mGuid = "";

    protected Context mcont;

    protected static int playerCount = 0;

    public SoundPoolCtrl(Context cont) {
        mcont = cont;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        soundPool.load(cont, R.raw.alarm, 1);
        mAudioM = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
    }

    public void player(String guid) {
        Log.i(TAG, "player isPlayer:" + isPlayer + ";guid:" + guid);
        if (isPlayer) return;


        if (BuildConfig.ENABLE_FOUCES) {
            mAudioM.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        playerId = soundPool.play(1, 15, 15, 1, -1, 1);
//		if(playerId==0)
//		{
//			isPlayer = false;
//		}else
        {
            mGuid = guid;
            isPlayer = true;
        }
    }

    public String getSoundGuid() {
        return mGuid;
    }

    //guid 为空，是强制停声音
    public void stop(String guid) {

        Log.i(TAG, "stop isPlayer:" + isPlayer + ";guid:" + guid);
        if (isPlayer == false) return;

        if (TextUtils.isEmpty(guid) || guid.equals(mGuid)) {
            try {
                //if(playerId!=0)
                {
                    soundPool.stop(playerId);
                }

                playerId = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (BuildConfig.ENABLE_FOUCES) {
                try {
                    mAudioM.abandonAudioFocus(afChangeListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            isPlayer = false;
            mGuid = "";
        }

    }

    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            /**
             * AUDIOFOCUS_GAIN：获得音频焦点。
             * AUDIOFOCUS_LOSS：失去音频焦点，并且会持续很长时间。这是我们需要停止MediaPlayer的播放。
             * AUDIOFOCUS_LOSS_TRANSIENT
             * ：失去音频焦点，但并不会持续很长时间，需要暂停MediaPlayer的播放，等待重新获得音频焦点。
             *
             * AUDIOFOCUS_REQUEST_GRANTED 永久获取媒体焦点（播放音乐）
             * AUDIOFOCUS_GAIN_TRANSIENT 暂时获取焦点 适用于短暂的音频
             * AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK Duck我们应用跟其他应用共用焦点
             * 我们播放的时候其他音频会降低音量
             */
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {//语音识别
                Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {


                // 降低音量

                Log.d(TAG, "有应用申请了短焦点 我压低声音  AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:" + focusChange);

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {//取到了焦点


                // 恢复至正常音量
                Log.d(TAG, "AUDIOFOCUS_GAIN");


            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {//失去焦点
                Log.d(TAG, "AUDIOFOCUS_LOSS");

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d(TAG, "永久获取媒体焦点（播放音乐）现在没有播放 AUDIOFOCUS_REQUEST_GRANTED");


            } else {

                Log.i(TAG, "focusChange:" + focusChange);
            }
        }
    };
}
