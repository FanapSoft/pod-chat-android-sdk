package com.example.chat.application.chatexample;


import static com.example.chat.application.chatexample.ServerConfig.*;


import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.application.chatexample.token.TokenHandler;
import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.camera.CameraId;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.screenshare.model.ScreenShareParam;
import com.fanap.podcall.util.CallPermissionHandler;
import com.fanap.podcall.video.codec.VideoCodecType;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.constants.CallType;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.model.CallErrorVO;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.model.CreateCallVO;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.call.request_model.RejectCallRequest;
import com.fanap.podchat.call.request_model.StartOrEndCallRecordRequest;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.request_model.screen_share.EndShareScreenRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenSharePermissionRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenShareRequest;
import com.fanap.podchat.call.request_model.screen_share.ScreenShareResult;
import com.fanap.podchat.call.result_model.CallCancelResult;
import com.fanap.podchat.call.result_model.CallClientErrorsResult;
import com.fanap.podchat.call.result_model.CallCreatedResult;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.GetCallParticipantResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.ping.request.StatusPingRequest;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutNotSeenDurations;
import com.fanap.podchat.model.OutputSignalMessage;
import com.fanap.podchat.model.ResultAddContact;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultClearHistory;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.networking.retrofithelper.TimeoutConfig;
import com.fanap.podchat.notification.CustomNotificationConfig;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.NetworkUtils.NetworkPingSender;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CallPresenter extends ChatAdapter implements CallContract.presenter, Application.ActivityLifecycleCallbacks {

    public static final int CALL_PERMISSION_REQUEST_CODE = 101;

    private String TOKEN = BaseApplication.getInstance().getString(R.string.Farhad_Kheirkhah);
    public static final int SIGNAL_INTERVAL_TIME = 1000;
    public static final int BASE_CALL_TYPE = CallType.Constants.VIDEO_CALL;
    private final int SHARE_SCREEN_PERMISSION_CODE = 107;
    private static final String TAG = "CHAT_SDK_PRESENTER";
    private Chat chat;
    private CallContract.view view;
    private Context context;
    private Activity activity;
    private static final String NOTIFICATION_APPLICATION_ID = "a7ef47ebe966e41b612216b457ccba222a33332de52e948c66708eb4e3a5328f";
    private TokenHandler tokenHandler = null;
    private String state = "";
    ViewGroup.LayoutParams defaultCameraPreviewLayoutParams;


    List<String> callUniqueIds = new ArrayList<>();
    List<String> callimpUniqueIds = new ArrayList<>();

    private CreateCallVO callVO;
    private boolean speakerOn = false;
    private boolean isMute = false;
    private boolean isInCall;
    private boolean isScreenIsSharing;
    private boolean isCallRecording;
    private List<CallPartnerView> remotePartnersViews;
    private CallPartnerView cameraPreview;

    private int cameraId = CameraId.FRONT;

    public CallPresenter(Context context, CallContract.view view, Activity activity) {


        chat = Chat.init(context);

        chat.addListener(this);


        CustomNotificationConfig notificationConfig = new CustomNotificationConfig
                .Builder(ChatActivity.class.getName())
                .setChannelName("POD_CHAT_CHANNEL")
                .setChannelId("PODCHAT")
                .setChannelDescription("Fanap soft podchat notification channel")
                .setIcon(R.mipmap.ic_launcher)
                .setNotificationImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .build();


        chat.setupNotification(notificationConfig);

        chat.isCacheables(false);


        chat.isLoggable(true);
        chat.rawLog(true);
        chat.isSentryLogActive(true);
        chat.isSentryResponseLogActive(true);

        chat.setDownloadDirectory(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));

        TimeoutConfig timeout = new TimeoutConfig()
                .newConfigBuilder()
                .withConnectTimeout(30, TimeUnit.SECONDS)
                .withWriteTimeout(30, TimeUnit.MINUTES)
                .withReadTimeout(30, TimeUnit.MINUTES)
                .build();


        chat.setUploadTimeoutConfig(timeout);

        chat.setDownloadTimeoutConfig(timeout);


        this.activity = activity;
        this.context = context;
        this.view = view;

        activity.getApplication().registerActivityLifecycleCallbacks(this);


        tokenHandler = new TokenHandler(activity, new TokenHandler.ITokenHandler() {
            @Override
            public void onGetToken(String token) {
                connect(token);
            }

            @Override
            public void onTokenRefreshed(String token) {

                if (state.equals(ChatStateType.ChatSateConstant.ASYNC_READY))
                    chat.setToken(token);
                else connect(token);

            }

            @Override
            public void onLoginNeeded() {

                view.onLoginNeeded();
            }

            @Override
            public void onError(String message) {
                view.onError(message);
            }
        });

    }

    @Override
    public void connect() {
        RequestConnect request = new RequestConnect.Builder(
                socketAddress,
                appId,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer,
                podspaceServer
        ).build();

        connect(request);
    }

    public void setupVideoCallParam(CallPartnerView localVideo, List<CallPartnerView> remoteViews) {


        cameraPreview = localVideo;
        this.remotePartnersViews = remoteViews;
        cameraPreview.setPartnerName("You");
        cameraPreview.setDisplayName(true);

        CallConfig callConfig = new CallConfig(CallActivity.class.getName());

        VideoCallParam videoCallParam =
                new VideoCallParam.Builder(localVideo)
                        .setCamWidth(320)
                        .setCamHeight(240)
                        .setCamFPS(15)
                        .setVideoCodecType(VideoCodecType.VIDEO_CODEC_VP8)
                        .setBitrate(90_000)
                        .setCameraId(cameraId)
                        .build();

        AudioCallParam audioCallParam = new AudioCallParam.Builder()
                .setFrameRate(16000)
                .setBitrate(12000)
                .setFrameSize(60)
                .setNumberOfChannels(2)
                .build();

        ScreenShareParam screenShareParam = new ScreenShareParam.Builder()
                .setLowQuality()
                .build();

        checkCallPermissions();

        chat.setupCall(videoCallParam, audioCallParam, screenShareParam, callConfig, remoteViews);

    }

    private void checkCallPermissions() {
        if (CallPermissionHandler.needCameraAndRecordPermission(activity)) {
            CallPermissionHandler.requestPermission(activity, CALL_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void switchCamera() {
        if (cameraId == CameraId.FRONT) {
            chat.switchToBackCamera();
            cameraId = CameraId.BACK;
        } else if (cameraId == CameraId.BACK) {
            chat.switchToFrontCamera();
            cameraId = CameraId.FRONT;
        }
    }

    @Override
    public void pauseVideo() {
        chat.turnOffVideo(callVO != null ? callVO.getCallId() : 0);
    }

    @Override
    public void resumeVideo() {
        chat.openCamera();
        chat.turnOnVideo(callVO != null ? callVO.getCallId() : 0);
    }


    private void connectToMainServer(String token) {

        RequestConnect request = new RequestConnect.Builder(
                mainSocketAddress,
                appId,
                mainServerName,
                token,
                mainSSOHost,
                mainPlatformHost,
                mainFileServer,
                podspaceServer
        ).build();

        connect(request);
    }

    private void connectToSandbox(String token) {

        RequestConnect request = new RequestConnect.Builder(
                sandBoxSocketAddress,
                appId,
                sandBoxServerName,
                token,
                sandBoxSSOHost,
                sandBoxPlatformHost,
                sandBoxFileServer,
                podspaceServer
        ).build();

        connect(request);
    }

    public void connect(RequestConnect requestConnect) {

        NetworkPingSender.NetworkStateConfig build = new NetworkPingSender.NetworkStateConfig()
                .setHostName("msg.pod.ir")
                .setPort(443)
                .setDisConnectionThreshold(2)
                .setInterval(7000)
                .setConnectTimeout(10000)
                .build();


//        TimeoutConfig uploadConfig = new TimeoutConfig()
//                .newConfigBuilder()
//                .withConnectTimeout(120, TimeUnit.MINUTES)
//                .withWriteTimeout(120, TimeUnit.MINUTES)
//                .withReadTimeout(120, TimeUnit.MINUTES)
//                .build();

        TimeoutConfig downloadConfig = new TimeoutConfig()
                .newConfigBuilder()
                .withConnectTimeout(20, TimeUnit.SECONDS)
                .withWriteTimeout(0, TimeUnit.SECONDS)
                .withReadTimeout(5, TimeUnit.MINUTES)
                .build();

        chat.setNetworkStateConfig(build);
//
//        chat.setUploadConfig(uploadConfig);
//
//        chat.setDownloadConfig(downloadConfig);

        chat.connect(requestConnect);

    }


    @Override
    public void onContactSelected(ContactsWrapper contact) {
        view.hideContactsFragment();

        if (isInCall) {
            inviteCallParticipant(contact);
        } else {
            view.updateTvCallee("Calling " + contact.getFirstName() + " " + contact.getLastName());
            view.showFabContact();
            requestP2PCallWithContactId((int) contact.getId());
        }


    }

    private void inviteCallParticipant(ContactsWrapper contact) {

        view.showMessage("Call request sent to " + contact.getFirstName() + " " + contact.getLastName() + " " + contact.getCellphoneNumber());
        RequestAddParticipants request = RequestAddParticipants.newBuilder()
                .threadId(callVO.getThreadId())
                .withContactId(contact.getId())
                .build();
        callUniqueIds.add(chat.addGroupCallParticipant(request));
    }

    @Override
    public void onShareScreenTouched() {
        if (isScreenIsSharing) {
            EndShareScreenRequest request =
                    new EndShareScreenRequest.Builder(callVO.getCallId())
                            .build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isScreenIsSharing = false;
                chat.endShareScreen(request);

            }
        } else if (isInCall) {
            ScreenSharePermissionRequest permissionRequest
                    = new ScreenSharePermissionRequest.Builder(activity)
                    .setPermissionCode(SHARE_SCREEN_PERMISSION_CODE)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chat.requestScreenSharePermission(permissionRequest);
            }

        }
    }

    private void onShareScreenPermissionGranted(Intent data) {

        ScreenShareRequest request = new ScreenShareRequest
                .Builder(data, callVO.getCallId())
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isScreenIsSharing = true;
            chat.startShareScreen(request);
        }

    }

    @Override
    public void getContact(RequestGetContact request) {

        chat.getContacts(request, null);

        StatusPingRequest statusRequest = new StatusPingRequest.Builder()
                .inContactsList()
                .build();

        chat.sendStatusPing(statusRequest);

    }


    @Override
    public void acceptIncomingCall() {


        AcceptCallRequest.Builder request = new AcceptCallRequest.Builder(
                callVO.getCallId());


        if (callVO.getType() == CallType.Constants.VIDEO_CALL) {
            request.withVideo();

            showVideoViews();
        }

        String uniqueId = chat.acceptVoiceCall(request.build());
        callUniqueIds.add(uniqueId);

    }

    @Override
    public void rejectIncomingCall() {

        RejectCallRequest request = new RejectCallRequest.Builder(
                callVO.getCallId())
                .build();

        String uniqueId = chat.rejectVoiceCall(request);
        callUniqueIds.add(uniqueId);

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

//        chat.shouldShowNotification(true);

        chat.closeChat();

    }

    @Override
    public void onResume() {

        chat.resumeChat();
//        chat.shouldShowNotification(false);

    }


    @Override
    public void deliverNotification(String threadId) {
        chat.deliverNotification(threadId);
    }

    @Override
    public void clearNotifications() {
        chat.clearAllNotifications();
    }

    @Override
    public void endStream() {
        chat.endAudioStream();
    }

    @Override
    public void connect(String token) {
        if (serverType == ServerType.SANDBOX)
            connectToSandbox(token);
        else if (serverType == ServerType.MAIN)
            connectToMainServer(token);
        else if (serverType == ServerType.INTEGRATION) {

            connect();
        }
        view.showMessage("Connected to " + serverType.toString());
    }

    @Override
    public void enableAutoRefresh(Activity activity, String entry) {

        if (Util.isNotNullOrEmpty(entry)) {
            tokenHandler.handshake(entry);
        } else {
            view.showMessage("Phone number should start with 09");
        }
    }

    @Override
    public void onLogEvent(String log) {
        view.onLogEvent(log);
    }

    @Override
    public void setToken(String token) {
        chat.setToken(token);
    }

    @Override
    public void getUserInfo(ChatHandler handler) {
        chat.getUserInfo(handler);
    }

    @Override
    public void getContact(Integer count, Long offset, ChatHandler handler) {
        chat.getContacts(count, offset, handler);
    }

    @Override
    public void logOut() {
        tokenHandler.logOut();
        chat.closeChat();
    }

    @Override
    public void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {
    }

    @Override
    public void onGetContacts(String content, ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(content, outPutContact);


        ArrayList<ContactsWrapper> contactsWrappers = new ArrayList<>();

        for (Contact c :
                outPutContact.getResult().getContacts()) {

            ContactsWrapper contactsWrapper = new ContactsWrapper(c);

            contactsWrappers.add(contactsWrapper);
        }

        ContactsFragment fragment = new ContactsFragment();
        Bundle v = new Bundle();


        v.putParcelableArrayList("CONTACTS", contactsWrappers);
        fragment.setArguments(v);


        if (!outPutContact.isCache())
            view.showContactsFragment(fragment);
        else view.updateContactsFragment(contactsWrappers);
    }

    @Override
    public void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {
        view.updateUserInfo(outPutUserInfo.getResult().getUser());
    }

    @Override
    public void onError(String content, ErrorOutPut outPutError) {
        super.onError(content, outPutError);

        if (outPutError.getErrorCode() == 21) {

            if (tokenHandler != null) {
                tokenHandler.refreshToken();
            } else {
                Toast.makeText(context, "Token refresh failed!", Toast.LENGTH_LONG).show();
            }
        }

        if (Util.isNotNullOrEmpty(outPutError.getUniqueId()) && callUniqueIds.contains(outPutError.getUniqueId())) {
            view.onError("");
            view.updateStatus(outPutError.getErrorMessage());

            if (callimpUniqueIds.contains(outPutError.getUniqueId())) {
                view.setInitState();
            }

            callUniqueIds.remove(outPutError.getUniqueId());
            callimpUniqueIds.remove(outPutError.getUniqueId());
        }

    }

    @Override
    public void closeChat() {
//        chat.closeChat();
    }


    @Override
    public void onContactAdded(String content, ChatResponse<ResultAddContact> chatResponse) {
        super.onContactAdded(content, chatResponse);
        view.onAddContact();
    }


    @Override
    public void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        super.onThreadAddParticipant(content, outPutAddParticipant);
        view.onAddParticipant();
    }

    @Override
    public void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {
        super.onThreadRemoveParticipant(content, chatResponse);
        view.onRemoveParticipant();
    }

    @Override
    public void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {
        super.onThreadLeaveParticipant(content, response);
        view.onLeaveThread();
    }


    @Override
    public void onChatProfileUpdated(ChatResponse<ResultUpdateProfile> response) {

        Log.d("CHAT_SDK_PRESENTER", "Chat profile updated");

        view.onChatProfileUpdated(response.getResult());

    }

    @Override
    public void onChatState(String state) {
        this.state = state;

        if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY)) {
            view.updateStatus("Chat is Ready :)");
            getCallHistory();
            view.switchToRecentCallsLoading();
        } else {
            view.updateStatus("Connecting");
        }

    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
        super.onNewMessage(content, chatResponse);

    }


    @Override
    public void OnSeenMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void handleCallbackError(Throwable cause) throws Exception {
        super.handleCallbackError(cause);
    }

    @Override
    public void onGetThreadAdmin(String content, ChatResponse<ResultParticipant> chatResponse) {
        super.onGetThreadAdmin(content, chatResponse);
    }


    @Override
    public void OnNotSeenDuration(OutPutNotSeenDurations resultNotSeen) {
        super.OnNotSeenDuration(resultNotSeen);
    }

    @Override
    public void OnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {
        super.OnClearHistory(content, chatResponse);
    }

    @Override
    public void OnSignalMessageReceive(OutputSignalMessage output) {
        super.OnSignalMessageReceive(output);
    }

    @Override
    public void OnSetRule(ChatResponse<ResultSetAdmin> outputSetRoleToUser) {
        super.OnSetRule(outputSetRoleToUser);
    }

    @Override
    public void onGetThreadParticipant(ChatResponse<ResultParticipant> outPutParticipant) {
        super.onGetThreadParticipant(outPutParticipant);


    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {


    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onReceiveCallRequest(ChatResponse<CallRequestResult> response) {

        if (response.getResult().getType() == CallType.Constants.VOICE_CALL) {

            callVO = response.getResult();

            String callerName = response.getResult().getCreatorVO().getName();

            view.showCallRequest(callerName);

        } else if (response.getResult().getType() == CallType.Constants.VIDEO_CALL) {

            showVideoViews();

            callVO = response.getResult();

            String callerName = response.getResult().getCreatorVO().getName();

            view.showCallRequest(callerName);

        }

    }

    @Override
    public void onReceiveGroupCallRequest(ChatResponse<CallRequestResult> response) {

        if (response.getResult().getType() == CallType.Constants.VOICE_CALL) {

            callVO = response.getResult();

            String callerName = response.getResult().getCreatorVO().getName();

            view.onGroupVoiceCallRequestReceived(callerName, response.getResult().getConversationVO().getTitle(), response.getResult().getConversationVO().getParticipants());

        } else if (response.getResult().getType() == CallType.Constants.VIDEO_CALL) {

            showVideoViews();

            callVO = response.getResult();

            String callerName = response.getResult().getCreatorVO().getName();

            view.onGroupVoiceCallRequestReceived(callerName, response.getResult().getConversationVO().getTitle(), response.getResult().getConversationVO().getParticipants());

        }
    }

    private void showVideoViews() {
        activity.runOnUiThread(() -> {
            try {
//                if (cameraPreview != null) {
//                    cameraPreview.setVisibility(View.VISIBLE);
//                    remotePartnersViews.get(0).setVisibility(View.VISIBLE);
//                    defaultCameraPreviewLayoutParams = cameraPreview.getLayoutParams();
//                    ViewGroup.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//                    callLocalView.setLayoutParams(lp);
//                }
                chat.setPartnerViews(remotePartnersViews);
                chat.openCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void hideVideoViews() {
        activity.runOnUiThread(() -> {
            try {
//                if (cameraPreview != null) {
//                    cameraPreview.setVisibility(View.INVISIBLE);
//                }
                if (remotePartnersViews != null)
                    for (CallPartnerView partnerView :
                            remotePartnersViews) {
                        partnerView.setVisibility(View.GONE);
                        partnerView.reset();
                    }
                chat.closeCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onVoiceCallStarted(ChatResponse<CallStartResult> response) {

        isInCall = true;

        view.onVoiceCallStarted(" " + response.getUniqueId(), "");

    }

    @Override
    public void onActiveCallParticipantsReceived(ChatResponse<GetCallParticipantResult> response) {


          activity.runOnUiThread(()->{
              for (CallParticipantVO cp :
                      response.getResult().getCallParticipantVOS()) {
                  try {
                      CallPartnerView pw = findParticipantView(cp.getUserId());
                      pw.setPartnerName(cp.getParticipantVO().getContactName());
                      pw.setDisplayName(true);
                  }catch (Exception ex){
                      ex.printStackTrace();
                  }

              }
          });



    }

    @Override
    public void onVoiceCallEnded(ChatResponse<EndCallResult> response) {

        isInCall = false;
        if (isScreenIsSharing) {
            EndShareScreenRequest endShareReq =
                    new EndShareScreenRequest.Builder(response.getResult().getCallId())
                            .build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chat.endShareScreen(endShareReq);
                isScreenIsSharing = false;
            }
        }


        hideVideoViews();

        view.onVoiceCallEnded(response.getUniqueId(), response.getResult().getCallId());

    }

    @Override
    public void onCallRequestRejected(ChatResponse<CallRequestResult> response) {

        callVO = response.getResult();
        String callerName = "Unnamed contact";
        hideVideoViews();
        view.setInitState();
        view.showMessage(callerName + " rejected your call");
    }

    @Override
    public void onReceiveCallHistory(ChatResponse<GetCallHistoryResult> response) {


        List<CallVO> calls = new ArrayList<>();

        for (CallVO call :
                response.getResult().getCallsList()) {

            if (call.getPartnerParticipantVO() != null)
                calls.add(call);

        }


        view.onGetCallHistory(calls);

    }

    @Override
    public void terminateCall() {

        isInCall = false;

        Log.e(TAG, "REQUEST TERMINATE FROM CLIENT");

        Log.e(TAG, "REQUEST TERMINATE FROM CLIENT. Call Response: " + callVO);

        if (callVO != null) {

            Log.e(TAG, "REQUEST TERMINATE FROM CLIENT call response not null");

            TerminateCallRequest terminateCallRequest = new TerminateCallRequest.Builder()
                    .setCallId(callVO.getCallId())
                    .build();

            hideVideoViews();

            String uniqueId = chat.terminateCall(terminateCallRequest);

        }

    }

    @Override
    public void endRunningCall() {


        if (isInCall) {

            isInCall = false;

            Log.e(TAG, "REQUEST END CALL FROM CLIENT");

            Log.e(TAG, "REQUEST END CALL FROM CLIENT. Call Response: " + callVO);

            if (callVO != null) {

                Log.e(TAG, "REQUEST END CALL FROM CLIENT call response not null");

                EndCallRequest endCallRequest = new EndCallRequest.Builder()
                        .setCallId(callVO.getCallId())
                        .build();


                String uniqueId = chat.endCall(endCallRequest);
                callUniqueIds.add(uniqueId);

                stopScreenShare();

            }

        } else {

            Log.e(TAG, "REQUEST Cancel CALL FROM CLIENT");

            Log.e(TAG, "REQUEST Cancell CALL FROM CLIENT. Call: " + callVO);

            if (callVO != null) {

                Log.e(TAG, "REQUEST Cancel CALL FROM CLIENT call response not null");

                RejectCallRequest endCallRequest = new RejectCallRequest.Builder(
                        callVO.getCallId())
                        .build();


                String uniqueId = chat.rejectVoiceCall(endCallRequest);
                callUniqueIds.add(uniqueId);

            }
        }
        hideVideoViews();
    }

    private void stopScreenShare() {
        if (isScreenIsSharing) {
            EndShareScreenRequest request =
                    new EndShareScreenRequest.Builder(callVO.getCallId())
                            .build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isScreenIsSharing = false;
                chat.endShareScreen(request);
            }
        }
    }


    private void getCallHistory() {

        GetCallHistoryRequest request = new GetCallHistoryRequest.Builder()
                .setType(BASE_CALL_TYPE)
                .count(50)
                .build();

        callUniqueIds.add(chat.getCallsHistory(request));

    }

    @Override
    public void switchMute() {

        this.isMute = !this.isMute;

        Log.d(TAG, "CHANGE MUTE: " + isMute);

        if (isInCall) {

//            ArrayList<Long> ids = new ArrayList<>();
//            ids.add((long) Masoud_ID);
//            ids.add((long) Pooria_ID);

//            ids.add(1L);
//            MuteUnMuteCallParticipantRequest request
//                    = new MuteUnMuteCallParticipantRequest.Builder(callVO.getCallId(), ids)
//                    .build();

//            if (isMute)
//                chat.requestMuteCallParticipant(request);
//            else
//                chat.requestUnMuteCallParticipant(request);
            callUniqueIds.add(chat.switchCallMuteState(isMute, callVO.getCallId()));
        } else {
            // send mute state in AcceptCallRequest
        }

    }

    @Override
    public void switchSpeaker() {

        this.speakerOn = !this.speakerOn;

        Log.d(TAG, "CHANGE SPEAKER: " + speakerOn);

        chat.switchCallSpeakerState(speakerOn);

    }


    @Override
    public void getContact() {

        if (chat.isChatReady()) {
            view.hideFabContactButton();
            RequestGetContact request =
                    new RequestGetContact.Builder()
                            .count(50)
                            .offset(0)
                            .build();

            callUniqueIds.add(chat.getContacts(request, null));
        }


    }

    @Override
    public void addCallParticipant() {


        getContact();

//        if (Util.isNotNullOrEmpty(username)) {
//
//            if (username.contains(",")) {
//                String[] names = username.split(",");
//                RequestAddParticipants request = RequestAddParticipants.newBuilder()
//                        .threadId(callVO.getThreadId())
//                        .withUserNames(names)
//                        .build();
//                callUniqueIds.add(chat.addGroupCallParticipant(request));
//            } else {
//                RequestAddParticipants request = RequestAddParticipants.newBuilder()
//                        .threadId(callVO.getThreadId())
//                        .withUserNames(username)
//                        .build();
//                callUniqueIds.add(chat.addGroupCallParticipant(request));
//            }
//
//        } else {
//            List<Long> ids = new ArrayList<>();
//
//            if (pooriaChecked)
//                ids.add((long) Pooria_ID);
//            if (masoudChecked)
//                ids.add((long) Masoud_ID);
//            if (farhadChecked)
//                ids.add((long) Farhad_ID);
//
//            RequestAddParticipants request = RequestAddParticipants.newBuilder()
//                    .threadId(callVO.getThreadId())
//                    .withCoreUserIds(ids)
//                    .build();
//
//
//            callUniqueIds.add(chat.addGroupCallParticipant(request));
//
//
//        }

    }

    @Override
    public void setCallInfo(CallInfo callInfo) {

        if (callVO == null) {
            isInCall = true;
            callVO = new CreateCallVO();
            callVO.setCallId(callInfo.getCallId());
        }


    }

    @Override
    public void requestMainOrSandboxCall(String query, boolean isGroupCall) {

        try {
            if (Util.isNotNullOrEmpty(query)) {

                showVideoViews();

                String uniqueId = "";


                if (query.contains("-")) {

                    String[] ids = query.split("-");

                    List<Invitee> invitees = new ArrayList<>();
                    if (ids.length == 1) {
                        // P2P Call

                        Invitee invitee = new Invitee();
                        invitee.setId(ids[0]);
                        invitee.setIdType(InviteType.Constants.TO_BE_USER_ID);
                        invitees.add(invitee);

                        //request with invitee list
                        CallRequest callRequest = new CallRequest.Builder(
                                invitees,
                                BASE_CALL_TYPE).build();
                        uniqueId = chat.requestCall(callRequest);
                        view.updateStatus("Request p2p call with: " + ids[0]);

                    } else if (ids.length <= 5) {
                        //Group Call with invitees

                        for (String id :
                                ids) {

                            Invitee invitee = new Invitee();
                            invitee.setId(id);
                            invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
                            invitees.add(invitee);

                        }

                        CallRequest callRequest = new CallRequest.Builder(
                                invitees,
                                BASE_CALL_TYPE).build();
                        uniqueId = chat.requestGroupCall(callRequest);

                        view.updateStatus("Request group call invitees: " + Arrays.toString(ids));
                    } else {
                        view.updateStatus("حداکثر اعضای تماس گروهی 5 نفر می باشد");
                    }
                } else {
                    //Group Call with subject id

                    CallRequest callRequest = new CallRequest.Builder(
                            Long.parseLong(query),
                            BASE_CALL_TYPE).build();
                    if (isGroupCall) {
                        uniqueId = chat.requestGroupCall(callRequest);
                    } else {
                        uniqueId = chat.requestCall(callRequest);
                    }

                }

                if (Util.isNotNullOrEmpty(uniqueId)) {

                    callUniqueIds.add(uniqueId);
                    callimpUniqueIds.add(uniqueId);
                }

            }
        } catch (NumberFormatException e) {
            view.updateStatus("Wrong format");
        }

    }

    @Override
    public void requestP2PCallWithP2PThreadId(int threadId) {

        //request with threadId
        CallRequest callRequest = new CallRequest.Builder(
                threadId,
                BASE_CALL_TYPE).build();

        if (callRequest.getCallType() == BASE_CALL_TYPE) {
            showVideoViews();
        }

        String uniqueId = chat.requestCall(callRequest);
        callUniqueIds.add(uniqueId);
        callimpUniqueIds.add(uniqueId);
    }

    @Override
    public void requestP2PCallWithContactId(int contactId) {
        List<Invitee> invitees = new ArrayList<>();
        Invitee invitee = new Invitee();
        invitee.setId(contactId);
        invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
        invitees.add(invitee);


        //request with invitee list
        CallRequest callRequest = new CallRequest.Builder(
                invitees,
                BASE_CALL_TYPE).build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        }

        String uniqueId;
        if (serverType == ServerType.SANDBOX) {
            uniqueId = chat.requestGroupCall(callRequest);
        } else {
            uniqueId = chat.requestCall(callRequest);
        }
        callUniqueIds.add(uniqueId);
        callimpUniqueIds.add(uniqueId);
    }

    @Override
    public void requestP2PCallWithUserId(int userId) {
        List<Invitee> invitees = new ArrayList<>();
        Invitee invitee = new Invitee();
        invitee.setId(userId);
        invitee.setIdType(InviteType.Constants.TO_BE_USER_ID);
        invitees.add(invitee);


        //request with invitee list
        CallRequest callRequest = new CallRequest.Builder(
                invitees,
                BASE_CALL_TYPE).build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        }

        String uniqueId = chat.requestCall(callRequest);
        callUniqueIds.add(uniqueId);
        callimpUniqueIds.add(uniqueId);
    }

    @Override
    public void onCallReconnect(ChatResponse<CallReconnectResult> response) {
        view.onCallReconnect(response.getResult().getCallId());
    }

    @Override
    public void onCallConnect(ChatResponse<CallReconnectResult> response) {
        view.onCallConnect(response.getResult().getCallId());
    }

    @Override
    public void onCallDelivered(ChatResponse<CallDeliverResult> response) {
        view.onCallDelivered(response.getResult());
    }


    @Override
    public void onCallParticipantLeft(ChatResponse<LeaveCallResult> response) {


        for (CallParticipantVO c :
                response.getResult().getCallParticipants()) {
            view.onCallParticipantLeft(c.getParticipantVO().getFirstName() + " " + c.getParticipantVO().getLastName());
        }
        try {
            CallPartnerView pw = findParticipantView(response.getResult().getCallParticipants().get(0).getUserId());
            if (pw != null)
                chat.addPartnerView(pw, 0);
        } catch (Exception e) {
            view.onError(e.getMessage());
        }


    }

    @Override
    public void onCallParticipantJoined(ChatResponse<JoinCallParticipantResult> response) {
        for (CallParticipantVO callParticipant :
                response.getResult().getJoinedParticipants()) {

            try {
               activity.runOnUiThread(()->{
                   CallPartnerView pw = findParticipantView(callParticipant.getUserId());
                   pw.setDisplayName(true);
                   pw.setPartnerName(callParticipant.getParticipantVO().getName());
               });
            }catch (Exception ignored){}

            view.onCallParticipantJoined(callParticipant.getParticipantVO().getFirstName() + " " + callParticipant.getParticipantVO().getLastName());
        }
    }

    @Override
    public void onEndCallRequestFromNotification() {
        isInCall = false;
        view.onVoiceCallEnded("", 0);
        stopScreenShare();
    }

    @Override
    public void onCallParticipantRemoved(ChatResponse<RemoveFromCallResult> response) {

        for (CallParticipantVO callParticipant :
                response.getResult().getCallParticipants()) {
            view.onCallParticipantRemoved(callParticipant.getParticipantVO().getFirstName() + " " + callParticipant.getParticipantVO().getLastName());
        }

    }

    @Override
    public void onRemovedFromCall(ChatResponse<RemoveFromCallResult> response) {

        hideVideoViews();
        view.onRemovedFromCall();
    }

    @Override
    public void onThreadClosed(ChatResponse<CloseThreadResult> response) {
        view.onThreadClosed(response.getSubjectId());
    }

    @Override
    public void onCallCreated(ChatResponse<CallCreatedResult> response) {

        callVO = response.getResult();

        view.onCallCreated(response.getResult().getCallId());
    }

    @Override
    public void onAudioCallUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        view.audioCallUnMuted();
    }

    @Override
    public void onCallParticipantUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (CallParticipantVO participant :
                response.getResult().getCallParticipants()) {

            Long userId = participant.getUserId();
            CallPartnerView partnerView = null;
            if (userId != null) {
                partnerView = findParticipantView(userId);
            }
            view.callParticipantUnMuted(participant, partnerView);
        }
    }

    @Override
    public void onUnMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        view.audioCallUnMutedByAdmin();
    }

    @Override
    public void onAudioCallMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        view.audioCallMuted();
    }

    @Override
    public void onMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        view.audioCallMutedByAdmin();
    }

    @Override
    public void onCallParticipantMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (CallParticipantVO participant :
                response.getResult().getCallParticipants()) {
            Long userId = participant.getUserId();
            CallPartnerView partnerView = null;
            if (userId != null) {
                partnerView = findParticipantView(userId);
            }
            view.callParticipantMuted(participant, partnerView);
        }
    }

    private CallPartnerView findParticipantView(Long userId) {
        CallPartnerView lpw = null;
        for (CallPartnerView partnerView :
                remotePartnersViews) {
            if (partnerView.getPartnerId() != null && partnerView.getPartnerId().equals(userId))
                lpw = partnerView;
        }
        return lpw;
    }

    @Override
    public void onCallParticipantStoppedVideo(ChatResponse<JoinCallParticipantResult> response) {
        try {
            CallPartnerView pw = findParticipantView(response.getResult().getJoinedParticipants().get(0).getUserId());
           activity.runOnUiThread(()->{
               if (pw != null){
                   pw.setPartnerName("");
                   pw.setPartnerId(0L);
                   pw.reset();
                   chat.addPartnerView(pw, 0);
               }
           });
        } catch (Exception e) {
            view.onError(e.getMessage());
        }

    }

    @Override
    public void onCallParticipantStartedVideo(ChatResponse<JoinCallParticipantResult> response) {
        try {
            view.showMessage(response.getResult().getJoinedParticipants().get(0).getParticipantVO().getName() + " has video now!");
        } catch (Exception e) {
            view.onError(e.getMessage());
        }
    }

    @Override
    public void onCallParticipantCanceledCall(ChatResponse<CallCancelResult> response) {
        view.callParticipantCanceledCall(response.getResult().getCallParticipant().getParticipantVO().getFirstName()
                + " " + response.getResult().getCallParticipant().getParticipantVO().getLastName());
    }

    @Override
    public void onAnotherDeviceAcceptedCall() {
        view.hideCallRequest();
        view.showMessage("Call accepted from another device");
    }

    @Override
    public void onScreenShareStarted(ChatResponse<ScreenShareResult> response) {
        view.onScreenIsSharing();
    }

    @Override
    public void onScreenShareEnded(ChatResponse<ScreenShareResult> response) {
        view.onScreenShareEnded();
    }

    @Override
    public void onCallParticipantSharedScreen(ChatResponse<ScreenShareResult> response) {
        view.onCallParticipantSharedScreen();
    }

    @Override
    public void onCallParticipantStoppedScreenSharing(ChatResponse<ScreenShareResult> response) {
        view.onCallParticipantStoppedScreenSharing();
    }

    @Override
    public void onRecordButtonTouched() {

        if (callVO != null && isInCall) {
            StartOrEndCallRecordRequest request =
                    new StartOrEndCallRecordRequest.Builder(callVO.getCallId())
                            .build();

            if (isCallRecording) {
                chat.endCallRecord(request);
            } else {
                chat.startCallRecord(request);
            }
        }

    }


    @Override
    public void onCallRecordStarted(ChatResponse<Participant> response) {
        isCallRecording = true;
        view.onCallRecordingStarted();
    }

    @Override
    public void onCallRecordEnded(ChatResponse<Participant> response) {
        isCallRecording = false;
        view.onCallRecordingStopped();
    }

    @Override
    public void onCallParticipantRecordStarted(ChatResponse<Participant> response) {
        view.onParticipantStartedRecordingCall(" " + response.getResult().getFirstName() + " " + response.getResult().getLastName());
    }

    @Override
    public void onCallParticipantRecordStopped(ChatResponse<Participant> response) {
        view.onParticipantStoppedRecordingCall(" " + response.getResult().getFirstName() + " " + response.getResult().getLastName());
    }

    @Override
    public void onCallClientErrors(ChatResponse<CallClientErrorsResult> response) {
        CallErrorVO error = response.getResult().getCallErrorVO();
        Participant cp = error.getParticipantVo();
        view.showMessage(cp.getName() + " " + error.getMessage());
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case SHARE_SCREEN_PERMISSION_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    onShareScreenPermissionGranted(data);
                }
                break;
            }

            case CALL_PERMISSION_REQUEST_CODE: {
                if (resultCode != Activity.RESULT_OK) {
                    view.updateStatus("Unable to make call without permissions");
                }
                break;
            }
        }
    }

    @Override
    public void onActivityPaused() {
//        if(isInCall){
//
//        }
//        presenter.rejectIncomingCall();
//        onCallEnded();
    }
}
