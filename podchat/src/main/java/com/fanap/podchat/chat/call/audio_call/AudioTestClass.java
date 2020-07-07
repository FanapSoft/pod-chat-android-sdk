package com.fanap.podchat.chat.call.audio_call;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.nio.ByteBuffer;

import static android.content.Context.AUDIO_SERVICE;

public class AudioTestClass {


    private static final int bufferSize = 2048 * 2;
    private boolean running = true;

    public void start(Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

//        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);


        int b1 = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        int b2 = AudioTrack.getMinBufferSize(8000,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);

        int recordBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (recordBufferSize <= 0) {
            recordBufferSize = 1280;
        }



        AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recordBufferSize * 10);

        audioRecorder.startRecording();





        int playerBufferSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (playerBufferSize <= 0) {
            playerBufferSize = 3840;
        }

        AudioTrack audioTrackPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, playerBufferSize, AudioTrack.MODE_STREAM);
        audioTrackPlayer.setStereoVolume(1.0f, 1.0f);

        audioTrackPlayer.play();

//        AudioRecord recorder = new AudioRecord(
//                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
//                8000, AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//
//        AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
//                8000, AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
//
//        track.play();
//
//        recorder.startRecording();

        ByteBuffer buffer = ByteBuffer.allocateDirect(recordBufferSize);


        byte[] bufferBytes = new byte[playerBufferSize];

        while (running) {

            int len = audioRecorder.read(buffer, buffer.capacity());


            audioTrackPlayer.write(bufferBytes, 0, playerBufferSize);

        }


    }

    public void stop() {

        running = false;
    }


}
