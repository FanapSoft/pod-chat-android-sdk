package com.example.chat.application.chatexample;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.application.chatexample.token.TokenHandler;
import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.video.codec.VideoCodecType;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.call.CallConfig;
import com.fanap.podchat.call.constants.CallType;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.model.CreateCallVO;
import com.fanap.podchat.call.request_model.AcceptCallRequest;
import com.fanap.podchat.call.request_model.CallRequest;
import com.fanap.podchat.call.request_model.EndCallRequest;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.call.request_model.RejectCallRequest;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.result_model.CallCancelResult;
import com.fanap.podchat.call.result_model.CallCreatedResult;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.bot.result_model.StartStopBotResult;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.ping.request.StatusPingRequest;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.thread.request.CloseThreadRequest;
import com.fanap.podchat.chat.thread.request.SafeLeaveRequest;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutNotSeenDurations;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutputSignalMessage;
import com.fanap.podchat.model.ResultAddContact;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultBlockList;
import com.fanap.podchat.model.ResultClearHistory;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultMute;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultRemoveContact;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.networking.retrofithelper.TimeoutConfig;
import com.fanap.podchat.notification.CustomNotificationConfig;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestEditMessage;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetFile;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetImage;
import com.fanap.podchat.requestobject.RequestGetLastSeens;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetPodSpaceImage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
import com.fanap.podchat.requestobject.RequestUnBlock;
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.fanap.podchat.requestobject.RequestUploadFile;
import com.fanap.podchat.requestobject.RequestUploadImage;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.NetworkUtils.NetworkPingSender;
import com.fanap.podchat.util.RequestMapSearch;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.chat.application.chatexample.CallActivity.Farhad_ID;
import static com.example.chat.application.chatexample.CallActivity.MASOUD_CID;
import static com.example.chat.application.chatexample.CallActivity.Masoud_ID;
import static com.example.chat.application.chatexample.CallActivity.POORIA_CID;
import static com.example.chat.application.chatexample.CallActivity.Pooria_ID;


public class CallPresenter extends ChatAdapter implements CallContract.presenter, Application.ActivityLifecycleCallbacks {

    public static final int SIGNAL_INTERVAL_TIME = 1000;
    private static final String TAG = "CHAT_SDK_PRESENTER";
    private final Enum<ServerType> serverType;
    private Chat chat;
    private CallContract.view view;
    private Context context;
    private Activity activity;
    private static final String NOTIFICATION_APPLICATION_ID = "a7ef47ebe966e41b612216b457ccba222a33332de52e948c66708eb4e3a5328f";
    private TokenHandler tokenHandler = null;
    private String state = "";
    ViewGroup.
            LayoutParams defaultCameraPreviewLayoutParams;


    List<String> callUniqueIds = new ArrayList<>();
    List<String> callimpUniqueIds = new ArrayList<>();

    private CreateCallVO callVO;
    private boolean speakerOn = false;
    private boolean isMute = false;
    private boolean isInCall;
    private List<CallPartnerView> remotePartnersViews;
    private CallPartnerView cameraPreview;

    public CallPresenter(Context context, CallContract.view view, Activity activity, Enum<ServerType> serverType) {


        this.serverType = serverType;

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
                view.onGetToken(token);
            }

            @Override
            public void onTokenRefreshed(String token) {

                if (state.equals(ChatStateType.ChatSateConstant.ASYNC_READY))
                    chat.setToken(token);
                else view.onGetToken(token);

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
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost, String fileServer, String typeCode) {

    }

    public void setupVideoCallParam(CallPartnerView localVideo, List<CallPartnerView> remoteViews) {


        cameraPreview = localVideo;
        this.remotePartnersViews = remoteViews;

        CallConfig callConfig = new CallConfig(CallActivity.class.getName());

        VideoCallParam videoCallParam =
                new VideoCallParam.Builder(localVideo)
                        .setCamWidth(320)
                        .setCamHeight(240)
                        .setCamFPS(30)
                        .setVideoCodecType(VideoCodecType.VIDEO_CODEC_VP8)
                        .setBitrate(90_000)
                        .build();

        AudioCallParam audioCallParam = new AudioCallParam.Builder()
                .setFrameRate(16000)
                .setBitrate(12000)
                .setFrameSize(60)
                .setNumberOfChannels(2)
                .build();

        chat.setupCall(videoCallParam, audioCallParam, callConfig, remoteViews);

    }

