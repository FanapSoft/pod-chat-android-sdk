package com.example.chat.application.chatexample;


import static com.example.chat.application.chatexample.ServerConfig.*;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
import com.fanap.podchat.call.history.CallWrapper;
import com.fanap.podchat.call.model.CallErrorVO;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.model.CreateCallVO;
import com.fanap.podchat.call.recording.CallRecordTracer;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetActiveCallsRequest;
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
import com.fanap.podchat.call.result_model.GetActiveCallsResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.GetCallParticipantResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.chat.CallPartnerViewManager;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.MainThreadExecutor;
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
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.NetworkUtils.NetworkPingSender;
import com.fanap.podchat.util.TextMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class CallPresenter extends ChatAdapter implements CallContract.presenter, Application.ActivityLifecycleCallbacks, CallPartnerViewManager.IAutoGenerate {

    public static final int CALL_PERMISSION_REQUEST_CODE = 101;
    public static final String GROUP_CALL_NAME_PREFIX =  "تماس گروهی ";

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
    List<String> callImpUniqueIds = new ArrayList<>();

    private CreateCallVO callVO;
    private boolean speakerOn = false;
    private boolean isMute = false;
    private boolean isCameraOn = false;
    private boolean isInCall;
    private boolean isScreenIsSharing;
    private boolean isCallRecording;
    private List<CallPartnerView> remotePartnersViews;
    private CallPartnerView cameraPreview;
    private int cameraId = CameraId.FRONT;
    @Nullable
    CallPartnerViewManager cpvManager;

    private ArrayList<CallWrapper> callsList = new ArrayList<>();


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
                        .setCamWidth(720) // FIXME: 3/12/2022 fix updating params after call is started
                        .setCamHeight(480)
                        .setCamFPS(30)
                        .setVideoCodecType(VideoCodecType.VIDEO_CODEC_VP8)
                        .setBitrate(50_000)
                        .setCameraId(cameraId)
                        .build();

        AudioCallParam audioCallParam = new AudioCallParam.Builder()
                .setFrameRate(8000) //8000
                .setBitrate(8000) //8000
                .setFrameSize(60)
                .setNumberOfChannels(1) //1
                .build();

        ScreenShareParam screenShareParam = new ScreenShareParam.Builder()
                .setLowQuality()
                .build();

        checkCallPermissions();

        chat.setupCall(videoCallParam, audioCallParam, screenShareParam, callConfig);

        cpvManager = chat.useCallPartnerViewManager();

        cpvManager.addView(remoteViews);

        cpvManager.setAsCameraPreview(cameraPreview);

        cpvManager.setAsScreenShareView(remoteViews.get(remoteViews.size() - 1));

        cpvManager.setAutoGenerateCallback(this);

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
    public void onContactSelected(ContactsWrapper contact, int callType) {
        view.hideContactsFragment();

        if (isInCall) {
            inviteCallParticipant(contact);
        } else {
            String name = getValidName(contact);
            view.updateTvCallee("داریم به " + name + " درخواست تماس می‌فرستیم...");
            view.showFabContact();
            requestP2PCallWithContactId((int) contact.getId(), callType,contact.getFirstName() + " " + contact.getLastName());
        }


    }




    private void inviteCallParticipant(ContactsWrapper contact) {

        String name = contact.getLinkedUser() != null ? contact.getLinkedUser().getName() : "" + contact.getFirstName() + " " + contact.getLinkedUser();
        view.showMessage("" + name + " به تماس دعوت شد");
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
    public void acceptIncomingCallWithVideo() {


        AcceptCallRequest.Builder request = new AcceptCallRequest.Builder(
                callVO.getCallId());
        request.withVideo();
        showVideoViews();
        String uniqueId = chat.acceptVoiceCall(request.build());
        callUniqueIds.add(uniqueId);

    }

    @Override
    public void acceptIncomingCallWithAudio() {

        AcceptCallRequest.Builder request = new AcceptCallRequest.Builder(
                callVO.getCallId());
        hideLocalCameraPreview();
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
        getActiveCalls();
    }

    @Override
    public void rejectIncomingCallWithMessage(String msg) {

        RejectCallRequest request = new RejectCallRequest.Builder(
                callVO.getCallId())
                .build();

        String uniqueId = chat.rejectVoiceCall(request);
        callUniqueIds.add(uniqueId);

        RequestMessage requestRejectMessage = new RequestMessage
                .Builder("الان نمی‌تونم جواب بدم", callVO.getConversationVO().getId())
                .messageType(TextMessageType.Constants.TEXT)
                .jsonMetaData(new GsonBuilder().create().toJson("{\"callRejectWithMessage\":true}"))
                .build();
        callUniqueIds.add(chat.sendTextMessage(requestRejectMessage, null));
        getActiveCalls();
    }

    @Override
    public void turnOnCamera() {
        showLocalCameraPreview();
        if (callVO.getCallId() > 0) {
            chat.turnOnVideo(callVO.getCallId());
        } else {
            view.showMessage("تماسی برقرار نیست!");
        }
    }

    @Override
    public void turnOffCamera() {
        hideLocalCameraPreview();
        if (callVO.getCallId() > 0) {
            chat.turnOffVideo(callVO.getCallId());
        } else {
            view.showMessage("تماسی برقرار نیست!");
        }
    }

    @Override
    public void prepareNewView(CallPartnerView partnerView) {
        view.addNewView(partnerView);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        chat.closeChat();
    }

    @Override
    public void onResume() {
        chat.resumeChat();
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
            view.showMessage("شماره صحیح نیست! ☹");
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
                view.showMessage("لطفا دوباره وارد بشید");
            }
        }

        if (Util.isNotNullOrEmpty(outPutError.getUniqueId()) && callUniqueIds.contains(outPutError.getUniqueId())) {
            view.onError("خطا در برقراری تماس 😓");
            view.updateStatus(outPutError.getErrorMessage() + " 😨");

            if (callImpUniqueIds.contains(outPutError.getUniqueId())) {
                view.setInitState();
            }

            callUniqueIds.remove(outPutError.getUniqueId());
            callImpUniqueIds.remove(outPutError.getUniqueId());
        }

    }

    @Override
    public void closeChat() {
//        chat.closeChat();
    }


    @Override
    public void onContactAdded(String content, ChatResponse<ResultAddContact> chatResponse) {
        super.onContactAdded(content, chatResponse);
        Contact c = chatResponse.getResult().getContact();

        view.showMessage(c.getFirstName() + " " + c.getLastName() + " " + c.getCellphoneNumber() + " " + c.getEmail()
        + " " + (c.getLinkedUser()!=null?c.getLinkedUser().getName() + " " + c.getLinkedUser().getUsername():"") + " اضافه شد");
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
            view.updateStatus("وصل شدیم ❤");
            getCallHistory();
            getActiveCalls();
            view.switchToRecentCallsLoading();
        } else {
            view.updateStatus("داریم وصل می‌شیم... 😍");
        }

    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {


        if (CallRecordTracer.isRecordedFileMessage(chatResponse.getUniqueId())) {

            try {
                view.showMessage("تماس در مسیر زیر ذخیره شد: \n " + chatResponse.getResult().getMessageVO().getMetadata());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        //This is reject message from contact
        if (!callUniqueIds.contains(chatResponse.getUniqueId())) {
            if (Util.isNotNullOrEmpty(chatResponse.getResult().getMessageVO().getMessage())) {
                if (chatResponse.getResult().getMessageVO().getCallHistoryVO() == null) {

                    String message = chatResponse.getResult().getMessageVO().getMessage();
                    Participant participant = chatResponse.getResult().getMessageVO().getParticipant();

                    view.updateStatus(participant.getName() + " گفت: " + message);
                    view.showMessage(" ✉ " + participant.getName() + " : " + message);
                }

            }
        } else {
            callUniqueIds.remove(chatResponse.getUniqueId());
        }
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
        String callerName = showCallRequest(response);
        view.updateTvCaller("تماس از " + callerName);
    }

    @Override
    public void onReceiveGroupCallRequest(ChatResponse<CallRequestResult> response) {
        String callerName = showCallRequest(response);
        view.updateTvCaller(" تماس " + callerName + " از " + response.getResult().getConversationVO().getTitle());
    }

    private String showCallRequest(ChatResponse<CallRequestResult> response) {
        callVO = response.getResult();
        String callerImage = response.getResult().getCreatorVO().getImage();
        String callerName = response.getResult().getCreatorVO().getName();
        if (Util.isNotNullOrEmpty(callerImage))
            view.updateCallerImage(callerImage);
        if (response.getResult().getType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        } else {
            view.hideRemoteViews();
            view.hideCameraPreview();
            if (Util.isNotNullOrEmpty(callerImage))
                view.updateCallImage(callerImage);
        }

        view.showCallRequest(callerName);
        return callerName;
    }

    private void showVideoViews() {
        view.showVideoCallElements();
        updatePartnerViewList();
        showLocalCameraPreview();
    }

    private void updatePartnerViewList() {
        chat.setPartnerViews(remotePartnersViews);
    }

    private void hideVideoViews() {
        hideAllRemotePartners();
        hideLocalCameraPreview();
    }

    private void showLocalCameraPreview() {
        activity.runOnUiThread(() -> {
            view.showCameraPreview();
            chat.openCamera();
            isCameraOn = true;
        });
    }

    private void hideLocalCameraPreview() {
        chat.closeCamera();
        isCameraOn = false;
        view.hideCameraPreview();
    }

    private void hideAllRemotePartners() {
        activity.runOnUiThread(() -> {
            if (remotePartnersViews != null)
                for (CallPartnerView partnerView :
                        remotePartnersViews) {
                    partnerView.setVisibility(View.GONE);
                    partnerView.reset();
                }
        });
    }

    @Override
    public void onVoiceCallStarted(ChatResponse<CallStartResult> response) {

        isInCall = true;

        if (Util.isNotNullOrEmpty(response.getResult().getCallPartners())) {

            if (response.getResult().getCallPartners().size() == 1) {
                CallParticipantVO rp = response.getResult().getCallPartners().get(0);
                if (!rp.hasVideo()) {
                    view.hideRemoteViews();
                } else {
                    view.showRemoteViews();
                }
            }
        }

        view.onVoiceCallStarted(" " + response.getUniqueId(), "");

    }

    @Override
    public void onActiveCallParticipantsReceived(ChatResponse<GetCallParticipantResult> response) {
        setNameOnView(response.getResult().getCallParticipantVOS());
    }

    private void setNameOnView(List<CallParticipantVO> callParticipantVOArrayList) {
        try {
            activity.runOnUiThread(() -> {
                for (CallParticipantVO cp :
                        callParticipantVOArrayList) {
                    if (cp.hasVideo()) {
                        if (cpvManager != null) {
                            cpvManager.showPartnerName(cp.getUserId(), cp.getParticipantVO().getContactName());
                        } else {
                            CallPartnerView pw = findParticipantView(cp.getUserId());
                            pw.setPartnerName(cp.getParticipantVO().getContactName());
                            pw.setDisplayName(true);
                        }

                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onVoiceCallEnded(ChatResponse<EndCallResult> response) {

        isInCall = false;
        isCallRecording = false;
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
        removeActiveCallIfExist(response.getResult().getCallId());
        getCallHistory();

    }

    private void removeActiveCallIfExist(long endedCallId) {
        CallWrapper endedCallInList = null;
        for (CallWrapper callItem :
                callsList) {
            if(callItem.getId() == endedCallId){
                endedCallInList = callItem;
                break;
            }
        }
        if(endedCallInList!=null){
            view.removeCallItem(endedCallInList);
            endedCallInList.setCallItemType(CallWrapper.CallItemType.HISTORY);
            view.onGetCallHistory(Collections.singletonList(endedCallInList));
        }

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


        List<CallWrapper> calls = new ArrayList<>();

        for (CallVO call :
                response.getResult().getCallsList()) {

            if (call.getPartnerParticipantVO() != null || call.getConversationVO() != null){
                CallWrapper callWrapper = CallWrapper.fromCall(call);
                callWrapper.setCallItemType(CallWrapper.CallItemType.HISTORY);
                calls.add(callWrapper);
            }


        }

        if(callsList.size()>0){
            calls.removeAll(callsList);
        }

        callsList.addAll(calls);
        view.onGetCallHistory(calls);

    }

    @Override
    public void onReceiveActiveCalls(ChatResponse<GetActiveCallsResult> response) {
        List<CallWrapper> calls = new ArrayList<>();

        for (CallVO call :
                response.getResult().getCallsList()) {

            if (call.getPartnerParticipantVO() != null || call.getConversationVO() != null){
                CallWrapper callWrapper = CallWrapper.fromCall(call);
                callWrapper.setCallItemType(CallWrapper.CallItemType.ACTIVE);
                calls.add(callWrapper);
            }

        }

        calls.removeAll(callsList);

        callsList.addAll(calls);

        if (calls.size() > 0) {
            view.onGetActiveCalls(calls);
        }

    }

    @Override
    public void terminateCall() {

        isInCall = false;
        isCallRecording = false;

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
            isCallRecording = false;

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
        getActiveCalls();
    }


    @Override
    public void addContact(String name, String lastName, String id, int idType) {

        view.hideContactsFragment();
        view.showFabContact();

        switch (idType){
            case EditorInfo.TYPE_CLASS_PHONE:{
                chat.addContact(name,lastName,id,null,"default",null);
                break;
            }
            case EditorInfo.TYPE_CLASS_TEXT:{
                chat.addContact(name,lastName,null,null,"default",id);
                break;
            }
            case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:{
                chat.addContact(name,lastName,null,id,"default",null);
                break;
            }
        }


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

    private void getActiveCalls() {

        GetActiveCallsRequest request = new GetActiveCallsRequest.Builder()
                .setType(BASE_CALL_TYPE)
                .count(50)
                .build();

        callUniqueIds.add(chat.getActiveCalls(request));

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
    }

    @Override
    public void setCallInfo(CallInfo callInfo) {
        if (callVO == null) {
            isInCall = true;
            callVO = new CreateCallVO();
            callVO.setCallId(callInfo.getCallId());
        }
    }


    private void prepareAudioCallView() {
        view.hideCameraPreview();
        view.hideRemoteViews();
    }

    private String getValidName(ContactsWrapper contact) {

        String name;

        if (Util.isNotNullOrEmpty(contact.getLinkedUser().getName())) {
            name = contact.getLinkedUser().getName();
        } else if (Util.isNotNullOrEmpty(contact.getFirstName()) && Util.isNotNullOrEmpty(contact.getLastName())) {
            name = contact.getFirstName() + " " + contact.getLastName();
        } else {
            name = contact.getCellphoneNumber();
        }

        return name;
    }

    private String getValidName(CallVO call) {

        if (call.getPartnerParticipantVO() != null) {
            if (Util.isNotNullOrEmpty(call.getPartnerParticipantVO().getContactName()))
                return call.getPartnerParticipantVO().getContactName();
            if (Util.isNotNullOrEmpty(call.getPartnerParticipantVO().getCellphoneNumber()))
                return call.getPartnerParticipantVO().getCellphoneNumber();
        } else if (call.getConversationVO() != null) {
            return call.getConversationVO().getTitle();
        }

        return "ناشناس👽";
    }


    @Override
    public void requestAudioCall(CallWrapper call) {

        if (chat.isChatReady()) {
            if(call.getCallItemType() == CallWrapper.CallItemType.ACTIVE){
                initCallVoByCallWrapper(call);
                acceptIncomingCallWithAudio();
                callsList.remove(call);
                view.removeCallItem(call);
            }else requestP2PCall(call, CallType.Constants.VOICE_CALL);
        } else {
            view.showMessage("Chat is not ready...");
        }

    }


    @Override
    public void requestVideoCall(CallWrapper call) {
        if (chat.isChatReady()) {
            if(call.getCallItemType() == CallWrapper.CallItemType.ACTIVE){
                initCallVoByCallWrapper(call);
                acceptIncomingCallWithVideo();
                callsList.remove(call);
                view.removeCallItem(call);
            }else requestP2PCall(call, CallType.Constants.VIDEO_CALL);
        } else {
            view.showMessage("Chat is not ready...");
        }

    }

    private void initCallVoByCallWrapper(CallWrapper call) {

            callVO = new CreateCallVO();
            callVO.setCallId(call.getId());
            callVO.setGroup(call.isGroup());
            callVO.setConversationVO(call.getConversationVO());
            callVO.setCreatorId(call.getCreatorId());
            callVO.setType(call.getType());


    }

    private void requestP2PCall(CallVO call, int callType) {
        view.updateTvCallee("داریم به " + getValidName(call) + " درخواست تماس می‌فرستیم...");

        if (call.getPartnerParticipantVO() != null) {
            int cId = (int) call.getPartnerParticipantVO().getContactId();
            int uId = (int) call.getPartnerParticipantVO().getId();
            if (cId > 0) {
                requestP2PCallWithContactId(cId, callType,call.getPartnerParticipantVO().getName());

            } else if (uId > 0) {
                requestP2PCallWithUserId(uId, callType);
            } else {
                view.updateStatus("نمی‌تونیم با ایشون تماس بگیریم! 😔");
            }
        } else {
            int tId = (int) call.getConversationVO().getId();
            if (tId > 0) {
                requestGroupCallWithThreadId(tId, callType);
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestP2PCallWithUserId(int userId, int callType) {
        List<Invitee> invitees = new ArrayList<>();
        Invitee invitee = new Invitee();
        invitee.setId(userId);
        invitee.setIdType(InviteType.Constants.TO_BE_USER_ID);
        invitees.add(invitee);


        //request with invitee list
        CallRequest callRequest = new CallRequest.Builder(
                invitees,
                callType).build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        } else {
            prepareAudioCallView();
        }

        String uniqueId = chat.requestCall(callRequest);
        callUniqueIds.add(uniqueId);
        callImpUniqueIds.add(uniqueId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestP2PCallWithContactId(int contactId, int callType) {
        List<Invitee> invitees = new ArrayList<>();
        Invitee invitee = new Invitee();
        invitee.setId(contactId);
        invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
        invitees.add(invitee);


        //request with invitee list
        CallRequest callRequest = new CallRequest.Builder(
                invitees,
                callType)
                .setTitle(GROUP_CALL_NAME_PREFIX + contactId)
                .setDescription("Generated at "+new Date().toString())
                .build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        } else {
            prepareAudioCallView();
        }

        String uniqueId;
        uniqueId = requestCallByServerType(callRequest);
        callUniqueIds.add(uniqueId);
        callImpUniqueIds.add(uniqueId);
    }

    @SuppressLint("MissingPermission")
    private String requestCallByServerType(CallRequest callRequest) {
        String uniqueId;
        if (serverType == ServerType.SANDBOX) {
            uniqueId = chat.requestCall(callRequest);
        } else {
            uniqueId = chat.requestCall(callRequest);
        }
        return uniqueId;
    }

    private void requestP2PCallWithContactId(int contactId, int callType, String contactName) {
        List<Invitee> invitees = new ArrayList<>();
        Invitee invitee = new Invitee();
        invitee.setId(contactId);
        invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
        invitees.add(invitee);


        //request with invitee list
        CallRequest callRequest = new CallRequest.Builder(
                invitees,
                callType)
                .setTitle(GROUP_CALL_NAME_PREFIX + contactName)
                .setDescription("Generated at "+new Date().toString())
                .build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        } else {
            prepareAudioCallView();
        }

        String uniqueId = requestCallByServerType(callRequest);
        callUniqueIds.add(uniqueId);
        callImpUniqueIds.add(uniqueId);
    }

    private void requestGroupCallWithThreadId(int threadId, int callType) {

        CallRequest callRequest = new CallRequest.Builder(
                threadId,
                callType)
                .setTitle(GROUP_CALL_NAME_PREFIX + " " + threadId)
                .setDescription("Generated at "+new Date().toString())
                .build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        } else {
            prepareAudioCallView();
        }
        @SuppressLint("MissingPermission") String uniqueId = chat.requestGroupCall(callRequest);
        callUniqueIds.add(uniqueId);
        callImpUniqueIds.add(uniqueId);

    }

    //Not using yet
    @Override
    public void requestP2PCallWithP2PThreadId(int threadId) {

        //request with threadId
        CallRequest callRequest = new CallRequest.Builder(
                threadId,
                BASE_CALL_TYPE).build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
            showVideoViews();
        }

        @SuppressLint("MissingPermission") String uniqueId = chat.requestCall(callRequest);
        callUniqueIds.add(uniqueId);
        callImpUniqueIds.add(uniqueId);
    }

    @SuppressLint("MissingPermission")
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
                                BASE_CALL_TYPE)
                                .setTitle(GROUP_CALL_NAME_PREFIX + ids)
                                .setDescription("Generated at "+new Date().toString())
                                .build();
                        uniqueId = chat.requestGroupCall(callRequest);

                        view.updateStatus("Request group call invitees: " + Arrays.toString(ids));
                    } else {
                        view.updateStatus("حداکثر اعضای تماس گروهی 5 نفر می باشد");
                    }
                } else {
                    //Group Call with subject id

                    CallRequest callRequest = new CallRequest.Builder(
                            Long.parseLong(query),
                            BASE_CALL_TYPE)
                            .setTitle(GROUP_CALL_NAME_PREFIX + query)
                            .setDescription("Generated at "+new Date().toString())
                            .build();
                    if (isGroupCall) {
                        uniqueId = chat.requestGroupCall(callRequest);
                    } else {
                        uniqueId = chat.requestCall(callRequest);
                    }

                }

                if (Util.isNotNullOrEmpty(uniqueId)) {

                    callUniqueIds.add(uniqueId);
                    callImpUniqueIds.add(uniqueId);
                }

            }
        } catch (NumberFormatException e) {
            view.updateStatus("Wrong format");
        }

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
        try {
            view.updateTvCallee("درحال زنگ خوردنه... ");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        view.onCallDelivered(response.getResult());
    }


    @Override
    public void onCallParticipantLeft(ChatResponse<LeaveCallResult> response) {

        for (CallParticipantVO c :
                response.getResult().getCallParticipants()) {
            view.showMessage(c.getParticipantVO().getName() + " تماس رو ترک کرد");
        }
        activity.runOnUiThread(() -> {
            try {
                if (cpvManager != null) {
                    cpvManager.releasePartnerView(
                            response.getResult()
                                    .getCallParticipants().get(0).getUserId());

                } else {
                    CallPartnerView pw = findParticipantView(response.getResult().getCallParticipants().get(0).getUserId());
                    if (pw != null) {
                        pw.setId(0);
                        pw.setPartnerName("");
                        pw.setDisplayName(false);
                        pw.setDisplayIsMuteIcon(false);
                        pw.setDisplayCameraIsOffIcon(false);
                        pw.reset();
                        chat.addPartnerView(pw, 0);
                    }
                }

            } catch (Exception e) {
                view.onError(e.getMessage());
            }

        });

    }

    @Override
    public void onCallParticipantJoined(ChatResponse<JoinCallParticipantResult> response) {
        for (CallParticipantVO callParticipant :
                response.getResult().getJoinedParticipants()) {

            try {
                activity.runOnUiThread(() -> {
                    if (cpvManager != null) {
                        cpvManager.showPartnerName(callParticipant.getUserId(), callParticipant.getParticipantVO().getName());
                        return;
                    }
                    CallPartnerView pw = findParticipantView(callParticipant.getUserId());
                    pw.setDisplayName(true);
                    pw.setPartnerName(callParticipant.getParticipantVO().getName());
                });
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            view.showMessage(callParticipant.getParticipantVO().getName() + " وارد تماس شد!");
        }
    }

    @Override
    public void onEndCallRequestFromNotification() {
        isInCall = false;
        isCallRecording = false;
        view.onVoiceCallEnded("", 0);
        stopScreenShare();
        getCallHistory();
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
            if (cpvManager != null) {
                activity.runOnUiThread(() -> cpvManager.hideMuteIcon(userId));
            } else {
                CallPartnerView partnerView = null;
                if (userId != null) {
                    partnerView = findParticipantView(userId);
                }
                String name = Util.isNotNullOrEmpty(participant.getParticipantVO().getContactName()) ? participant.getParticipantVO().getContactName() : participant.getParticipantVO().getName();
                view.showMessage(name + " میکروفنش رو باز کرد 😍");
                view.callParticipantUnMuted(participant, partnerView);
            }
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
            if (cpvManager != null) {
                activity.runOnUiThread(() -> cpvManager.showMuteIcon(userId));
            } else {
                CallPartnerView partnerView = null;
                if (userId != null) {
                    partnerView = findParticipantView(userId);
                }
                String name = Util.isNotNullOrEmpty(participant.getParticipantVO().getContactName()) ? participant.getParticipantVO().getContactName() : participant.getParticipantVO().getName();
                view.showMessage(name + " میکروفنش رو بست 🤐");
                view.callParticipantMuted(participant, partnerView);
            }
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
            view.showMessage(response.getResult().getJoinedParticipants().get(0).getParticipantVO().getName() + " دوربینش رو بست! ");
            if (cpvManager != null) {
                activity.runOnUiThread(() -> cpvManager.releasePartnerView(
                        response.getResult().getJoinedParticipants().get(0).getUserId()));
                return;
            }
            CallPartnerView pw = findParticipantView(response.getResult().getJoinedParticipants().get(0).getUserId());
            activity.runOnUiThread(() -> {
                if (pw != null) {
                    pw.setPartnerName("");
                    pw.setPartnerId(0L);
                    pw.reset();
                    pw.setVisibility(View.GONE);
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
            if (response.getResult().getJoinedParticipants().get(0).getParticipantVO().getId() == remotePartnersViews.get(0).getPartnerId()) {
                view.showRemoteViews();
            }
            view.showMessage(response.getResult().getJoinedParticipants().get(0).getParticipantVO().getName() + " الان تصویر داره!");
            setNameOnView(response.getResult().getJoinedParticipants());
        } catch (Exception e) {
            view.onError(e.getMessage());
        }
    }

    @Override
    public void onCallParticipantCanceledCall(ChatResponse<CallCancelResult> response) {
        view.showMessage(response.getResult().getCallParticipant().getParticipantVO().getName() + " تماس رو کنسل کرد");
    }

    @Override
    public void onAnotherDeviceAcceptedCall() {
        view.hideCallRequest();
        view.showMessage(" با حساب دیگه‌اتون وارد تماس شدید!");
    }

    @Override
    public void onScreenShareStarted(ChatResponse<ScreenShareResult> response) {
        view.showMessage(" صفحه نمایش‌اتون در حال اشتراک گذاری با اعضای تماسه!");
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
        if (cpvManager != null) {
            MainThreadExecutor exec = new MainThreadExecutor();
            exec.execute(() -> cpvManager.releaseScreenShareView());
        } else
            view.onCallParticipantStoppedScreenSharing();
    }

    @Override
    public void onRecordButtonTouched() {

        if (callVO != null && isInCall) {
            StartOrEndCallRecordRequest request =
                    new StartOrEndCallRecordRequest.Builder(callVO.getCallId())
                            .build();

            if (isCallRecording) {
                CallRecordTracer.requestUniqueId = chat.endCallRecord(request);
            } else {
                chat.startCallRecord(request);
            }
            isCallRecording = !isCallRecording;
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
        view.showMessage(" " + response.getResult().getName() + " در حال ضبط تماس است...");
    }

    @Override
    public void onCallParticipantRecordStopped(ChatResponse<Participant> response) {
        CallRecordTracer.ongoingRecordUniqueId = response.getUniqueId();
        view.showMessage(" " + response.getResult().getName() + " ضبط تماس را متوقف کرد.");

    }

    @Override
    public void onCallClientErrors(ChatResponse<CallClientErrorsResult> response) {
        CallErrorVO error = response.getResult().getCallErrorVO();
        Participant cp = error.getParticipant();
        if (error.getCode() == ChatConstant.ERROR_CODE_CAMERA_NOT_AVAILABLE) {
            view.showMessage("دوربین " + cp.getName() + " قابل استفاده نیست!");
        }
        if (error.getCode() == ChatConstant.ERROR_CODE_MICROPHONE_NOT_AVAILABLE) {
            view.showMessage("میکروفن " + cp.getName() + " قابل استفاده نیست!");

        }

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
                    view.updateStatus("بدون اجازه دسترسی، نمی‌تونیم‌ تماس برقرار کنیم 😵");
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

    @Override
    public void onNewViewGenerated(CallPartnerView callPartnerView) {
        view.addNewView(callPartnerView);
    }
}
