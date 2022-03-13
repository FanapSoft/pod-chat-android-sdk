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
import android.widget.Toast;

import com.example.chat.application.chatexample.token.TokenHandler;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.BlockUnblockAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantHistoryRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetBlockedAssistantsRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.GetUserBotsRequest;
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
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.GetTagListRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagListResult;
import com.fanap.podchat.chat.tag.result_model.TagParticipantResult;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.thread.request.ChangeThreadTypeRequest;
import com.fanap.podchat.chat.thread.request.CloseThreadRequest;
import com.fanap.podchat.chat.thread.request.GetMutualGroupRequest;
import com.fanap.podchat.chat.thread.request.SafeLeaveRequest;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.chat.messge.SearchSystemMetadataRequest;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.Thread;
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
import com.fanap.podchat.requestobject.RequestRemoveContact;
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
import com.fanap.podchat.util.NetworkUtils.NetworkPingSender;
import com.fanap.podchat.util.RequestMapSearch;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ChatPresenter extends ChatAdapter implements ChatContract.presenter, Application.ActivityLifecycleCallbacks {

    public static final int SIGNAL_INTERVAL_TIME = 1000;
    private static final String TAG = "CHAT_SDK_PRESENTER";
    private Chat chat;
    private ChatContract.view view;
    private Context context;
    private Activity activity;
    private static final String NOTIFICATION_APPLICATION_ID = "a7ef47ebe966e41b612216b457ccba222a33332de52e948c66708eb4e3a5328f";
    private TokenHandler tokenHandler = null;
    private String state = "";


    List<String> uniqueIds = new ArrayList<>();

    public ChatPresenter(Context context, ChatContract.view view, Activity activity) {


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



        chat.isCacheables(true);


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


//        chat.setNetworkListenerEnabling(false);
//        chat.setReconnectOnClose(false);
//        chat.setExpireAmount(180);
        this.activity = activity;
        this.context = context;
        this.view = view;

        activity.getApplication().registerActivityLifecycleCallbacks(this);


        tokenHandler = new TokenHandler(activity.getApplicationContext(),new TokenHandler.ITokenHandler() {
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
    public void onTagCreated(String content, ChatResponse<TagResult> response) {
        Log.e(TAG, content);
        view.onTagCreated(response.getResult());
    }

    @Override
    public void onTagEdited(String content, ChatResponse<TagResult> response) {
        Log.e(TAG, content);
    }

    @Override
    public void onTagDeleted(String content, ChatResponse<TagResult> response) {
        Log.e(TAG, content);
    }

    @Override
    public void OnTagParticipantAdded(String content, ChatResponse<TagParticipantResult> response) {
        Log.e(TAG, content);
    }

    @Override
    public void OnTagParticipantRemoved(String content, ChatResponse<TagParticipantResult> response) {
        Log.e(TAG, content);
    }

    @Override
    public void OnTagList(String content, ChatResponse<TagListResult> response) {
        String x = response.getJson();
        Log.e(TAG, x);
    }

    @Override
    public void getSentryLogs() {
//        view.onGetSentryLogs(chat.getSenrtyLogs());
    }

    @Override
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost, String fileServer, String typeCode) {

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
    public void checkIsNameAvailable(RequestCheckIsNameAvailable request) {

        chat.isNameAvailable(request);
    }

    @Override
    public void createPublicThread(RequestCreatePublicThread request) {

        chat.createThread(request);
    }

    @Override
    public void joinPublicThread(RequestJoinPublicThread request) {
        chat.joinPublicThread(request);
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
    public void getBlockList(RequestBlockList request) {
        chat.getBlockList(request, null);
    }

    @Override
    public void getThreadParticipant(RequestThreadParticipant request) {
        chat.getThreadParticipants(request, null);
    }

    @Override
    public void shareLogs() {
        if (chat != null) {
            chat.shareLogs(context);
        }
    }

    @Override
    public String downloadFile(RequestGetPodSpaceFile rePod, ProgressHandler.IDownloadFile iDownloadFile) {

        Log.e(TAG, "isInCache=" + chat.isAvailableInCache(rePod));

        return chat.getFile(rePod, iDownloadFile);
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
    public String downloadFile(RequestGetPodSpaceImage rePod, ProgressHandler.IDownloadFile iDownloadFile) {

        Log.e(TAG, "isInCache=" + chat.isAvailableInCache(rePod));

        return chat.getImage(rePod, iDownloadFile);

    }

    @Override
    public String updateThreadInfo(RequestThreadInfo request) {
        return chat.updateThreadInfo(request, null);
    }

    @Override
    public String createThread(RequestCreateThread requestCreateThread) {
        return chat.createThread(requestCreateThread);
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
    public void defineBotCommand(DefineBotCommandRequest request) {
        String uniqueId = chat.addBotCommand(request);
    }

    @Override
    public void createBot(CreateBotRequest request) {
        String uniqueId = chat.createBot(request);
    }

    @Override
    public void startBot(StartAndStopBotRequest request) {
        String uniqueId = chat.startBot(request);
    }

    @Override
    public void stopBot(StartAndStopBotRequest request) {
        String uniqueId = chat.stopBot(request);
    }

    @Override
    public void getUserBots(GetUserBotsRequest request) {
        chat.getUserBots(request);
    }

    @Override
    public void onBotCreated(ChatResponse<CreateBotResult> response) {
        view.onBotCreated(response);
    }

    @Override
    public void onRegisterAssistant(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onRegisterAssistant: " + response.getJson());
    }

    @Override
    public void onDeActiveAssistant(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onDeActiveAssistant: " + response.getJson());
    }

    @Override
    public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onGetAssistants: " + response.getJson());
    }

    @Override
    public void onGetAssistantHistory(ChatResponse<List<AssistantHistoryVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onGetAssistants: " + response.getJson());
    }

    @Override
    public void onAssistantBlocked(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onAssistantBlocked: " + response.getJson());
    }

    @Override
    public void onAssistantUnBlocked(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onAssistantUnBlocked: " + response.getJson());
    }

    @Override
    public void onAssistantBlocks(ChatResponse<List<AssistantVo>> response) {
        String json = response.getJson();
        Log.e(TAG, "onAssistantBlocks: " + response.getJson());
    }

    @Override
    public void onBotCommandsDefined(ChatResponse<DefineBotCommandResult> response) {
        view.onBotCommandsDefined(response);
    }


    @Override
    public void onBotStopped(ChatResponse<StartStopBotResult> response) {
        view.onBotStopped(response.getResult().getBotName());
    }

    @Override
    public void onBotStarted(ChatResponse<StartStopBotResult> response) {
        view.onBotStarted(response.getResult().getBotName());

    }

    @Override
    public void enableAutoRefresh(Activity activity, String entry) {


        if (entry.startsWith("09")) {
            tokenHandler.handshake(entry);
        } else if (entry.trim().startsWith("*")) {
            view.onGetToken(entry.replace("*", ""));
        } else {
            tokenHandler.verifyNumber(entry);
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
    public void sendLocationMessage(RequestLocationMessage request) {
        chat.sendLocationMessage(request);
    }

    @Override
    public void sendLocationMessage(RequestLocationMessage requestLocationMessage, ProgressHandler.sendFileMessage handler) {

        chat.sendLocationMessage(requestLocationMessage, handler);
    }

    @Override
    public void onLogEvent(String log) {
        view.onLogEvent(log);
    }


    @Override
    public void searchMap(String searchTerm, double lat, double lon) {


        RequestMapSearch request = new RequestMapSearch
                .Builder(searchTerm, lat, lon)
                .build();

        chat.mapSearch(request);
    }

    @Override
    public void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler) {
        chat.retryUpload(retry, handler);
    }

    @Override
    public void resendMessage(String uniqueId) {
        chat.resendMessage(uniqueId);
    }

    @Override
    public void cancelMessage(String uniqueId) {
        chat.cancelMessage(uniqueId);
    }

    @Override
    public void retryUpload(String uniqueId) {

    }


    @Override
    public void cancelUpload(String uniqueId) {
        chat.cancelUpload(uniqueId);
    }

    @Override
    public void getSeenMessageList(RequestSeenMessageList requestParams) {

        chat.getMessageSeenList(requestParams);

    }

    @Override
    public void deliveredMessageList(RequestDeliveredMessageList requestParams) {

        chat.getMessageDeliveredList(requestParams);

    }

    @Override
    public void createThreadWithMessage(RequestCreateThread threadRequest) {

        chat.createThreadWithMessage(threadRequest);
    }

    @Override
    public String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String metadata) {


        return chat.createThread(threadType, invitee, threadTitle, description, image, metadata, null);
    }

    @Override
    public void getThreads(RequestThread requestThread, ChatHandler handler) {

        chat.getThreads(requestThread, handler);


        StatusPingRequest statusRequest = new StatusPingRequest.Builder()
                .inChat()
                .build();

        chat.sendStatusPing(statusRequest);

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException ignored) {}
//
//        RequestGetUnreadMessagesCount req = new RequestGetUnreadMessagesCount
//                .Builder()
//                .withMuteThreads()
//                .build();
//
//        chat.getAllUnreadMessagesCount(req);
    }

    @Override
    public void getThreads(Integer count,
                           Long offset,
                           ArrayList<Integer> threadIds,
                           String threadName,
                           long creatorCoreUserId,
                           long partnerCoreUserId,
                           long partnerCoreContactId,
                           ChatHandler handler) {

        RequestThread request = new RequestThread.Builder()
                .count(count)
                .offset(offset)
                .threadIds(threadIds)
                .threadName(threadName)
                .creatorCoreUserId(creatorCoreUserId)
                .partnerCoreContactId(partnerCoreContactId)
                .partnerCoreContactId(partnerCoreContactId)
                .build();


        chat.getThreads(request, handler);
    }

    @Override
    public void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, boolean isNew, ChatHandler handler) {


        RequestThread request = new RequestThread.Builder()
                .count(count)
                .offset(offset)
                .threadIds(threadIds)
                .threadName(threadName)
                .creatorCoreUserId(0)
                .partnerCoreContactId(0)
                .partnerCoreContactId(0)
                .build();

        chat.getThreads(request, handler);
    }


    @Override
    public void setToken(String token) {
        chat.setToken(token);
    }


    @Override
    public void mapSearch(String searchTerm, Double latitude, Double longitude) {
        chat.mapSearch(searchTerm, latitude, longitude);
    }

    @Override
    public void mapRouting(String origin, String originLng) {
        chat.mapRouting(origin, originLng);
    }

    @Override
    public void mapStaticImage(RequestMapStaticImage request) {
        chat.mapStaticImage(request);
    }

    @Override
    public void mapReverse(RequestMapReverse request) {
        chat.mapReverse(request);
    }


    @Override
    public void getUserInfo(ChatHandler handler) {

        chat.getUserInfo(handler);
    }

    @Override
    public void getHistory(History history, long threadId, ChatHandler handler) {

//
//        RequestGetHistory request = new RequestGetHistory.Builder()
//                .threadId(history.getId())
//                .build();
//
//
//
//
//        String uniq = chat.getHistory(history, handler);
//        if (uniq != null) {
//            Log.i("un", uniq);
//        }


    }

    @Override
    public String getHistory(RequestGetHistory request, ChatHandler handler) {

        String uniqueId = chat.getHistory(request, handler);


        StatusPingRequest statusRequest = new StatusPingRequest.Builder()
                .inThread()
                .setThreadId(request.getThreadId())
                .build();

        chat.sendStatusPing(statusRequest);

        return uniqueId;
    }

    @Override
    public String getHashTagList(RequestGetHashTagList request, ChatHandler handler) {
        return chat.getHashTagList(request);
    }

    @Override
    public void searchHistory(SearchSystemMetadataRequest builderListMessage, ChatHandler handler) {
        chat.searchHistory(builderListMessage, handler);
    }

    @Override
    public void getContact(Integer count, Long offset, ChatHandler handler) {
        chat.getContacts(count, offset, handler);
    }

    @Override
    public void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String systemMetadata, ChatHandler handler) {
        chat.createThread(threadType, invitee, threadTitle, description, image, systemMetadata, handler);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, ChatHandler handler) {
        chat.sendTextMessage(textMessage, threadId, messageType, metaData, handler);
    }

    @Override
    public void sendTextMessage(RequestMessage requestMessage, ChatHandler handler) {
        chat.sendTextMessage(requestMessage, null);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId, Integer messageType, ChatHandler handler) {
        chat.replyMessage(messageContent, threadId, messageId, "meta", messageType, handler);
    }

    @Override
    public void replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler) {
        chat.replyFileMessage(request, handler);
    }

    @Override
    public void replyMessage(RequestReplyMessage request, ChatHandler handler) {
        chat.replyMessage(request, handler);
    }

    @Override
    public void muteThread(int threadId, ChatHandler handler) {
        chat.muteThread(threadId, handler);
    }

    @Override
    public void renameThread(long threadId, String title, ChatHandler handler) {


        chat.renameThread(threadId, title, handler);
    }

    @Override
    public void unMuteThread(int threadId, ChatHandler handler) {
        chat.unMuteThread(threadId, handler);
    }

    @Override
    public void editMessage(int messageId, String messageNewContent, String metaData, ChatHandler handler) {

        RequestEditMessage requestEditMessage = new RequestEditMessage.Builder(messageNewContent,
                messageId)
                .metaData(metaData)
                .build();

        chat.editMessage(requestEditMessage, handler);
    }

    @Override
    public void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler) {

        RequestThreadParticipant participant = new RequestThreadParticipant
                .Builder(threadId)
                .count(50)
                .offset(0)
                .build();
        chat.getThreadParticipants(participant, handler);

//        chat.getThreadParticipants(count, offset, threadId, handler);
    }

    @Override
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email, String username) {


//        RequestAddContact requestAddContact = new RequestAddContact.Builder()
//                .firstName(firstName)
//                .lastName(lastName)
//                .cellphoneNumber(cellphoneNumber)
//                .email(email)
//                .username(username)
//                .build();


        chat.addContact(firstName, lastName, cellphoneNumber, email, null, username);


    }

    @Override
    public void removeContact(long id) {
//        chat.removeContact(id);
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact.Builder(id).build();
        chat.removeContact(requestRemoveContact);

    }

    @Override
    public void searchContact(RequestSearchContact requestSearchContact) {
        chat.searchContact(requestSearchContact);
    }

    @Override
    public void block(Long contactId, Long userId, Long threadId, ChatHandler handler) {
//        RequestBlock requestBlock = new RequestBlock.Builder()
//                .contactId()
//                .threadId()
//                .userId()
//                .build();
//        chat.block(requestBlock,null);
        chat.block(contactId, userId, threadId, handler);
    }

    @Override
    public void unBlock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler) {

        chat.unblock(blockId, userId, threadId, contactId, handler);
    }

    @Override
    public void unBlock(RequestUnBlock request, ChatHandler handler) {

//        RequestUnBlock requestUnBlock = new RequestUnBlock.Builder()
//                .blockId()
//                .contactId()
//                .threadId()
//                .userId()
//                .build();requestUnBlock = new RequestUnBlock.Builder()
//                .blockId()
//                .contactId()
//                .threadId()
//                .userId()
//                .build();

        chat.unblock(request, handler);
    }

    @Override
    public void spam(long threadId) {

        RequestSpam requestSpam = new RequestSpam.Builder()
                .threadId(threadId)
                .build();

        chat.spam(requestSpam);


    }

    @Override
    public String spam(RequestSpam requestSpam) {


        return chat.spam(requestSpam);
    }

    @Override
    public void getBlockList(Long count, Long offset, ChatHandler handler) {
        chat.getBlockList(count, offset, handler);
    }

    @Override
    public String sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType, ProgressHandler.sendFileMessage handler) {

        RequestFileMessage request = new RequestFileMessage.Builder(activity, threadId, fileUri, messageType)
                .description(description)
                .systemMetadata(metaData)

                .build();


        return chat.sendFileMessage(request, handler);
    }

    @Override
    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
        return chat.sendFileMessage(requestFileMessage, handler);
    }

    @Override
    public void syncContact(Activity activity) {
        chat.syncContact(activity);
    }

    @Override
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {
        chat.forwardMessage(threadId, messageIds);
    }

    @Override
    public void forwardMessage(RequestForwardMessage request) {
        chat.forwardMessage(request);
    }

    @Override
    public void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email) {


        chat.updateContact(id, firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void updateContact(RequestUpdateContact updateContact) {
        chat.updateContact(updateContact);
    }

    @Override
    public void uploadImage(Activity activity, Uri fileUri) {

        RequestUploadImage req = new RequestUploadImage.Builder(activity, fileUri)
                .build();
        chat.uploadImage(req);
    }

    @Override
    public void uploadFile(@NonNull Activity activity, @NonNull Uri uri) {

        RequestUploadFile request = new RequestUploadFile.Builder(activity, uri)
                .build();

        chat.uploadFile(request);
    }

    @Override
    public void seenMessage(int messageId, long ownerId, ChatHandler handler) {
        chat.seenMessage(messageId, ownerId, handler);
    }

    @Override
    public void logOut() {
        tokenHandler.logOut();
        chat.closeChat();
    }

    @Override
    public void removeParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
        chat.removeParticipants(threadId, contactIds, handler);
    }

    @Override
    public void removeParticipants(RemoveParticipantRequest removeParticipantRequest, ChatHandler handler) {
        chat.removeParticipants(removeParticipantRequest, handler);
    }


    @Override
    public void addParticipants(RequestAddParticipants requestAddParticipants, ChatHandler handler) {
        chat.addParticipants(requestAddParticipants, handler);
    }

    @Override
    public void leaveThread(long threadId, ChatHandler handler) {

        SafeLeaveRequest request = new SafeLeaveRequest.Builder(threadId, 18477)
                .build();
//        RequestLeaveThread leaveThread = new RequestLeaveThread.Builder(threadId).shouldKeepHistory()
//                .build();
//
//        chat.leaveThread(leaveThread, null);

        chat.safeLeaveThread(request);
    }

    @Override
    public void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler) {


//        chat.updateThreadInfo(threadId, threadInfoVO, handler);
    }

    @Override
    public void updateThreadInfo(RequestThreadInfo request, ChatHandler handler) {
        chat.updateThreadInfo(request, handler);
    }

    @Override
    public void deleteMessage(ArrayList<Long> messageIds, long threadId, Boolean deleteForAll, ChatHandler handler) {

        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder()
                .messageIds(messageIds)
                .deleteForAll(deleteForAll)
                .build();


        String un = chat.deleteMessage(requestDeleteMessage, handler);


    }

    @Override
    public void deleteMessage(RequestDeleteMessage deleteMessage, ChatHandler handler) {

        if (deleteMessage.getMessageIds().size() > 1) {
            String uniqueId = chat.deleteMessage(deleteMessage, handler);
        } else {
            List<String> uniqueIds = chat.deleteMultipleMessage(deleteMessage, handler);
        }
    }

    @Override
    public void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler) {


        RequestUploadImage req = new RequestUploadImage.Builder(activity, fileUri)
                .setwC(240)
                .sethC(120)
                .setxC(10)
                .setyC(5)
                .setPublic(false)
                .build();

//        chat.uploadImageProgress(activity,fileUri,handler);
        chat.uploadImageProgress(req, handler);


    }

    @Override
    public void uploadFileProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgressFile handler) {


        RequestUploadFile req = new RequestUploadFile
                .Builder(activity, fileUri)
                .setPublic(true)
                .build();

        chat.uploadFileProgress(req, handler);
    }

    @Override
    public void setAdmin(RequestSetAdmin requestAddAdmin) {
        chat.addAdmin(requestAddAdmin);
    }

    @Override
    public void removeAdminRules(RequestSetAdmin requestAddAdmin) {

        chat.removeAdmin(requestAddAdmin);
    }

    @Override
    public void clearHistory(RequestClearHistory requestClearHistory) {
        chat.clearHistory(requestClearHistory);
    }

    @Override
    public void getAdminList(RequestGetAdmin requestGetAdmin) {
        chat.getAdminList(requestGetAdmin);
    }


    @Override
    public void getNotSeenDuration(ArrayList<Integer> userIds) {


        RequestGetLastSeens requestGetLastSeens =
                new RequestGetLastSeens
                        .Builder(userIds)
                        .build();


        chat.getNotSeenDuration(requestGetLastSeens);


    }

    @Override
    public String startTyping(long threadId) {

        RequestSignalMsg requestSignalMsg = new RequestSignalMsg.Builder()
                .signalType(ChatMessageType.SignalMsg.IS_TYPING)
                .threadId(threadId)
                .build();

        return chat.startTyping(requestSignalMsg);


    }

    @Override
    public boolean stopTyping(String signalUniqueId) {

        chat.stopTyping();

        return true;

    }

    @Override
    public void stopAllSignalMessages() {

        chat.stopAllSignalMessages();
    }

    @Override
    public void pinThread(RequestPinThread requestPinThread) {

        chat.pinThread(requestPinThread);
    }


    @Override
    public void unPinThread(RequestPinThread requestPinThread) {
        chat.unPinThread(requestPinThread);
    }

    @Override
    public void setAuditor(RequestSetAuditor requestAddAdmin) {

        chat.addAuditor(requestAddAdmin);

    }

    @Override
    public void removeAuditor(RequestSetAuditor requestAddAdmin) {

        chat.removeAuditor(requestAddAdmin);
    }


    @Override
    public void createThreadWithFile(RequestCreateThreadWithFile request, ProgressHandler.sendFileMessage handler) {
        chat.createThreadWithFile(request, handler);
    }

    //View
    @Override
    public void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onDeliver(content, chatResponse);
        view.onGetDeliverMessage();
    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);
        view.onGetThreadList(content, thread);
    }

    @Override
    public void onGetMutualGroups(String content, ChatResponse<ResultThreads> thread) {
        super.onGetMutualGroups(content, thread);
        Log.e(TAG, "onGetMutualGroups: " + content);

    }

    @Override
    public void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {
        Log.e(TAG, "onThreadInfoUpdated: "+response.getResult().getThread().getId() + "===> "+response.getResult().getThread().getLastMessage());
    }

    @Override
    public void onGetContacts(String content, ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(content, outPutContact);
        view.onGetContacts(outPutContact);
    }

    @Override
    public void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSeen(content, chatResponse);
        view.onGetSeenMessage();
    }

    @Override
    public void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {
        view.onGetUserInfo(outPutUserInfo);
    }

    @Override
    public void onSent(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSent(content, chatResponse);
        view.onSentMessage();
        Log.e("testmsg", "id : " + chatResponse.getResult().getMessageId());
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

        if (Util.isNotNullOrEmpty(outPutError.getUniqueId()) && uniqueIds.contains(outPutError.getUniqueId())) {
            view.onError("");
            view.updateStatus(outPutError.getErrorMessage());
        }
    }

    @Override
    public void getUserRoles(RequestGetUserRoles req) {

        String uniqueId = chat.getCurrentUserRoles(req);


    }

    @Override
    public void pinMessage(RequestPinMessage requestPinMessage) {

        String uniqueId = chat.pinMessage(requestPinMessage);


    }

    @Override
    public void unPinMessage(RequestPinMessage requestPinMessage) {

        String uniqueId = chat.unPinMessage(requestPinMessage);
    }

    @Override
    public void getMentionList(RequestGetMentionList req) {

        chat.getMentionList(req);

    }

    @Override
    public void startTyping(RequestSignalMsg req) {

        chat.startTyping(req);

    }

    @Override
    public String downloadFile(RequestGetImage requestGetImage, ProgressHandler.IDownloadFile onProgressFile) {
        return chat.getImage(requestGetImage, onProgressFile);
    }

    @Override
    public String downloadFile(RequestGetFile requestGetFile, ProgressHandler.IDownloadFile onProgressFile) {
        return chat.getFile(requestGetFile, onProgressFile);
    }

    @Override
    public boolean cancelDownload(String downloadingId) {
        return chat.cancelDownload(downloadingId);
    }

    @Override
    public void getCacheSize() {
        chat.getCacheSize();
    }

    @Override
    public void clearDatabaseCache(Chat.IClearMessageCache listener) {
        chat.clearCacheDatabase(listener);
    }

    @Override
    public long getStorageSize() {
        return chat.getStorageSize();
    }

    @Override
    public long getImageFolderSize() {
        return chat.getCachedPicturesFolderSize();
    }

    @Override
    public long getFilesFolderSize() {
        return chat.getCachedFilesFolderSize();
    }

    @Override
    public boolean clearPictures() {
        return chat.clearCachedPictures();
    }

    @Override
    public boolean clearFiles() {
        return chat.clearCachedFiles();
    }

    @Override
    public void closeChat() {
//        chat.closeChat();
    }

    @Override
    public void addContact(RequestAddContact request) {
        chat.addContact(request);
    }

    @Override
    public void updateChatProfile(RequestUpdateProfile request) {
        chat.updateChatProfile(request);
    }


    @Override
    public void onCreateThread(String content, ChatResponse<ResultThread> outPutThread) {
        super.onCreateThread(content, outPutThread);
        view.onCreateThread();
    }

    @Override
    public void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {
        super.onGetThreadParticipant(content, outPutParticipant);
        view.onGetThreadParticipant();
    }

    @Override
    public void onEditedMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
        super.onEditedMessage(content, chatResponse);
        view.onEditMessage();
    }

    @Override
    public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
        super.onGetHistory(content, history);
        view.onGetThreadHistory(history);
    }

    @Override
    public void onMuteThread(String content, ChatResponse<ResultMute> outPutMute) {
        super.onMuteThread(content, outPutMute);
        view.onMuteThread();
    }

    @Override
    public void onUnmuteThread(String content, ChatResponse<ResultMute> outPutMute) {
        super.onUnmuteThread(content, outPutMute);
        view.onUnMuteThread();
    }

    @Override
    public void onGetMentionList(ChatResponse<ResultHistory> response) {
        super.onGetMentionList(response);
    }

    @Override
    public void onGetHashTagList(ChatResponse<ResultHistory> response) {
        super.onGetHashTagList(response);
        Log.e(TAG, "onGetHashTagList: " + response.getJson());

    }

    @Override
    public void onRenameThread(String content, OutPutThread outPutThread) {
        super.onRenameThread(content, outPutThread);
        view.onRenameGroupThread();
    }

    @Override
    public void onContactAdded(String content, ChatResponse<ResultAddContact> chatResponse) {
        super.onContactAdded(content, chatResponse);
        view.onAddContact();
    }

    @Override
    public void onUpdateContact(String content, ChatResponse<ResultUpdateContact> chatResponse) {
        super.onUpdateContact(content, chatResponse);
        view.onUpdateContact();
    }

    @Override
    public void onUploadFile(String content, ChatResponse<ResultFile> response) {
        super.onUploadFile(content, response);
        view.onUploadFile();
    }


    @Override
    public void onUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {
        super.onUploadImageFile(content, chatResponse);
        view.onUploadImageFile();
    }

    @Override
    public void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {
        super.onRemoveContact(content, response);
        view.onRemoveContact();
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
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        super.onDeleteMessage(content, outPutDeleteMessage);
        view.onDeleteMessage();
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
//                            .firstName(name)
//                            .lastName(family)
//                            .cellphoneNumber(usernames.get(i))
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
    public void onBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onBlock(content, outPutBlock);
        view.onBlock();
    }

    @Override
    public void onUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onUnBlock(content, outPutBlock);
        view.onUnblock();
    }

    @Override
    public void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {
        super.onMapSearch(content, outPutMapNeshan);
        view.onMapSearch();
    }

    @Override
    public void onMapRouting(String content) {
        view.onMapRouting();
    }

    @Override
    public void onGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList) {
        super.onGetBlockList(content, outPutBlockList);
        view.ongetBlockList();
    }

    @Override
    public void OnSeenMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void onSearchContact(String content, ChatResponse<ResultContact> chatResponse) {
        super.onSearchContact(content, chatResponse);
        view.onSearchContact();
    }

    @Override
    public void OnStaticMap(ChatResponse<ResultStaticMapImage> chatResponse) {
        view.onMapStaticImage(chatResponse);
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
    public void onPinMessage(ChatResponse<ResultPinMessage> response) {
        view.onPinMessage(response);
    }

    @Override
    public void onUnPinMessage(ChatResponse<ResultPinMessage> response) {
        view.onUnPinMessage(response);
    }

    @Override
    public void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {
        view.onGetCurrentUserRoles(response);
    }

    @Override
    public void onTypingSignalTimeout(long threadId) {
        view.onTypingSignalTimeout(threadId);
    }


    @Override
    public void onUniqueNameIsAvailable(ChatResponse<ResultIsNameAvailable> response) {
        view.onUniqueNameIsAvailable(response);
    }

    @Override
    public void onJoinPublicThread(ChatResponse<ResultJoinPublicThread> response) {
        view.onJoinPublicThread(response);
    }

    @Override
    public void onGetUnreadMessagesCount(ChatResponse<ResultUnreadMessagesCount> response) {
        view.onGetUnreadsMessagesCount(response);
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
    public void onPingStatusSent(ChatResponse<StatusPingResult> response) {
        view.pingStatusSent(response);
    }


//    @Override
//    public void onReceiveCallHistory(ChatResponse<GetCallHistoryResult> response) {
//
////        List<CallVO> calls = new ArrayList<>();
////
////        for (CallVO call :
////                response.getResult().getCallsList()) {
////
////            if(call.getPartnerParticipantVO()!=null)
////                calls.add(call);
////
////        }
////
////        view.onGetCallHistory(response);
//
//    }


//    @Override
//    public void getCallHistory() {
//
//
//        GetCallHistoryRequest request = new GetCallHistoryRequest.Builder()
//                .setType(CallType.Constants.VIDEO_CALL)
//                .count(50)
//                .build();
//
//        uniqueIds.add(chat.getCallsHistory(request));
//
//
//    }

    @Override
    public void closeThread(int testThreadId) {

        CloseThreadRequest closeThreadRequest = new CloseThreadRequest
                .Builder(testThreadId)
                .typeCode("default")
                .build();

        if (chat.isChatReady()) {
            String uniqueId = chat.closeThread(closeThreadRequest);
        }


    }

    @Override
    public void getContact() {

        if(chat.isChatReady()){
            view.onLoadingContactsStarted();
            RequestGetContact request =
                    new RequestGetContact.Builder()
                            .count(50)
                            .offset(0)
                            .build();

            uniqueIds.add(chat.getContacts(request, null));
        }


    }

    @Override
    public void registerAssistant(RegisterAssistantRequest request) {
        chat.registerAssistant(request);
    }

    @Override
    public void getAssistants(GetAssistantRequest request) {
        chat.getAssistants(request);
    }

    @Override
    public void deActiveAssistant(DeActiveAssistantRequest request) {
        chat.deactiveAssistant(request);
    }

    @Override
    public void getAssistantHistory(GetAssistantHistoryRequest request) {
        chat.getAssistantHistory(request);
    }

    @Override
    public void blockAssistant(BlockUnblockAssistantRequest o) {
        chat.blockAssistant(o);
    }

    @Override
    public void unBlockAssistant(BlockUnblockAssistantRequest o) {
        chat.unBlockAssistant(o);
    }

    @Override
    public void getBlocksAssistant(GetBlockedAssistantsRequest o) {
        chat.getBlocksAssistant(o);
    }

    @Override
    public void changeThreadType(ChangeThreadTypeRequest request) {
        chat.changeThreadType(request);
    }

    @Override
    public void createTag(CreateTagRequest request) {
//        chat.createTag(request);
    }

    @Override
    public void editTag(EditTagRequest request) {
        chat.editTag(request);
    }

    @Override
    public void deleteTag(DeleteTagRequest request) {
        chat.deleteTag(request);
    }

    @Override
    public void addTagParticipant(AddTagParticipantRequest request) {
        chat.addTagParticipant(request);
    }

    @Override
    public void removeTagParticipant(RemoveTagParticipantRequest request) {
        chat.removeTagParticipant(request);
    }

    @Override
    public void getTagList(GetTagListRequest request) {
        chat.getTagList(request);
    }

    @Override
    public void getMutualGroups(GetMutualGroupRequest request) {
       chat.getMutualGroup(request);
    }


    @Override
    public void onThreadTypeChanged(ChatResponse<Thread> response) {
        super.onThreadTypeChanged(response);
        Log.e(TAG, "onThreadChangeType: " + response.getJson());
    }

    @Override
    public void onThreadClosed(ChatResponse<CloseThreadResult> response) {
        view.onThreadClosed(response.getSubjectId());
    }

}