    @Override
    public void switchCamera() {
        chat.switchCamera();
    }

    @Override
    public void pauseVideo() {
        chat.turnOffVideo(callVO != null ? callVO.getCallId() : 0);
    }

    @Override
    public void resumeVideo() {
        chat.turnOnVideo(callVO != null ? callVO.getCallId() : 0);
    }


    @Override
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

        if (true) {
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
    public String getNameById(int partnerId) {
        return getCallerName(partnerId);
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
    public void enableAutoRefresh(Activity activity, String entry) {


        if (Util.isNotNullOrEmpty(entry)) {
            if (entry.startsWith("09")) {
                tokenHandler.handshake(entry);
            } else if (entry.trim().startsWith("*")) {
                view.onGetToken(entry.replace("*", ""));
            } else {
                tokenHandler.verifyNumber(entry);
            }
        }


//        ArrayList<String> scopes = new ArrayList<>();
//
//        scopes.add("profile");
//
//        PodOtp otp = new PodOtp.Builder()
//                .with(activity.getApplicationContext())
//                .setBaseUrl("https://accounts.pod.ir/")
//                .build();
//
//
//        otp.authorization("09157770684", new AuthorizationCallback() {
//
//            @Override
//            public void onSuccess(String s, Long aLong) {
//                Log.e("OTP", ">>> " + s);
//
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                Log.e("OTP", ">>> " + i + " >> " + s);
//
//            }
//        });
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
        view.onGetUserInfo(outPutUserInfo);
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

        view.onState(state);

        this.state = state;

        if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY)) {


//            List<String> usernames = new ArrayList<>();
//        usernames.add("leila.nemati");
//            usernames.add("pooria.pahlevani");
//        usernames.add("nadia.anvari");
//        usernames.add("p.khoshghadam");
//        usernames.add("m.hasanpour");
//        usernames.add("z.ershad");
//        usernames.add("Samira.amiri");
//        usernames.add("s.heydarizadeh");
//        usernames.add("p.pahlavani");
//            usernames.add("09379520706");
//            usernames.add("09124704905");

//            chat.getContacts(new RequestGetContact.Builder()
//                    .count(50).offset(0).build(), null);
//            for (int i = 0; i < usernames.size(); i++) {
//
//
//                try {
//                    String name;
//                    String family;
//                    if (i == 0) {
//                        name = "Masoud";
//                        family = "Alavi";
//                    } else {
//                        name = "Ms";
//                        family = "HeidariZadeh";
//                    }
//                    RequestAddContact request = new RequestAddContact.Builder()
//                            .firstName("")
//                            .lastName("")
//                            .cellphoneNumber("")
//                            .build();
//
//                    chat.addContact(request);
//
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//            }


        }
//        if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY)) {
//
//
//            getThreadParticipant(new RequestThreadParticipant.Builder().threadId(6952).build());
//
//        }

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

            view.onVoiceCallRequestReceived(callerName);

        } else if (response.getResult().getType() == CallType.Constants.VIDEO_CALL) {

            showVideoViews();

            callVO = response.getResult();

            String callerName = response.getResult().getCreatorVO().getName();

            view.onVoiceCallRequestReceived(callerName);

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
                if (cameraPreview != null) {
                    cameraPreview.setVisibility(View.VISIBLE);
                    remotePartnersViews.get(0).setVisibility(View.VISIBLE);
                    defaultCameraPreviewLayoutParams = cameraPreview.getLayoutParams();
//                    ViewGroup.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//                    callLocalView.setLayoutParams(lp);
                }
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
                if (cameraPreview != null) {
                    cameraPreview.setVisibility(View.INVISIBLE);
                }
                if (remotePartnersViews != null)
                    for (CallPartnerView partnerView :
                            remotePartnersViews) {
                        partnerView.setVisibility(View.INVISIBLE);
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

        activity.runOnUiThread(() -> {
            if (cameraPreview != null) {

//                cameraPreview.setLayoutParams(defaultCameraPreviewLayoutParams);

                cameraPreview.getSurfaceView().setZOrderOnTop(true);
                cameraPreview.getSurfaceView().setZOrderMediaOverlay(true);

                remotePartnersViews.get(0).setVisibility(View.VISIBLE);
                remotePartnersViews.get(0).getSurfaceView().setZOrderOnTop(false);
                remotePartnersViews.get(0).getSurfaceView().setZOrderMediaOverlay(false);
            }
        });

        view.onVoiceCallStarted(" " + response.getUniqueId(), "");

    }

    @Override
    public void onVoiceCallEnded(ChatResponse<EndCallResult> response) {

        isInCall = false;

        hideVideoViews();

        view.onVoiceCallEnded(response.getUniqueId(), response.getResult().getCallId());

    }

    public String getCallerName(long callerId) {
        String callerName = "";
        if (callerId == CallActivity.Farhad_ID) {

            callerName = "ZIZI";
        } else if (callerId == Pooria_ID) {
            callerName = "FIFI";
        } else {
            callerName = "JIJI";
        }
        return callerName;
    }

    @Override
    public void onCallRequestRejected(ChatResponse<CallRequestResult> response) {

//        if (response.getResult().getType() == CallType.Constants.VOICE_CALL) {

        callVO = response.getResult();

        long callerId = response.getResult().getCreatorId();

        String callerName = getCallerName(callerId);

        hideVideoViews();

        view.onVoiceCallRequestRejected(callerName);

//        }

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

        Log.e(TAG, "REQUEST TERMINATE FROM CLIENT");

        Log.e(TAG, "REQUEST TERMINATE FROM CLIENT. Call Response: " + callVO);

        if (callVO != null) {

            Log.e(TAG, "REQUEST TERMINATE FROM CLIENT call response not null");

            TerminateCallRequest terminateCallRequest = new TerminateCallRequest.Builder()
                    .setCallId(callVO.getCallId())
                    .build();

            hideVideoViews();

            String uniqueId = chat.terminateAudioCall(terminateCallRequest);

        }

    }

    @Override
    public void endRunningCall() {


        if (isInCall) {

            Log.e(TAG, "REQUEST END CALL FROM CLIENT");

            Log.e(TAG, "REQUEST END CALL FROM CLIENT. Call Response: " + callVO);

            if (callVO != null) {

                Log.e(TAG, "REQUEST END CALL FROM CLIENT call response not null");

                EndCallRequest endCallRequest = new EndCallRequest.Builder()
                        .setCallId(callVO.getCallId())
                        .build();


                String uniqueId = chat.endAudioCall(endCallRequest);
                callUniqueIds.add(uniqueId);

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

    @Override
    public void getCallHistory() {


        GetCallHistoryRequest request = new GetCallHistoryRequest.Builder()
                .setType(CallType.Constants.VIDEO_CALL)
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


            chat.switchCallMuteState(isMute, callVO.getCallId());
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
    public void requestGroupCall(boolean withFifi, boolean withZizi, boolean withJiji) {

        List<Invitee> invitees = new ArrayList<>();

        Invitee invitee;

        if (withFifi) {
            invitee = new Invitee();
            invitee.setId(POORIA_CID);
            invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
            invitees.add(invitee);
        }
//        if (withZizi) {
//            invitee = new Invitee();
//            invitee.setId(JIJI_CID);
//            invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
//            invitees.add(invitee);
//        }
        if (withJiji) {
            invitee = new Invitee();
            invitee.setId(MASOUD_CID);
            invitee.setIdType(InviteType.Constants.TO_BE_USER_CONTACT_ID);
            invitees.add(invitee);
        }

        CallRequest request = new CallRequest
//                .Builder(invitees,CallType.Constants.VOICE_CALL)
                .Builder(35311, CallType.Constants.VIDEO_CALL)
                .build();

        showVideoViews();

        String uniqueId = chat.requestGroupCall(request);
        callUniqueIds.add(uniqueId);
        callimpUniqueIds.add(uniqueId);


    }

    @Override
    public void removeCallParticipant(String etId, boolean fifiChecked, boolean jijiChecked, boolean ziziChecked) {

        List<Long> ids = new ArrayList<>();

        if (etId.isEmpty()) {
            if (fifiChecked)
                ids.add((long) Pooria_ID);
            if (jijiChecked)
                ids.add((long) Masoud_ID);
            if (ziziChecked)
                ids.add((long) Farhad_ID);
        } else {
            try {
                ids.add(Long.parseLong(etId));
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid Id", Toast.LENGTH_SHORT).show();
            }
        }


        RemoveParticipantRequest request = new RemoveParticipantRequest.Builder(
                callVO.getCallId(),
                ids)
                .build();


        chat.removeGroupCallParticipant(request);

    }

    @Override
    public void getContact() {

        if (chat.isChatReady()) {
            view.onLoadingContactsStarted();
            RequestGetContact request =
                    new RequestGetContact.Builder()
                            .count(50)
                            .offset(0)
                            .build();

            callUniqueIds.add(chat.getContacts(request, null));
        }


    }

    @Override
    public void addCallParticipant(String username, boolean pooriaChecked, boolean masoudChecked, boolean farhadChecked) {


        if (Util.isNotNullOrEmpty(username)) {

            if (username.contains(",")) {
                String[] names = username.split(",");
                RequestAddParticipants request = RequestAddParticipants.newBuilder()
                        .threadId(callVO.getThreadId())
                        .withUserNames(names)
                        .build();
                callUniqueIds.add(chat.addGroupCallParticipant(request));
            } else {
                RequestAddParticipants request = RequestAddParticipants.newBuilder()
                        .threadId(callVO.getThreadId())
                        .withUserNames(username)
                        .build();
                callUniqueIds.add(chat.addGroupCallParticipant(request));
            }

        } else {
            List<Long> ids = new ArrayList<>();

            if (pooriaChecked)
                ids.add((long) Pooria_ID);
            if (masoudChecked)
                ids.add((long) Masoud_ID);
            if (farhadChecked)
                ids.add((long) Farhad_ID);

            RequestAddParticipants request = RequestAddParticipants.newBuilder()
                    .threadId(callVO.getThreadId())
                    .withCoreUserIds(ids)
                    .build();


            callUniqueIds.add(chat.addGroupCallParticipant(request));


        }

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
                                CallType.Constants.VIDEO_CALL).build();
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
                                CallType.Constants.VIDEO_CALL).build();
                        uniqueId = chat.requestGroupCall(callRequest);

                        view.updateStatus("Request group call invitees: " + Arrays.toString(ids));
                    } else {
                        view.updateStatus("حداکثر اعضای تماس گروهی 5 نفر می باشد");
                    }
                } else {
                    //Group Call with subject id

                    CallRequest callRequest = new CallRequest.Builder(
                            Long.parseLong(query),
                            CallType.Constants.VIDEO_CALL).build();
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
                CallType.Constants.VIDEO_CALL).build();

        if (callRequest.getCallType() == CallType.Constants.VIDEO_CALL) {
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
                CallType.Constants.VIDEO_CALL).build();

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
                CallType.Constants.VIDEO_CALL).build();

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

    }

    @Override
    public void onCallParticipantJoined(ChatResponse<JoinCallParticipantResult> response) {
        for (CallParticipantVO callParticipant :
                response.getResult().getJoinedParticipants()) {
            view.onCallParticipantJoined(callParticipant.getParticipantVO().getFirstName() + " " + callParticipant.getParticipantVO().getLastName());
        }
    }

    @Override
    public void onEndCallRequestFromNotification() {
        view.onVoiceCallEnded("", 0);
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
            view.callParticipantUnMuted(participant);
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
            view.callParticipantMuted(participant);
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
    }

}