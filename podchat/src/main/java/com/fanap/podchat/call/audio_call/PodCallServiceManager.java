package com.fanap.podchat.call.audio_call;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.StartCallResult;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.Util;

import static com.fanap.podchat.call.audio_call.EndCallReceiver.ACTION_STOP_CALL;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

public class PodCallServiceManager implements ICallServiceState {

    static final String RECEIVING_TOPIC = "RECEIVING_TOPIC";
    static final String SENDING_TOPIC = "SENDING_TOPIC";
    static final String CLIENT_ID = "CLIENT_ID";
    static final String BROKER_ADDRESS = "BROKER_ADDRESS";
    static final String TARGET_ACTIVITY = "TARGET_ACTIVITY";

    static final String SSL_CONFIG = "SSL_CONFIG";

    private AudioCallService callService;
    private CallConfig callConfig;
    private CallInfo callInfo;

    private Context mContext;
    private Intent runServiceIntent;
    private ICallState callStateCallback;

    private boolean bound = false;
    private boolean enableSSL = true;

    public PodCallServiceManager(Context context) {
        mContext = context;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            AudioCallService.CallBinder binder = (AudioCallService.CallBinder) service;
            callService = binder.getService();
            callService.setSSL(enableSSL);
            bound = true;
            callService.registerCallStateCallback(PodCallServiceManager.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            callService.unregisterCallStateCallback(PodCallServiceManager.this);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            bound = false;
            callService.unregisterCallStateCallback(PodCallServiceManager.this);
        }
    };

    public void setCallConfig(CallConfig callConfig) {
        this.callConfig = callConfig;
    }


    /*
    Manage audio streaming
     */

    public void startCallStream(ChatResponse<StartCallResult> response, ICallState iCallState) {
        this.callStateCallback = iCallState;
        startCallService(response.getResult());
        bindService();
    }

    public void startStream(ICallState iCallState) {
        this.callStateCallback = iCallState;
        startCallService();
        bindService();

    }

    public void testStream(String groupId, String sender, String receiver, ICallState iCallState) {
        this.callStateCallback = iCallState;
        callInfo = new CallInfo();
        callInfo.setSubjectId(1000001);
        callInfo.setPartnerId(15000);
        callInfo.setPartnerName("تست کافکا");
        callInfo.setPartnerImage("https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482");

        startCallService(groupId, sender, receiver);
        bindService();

    }

    public void testAudio(ICallState iCallState) {
        this.callStateCallback = iCallState;


        callInfo = new CallInfo();
        callInfo.setSubjectId(1000001);
        callInfo.setPartnerId(15000);
        callInfo.setPartnerName("تست صدا");
        callInfo.setPartnerImage("https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482");


        startCallService();
        bindService();
    }

    public void endStream(boolean fromPartner) {


        showInfoLog("End Stream Requested");

        if (bound && callService != null) {
            showInfoLog("End Stream Requested => bound && callService != null");
            callService.endCall();
            unbindService();
        } else if (!fromPartner) {
            showInfoLog("End Stream Requested => sendEndCallIntent(mContext)");
            sendEndCallIntent(mContext);
        }


        callInfo = null;

    }

    private void showInfoLog(String message) {
        if (callStateCallback != null) {
            callStateCallback.onInfoEvent(message);
        }
    }

    private void showErrorLog(String message) {
        if (callStateCallback != null) {
            callStateCallback.onErrorEvent(message);
        }
    }

    private void sendEndCallIntent(Context context) {
        Intent intent1 = new Intent(context, AudioCallService.class);
        intent1.setAction(ACTION_STOP_CALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else context.startService(intent1);
    }

    /*
    Stream Setting
     */
    public void switchAudioSpeakerState(boolean isSpeakerOn) {

        if (bound && callService != null)
            callService.switchSpeaker(isSpeakerOn);
    }

    public void switchAudioMuteState(boolean isMute) {

        if (bound && callService != null)
            callService.switchMic(isMute);
    }



    /*

    Manage Call Service
     */

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
        runServiceIntent.putExtra(SENDING_TOPIC, result.getClientDTO().getTopicSend());
        runServiceIntent.putExtra(RECEIVING_TOPIC, result.getClientDTO().getTopicReceive());
        runServiceIntent.putExtra(CLIENT_ID, result.getClientDTO().getClientId());
        runServiceIntent.putExtra(BROKER_ADDRESS, result.getClientDTO().getBrokerAddress());
        runServiceIntent.putExtra(SSL_CONFIG, result.getCert_file());
        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }
    }

    private void startCallService(String groupId, String sender, String receiver) {
        runServiceIntent = new Intent(mContext, AudioCallService.class);
        runServiceIntent.putExtra(SENDING_TOPIC, sender);
        runServiceIntent.putExtra(RECEIVING_TOPIC, receiver);
        runServiceIntent.putExtra(CLIENT_ID, groupId);
        runServiceIntent.putExtra(BROKER_ADDRESS, "172.16.106.158:9093");


        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());
        if (callInfo != null)
            runServiceIntent.putExtra(POD_CALL_INFO, callInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }
    }

    private void startCallService() {

        runServiceIntent = new Intent(mContext, AudioCallService.class);

        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());


        if (callInfo != null)
            runServiceIntent.putExtra(POD_CALL_INFO, callInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }
    }


    /*
    Call Service Callback
     */
    /*
    Call Stopped from service
     */
    @Override
    public void onEndCallRequested() {

        showInfoLog("End Stream From Service Requested");

        if (callStateCallback != null)
            callStateCallback.onEndCallRequested();
        callInfo = null;
    }


    /*
   Store incoming call info
     */
    public void addNewCallInfo(ChatResponse<CallRequestResult> response) {


        callInfo = new CallInfo();

        callInfo.setSubjectId(response.getSubjectId());
        callInfo.setPartnerId(response.getResult().getCreatorVO().getId());
        callInfo.setPartnerName(response.getResult().getCreatorVO().getName());
        callInfo.setPartnerImage(response.getResult().getCreatorVO().getImage());


    }

    public void setSSL(boolean withSSL) {

        enableSSL = withSSL;
    }
}
