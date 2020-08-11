package com.fanap.podchat.call.audio_call;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

import com.fanap.podchat.call.codec.opus.OpusDecoder;
import com.fanap.podchat.call.codec.speexdsp.EchoCanceller;

public class PlayThread extends Thread {

    // Sample rate must be one supported by Opus.
    static final int SAMPLE_RATE = 8000;

    // Number of samples per frame is not arbitrary,
    // it must match one of the predefined values, specified in the standard.
    static final int FRAME_SIZE = 160;

    // 1 or 2
    static final int NUM_CHANNELS = 1;
    private static final String TAG = "AUDIO_RECORDER";

    private byte[] encodedBuffer = new byte[]{};
    private boolean playing = false;
    private EchoCanceller echoCanceller;
    private AudioTrack audioTrack;

    private boolean hasBuiltInAEC;


    synchronized void fillPlayerBuffer(byte[] bytes) {
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

        if (!hasBuiltInAEC) {

            echoCanceller = new EchoCanceller();

            echoCanceller.openEcho(SAMPLE_RATE, minBufSize, 1024);
        }


        playAudio(decoder);

        super.run();
    }

    private OpusDecoder initDecoder() {
        OpusDecoder decoder = new OpusDecoder();
        decoder.init(SAMPLE_RATE, NUM_CHANNELS);
        return decoder;
    }

    private int initAudioTracker() {
        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        // init audio track
        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufSize,
                AudioTrack.MODE_STREAM);

        audioTrack.play();

        playing = true;

        return minBufSize;
    }

    private void playAudio(OpusDecoder decoder) {

        short[] outputBuffer = new short[FRAME_SIZE * NUM_CHANNELS];

        try {

            while (playing) {

                if (encodedBuffer == null || encodedBuffer.length == 0) continue;

                int decoded = decoder.decode(encodedBuffer, outputBuffer, FRAME_SIZE);

                Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");

                if (hasBuiltInAEC)
                    audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);
                else
                    playWithSpeexAEC(outputBuffer, decoded);


            }

        } catch (Exception e) {
            stopPlaying();
        }
    }

    private void playWithSpeexAEC(short[] outputBuffer, int decoded) {
        audioTrack.write(echoCanceller.capture(outputBuffer), 0, decoded * NUM_CHANNELS);

        echoCanceller.playback(outputBuffer);
    }

     void stopPlaying() {
        try {
            playing = false;
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
            }
            if (encodedBuffer != null)
                echoCanceller.closeEcho();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
