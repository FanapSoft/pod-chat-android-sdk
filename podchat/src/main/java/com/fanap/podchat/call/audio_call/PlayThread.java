package com.fanap.podchat.call.audio_call;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

import ir.farhad7d7.theopuscodec.opus.OpusDecoder;


public class PlayThread extends Thread {

    // Sample rate must be one supported by Opus.
    private static final int SAMPLE_RATE = 12000;

    // Number of samples per frame is not arbitrary,
    // it must match one of the predefined values, specified in the standard.
    private static final int FRAME_SIZE = 720;


    private static final String TAG = "AUDIO_RECORDER";

    private byte[] encodedBuffer = new byte[]{};
    private boolean playing = false;
//    private EchoCanceller echoCanceller;
    private AudioTrack audioTrack;

    private boolean hasBuiltInAEC;

    OpusDecoder decoder;


    void fillPlayerBuffer(byte[] bytes) {
        this.encodedBuffer = bytes;
    }

    PlayThread() {
        hasBuiltInAEC = AcousticEchoCanceler.isAvailable();
    }

    @Override
    public void run() {


        int minBufSize = initAudioTracker();

        // init opus decoder
        OpusDecoder decoder = initDecoder();

//        if (!hasBuiltInAEC) {
//            if (echoCanceller == null) {
//                echoCanceller = new EchoCanceller();
//                echoCanceller.openEcho(SAMPLE_RATE, minBufSize, 1024);
//            }
//        }


        playAudio(decoder);

        super.run();
    }

    private OpusDecoder initDecoder() {
        if (decoder == null) {
            decoder = new OpusDecoder();
            decoder.init(SAMPLE_RATE, 1);
        }
        return decoder;
    }

    private int initAudioTracker() {

        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);


        if (audioTrack == null)
            // init audio track
            audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize,
                    AudioTrack.MODE_STREAM);

        audioTrack.play();

        playing = true;

        return minBufSize;
    }

    private void playAudio(OpusDecoder decoder) {

        short[] outputBuffer = new short[FRAME_SIZE];

        try {

            while (playing) {

                if (encodedBuffer == null || encodedBuffer.length == 0) continue;


                byte[] safeBufferData = encodedBuffer.clone();

                encodedBuffer = null;

                int decoded = decoder.decode(safeBufferData, outputBuffer, FRAME_SIZE);

                Log.v(TAG, "Decoded back " + decoded * 2 + " bytes");

                audioTrack.write(outputBuffer, 0, decoded);

//                if (hasBuiltInAEC)
//                    audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);
//                else
//                    playWithSpeexAEC(outputBuffer, decoded);


            }

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }
    }

    private void playWithSpeexAEC(short[] outputBuffer, int decoded) {
//        audioTrack.write(echoCanceller.capture(outputBuffer), 0, decoded);
//
//        echoCanceller.playback(outputBuffer);
    }

    void stopPlaying() {
        try {

            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {

                    try {
                        audioTrack.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                audioTrack.release();
            }
            playing = false;
//            if (echoCanceller != null)
//                echoCanceller.closeEcho();
//            if (decoder != null)
//                decoder.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
