package com.fanap.podchat.call.audio_call;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.ClientDTO;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.StartedCallModel;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.Util;

import static com.fanap.podchat.call.audio_call.EndCallReceiver.ACTION_STOP_CALL;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

public class PodCallAudioCallServiceManager implements ICallServiceState {

    static final String RECEIVING_TOPIC = "RECEIVING_TOPIC";
    static final String SENDING_TOPIC = "SENDING_TOPIC";
    static final String CLIENT_ID = "CLIENT_ID";
    static final String BROKER_ADDRESS = "BROKER_ADDRESS";
    static final String TARGET_ACTIVITY = "TARGET_ACTIVITY";

    static final String SSL_CONFIG = "SSL_CONFIG";
    static final String KAFKA_CONFIG = "KAFKA_CONFIG";

    private PodCallAudioCallService callService;
    private CallConfig callConfig;
    private CallInfo callInfo;

    private Context mContext;
    private Intent runServiceIntent;
    private ICallState callStateCallback;

    private boolean bound = false;
    private boolean enableSSL = true;

    public PodCallAudioCallServiceManager(Context context) {
        mContext = context;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            PodCallAudioCallService.CallBinder binder = (PodCallAudioCallService.CallBinder) service;
            callService = binder.getService();
            callService.setSSL(enableSSL);
            bound = true;
            callService.registerCallStateCallback(PodCallAudioCallServiceManager.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            callService.unregisterCallStateCallback();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            bound = false;
            callService.unregisterCallStateCallback();
        }
    };

    public void setCallConfig(CallConfig callConfig) {
        this.callConfig = callConfig;
    }


    /*
    Manage audio streaming
     */

    public void startCallStream(ChatResponse<StartedCallModel> response, ICallState iCallState) {

        this.callStateCallback = iCallState;

        if(callInfo==null){
            callInfo = new CallInfo();
            callInfo.setCallId(response.getSubjectId());
        }

        if (response.getResult().getCallName() != null) {
            callInfo.setCallName(response.getResult().getCallName());
        }

        if (response.getResult().getCallImage() != null) {
            callInfo.setCallImage(response.getResult().getCallImage());
        }

        startCallService(response.getResult());
        bindService();
    }

    /**
     * direct connect to kafka server for connection and audio test
     *
     * @param iCallState call callback
     * @param brokerAddress    brokerAddress
     * @param sender     sending topic
     * @param receiver   receiving topic
     */

    public void testStream(String brokerAddress, String sender, String receiver, ICallState iCallState) {
        this.callStateCallback = iCallState;
        callInfo = new CallInfo();
        callInfo.setCallId(1000001);
        callInfo.setPartnerId(15000);
        callInfo.setCallName("تست کافکا");
        callInfo.setCallImage("https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482");

        startCallService(brokerAddress, sender, receiver);
        bindService();

    }

    public void testStream(String broker,
                           String sendTopic,
                           String receiveTopic,
                           String ssl,
                           String sendKey,
                           ICallState iCallState) {

        this.callStateCallback = iCallState;
        callInfo = new CallInfo();
        callInfo.setCallId(1000001);
        callInfo.setPartnerId(15000);
        callInfo.setCallName("تست کافکا");
        callInfo.setCallImage("https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482");

        startCallService(broker, sendTopic, receiveTopic,ssl,sendKey);
        bindService();

    }



    /**
     * Test Audio Record
     *
     * @param iCallState callback
     */
    public void testAudio(ICallState iCallState) {
        this.callStateCallback = iCallState;


        callInfo = new CallInfo();
        callInfo.setCallId(1000001);
        callInfo.setPartnerId(15000);
        callInfo.setCallName("تست صدا");
        callInfo.setCallImage("https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482");


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
        Intent intent1 = new Intent(context, PodCallAudioCallService.class);
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

    public void addCallParticipant(ChatResponse<JoinCallParticipantResult> response) {


        if (bound && callService != null) {
            callService.addNewCallParticipant(response.getResult().getJoinedParticipants());
        }

    }


    public void removeCallParticipant(LeaveCallResult result) {

        if (bound && callService != null) {
            callService.removeCallParticipant(result.getCallParticipants());
        }

    }



    /*

    Manage Call Service
     */

    private void bindService() {
        mContext.bindService(runServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        try {
            if (bound) {
                bound = false;
                mContext.unbindService(serviceConnection);
            }
        }
        catch (IllegalStateException illegal){
            callStateCallback.onInfoEvent("Service already unregistered");
        }
        catch (Exception e) {
            callStateCallback.onErrorEvent("Exception in method => unbindService()");

        }
    }


    private void startCallService(StartedCallModel result) {

        runServiceIntent = new Intent(mContext, PodCallAudioCallService.class);

        runServiceIntent.putExtra(KAFKA_CONFIG, result.getClientDTO());

        runServiceIntent.putExtra(SSL_CONFIG, result.getCert_file());

        runServiceIntent.putExtra(POD_CALL_INFO, callInfo);

        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }
    }

    private void startCallService(String groupId, String sender, String receiver) {
        runServiceIntent = new Intent(mContext, PodCallAudioCallService.class);
        runServiceIntent.putExtra(SENDING_TOPIC, sender);
        runServiceIntent.putExtra(RECEIVING_TOPIC, receiver);
        runServiceIntent.putExtra(CLIENT_ID, groupId);
        runServiceIntent.putExtra(BROKER_ADDRESS, "172.16.106.158:9093");


        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }
    }

    private void startCallService(String broker,
                                  String sendTopic,
                                  String receiveTopic,
                                  String ssl,
                                  String sendKey) {



        ClientDTO client = new ClientDTO();
        client.setTopicReceive(receiveTopic);
        client.setTopicSend(sendTopic);
        client.setSendKey(sendKey);
        client.setClientId(sendKey);
        client.setBrokerAddress(broker);


        runServiceIntent = new Intent(mContext, PodCallAudioCallService.class);

        runServiceIntent.putExtra(KAFKA_CONFIG, client);

        runServiceIntent.putExtra(SSL_CONFIG, ssl);

        runServiceIntent.putExtra(POD_CALL_INFO, callInfo);

        if (callConfig != null && !Util.isNullOrEmpty(callConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, callConfig.getTargetActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(runServiceIntent);
        } else {
            mContext.startService(runServiceIntent);
        }

    }


    private void startCallService() {

        runServiceIntent = new Intent(mContext, PodCallAudioCallService.class);

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

        callInfo.setCallId(response.getSubjectId());

        callInfo.setPartnerId(response.getResult().getCreatorVO().getId());

        if (response.getResult().isGroup()) {
            callInfo.setCallName(response.getResult().getConversationVO().getTitle());
            callInfo.setCallImage(response.getResult().getConversationVO().getImage());
        } else {
            callInfo.setCallName(response.getResult().getCreatorVO().getName());
            callInfo.setCallImage(response.getResult().getCreatorVO().getImage());
        }


    }

    public void setSSL(boolean withSSL) {

        enableSSL = withSSL;
    }


}
