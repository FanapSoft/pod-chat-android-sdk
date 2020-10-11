package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static android.content.Context.AUDIO_SERVICE;

public class AudioTestClass {


    private static final int bufferSize = 2048 * 2;
    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String TAG  = "CAG";
    private boolean running = true;

    public void start(Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        audioManager.setSpeakerphoneOn(true);

        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

        int b1 = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        int b2 = AudioTrack.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        AudioRecord recorder = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO,
                ENCODING, bufferSize);


        setConfigs(recorder.getAudioSessionId());

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC
                ,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_OUT_MONO,
                ENCODING, bufferSize, AudioTrack.MODE_STREAM,recorder.getAudioSessionId());

        track.play();

        recorder.startRecording();


        byte[] bytes = new byte[bufferSize];

        while (running) {

            int len = recorder.read(buffer, buffer.capacity());


            Log.e("CHAT_AUDIO", "BYTES: " + Arrays.toString(buffer.array()));
            Log.e("CHAT_AUDIO", "LEN: " + len);
            Log.e("CHAT_AUDIO", "SIZE: " + buffer.array().length);

//            long sume = 0;
//            byte min = 0;
//            byte max = 0;
//            for (byte b :
//                    buffer.array()) {
//
//                if (b > max)
//                    max = b;
//                if (b < min)
//                    min = b;
//
//            }
//
//            byte avg = (byte) ((max + min) / 2);
//
//            byte[] as = new byte[buffer.array().length];
//
//            for (int i = 0; i < buffer.array().length - 1; i++) {
//
//                byte oo = buffer.array()[i];
//                byte op = oo;
//
//                if(Math.abs(oo) > (Math.abs(avg) + 30)){
//                    op = avg;
//                }
//                as[i] = op;
//            }

            if (len > 0)
                track.write(buffer.array(), 0, buffer.array().length);

        }


    }

    private void setConfigs(int sessionId) {
        try {
            if (AutomaticGainControl.isAvailable()) {
                AutomaticGainControl agc = AutomaticGainControl.create(sessionId);
                if (agc != null)
                    agc.setEnabled(true);
            } else {
                Log.w(TAG, "AutomaticGainControl is not available on this device :(");
            }
        } catch (Throwable x) {
            Log.e(TAG, "error creating AutomaticGainControl", x);
        }


        try {
            if (NoiseSuppressor.isAvailable()) {
                NoiseSuppressor ns = NoiseSuppressor.create(sessionId);
                if (ns != null) {
                    ns.setEnabled(true);
                }
            } else {
                Log.w(TAG, "NoiseSuppressor is not available on this device :(");
            }
        } catch (Throwable x) {
            Log.e(TAG, "error creating NoiseSuppressor", x);
        }

        try {
            if (AcousticEchoCanceler.isAvailable()) {
                AcousticEchoCanceler aec = AcousticEchoCanceler.create(sessionId);
                if (aec != null) {
                    aec.setEnabled(true);
                }
            } else {
                Log.w(TAG, "AcousticEchoCanceler is not available on this device");
            }
        } catch (Throwable x) {
            Log.e(TAG, "error creating AcousticEchoCanceler", x);
        }
    }

    public void stop() {

        running = false;
    }


}
