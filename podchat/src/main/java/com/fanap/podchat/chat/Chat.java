package com.fanap.podchat.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.model.Device;
import com.fanap.podasync.model.DeviceResult;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.localmodel.LFileUpload;
import com.fanap.podchat.mainmodel.AddParticipant;
import com.fanap.podchat.mainmodel.BaseMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ChatMessageContent;
import com.fanap.podchat.mainmodel.ChatThread;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.MapNeshan;
import com.fanap.podchat.mainmodel.MapReverse;
import com.fanap.podchat.mainmodel.MapRout;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.RemoveParticipant;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.SearchContactVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.mainmodel.UpdateContact;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.mainmodel.UserRoleVO;
import com.fanap.podchat.model.Admin;
import com.fanap.podchat.model.ChatMessageForward;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ContactRemove;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.DeleteMessageContent;
import com.fanap.podchat.model.EncResponse;
import com.fanap.podchat.model.Error;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageMetaData;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.FileMetaDataContent;
import com.fanap.podchat.model.MapLocation;
import com.fanap.podchat.model.MetaDataFile;
import com.fanap.podchat.model.MetaDataImageFile;
import com.fanap.podchat.model.MetadataLocationFile;
import com.fanap.podchat.model.OutPutHistory;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutMapRout;
import com.fanap.podchat.model.OutPutNotSeenDurations;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutputSetRoleToUser;
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
import com.fanap.podchat.model.ResultMap;
import com.fanap.podchat.model.ResultMapReverse;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultMute;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultNotSeenDuration;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultRemoveContact;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultSignalMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.networking.ProgressRequestBody;
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.api.MapApi;
import com.fanap.podchat.networking.api.SSOApi;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperFileServer;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperMap;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperPlatformHost;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperSsoHost;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.PhoneContactDbHelper;
import com.fanap.podchat.persistance.module.AppDatabaseModule;
import com.fanap.podchat.persistance.module.AppModule;
import com.fanap.podchat.persistance.module.DaggerMessageComponent;
import com.fanap.podchat.requestobject.RequestAddAdmin;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestBlock;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
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
import com.fanap.podchat.requestobject.RequestLeaveThread;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapRouting;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestMuteThread;
import com.fanap.podchat.requestobject.RequestRemoveContact;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSeenMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
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
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.FileUtils;
import com.fanap.podchat.util.Permission;
import com.fanap.podchat.util.RequestMapSearch;
import com.fanap.podchat.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.*;


public class Chat extends AsyncAdapter {
    private static final String MTAG = "MTAG";
    public static final String PING = "PING";
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 1007;
    private static Async async;
    private String token;
    private String typeCode;
    private String platformHost;
    private String fileServer;
    private static Chat instance;
    private static SecurePreferences mSecurePrefs;
    private static ChatListenerManager listenerManager;
    private long userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, ArrayList<Callback>> threadCallbacks;
    private static String TAG = "CHAT_SDK";


//    private Map<Long, LinkedHashMap<String, Handler>> threadSignalsManager = new HashMap<>();

    private HashMap<String, Handler> signalHandlerKeeper = new HashMap<>();
    private HashMap<String, RequestSignalMsg> requestSignalsKeeper = new HashMap<>();
    private HashMap<Long, ArrayList<String>> threadSignalsKeeper = new HashMap<>();

    private static HashMap<String, ChatHandler> handlerSend;
    private static HashMap<String, SendingQueueCache> sendingQList;
    private static HashMap<String, UploadingQueueCache> uploadingQList;
    private static HashMap<String, WaitQueueCache> waitQList;
    private ProgressHandler.cancelUpload cancelUpload;
    private static final String API_KEY_MAP = "8b77db18704aa646ee5aaea13e7370f4f88b9e8c";
    private long lastSentMessageTime;
    private boolean chatReady = false;
    private boolean rawLog = false;
    private boolean asyncReady = false;

    private static boolean cache = false;
    private static boolean permit = false;
    private static final int TOKEN_ISSUER = 1;
    private long retryStepUserInfo = 1;
    private long retrySetToken = 1;
    private static final Handler sUIThreadHandler;
    private static final Handler getUserInfoHandler;
    private static final Handler pingHandler;
    private static final Handler tokenHandler;
    private boolean currentDeviceExist;
    private int signalIntervalTime;
    private Context context;
    private boolean log;
    private int expireAmount;
    private static Gson gson;
    private boolean checkToken = false;
    private boolean userInfoResponse = false;
    private long ttl;
    private String ssoHost;


    private static HandlerThread signalMessageHandlerThread = null;
//    private static HandlerThread signalMessageHandlerThread = null;


    @Inject
    public MessageDatabaseHelper messageDatabaseHelper;

    @Inject
    public PhoneContactDbHelper phoneContactDbHelper;

    private Chat() {

    }

    /**
     * Initialize the Chat
     *
     * @param context for Async sdk and other usage
     **/

