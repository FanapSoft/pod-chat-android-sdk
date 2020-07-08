package com.fanap.podchat.chat.call.audio_call;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.fanap.podchat.chat.call.result_model.StartCallResult;

public class PodCallServiceManager {

    public static final String RECEIVING_TOPIC = "RECEIVING_TOPIC";
    public static final String SENDING_TOPIC = "SENDING_TOPIC";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String BROKER_ADDRESS = "BROKER_ADDRESS";
    private Context mContext;

    private Intent runServiceIntent;
    private AudioCallService callService;
    private boolean bound = false;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            AudioCallService.CallBinder binder = (AudioCallService.CallBinder) service;
            callService = binder.getService();
            bound = true;
//            callService.registerCallBack(YourActivity.this); // register

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            bound = false;
        }
    };


    public PodCallServiceManager(Context context) {

        mContext = context;

    }

    public void startCallStream(StartCallResult result) {
        startCallService(result);
        bindService();


    }

    public void endStream() {

        if (bound && callService != null)
            callService.endCall();

        unbindService();


    }

    public void startStream() {

        startCallService();
        bindService();

    }


    public void testStream(String groupId, String sender, String receiver) {

        startCallService(groupId, sender, receiver);
        bindService();

    }


    public void testAudio() {
        startCallService();
        bindService();
    }

    public void switchAudioSpeakerState(boolean isSpeakerOn) {

        if (bound && callService != null)
            callService.switchSpeaker(isSpeakerOn);
    }

    public void switchAudioMuteState(boolean isMute) {

        if (bound && callService != null)
            callService.switchMic(isMute);
    }


    private void bindService() {
        mContext.bindService(runServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (bound) {
//            callService.registerCallBack(null); // unregister
            mContext.unbindService(serviceConnection);
            bound = false;
        }
    }


    private void startCallService(StartCallResult result) {
        runServiceIntent = new Intent(mContext, AudioCallService.class);
        runServiceIntent.putExtra(SENDING_TOPIC, result.getTopicSend());
        runServiceIntent.putExtra(RECEIVING_TOPIC, result.getTopicReceive());
        runServiceIntent.putExtra(CLIENT_ID, result.getClientId());
        runServiceIntent.putExtra(BROKER_ADDRESS, result.getBrokerAddress());
        mContext.startService(runServiceIntent);
    }

    private void startCallService(String groupId, String sender, String receiver) {
        runServiceIntent = new Intent(mContext, AudioCallService.class);
        runServiceIntent.putExtra(SENDING_TOPIC, sender);
        runServiceIntent.putExtra(RECEIVING_TOPIC, receiver);
        runServiceIntent.putExtra(CLIENT_ID, groupId);
        runServiceIntent.putExtra(BROKER_ADDRESS, "172.16.106.158:9093");
        mContext.startService(runServiceIntent);
    }


    private void startCallService() {
        runServiceIntent = new Intent(mContext, AudioCallService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);

        }
    }
}
