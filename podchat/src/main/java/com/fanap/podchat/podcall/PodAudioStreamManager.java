package com.fanap.podchat.podcall;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podchat.util.Permission;

import java.util.Arrays;

public class PodAudioStreamManager {

    private static final int BITRATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes

    private static final int BufferElements2Rec = 2048; // want to play 2048 (2K) since 2 bytes we use only 1024
    private static final int BytesPerElement = 2; // 2 bytes in 16bit format

//    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
        private int BUF_SIZE = BufferElements2Rec * BytesPerElement; //Bytes
    private static final String TAG = "CHAT_SDK_CALL";


    private AudioRecord recorder = null;
    private AudioTrack track = null;
    private Thread recordingThread = null;
    private Thread playingThread = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    boolean hasPermission = false;


    private IPodAudioListener recordCallback;

    private IPodAudioPlayerListener playerCallback;


    public void recordAudio(@NonNull IPodAudioListener listener) {

        this.recordCallback = listener;
//
//        if (!hasPermission) {
//
//            callback.onErrorOccurred("Need Audio Record Permission");
//
//            return;
//        }


//        BUF_SIZE = AudioRecord.getMinBufferSize(BITRATE,
//                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

//        int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
//        int BytesPerElement = 2; // 2 bytes in 16bit format

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                BITRATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BUF_SIZE);

//        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, BITRATE,
//                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
//                AudioRecord.getMinBufferSize(BITRATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 10);


        recordingThread = new Thread(this::generateBytes, "AudioRecorder Thread");
        recordingThread.start();
    }



    private void generateBytes() {
        // Write the output audio in byte

        short[] sData = new short[BufferElements2Rec];

        isRecording = true;
        recorder.startRecording();

        int counter = 0;

        while (isRecording) {

            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec);

            // // writes the data to file from buffer
            // // stores the voice buffer
            byte[] bytes = short2byte(sData);


            recordCallback.onByteRecorded(bytes);

        }
    }

    public void initPlayAudio(IPodAudioPlayerListener listener) {

        playerCallback = listener;

        track = new AudioTrack(AudioManager.STREAM_MUSIC, BITRATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);
        track.play();

    }

    public void setPlayingThread(Thread playingThread) {
        this.playingThread = playingThread;
    }

    public void playAudio(byte[] bytes) {

        try {

            track.write(bytes, 0, BUF_SIZE);
            Log.e(TAG, "WRTITTING: " + Arrays.toString(bytes));
            Log.e(TAG, "SIZE: " + bytes.length);

        } catch (Exception e) {
            e.printStackTrace();
            playerCallback.onErrorOccurred(e.getMessage());
        }


    }


    public void stopPlaying() {

        if (track != null) {
            track.stop();
            track.flush();
            track.release();
            isPlaying = false;
            playingThread = null;
            track = null;
        }

        if (playerCallback != null) playerCallback.onPlayStopped();
    }


    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }

        if (recordCallback != null) recordCallback.onRecordStopped();

    }

    public void checkPermission(Activity activity) {
        hasPermission = Permission.Check_RECORD_AUDIO(activity);
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


    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onErrorOccurred(String cause);

    }

    public interface IPodAudioPlayerListener {

        void onPlayStopped();

        void onErrorOccurred(String cause);

    }


}
