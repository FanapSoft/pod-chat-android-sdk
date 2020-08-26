package com.fanap.podchat.call.audio_call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.fanap.podchat.call.model.CallSSLData;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class PodAudioStreamManager2 implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, RecordThread.IRecordThread, CallConsumer.IConsumer, CallProducer.IRecordThread {

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private boolean isSpeakerOn = false;
    private boolean isMute = false;
    private boolean isHeadset = false;
    private boolean haveAudioFocus;

    private SensorManager mSensorManager = null;
    private Sensor mProximitySensor = null;

    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    private PowerManager.WakeLock proximityWakelock;
    private boolean isProximityNear;

    private AudioManager audioManager;
    private Context mContext;

    private IPodAudioListener recordCallback;

    private IPodAudioPlayerListener playerCallback;

    private BroadcastReceiver headsetReceiver;


    private CallConsumer callConsumer;

    private CallProducer callProducer;

    private CallSSLData callSSL;

    Thread consumerThread;

    Thread producerThread;

    String brokerAddress;
    String sendKey;
    String receivingTopic;
    String sendingTopic;


    PodAudioStreamManager2(Context context) {

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
        resetRecorder();
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

    void recordAudio(@NonNull IPodAudioListener listener, String sendingTopic) {

        this.recordCallback = listener;
        this.sendingTopic = sendingTopic;

        if (isRecording) {
            stopRecording();
        }

        updateAudioManager();

        callProducer = new CallProducer(this, callSSL,brokerAddress,sendKey,sendingTopic);

        producerThread = new Thread(callProducer);
        producerThread.setName("PRODUCING THREAD");
        producerThread.start();
    }


    void initAudioPlayer(CallSSLData callSSL,
                         String brokerAddress,
                         String sendKey,
                         String receivingTopic,
                         IPodAudioPlayerListener listener) {

        playerCallback = listener;
        this.callSSL = callSSL;

        this.brokerAddress = brokerAddress;
        this.receivingTopic = receivingTopic;
        this.sendKey = sendKey;

        if (isPlaying) {
            stopPlaying();
        }


        callConsumer = new CallConsumer(
                callSSL, brokerAddress, sendKey, receivingTopic, this);

        consumerThread = new Thread(callConsumer);

        consumerThread.setName("CONSUMING THREAD");

        consumerThread.start();

        playerCallback.onPlayerReady();

        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    void setPlaying() {
        isPlaying = true;
        callConsumer.stopPlaying();
    }


    private void stopPlaying() {

        if (isPlaying) {
            callConsumer.stopPlaying();
            isPlaying = false;
        }

        if (playerCallback != null) playerCallback.onPlayStopped();

        mSensorManager.unregisterListener(this);

    }

    private void stopRecording() {
        // stops the recording activity

        if (isRecording) {
            isRecording = false;
            callProducer.stopRecording();
        }

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

    void switchSpeakerState(boolean isSpeakerOn) {

        if (isSpeakerOn != this.isSpeakerOn) {
            this.isSpeakerOn = isSpeakerOn;
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(isSpeakerOn);
            }
        }
    }

    void switchMuteState(boolean isMute) {
        if (isMute != this.isMute) {
            this.isMute = isMute;
            if (audioManager != null) {
                audioManager.setMicrophoneMute(isMute);
            }
        }
    }


    private void resetRecorder() {
        if (isRecording) {
            recordAudio(recordCallback, sendingTopic);
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

    @Override
    public void onRecord(byte[] encoded) {
        recordCallback.onByteRecorded(encoded);
    }

    @Override
    public void onRecorderSet() {
        isRecording = true;
        recordCallback.onRecordStarted();
    }

    @Override
    public void onRecorderError(String cause) {
        recordCallback.onAudioRecordError(cause);
    }

    @Override
    public void onFirstBytesReceived() {


    }

    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onAudioRecordError(String cause);

        void onRecordRestarted();

        void onRecordStarted();

    }

    public interface IPodAudioPlayerListener {

        void onPlayStopped();

        void onAudioPlayError(String cause);

        void onPlayerReady();

    }

}