    public synchronized static Chat init(Context context) {


        if (instance == null) {
            async = Async.getInstance(context);
            instance = new Chat();
            gson = new GsonBuilder().create();
            instance.setContext(context);
            listenerManager = new ChatListenerManager();
            threadCallbacks = new HashMap<>();
            mSecurePrefs = new SecurePreferences(context, "", "chat_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);


            setSignalHandlerThread();


            if (!Util.isNullOrEmpty(instance.getKey())) {
                DaggerMessageComponent.builder()
                        .appDatabaseModule(new AppDatabaseModule(context, instance.getKey()))
                        .appModule(new AppModule(context))
                        .build()
                        .inject(instance);
                permit = true;
            }

            if (!cache) {
                sendingQList = new HashMap();
                uploadingQList = new HashMap();
                waitQList = new HashMap<>();
            }
        }
        return instance;
    }

    private static void setSignalHandlerThread() {


        try {

            if (signalMessageHandlerThread != null) {

                // to avoid multiple start signal request

                signalMessageHandlerThread.quit();

                signalMessageHandlerThread = null;

            }


            signalMessageHandlerThread = new HandlerThread("signal handler thread");

            signalMessageHandlerThread.start();


//                if (!signalMessageHandlerThread.isAlive())
//                    signalMessageHandlerThread.start();


//            if (startSignalHandler != null) {
//
//                startSignalHandler = null;
//
//            }
//            startSignalHandler = new Handler(signalMessageHandlerThread.getLooper());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * It's showed the state of socket
     * When state of the Async changed then the chat ping is stopped by (chatState)flag
     * We have six state :
     * OPEN
     * CONNECTING
     * CLOSING
     * CLOSED
     * ASYNC_READY
     * CHAT_READY
     */


    @Override
    public void onStateChanged(String state) throws IOException {

        super.onStateChanged(state);

        listenerManager.callOnChatState(state);

        if (log) {
            Log.i(TAG, "State is" + " " + state);
        }

        @ChatStateType.ChatSateConstant String currentChatState = state;
        switch (currentChatState) {
            case OPEN:
                break;
            case ASYNC_READY:
                asyncReady = true;
                retryOnGetUserInfo();
                break;
            case CONNECTING:
                chatReady = false;
                tokenHandler.removeCallbacksAndMessages(null);
                break;
            case CLOSING:
                chatReady = false;
                retrySetToken = 1;
                tokenHandler.removeCallbacksAndMessages(null);
                break;
            case CLOSED:
                chatReady = false;
                retrySetToken = 1;
                tokenHandler.removeCallbacksAndMessages(null);
                break;
        }
    }

    /**
     * First we check the message type and then we set the
     * the callback for it.
     * Here its showed the raw log.
     */
    @Override
    public void onReceivedMessage(String textMessage) throws IOException {

        super.onReceivedMessage(textMessage);
        int messageType = 0;
        ChatMessage chatMessage = gson.fromJson(textMessage, ChatMessage.class);

        if (rawLog) {
            Log.i(TAG, "RAW_LOG");
            Log.i(TAG, textMessage);
        }

        String messageUniqueId = chatMessage != null ? chatMessage.getUniqueId() : null;
        long threadId = chatMessage != null ? chatMessage.getSubjectId() : 0;
        Callback callback = null;
        if (messageCallbacks.containsKey(messageUniqueId)) {
            callback = messageCallbacks.get(messageUniqueId);
        }

        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }
        @Constants int currentMessageType = messageType;
        switch (currentMessageType) {
            case Constants.ADD_PARTICIPANT:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.UNBLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.BLOCK:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.CHANGE_TYPE:
                break;
            case Constants.SENT:
                handleSent(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.DELIVERY:
                handleDelivery(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.SEEN:
                handleSeen(chatMessage, messageUniqueId, threadId);
                break;
            case Constants.ERROR:
                handleError(chatMessage);
                break;
            case Constants.FORWARD_MESSAGE:
                handleForwardMessage(chatMessage);
                break;
            case Constants.GET_CONTACTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.GET_HISTORY:
                /*Remove uniqueIds from waitQueue /***/
                if (callback == null) {
                    handleRemoveFromWaitQueue(chatMessage);
                } else {
                    handleResponseMessage(callback, chatMessage, messageUniqueId);
                }
                break;
            case Constants.GET_STATUS:
                break;
            case Constants.GET_THREADS:
                if (callback == null) {
                    handleGetThreads(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.INVITATION:
                if (callback == null) {
                    handleCreateThread(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.REMOVED_FROM_THREAD:
                handleRemoveFromThread(chatMessage);
                break;
            case Constants.LEAVE_THREAD:
                if (callback == null) {
                    handleOutPutLeaveThread(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.MESSAGE: {
                handleNewMessage(chatMessage);
                break;

            }
            case Constants.MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.PING:
                handleOnPing(chatMessage);
                break;
            case Constants.RELATION_INFO:
                break;
            case Constants.REMOVE_PARTICIPANT:
                if (callback == null) {
                    handleOutPutRemoveParticipant(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.RENAME:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_PARTICIPANTS:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.UN_MUTE_THREAD:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.USER_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.USER_STATUS:
                break;
            case Constants.GET_BLOCKED:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.DELETE_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.EDIT_MESSAGE:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_INFO_UPDATED:
                handleThreadInfoUpdated(chatMessage);
                break;
            case Constants.LAST_SEEN_UPDATED:
                handleLastSeenUpdated(chatMessage);
                break;
            case Constants.UPDATE_THREAD_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.SPAM_PV_THREAD:
                break;
            case Constants.DELIVERED_MESSAGE_LIST:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.SEEN_MESSAGE_LIST:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.SET_ROLE_TO_USER:

                handleSetRole(chatMessage);
                break;
            case Constants.CLEAR_HISTORY:
                handleClearHistory(chatMessage);
                break;

            case Constants.GET_THREAD_ADMINS:
                //TODO handle response
                listenerManager.callOnGetThreadAdmin(chatMessage.getContent());
                break;

            case Constants.GET_NOT_SEEN_DURATION:
                handleGetNotSeenDuration(callback, chatMessage, messageUniqueId);
                break;

            case Constants.SYSTEM_MESSAGE:
                handleSystemMessage(callback, chatMessage, messageUniqueId);
                break;
        }
    }


    /**
     * It Connects to the Async .
     * <p>
     * socketAddress {**REQUIRED**}
     * platformHost  {**REQUIRED**}
     * severName     {**REQUIRED**}
     * appId         {**REQUIRED**}
     * token         {**REQUIRED**}
     * fileServer    {**REQUIRED**}
     * ssoHost       {**REQUIRED**}
     */
    public void connect(RequestConnect requestConnect) {
        String platformHost = requestConnect.getPlatformHost();
        String token = requestConnect.getToken();
        String fileServer = requestConnect.getFileServer();
        String socketAddress = requestConnect.getSocketAddress();
        String appId = requestConnect.getAppId();
        String severName = requestConnect.getSeverName();
        String ssoHost = requestConnect.getSsoHost();

        connect(socketAddress, appId, severName, token, ssoHost, platformHost, fileServer, typeCode);
    }

    /**
     * It shows the log
     *
     * @param log {@code true} to allow show of logs.
     */
    public void isLoggable(boolean log) {
        this.log = log;
    }

    private void socketLog(boolean log) {
        async.isLoggable(log);
    }

    /**
     * It shows the message that come from Async without any changes
     *
     * @param rawLog {@code true} to allow show of the raw logs.
     */
    public void rawLog(boolean rawLog) {
        this.rawLog = rawLog;
    }

    /**
     * It's Connected to the Async .
     *
     * @param socketAddress {**REQUIRED**}  Address of the socket
     * @param platformHost  {**REQUIRED**}  Address of the platform host
     * @param severName     {**REQUIRED**}  Name of the server
     * @param appId         {**REQUIRED**}  Id of the app
     * @param token         {**REQUIRED**}  Token for Authentication
     * @param fileServer    {**REQUIRED**}  Address of the file server
     * @param ssoHost       {**REQUIRED**}  Address of the SSO Host
     */
    public void connect(String socketAddress, String appId, String severName, String token,
                        String ssoHost, String platformHost, String fileServer, String typeCode) {
        try {
            if (platformHost.endsWith("/")) {

                messageCallbacks = new HashMap<>();
                handlerSend = new HashMap<>();
                async.addListener(this);
                RetrofitHelperPlatformHost retrofitHelperPlatformHost = new RetrofitHelperPlatformHost(platformHost, getContext());
                contactApi = retrofitHelperPlatformHost.getService(ContactApi.class);
                setPlatformHost(platformHost);
                setToken(token);
                setSsoHost(ssoHost);
                setTypeCode(typeCode);
                setFileServer(fileServer);
                gson = new GsonBuilder().create();
                async.connect(socketAddress, appId, severName, token, ssoHost, "");


            } else {
                String jsonError = getErrorOutPut("PlatformHost " + ChatConstant.ERROR_CHECK_URL
                        , ChatConstant.ERROR_CODE_CHECK_URL, null);


                if (log) {
                    Log.e(TAG, jsonError);
                    listenerManager.callOnLogEvent(jsonError);

                }
            }
        } catch (Throwable throwable) {
            if (log) {
                Log.e(TAG, throwable.getMessage());
                listenerManager.callOnLogEvent(throwable.getMessage());
            }
        }
    }

    /**
     * It shows the state of the cache Database
     */
    public boolean isDbOpen() {
        return messageDatabaseHelper.isDbOpen();
    }


    /**
     * Send text message to the thread
     * All of the messages first send to Message Queue(Cache) and then send to chat server
     *
     * @param textMessage        String that we want to sent to the thread
     * @param threadId           Id of the destination thread
     * @param jsonSystemMetadata It should be Json,if you don't have metaData you can set it to "null"
     */
    public String sendTextMessage(String textMessage, long threadId, Integer messageType, String jsonSystemMetadata
            , ChatHandler handler) {

        String asyncContentWaitQueue;
        String uniqueId;
        uniqueId = generateUniqueId();

        try {

            ChatMessage chatMessageQueue = new ChatMessage();
            chatMessageQueue.setContent(textMessage);
            chatMessageQueue.setType(Constants.MESSAGE);
            chatMessageQueue.setTokenIssuer("1");
            chatMessageQueue.setToken(getToken());

            if (jsonSystemMetadata != null) {
                chatMessageQueue.setSystemMetadata(jsonSystemMetadata);
            }

            chatMessageQueue.setUniqueId(uniqueId);
            chatMessageQueue.setTime(1000);
            chatMessageQueue.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageQueue);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }
            if (!Util.isNullOrEmpty(messageType)) {
                jsonObject.addProperty("messageType", messageType);
            } else {
                jsonObject.remove("messageType");
            }

            SendingQueueCache sendingQueue = new SendingQueueCache();
            sendingQueue.setSystemMetadata(jsonSystemMetadata);
            sendingQueue.setMessageType(messageType);
            sendingQueue.setThreadId(threadId);
            sendingQueue.setUniqueId(uniqueId);
            sendingQueue.setMessage(textMessage);

            asyncContentWaitQueue = jsonObject.toString();

            sendingQueue.setAsyncContent(asyncContentWaitQueue);

            if (cache) {
                messageDatabaseHelper.insertSendingMessageQueue(sendingQueue);
            } else {
                sendingQList.put(uniqueId, sendingQueue);
            }

            if (log) {
                Log.i(TAG, "Message with this" + "  uniqueId  " + uniqueId + "  has been added to Message Queue");
            }
            if (chatReady) {

                if (handler != null) {
                    handler.onSent(uniqueId, threadId);
                    handler.onSentResult(null);
                    handlerSend.put(uniqueId, handler);
                }

                if (cache) {
                    messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
                    messageDatabaseHelper.insertWaitMessageQueue(sendingQueue);
                }

                setThreadCallbacks(threadId, uniqueId);
                sendAsyncMessage(asyncContentWaitQueue, 4, "SEND_TEXT_MESSAGE");

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                if (log) {
                    Log.e(TAG, jsonError);
                }
            }
        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
        return uniqueId;
    }

    /**
     * @param requestAddAdmin You can add or remove someone as admin to some thread set
     *                        and roles to them
     *                        `setRoleOperation` could be `Add` or `remove`
     */
    public String setAdmin(RequestAddAdmin requestAddAdmin) {

        long threadId = requestAddAdmin.getThreadId();
        ArrayList<RequestRole> roles = requestAddAdmin.getRoles();
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setCheckThreadMembership(true);
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVO.setRoleOperation(requestRole.getRoleOperation());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(Constants.SET_ROLE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            setCallBacks(null, null, null, true, Constants.SET_ROLE_TO_USER, null, uniqueId);
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "SET_ROLE_TO_USER");
        }
        return uniqueId;
    }


    public String removeAdminRoles(RequestAddAdmin requestAddAdmin) {

        long threadId = requestAddAdmin.getThreadId();
        ArrayList<RequestRole> roles = requestAddAdmin.getRoles();
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setCheckThreadMembership(true);
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVO.setRoleOperation(requestRole.getRoleOperation());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(Constants.SET_ROLE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            setCallBacks(null, null, null, true, Constants.SET_ROLE_TO_USER, null, uniqueId);
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "SET_ROLE_TO_USER");
        }
        return uniqueId;
    }

    /**
     * Its sent message but it gets Object as an attribute
     *
     * @param requestMessage this object has :
     *                       String textMessage {text of the message}
     *                       int messageType {type of the message}
     *                       String jsonMetaData {metadata of the message}
     *                       long threadId {The id of a thread that its wanted to send  }
     */
    public String sendTextMessage(RequestMessage requestMessage, ChatHandler handler) {
        String textMessage = requestMessage.getTextMessage();
        long threadId = requestMessage.getThreadId();
        int messageType = requestMessage.getMessageType();
        String jsonMetaData = requestMessage.getJsonMetaData();

        return sendTextMessage(textMessage, threadId, messageType, jsonMetaData, handler);
    }

    /**
     * First we get the contact from server then at the respond of that
     *
     * @param activity its for check the permission of reading the phone contact
     *                 {@link #getPhoneContact(Context)}
     */
    public String syncContact(Activity activity) {
        String uniqueId = generateUniqueId();
        if (Permission.Check_READ_CONTACTS(activity)) {
            if (chatReady) {
                List<PhoneContact> phoneContacts = getPhoneContact(getContext());

                if (phoneContacts.size() > 0) {
                    addContacts(phoneContacts, uniqueId);
                } else {
                    ChatResponse<Contacts> chatResponse = new ChatResponse<>();
                    listenerManager.callOnSyncContact("", chatResponse);

                    if (log) Log.i(TAG, "SYNC_CONTACT_COMPLETED");
                }

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                if (log) Log.e(TAG, jsonError);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_CONTACT_PERMISSION, ChatConstant.ERROR_CODE_READ_CONTACT_PERMISSION
                    , uniqueId);
            if (log) Log.e(TAG, jsonError);
        }
        return uniqueId;
    }

    /**
     * This method first check the type of the file and then choose the right
     * server and send that
     *
     * @param description    Its the description that you want to send with file in the thread
     * @param fileUri        Uri of the file that you want to send to thread
     * @param threadId       Id of the thread that you want to send file
     * @param systemMetaData [optional]
     * @param handler        it is for send file message with progress
     */
    public String sendFileMessage(Activity activity, String description, long threadId
            , Uri fileUri, String systemMetaData, Integer messageType, ProgressHandler.sendFileMessage handler) {
        String uniqueId;

        uniqueId = generateUniqueId();

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setActivity(activity);
        lFileUpload.setDescription(description);
        lFileUpload.setFileUri(fileUri);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);

        try {
            if (fileUri != null) {
                File file = new File(fileUri.getPath());
                String mimeType = handleMimType(fileUri, file);
                lFileUpload.setMimeType(mimeType);

                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    uploadFileMessage(lFileUpload);
                }
                return uniqueId;
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_INVALID_FILE_URI
                        , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                listenerManager.callOnLogEvent(jsonError);
                if (handler != null) {
                    handler.onError(jsonError, error);
                }
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
            return null;
        }
        return uniqueId;
    }

    /**
     * This method first check the type of the file and then choose the right
     * server and send that
     * <p>
     * description    Its the description that you want to send with file in the thread
     * fileUri        Uri of the file that you want to send to thread
     * threadId       Id of the thread that you want to send file
     * systemMetaData [optional]
     * handler        it is for send file message with progress
     */
    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
        long threadId = requestFileMessage.getThreadId();
        Activity activity = requestFileMessage.getActivity();
        Uri fileUri = requestFileMessage.getFileUri();
        String description = requestFileMessage.getDescription();
        int messageType = requestFileMessage.getMessageType();
        String systemMetadata = requestFileMessage.getSystemMetadata();

        return sendFileMessage(activity, description, threadId, fileUri, systemMetadata, messageType, handler);
    }

    @Deprecated
    public String uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            if (fileServer != null) {
                if (Permission.Check_READ_STORAGE(activity)) {
                    String mimeType = context.getContentResolver().getType(fileUri);
                    RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                    FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                    File file = new File(getRealPathFromURI(context, fileUri));
                    if (!Util.isNullOrEmpty(mimeType) && FileUtils.isImage(mimeType)) {

                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                                handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFinish() {

                            }
                        });

                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                        Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
                        uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadResponse -> {
                            if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {

                                boolean hasError = fileUploadResponse.body().isHasError();
                                if (hasError) {
                                    String errorMessage = fileUploadResponse.body().getMessage();
                                    if (Util.isNullOrEmpty(errorMessage)) {
                                        errorMessage = "";
                                    }
                                    int errorCode = fileUploadResponse.body().getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                    if (log) Log.e(TAG, jsonError);
                                } else {
                                    FileImageUpload fileImageUpload = fileUploadResponse.body();

                                    ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                    ResultImageFile resultImageFile = new ResultImageFile();
                                    chatResponse.setUniqueId(uniqueId);
                                    resultImageFile.setId(fileImageUpload.getResult().getId());
                                    resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                    resultImageFile.setName(fileImageUpload.getResult().getName());
                                    resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                    resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                    resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                    resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());

                                    chatResponse.setResult(resultImageFile);

                                    String imageJson = gson.toJson(chatResponse);

                                    if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
                                    listenerManager.callOnLogEvent(imageJson);

                                    handler.onFinish(imageJson, chatResponse);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, null);
                                String jsonError = gson.toJson(error);
                                handler.onError(jsonError, error);
                                if (log) Log.e(TAG, throwable.getMessage());
                            }
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        handler.onError(jsonError, error);
                        if (log) Log.e(TAG, jsonError);
                        return null;
                    }
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                    handler.onError(jsonError, error);
                    if (log) Log.e(TAG, jsonError);
                    return null;
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                handler.onError(jsonError, error);
                if (log) Log.e(TAG, "FileServer url Is null");
                return null;
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            listenerManager.callOnLogEvent(jsonError);
        }
        return uniqueId;
    }

    /**
     * It uploads image to the server just by pass image uri
     */
    @Deprecated
    public String uploadImage(Activity activity, Uri fileUri) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            try {
                if (fileServer != null && fileUri != null) {
                    if (Permission.Check_READ_STORAGE(activity)) {
                        String path = FilePick.getSmartFilePath(getContext(), fileUri);
                        if (Util.isNullOrEmpty(path)) {
                            path = "";
                        }
                        File file = new File(path);
                        if (file.exists()) {
                            String mimeType = handleMimType(fileUri, file);
                            if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                                Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
                                String finalUniqueId = uniqueId;
                                String finalUniqueId1 = uniqueId;
                                uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileImageUpload>>() {
                                    @Override
                                    public void call(Response<FileImageUpload> fileUploadResponse) {
                                        if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                                            boolean hasError = fileUploadResponse.body().isHasError();
                                            if (hasError) {
                                                String errorMessage = fileUploadResponse.body().getMessage();
                                                int errorCode = fileUploadResponse.body().getErrorCode();
                                                String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId1);
                                                if (log) Log.e(TAG, jsonError);
                                            } else {
                                                FileImageUpload fileImageUpload = fileUploadResponse.body();
                                                ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                                ResultImageFile resultImageFile = new ResultImageFile();
                                                chatResponse.setUniqueId(finalUniqueId);
                                                resultImageFile.setId(fileImageUpload.getResult().getId());
                                                resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                                resultImageFile.setName(fileImageUpload.getResult().getName());
                                                resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                                resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                                resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                                resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());

                                                chatResponse.setResult(resultImageFile);

                                                String imageJson = gson.toJson(chatResponse);

                                                listenerManager.callOnUploadImageFile(imageJson, chatResponse);
                                                if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
                                                listenerManager.callOnLogEvent(imageJson);
                                            }
                                        }
                                    }
                                }, throwable -> {
                                    String jsonError = getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, null);
                                    if (log) Log.e(TAG, jsonError);
                                });
                            } else {
                                String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                                if (log) Log.e(TAG, jsonError);
                                uniqueId = null;
                            }
                        }
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                        if (log) Log.e(TAG, jsonError);
                        uniqueId = null;
                    }
                } else {
                    if (log) Log.e(TAG, "FileServer url Is null");
                    uniqueId = null;
                }
            } catch (Exception e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
                uniqueId = null;
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It uploads image to the server just by pass image uri
     */
    public String uploadImage(RequestUploadImage requestUploadImage) {
        Activity activity = requestUploadImage.getActivity();
        Uri fileUri = requestUploadImage.getFileUri();
        return uploadImage(activity, fileUri);
    }

    public String uploadImageProgress(RequestUploadImage requestUploadImage, ProgressHandler.onProgress handler) {
        Activity activity = requestUploadImage.getActivity();
        Uri fileUri = requestUploadImage.getFileUri();
        return uploadImageProgress(getContext(), activity, fileUri, handler);
    }

    /**
     * It uploads file to file server
     */
    @Deprecated
    public String uploadFile(@NonNull Activity activity, @NonNull Uri uri) {

        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            try {
                if (Permission.Check_READ_STORAGE(activity)) {
                    if (getFileServer() != null) {
                        String path = FilePick.getSmartFilePath(getContext(), uri);
                        if (Util.isNullOrEmpty(path)) {
                            path = "";
                        }

                        File file = new File(path);
                        String mimeType = handleMimType(uri, file);
                        if (file.exists()) {
                            long fileSize = file.length();
                            RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                            FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                            Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                            uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadResponse -> {
                                if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                                    boolean hasError = fileUploadResponse.body().isHasError();
                                    if (hasError) {
                                        String errorMessage = fileUploadResponse.body().getMessage();
                                        int errorCode = fileUploadResponse.body().getErrorCode();
                                        String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                        if (log) Log.e(TAG, jsonError);
                                    } else {
                                        ResultFile result = fileUploadResponse.body().getResult();

                                        ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                        result.setSize(fileSize);
                                        chatResponse.setUniqueId(uniqueId);
                                        chatResponse.setResult(result);
                                        String json = gson.toJson(chatResponse);

                                        listenerManager.callOnUploadFile(json, chatResponse);
                                        if (log) Log.i(TAG, "RECEIVE_UPLOAD_FILE");
                                        listenerManager.callOnLogEvent(json);
                                    }
                                }
                            }, throwable -> {
                                String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                                if (log) Log.e(TAG, jsonError);
                            });

//                            uploadObservable.unsubscribeOn(Schedulers.io());

                        } else {
                            if (log) Log.e(TAG, "File is not Exist");
                            return null;
                        }
                    } else {
                        if (log) Log.e(TAG, "FileServer url Is null");
                        return null;
                    }
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    if (log) Log.e(TAG, jsonError);
                    return uniqueId;
                }
            } catch (Exception e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
                return uniqueId;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    public String uploadFile(@NonNull RequestUploadFile requestUploadFile) {
        return uploadFile(requestUploadFile.getActivity(), requestUploadFile.getFileUri());
    }

    /**
     * It uploads file and it shows progress of the file downloading
     */
    public String uploadFileProgress(Context context, Activity activity, String fileUri, Uri uri, ProgressHandler.onProgressFile handler) {
        String uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                if (Permission.Check_READ_STORAGE(activity)) {
                    if (getFileServer() != null) {
                        String mimeType = context.getContentResolver().getType(uri);
//                    File file = new File(getRealPathFromURI(context, uri));
                        String path = FilePick.getSmartFilePath(getContext(), uri);
                        File file = new File(path);

                        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                                handler.onProgress(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFinish() {

                            }
                        });

                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                        uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<FileUpload>>() {
                            @Override
                            public void call(Response<FileUpload> fileUploadResponse) {
                                if (fileUploadResponse.isSuccessful()) {
                                    boolean hasError = fileUploadResponse.body().isHasError();
                                    if (hasError) {
                                        String errorMessage = fileUploadResponse.body().getMessage();
                                        int errorCode = fileUploadResponse.body().getErrorCode();
                                        String jsonError = getErrorOutPut(errorMessage, errorCode, null);
                                        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, null);
                                        handler.onError(jsonError, error);
                                    } else {
                                        FileUpload result = fileUploadResponse.body();
                                        String json = gson.toJson(result);

                                        handler.onFinish(json, result);
                                        listenerManager.callOnLogEvent(json);
                                    }
                                }
                            }
                        }, throwable -> {
                            ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, uniqueId);
                            String json = gson.toJson(error);
                            handler.onError(json, error);
                            if (log) Log.e(TAG, throwable.getMessage());
                        });
                    } else {

                        if (log) Log.e(TAG, "FileServer url Is null");
                    }

                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    handler.onError(jsonError, error);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
        return uniqueId;
    }

    /**
     * If you want to resend the message that is stored in waitQueue
     *
     * @param uniqueId the unique id of the waitQueue message
     */
    public void resendMessage(String uniqueId) {
        if (uniqueId != null) {
            SendingQueueCache sendingQueueCache;
            if (cache) {
                sendingQueueCache = messageDatabaseHelper.getWaitQueueMsgByUnique(uniqueId);
                setThreadCallbacks(sendingQueueCache.getThreadId(), uniqueId);
                messageDatabaseHelper.deleteWaitQueueMsgs(uniqueId);
                messageDatabaseHelper.insertSendingMessageQueue(sendingQueueCache);
            } else {
                sendingQueueCache = sendingQList.get(uniqueId);
            }

            JsonObject jsonObject = (new JsonParser()).parse(sendingQueueCache.getAsyncContent()).getAsJsonObject();
            jsonObject.remove("token");
            jsonObject.addProperty("token", getToken());

            sendAsyncMessage(jsonObject.toString(), 4, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
        }
    }

    /**
     * It cancels message if its still in the Queue
     */
    public void cancelMessage(String uniqueId) {
        if (uniqueId != null) {
            if (cache) {
                messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
                messageDatabaseHelper.deleteWaitQueueMsgs(uniqueId);
            } else {
                sendingQList.remove(uniqueId);
                waitQList.remove(uniqueId);
            }
        }
    }

    public void cancelUpload(String uniqueId) {
        if (uniqueId != null) {
            if (cache) {
                messageDatabaseHelper.deleteUploadingQueue(uniqueId);
            } else {
                uploadingQList.remove(uniqueId);
            }
            cancelUpload.cancelUpload(uniqueId);
        }
    }

    /**
     * It retry upload that they didn't send
     */

    public void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler) {
        String uniqueId = retry.getUniqueId();
        Activity activity = retry.getActivity();

        UploadingQueueCache uploadingQ;
        if (cache) {
            uploadingQ = messageDatabaseHelper.getUploadingQ(uniqueId);
        } else {
            uploadingQ = uploadingQList.get(uniqueId);
        }

        if (uploadingQ != null) {
            long messageId = uploadingQ.getId();
            int messageType = uploadingQ.getMessageType();
            long threadId = uploadingQ.getThreadId();
            String message = uploadingQ.getMessage();
            String systemMetadata = uploadingQ.getSystemMetadata();
            MetaDataFile metaDataFile = gson.fromJson(systemMetadata, MetaDataFile.class);
            String link = metaDataFile.getFile().getLink();
            String mimeType = metaDataFile.getFile().getMimeType();

            LFileUpload lFileUpload = new LFileUpload();
            lFileUpload.setActivity(activity);
            lFileUpload.setDescription(message);
            lFileUpload.setFileUri(Uri.parse(link));
            lFileUpload.setHandler(handler);
            lFileUpload.setMessageType(messageType);
            lFileUpload.setThreadId(threadId);
            lFileUpload.setUniqueId(uniqueId);
            lFileUpload.setSystemMetaData(systemMetadata);
            lFileUpload.setHandler(handler);
            lFileUpload.setMimeType(mimeType);

            if (!Util.isNullOrEmpty(messageId)) {
                String methodName = ChatConstant.METHOD_REPLY_MSG;
                lFileUpload.setMethodName(methodName);
            }
            if (FileUtils.isImage(mimeType)) {

                if (cache) {
                    messageDatabaseHelper.deleteUploadingQueue(uniqueId);
                } else {
                    uploadingQList.remove(uniqueId);
                }
                uploadImageFileMessage(lFileUpload);
            } else {
                if (cache) {
                    messageDatabaseHelper.deleteUploadingQueue(uniqueId);
                } else {
                    uploadingQList.remove(uniqueId);
                }
                uploadFileMessage(lFileUpload);
            }
        }
    }

    /**
     * This method generate url that you can use to get your file
     */
    public String getFile(long fileId, String hashCode, boolean downloadable) {
        return getFileServer() + "nzh/file/" + "?fileId=" + fileId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
    }

    /**
     * This method generate url that you can use to get your file
     */
    public String getFile(RequestGetFile requestGetFile) {
        return getFileServer() + "nzh/file/"
                + "?fileId=" + requestGetFile.getFileId()
                + "&downloadable=" + requestGetFile.isDownloadable()
                + "&hashCode=" + requestGetFile.getHashCode();
    }

    /**
     * This method generate url based on your input params that you can use to get your image
     */
    public String getImage(long imageId, String hashCode, boolean downloadable) {
        String url;
        if (downloadable) {
            url = getFileServer() + "nzh/image/" + "?imageId=" + imageId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
        } else {
            url = getFileServer() + "nzh/image/" + "?imageId=" + imageId + "&hashCode=" + hashCode;
        }
        return url;
    }

    /**
     * This method generate url based on your input params that you can use to get your image
     */
    public String getImage(RequestGetImage requestGetImage) {
        String url;
        if (requestGetImage.isDownloadable()) {
            url = getFileServer() + "nzh/image/"
                    + "?imageId=" + requestGetImage.getImageId()
                    + "&downloadable=" + requestGetImage.isDownloadable()
                    + "&hashCode=" + requestGetImage.getHashCode();
        } else {
            url = getFileServer() + "nzh/image/"
                    + "?imageId=" + requestGetImage.getImageId()
                    + "&hashCode=" + requestGetImage.getHashCode();
        }
        return url;
    }

    /**
     * Remove the peerId and send ping again but this time
     * peerId that was set in the server was removed
     */
    public void logOutSocket() {

        signalMessageHandlerThread.quit();

        async.logOut();

    }

    /**
     * Notice : You should consider that this method is for rename group and you have to be the admin
     * to change the thread name if not you don't have the Permission
     */
    @Deprecated
    public String renameThread(long threadId, String title, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.RENAME);
        chatMessage.setSubjectId(threadId);
        chatMessage.setContent(title);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        setCallBacks(null, null, null, true, Constants.RENAME, null, uniqueId);
        String asyncContent = gson.toJson(chatMessage);
        sendAsyncMessage(asyncContent, 4, "SEND_RENAME_THREAD");
        if (handler != null) {
            handler.onRenameThread(uniqueId);
        }
        return uniqueId;
    }

    /**
     * @param contactIds List of CONTACT IDs
     * @param threadId   Id of the thread that you are {*NOTICE*}admin of that and you want to
     *                   add someone as a participant.
     */
    public String addParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                AddParticipant addParticipant = new AddParticipant();
                addParticipant.setSubjectId(threadId);
                addParticipant.setUniqueId(uniqueId);
                JsonArray contacts = new JsonArray();
                for (Long p : contactIds) {
                    contacts.add(p);
                }
                addParticipant.setContent(contacts.toString());
                addParticipant.setSubjectId(threadId);
                addParticipant.setToken(getToken());
                addParticipant.setTokenIssuer("1");
                addParticipant.setUniqueId(uniqueId);
                addParticipant.setType(Constants.ADD_PARTICIPANT);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(addParticipant);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.ADD_PARTICIPANT, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_ADD_PARTICIPANTS");
                if (handler != null) {
                    handler.onAddParticipants(uniqueId);
                }

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }


        } catch (Throwable t) {
            if (log) Log.e(TAG, t.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * contactIds  List of CONTACT IDs
     * threadId   Id of the thread that you are {*NOTICE*}admin of that and you are going to
     * add someone as a participant.
     */
    public String addParticipants(RequestAddParticipants request, ChatHandler handler) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            JsonArray contacts = new JsonArray();
            for (Long p : request.getContactIds()) {
                contacts.add(p);
            }
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setContent(contacts.toString());
            chatMessage.setSubjectId(request.getThreadId());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setType(Constants.ADD_PARTICIPANT);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String typeCode = request.getTypeCode();
            if (Util.isNullOrEmpty(typeCode)) {
                jsonObject.addProperty("typeCode", getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            setCallBacks(null, null, null, true, Constants.ADD_PARTICIPANT, null, uniqueId);
            sendAsyncMessage(jsonObject.toString(), 4, "SEND_ADD_PARTICIPANTS");
            if (handler != null) {
                handler.onAddParticipants(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * @param participantIds List of PARTICIPANT IDs that gets from {@link #getThreadParticipants}
     * @param threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();
            removeParticipant.setTokenIssuer("1");
            removeParticipant.setType(Constants.REMOVE_PARTICIPANT);
            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(getToken());
            removeParticipant.setUniqueId(uniqueId);

            JsonArray contacts = new JsonArray();
            for (Long p : participantIds) {
                contacts.add(p);
            }
            removeParticipant.setContent(contacts.toString());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_REMOVE_PARTICIPANT");
            setCallBacks(null, null, null, true, Constants.REMOVE_PARTICIPANT, null, uniqueId);
            if (handler != null) {
                handler.onRemoveParticipants(uniqueId);
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * participantIds List of PARTICIPANT IDs from Thread's Participants object
     * threadId       Id of the thread that we wants to remove their participant
     */
    public String removeParticipants(RequestRemoveParticipants request, ChatHandler handler) {

        List<Long> participantIds = request.getParticipantIds();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return removeParticipants(threadId, participantIds, handler);
    }

    /**
     * It leaves the thread that you are in there
     */
    public String leaveThread(long threadId, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        if (chatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(getToken());
            removeParticipant.setTokenIssuer("1");
            removeParticipant.setUniqueId(uniqueId);
            removeParticipant.setType(Constants.LEAVE_THREAD);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.LEAVE_THREAD, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_LEAVE_THREAD");

            if (handler != null) {
                handler.onLeaveThread(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * leaves the thread
     *
     * @ param threadId id of the thread
     */
    public String leaveThread(RequestLeaveThread request, ChatHandler handler) {

        return leaveThread(request.getThreadId(), handler);
    }

    /**
     * forward message
     *
     * @param threadId   destination thread id
     * @param messageIds Array of message ids that we want to forward them
     */
    public List<String> forwardMessage(long threadId, ArrayList<Long> messageIds) {
        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<Callback> callbacks = new ArrayList<>();

        for (long messageId : messageIds) {
            String uniqueId = generateUniqueId();
            uniqueIds.add(uniqueId);
            Callback callback = new Callback();
            callback.setDelivery(true);
            callback.setSeen(true);
            callback.setSent(true);
            callback.setUniqueId(uniqueId);
            callbacks.add(callback);
            /*
             * add to message Queue
             * */
            SendingQueueCache sendingQueue = new SendingQueueCache();
            sendingQueue.setUniqueId(uniqueId);

            MessageVO messageVO = new MessageVO();
            messageVO.setId(messageId);
            messageVO.setUniqueId(uniqueId);

            sendingQueue.setThreadId(threadId);
            String queueAsyncContent = CreateAsyncContentForQueue(threadId, messageId, uniqueId);
            sendingQueue.setAsyncContent(queueAsyncContent);
            if (cache) {
                messageDatabaseHelper.insertSendingMessageQueue(sendingQueue);
            } else {
                sendingQList.put(uniqueId, sendingQueue);
            }
        }

        if (log)
            Log.i(TAG, "Messages " + messageIds + "with this" + "uniqueIds" + uniqueIds + "has been added to Message Queue");

        if (chatReady) {
            ChatMessageForward chatMessageForward = new ChatMessageForward();
//            ObjectMapper mapper = new ObjectMapper();
            chatMessageForward.setSubjectId(threadId);

            threadCallbacks.put(threadId, callbacks);
            String jsonUniqueIds = Util.listToJson(uniqueIds, gson);
            chatMessageForward.setUniqueId(jsonUniqueIds);

            chatMessageForward.setContent(messageIds.toString());
            chatMessageForward.setToken(getToken());
            chatMessageForward.setTokenIssuer("1");
            chatMessageForward.setType(Constants.FORWARD_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageForward);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();
            for (String uniqueId : uniqueIds) {
                SendingQueueCache queueCache;
                if (cache) {
                    queueCache = messageDatabaseHelper.getSendingQueueCache(uniqueId);
                    messageDatabaseHelper.insertWaitMessageQueue(queueCache);
                    messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
                } else {
                    queueCache = sendingQList.get(uniqueId);
                    WaitQueueCache waitMessageQueue = new WaitQueueCache();

                    waitMessageQueue.setUniqueId(queueCache.getUniqueId());
                    waitMessageQueue.setId(queueCache.getId());
                    waitMessageQueue.setAsyncContent(queueCache.getAsyncContent());
                    waitMessageQueue.setMessage(queueCache.getMessage());
                    waitMessageQueue.setThreadId(queueCache.getThreadId());
                    waitMessageQueue.setMessageType(queueCache.getMessageType());
                    waitMessageQueue.setSystemMetadata(queueCache.getSystemMetadata());
                    waitMessageQueue.setMetadata(queueCache.getMetadata());
                    waitQList.put(uniqueId, waitMessageQueue);
                }
            }
            sendAsyncMessage(asyncContent, 4, "SEND_FORWARD_MESSAGE");
        } else {
            if (Util.isNullOrEmpty(uniqueIds)) {
                for (String uniqueId : uniqueIds) {
                    getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }
            }
        }
        return uniqueIds;
    }


    /**
     * forward message
     * <p>
     * threadId   destination thread id
     * messageIds Array of message ids that we want to forward them
     */
    public List<String> forwardMessage(RequestForwardMessage request) {
        return forwardMessage(request.getThreadId(), request.getMessageIds());
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId       id of the thread
     * messageId      of the message that we want to reply
     * metaData       meta data of the message
     */
    public String replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler) {
        String uniqueId = generateUniqueId();
        long threadId = request.getThreadId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        Uri fileUri = request.getFileUri();
        long messageId = request.getMessageId();
        Activity activity = request.getActivity();
        int messageType = request.getMessageType();
        String methodName = ChatConstant.METHOD_REPLY_MSG;

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setActivity(activity);
        lFileUpload.setDescription(messageContent);
        lFileUpload.setFileUri(fileUri);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setMessageId(messageId);
        lFileUpload.setMethodName(methodName);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);

        try {
            if (fileUri != null) {
                File file = new File(fileUri.getPath());
                String mimeType = handleMimType(fileUri, file);
                lFileUpload.setMimeType(mimeType);
                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    String path = FilePick.getSmartFilePath(context, fileUri);
                    uploadFileMessage(lFileUpload);
                }
                return uniqueId;
            } else {
                getErrorOutPut(ChatConstant.ERROR_INVALID_URI, ChatConstant.ERROR_CODE_INVALID_URI, uniqueId);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
            return null;
        }
        return uniqueId;
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId       id of the thread
     * messageId      of the message that we want to reply
     * metaData       meta data of the message
     */
    public String replyMessage(RequestReplyMessage request, ChatHandler handler) {
        long threadId = request.getThreadId();
        long messageId = request.getMessageId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        int messageType = request.getMessageType();

        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, handler);
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     *
     * @param messageContent content of the reply message
     * @param threadId       id of the thread
     * @param messageId      of the message that we want to reply
     * @param systemMetaData meta data of the message
     */
    public String replyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, ChatHandler handler) {
        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, handler);
    }

    /**
     * It deletes message from the thread
     *
     * @param messageIds   Id of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u don't want
     *                     you can set it false or even null.
     */


    public String deleteMessage(ArrayList<Long> messageIds, long threadId, Boolean deleteForAll, ChatHandler handler) {


        Log.d(MTAG, "Multiple Message delete");


        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();


        if (chatReady) {


            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();


            for (Long id :
                    messageIds) {

                String uniqueId1 = generateUniqueId();

                uniqueIds.add(uniqueId1);

                setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId1);


            }

            JsonObject contentObj = new JsonObject();


            JsonElement messageIdsElement = gson
                    .toJsonTree(messageIds, new TypeToken<List<Long>>() {
                    }.getType());

            JsonElement uniqueIdsElement = gson.toJsonTree(uniqueIds, new TypeToken<List<String>>() {
            }.getType());


            contentObj.add("ids", messageIdsElement.getAsJsonArray());
            contentObj.add("uniqueIds", uniqueIdsElement.getAsJsonArray());
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer("1");
            baseMessage.setType(Constants.DELETE_MESSAGE);
//            baseMessage.setUniqueId(uniqueId);
            baseMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);


//            jsonObject.remove("subjectId");
            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    private String deleteMessage(Long messageId, Boolean deleteForAll, ChatHandler handler) {


        Log.d(MTAG, "Single Message delete");
        String uniqueId;
        uniqueId = generateUniqueId();


        if (chatReady) {
            deleteForAll = deleteForAll != null ? deleteForAll : false;
            BaseMessage baseMessage = new BaseMessage();

            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(getToken());
            baseMessage.setTokenIssuer("1");
            baseMessage.setType(Constants.DELETE_MESSAGE);
            baseMessage.setUniqueId(uniqueId);
            baseMessage.setSubjectId(messageId);


            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);


            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 4, "SEND_DELETE_MESSAGE");

            setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId);


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }


        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }


        return uniqueId;


    }

    /**
     * DELETE MESSAGE IN THREAD
     * <p>
     * messageId    Id of the message that you want to be removed.
     * deleteForAll If you want to delete message for everyone you can set it true if u don't want
     * you can set it false or even null.
     */
    public String deleteMessage(RequestDeleteMessage request, ChatHandler handler) {


        if (request.getMessageIds().size() > 1)
            return deleteMessage(request.getMessageIds(), request.getThreadId(), request.isDeleteForAll(), handler);
        else if (request.getMessageIds().size() == 1) {
            return deleteMessage(request.getMessageIds().get(0), request.isDeleteForAll(), handler);
        } else {

            return null;
        }
    }


    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count  number of thread
     * @param offset specified offset you want
     */


    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds,
                             String threadName, ChatHandler handler) {

        String uniqueId;

        count = count != null ? count : 50;

        uniqueId = generateUniqueId();

        try {
            if (cache) {
                handleCacheThread(count, offset, threadIds, threadName);
            }

            if (chatReady) {

                ChatMessageContent chatMessageContent = new ChatMessageContent();

                Long offsets;

                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                    offsets = offset;
                } else {
                    chatMessageContent.setOffset(0);
                    offsets = 0L;
                }

                chatMessageContent.setCount(count);

                if (threadName != null) {
                    chatMessageContent.setName(threadName);
                }
                JsonObject jObj;

                if (threadIds != null && threadIds.size() > 0) {
                    chatMessageContent.setThreadIds(threadIds);
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);

                } else {
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
                    jObj.remove("threadIds");
                }

                jObj.remove("lastMessageId");
                jObj.remove("firstMessageId");
                jObj.remove("new");
//                jObj.remove("count");
//                jObj.remove("offset");
                jObj.addProperty("summary", true);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(Constants.GET_THREADS);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, Constants.GET_THREADS, offsets, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 3, "SEND_GET_THREADS");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param creatorCoreUserId    if it sets to '0' its considered as it was'nt set
     * @param partnerCoreUserId    if it sets to '0' its considered as it was'nt set -
     *                             it gets threads of p2p not groups
     * @param partnerCoreContactId if it sets to '0' its considered as it was'nt set-
     *                             it gets threads of p2p not groups
     * @param count                Count of the list
     * @param offset               Offset of the list
     * @param handler              Its not working yet
     * @param threadIds            List of thread ids that you want to get
     * @param threadName           Name of the thread that you want to get
     */
    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,
                             long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler) {

        String uniqueId;
        count = count != null ? count : 50;
        uniqueId = generateUniqueId();
        try {

            if (cache) {
                handleCacheThread(count, offset, threadIds, threadName);
            }

            if (chatReady) {
                ChatMessageContent chatMessageContent = new ChatMessageContent();

                Long offsets;
                if (offset != null) {
                    chatMessageContent.setOffset(offset);
                    offsets = offset;
                } else {
                    chatMessageContent.setOffset(0);
                    offsets = 0L;
                }

                chatMessageContent.setCount(count);
                if (threadName != null) {
                    chatMessageContent.setName(threadName);
                }
                JsonObject jObj;

                if (threadIds != null && threadIds.size() > 0) {
                    chatMessageContent.setThreadIds(threadIds);
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);

                } else {
                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
                    jObj.remove("threadIds");
                }


                if (creatorCoreUserId > 0) {
                    jObj.addProperty("creatorCoreUserId", creatorCoreUserId);
                }
                if (partnerCoreUserId > 0) {
                    jObj.addProperty("partnerCoreUserId", partnerCoreUserId);
                }
                if (partnerCoreContactId > 0) {
                    jObj.addProperty("partnerCoreContactId", partnerCoreContactId);
                }

                jObj.remove("lastMessageId");
                jObj.remove("firstMessageId");
                jObj.remove("new");

                jObj.addProperty("summary", true);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(Constants.GET_THREADS);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, Constants.GET_THREADS, offsets, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 3, "SEND_GET_THREADS");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }
        return uniqueId;
    }


    public String getThreads(RequestThread requestThread, ChatHandler handler) {

        ArrayList<Integer> threadIds = requestThread.getThreadIds();
        long offset = requestThread.getOffset();
        long creatorCoreUserId = requestThread.getCreatorCoreUserId();
        long partnerCoreContactId = requestThread.getPartnerCoreContactId();
        long partnerCoreUserId = requestThread.getPartnerCoreUserId();
        String threadName = requestThread.getThreadName();
        long count = requestThread.getCount();
        return getThreads((int) count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId, handler);
    }

    /*
      firstMessageId is going to deprecate
      lastMessageId is going to deprecate
      */

    /**
     * Get history of the thread
     * <p>
     * count    count of the messages
     * order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * and order must be lower case
     * lastMessageId
     * FirstMessageId
     * {@link #checkMessagesValidation(long)} It is checked that if the message had been sent then its removed
     * it from the messageQueue.
     *
     * @param threadId Id of the thread that we want to get the history
     */
    @Deprecated
    public String getHistory(History history, long threadId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();

        if (history.getCount() != 0) {
            history.setCount(history.getCount());
        } else {
            history.setCount(50);
        }

        if (history.getOffset() != 0) {
            history.setOffset(history.getOffset());
        } else {
            history.setOffset(0);
        }

        if (cache) {
            List<MessageVO> messageVOS = messageDatabaseHelper.getHistories(history, threadId);
            checkMessagesValidation(threadId);
            if (messageVOS != null) {

                long contentCount = messageDatabaseHelper.getHistoryContentCount(threadId);

                ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();

                ResultHistory resultHistory = new ResultHistory();
                resultHistory.setHistory(messageVOS);

                resultHistory.setNextOffset(history.getOffset() + messageVOS.size());
                resultHistory.setContentCount(contentCount);
                if (messageVOS.size() + history.getOffset() < contentCount) {
                    resultHistory.setHasNext(true);
                } else {
                    resultHistory.setHasNext(false);
                }

                resultHistory.setHistory(messageVOS);

                resultHistory.setSending(messageDatabaseHelper.getAllSendingQueueByThreadId(threadId));
                resultHistory.setUploadingQueue(messageDatabaseHelper.getAllUploadingQueueByThreadId(threadId));
                resultHistory.setFailed(messageDatabaseHelper.getAllWaitQueueCacheByThreadId(threadId));

                chatResponse.setErrorCode(0);
                chatResponse.setHasError(false);
                chatResponse.setErrorMessage("");
                chatResponse.setResult(resultHistory);
                chatResponse.setUniqueId(uniqueId);
                chatResponse.setCache(true);

                String json = gson.toJson(chatResponse);
                listenerManager.callOnGetThreadHistory(json, chatResponse);
                showLog("CACHE_GET_HISTORY", json);
            }
        }

        if (chatReady) {
            getHistoryMain(history, threadId, handler, uniqueId);
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Gets history of the thread
     * <p>
     *
     * @Param count    count of the messages
     * @Param order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * lastMessageId
     * FirstMessageId
     * @Param long threadId   Id of the thread
     * @Param long fromTime    Start Time of the messages
     * @Param long fromTimeNanos  Start Time of the messages in Nano second
     * @Param long toTime         End time of the messages
     * @Param long toTimeNanos    End time of the messages
     * @Param @Deprecated long firstMessageId
     * @Param @Deprecated long lastMessageId
     *
     * <p>
     * threadId Id of the thread that we want to get the history
     */
    public String getHistory(RequestGetHistory request, ChatHandler handler) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            History history = new History.Builder()
                    .count(request.getCount())
                    .firstMessageId(request.getFirstMessageId())
                    .lastMessageId(request.getLastMessageId())
                    .offset(request.getOffset())
                    .fromTime(request.getFromTime())
                    .fromTimeNanos(request.getFromTimeNanos())
                    .toTime(request.getToTime())
                    .toTimeNanos(request.getToTimeNanos())
                    .order(request.getOrder()).build();
            getHistoryMain(history, request.getThreadId(), handler, uniqueId);

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * Gets history of the thread
     * <p>
     *
     * @Param count    count of the messages
     * @Param order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * lastMessageId
     * FirstMessageId
     * @Param long threadId   Id of the thread
     * @Param long userId
     * @Param long id         Id of the message
     * @Param String query
     * @Param long fromTime    Start Time of the messages
     * @Param long fromTimeNanos  Start Time of the messages in Nano second
     * @Param long toTime         End time of the messages
     * @Param long toTimeNanos    End time of the messages
     * @Param NosqlSearchMetadataCriteria metadataCriteria
     * ------ String field
     * ------ String is
     * ------ String has
     * ------ String gt
     * ------ String gte
     * ------ String lt
     * ------ String lte
     * ------ List<NosqlSearchMetadataCriteria> and
     * ------ List<NosqlSearchMetadataCriteria> or
     * ------ List<NosqlSearchMetadataCriteria> not
     **/
    public String searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            String content = gson.toJson(messageCriteriaVO);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(Constants.GET_HISTORY);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(messageCriteriaVO.getMessageThreadId());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.GET_HISTORY, messageCriteriaVO.getOffset(), uniqueId);
            sendAsyncMessage(asyncContent, 3, "SEND SEARCH0. HISTORY");
            if (handler != null) {
                handler.onSearchHistory(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * You an have Signal Type from ChatMessageType.SignalMsg{
     * IS_TYPING = 1 ;
     * int RECORD_VOICE = 2 ;
     * int UPLOAD_PICTURE = 3 ;
     * int UPLOAD_VIDEO = 4 ;
     * int UPLOAD_SOUND = 5 ;
     * int UPLOAD_FILE = 6 ;
     * }
     */

    public String startSignalMessage(RequestSignalMsg requestSignalMsg, String logMessage) {

        String uniqueId = generateUniqueId();

//        Log.d(MTAG, "Start of sending signal");

//        if (!Util.isNullOrEmpty(threadSignalsKeeper.get(requestSignalMsg.getThreadId()))) {
//
//            Log.d(MTAG, "this thread have another signals, stop last ones");
//
//            stopSignalMessage(getLastThreadSignalUniqueId(requestSignalMsg.getThreadId()), false);
//
//
//        } else {
//
//            threadSignalsKeeper.put(requestSignalMsg.getThreadId(), new ArrayList<>());
//
//        }

        Handler handler = new Handler(signalMessageHandlerThread.getLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                signalMessage(requestSignalMsg, logMessage, uniqueId);
                handler.postDelayed(this, getSignalIntervalTime());
            }
        };

        handler.post(runnable);

        signalHandlerKeeper.put(uniqueId, handler);

//        requestSignalsKeeper.put(uniqueId, requestSignalMsg);
//
//        threadSignalsKeeper.get(requestSignalMsg.getThreadId()).add(uniqueId);
//
//        Log.d(MTAG, "add new unique id to thread " + requestSignalMsg
//                .getThreadId() + " thread signal size: " + threadSignalsKeeper
//                .get(requestSignalMsg.getThreadId()).size());


//        Handler handler = new Handler(signalMessageHandlerThread.getLooper());
//
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg,logMessage,uniqueId);
//                        handler.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//
//
//                handler.post(runnable);


//                signalHandlerKeeper.put(uniqueId, handler);

//                threadSignalsManager.put(requestSignalMsg.getThreadId(),signalHandlerKeeper);


//        if (threadSignalsManager.get(requestSignalMsg.getThreadId()).size() > 1) {
//
//            LinkedHashMap<String, Handler> m = threadSignalsManager.get(requestSignalMsg.getThreadId());
//
//            List<Map.Entry<String, Handler>> entries = new ArrayList<Map.Entry<String, Handler>>(m.entrySet());
//
//            Map.Entry<String, Handler> lastEntry = entries.get(entries.size() - 1);
//
//            Handler v = lastEntry.getValue();
//
//            /**
//             should not keep a handler.
//             we should keep signal message details
//             and keep it in a queue. next get last item and
//             execute it until cancellation.
//             if new item come, it start executing and last item send to
//             queue with it unique id.
//             user can cancel it anytime with uniqueId.
//             anytime handler going to stop, it should check the queue.
//             **/
//
//
//        }


//        int signalType = requestSignalMsg.getSignalType();

//        switch (signalType) {
//
//            case ChatMessageType.SignalMsg.IS_TYPING:
//                Handler handler = new Handler();
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handler.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handler.post(runnable);
//                signalHandlerKeeper.put(uniqueId, handler);
//                break;
//
//            case ChatMessageType.SignalMsg.RECORD_VOICE:
//                Handler handlerRV = new Handler();
//                Runnable runnableRV = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handlerRV.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handlerRV.post(runnableRV);
//                signalHandlerKeeper.put(uniqueId, handlerRV);
//                break;
//
//            case ChatMessageType.SignalMsg.UPLOAD_FILE:
//                Handler handlerUF = new Handler();
//                Runnable runnableUF = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handlerUF.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handlerUF.post(runnableUF);
//                signalHandlerKeeper.put(uniqueId, handlerUF);
//                break;
//
//            case ChatMessageType.SignalMsg.UPLOAD_PICTURE:
//                Handler handlerUP = new Handler();
//                Runnable runnableUP = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handlerUP.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handlerUP.post(runnableUP);
//                signalHandlerKeeper.put(uniqueId, handlerUP);
//                break;
//
//            case ChatMessageType.SignalMsg.UPLOAD_SOUND:
//                Handler handlerUS = new Handler();
//                Runnable runnableUS = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handlerUS.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handlerUS.post(runnableUS);
//                signalHandlerKeeper.put(uniqueId, handlerUS);
//                break;
//
//            case ChatMessageType.SignalMsg.UPLOAD_VIDEO:
//                Handler handlerUV = new Handler();
//                Runnable runnableUV = new Runnable() {
//                    @Override
//                    public void run() {
//                        signalMessage(requestSignalMsg);
//                        handlerUV.postDelayed(this, getSignalIntervalTime());
//                    }
//                };
//                handlerUV.post(runnableUV);
//                signalHandlerKeeper.put(uniqueId, handlerUV);
//                break;
//        }
        return uniqueId;

    }

    private String getLastThreadSignalUniqueId(Long threadId) {

        ArrayList<String> handlersIds = threadSignalsKeeper.get(threadId);

        return handlersIds.get(handlersIds.size() - 1);
    }


//    private boolean stopSignalMessage(String uniqueId) {
//
//
//        Log.d(MTAG, "Request to stop from user");
//
//        Log.d(MTAG,"Unique id to remove: " + uniqueId);
//
//        if (signalHandlerKeeper.containsKey(uniqueId)) {
//
//            Log.d(MTAG,"signal handler keeper contains key: " + uniqueId);
//
//
//
//            //find thread of this signal
//            Long threadIdOfSignal = Util.findKeyWithUniqueValue(threadSignalsKeeper, uniqueId);
//
//            if (threadIdOfSignal == null) return true;
//
//            Log.d(MTAG, "found thread of signal with unique value: " + threadIdOfSignal);
//
//
//            //if found, get last signal unique key sent to thread
//            //
//            //remove last signal from threads signals queue
//
//            threadSignalsKeeper.get(threadIdOfSignal)
//                    .remove(threadSignalsKeeper.get(threadIdOfSignal)
//                            .get(threadSignalsKeeper
//                                    .get(threadIdOfSignal).size() - 1));
//
//            signalHandlerKeeper.get(uniqueId).removeCallbacksAndMessages(null);
//
//            signalHandlerKeeper.remove(uniqueId);
//
//            requestSignalsKeeper.remove(uniqueId);
//
//            Log.d(MTAG, "executed signal removed from handlers and request keepers");
//
//
//            showLog("STOP_SIGNAL", "");
//
//
//            Log.d(MTAG, "last signal removed from threads signal bc it is stopped");
//
//
//            ArrayList<String> threadSignalsKeys = threadSignalsKeeper.get(threadIdOfSignal);
//
//
//            //check if more signals are in queue
//            if (threadSignalsKeys.size() > 0) {
//
//                Log.d(MTAG, "there are more signal in thread to send: " + threadSignalsKeys.toString());
//
//
//                String lastOneUniqueId = threadSignalsKeys.get(threadSignalsKeys.size() - 1);
//
//                Log.d(MTAG,"last one: " + lastOneUniqueId);
//
////                RequestSignalMsg lastReq = requestSignalsKeeper.get(lastOneUniqueId);
//
//                //remove from requests queue
//                RequestSignalMsg lastReq = requestSignalsKeeper.get(lastOneUniqueId);
//
//                requestSignalsKeeper.remove(lastOneUniqueId);
//
//                if(lastReq == null) {
//
//                    Log.d(MTAG,"Request signal message is null");
//
//                    return true;
//                }
//
//                Log.d(MTAG, "keep last request and remove it from request signal queue " + lastReq.getSignalType());
//
//
//                //remove from thread signals
//                threadSignalsKeeper.get(threadIdOfSignal)
//                        .remove(threadSignalsKeeper.get(threadIdOfSignal)
//                                .get(threadSignalsKeeper
//                                        .get(threadIdOfSignal).size() - 1));
//
//
//                Log.d(MTAG, "remove last signal of thread. thread size: " + threadSignalsKeeper.get(threadIdOfSignal).size());
//
//
//                //send it to execute
//                startSignalMessage(lastReq, "SIGNAL MESSAGE TYPE " +
//                       lastReq.getSignalType());
//
//
//            } else {
//
//                Log.d(MTAG, "no signal remain in thread");
//
//            }
//
//
//            return true;
//
//        }
//
//        return false;
//    }


//    private void stopSignalMessage(String uniqueId, boolean checkQueue) {
//
//        Log.d(MTAG, "going to stop a signal in thread to add another one check queue: " + checkQueue);
//
//
//        if (signalHandlerKeeper.containsKey(uniqueId)) {
//
//            signalHandlerKeeper.get(uniqueId).removeCallbacksAndMessages(null);
//
////            signalHandlerKeeper.remove(uniqueId);
//
//            showLog("STOP_SIGNAL", "");
//
//        } else {
//
//            showLog("NO_SIGNAL_TO_STOP", "");
//
//        }
//
//
//    }

    private boolean stopSignalMessage(String uniqueId) {


        if (signalHandlerKeeper.containsKey(uniqueId)) {

            signalHandlerKeeper.get(uniqueId).removeCallbacksAndMessages(null);

            showLog("STOP_SIGNAL", "");

            return true;
        }

        return false;

    }


    public void stopAllSignalMessages() {


        for (String k :
                signalHandlerKeeper.keySet()) {

            signalHandlerKeeper.get(k).removeCallbacksAndMessages(null);

        }


    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(RequestGetContact request, ChatHandler handler) {

        Long offset = request.getOffset();
        Long count = request.getCount();
        return getContacts(count.intValue(), offset, handler);
    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(Integer count, Long offset, ChatHandler handler) {
        return getContactMain(count, offset, false, handler);
    }


    //TODO test again on cache
    public String searchContact(SearchContact searchContact) {
        String uniqueId = generateUniqueId();
        String type_code;

        if (cache) {
            List<Contact> contacts = new ArrayList<>();
            if (searchContact.getId() != null) {
                Contact contact = messageDatabaseHelper.getContactById(Long.valueOf(searchContact.getId()));
                contacts.add(contact);
            } else if (searchContact.getFirstName() != null) {
                contacts = messageDatabaseHelper.getContactsByFirst(searchContact.getFirstName());
            } else if (searchContact.getFirstName() != null && searchContact.getLastName() != null && !searchContact.getFirstName().isEmpty() && !searchContact.getLastName().isEmpty()) {
                contacts = messageDatabaseHelper.getContactsByFirstAndLast(searchContact.getFirstName(), searchContact.getLastName());
            } else if (searchContact.getEmail() != null && !searchContact.getEmail().isEmpty()) {
                contacts = messageDatabaseHelper.getContactsByEmail(searchContact.getEmail());
            } else if (searchContact.getCellphoneNumber() != null && !searchContact.getCellphoneNumber().isEmpty()) {
                contacts = messageDatabaseHelper.getContactByCell(searchContact.getCellphoneNumber());
            }

            ChatResponse<ResultContact> chatResponse = new ChatResponse<>();

            ResultContact resultContact = new ResultContact();
            ArrayList<Contact> listContact = new ArrayList<>(contacts);
            resultContact.setContacts(listContact);

            chatResponse.setHasError(false);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");
            chatResponse.setResult(resultContact);

            String jsonContact = gson.toJson(chatResponse);

            listenerManager.callOnLogEvent(jsonContact);
            if (log) Log.i(TAG, "CACHE_SEARCH_CONTACT");

        }

        if (searchContact.getTypeCode() != null && !searchContact.getTypeCode().isEmpty()) {
            type_code = searchContact.getTypeCode();
        } else {
            type_code = "1";
        }

        if (chatReady) {

            Observable<Response<SearchContactVO>> observable = contactApi.searchContact(
                    getToken(),
                    TOKEN_ISSUER,
                    searchContact.getId()
                    , searchContact.getFirstName()
                    , searchContact.getLastName()
                    , searchContact.getEmail()
                    , null
                    , searchContact.getOffset()
                    , searchContact.getSize()
                    , null
                    , searchContact.getQuery()
                    , searchContact.getCellphoneNumber());

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(contactResponse -> {


                        if (contactResponse.isSuccessful()) {

                            if (contactResponse.body().getResult() != null) {
                                ArrayList<Contact> contacts = new ArrayList<>(contactResponse.body().getResult());

                                ResultContact resultContacts = new ResultContact();
                                resultContacts.setContacts(contacts);

                                ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
                                chatResponse.setUniqueId(uniqueId);
                                chatResponse.setResult(resultContacts);

                                String content = gson.toJson(chatResponse);

                                listenerManager.callOnSearchContact(content, chatResponse);
                                listenerManager.callOnLogEvent(content);
                                if (log) Log.i(TAG, "RECEIVE_SEARCH_CONTACT");
                            }

                        } else {

                            if (contactResponse.body() != null) {
                                String errorMessage = contactResponse.body().getMessage() != null ? contactResponse.body().getMessage() : "";
                                int errorCode = contactResponse.body().getErrorCode() != null ? contactResponse.body().getErrorCode() : 0;
                                String error = getErrorOutPut(errorMessage, errorCode, uniqueId);
                            }
                        }

                    }, (Throwable throwable) -> Log.e(TAG, throwable.getMessage()));
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Add one contact to the contact list
     *
     * @param firstName       Notice: if just put fistName without lastName its ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public String addContact(String firstName, String lastName, String cellphoneNumber, String email) {

        typeCode = getTypeCode();

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }
        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }
        if (Util.isNullOrEmpty(email)) {
            email = "";
        }
        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        String uniqueId = generateUniqueId();
        Observable<Response<Contacts>> addContactObservable;
        if (chatReady) {
            if (Util.isNullOrEmpty(getTypeCode())) {
                addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);

            } else {
                addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);

            }
            addContactObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addContactResponse -> {
                        if (addContactResponse.isSuccessful()) {
                            Contacts contacts = addContactResponse.body();
                            if (!contacts.getHasError()) {

                                ChatResponse<ResultAddContact> chatResponse = Util.getReformatOutPutAddContact(contacts, uniqueId);

                                String contactsJson = gson.toJson(chatResponse);

                                listenerManager.callOnAddContact(contactsJson, chatResponse);

                                showLog("RECEIVED_ADD_CONTACT", contactsJson);

                                if (cache) {

                                    messageDatabaseHelper.saveContact(chatResponse.getResult().getContact(), getExpireAmount());
                                }
                            } else {
                                String jsonError = getErrorOutPut(contacts.getMessage(), contacts.getErrorCode(), uniqueId);
                            }
                        }
                    }, (Throwable throwable) ->
                    {
                        Log.e("Error on add contact", throwable.getMessage());
                        Log.e(TAG, throwable.getMessage());
                    });
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Add one contact to the contact list
     * <p>
     * firstName       Notice: if just put fistName without lastName its ok.
     * lastName        last name of the contact
     * cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * email           email of the contact
     */
    public String addContact(RequestAddContact request) {

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();

        typeCode = getTypeCode();

        return addContact(firstName, lastName, cellphoneNumber, email);
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public String removeContact(long userId) {
        String uniqueId = generateUniqueId();
        Observable<Response<ContactRemove>> removeContactObservable;
        if (chatReady) {
            if (Util.isNullOrEmpty(getTypeCode())) {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
            } else {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId, getTypeCode());
            }

            removeContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                if (response.body() != null && response.isSuccessful()) {
                    ContactRemove contactRemove = response.body();
                    if (!contactRemove.getHasError()) {
                        ChatResponse<ResultRemoveContact> chatResponse = new ChatResponse<>();
                        chatResponse.setUniqueId(uniqueId);
                        ResultRemoveContact resultRemoveContact = new ResultRemoveContact();
                        resultRemoveContact.setResult(contactRemove.isResult());

                        chatResponse.setResult(resultRemoveContact);

                        String json = gson.toJson(chatResponse);
                        if (cache) {
                            messageDatabaseHelper.deleteContactById(userId);
                        }
                        listenerManager.callOnRemoveContact(json, chatResponse);
                        showLog("RECEIVED_REMOVE_CONTACT", json);
                    } else {
                        String jsonError = getErrorOutPut(contactRemove.getErrorMessage(), contactRemove.getErrorCode(), uniqueId);
                    }
                }
            }, (Throwable throwable) -> {
                String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
            });
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Remove contact with the user id
     * <p>
     * userId id of the user that we want to remove from contact list
     */
    public String removeContact(RequestRemoveContact request) {
        long userId = request.getUserId();
        return removeContact(userId);
    }

    /**
     * Update contacts
     * all of the params all required to update
     */
    public String updateContact(long userId, String firstName, String lastName, String cellphoneNumber, String email) {

        String uniqueId = generateUniqueId();

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }

        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }

        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        if (Util.isNullOrEmpty(email)) {
            email = "";
        }

        if (chatReady) {
            Observable<Response<UpdateContact>> updateContactObservable;
            if (Util.isNullOrEmpty(getTypeCode())) {
                updateContactObservable = contactApi.updateContact(
                        getToken(),
                        1,
                        userId,
                        firstName,
                        lastName,
                        email,
                        uniqueId,
                        cellphoneNumber);
            } else {
                updateContactObservable = contactApi.updateContact(
                        getToken(),
                        1,
                        userId,
                        firstName,
                        lastName,
                        email,
                        uniqueId,
                        cellphoneNumber,
                        getTypeCode());
            }
            updateContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                UpdateContact updateContact = response.body();
                if (updateContact != null && response.isSuccessful()) {
                    if (!response.body().getHasError()) {

                        ChatResponse<ResultUpdateContact> chatResponse = new ChatResponse<>();
                        chatResponse.setUniqueId(uniqueId);
                        ResultUpdateContact resultUpdateContact = new ResultUpdateContact();

                        if (!Util.isNullOrEmpty(updateContact.getCount())) {
                            resultUpdateContact.setContentCount(updateContact.getCount());
                        }
                        resultUpdateContact.setContacts(updateContact.getResult());
                        chatResponse.setResult(resultUpdateContact);

                        String json = gson.toJson(chatResponse);
                        listenerManager.callOnUpdateContact(json, chatResponse);

                        if (cache) {
                            messageDatabaseHelper.saveContact(updateContact.getResult().get(0), getExpireAmount());
                        }

                        showLog("RECEIVE_UPDATE_CONTACT", json);
                    } else {
                        String errorMsg = response.body().getMessage();
                        int errorCodeMsg = response.body().getErrorCode();

                        errorMsg = errorMsg != null ? errorMsg : "";

                        String jsonError = getErrorOutPut(errorMsg, errorCodeMsg, uniqueId);
                    }
                }
            }, (Throwable throwable) ->
            {
                if (throwable != null) {
//                    String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                }
            });
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * Update contacts
     * all of the params all required
     */
    public String updateContact(RequestUpdateContact request) {
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();
        long userId = request.getUserId();

        return updateContact(userId, firstName, lastName, cellphoneNumber, email);
    }

    public String mapSearch(String searchTerm, Double latitude, Double longitude) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");

            MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
            Observable<Response<MapNeshan>> observable = mapApi.mapSearch("8b77db18704aa646ee5aaea13e7370f4f88b9e8c"
                    , searchTerm, latitude, longitude);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<MapNeshan>>() {
                @Override
                public void call(Response<MapNeshan> mapNeshanResponse) {
                    OutPutMapNeshan outPutMapNeshan = new OutPutMapNeshan();
                    if (mapNeshanResponse.isSuccessful()) {
                        MapNeshan mapNeshan = mapNeshanResponse.body();

                        outPutMapNeshan = new OutPutMapNeshan();
                        outPutMapNeshan.setCount(mapNeshan.getCount());
                        ResultMap resultMap = new ResultMap();
                        resultMap.setMaps(mapNeshan.getItems());
                        outPutMapNeshan.setResult(resultMap);
                        String json = gson.toJson(outPutMapNeshan);
                        listenerManager.callOnMapSearch(json, outPutMapNeshan);
                        showLog("RECEIVE_MAP_SEARCH", json);
                    } else {
                        ErrorOutPut errorOutPut = new ErrorOutPut();
                        errorOutPut.setErrorCode(mapNeshanResponse.code());
                        errorOutPut.setErrorMessage(mapNeshanResponse.message());
                        errorOutPut.setHasError(true);
                        String json = gson.toJson(outPutMapNeshan);
                        listenerManager.callOnError(json, errorOutPut);
                    }
                }
            }, (Throwable throwable) -> {
                ErrorOutPut errorOutPut = new ErrorOutPut();
                errorOutPut.setErrorMessage(throwable.getMessage());
                errorOutPut.setHasError(true);
                String json = gson.toJson(errorOutPut);

                listenerManager.callOnError(json, errorOutPut);
            });
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * String searchTerm ;
     * double latitude  ;
     * double longitude ;
     */

    public String mapSearch(RequestMapSearch request) {
        String searchTerm = request.getSearchTerm();
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();
        return mapSearch(searchTerm, latitude, longitude);
    }

    public String mapRouting(String origin, String destination) {
        String uniqueId = generateUniqueId();

        if (chatReady) {
            RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
            MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
            Observable<Response<MapRout>> responseObservable = mapApi.mapRouting("8b77db18704aa646ee5aaea13e7370f4f88b9e8c"
                    , origin, destination, true);
            responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mapRoutResponse -> {
                if (mapRoutResponse.isSuccessful()) {
                    MapRout mapRout = mapRoutResponse.body();
                    OutPutMapRout outPutMapRout = new OutPutMapRout();
                    outPutMapRout.setResult(mapRout);
                    String jsonMapRout = gson.toJson(outPutMapRout);
                    listenerManager.callOnMapRouting(jsonMapRout);
                    showLog("RECEIVE_MAP_ROUTING", jsonMapRout);
                }
            }, (Throwable throwable) -> {
                Log.e(TAG, throwable.getMessage());
            });
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    public String mapRouting(RequestMapRouting request) {
        String origin = request.getOrigin();
        String destination = request.getDestination();
        String uniqueId;
        RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
        MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
        uniqueId = generateUniqueId();
        Observable<Response<MapRout>> responseObservable = mapApi.mapRouting("8b77db18704aa646ee5aaea13e7370f4f88b9e8c"
                , origin, destination, true);
        String finalUniqueId = uniqueId;
        responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<MapRout>>() {
            @Override
            public void call(Response<MapRout> mapRoutResponse) {
                if (mapRoutResponse.isSuccessful()) {
                    MapRout mapRout = mapRoutResponse.body();
                    OutPutMapRout outPutMapRout = new OutPutMapRout();
                    outPutMapRout.setResult(mapRout);
                    String jsonMapRout = gson.toJson(outPutMapRout);
                    listenerManager.callOnMapRouting(jsonMapRout);
                    showLog("RECEIVE_MAP_ROUTING", jsonMapRout);
                } else {
                    String jsonError = getErrorOutPut(mapRoutResponse.message(), mapRoutResponse.code(), finalUniqueId);
                }
            }
        }, (Throwable throwable) -> {
            String jsonError = getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
        });
        return uniqueId;
    }

    /**
     * [Required] center
     * if these params don't set they have default values :
     * [default value] type = "standard-night"
     * [default value] zoom = 15
     * [default value] width = 800
     * [default value] height = 500
     */
    private String mainMapStaticImage(RequestLocationMessage request,
                                      Activity activity,
                                      String uniqueId,
                                      boolean isMessage) {

        try {
            if (Util.isNullOrEmpty(uniqueId)) {
                uniqueId = generateUniqueId();
            }
            if (chatReady) {
                String type = request.getType();
                int zoom = request.getZoom();
                int width = request.getWidth();
                int height = request.getHeight();
                String center = request.getCenter();
                long threadId = request.getThreadId();
                int messageType = request.getMessageType();
                String systemMetadata = request.getSystemMetadata();

                if (Util.isNullOrEmpty(type)) {
                    type = "standard-night";
                }
                if (Util.isNullOrEmpty(zoom)) {
                    zoom = 15;
                }
                if (Util.isNullOrEmpty(width)) {
                    width = 800;
                }
                if (Util.isNullOrEmpty(height)) {
                    height = 500;
                }

                RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
                MapApi mapApi = retrofitHelperMap.getService(MapApi.class);

                Call<ResponseBody> call = mapApi.mapStaticCall(API_KEY_MAP,
                        type,
                        zoom,
                        center,
                        width,
                        height);
                String finalUniqueId = uniqueId;

                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            if (response.body() != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                ChatResponse<ResultStaticMapImage> chatResponse = new ChatResponse<>();
                                ResultStaticMapImage result = new ResultStaticMapImage();
                                result.setBitmap(bitmap);
                                chatResponse.setUniqueId(finalUniqueId);
                                chatResponse.setResult(result);
                                listenerManager.callOnStaticMap(chatResponse);
                                showLog("RECEIVE_MAP_STATIC", "");
                                if (!call.isCanceled()) {
                                    call.cancel();
                                }

                                if (isMessage) {
                                    File file = FileUtils.saveBitmap(bitmap, "map");
                                    Uri fileUri = Uri.fromFile(file);
                                    String newPath = FilePick.getSmartFilePath(getContext(), fileUri);

                                    String mimType = handleMimType(fileUri, file);

                                    LFileUpload lFileUpload = new LFileUpload();

                                    lFileUpload.setFileUri(fileUri);
                                    if (activity != null) {
                                        lFileUpload.setActivity(activity);
                                    }
                                    lFileUpload.setThreadId(threadId);
                                    lFileUpload.setUniqueId(finalUniqueId);
                                    lFileUpload.setMessageType(messageType);
                                    lFileUpload.setMimeType(mimType);
                                    lFileUpload.setMethodName(ChatConstant.METHOD_LOCATION_MSG);
                                    lFileUpload.setSystemMetaData(systemMetadata);
                                    lFileUpload.setCenter(center);

                                    uploadImageFileMessage(lFileUpload);

                                }
                            }
                        } else {
                            try {
                                String errorBody;
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                    JSONObject jObjError = new JSONObject(errorBody);
                                    String errorMessage = jObjError.getString("message");
                                    int errorCode = jObjError.getInt("code");
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                }

                            } catch (JSONException e) {
                                if (log) Log.e(TAG, e.getCause().getMessage());
                            } catch (IOException e) {
                                if (log) Log.e(TAG, e.getCause().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        String jsonError = getErrorOutPut(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                    }
                });
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
        return uniqueId;
    }



    private String mainMapStaticImage(RequestLocationMessage request,
                                      Activity activity,
                                      String uniqueId,
                                      boolean isMessage,
                                      ProgressHandler.sendFileMessage handler) {

        try {
            if (Util.isNullOrEmpty(uniqueId)) {
                uniqueId = generateUniqueId();
            }
            if (chatReady) {
                String type = request.getType();
                int zoom = request.getZoom();
                int width = request.getWidth();
                int height = request.getHeight();
                String center = request.getCenter();
                long threadId = request.getThreadId();
                int messageType = request.getMessageType();
                String systemMetadata = request.getSystemMetadata();

                if (Util.isNullOrEmpty(type)) {
                    type = "standard-night";
                }
                if (Util.isNullOrEmpty(zoom)) {
                    zoom = 15;
                }
                if (Util.isNullOrEmpty(width)) {
                    width = 800;
                }
                if (Util.isNullOrEmpty(height)) {
                    height = 500;
                }

                RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
                MapApi mapApi = retrofitHelperMap.getService(MapApi.class);

                Call<ResponseBody> call = mapApi.mapStaticCall(API_KEY_MAP,
                        type,
                        zoom,
                        center,
                        width,
                        height);
                String finalUniqueId = uniqueId;


                showLog("SEND_LOCATION_MESSAGE", "");

                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            if (response.body() != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                                ChatResponse<ResultStaticMapImage> chatResponse = new ChatResponse<>();
                                ResultStaticMapImage result = new ResultStaticMapImage();
                                result.setBitmap(bitmap);
                                chatResponse.setUniqueId(finalUniqueId);
                                chatResponse.setResult(result);
                                listenerManager.callOnStaticMap(chatResponse);
                                showLog("RECEIVE_MAP_STATIC", "");
                                if (!call.isCanceled()) {
                                    call.cancel();
                                }

                                if (isMessage) {
                                    File file = FileUtils.saveBitmap(bitmap, "map");
                                    Uri fileUri = Uri.fromFile(file);
                                    String newPath = FilePick.getSmartFilePath(getContext(), fileUri);

                                    String mimType = handleMimType(fileUri, file);

                                    LFileUpload lFileUpload = new LFileUpload();

                                    lFileUpload.setFileUri(fileUri);
                                    if (activity != null) {
                                        lFileUpload.setActivity(activity);
                                    }
                                    lFileUpload.setThreadId(threadId);
                                    lFileUpload.setUniqueId(finalUniqueId);
                                    lFileUpload.setMessageType(messageType);
                                    lFileUpload.setMimeType(mimType);
                                    lFileUpload.setMethodName(ChatConstant.METHOD_LOCATION_MSG);
                                    lFileUpload.setSystemMetaData(systemMetadata);
                                    lFileUpload.setCenter(center);
                                    lFileUpload.setHandler(handler);

                                    uploadImageFileMessage(lFileUpload);

                                }
                            }
                        } else {
                            try {
                                String errorBody;
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                    JSONObject jObjError = new JSONObject(errorBody);
                                    String errorMessage = jObjError.getString("message");
                                    int errorCode = jObjError.getInt("code");
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                }

                            } catch (JSONException e) {
                                if (log) Log.e(TAG, e.getCause().getMessage());
                            } catch (IOException e) {
                                if (log) Log.e(TAG, e.getCause().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        String jsonError = getErrorOutPut(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                    }
                });
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
        return uniqueId;
    }



    public String mapStaticImage(RequestMapStaticImage request) {

        int zoom = request.getZoom();
        int width = request.getWidth();
        int height = request.getHeight();
        String center = request.getCenter();

        RequestLocationMessage requestLMsg = new RequestLocationMessage.Builder()
                .height(height)
                .width(width)
                .center(center)
                .zoom(zoom)
                .build();


        return mainMapStaticImage(requestLMsg, null, null, false);
    }

    public String sendLocationMessage(RequestLocationMessage request) {

        String uniqueId = generateUniqueId();
        Activity activity = request.getActivity();
        mainMapStaticImage(request, activity, uniqueId, true);

        return uniqueId;
    }

    public String sendLocationMessage(RequestLocationMessage request, ProgressHandler.sendFileMessage handler) {


        String uniqueId = generateUniqueId();

        Activity activity = request.getActivity();

        mainMapStaticImage(request, activity, uniqueId, true,handler);

        return uniqueId;

    }

    public String mapReverse(RequestMapReverse request) {

        String uniqueId = null;
        if (chatReady) {

            double lat = request.getLat();
            double lng = request.getLng();
            uniqueId = generateUniqueId();
            RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
            MapApi mapApi = retrofitHelperMap.getService(MapApi.class);
            Observable<Response<MapReverse>> observable = mapApi.mapReverse(API_KEY_MAP, lat, lng);
            String finalUniqueId = uniqueId;
            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Response<MapReverse>>() {
                        @Override
                        public void call(Response<MapReverse> mapReverseResponse) {
                            MapReverse mapReverse = mapReverseResponse.body();
                            if (mapReverseResponse.isSuccessful()) {
                                ChatResponse<ResultMapReverse> chatResponse = new ChatResponse<>();

                                ResultMapReverse resultMap = new ResultMapReverse();
                                String address = mapReverse.getAddress();
                                if (!Util.isNullOrEmpty(address)) {
                                    resultMap.setAddress(address);
                                }
                                resultMap.setCity(mapReverse.getCity());
                                resultMap.setIn_odd_even_zone(mapReverse.isIn_odd_even_zone());
                                resultMap.setIn_traffic_zone(mapReverse.isIn_traffic_zone());
                                resultMap.setMunicipality_zone(mapReverse.getMunicipality_zone());
                                resultMap.setNeighbourhood(mapReverse.getNeighbourhood());
                                resultMap.setState(mapReverse.getState());

                                chatResponse.setUniqueId(finalUniqueId);
                                chatResponse.setResult(resultMap);
                                String json = gson.toJson(chatResponse);
                                listenerManager.callOnMapReverse(json, chatResponse);
                                showLog("RECEIVE_MAP_REVERSE", json);
                            } else {
                                try {
                                    String errorBody;
                                    if (mapReverseResponse.errorBody() != null) {
                                        errorBody = mapReverseResponse.errorBody().string();
                                        JSONObject jObjError = new JSONObject(errorBody);
                                        String errorMessage = jObjError.getString("message");
                                        int errorCode = jObjError.getInt("code");
                                        String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                    }

                                } catch (JSONException e) {
                                    if (log) Log.e(TAG, e.getCause().getMessage());
                                } catch (IOException e) {
                                    if (log) Log.e(TAG, e.getCause().getMessage());
                                }
                            }
                        }
                    }, (Throwable throwable) -> {
                        String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                    });
        }
        return uniqueId;
    }


    /**
     * It blocks the thread
     *
     * @param contactId id of the contact
     * @param threadId  id of the thread
     * @param userId    id of the user
     */
    public String block(Long contactId, Long userId, Long threadId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String json = contentObject.toString();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer("1");
            chatMessage.setType(Constants.BLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.BLOCK, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_BLOCK");
            if (handler != null) {
                handler.onBlock(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It blocks the thread
     *
     * @ param contactId id of the contact
     * @ param threadId  id of the thread
     * @ param userId    id of the user
     */
    public String block(RequestBlock request, ChatHandler handler) {
        Long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        return block(contactId, userId, threadId, handler);
    }

    /**
     * It unblocks thread with three way
     *
     * @param blockId   the id that came from getblockList
     * @param userId
     * @param threadId  Id of the thread
     * @param contactId Id of the contact
     */
    public String unblock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String jsonContent = contentObject.toString();


            chatMessage.setContent(jsonContent);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer("1");
            chatMessage.setType(Constants.UNBLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(blockId)) {
                jsonObject.remove("subjectId");
            } else {
                jsonObject.remove("subjectId");
                jsonObject.addProperty("subjectId", blockId);
            }


            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.UNBLOCK, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_UN_BLOCK");
            if (handler != null) {
                handler.onUnBlock(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It unblocks thread with three way
     *
     * @ param blockId it can be found in the response of getBlockList
     * @ param userId Id of the user
     * @ param threadId Id of the thread
     * @ param contactId Id of the contact
     */
    public String unblock(RequestUnBlock request, ChatHandler handler) {
        long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        long blockId = request.getBlockId();

        return unblock(blockId, userId, threadId, contactId, handler);
    }

    /**
     * If someone that is not in your contacts send message to you
     * their spam is false
     */
    @Deprecated
    public String spam(long threadId) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(Constants.SPAM_PV_THREAD);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.SPAM_PV_THREAD, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_REPORT_SPAM");
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * If someone that is not in your contact list tries to send message to you
     * their spam value is true and you can call this method in order to set that to false
     *
     * @ param long threadId Id of the thread
     */
    public String spam(RequestSpam request) {

        String uniqueId;
        JsonObject jsonObject;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                long threadId = request.getThreadId();
                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.SPAM_PV_THREAD);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(threadId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", request.getTypeCode());
                }
                setCallBacks(null, null, null, true, Constants.SPAM_PV_THREAD, null, uniqueId);

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_REPORT_SPAM");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }


    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(Long count, Long offset, ChatHandler handler) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            ChatMessageContent chatMessageContent = new ChatMessageContent();
            if (offset != null) {
                chatMessageContent.setOffset(offset);
            }
            if (count != null) {
                chatMessageContent.setCount(count);
            } else {
                chatMessageContent.setCount(50);
            }

            String json = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setType(Constants.GET_BLOCKED);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.GET_BLOCKED, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_BLOCK_List");
            if (handler != null) {
                handler.onGetBlockList(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }


    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(RequestBlockList request, ChatHandler handler) {
        return getBlockList(request.getCount(), request.getOffset(), handler);
    }

    /**
     * Create the thread to p to p/channel/group. The list below is showing all of the threads type
     * int NORMAL = 0;
     * int OWNER_GROUP = 1;
     * int PUBLIC_GROUP = 2;
     * int CHANNEL_GROUP = 4;
     * int TO_BE_USER_ID = 5;
     * <p>
     * int CHANNEL = 8;
     */

    /*available invitee types*/
    /*int TO_BE_USER_SSO_ID = 1;
     *int TO_BE_USER_CONTACT_ID = 2;
     *int TO_BE_USER_CELLPHONE_NUMBER = 3;
     *int TO_BE_USER_USERNAME = 4;
     *TO_BE_USER_ID = 5  // just for p2p
     */
    public String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String metadata, ChatHandler handler) {

        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            List<Invitee> invitees = new ArrayList<>(Arrays.asList(invitee));
            ChatThread chatThread = new ChatThread();
            chatThread.setType(threadType);
            chatThread.setInvitees(invitees);
            chatThread.setTitle(threadTitle);

            JsonObject chatThreadObject = (JsonObject) gson.toJsonTree(chatThread);

            if (Util.isNullOrEmpty(description)) {
                chatThreadObject.remove("description");
            } else {
                chatThreadObject.remove("description");
                chatThreadObject.addProperty("description", description);
            }

            if (Util.isNullOrEmpty(image)) {
                chatThreadObject.remove("image");
            } else {
                chatThreadObject.remove("image");
                chatThreadObject.addProperty("image", image);
            }

            if (Util.isNullOrEmpty(metadata)) {
                chatThreadObject.remove("metadata");

            } else {
                chatThreadObject.remove("metadata");
                chatThreadObject.addProperty("metadata", metadata);
            }
            String contentThreadChat = chatThreadObject.toString();

            ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId, getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.INVITATION, null, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_CREATE_THREAD");
            if (handler != null) {
                handler.onCreateThread(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }


    /**
     * Create the thread with message is just for  p to p.( Thread Type is int NORMAL = 0)
     *
     * @return The first UniqueId is for create thread and the rest of them are for new message or forward messages
     * Its have three kind of Unique Ids. One of them is for message. One of them for Create Thread
     * and the others for Forward Message or Messages.
     * <p>
     * int type  Type of the Thread (You can have Thread Type from ThreadType.class)
     * String ownerSsoId  [Optional]
     * List<Invitee> invitees  you can add your invite list here
     * String title  [Optional] title of the group thread
     * <p>
     * RequestThreadInnerMessage message{  object of the inner message
     * <p>
     * -------------  String text  text of the message
     * -------------  int type  type of the message  [Optional]
     * -------------  String metadata  [Optional]
     * -------------  String systemMetadata  [Optional]
     * -------------  List<Long> forwardedMessageIds  [Optional]
     * }
     */
    public ArrayList<String> createThreadWithMessage(RequestCreateThread threadRequest) {

        List<String> forwardUniqueIds;
        JsonObject innerMessageObj;
        JsonObject jsonObject;
        String threadUniqueId = generateUniqueId();
        ArrayList<String> uniqueIds = new ArrayList<>();
        uniqueIds.add(threadUniqueId);
        try {
            if (chatReady) {

                RequestThreadInnerMessage innerMessage = threadRequest.getMessage();
                innerMessageObj = (JsonObject) gson.toJsonTree(innerMessage);

                if (Util.isNullOrEmpty(threadRequest.getMessage().getType())) {
                    innerMessageObj.remove("type");
                }

                if (Util.isNullOrEmpty(threadRequest.getMessage().getText())) {
                    innerMessageObj.remove("message");
                } else {
                    String newMsgUniqueId = generateUniqueId();
                    innerMessageObj.addProperty("uniqueId", newMsgUniqueId);
                    uniqueIds.add(newMsgUniqueId);
                    setCallBacks(true, true, true, true, Constants.MESSAGE, null, newMsgUniqueId);
                }

                if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                    /** Its generated new unique id for each forward message*/
                    List<Long> messageIds = threadRequest.getMessage().getForwardedMessageIds();
                    forwardUniqueIds = new ArrayList<>();
                    for (long ids : messageIds) {
                        String frwMsgUniqueId = generateUniqueId();
                        forwardUniqueIds.add(frwMsgUniqueId);
                        uniqueIds.add(frwMsgUniqueId);
                        setCallBacks(true, true, true, true, Constants.MESSAGE, null, frwMsgUniqueId);
                    }
                    JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                    }.getType());

                    JsonArray jsonArray = element.getAsJsonArray();
                    innerMessageObj.add("forwardedUniqueIds", jsonArray);
                } else {
                    innerMessageObj.remove("forwardedUniqueIds");
                    innerMessageObj.remove("forwardedMessageIds");
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(threadRequest);

                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(Constants.INVITATION);
                chatMessage.setUniqueId(threadUniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = threadRequest.getTypeCode();

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", typeCode);
                }

                setCallBacks(null, null, null, true, Constants.INVITATION, null, threadUniqueId);

                sendAsyncMessage(jsonObject.toString(), 4, "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {


                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);

            }

        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueIds;
    }


    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */
    public String updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                JsonObject jObj = new JsonObject();

                jObj.addProperty("name", threadInfoVO.getTitle());
                jObj.addProperty("description", threadInfoVO.getDescription());
                jObj.addProperty("metadata", threadInfoVO.getMetadata());
                jObj.addProperty("image", threadInfoVO.getImage());

                String content = jObj.toString();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(content);

                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(Constants.UPDATE_THREAD_INFO);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                }

                setCallBacks(null, null, null, true, Constants.UPDATE_THREAD_INFO, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UPDATE_THREAD_INFO");
                if (handler != null) {
                    handler.onUpdateThreadInfo(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }

        return uniqueId;
    }


    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */

    public String updateThreadInfo(RequestThreadInfo request, ChatHandler handler) {
        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().title(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .metadat(request.getMetadata())
                .build();
        return updateThreadInfo(request.getThreadId(), threadInfoVO, handler);
    }

    /**
     * Get the participant list of specific thread
     * <p>
     *
     * @ param long threadId id of the thread we want to get the participant list
     * @ param long count number of the participant wanted to get
     * @ param long offset offset of the participant list
     */
    public String getThreadParticipants(RequestThreadParticipant request, @Nullable Boolean admin, ChatHandler handler) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();
        boolean getAdmin = admin != null ? admin : false;

        return getThreadParticipantsMain((int) count, offset, threadId, getAdmin, typeCode, handler);
    }

    public String getThreadParticipants(RequestThreadParticipant request, ChatHandler handler) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        return getThreadParticipantsMain((int) count, offset, threadId, typeCode, handler);
    }

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    @Deprecated
    public String getThreadParticipants(Integer count, Long offset, long threadId, ChatHandler handler) {
        return getThreadParticipantsMain(count, offset, threadId, null, handler);
    }


    private String getThreadParticipantsMain(Integer count, Long offset, long threadId, String typeCode, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();

        offset = offset != null ? offset : 0;
        count = count != null ? count : 50;


        if (cache) {
            List<Participant> participants = messageDatabaseHelper.getThreadParticipant(offset, count, threadId);
            if (participants != null) {
                long participantCount = messageDatabaseHelper.getParticipantCount(threadId);
                ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
                ResultParticipant resultParticipant = new ResultParticipant();

                resultParticipant.setContentCount(participants.size());
                if (participants.size() + offset < participantCount) {
                    resultParticipant.setHasNext(true);
                } else {
                    resultParticipant.setHasNext(false);
                }
                resultParticipant.setParticipants(participants);
                chatResponse.setResult(resultParticipant);
                chatResponse.setCache(true);

                resultParticipant.setNextOffset(offset + participants.size());
                String jsonParticipant = gson.toJson(chatResponse);
                listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);
                showLog("Cache Thread Participant", jsonParticipant);

            }
        }

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(count);

            chatMessageContent.setOffset(offset);

            String content = gson.toJson(chatMessageContent);


            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(Constants.THREAD_PARTICIPANTS);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            jsonObject.remove("lastMessageId");
            jsonObject.remove("firstMessageId");
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, offset, uniqueId);

            sendAsyncMessage(asyncContent, 3, "SEND_THREAD_PARTICIPANT");

            if (handler != null) {
                handler.onGetThreadParticipant(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;

    }


    private String getThreadParticipantsMain(Integer count, Long offset, long threadId, boolean getAdmin, String typeCode, ChatHandler handler) {

        String uniqueId;
        uniqueId = generateUniqueId();

        offset = offset != null ? offset : 0;
        count = count != null ? count : 50;


        if (cache) {
            List<Participant> participants = messageDatabaseHelper.getThreadParticipant(offset, count, threadId);
            if (participants != null) {
                long participantCount = messageDatabaseHelper.getParticipantCount(threadId);
                ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
                ResultParticipant resultParticipant = new ResultParticipant();

                resultParticipant.setContentCount(participants.size());
                if (participants.size() + offset < participantCount) {
                    resultParticipant.setHasNext(true);
                } else {
                    resultParticipant.setHasNext(false);
                }
                resultParticipant.setParticipants(participants);
                chatResponse.setResult(resultParticipant);
                chatResponse.setCache(true);

                resultParticipant.setNextOffset(offset + participants.size());
                String jsonParticipant = gson.toJson(chatResponse);
                listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);
                showLog("Cache Thread Participant", jsonParticipant);

            }
        }

        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);

            JsonObject object = (JsonObject) gson.toJsonTree(chatMessageContent);

            object.addProperty("admin", getAdmin);


            String content = object.toString();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(Constants.THREAD_PARTICIPANTS);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            jsonObject.remove("lastMessageId");
            jsonObject.remove("firstMessageId");
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, offset, uniqueId);
            sendAsyncMessage(asyncContent, 3, "SEND_THREAD_PARTICIPANT");
            if (handler != null) {
                handler.onGetThreadParticipant(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;

    }

    /**
     * In order to send seen message you have to call this method
     */
    public String seenMessage(long messageId, long ownerId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            if (ownerId != getUserId()) {
                ChatMessage message = new ChatMessage();
                message.setType(Constants.SEEN);
                message.setContent(String.valueOf(messageId));
                message.setTokenIssuer("1");
                message.setToken(getToken());
                message.setUniqueId(uniqueId);
                message.setTime(1000);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(message);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, 4, "SEND_SEEN_MESSAGE");
                if (handler != null) {
                    handler.onSeen(uniqueId);
                }
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * In order to send seen message you have to call {@link #seenMessage(long, long, ChatHandler)}
     */
    public String seenMessage(RequestSeenMessage request, ChatHandler handler) {
        long messageId = request.getMessageId();
        long ownerId = request.getOwnerId();

        return seenMessage(messageId, ownerId, handler);
    }

    /**
     * It Gets the information of the current user
     */
    public String getUserInfo(ChatHandler handler) {

        String uniqueId = generateUniqueId();

        try {
            if (permit) {
                UserInfo userInfo = messageDatabaseHelper.getUserInfo();

                if (userInfo != null) {

                    ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();

                    ResultUserInfo result = new ResultUserInfo();

                    setUserId(userInfo.getId());

                    result.setUser(userInfo);

                    chatResponse.setResult(result);

                    chatResponse.setCache(true);

                    String userInfoJson = gson.toJson(chatResponse);

                    listenerManager.callOnUserInfo(userInfoJson, chatResponse);
                    showLog("CACHE_USER_INFO", userInfoJson);
                }
            }

            if (asyncReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.USER_INFO);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");

                setCallBacks(null, null, null, true, Constants.USER_INFO, null, uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());

                }

                String asyncContent = jsonObject.toString();
                showLog("SEND_USER_INFO", asyncContent);
                async.sendMessage(asyncContent, 3);

                if (handler != null) {
                    handler.onGetUserInfo(uniqueId);
                }
            }

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It Mutes the thread so notification is set to off for that thread
     */
    public String muteThread(long threadId, ChatHandler handler) {
        String uniqueId;
        JsonObject jsonObject;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
//                long threadId = request.getThreadId();
//                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setSubjectId(threadId);


                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                }

                setCallBacks(null, null, null, true, Constants.MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_MUTE_THREAD");

                if (handler != null) {
                    handler.onMuteThread(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * Mute the thread so notification is off for that thread
     */
    public String muteThread(RequestMuteThread request, ChatHandler handler) {
        return muteThread(request.getThreadId(), handler);
    }

    /**
     * It Un mutes the thread so notification is on for that thread
     */
    public String unMuteThread(RequestMuteThread request, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        JsonObject jsonObject = null;
        try {
            if (chatReady) {
                long threadId = request.getThreadId();
                String typeCode = request.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", request.getTypeCode());
                }

                setCallBacks(null, null, null, true, Constants.UN_MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Un mute the thread so notification is on for that thread
     */
    public String unMuteThread(long threadId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.UN_MUTE_THREAD);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.UN_MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Message can be edit when you pass the message id and the edited
     * content in order to edit your Message.
     */
    public String editMessage(int messageId, String messageContent, String systemMetaData, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        try {
            if (chatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.EDIT_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(systemMetaData);
                chatMessage.setTokenIssuer("1");

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                jsonObject.remove("metadata");

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_EDIT_MESSAGE");
                if (handler != null) {
                    handler.onEditMessage(uniqueId);
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }


    /**
     * Message can be edit when you pass the message id and the edited
     * content in order to edit your Message.
     */
    public String editMessage(RequestEditMessage request, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        try {
            JsonObject jsonObject = null;
            if (chatReady) {

                String messageContent = request.getMessageContent();
                long messageId = request.getMessageId();
                String metaData = request.getMetaData();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.EDIT_MESSAGE);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(metaData);
                chatMessage.setTokenIssuer("1");

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", request.getTypeCode());
                }

                setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), 4, "SEND_EDIT_MESSAGE");
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
            if (handler != null) {
                handler.onEditMessage(uniqueId);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    public String getMessageDeliveredList(RequestDeliveredMessageList requestParams) {
        return deliveredMessageList(requestParams);
    }

    public String getMessageSeenList(RequestSeenMessageList requestParams) {
        return seenMessageList(requestParams);
    }

    /**
     * Add a listener to receive events on this Chat.
     *
     * @param listener A listener to add.
     * @return {@code this} object.
     */
    public Chat addListener(ChatListener listener) {
        listenerManager.addListener(listener, log);
        return this;
    }

    public Chat addListeners(List<ChatListener> listeners) {
        listenerManager.addListeners(listeners);
        return this;
    }

    public Chat removeListener(ChatListener listener) {
        listenerManager.removeListener(listener);
        return this;
    }

    /*
     * If you want to disable cache Set isCacheables to false
     * */
    public boolean isCacheables(boolean cache) {
        Chat.cache = cache;
        return cache;
    }


    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }


    /**
     * @param expireSecond participants and contacts have an expire date in cache and after expireSecond
     *                     they are going to delete from the cache/
     */
    public void setExpireAmount(int expireSecond) {
        this.expireAmount = expireSecond;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String clearHistory(RequestClearHistory requestClearHistory) {

        String uniqueId = generateUniqueId();

        long threadId = requestClearHistory.getThreadId();

        if (chatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(Constants.CLEAR_HISTORY);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setSubjectId(threadId);
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("contentCount");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.CLEAR_HISTORY, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_CLEAR_HISTORY");
        }
        return uniqueId;
    }


    public String getNotSeenDuration(RequestGetLastSeens requestGetLastSeens) {

        String uniqueId = generateUniqueId();

        ArrayList<Integer> userIdsList = requestGetLastSeens.getUserIds();

        JsonObject jObj = new JsonObject();

        JsonElement jsonElement = gson.toJsonTree(userIdsList);

        jObj.add("userIds", jsonElement);


        if (chatReady) {

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(Constants.GET_NOT_SEEN_DURATION);
            chatMessage.setContent(jObj.toString());
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("contentCount");
            jsonObject.remove("time");
            jsonObject.remove("subjectId");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }


            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.GET_NOT_SEEN_DURATION, null, uniqueId);

            sendAsyncMessage(asyncContent, 4, "SEND_GET_NOT_SEEN_DURATION");
        }

        return uniqueId;


    }

    public String getAdminList(RequestGetAdmin requestGetAdmin) {


        return getThreadParticipants(requestGetAdmin, requestGetAdmin.isAdmin(), null);
//        String uniqueId = generateUniqueId();
//        long threadId = requestGetAdmin.getThreadId();
//        if (chatReady) {
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(Constants.GET_THREAD_ADMINS);
//            chatMessage.setToken(getToken());
//            chatMessage.setTokenIssuer("1");
//            chatMessage.setSubjectId(threadId);
//            chatMessage.setUniqueId(uniqueId);
//
//            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
//            jsonObject.remove("systemMetadata");
//            jsonObject.remove("metadata");
//            jsonObject.remove("repliedTo");
//            jsonObject.remove("contentCount");
//
//            if (Util.isNullOrEmpty(getTypeCode())) {
//                jsonObject.remove("typeCode");
//            } else {
//                jsonObject.remove("typeCode");
//                jsonObject.addProperty("typeCode", getTypeCode());
//            }
//
//            String asyncContent = jsonObject.toString();
//
//            //TODO clear history?
////            setCallBacks(null, null, null, true, Constants.CLEAR_HISTORY, null, uniqueId);
//
//            setCallBacks(null, null, null, true, Constants.GET_THREAD_ADMINS, null, uniqueId);
//
//            sendAsyncMessage(asyncContent, 4, "SEND_GET_THREAD_ADMINS");
//
//
//        }
//        return uniqueId;
    }

    /**
     * This Function sends ping message to keep user connected to
     * chat server and updates its status
     * <p>
     * long lastSentMessageTimeout =
     * long lastSentMessageTime =
     */

    private void pingWithDelay() {

        long lastSentMessageTimeout = 9 * 1000;

        lastSentMessageTime = new Date().getTime();


        pingRunOnUIThread(() -> {

            long currentTime = new Date().getTime();

            if (currentTime - lastSentMessageTime > lastSentMessageTimeout && chatReady) {
                ping();
            }
        }, 20000);

    }

    /**
     * Ping for staying chat alive
     */
    private void ping() {
        if (chatReady && async.getPeerId() != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(Constants.PING);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());

            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, 4, "CHAT PING");
        }
    }

    private void pingAfterSetToken() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.PING);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());

        checkToken = true;


        String asyncContent = gson.toJson(chatMessage);
        async.sendMessage(asyncContent, 4);
        showLog("**SEND_CHAT PING FOR CHECK TOKEN AUTHENTICATION", asyncContent);
    }

    private void showLog(String i, String json) {
        if (log) {
            Log.i(TAG, i);
            Log.i(TAG, json);
            if (!Util.isNullOrEmpty(json)) {
                listenerManager.callOnLogEvent(json);
            }
        }
    }

    private String handleMimType(Uri uri, File file) {
        String mimType;

        if (context.getContentResolver().getType(uri) != null) {
            mimType = context.getContentResolver().getType(uri);
        } else {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            int index = file.getName().lastIndexOf('.') + 1;
            String ext = file.getName().substring(index).toLowerCase();
            mimType = mime.getMimeTypeFromExtension(ext);
        }
        return mimType;
    }

    @SuppressLint("MissingPermission")
    private boolean isConnected(Activity context) {
        boolean isConnected = false;
        if (Permission.Check_ACCESS_NETWORK_STATE(context)) {
            NetworkInfo activeNetwork;
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    private void handleError(ChatMessage chatMessage) {

        Error error = gson.fromJson(chatMessage.getContent(), Error.class);
        if (error.getCode() == 401) {
            pingHandler.removeCallbacksAndMessages(null);
        } else if (error.getCode() == 21) {
            userInfoResponse = true;
            retryStepUserInfo = 1;
            chatReady = false;

            tokenHandler.removeCallbacksAndMessages(null);
            retrySetToken = 1;

            String errorMessage = error.getMessage();
            long errorCode = error.getCode();
            String errorJson = getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());

            pingHandler.removeCallbacksAndMessages(null);

            /*we are Changing the state of the chat because of the Client is not Authenticate*/
            listenerManager.callOnChatState("ASYNC_READY");

            return;
        }
        String errorMessage = error.getMessage();
        long errorCode = error.getCode();

        getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());
    }

    private void handleLastSeenUpdated(ChatMessage chatMessage) {
        showLog("LAST_SEEN_UPDATED", "");
        showLog(chatMessage.getContent(), "");
        listenerManager.callOnLastSeenUpdated(chatMessage.getContent());
    }

    private void handleThreadInfoUpdated(ChatMessage chatMessage) {
        ResultThread resultThread = new ResultThread();
        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
        resultThread.setThread(thread);

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        listenerManager.callOnThreadInfoUpdated(chatMessage.getContent(), chatResponse);
        showLog("THREAD_INFO_UPDATED", chatMessage.getContent());
    }

    private void handleRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        ResultThread resultThread = new ResultThread();
        Thread thread = new Thread();
        thread.setId(chatMessage.getSubjectId());
        resultThread.setThread(thread);
        String content = gson.toJson(chatResponse);

        showLog("RECEIVED_REMOVED_FROM_THREAD", content);
        listenerManager.callOnRemovedFromThread(content, chatResponse);
    }

    /**
     * After the set Token, we send ping for checking client Authenticated or not
     * the (boolean)checkToken is for that reason
     */
    private void handleOnPing(ChatMessage chatMessage) {

        showLog("RECEIVED_CHAT_PING", "");

        if (checkToken) {
            chatReady = true;
            checkToken = false;

            retrySetToken = 1;
            tokenHandler.removeCallbacksAndMessages(null);

            listenerManager.callOnChatState(CHAT_READY);
            showLog("** CLIENT_AUTHENTICATED_NOW", "");
            pingWithDelay();
        }
    }

    /**
     * When the new message arrived we send the deliver message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {

        try {
            MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

            if (cache) {
                CacheMessageVO cacheMessageVO = gson.fromJson(chatMessage.getContent(), CacheMessageVO.class);
                messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId());
            }

            ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            chatResponse.setHasError(false);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");
            ResultNewMessage resultNewMessage = new ResultNewMessage();
            resultNewMessage.setMessageVO(messageVO);
            resultNewMessage.setThreadId(chatMessage.getSubjectId());
            chatResponse.setResult(resultNewMessage);
            String json = gson.toJson(chatResponse);
            listenerManager.callOnNewMessage(json, chatResponse);
            long ownerId = 0;
            if (messageVO != null) {
                ownerId = messageVO.getParticipant().getId();
            }
            showLog("RECEIVED_NEW_MESSAGE", json);
//            if (true) {
            if (ownerId != getUserId()) {
                ChatMessage message = getChatMessage(messageVO);

                String asyncContent = gson.toJson(message);
                async.sendMessage(asyncContent, 4);
                Log.i(TAG, "SEND_DELIVERY_MESSAGE");
                listenerManager.callOnLogEvent(asyncContent);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }

    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (cache) {
            messageDatabaseHelper.deleteWaitQueueMsgs(messageUniqueId);
        } else {
            waitQList.remove(messageUniqueId);
        }
        try {
            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
                for (Callback callback : callbacks) {
                    if (messageUniqueId.equals(callback.getUniqueId())) {
                        int indexUnique = callbacks.indexOf(callback);
                        if (callbacks.get(indexUnique).isSent()) {

                            ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                            ResultMessage resultMessage = new ResultMessage();

                            chatResponse.setErrorCode(0);
                            chatResponse.setErrorMessage("");
                            chatResponse.setHasError(false);
                            chatResponse.setUniqueId(callback.getUniqueId());

                            resultMessage.setConversationId(chatMessage.getSubjectId());
                            resultMessage.setMessageId(Long.valueOf(chatMessage.getContent()));
                            chatResponse.setResult(resultMessage);

                            String json = gson.toJson(chatResponse);
                            listenerManager.callOnSentMessage(json, chatResponse);

                            runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (handlerSend.get(callback.getUniqueId()) != null) {
                                        handlerSend.get(callback.getUniqueId())
                                                .onSentResult(chatMessage.getContent());
                                    }
                                }
                            });

                            Callback callbackUpdateSent = new Callback();
                            callbackUpdateSent.setSent(false);
                            callbackUpdateSent.setDelivery(callback.isDelivery());
                            callbackUpdateSent.setSeen(callback.isSeen());
                            callbackUpdateSent.setUniqueId(callback.getUniqueId());

                            callbacks.set(indexUnique, callbackUpdateSent);
                            threadCallbacks.put(threadId, callbacks);
                            showLog("RECEIVED_SENT_MESSAGE", json);
                        }
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    static {
        sUIThreadHandler = new Handler(Looper.getMainLooper());
    }

    private static void runOnUIThread(Runnable runnable) {
        if (sUIThreadHandler != null) {
            sUIThreadHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    static {
        pingHandler = new Handler(Looper.getMainLooper());
    }

    private static void pingRunOnUIThread(Runnable runnable, long delay) {
        if (pingHandler != null) {
            pingHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
    }

    static {
        tokenHandler = new Handler(Looper.getMainLooper());
    }

    private static void retryTokenRunOnUIThread(Runnable runnable, long delay) {
        if (tokenHandler != null) {
            tokenHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
    }

    /**
     * When chat is ready its checking the message that was stored in the Sending Queue and if there is any,
     * it's sent them to async
     */
    private void checkMessageQueue() {

        try {
            if (cache) {
                List<SendingQueueCache> sendingQueueCaches = messageDatabaseHelper.getAllSendingQueue();
                if (log) Log.i(TAG, "checkMessageQueue");
                if (!Util.isNullOrEmpty(sendingQueueCaches)) {
                    for (SendingQueueCache sendingQueue : sendingQueueCaches) {
                        String uniqueId = sendingQueue.getUniqueId();
                        long threadId = sendingQueue.getThreadId();
                        setThreadCallbacks(threadId, uniqueId);
                        messageDatabaseHelper.insertWaitMessageQueue(sendingQueue);
                        messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
                        JsonObject jsonObject = (new JsonParser()).parse(sendingQueue.getAsyncContent()).getAsJsonObject();

                        jsonObject.remove("token");
                        jsonObject.addProperty("token", getToken());

                        if (log) Log.i(TAG, "checkMessageQueue");
                        String async = jsonObject.toString();
                        sendAsyncMessage(async, 4, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
                    }
                }
            } else {

                if (!sendingQList.isEmpty()) {
                    for (SendingQueueCache sendingQueue : sendingQList.values()) {
                        String uniqueId = sendingQueue.getUniqueId();
                        long threadId = sendingQueue.getThreadId();
                        setThreadCallbacks(threadId, uniqueId);

                        WaitQueueCache waitMessageQueue = new WaitQueueCache();

                        waitMessageQueue.setUniqueId(uniqueId);
                        waitMessageQueue.setId(sendingQueue.getId());
                        waitMessageQueue.setAsyncContent(sendingQueue.getAsyncContent());
                        waitMessageQueue.setMessage(sendingQueue.getMessage());
                        waitMessageQueue.setThreadId(sendingQueue.getThreadId());
                        waitMessageQueue.setMessageType(sendingQueue.getMessageType());
                        waitMessageQueue.setSystemMetadata(sendingQueue.getSystemMetadata());
                        waitMessageQueue.setMetadata(sendingQueue.getMetadata());

                        waitQList.put(uniqueId, waitMessageQueue);
//                        sendingQList.remove(uniqueId);

                        JsonObject jsonObject = (new JsonParser()).parse(sendingQueue.getAsyncContent()).getAsJsonObject();

                        jsonObject.remove("token");
                        jsonObject.addProperty("token", getToken());
                        sendAsyncMessage(jsonObject.toString(), 4, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
                    }
                    sendingQList.clear();
                }
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getCause().getMessage());
        }
    }

    private void handleSeen(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
            for (Callback callback : callbacks) {
                if (messageUniqueId.equals(callback.getUniqueId())) {
                    int indexUnique = callbacks.indexOf(callback);
                    while (indexUnique > -1) {
                        if (callbacks.get(indexUnique).isSeen()) {
                            ResultMessage resultMessage = gson.fromJson(chatMessage.getContent(), ResultMessage.class);
                            if (callbacks.get(indexUnique).isDelivery()) {

                                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                                chatResponse.setErrorMessage("");
                                chatResponse.setErrorCode(0);
                                chatResponse.setHasError(false);
                                chatResponse.setUniqueId(callback.getUniqueId());
                                chatResponse.setResult(resultMessage);

                                String json = gson.toJson(chatResponse);

                                listenerManager.callOnDeliveryMessage(json, chatResponse);

                                Callback callbackUpdateSent = new Callback();
                                callbackUpdateSent.setSent(callback.isSent());
                                callbackUpdateSent.setDelivery(false);
                                callbackUpdateSent.setSeen(callback.isSeen());
                                callbackUpdateSent.setUniqueId(callback.getUniqueId());

                                callbacks.set(indexUnique, callbackUpdateSent);
                                threadCallbacks.put(threadId, callbacks);
                                showLog("RECEIVED_DELIVERED_MESSAGE", json);
                            }

                            ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                            chatResponse.setErrorMessage("");
                            chatResponse.setErrorCode(0);
                            chatResponse.setHasError(false);
                            chatResponse.setUniqueId(callback.getUniqueId());
                            chatResponse.setResult(resultMessage);

                            String json = gson.toJson(chatResponse);
                            listenerManager.callOnSeenMessage(json, chatResponse);
                            callbacks.remove(indexUnique);
                            threadCallbacks.put(threadId, callbacks);
                            showLog("RECEIVED_SEEN_MESSAGE", json);

                        }
                        indexUnique--;
                    }
                    break;
                }
            }
        }
    }

    private void handleDelivery(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        try {
            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
                for (Callback callback : callbacks) {
                    if (messageUniqueId.equals(callback.getUniqueId())) {
                        int indexUnique = callbacks.indexOf(callback);
                        while (indexUnique > -1) {
                            if (callbacks.get(indexUnique).isDelivery()) {
                                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
                                ResultMessage resultMessage = gson.fromJson(chatMessage.getContent(), ResultMessage.class);
                                chatResponse.setErrorMessage("");
                                chatResponse.setErrorCode(0);
                                chatResponse.setHasError(false);
                                chatResponse.setUniqueId(callback.getUniqueId());

                                chatResponse.setResult(resultMessage);
                                String json = gson.toJson(chatResponse);

                                listenerManager.callOnDeliveryMessage(json, chatResponse);

                                Callback callbackUpdateSent = new Callback();
                                callbackUpdateSent.setSent(callback.isSent());
                                callbackUpdateSent.setDelivery(false);
                                callbackUpdateSent.setSeen(callback.isSeen());
                                callbackUpdateSent.setUniqueId(callback.getUniqueId());
                                callbacks.set(indexUnique, callbackUpdateSent);
                                threadCallbacks.put(threadId, callbacks);
                                showLog("RECEIVED_DELIVERED_MESSAGE", json);
                            }
                            indexUnique--;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage resultMessage = new ResultNewMessage();
        resultMessage.setThreadId(chatMessage.getSubjectId());
        resultMessage.setMessageVO(messageVO);
        chatResponse.setResult(resultMessage);
        String json = gson.toJson(chatResponse);

        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }
        showLog("RECEIVED_FORWARD_MESSAGE", json);

        listenerManager.callOnNewMessage(json, chatResponse);
        if (ownerId != getUserId()) {
            ChatMessage message = getChatMessage(messageVO);

            String asyncContent = gson.toJson(message);
            showLog("SEND_DELIVERY_MESSAGE", asyncContent);

            async.sendMessage(asyncContent, 4);
        }
    }

    private void handleResponseMessage(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        try {
            if (callback != null) {
                if (callback.getRequestType() >= 1) {
                    switch (callback.getRequestType()) {

                        case Constants.GET_HISTORY:

                            handleOutPutGetHistory(callback, chatMessage, messageUniqueId);
                            break;
                        case Constants.GET_CONTACTS:

                            handleGetContact(callback, chatMessage, messageUniqueId);
                            break;
                        case Constants.UPDATE_THREAD_INFO:

                            handleUpdateThreadInfo(chatMessage, messageUniqueId, callback);
                            break;
                        case Constants.GET_THREADS:
                            if (callback.isResult()) {
                                handleGetThreads(callback, chatMessage, messageUniqueId);
                            }
                            break;
                        case Constants.INVITATION:
                            if (callback.isResult()) {
                                handleCreateThread(callback, chatMessage, messageUniqueId);
                            }
                            break;
                        case Constants.MUTE_THREAD:

                            if (callback.isResult()) {
                                ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
                                String muteThreadJson = reformatMuteThread(chatMessage, chatResponse);
                                listenerManager.callOnMuteThread(muteThreadJson, chatResponse);
                                messageCallbacks.remove(messageUniqueId);
                                showLog("RECEIVE_MUTE_THREAD", muteThreadJson);
                            }
                            break;
                        case Constants.UN_MUTE_THREAD:

                            if (callback.isResult()) {
                                ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
                                String unmuteThreadJson = reformatMuteThread(chatMessage, chatResponse);
                                listenerManager.callOnUnmuteThread(unmuteThreadJson, chatResponse);
                                messageCallbacks.remove(messageUniqueId);
                                showLog("RECEIVE_UN_MUTE_THREAD", unmuteThreadJson);

                            }
                            break;
                        case Constants.EDIT_MESSAGE:

                            if (callback.isResult()) {
                                handleEditMessage(chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.USER_INFO:

                            handleOnGetUserInfo(chatMessage, messageUniqueId, callback);
                            break;
                        case Constants.THREAD_PARTICIPANTS:

                            handleOnGetParticipants(callback, chatMessage, messageUniqueId);

                            break;
                        case Constants.ADD_PARTICIPANT:
                            if (callback.isResult()) {
                                handleAddParticipant(chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.REMOVE_PARTICIPANT:
                            if (callback.isResult()) {
                                handleOutPutRemoveParticipant(callback, chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.LEAVE_THREAD:
                            if (callback.isResult()) {
                                handleOutPutLeaveThread(callback, chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.RENAME:

                            break;
                        case Constants.DELETE_MESSAGE:
                            handleOutPutDeleteMsg(chatMessage);

                            break;
                        case Constants.BLOCK:
                            if (callback.isResult()) {
                                handleOutPutBlock(chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.UNBLOCK:
                            if (callback.isResult()) {
                                handleUnBlock(chatMessage, messageUniqueId);
                            }

                            break;
                        case Constants.GET_BLOCKED:
                            if (callback.isResult()) {
                                handleOutPutGetBlockList(chatMessage);
                            }

                            break;
                        case Constants.DELIVERED_MESSAGE_LIST:
                            if (callback.isResult()) {
                                handleOutPutDeliveredMessageList(chatMessage, callback);
                            }
                            break;
                        case Constants.SEEN_MESSAGE_LIST:
                            if (callback.isResult()) {
                                handleOutPutSeenMessageList(chatMessage, callback);
                            }
                            break;

                        case Constants.GET_NOT_SEEN_DURATION: {
                            handleGetNotSeenDuration(callback, chatMessage, messageUniqueId);
                            break;
                        }


                    }
                }
            }
        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void handleOnGetParticipants(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        if (callback.isResult()) {

            ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

            String jsonParticipant = gson.toJson(chatResponse);

            listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);

            OutPutParticipant output = new OutPutParticipant();

            output.setResult(chatResponse.getResult());

            listenerManager.callOnGetThreadParticipant(output);

            messageCallbacks.remove(messageUniqueId);

            showLog("RECEIVE_PARTICIPANT", jsonParticipant);

        }
    }


    private void handleEditMessage(ChatMessage chatMessage, String messageUniqueId) {
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage newMessage = new ResultNewMessage();
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

        if (cache) {
            CacheMessageVO cacheMessageVO = gson.fromJson(chatMessage.getContent(), CacheMessageVO.class);
            messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId());
        }

        newMessage.setMessageVO(messageVO);
        newMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(newMessage);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String content = gson.toJson(chatResponse);
        showLog("RECEIVE_EDIT_MESSAGE", content);

        listenerManager.callOnEditedMessage(content, chatResponse);
        messageCallbacks.remove(messageUniqueId);
    }

    private void handleOutPutSeenMessageList(ChatMessage chatMessage, Callback callback) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
            resultParticipant.setContentCount(chatMessage.getContentCount());
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }

            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);
            listenerManager.callOnSeenMessageList(content, chatResponse);
            showLog("RECEIVE_SEEN_MESSAGE_LIST", content);

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    /**
     * After getting the key ChatState is 'CHAT_READY' and cache is working
     */

    private void generateEncryptionKey(String ssoHost) {

        String algorithm = "AES";
        int keySize = 256;
        long validity = 31536000;
        RetrofitHelperSsoHost retrofitHelperSsoHost = new RetrofitHelperSsoHost(ssoHost);
        SSOApi api = retrofitHelperSsoHost.getService(SSOApi.class);


        String bToken = "bearer " + getToken();


        Call<EncResponse> responseCallback = api.generateEncryptionKey(bToken,
                algorithm, keySize, false, validity);


        responseCallback.enqueue(new retrofit2.Callback<EncResponse>() {
            @Override
            public void onResponse(Call<EncResponse> call, Response<EncResponse> response) {

                if (response.body() != null) {

                    if (response.isSuccessful()) {



                        String secretKey = response.body().getSecretKey();


                        DaggerMessageComponent.builder()
                                .appDatabaseModule(new AppDatabaseModule(getContext(), secretKey))
                                .appModule(new AppModule(context))
                                .build()
                                .inject(instance);

                        setKey(secretKey);

                        listenerManager.callOnChatState("CHAT_READY");
                        chatReady = true;
                        checkMessageQueue();
                        showLog("CHAT_READY", "");
                        permit = true;


                    } else {


                        if (log) Log.e(TAG, response.errorBody().toString());

                        DaggerMessageComponent.builder()
                                .appDatabaseModule(new AppDatabaseModule(getContext()))
                                .appModule(new AppModule(context))
                                .build()
                                .inject(instance);

                        listenerManager.callOnChatState("CHAT_READY");
                        chatReady = true;

                        checkMessageQueue();
                        showLog("CHAT_READY_WITHOUT_ENCRYPTION_US", "");
                        permit = true;


                    }


                } else {


                    if (log) Log.e(TAG, response.errorBody().toString());
                    DaggerMessageComponent.builder()
                            .appDatabaseModule(new AppDatabaseModule(getContext()))
                            .appModule(new AppModule(context))
                            .build()
                            .inject(instance);


                    listenerManager.callOnChatState("CHAT_READY");
                    chatReady = true;
                    checkMessageQueue();
                    showLog("CHAT_READY_WITHOUT_ENCRYPTION_NS", "");

                    permit = true;

                }


            }

            @Override
            public void onFailure(Call<EncResponse> call, Throwable t) {

                Log.d(TAG, "Failure On: " + t.getMessage());

            }
        });


//
//        Observable<Response<EncResponse>> observable
//                = api.generateEncryptionKey("bearer " + getToken(),
//                algorithm, keySize, false, validity);
//
//        observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Response<EncResponse>>() {
//                    @Override
//                    public void call(Response<EncResponse> encResponseResponse) {
//
//
//                        if (encResponseResponse.body() != null) {
//                            String secretKey = encResponseResponse.body().getSecretKey();
//                            DaggerMessageComponent.builder()
//                                    .appDatabaseModule(new AppDatabaseModule(getContext(), secretKey))
//                                    .appModule(new AppModule(context))
//                                    .build()
//                                    .inject(instance);
//                            setKey(secretKey);
//
//                            listenerManager.callOnChatState("CHAT_READY");
//                            chatReady = true;
//                            checkMessageQueue();
//                            showLog("CHAT_READY", "");
//                            permit = true;
//
//                        } else if (encResponseResponse.errorBody() != null) {
//                            if (log) Log.e(TAG, encResponseResponse.errorBody().toString());
//                            listenerManager.callOnChatState("CHAT_READY");
//                            chatReady = true;
//                            checkMessageQueue();
//                            showLog("CHAT_READY", "");
//                        }
//                    }
//                }, throwable -> {
//                    try {
//                        if (log) Log.e(TAG, throwable.getCause().getMessage());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "throw message is null");
//                    }
//                });
//


    }

    /**
     * It is sent uniqueIds of messages from waitQueue to ensure all of them have been sent.
     */
    private void checkMessagesValidation(long threadId) {

        /*  if waitQueue had these messages then send request's getHistory and in onSent remove them from wait queue
         */
        List<WaitQueueCache> uniqueIds = messageDatabaseHelper.getWaitQueueMsg(threadId);
        if (!Util.isNullOrEmpty(uniqueIds)) {

            String uniqueId = generateUniqueId();
            Type uniqueIdsType = new TypeToken<ArrayList<String>>() {
            }.getType();
            String listUniqueIds = gson.toJson(uniqueIds, uniqueIdsType);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(listUniqueIds);
            chatMessage.setType(Constants.GET_HISTORY);
            chatMessage.setToken(getToken());
            chatMessage.setTokenIssuer("1");
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, 3, "CHECK_HISTORY_MESSAGES");
        }
    }

    /**
     * order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * order should be set with lower case
     */
    private void getHistoryMain(History history, long threadId, ChatHandler handler, String uniqueId) {
        long offsets = history.getOffset();
        long firstMessageId = history.getFirstMessageId();
        long lastMessageId = history.getLastMessageId();
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();
        long id = history.getId();

        String query = history.getQuery();

        JsonObject jObj = (JsonObject) gson.toJsonTree(history);
        if (history.getLastMessageId() == 0) {
            jObj.remove("lastMessageId");
        }

        if (history.getFirstMessageId() == 0) {
            jObj.remove("firstMessageId");
        }

        if (history.getId() <= 0) {
            jObj.remove("id");
        }

        if (Util.isNullOrEmpty(query)) {
            jObj.remove("query");
        }

        if (Util.isNullOrEmpty(fromTime)) {
            jObj.remove("fromTime");
        }

        if (Util.isNullOrEmpty(fromTimeNanos)) {
            jObj.remove("fromTimeNanos");
        }

        if (Util.isNullOrEmpty(toTime)) {
            jObj.remove("toTime");
        }

        if (Util.isNullOrEmpty(toTimeNanos)) {
            jObj.remove("toTimeNanos");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jObj.toString());
        chatMessage.setType(Constants.GET_HISTORY);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        String asyncContent = jsonObject.toString();
        String order;
        if (Util.isNullOrEmpty(history.getOrder())) {
            order = "asc";
        } else {
            order = history.getOrder();
        }

        setCallBacks(firstMessageId, lastMessageId, order, history.getCount(), history.getOffset(), uniqueId, id, true, query);
        if (handler != null) {
            handler.onGetHistory(uniqueId);
        }

        sendAsyncMessage(asyncContent, 3, "SEND GET THREAD HISTORY");
    }

    private void handleOutPutDeliveredMessageList(ChatMessage chatMessage, Callback callback) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());

            ResultParticipant resultParticipant = new ResultParticipant();

            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());

            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
            resultParticipant.setContentCount(chatMessage.getContentCount());
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }


            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);
            listenerManager.callOnDeliveredMessageList(content, chatResponse);
            showLog("RECEIVE_DELIVERED_MESSAGE_LIST", content);

        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void handleGetContact(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage, callback);
        String contactJson = gson.toJson(chatResponse);
        listenerManager.callOnGetContacts(contactJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_GET_CONTACT", contactJson);

    }

    private void handleCreateThread(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThread> chatResponse = reformatCreateThread(chatMessage);

        String inviteJson = gson.toJson(chatResponse);
        listenerManager.callOnCreateThread(inviteJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_CREATE_THREAD", inviteJson);

    }

    private void handleGetThreads(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage, callback);
        String threadJson = gson.toJson(chatResponse);
        listenerManager.callOnGetThread(threadJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_GET_THREAD", threadJson);

    }

    private void handleSystemMessage(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        //todo

        /**
         *
         *   case chatMessageVOTypes.SYSTEM_MESSAGE:
         *                         fireEvent('systemEvents', {
         *                             type: 'IS_TYPING',
         *                             result: {
         *                                 thread: threadId,
         *                                 user: messageContent
         *                             }
         *                         });
         *                         break;
         *
         *
         *  systemMessageTypes = {
         *
         *                 IS_TYPING: '1',
         *                 RECORD_VOICE: '2',
         *                 UPLOAD_PICTURE: '3',
         *                 UPLOAD_VIDEO: '4',
         *                 UPLOAD_SOUND: '5',
         *                 UPLOAD_FILE: '6'
         *
         *             },
         *
         *
         *
         *
         *
         * **/


        if (callback != null && callback.isResult()) {

            messageCallbacks.remove(messageUniqueId);

        }

        ChatResponse<ResultSignalMessage> result = reformatSignalMessage(chatMessage);

        if (result == null) {
            return;
        }

        OutputSignalMessage output = new OutputSignalMessage();

        output.setResultSignalMessage(result.getResult());

        ResultSignalMessage sm = result.getResult();

        output.setSignalMessageType(getSignalMessageType(sm.getSmt()));

        output.setSignalSenderName(sm.getUser());

        listenerManager.callOnGetSignalMessage(output);

        showLog("RECEIVE_SIGNAL_MESSAGE", chatMessage.getContent());


    }

    private String getSignalMessageType(int intType) {

        switch (intType) {

            case 1: {

                return "IS_TYPING";


            }

            case 2: {

                return "RECORD_VOICE";


            }
            case 3: {

                return "UPLOAD_PICTURE";


            }
            case 4: {

                return "UPLOAD_VIDEO";


            }
            case 5: {

                return "UPLOAD_SOUND";


            }
            case 6: {

                return "UPLOAD_FILE";


            }
            default: {

                return "IS_TYPING";


            }


        }


    }

    private ChatResponse<ResultSignalMessage> reformatSignalMessage(ChatMessage chatMessage) {

        ChatResponse<ResultSignalMessage> result = new ChatResponse<>();


        ResultSignalMessage signalMessage = null;

        try {
            signalMessage = gson.fromJson(chatMessage.getContent(), ResultSignalMessage.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            listenerManager.callOnError("Exception in reformatting signal message content", null);

            return null;

        }


        result.setResult(signalMessage);


        return result;
    }


    private void handleGetNotSeenDuration(Callback callback, ChatMessage chatMessage, String messageUniqueId) {


        messageCallbacks.remove(messageUniqueId);


        if (callback != null && callback.isResult()) {

            ChatResponse<ResultNotSeenDuration> result = reformatNotSeenDuration(chatMessage, callback);

            if (result == null) {

                messageCallbacks.remove(messageUniqueId);

                return;

            }


            OutPutNotSeenDurations output = new OutPutNotSeenDurations();

            output.setResultNotSeenDuration(result.getResult());

            listenerManager.callOnGetNotSeenDuration(output);


            showLog("RECEIVE_NOT_SEEN_DURATION", chatMessage.getContent());

        } else {

            listenerManager.callOnError("RECEIVE NOT SEEN DURATION FAILED", null);


        }


    }

    private ChatResponse<ResultNotSeenDuration> reformatNotSeenDuration(ChatMessage chatMessage, Callback callback) {

        ChatResponse<ResultNotSeenDuration> response = new ChatResponse<>();

        Map<String, Long> idNotSeenPairs = new HashMap<>();

        JsonParser parser = new JsonParser();

        JsonObject jsonObject = null;

        try {
            jsonObject = parser
                    .parse(chatMessage.getContent())
                    .getAsJsonObject();

        } catch (Exception e) {
            e.printStackTrace();
            listenerManager.callOnError(e.getMessage(), null);
            return null;

        }

        for (String key :
                jsonObject.keySet()) {

            idNotSeenPairs.put(key, jsonObject.get(key).getAsLong());

        }

        ResultNotSeenDuration resultNotSeenDuration = new ResultNotSeenDuration();

        resultNotSeenDuration.setIdNotSeenPair(idNotSeenPairs);

        response.setResult(resultNotSeenDuration);

        return response;

    }


    private void handleUpdateThreadInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        if (callback.isResult()) {

            Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

            ResultThread resultThread = new ResultThread();

            resultThread.setThread(thread);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");
            chatResponse.setHasError(false);
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            chatResponse.setResult(resultThread);

            String threadJson = gson.toJson(chatResponse);
            messageCallbacks.remove(messageUniqueId);

            listenerManager.callOnUpdateThreadInfo(threadJson, chatResponse);
            showLog("RECEIVE_UPDATE_THREAD_INFO", threadJson);
        }
    }

    /**
     * Its check the Failed Queue {@link #checkMessageQueue()} to send all the message that is waiting to be send.
     */
    private void handleOnGetUserInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        if (callback.isResult()) {
            userInfoResponse = true;
            ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
            UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);
            String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
            listenerManager.callOnUserInfo(userInfoJson, chatResponse);
            messageCallbacks.remove(messageUniqueId);
            showLog("RECEIVE_USER_INFO", userInfoJson);


//            if there is a key its ok if not it will go for the key and then chat ready

            if (permit) {
                listenerManager.callOnChatState("CHAT_READY");
                chatReady = true;
                checkMessageQueue();
                showLog("CHAT_READY", "");
            } else {
                showLog("GENERATE_KEY", "");
                generateEncryptionKey(getSsoHost());
            }

            //ping start after the response of the get userInfo
            pingWithDelay();
        }
    }

    private String mainReplyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, String metaData, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();

        /* Add to sending Queue*/
        SendingQueueCache sendingQueue = new SendingQueueCache();
        sendingQueue.setSystemMetadata(systemMetaData);
        sendingQueue.setMessageType(messageType);
        sendingQueue.setThreadId(threadId);
        sendingQueue.setUniqueId(uniqueId);
        sendingQueue.setMessage(messageContent);
        sendingQueue.setMetadata(metaData);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setContent(messageContent);
        chatMessage.setTime(1000);
        chatMessage.setType(Constants.MESSAGE);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetaData)) {
            jsonObject.remove("systemMetaData");
        } else {
            jsonObject.remove("systemMetaData");
            jsonObject.addProperty("systemMetaData", systemMetaData);
        }

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        if (Util.isNullOrEmpty(messageType)) {
            jsonObject.remove("messageType");
        } else {
            jsonObject.remove("messageType");
            jsonObject.addProperty("messageType", messageType);
        }

        String asyncContent = jsonObject.toString();

        sendingQueue.setAsyncContent(asyncContent);

        if (cache) {
            messageDatabaseHelper.insertSendingMessageQueue(sendingQueue);
        } else {
            sendingQList.put(uniqueId, sendingQueue);
        }
        if (log)
            Log.i(TAG, "Message with this" + "uniqueId" + uniqueId + "has been added to Message Queue");
        if (chatReady) {
            if (cache) {
                messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
                messageDatabaseHelper.insertWaitMessageQueue(sendingQueue);
            } else {
                sendingQList.remove(uniqueId);
                WaitQueueCache waitMessageQueue = new WaitQueueCache();

                waitMessageQueue.setUniqueId(sendingQueue.getUniqueId());
                waitMessageQueue.setId(sendingQueue.getId());
                waitMessageQueue.setAsyncContent(sendingQueue.getAsyncContent());
                waitMessageQueue.setMessage(sendingQueue.getMessage());
                waitMessageQueue.setThreadId(sendingQueue.getThreadId());
                waitMessageQueue.setMessageType(sendingQueue.getMessageType());
                waitMessageQueue.setSystemMetadata(sendingQueue.getSystemMetadata());
                waitMessageQueue.setMetadata(sendingQueue.getMetadata());
                waitQList.put(uniqueId, waitMessageQueue);
            }

            setThreadCallbacks(threadId, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_REPLY_MESSAGE");

            if (handler != null) {
                handler.onReplyMessage(uniqueId);
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    private String getKey() {


        String key = mSecurePrefs.getString("KEY", null);
        return key;
    }

    private void setKey(String key) {
        mSecurePrefs.edit().putString("KEY", key).apply();
    }

    private void retryOnGetUserInfo() {
        runOnUIUserInfoThread(new Runnable() {
            @Override
            public void run() {
                if (userInfoResponse) {
                    getUserInfoHandler.removeCallbacksAndMessages(null);
                    userInfoResponse = false;
                    retryStepUserInfo = 1;

                } else {
                    if (retryStepUserInfo < 60) retryStepUserInfo *= 4;
                    getUserInfo(null);
                    runOnUIUserInfoThread(this, retryStepUserInfo * 1000);
                    showLog("getUserInfo " + " retry in " + retryStepUserInfo + " s ", "");
                }
            }
        }, retryStepUserInfo * 1000);
    }

    static {
        getUserInfoHandler = new Handler(Looper.getMainLooper());
    }

    private static void runOnUIUserInfoThread(Runnable runnable, long delayedTime) {
        if (getUserInfoHandler != null) {
            getUserInfoHandler.postDelayed(runnable, delayedTime);
        } else {
            runnable.run();
        }
    }

    private void handleOutPutLeaveThread(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultLeaveThread> chatResponse = new ChatResponse<>();

        ResultLeaveThread leaveThread = gson.fromJson(chatMessage.getContent(), ResultLeaveThread.class);

        long threadId = chatMessage.getSubjectId();

        leaveThread.setThreadId(threadId);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(leaveThread);

        String jsonThread = gson.toJson(chatResponse);

        if (cache) {
            messageDatabaseHelper.leaveThread(threadId);
        }

        listenerManager.callOnThreadLeaveParticipant(jsonThread, chatResponse);
        if (callback != null) {
            messageCallbacks.remove(messageUniqueId);
        }

        showLog("RECEIVE_LEAVE_THREAD", jsonThread);
    }

    private void handleAddParticipant(ChatMessage chatMessage, String messageUniqueId) {
        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

        if (cache) {
            ThreadVo threadVo = gson.fromJson(chatMessage.getContent(), ThreadVo.class);

            List<CacheParticipant> cacheParticipants = threadVo.getParticipants();
            messageDatabaseHelper.saveParticipants(cacheParticipants, thread.getId(), getExpireAmount());
        }

        ChatResponse<ResultAddParticipant> chatResponse = new ChatResponse<>();

        ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
        resultAddParticipant.setThread(thread);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setResult(resultAddParticipant);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonAddParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadAddParticipant(jsonAddParticipant, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_ADD_PARTICIPANT", jsonAddParticipant);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {

        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        long messageId = Long.valueOf(chatMessage.getContent());
        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageId);
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);

        String jsonDeleteMsg = gson.toJson(chatResponse);

        if (cache) {
            messageDatabaseHelper.deleteMessage(messageId);
            showLog("DeleteMessage from dataBase with this messageId" + " " + messageId, "");
        }

        listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);
        showLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);
    }

    private void handleOutPutBlock(ChatMessage chatMessage, String messageUniqueId) {

        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonBlock = gson.toJson(chatResponse);
        listenerManager.callOnBlock(jsonBlock, chatResponse);
        showLog("RECEIVE_BLOCK", jsonBlock);
        messageCallbacks.remove(messageUniqueId);
    }


    public String startTyping(RequestSignalMsg signalMsg) {

        if (!chatReady) return "";

        return startSignalMessage(signalMsg, "SEND IS TYPING");


//
//        String uniqueId = generateUniqueId();
//        //
//        int signalType = signalMsg.getSignalType();
//        long threadId = signalMsg.getThreadId();
//
//
//        JsonObject contentObject = new JsonObject();
//        contentObject.addProperty("type", String.valueOf(signalType));
//
//
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setContent(contentObject.toString());
//        chatMessage.setType(Constants.SYSTEM_MESSAGE);
//        chatMessage.setToken(getToken());
//        chatMessage.setTokenIssuer("1");
//        chatMessage.setUniqueId(uniqueId);
//        chatMessage.setSubjectId(threadId);
//
//
//        JsonObject mainObject = (JsonObject) gson.toJsonTree(chatMessage);
//
//
//        mainObject.remove("systemMetadata");
//        mainObject.remove("metadata");
//        mainObject.remove("repliedTo");
//        mainObject.remove("contentCount");
//        mainObject.remove("time");
//
//        if (Util.isNullOrEmpty(getTypeCode())) {
//            mainObject.remove("typeCode");
//        } else {
//            mainObject.remove("typeCode");
//            mainObject.addProperty("typeCode", getTypeCode());
//        }
//
//
//        String asyncContent = mainObject.toString();
//
//
//        setSignalHandlerThread();
//
//        if(startSignalHandler == null || signalMessageHandlerThread == null) return;
//
//
//        Runnable job = new Runnable() {
//
//            @Override
//            public void run() {
//
//
//                sendAsyncMessage(asyncContent, 4, "IS TYPING...");
//
//                startSignalHandler.postDelayed(this, getSignalIntervalTime());
//
//
//            }
//        };
//
//        startSignalHandler.post(job);


    }


    public boolean stopTyping(String uniqueId) {

        return stopSignalMessage(uniqueId);

//        if (signalMessageHandlerThread.isAlive()) {
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//
//                signalMessageHandlerThread.quitSafely();
//
//            } else {
//
//                signalMessageHandlerThread.quit();
//
//            }
//
//
//            signalMessageHandlerThread = null;
//
//            startSignalHandler.removeCallbacksAndMessages(null);
//
//            startSignalHandler = null;
//
//            showLog("STOP_SIGNAL", "");
//        }


    }


    private void signalMessage(RequestSignalMsg requestSignalMsg, String logMessage, String uniqueId) {

        int signalType = requestSignalMsg.getSignalType();
        long threadId = requestSignalMsg.getThreadId();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", String.valueOf(signalType));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jsonObject.toString());
        chatMessage.setType(Constants.SYSTEM_MESSAGE);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObjectCht = (JsonObject) gson.toJsonTree(chatMessage);


        jsonObjectCht.remove("systemMetadata");
        jsonObjectCht.remove("metadata");
        jsonObjectCht.remove("repliedTo");
        jsonObjectCht.remove("contentCount");
        jsonObjectCht.remove("time");

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObjectCht.remove("typeCode");
        } else {
            jsonObjectCht.remove("typeCode");
            jsonObjectCht.addProperty("typeCode", getTypeCode());
        }
        String asyncContent = jsonObjectCht.toString();
        sendAsyncMessage(asyncContent, 3, logMessage);
    }


    private void handleClearHistory(ChatMessage chatMessage) {

        ChatResponse<ResultClearHistory> chatResponseClrHistory = new ChatResponse<>();

        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        String jsonClrHistory = gson.toJson(chatResponseClrHistory);
        listenerManager.callOnClearHistory(jsonClrHistory, chatResponseClrHistory);
        showLog("RECEIVE_CLEAR_HISTORY", jsonClrHistory);

    }

    private void handleSetRole(ChatMessage chatMessage) {

        ChatResponse<ResultSetAdmin> chatResponse = new ChatResponse<>();

        ResultSetAdmin resultSetAdmin = new ResultSetAdmin();

        ArrayList<Admin> admins = gson
                .fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
                }.getType());

        resultSetAdmin.setAdmins(admins);

        chatResponse.setResult(resultSetAdmin);

        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String responseJson = gson.toJson(chatResponse);

        OutputSetRoleToUser output = new OutputSetRoleToUser();

        output.setResultSetAdmin(resultSetAdmin);

        listenerManager.callOnSetRoleToUser(responseJson, output);

        showLog("RECEIVE_SET_ROLE", responseJson);
    }

    private void handleUnBlock(ChatMessage chatMessage, String messageUniqueId) {

        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String jsonUnBlock = gson.toJson(chatResponse);
        listenerManager.callOnUnBlock(jsonUnBlock, chatResponse);
        showLog("RECEIVE_UN_BLOCK", jsonUnBlock);
        messageCallbacks.remove(messageUniqueId);
    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();

        List<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        resultBlockList.setContacts(contacts);
        chatResponse.setResult(resultBlockList);
        String jsonGetBlock = gson.toJson(chatResponse);
        listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);
        showLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);
    }

    private void handleOutPutRemoveParticipant(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

        String jsonRmParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOutPutGetHistory(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
        }.getType());

        ResultHistory resultHistory = new ResultHistory();

        if (cache) {

            List<CacheMessageVO> cMessageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheMessageVO>>() {
            }.getType());

            messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, chatMessage.getSubjectId(), cMessageVOS);
            messageDatabaseHelper.saveHistory(cMessageVOS, chatMessage.getSubjectId());

            resultHistory.setSending(messageDatabaseHelper.getAllSendingQueueByThreadId(chatMessage.getSubjectId()));
            resultHistory.setUploadingQueue(messageDatabaseHelper.getAllUploadingQueueByThreadId(chatMessage.getSubjectId()));
            resultHistory.setFailed(messageDatabaseHelper.getAllWaitQueueCacheByThreadId(chatMessage.getSubjectId()));
        }

        ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();

        resultHistory.setNextOffset(callback.getOffset() + messageVOS.size());
        resultHistory.setContentCount(chatMessage.getContentCount());
        if (messageVOS.size() + callback.getOffset() < chatMessage.getContentCount()) {
            resultHistory.setHasNext(true);
        } else {
            resultHistory.setHasNext(false);
        }

        resultHistory.setHistory(messageVOS);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String json = gson.toJson(chatResponse);
        listenerManager.callOnGetThreadHistory(json, chatResponse);
        showLog("RECEIVE_GET_HISTORY", json);
        messageCallbacks.remove(messageUniqueId);
    }

    private String getContactMain(Integer count, Long offset, boolean syncContact, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        count = count != null && count > 0 ? count : 50;
        offset = offset != null && offset >= 0 ? offset : 0;

        if (cache) {
            ArrayList<Contact> arrayList = new ArrayList<>(messageDatabaseHelper.getContacts(count, offset));
            ChatResponse<ResultContact> chatResponse = new ChatResponse<>();

            ResultContact resultContact = new ResultContact();
            resultContact.setContacts(arrayList);
            chatResponse.setResult(resultContact);
            chatResponse.setCache(true);
            resultContact.setContentCount(messageDatabaseHelper.getContactCount());

            String contactJson = gson.toJson(chatResponse);

            listenerManager.callOnGetContacts(contactJson, chatResponse);
            showLog("CACHE_GET_CONTACT", contactJson);
        }
        if (chatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setOffset(offset);

            JsonObject jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
            jObj.remove("lastMessageId");
            jObj.remove("firstMessageId");

            jObj.remove("count");
            jObj.addProperty("size", count);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(jObj.toString());
            chatMessage.setType(Constants.GET_CONTACTS);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            if (syncContact) {
                setCallBacks(null, null, null, false, Constants.GET_CONTACTS, offset, uniqueId);
            } else {
                setCallBacks(null, null, null, true, Constants.GET_CONTACTS, offset, uniqueId);
            }
            sendAsyncMessage(asyncContent, 3, "GET_CONTACT_SEND");
            if (handler != null) {
                handler.onGetContact(uniqueId);
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;

    }

    @NonNull
    private String getErrorOutPut(String errorMessage, long errorCode, String uniqueId) {
        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
        String jsonError = gson.toJson(error);
        listenerManager.callOnError(jsonError, error);
        listenerManager.callOnLogEvent(jsonError);

        if (log) {
            Log.e(TAG, "ErrorMessage :" + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);
        }
        return jsonError;
    }

    private String getTypeCode() {
        if (Util.isNullOrEmpty(typeCode)) {
            typeCode = "default";
        }
        return typeCode;
    }

    private void handleCacheThread(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName) {
        if (offset == null) {
            offset = 0L;
        }

        List<Thread> threads = messageDatabaseHelper.getThreadRaw(count, offset, threadIds, threadName);

        if (!Util.isNullOrEmpty(threads)) {

            ChatResponse<ResultThreads> chatResponse = new ChatResponse<>();
            int contentCount = messageDatabaseHelper.getThreadCount();

            ResultThreads resultThreads = new ResultThreads();
            resultThreads.setThreads(threads);
            resultThreads.setContentCount(contentCount);
            chatResponse.setCache(true);

            if (threads.size() + offset < contentCount) {
                resultThreads.setHasNext(true);
            } else {
                resultThreads.setHasNext(false);
            }
            resultThreads.setNextOffset(offset + threads.size());
            chatResponse.setResult(resultThreads);

            String threadJson = gson.toJson(chatResponse);
            listenerManager.callOnGetThread(threadJson, chatResponse);
            if (log) Log.i(TAG, "CACHE_GET_THREAD");
            listenerManager.callOnLogEvent(threadJson);
        }
    }

    private int getExpireAmount() {
        if (Util.isNullOrEmpty(expireAmount)) {
            expireAmount = 2 * 24 * 60 * 60;
        }
        return expireAmount;
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */
    private String deliveredMessageList(RequestDeliveredMessageList requestParams) {

        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(Constants.DELIVERED_MESSAGE_LIST);

                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(requestParams.getTypeCode())) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.remove("typeCode");
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", requestParams.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.DELIVERED_MESSAGE_LIST, requestParams.getOffset(), uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
        return uniqueId;
    }

    //Get the list of the participants that saw the specific message
    /*
     * The replacement method is getMessageSeenList.
     * */
    private String seenMessageList(RequestSeenMessageList requestParams) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            try {
                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(Constants.SEEN_MESSAGE_LIST);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(requestParams.getTypeCode())) {
                    if (Util.isNullOrEmpty(getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.remove("typeCode");
                        jsonObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", requestParams.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.SEEN_MESSAGE_LIST, requestParams.getOffset(), uniqueId);
                sendAsyncMessage(asyncContent, 4, "SEND_SEEN_MESSAGE_LIST");
            } catch (Throwable e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
            }
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    //TODO test cache
    private ChatResponse<ResultParticipant> reformatThreadParticipants(Callback callback, ChatMessage chatMessage) {

        ArrayList<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());


        if (cache) {
            List<CacheParticipant> cacheParticipants;
            cacheParticipants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheParticipant>>() {
            }.getType());

            Log.d("MTAG","Cache Participant to Save: " + cacheParticipants.toString() );

            messageDatabaseHelper.saveParticipants(cacheParticipants, chatMessage.getSubjectId(), getExpireAmount());
        }

        ChatResponse<ResultParticipant> outPutParticipant = new ChatResponse<>();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        outPutParticipant.setUniqueId(chatMessage.getUniqueId());

        ResultParticipant resultParticipant = new ResultParticipant();

        resultParticipant.setContentCount(chatMessage.getContentCount());

        if (callback != null) {
            if (participants.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultParticipant.setHasNext(true);
            } else {
                resultParticipant.setHasNext(false);
            }
            resultParticipant.setNextOffset(callback.getOffset() + participants.size());
        }

        resultParticipant.setParticipants(participants);
        outPutParticipant.setResult(resultParticipant);
        return outPutParticipant;
    }

    private void sendTextMessageWithFile(String description, long threadId, String metaData, String systemMetadata, String uniqueId, String typeCode, Integer messageType) {

        /* Add to sending Queue*/
        SendingQueueCache sendingQueue = new SendingQueueCache();
        sendingQueue.setSystemMetadata(systemMetadata);
        sendingQueue.setMessageType(messageType);
        sendingQueue.setThreadId(threadId);
        sendingQueue.setUniqueId(uniqueId);
        sendingQueue.setMessage(description);
        sendingQueue.setMetadata(metaData);

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent(description);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());
        chatMessage.setMetadata(metaData);

        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTime(1000);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        String asyncContent = jsonObject.toString();

        sendingQueue.setAsyncContent(asyncContent);

        if (cache) {
            messageDatabaseHelper.insertSendingMessageQueue(sendingQueue);
        } else {
            sendingQList.put(uniqueId, sendingQueue);
        }

        if (chatReady) {
            setThreadCallbacks(threadId, uniqueId);
            sendAsyncMessage(asyncContent, 4, "SEND_TXT_MSG_WITH_FILE");
        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
    }

    private void setThreadCallbacks(long threadId, String uniqueId) {
        try {
            if (chatReady) {
                Callback callback = new Callback();
                callback.setDelivery(true);
                callback.setSeen(true);
                callback.setSent(true);
                callback.setUniqueId(uniqueId);
                ArrayList<Callback> callbackList = threadCallbacks.get(threadId);
                if (!Util.isNullOrEmpty(callbackList)) {
                    callbackList.add(callback);
                    threadCallbacks.put(threadId, callbackList);
                } else {
                    ArrayList<Callback> callbacks = new ArrayList<>();
                    callbacks.add(callback);
                    threadCallbacks.put(threadId, callbacks);
                }
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void setCallBacks(Boolean delivery, Boolean sent, Boolean seen, Boolean result, int requestType, Long offset, String uniqueId) {

        try {
            if (chatReady || asyncReady) {
                delivery = delivery != null ? delivery : false;
                sent = sent != null ? sent : false;
                seen = seen != null ? seen : false;
                result = result != null ? result : false;
                offset = offset != null ? offset : 0;

                Callback callback = new Callback();

                callback.setDelivery(delivery);
                callback.setOffset(offset);
                callback.setSeen(seen);
                callback.setSent(sent);
                callback.setRequestType(requestType);
                callback.setResult(result);
                messageCallbacks.put(uniqueId, callback);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }


    private void setCallBacks(long firstMessageId, long lastMessageId, String order, long count
            , Long offset, String uniqueId, long msgId, boolean messageCriteriaVO, String query) {

        try {
            if (chatReady || asyncReady) {

                offset = offset != null ? offset : 0;

                Callback callback = new Callback();
                callback.setFirstMessageId(firstMessageId);
                callback.setLastMessageId(lastMessageId);
                callback.setOffset(offset);
                callback.setCount(count);
                callback.setOrder(order);
                callback.setMessageId(msgId);
                callback.setResult(true);
                callback.setQuery(query);
                callback.setMetadataCriteria(messageCriteriaVO);
                callback.setRequestType(Constants.GET_HISTORY);

                messageCallbacks.put(uniqueId, callback);
            }
        } catch (Exception e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void sendAsyncMessage(String asyncContent, int asyncMsgType, String logMessage) {
        if (chatReady) {
            showLog(logMessage, asyncContent);
            try {
                async.sendMessage(asyncContent, asyncMsgType);
            } catch (Exception e) {
                if (log) Log.e(TAG, e.getMessage());
                return;
            }

            pingWithDelay();

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, null);
        }
    }

    /**
     * Get the list of the Device Contact
     */
    // TODO working progress
    private List<PhoneContact> getPhoneContact(Context context) {
        String firstName;
        String phoneNumber;
        String lastName;
        String empty = "";
        int version;
        ArrayList<PhoneContact> newPhoneContact = new ArrayList<>();

        try {

            List<PhoneContact> cachePhoneContacts = phoneContactDbHelper.getPhoneContacts();

            HashMap<Long, PhoneContact> map = new HashMap<>();

            if (cachePhoneContacts != null && cachePhoneContacts.size() > 0) {
                for (PhoneContact contact : cachePhoneContacts) {
                    map.put(contact.getPhoneNumber(), contact);
                }
            }

            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor == null) throw new AssertionError();


            /*
             * get Contact from phone Contact
             * */
            while (cursor.moveToNext()) {
                firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                lastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                version = cursor.getInt(cursor.getColumnIndex(ContactsContract.RawContacts.VERSION));
                phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                char ch1 = phoneNumber.charAt(0);

//            if (!Character.toString(ch1).equals("+")) {
//                contact.setCellphoneNumber(phoneNumber.replaceAll(Character.toString(ch1), "+98"));
//            }
                PhoneContact phoneContact = new PhoneContact();

                if (!Util.isNullOrEmpty(phoneNumber)) {
                    phoneNumber = phoneNumber.replaceAll(" ", "");
                    phoneContact.setPhoneNumber(Long.valueOf(phoneNumber));

                    if (!Util.isNullOrEmpty(firstName)) {
                        phoneContact.setName(firstName.replaceAll(" ", ""));
                    } else {
                        phoneContact.setName(empty);
                    }

                    if (!Util.isNullOrEmpty(lastName)) {
                        phoneContact.setLastName(lastName.replaceAll(" ", ""));
                    } else {
                        phoneContact.setLastName(empty);
                    }
                    if (!Util.isNullOrEmpty(version)) {
                        phoneContact.setVersion(version);
                    }
                    if (cachePhoneContacts != null && cachePhoneContacts.size() > 0) {
                        // if its not in PhoneContactCache and its a contact that added recently
                        if (map.get(Long.valueOf(phoneNumber)) != null) {
                            if (version != map.get(Long.valueOf(phoneNumber)).getVersion()) {
                                newPhoneContact.add(phoneContact);
                            }
                        } else {
                            newPhoneContact.add(phoneContact);
                        }
                    } else {
                        newPhoneContact.add(phoneContact);
                    }
                }
            }
            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, e.getCause().getMessage());
        }

        return newPhoneContact;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] strings = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, strings, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private String createFileMetadata(File file, String hashCode, long fileId, String mimeType, long fileSize, String filePath) {
        MetaDataFile metaDataFile = new MetaDataFile();
        FileMetaDataContent metaDataContent = new FileMetaDataContent();

        metaDataContent.setId(fileId);
        metaDataContent.setName(file.getName());
        metaDataContent.setMimeType(mimeType);
        metaDataContent.setSize(fileSize);
        if (hashCode != null) {
            metaDataContent.setHashCode(hashCode);
            metaDataContent.setLink(getFile(fileId, hashCode, true));
        } else {
            metaDataContent.setLink(filePath);
        }

        metaDataFile.setFile(metaDataContent);

        return gson.toJson(metaDataFile);
    }


    private String createImageMetadata(File fileUri, String hashCode, long imageId, int actualHeight, int actualWidth, String mimeType
            , long fileSize, String path, boolean isLocation, String center) {

        String originalName = fileUri.getName();
        FileImageMetaData fileMetaData = new FileImageMetaData();


        if (originalName.contains(".")) {
            String editedName = originalName.substring(0, originalName.lastIndexOf('.'));
            fileMetaData.setName(editedName);
        }
        fileMetaData.setHashCode(hashCode);
        fileMetaData.setId(imageId);
        fileMetaData.setOriginalName(originalName);
        fileMetaData.setActualHeight(actualHeight);
        fileMetaData.setActualWidth(actualWidth);
        fileMetaData.setMimeType(mimeType);
        fileMetaData.setSize(fileSize);
        if (!Util.isNullOrEmpty(hashCode)) {
            fileMetaData.setLink(getImage(imageId, hashCode, false));
        } else {
            fileMetaData.setLink(path);
        }
        if (isLocation) {
            MetadataLocationFile locationFile = new MetadataLocationFile();
            MapLocation mapLocation = new MapLocation();

            if (center.contains(",")) {
                String latitude = center.substring(0, center.lastIndexOf(','));
                String longitude = center.substring(center.lastIndexOf(',') + 1, center.length());
                mapLocation.setLatitude(Double.valueOf(latitude));
                mapLocation.setLongitude(Double.valueOf(longitude));
            }

            locationFile.setLocation(mapLocation);
            locationFile.setFile(fileMetaData);
            return gson.toJson(locationFile);

        } else {
            MetaDataImageFile metaData = new MetaDataImageFile();
            metaData.setFile(fileMetaData);
            return gson.toJson(metaData);
        }
    }


    /**
     * Add list of contacts with their mobile numbers and their cellphoneNumbers
     */
    private void addContacts(List<PhoneContact> phoneContacts, String uniqueId) {

        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> cellphoneNumbers = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> typeCodes = new ArrayList<>();

        for (PhoneContact contact : phoneContacts) {
            firstNames.add(contact.getName());
            lastNames.add(contact.getLastName());
            cellphoneNumbers.add(String.valueOf(contact.getPhoneNumber()));
        }

        ArrayList<String> emails = new ArrayList<>();
        for (int i = 0; i < cellphoneNumbers.size(); i++) {
            emails.add("");
            typeCodes.add(getTypeCode());
        }

        Observable<Response<Contacts>> addContactsObservable;
        if (getPlatformHost() != null) {
            if (!Util.isNullOrEmpty(getTypeCode())) {
                addContactsObservable = contactApi.addContacts(getToken(), TOKEN_ISSUER, firstNames, lastNames, emails, cellphoneNumbers
                        , cellphoneNumbers, typeCodes);
            } else {
                addContactsObservable = contactApi.addContacts(getToken(), TOKEN_ISSUER, firstNames, lastNames, emails, cellphoneNumbers
                        , cellphoneNumbers);
            }
            addContactsObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contactsResponse -> {
                boolean error = contactsResponse.body().getHasError();
                if (contactsResponse.isSuccessful()) {
                    if (error) {
                        getErrorOutPut(contactsResponse.body().getMessage(), contactsResponse.body().getErrorCode()
                                , uniqueId);
                    } else {
                        Contacts contacts = contactsResponse.body();
                        ChatResponse<Contacts> chatResponse = new ChatResponse<>();

                        chatResponse.setResult(contacts);
                        chatResponse.setUniqueId(uniqueId);

                        String contactsJson = gson.toJson(chatResponse);

                        if (cache) {
                            messageDatabaseHelper.saveContacts(chatResponse.getResult().getResult(), getExpireAmount());
                        }

                        listenerManager.callOnSyncContact(contactsJson, chatResponse);

                        phoneContactDbHelper.addPhoneContacts(phoneContacts);

                        showLog("SYNC_CONTACT_COMPLETED", contactsJson);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                }
            });
        }
    }

    @NonNull
    private ChatMessage getChatMessage(String contentThreadChat, String uniqueId, String typeCode) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(Constants.INVITATION);
        chatMessage.setToken(getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        if (typeCode != null && !typeCode.isEmpty()) {
            chatMessage.setTypeCode(typeCode);
        } else {
            chatMessage.setTypeCode(getTypeCode());
        }
        return chatMessage;
    }


    /**
     * Get the manager that manages registered listeners.
     */
    ChatListenerManager getListenerManager() {
        return listenerManager;
    }

    @NonNull
    private ChatMessage getChatMessage(MessageVO jsonMessage) {
        ChatMessage message = new ChatMessage();
        message.setType(Constants.DELIVERY);
        message.setContent(String.valueOf(jsonMessage.getId()));
        message.setTokenIssuer("1");
        message.setToken(getToken());
        message.setUniqueId(generateUniqueId());
        message.setTime(1000);
        return message;
    }

    private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {

        ResultUserInfo result = new ResultUserInfo();

        if (cache && permit) {
            messageDatabaseHelper.saveUserInfo(userInfo);
        }

        setUserId(userInfo.getId());
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());

        return gson.toJson(outPutUserInfo);
    }

    private String reformatMuteThread(ChatMessage chatMessage, ChatResponse<ResultMute> outPut) {
        ResultMute resultMute = new ResultMute();
        resultMute.setThreadId(Long.valueOf(chatMessage.getContent()));
        outPut.setResult(resultMute);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }

    private ChatResponse<ResultThread> reformatCreateThread(ChatMessage chatMessage) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultThread resultThread = new ResultThread();

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
        resultThread.setThread(thread);
        chatResponse.setResult(resultThread);

        resultThread.setThread(thread);
        return chatResponse;
    }

    /**
     * description    description of the message
     * threadId       id of the thread its wanted to send in
     * fileUri        uri of the file
     * mimeType       mime type of the file
     * systemMetaData metadata of the message
     * messageType    type of a message
     * messageId      id of a message
     * methodName     METHOD_REPLY_MSG or other
     * handler        description of the interface methods are :
     * bytesSent        - Bytes sent since the last time this callback was called.
     * totalBytesSent   - Total number of bytes sent so far.
     * totalBytesToSend - Total bytes to send.
     */
    private void uploadImageFileMessage(LFileUpload lFileUpload) {

        Activity activity = lFileUpload.getActivity();
        String description = lFileUpload.getDescription();
        Uri fileUri = lFileUpload.getFileUri();
        Integer messageType = lFileUpload.getMessageType();
        long threadId = lFileUpload.getThreadId();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetaData = lFileUpload.getSystemMetaData();
        long messageId = lFileUpload.getMessageId();
        String mimeType = lFileUpload.getMimeType();
        String center = lFileUpload.getCenter();
        String methodName = lFileUpload.getMethodName();

        systemMetaData = systemMetaData != null ? systemMetaData : "";
        description = description != null ? description : "";
        messageType = messageType != null ? messageType : 0;

        if (Permission.Check_READ_STORAGE(activity)) {
            String path = FilePick.getSmartFilePath(getContext(), fileUri);

            if (Util.isNullOrEmpty(path)) {
                path = "";
            }
            File file = new File(path);
            if (file.exists()) {
                long fileSize = file.length();
                UploadingQueueCache uploadingQueue = new UploadingQueueCache();
                uploadingQueue.setMessage(description);
                uploadingQueue.setMessageType(messageType);
                uploadingQueue.setSystemMetadata(systemMetaData);
                uploadingQueue.setUniqueId(uniqueId);
                uploadingQueue.setThreadId(threadId);
                uploadingQueue.setId(messageId);

                String metaData;
                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_LOCATION_MSG)) {

                    metaData = createImageMetadata(file, null, 0, 0, 0,
                            mimeType, fileSize, fileUri.toString(), true, center);
                } else {
                    metaData = createImageMetadata(file, null, 0, 0, 0,
                            mimeType, fileSize, fileUri.toString(), false, null);
                }

                uploadingQueue.setMetadata(metaData);

                if (cache) {
                    messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
                } else {
                    uploadingQList.put(uniqueId, uploadingQueue);
                }

                if (log)
                    Log.i(TAG, "Message with this" + "  uniqueId  " + uniqueId + "  has been added to Uploading Queue");
                lFileUpload.setFile(file);
                lFileUpload.setFileSize(fileSize);
                lFileUpload.setSystemMetaData(systemMetaData);
                lFileUpload.setDescription(description);
                lFileUpload.setMessageType(messageType);
                lFileUpload.setCenter(center);

                mainUploadImageFileMsg(lFileUpload);

            } else {
                if (log) Log.e(TAG, "File Is Not Exist");
            }
        } else {

            Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);

            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private void mainUploadImageFileMsg(LFileUpload fileUpload) {

        String description = fileUpload.getDescription();
        ProgressHandler.sendFileMessage handler = fileUpload.getHandler();
        Integer messageType = fileUpload.getMessageType();
        long threadId = fileUpload.getThreadId();
        String uniqueId = fileUpload.getUniqueId();
        String systemMetaData = fileUpload.getSystemMetaData();
        long messageId = fileUpload.getMessageId();
        String mimeType = fileUpload.getMimeType();
        String methodName = fileUpload.getMethodName();
        long fileSize = fileUpload.getFileSize();
        String center = fileUpload.getCenter();
        File file = fileUpload.getFile();

        if (chatReady) {

            if (fileServer != null) {

                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());


                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);


                showLog("UPLOAD_FILE_TO_SERVER","");

                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId,

                        new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {





                                if (handler != null) {
                                    handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                    if (log)
                                        Log.i(TAG, "uniqueId " + uniqueId + " bytesSent " + bytesSent);
                                }
                            }

                            @Override
                            public void onError() {
                                if (log)
                                    Log.i(TAG, "Error on upload");

                            }

                            @Override
                            public void onFinish() {

                                if (log)
                                    Log.i(TAG, "Finish upload");

                            }

                        });

                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Observable<Response<FileImageUpload>> uploadObservable =
                        fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);

                Subscription subscribe = uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadResponse -> {
                    if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                        boolean hasError = fileUploadResponse.body().isHasError();
                        if (hasError) {
                            String errorMessage = fileUploadResponse.body().getMessage();
                            int errorCode = fileUploadResponse.body().getErrorCode();
                            String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                            listenerManager.callOnLogEvent(jsonError);
                            ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }
                        } else {

                            ResultImageFile result = fileUploadResponse.body().getResult();
                            long imageId = result.getId();
                            String hashCode = result.getHashCode();

                            ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                            ResultImageFile resultImageFile = new ResultImageFile();
                            chatResponse.setUniqueId(uniqueId);
                            resultImageFile.setId(result.getId());
                            resultImageFile.setHashCode(result.getHashCode());
                            resultImageFile.setName(result.getName());
                            resultImageFile.setHeight(result.getHeight());
                            resultImageFile.setWidth(result.getWidth());
                            resultImageFile.setActualHeight(result.getActualHeight());
                            resultImageFile.setActualWidth(result.getActualWidth());

                            chatResponse.setResult(resultImageFile);

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
                            listenerManager.callOnLogEvent(imageJson);
                            String metaJson;
                            if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_LOCATION_MSG)) {
                                metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                        , result.getActualWidth(), mimeType, fileSize, null, true, center);

                            } else {
                                metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                        , result.getActualWidth(), mimeType, fileSize, null, false, null);
                            }

                            //TODO null object reference is here
                            //remove from Uploading Queue
                            if (cache) {
                                messageDatabaseHelper.deleteUploadingQueue(uniqueId);
                            } else {
                                uploadingQList.remove(uniqueId);
                            }

                            // send to handler
                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                mainReplyMessage(description, threadId, messageId, systemMetaData, messageType, metaJson, null);
                                if (log) Log.i(TAG, "SEND_REPLY_FILE_MESSAGE");
                                listenerManager.callOnLogEvent(metaJson);
                            } else {
                                sendTextMessageWithFile(description, threadId, metaJson, systemMetaData, uniqueId, typeCode, messageType);
                            }
                            listenerManager.callOnLogEvent(metaJson);
                        }
                    }
                }, throwable -> {
                    String jsonError = getErrorOutPut(throwable.getMessage()
                            , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                    ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage()
                            , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

                    if (log) Log.e(TAG, jsonError);
                    if (handler != null) {
                        handler.onError(jsonError, error);
                    }

                });

                /*
                 * Cancel Upload request
                 * */

                cancelUpload = new ProgressHandler.cancelUpload() {
                    @Override
                    public void cancelUpload(String uniqueCancel) {
                        if (uniqueCancel.equals(uniqueId) && !subscribe.isUnsubscribed()) {
                            subscribe.unsubscribe();
                            if (log) Log.e(TAG, "Uploaded Canceled");

                        }
                    }

                };


            } else {
                if (log) Log.e(TAG, "FileServer url Is null");
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private void uploadFileMessage(LFileUpload lFileUpload) {

        Activity activity = lFileUpload.getActivity();
        String description = lFileUpload.getDescription();
        Uri fileUri = lFileUpload.getFileUri();
        Integer messageType = lFileUpload.getMessageType();
        long threadId = lFileUpload.getThreadId();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetadata = lFileUpload.getSystemMetaData();
        String mimeType = lFileUpload.getMimeType();

        systemMetadata = systemMetadata != null ? systemMetadata : "";
        description = description != null ? description : "";
        messageType = messageType != null ? messageType : 0;
        try {

            if (Permission.Check_READ_STORAGE(activity)) {
                String path = FilePick.getSmartFilePath(context, fileUri);
                if (Util.isNullOrEmpty(path)) {
                    path = "";
                }
                File file = new File(path);
                long file_size;
                if (file.exists() || file.isFile()) {
                    file_size = file.length();

                    UploadingQueueCache uploadingQueue = new UploadingQueueCache();
                    uploadingQueue.setMessage(description);
                    uploadingQueue.setMessageType(messageType);
                    uploadingQueue.setSystemMetadata(systemMetadata);
                    uploadingQueue.setUniqueId(uniqueId);
                    uploadingQueue.setThreadId(threadId);

                    String metaData = createFileMetadata(file, null, 0, mimeType, file_size, fileUri.toString());
                    uploadingQueue.setMetadata(metaData);
                    if (cache) {
                        messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
                    } else {
                        uploadingQList.put(uniqueId, uploadingQueue);
                    }

                    showLog("Message with this" + "  uniqueId  " + uniqueId + "  has been added to Uploading Queue", null);

                    lFileUpload.setFileSize(file_size);

                    mainUploadFileMessage(lFileUpload);

                } else {
                    if (log) Log.e(TAG, "File Is Not Exist");
                }
            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                        , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                if (log) Log.e(TAG, jsonError);
            }
        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void mainUploadFileMessage(LFileUpload lFileUpload) {

        String description = lFileUpload.getDescription();
        Integer messageType = lFileUpload.getMessageType();
        long threadId = lFileUpload.getThreadId();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetadata = lFileUpload.getSystemMetaData();
        long messageId = lFileUpload.getMessageId();
        String mimeType = lFileUpload.getMimeType();
        File file = lFileUpload.getFile();
        ProgressHandler.sendFileMessage handler = lFileUpload.getHandler();
        long file_size = lFileUpload.getFileSize();
        String methodName = lFileUpload.getMethodName();

        if (chatReady) {
            if (getFileServer() != null) {
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                    @Override
                    public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                        handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                    }
                });

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
                File finalFile = file;
                Subscription subscription = uploadObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(fileUploadResponse -> {
                            if (fileUploadResponse.isSuccessful() && fileUploadResponse.body() != null) {
                                boolean error = fileUploadResponse.body().isHasError();
                                if (error) {
                                    String errorMessage = fileUploadResponse.body().getMessage();
                                    if (log) Log.e(TAG, errorMessage);
                                } else {
                                    ResultFile result = fileUploadResponse.body().getResult();
                                    if (result != null) {
                                        long fileId = result.getId();
                                        String hashCode = result.getHashCode();

                                        ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                        chatResponse.setResult(result);
                                        chatResponse.setUniqueId(uniqueId);
                                        result.setSize(file_size);
                                        String json = gson.toJson(chatResponse);

                                        listenerManager.callOnUploadFile(json, chatResponse);
                                        if (log) Log.i(TAG, "RECEIVE_UPLOAD_FILE");
                                        listenerManager.callOnLogEvent(json);

                                        String jsonMeta = createFileMetadata(finalFile, hashCode, fileId, mimeType, file_size, "");

                                        listenerManager.callOnLogEvent(jsonMeta);
                                        if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                            mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, null);
                                            if (log) Log.i(TAG, "SEND_REPLY_FILE_MESSAGE");

                                        } else {
                                            sendTextMessageWithFile(description, threadId, jsonMeta, systemMetadata, uniqueId, typeCode, messageType);
                                        }
                                    }

                                }
                            }
                        }, throwable -> {
                            if (log) Log.e(TAG, throwable.getMessage());
                        });

                /*
                 * Cancel Uploading progress
                 * */

                cancelUpload = new ProgressHandler.cancelUpload() {
                    @Override
                    public void cancelUpload(String uniqueCancel) {
                        if (uniqueCancel.equals(uniqueId) && !subscription.isUnsubscribed()) {
                            subscription.unsubscribe();
                            if (log) Log.e(TAG, "Uploaded Canceled");
                        }
                    }
                };

            } else {
                if (log) Log.e(TAG, "FileServer url Is null");
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private void deviceIdRequest(String ssoHost, String serverAddress, String appId, String severName) {

        showLog("GET_DEVICE_ID", "");
        currentDeviceExist = false;

        RetrofitHelperSsoHost retrofitHelperSsoHost = new RetrofitHelperSsoHost(ssoHost);
        SSOApi SSOApi = retrofitHelperSsoHost.getService(SSOApi.class);
        rx.Observable<Response<DeviceResult>> listObservable = SSOApi.getDeviceId("Bearer" + " " + getToken());
        listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceResults -> {
                    if (deviceResults.isSuccessful()) {
                        ArrayList<Device> devices = deviceResults.body().getDevices();
                        for (Device device : devices) {
                            if (device.isCurrent()) {
                                currentDeviceExist = true;
                                showLog("DEVICE_ID :" + device.getUid(), "");
                                async.connect(serverAddress, appId, severName, token, ssoHost, device.getUid());
                                break;
                            }
                        }
                        if (!currentDeviceExist) {
                            getErrorOutPut(ChatConstant.ERROR_CURRENT_DEVICE, ChatConstant.ERROR_CODE_CURRENT_DEVICE, null);
                        }
                    } else {
                        if (deviceResults.code() == 401) {
                            getErrorOutPut("unauthorized", deviceResults.code(), null);

                        } else {
                            getErrorOutPut(deviceResults.message(), deviceResults.code(), null);
                        }
                    }


                }, (Throwable throwable) -> {
                    if (log) Log.e(TAG, "Error on get devices" + throwable.getMessage());

                });
    }

    /**
     * Reformat the get thread response
     */
    private ChatResponse<ResultThreads> reformatGetThreadsResponse(ChatMessage chatMessage, Callback callback) {
        ChatResponse<ResultThreads> outPutThreads = new ChatResponse<>();
        ArrayList<Thread> threads = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Thread>>() {
        }.getType());

        if (cache) {
            ArrayList<ThreadVo> threadVos = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<ThreadVo>>() {
            }.getType());
            messageDatabaseHelper.saveThreads(threadVos);
        }

        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(threads);
        resultThreads.setContentCount(chatMessage.getContentCount());
        outPutThreads.setErrorCode(0);
        outPutThreads.setErrorMessage("");
        outPutThreads.setHasError(false);
        outPutThreads.setUniqueId(chatMessage.getUniqueId());

        if (callback != null) {

            if (threads.size() + callback.getOffset() < chatMessage.getContentCount()) {
                resultThreads.setHasNext(true);
            } else {
                resultThreads.setHasNext(false);
            }

            resultThreads.setNextOffset(callback.getOffset() + threads.size());
        }

        outPutThreads.setResult(resultThreads);
        return outPutThreads;
    }

    private String CreateAsyncContentForQueue(long threadId, long messageId, String uniqueId) {


        ChatMessageForward chatMessageForward = new ChatMessageForward();
        chatMessageForward.setSubjectId(threadId);

        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<Long> messageIds = new ArrayList<>();
        messageIds.add(messageId);
        uniqueIds.add(uniqueId);

        String jsonUniqueIds = Util.listToJson(uniqueIds, gson);

        chatMessageForward.setUniqueId(jsonUniqueIds);
        chatMessageForward.setContent(messageIds.toString());
        chatMessageForward.setToken(getToken());
        chatMessageForward.setTokenIssuer("1");
        chatMessageForward.setType(Constants.FORWARD_MESSAGE);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageForward);
        jsonObject.remove("contentCount");
        jsonObject.remove("systemMetadata");
        jsonObject.remove("metadata");
        jsonObject.remove("repliedTo");

        if (Util.isNullOrEmpty(getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", getTypeCode());
        }

        return jsonObject.toString();
    }

    @NonNull
    private String reformatError(boolean hasError, ChatMessage chatMessage, OutPutHistory outPut) {
        Error error = gson.fromJson(chatMessage.getContent(), Error.class);
        Log.e("RECEIVED_ERROR", chatMessage.getContent());
        Log.e("ErrorMessage", error.getMessage());
        Log.e("ErrorCode", String.valueOf(error.getCode()));
        outPut.setHasError(hasError);
        outPut.setErrorMessage(error.getMessage());
        outPut.setErrorCode(error.getCode());
        return gson.toJson(outPut);
    }

    /**
     * It Removes messages from wait queue after the check-in of their existence.
     */
    private void handleRemoveFromWaitQueue(ChatMessage chatMessage) {

        try {
            List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
            }.getType());
            if (cache) {
                for (MessageVO messageVO : messageVOS) {
                    messageDatabaseHelper.deleteWaitQueueMsgs(messageVO.getUniqueId());
                }
            } else {
                for (MessageVO messageVO : messageVOS) {
                    waitQList.remove(messageVO.getUniqueId());
                }
            }
        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
    }


    @NonNull
    private ChatResponse<ResultContact> reformatGetContactResponse(ChatMessage chatMessage, Callback callback) {
        ResultContact resultContact = new ResultContact();
        ChatResponse<ResultContact> outPutContact = new ChatResponse<>();
        ArrayList<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        if (log) Log.i(TAG, String.valueOf(cache));
        if (cache) {
            messageDatabaseHelper.saveContacts(contacts, getExpireAmount());
        }
        resultContact.setContacts(contacts);
        resultContact.setContentCount(chatMessage.getContentCount());

        if (contacts.size() + callback.getOffset() < chatMessage.getContentCount()) {
            resultContact.setHasNext(true);
        } else {
            resultContact.setHasNext(false);
        }
        resultContact.setNextOffset(callback.getOffset() + contacts.size());
        resultContact.setContentCount(chatMessage.getContentCount());

        outPutContact.setResult(resultContact);
        outPutContact.setErrorMessage("");
        outPutContact.setUniqueId(chatMessage.getUniqueId());
        return outPutContact;
    }

    private static synchronized String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public void setToken(String token) {
        this.token = token;

        //TODO unreachable lines bc async is not ready here =>
        if (asyncReady) {
            retryTokenRunOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (retrySetToken < 60) retrySetToken *= 4;
                    pingAfterSetToken();
                    retryTokenRunOnUIThread(this, retrySetToken * 1000);
                    showLog("Ping for check Token Authentication is retry after " + retrySetToken + " s", "");
                }
            }, retrySetToken * 1000);
        } else {

            Log.d(TAG, "can't set token bc async is not ready");
        }
    }

    private String getToken() {
        return token;
    }

    private long getUserId() {
        return userId;
    }

    private String getSsoHost() {
        return ssoHost;
    }

    private void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }

    private void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    private String getPlatformHost() {
        return platformHost;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    private void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    private String getFileServer() {
        return fileServer;
    }

    private int getSignalIntervalTime() {
        return signalIntervalTime;
    }

    public void setSignalIntervalTime(int signalIntervalTime) {
        this.signalIntervalTime = signalIntervalTime;
    }


    @Override
    public void onDisconnected(String textMessage) {
        super.onDisconnected(textMessage);
    }

    @Override
    public void onError(String textMessage) {
        super.onError(textMessage);
    }

    @Override
    public void handleCallbackError(Throwable cause) {
        super.handleCallbackError(cause);
    }

    public interface GetThreadHandler {
        void onGetThread();
    }

    public interface SendTextMessageHandler {
        void onSent(String uniqueId, long threadId);

        void onSentResult(String content);
    }
}