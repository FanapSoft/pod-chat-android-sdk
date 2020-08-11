package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.fanap.podchat.call.codec.opus.OpusDecoder;
import com.fanap.podchat.call.codec.opus.OpusEncoder;
import com.fanap.podchat.call.codec.speexdsp.EchoCanceller;

import java.util.Arrays;


import static android.content.Context.AUDIO_SERVICE;

public class AudioThread extends Thread {


    // Sample rate must be one supported by Opus.
    static final int SAMPLE_RATE = 8000;

    // Number of samples per frame is not arbitrary,
    // it must match one of the predefined values, specified in the standard.
    static final int FRAME_SIZE = 160;

    // 1 or 2
    static final int NUM_CHANNELS = 1;
    private static final String TAG = "AUDIO_RECORDER";

    private Context mContext;

    public AudioThread(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);

        audioManager.setSpeakerphoneOn(false);

        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        // initialize audio recorder
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufSize);


        // init opus encoder
        OpusEncoder encoder = new OpusEncoder();
        encoder.init(SAMPLE_RATE, NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);


        // init audio track
        AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufSize,
                AudioTrack.MODE_STREAM);


        // init opus decoder
        OpusDecoder decoder = new OpusDecoder();
        decoder.init(SAMPLE_RATE, NUM_CHANNELS);
//

        // start
        recorder.startRecording();
        track.play();

//        byte[] inBuf = new byte[(FRAME_SIZE * NUM_CHANNELS * 2)];
        short[] inBufShort = new short[FRAME_SIZE * NUM_CHANNELS];
        byte[] encBuf = new byte[1024];
        short[] outBuf = new short[FRAME_SIZE * NUM_CHANNELS];


        EchoCanceller echoCanceller = new EchoCanceller();

        echoCanceller.openEcho(SAMPLE_RATE, minBufSize, 1024);


        try {

            while (!Thread.interrupted()) {
//                ByteBuffer buffer = ByteBuffer.allocateDirect(minBufSize);
//                short[] buffer = new short[minBufSize];
//
//                echoCanceller.openEcho(SAMPLE_RATE, minBufSize, 1024);
//
//                while (true) {

//                    int read = recorder.read(buffer, 0, minBufSize);
//
//                    Log.e(TAG,"Recorded: " + Arrays.toString(buffer));
//                    if (read < 0) {
//                        throw new RuntimeException("recorder.read() returned error " + read);
//                    }
//
//                    short[] out = echoCanceller.processEcho(buffer, buffer);
//
//                    Log.e(TAG,"Echo Cancellation: " + Arrays.toString(out));
//
////                    echoCanceller.playback(buffer);
//
//                    track.write(out, 0, minBufSize);
//
////                    buffer = echoCanceller.capture(buffer);
//
//                }
                int to_read = inBufShort.length;
                int offset = 0;
                while (to_read > 0) {
                    int read = recorder.read(inBufShort, offset, to_read);
                    if (read < 0) {
                        throw new RuntimeException("recorder.read() returned error " + read);
                    }
                    to_read -= read;
                    offset += read;
                }

                Log.e(TAG, "Recorded: " + Arrays.toString(inBufShort));

//                ByteBuffer.wrap(inBuf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(inBufShort);

//                short[] out = echoCanceller.processEcho(inBufShort, inBufShort);
                short[] out = inBufShort;

                Log.e(TAG, "Echo Cancelled: " + Arrays.toString(out));

                int encoded = encoder.encode(out, FRAME_SIZE, encBuf);

                Log.v(TAG, "Encoded " + (out.length * 2) + " bytes of audio into " + encoded + " bytes");

                byte[] encBuf2 = Arrays.copyOf(encBuf, encoded);

                int decoded = decoder.decode(encBuf2, outBuf, FRAME_SIZE);

                Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");

                track.write(echoCanceller.capture(outBuf), 0, decoded * NUM_CHANNELS);

                echoCanceller.playback(outBuf);

            }
        } finally {
            recorder.stop();
            recorder.release();
            track.stop();
            track.release();
            echoCanceller.closeEcho();
        }


        super.run();


    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }



}
