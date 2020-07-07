package com.fanap.podchat.chat.call.audio_call;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fanap.podchat.util.Permission;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class PodAudioStreamManager implements SensorEventListener, AudioManager.OnAudioFocusChangeListener {

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
    //    private Thread recordingThread = null;
    private Thread playingThread = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    boolean hasPermission;

    boolean isSpeakerOn = false;
    boolean isMute = false;


    private SensorManager mSensorManager = null;
    private Sensor mProximitySensor = null;

    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    private PowerManager.WakeLock proximityWakelock;
    private boolean isProximityNear;


    private AutomaticGainControl agc;
    private NoiseSuppressor ns;
    private AcousticEchoCanceler aec;

    private AudioManager audioManager;
    private Context mContext;


    private IPodAudioListener recordCallback;

    private IPodAudioPlayerListener playerCallback;
    private boolean needResampling = false;
    private ByteBuffer buffer;
    private boolean running = false;

    private BroadcastReceiver headsetReceiver;

    private boolean isHeadset;
    private boolean haveAudioFocus;

    PodAudioStreamManager(Context context) {

        this.mContext = context;

        audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);

        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        if (audioManager != null) {
            setupAudioManager();
        }

        if (mSensorManager != null) {
            setupProximity();
        }

        setupHeadsetReceiver();

        hasPermission = false;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)!=null) {
//            BUF_SIZE = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));
//        }

    }

    private void setupHeadsetReceiver() {
        headsetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateHeadsetState(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        }

        mContext.registerReceiver(headsetReceiver, intentFilter);
    }

    private void updateHeadsetState(Intent intent) {
        int state = intent.getIntExtra("state", -1);
        isHeadset = state == 1;
        boolean hasMic = intent.getIntExtra("microphone", -1) == 1;

        resetRecorder();

        Log.e(TAG, "STATE: " + intent.getIntExtra("state", -1));
        Log.e(TAG, "NAME: " + intent.getStringExtra("name"));
        Log.e(TAG, "mic: " + intent.getIntExtra("microphone", -1));
    }

    private void setupProximity() {
        try {
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            proximityWakelock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.fanap.podchat:audio_stream");
        } catch (Exception ignored) {
        }
    }

    private void setupAudioManager() {
        isHeadset = audioManager.isWiredHeadsetOn();
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void updateAudioManager() {
        isHeadset = audioManager.isWiredHeadsetOn();
        audioManager.setMode(AudioManager.MODE_IN_CALL);
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT), min);
    }

    void recordAudio(@NonNull IPodAudioListener listener) {

        this.recordCallback = listener;

        if (recorder != null) {
            stopRecording();
        }

        updateAudioManager();

//        BUF_SIZE = AudioRecord.getMinBufferSize(BITRATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        recorder = new AudioRecord(
                isHeadset ? MediaRecorder.AudioSource.MIC
                        : MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                BITRATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BUF_SIZE);


        try {
            if (AutomaticGainControl.isAvailable()) {
                agc = AutomaticGainControl.create(recorder.getAudioSessionId());
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
                ns = NoiseSuppressor.create(recorder.getAudioSessionId());
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
                aec = AcousticEchoCanceler.create(recorder.getAudioSessionId());
                if (aec != null) {
                    aec.setEnabled(true);
                }
            } else {
                Log.w(TAG, "AcousticEchoCanceler is not available on this device");
            }
        } catch (Throwable x) {
            Log.e(TAG, "error creating AcousticEchoCanceler", x);
        }


        buffer = ByteBuffer.allocateDirect(BUF_SIZE);

        start();
    }

    public boolean start() {
        if (recorder == null || recorder.getState() != AudioRecord.STATE_INITIALIZED)
            return false;
        try {
            if (recordingThread == null) {
                if (recorder == null)
                    return false;
                recorder.startRecording();
                isRecording = true;
                startThread();
            } else {
                recorder.startRecording();
            }
            return true;
        } catch (Exception x) {
            Log.e(TAG, "Error initializing AudioRecord", x);
        }
        return false;
    }

    private void startThread() {
        if (recordingThread != null) {
            throw new IllegalStateException("thread already started");
        }
        running = true;
        recordingThread = new Thread(() -> {

//            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            while (isRecording) {
                try {
                    recorder.read(buffer, BUF_SIZE);
                    if (!isRecording) {
                        recorder.stop();
                        break;
                    }
                    recordCallback.onByteRecorded(buffer.array());
                } catch (Exception ignored) {
                }
            }
            Log.i(TAG, "audiorecord thread exits");
        });
        recordingThread.start();
    }

    void initAudioPlayer(IPodAudioPlayerListener listener) {

        playerCallback = listener;

        if (isPlaying)
            stopPlaying();

        track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                BITRATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);


        track.play();

        playerCallback.onPlayerReady();

        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    void setPlayingThread(Thread playingThread) {
        this.playingThread = playingThread;
    }

    void playAudio(byte[] bytes) {

        isPlaying = true;

        try {
            if (track != null) {
                int numOfWroteBytes = track.write(bytes, 0, BUF_SIZE);
                Log.e(TAG, "CONSUME: " + numOfWroteBytes);
                //            Log.e(TAG, "SIZE: " + bytes.length);

            }


        } catch (Exception e) {
            e.printStackTrace();
            playerCallback.onAudioPlayError(e.getMessage());
        }


    }

    void stopPlaying() {

        if (track != null) {
            track.stop();
            track.flush();
            track.release();
            isPlaying = false;
            playingThread = null;
            track = null;
        }

        if (playerCallback != null) playerCallback.onPlayStopped();

        mSensorManager.unregisterListener(this);

    }

    void stopRecording() {
        // stops the recording activity

        if (recordingThread != null) {

            recordingThread = null;

        }

        if (null != recorder) {
            isRecording = false;
            recorder.release();
            recorder = null;
        }

        if (agc != null) {
            agc.release();
            agc = null;
        }
        if (ns != null) {
            ns.release();
            ns = null;
        }
        if (aec != null) {
            aec.release();
            aec = null;
        }

        buffer.clear();

        if (recordCallback != null) recordCallback.onRecordStopped();

    }

    public void endStream() {

        try {
            if (audioManager != null) {

                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setMicrophoneMute(false);
                audioManager.setSpeakerphoneOn(false);

                if (haveAudioFocus)
                    audioManager.abandonAudioFocus(this);
            }
            stopRecording();
            stopPlaying();
            mContext.unregisterReceiver(headsetReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void switchSpeakerState(boolean isSpeakerOn) {

        if (isSpeakerOn != this.isSpeakerOn) {
            this.isSpeakerOn = isSpeakerOn;
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(isSpeakerOn);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo audioDeviceInfo = track.getRoutedDevice();
            Log.d(TAG, "DEVICE: " + audioDeviceInfo.getProductName());
            Log.d(TAG, ">>>>: " + Arrays.toString(audioDeviceInfo.getSampleRates()));
            Log.d(TAG, ">>>>>: " + audioDeviceInfo.getType());
        }
    }

    void switchMuteState(boolean isMute) {

        if (isMute != this.isMute) {
            this.isMute = isMute;
            audioManager.setMicrophoneMute(isMute);
        }
    }


    private void resetRecorder() {
        if (isRecording) {
            recordAudio(recordCallback);
            recordCallback.onRecordRestarted();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (isRecording) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                if (isHeadset || audioManager.isSpeakerphoneOn()) {
                    return;
                }

                //calc distance from ear

                boolean newIsNear = sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3);

                //detect change

                if (isProximityNear != newIsNear) {

                    isProximityNear = newIsNear;

                    if (isProximityNear) {

                        proximityWakelock.acquire(10 * 60 * 1000L /*10 minutes*/);

                    } else {

                        proximityWakelock.release();

                    }

                }

            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        haveAudioFocus = focusChange == AudioManager.AUDIOFOCUS_GAIN;
    }

    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onAudioRecordError(String cause);

        void onRecordRestarted();

    }

    public interface IPodAudioPlayerListener {

        void onPlayStopped();

        void onAudioPlayError(String cause);

        void onPlayerReady();

    }

    public interface IPodAudioSendReceiveSync {

        void onConsumerReady();

        void onFirstBytesRecorded();

    }

}
