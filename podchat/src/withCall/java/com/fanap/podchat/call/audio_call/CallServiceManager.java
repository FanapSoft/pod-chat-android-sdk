package com.fanap.podchat.call.audio_call;

import static com.fanap.podchat.call.audio_call.EndCallReceiver.ACTION_STOP_CALL;
import static com.fanap.podchat.call.audio_call.PodCallAudioCallService.TARGET_ACTIVITY;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.StartedCallModel;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.Util;

public class CallServiceManager implements ICallServiceState {


    private PodCallAudioCallService callService;
    private CallConfig mCallConfig;
    private CallInfo callInfo;

    private Context mContext;
    private Intent runServiceIntent;
    private ICallState callStateCallback;

    private boolean bound = false;

    public CallServiceManager(Context context,CallConfig callConfig) {
        mContext = context;
        mCallConfig = callConfig;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            PodCallAudioCallService.CallBinder binder = (PodCallAudioCallService.CallBinder) service;
            callService = binder.getService();
            bound = true;
            callService.registerCallStateCallback(CallServiceManager.this);
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

    public void setmCallConfig(CallConfig mCallConfig) {
        this.mCallConfig = mCallConfig;
    }


    /*
    Manage audio streaming
     */

    public void startCallService(ChatResponse<StartedCallModel> response, ICallState iCallState) {

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

        startCallService();
        bindService();
    }

    public void stopCallService() {

        showInfoLog("End Stream Requested");

        if (bound && callService != null) {
            showInfoLog("End Stream Requested => bound && callService != null");
            callService.endCall();
            unbindService();
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

    private void startCallService() {

        runServiceIntent = new Intent(mContext, PodCallAudioCallService.class);

        if (mCallConfig != null && !Util.isNullOrEmpty(mCallConfig.getTargetActivity()))
            runServiceIntent.putExtra(TARGET_ACTIVITY, mCallConfig.getTargetActivity());


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

}
