package com.difz.tpmsdemo.newcopy.utils;

import android.media.AudioTrack;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class AudioTrackPlayer {
    private byte[] audioData;
    private AudioTrack audioTrack;

    private void releaseAudioTrack() {
        AudioTrack audioTrack = this.audioTrack;
        if (audioTrack != null) {
            audioTrack.stop();
            this.audioTrack.release();
            this.audioTrack = null;
        }
    }

    public AudioTrackPlayer() {
        releaseAudioTrack();
        int min = AudioTrack.getMinBufferSize(44100, 12, 2);
        this.audioTrack = new AudioTrack(3, 44100, 12, 2, min, 0);
    }

    public boolean isPlaying() {
        return this.audioTrack.getPlayState() == 3;
    }

    public void load(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(264848);
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                out.write(b);
            }
            this.audioData = out.toByteArray();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioTrack audioTrack = this.audioTrack;
        byte[] bArr = this.audioData;
        audioTrack.write(bArr, 0, bArr.length);
    }

    public void start() {
        AudioTrack audioTrack = this.audioTrack;
        byte[] bArr = this.audioData;
        audioTrack.write(bArr, 0, bArr.length);
        this.audioTrack.play();
    }

    public void pause() {
        this.audioTrack.pause();
    }
}