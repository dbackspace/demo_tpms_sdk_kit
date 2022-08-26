package com.difz.tpmsdemo.utils;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import com.tpms.utils.Log;

import com.difz.tpmsdemo.BuildConfig;
import com.difz.tpmsdemo.R;



public class SoundPoolCtrl2 extends SoundPoolCtrl {
    MediaPlayer mediaPlayer;
    String TAG = "difengze.com-SoundPoolCtrl2";

    public SoundPoolCtrl2(Context cont) {
        super(cont);

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(cont, R.raw.alarm);
        }
    }

    public void player(String guid) {
        Log.i(TAG, "player isPlayer:" + isPlayer + ";guid:" + guid);
        if (isPlayer)
        {
            playerCount++;
            if(playerCount % 10 == 0)
            {
                stopPlayer();
                startPlayer();
            }
            return;
        }

        startPlayer();

//		if(playerId==0)
//		{
//			isPlayer = false;
//		}else
        {
            mGuid = guid;
            isPlayer = true;
        }
    }

    private void startPlayer()
    {
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {


                //Log.i(TAG, "onCompletion");
                if (isPlayer == false) {
                    Log.i(TAG, "is over");
                    return;
                }
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });

        try{
            if (BuildConfig.ENABLE_FOUCES) {

                Intent inte = new Intent("com.ts.audio.tpms.open");
                mcont.sendBroadcast(inte);
                mAudioM.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                Log.i(TAG, "requestAudioFocus");
            }
        }catch (Exception e)
        {
            Log.i(TAG, "requestAudioFocus :"+e.getMessage());
            e.printStackTrace();
        }
    }


    //guid 为空，是强制停声音
    public void stop(String guid) {

        Log.i(TAG, "stop isPlayer:" + isPlayer + ";guid:" + guid);
        if (isPlayer == false) return;

        if (TextUtils.isEmpty(guid) || guid.equals(mGuid)) {

            stopPlayer();

            mGuid = "";
        }

    }

    private void stopPlayer()
    {
        isPlayer = false;
        try {
            //if(playerId!=0)
            {
                mediaPlayer.pause();
                //mediaPlayer.stop();//不能单独调这个
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BuildConfig.ENABLE_FOUCES) {
            try {
                Intent inte = new Intent("com.ts.audio.tpms.close");
                mcont.sendBroadcast(inte);
                mAudioM.abandonAudioFocus(afChangeListener);
                Log.i(TAG, "abandonAudioFocus");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "abandonAudioFocus :"+e.getMessage());
            }
        }
    }


}
