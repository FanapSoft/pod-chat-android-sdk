package com.fanap.podchat.chat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;


import com.fanap.podasync.Async;
import com.fanap.podasync.AsyncAdapter;
import com.fanap.podasync.model.Device;
import com.fanap.podasync.model.DeviceResult;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.GapMessageVO;
import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.chat.file_manager.download_file.PodDownloader;
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.chat.mention.Mention;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.pin.pin_message.PinMessage;
import com.fanap.podchat.chat.pin.pin_thread.PinThread;
import com.fanap.podchat.chat.thread.public_thread.PublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.profile.UserProfile;
import com.fanap.podchat.chat.user.user_roles.UserRoles;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.localmodel.LFileUpload;
import com.fanap.podchat.localmodel.SetRuleVO;
import com.fanap.podchat.mainmodel.AddParticipant;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.BlockedContact;
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
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
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
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread;
import com.fanap.podchat.model.ResultRemoveContact;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultSignalMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultThreadsSummary;
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
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestCreateThreadWithMessage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.requestobject.RequestSetAdmin;
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
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.requestobject.RequestRemoveContact;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSeenMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
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
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.FileUtils;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.NetworkPingSender;
import com.fanap.podchat.util.NetworkStateListener;
import com.fanap.podchat.util.NetworkStateReceiver;
import com.fanap.podchat.util.OnWorkDone;
import com.fanap.podchat.util.Permission;
import com.fanap.podchat.util.RequestMapSearch;
import com.fanap.podchat.util.TextMessageType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import rx.schedulers.Schedulers;

import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.*;


public class Chat extends AsyncAdapter {
    private static final String MTAG = "MTAG";
    public static final String PING = "PING";
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 1007;
    public static final int READ_CONTACTS_CODE = 1008;
    private static final int PING_INTERVAL = 20000;
    private static final int signalIntervalTime = 3000;

    private long freeSpaceThreshold = 100 * 1024 * 1024;

    private int signalMessageRanTime = 0;
    private static Async async;
    private String token;
    private String typeCode;
    private String platformHost;
    private String fileServer;
    private static volatile Chat instance;
    private static SecurePreferences mSecurePrefs;
    private static ChatListenerManager listenerManager;
    private long userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, ArrayList<Callback>> threadCallbacks;
    private static String TAG = "CHAT_SDK";
    private String chatState = "CLOSED";
    private boolean isWebSocketNull = true;


    private int getUserInfoRetryCount = 5;

    private int getUserInfoNumberOfTry = 0;

    private long maxReconnectStepTime = 64000;

    private long connectNumberOfRetry = 1000;


    private NetworkPingSender.NetworkStateConfig networkStateConfig;


//    private Map<Long, LinkedHashMap<String, Handler>> threadSignalsManager = new HashMap<>();

    HashMap<String, Long> downloadQList = new HashMap<>();
    HashMap<String, Call> downloadCallList = new HashMap<>();

    private HashMap<String, Handler> signalHandlerKeeper = new HashMap<>();
    private HashMap<String, RequestSignalMsg> requestSignalsKeeper = new HashMap<>();
    private HashMap<Long, ArrayList<String>> threadSignalsKeeper = new HashMap<>();
    private static JsonParser parser;
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
    private Context context;
    private boolean log;
    private int expireAmount;
    private static Gson gson;
    private boolean checkToken = false;
    private boolean userInfoResponse = false;
    private long ttl;
    private String ssoHost;
    private boolean isNetworkStateListenerEnable = true;


    private static NetworkStateReceiver networkStateReceiver;

    private static NetworkPingSender pinger;

    private static HandlerThread signalMessageHandlerThread = null;
//    private static HandlerThread signalMessageHandlerThread = null;

    @Inject
    public MessageDatabaseHelper messageDatabaseHelper;

    @Inject
    public PhoneContactDbHelper phoneContactDbHelper;

    private String socketAddress;
    private String appId;
    private String serverName;
    private boolean hasFreeSpace = true;


    public void setFreeSpaceThreshold(long freeSpaceThreshold) {
        this.freeSpaceThreshold = freeSpaceThreshold;
    }


    public void setMaxReconnectTime(long maxMilliseconds) {

        if (maxMilliseconds < 4000) {
            Log.e(TAG, "Minimum Reconnect Time is 4000 milliseconds");
            Log.i(TAG, "Max Reconnect Time is set to 4000");
            maxReconnectStepTime = 4000;
        } else
            maxReconnectStepTime = maxMilliseconds;
    }

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
            gson = new GsonBuilder().setPrettyPrinting().create();
            parser = new JsonParser();
            instance.setContext(context);
            listenerManager = new ChatListenerManager();
            threadCallbacks = new HashMap<>();
            mSecurePrefs = new SecurePreferences(context, "", "chat_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);


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

            messageCallbacks = new HashMap<>();
            handlerSend = new HashMap<>();
            gson = new GsonBuilder().create();


        }
        return instance;
    }


    public void setDownloadDirectory(File directory) {
        FileUtils.setDownloadDirectory(directory);
    }


    public void setNetworkListenerEnabling(boolean networkStateListenerEnable) {
        this.isNetworkStateListenerEnable = networkStateListenerEnable;
    }


    public void setGetUserInfoRetryCount(int getUserInfoRetryCount) {


        if (getUserInfoRetryCount < 3) {

            Log.e(TAG, "Retry count could not be below 3");

            return;
        }

        this.getUserInfoRetryCount = getUserInfoRetryCount;
    }

    private void enableNetworkStateListener() {

        if (networkStateReceiver == null) {

            networkStateReceiver = new NetworkStateReceiver();

            //it ping and check network availability

            pinger = new NetworkPingSender(new NetworkStateListener() {
                @Override
                public void networkAvailable() {

                    tryToConnectOrReconnect();

                }

                @Override
                public void networkUnavailable() {

                    closeSocketServer();

                }
            });

            networkStateReceiver.setHostName(networkStateConfig.getHostName());

            networkStateReceiver.setPort(networkStateConfig.getPort());

            networkStateReceiver.setTimeOut(networkStateConfig.getConnectTimeout());

            pinger.setConfig(networkStateConfig);

            pinger.setStateListener(this);

            //it listen to turning on and off wifi or mobile data and accessing to internet

            networkStateReceiver.addListener(new NetworkStateListener() {
                @Override
                public void networkAvailable() {

                    Log.i(TAG, "Network State Changed, Available");
                    tryToConnectOrReconnect();
                    pinger.startPing();

                }

                @Override
                public void networkUnavailable() {

                    Log.e(TAG, "Network State Changed, Unavailable");
                    closeSocketServer();

                }
            });


            try {
                context.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                if (log)
                    Log.e(TAG, "Network listener could not registered");
                try {
                    context.unregisterReceiver(networkStateReceiver);
                } catch (Exception ex) {
                    if (log)
                        Log.e(TAG, "Error on unregistering network listener");
                }
            }

        }
    }


    public void closeChat() {


        try {
            stopTyping();
            context.unregisterReceiver(networkStateReceiver);
            if (pinger != null) pinger.stopPing();

        } catch (Exception ex) {

            if (log) {

                Log.e(TAG, "Exception When Closing Chat. Unregistering Receiver failed. cause: " + ex.getMessage());
                Log.w(TAG, "Pinger has been stopped");

            }
        }
    }

    private synchronized void closeSocketServer() {


        try {

            if (TextUtils.equals(chatState, CLOSED) || TextUtils.equals(chatState, CLOSING))
                return;

            if (!async.isServerRegister())
                return;


            async.stopSocket();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

    }

    private void tryToConnectOrReconnect() {


        if (isAsyncReady()) {

            pingAfterSetToken();

        } else {
            scheduleForReconnect();
        }

    }

    private boolean isAsyncReady() {
        return TextUtils.equals(chatState, ASYNC_READY);
    }

    private void scheduleForReconnect() {

        resetReconnectRetryTime();

        Handler connectHandler = new Handler(Looper.getMainLooper());

        connectHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (log) Log.i(TAG, "The Connection Watcher is trying to reconnect...");


                if (connectNumberOfRetry < maxReconnectStepTime) {

                    connectNumberOfRetry = connectNumberOfRetry * 2;

                } else {

                    connectNumberOfRetry = maxReconnectStepTime;

                }

                if (log) Log.i(TAG, "Next retry is after " + connectNumberOfRetry);


                boolean shouldReconnect = TextUtils.equals(chatState, CLOSED)
                        || shouldReconnectOnOpenState();

                if (shouldReconnect) {

                    async.connect(socketAddress, appId, serverName, token, ssoHost, "");

                    connectHandler.postDelayed(this, connectNumberOfRetry);

                } else if (!chatReady) {

                    if (isAsyncReady()) {

                        pingAfterSetToken();

                    }

                    connectHandler.postDelayed(this, connectNumberOfRetry);
                }


            }
        }, connectNumberOfRetry);

    }

    private boolean shouldReconnectOnOpenState() {
        return TextUtils.equals(chatState, OPEN) && connectNumberOfRetry == maxReconnectStepTime;
    }


    private void setSignalHandlerThread() {

        signalMessageHandlerThread = new HandlerThread("signal handler thread");
        signalMessageHandlerThread.start();


    }


    private void stopSignalHandlerThread() {

        if (signalMessageHandlerThread != null) {
            // to avoid multiple start signal request
            signalMessageHandlerThread.quit();

            signalMessageRanTime = 0;
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

        chatState = currentChatState;

        switch (currentChatState) {
            case OPEN:
                retrySetToken = 1;
                resetReconnectRetryTime();
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
            case CLOSED:
                chatReady = false;
                retrySetToken = 1;
                getUserInfoNumberOfTry = 0;
                tokenHandler.removeCallbacksAndMessages(null);
                if (pinger != null) pinger.asyncIsClosedOrClosing();
                break;
        }
    }

    private void resetReconnectRetryTime() {
        connectNumberOfRetry = 1000;
    }

    /**
     * First we check the message messageType and then we set the
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
            case Constants.SEEN_MESSAGE_LIST:
            case Constants.DELIVERED_MESSAGE_LIST:
            case Constants.UNBLOCK:
            case Constants.GET_CONTACTS:
            case Constants.MUTE_THREAD:
            case Constants.GET_BLOCKED:
            case Constants.RENAME:
            case Constants.THREAD_PARTICIPANTS:
            case Constants.UN_MUTE_THREAD:
            case Constants.USER_INFO:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;


            case Constants.IS_NAME_AVAILABLE:
                handleIsNameAvailable(chatMessage);
                break;

            case Constants.JOIN_THREAD:
                handleOnJoinPublicThread(chatMessage);
                break;


            case Constants.UPDATE_CHAT_PROFILE: {

                handleOnChatProfileUpdated(chatMessage);

                break;
            }


            case Constants.GET_USER_ROLES: {


                handleOnGetUserRoles(chatMessage);

                break;
            }


            case Constants.UPDATE_LAST_SEEN: {


                handleUpdateLastSeen(chatMessage);

                break;
            }

            case Constants.PIN_MESSAGE: {

                handleOnPinMessage(chatMessage);

                break;
            }
            case Constants.UNPIN_MESSAGE: {

                handleOnUnPinMessage(chatMessage);

                break;
            }
            case Constants.EDIT_MESSAGE:

                handleEditMessage(chatMessage, messageUniqueId);

                break;
            case Constants.DELETE_MESSAGE:

                handleOutPutDeleteMsg(chatMessage);

                break;
            case Constants.UPDATE_THREAD_INFO:

                handleUpdateThreadInfo(chatMessage, messageUniqueId, callback);
                break;
            case Constants.BLOCK:
                handleOutPutBlock(chatMessage, messageUniqueId);
                break;
            case Constants.CHANGE_TYPE:
            case Constants.USER_STATUS:
            case Constants.RELATION_INFO:
            case Constants.GET_STATUS:
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
            case Constants.GET_HISTORY:
                /*Remove uniqueIds from waitQueue /***/
                if (callback == null) {
                    if (handlerSend.get(messageUniqueId) != null)
                        handleRemoveFromWaitQueue(chatMessage);
                    else
                        handleOnGetMentionList(chatMessage);

                    //todo add callbacks for pagination
                } else {
                    handleOutPutGetHistory(callback, chatMessage);
                }
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
                handleOutPutLeaveThread(null, chatMessage, messageUniqueId);
                break;
            case Constants.MESSAGE: {
                handleNewMessage(chatMessage);
                break;

            }
            case Constants.PING:
                handleOnPing(chatMessage);
                break;
            case Constants.REMOVE_PARTICIPANT:
                if (callback == null) {
                    handleOutPutRemoveParticipant(null, chatMessage, messageUniqueId);
                }
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            case Constants.THREAD_INFO_UPDATED:
                handleThreadInfoUpdated(chatMessage);
                break;
            case Constants.LAST_SEEN_UPDATED:
                handleLastSeenUpdated(chatMessage);
                break;
            case Constants.SPAM_PV_THREAD:
                handleOutPutSpamPVThread(chatMessage, messageUniqueId);
                break;
            case Constants.SET_ROLE_TO_USER:
                handleSetRole(chatMessage);
                break;
            case Constants.REMOVE_ROLE_FROM_USER:
                handleRemoveRole(chatMessage);
                break;
            case Constants.CLEAR_HISTORY:
                handleClearHistory(chatMessage);
                break;

            case Constants.PIN_THREAD:
                handleOnPinThread(chatMessage);
                break;
            case Constants.UNPIN_THREAD:
                handOnUnPinThread(chatMessage);
                break;

            case Constants.GET_NOT_SEEN_DURATION:
                handleGetNotSeenDuration(callback, chatMessage, messageUniqueId);
                break;

            case Constants.SYSTEM_MESSAGE:
                handleSystemMessage(callback, chatMessage, messageUniqueId);
                break;
        }
    }

    private void handleOnJoinPublicThread(ChatMessage chatMessage) {

        ChatResponse<ResultJoinPublicThread> response = PublicThread.handleJoinThread(chatMessage);

        listenerManager.callOnJoinPublicThread(response);

        showLog("ON JOIN PUBLIC THREAD",gson.toJson(chatMessage));


        if(cache){

            messageDatabaseHelper.saveNewThread(response.getResult().getThread());

        }


    }

    private void handleIsNameAvailable(ChatMessage chatMessage) {

        ChatResponse<ResultIsNameAvailable> response = PublicThread.handleIsNameAvailableResponse(chatMessage);

        listenerManager.callOnUniqueNameIsAvailable(response);

        showLog("UNIQUE NAME IS AVAILABLE", gson.toJson(chatMessage));

    }

    private void handleOnChatProfileUpdated(ChatMessage chatMessage) {

        ChatResponse<ResultUpdateProfile> response = UserProfile.handleOutputUpdateProfile(chatMessage);

        listenerManager.callOnChatProfileUpdated(response);

        showLog("CHAT PROFILE UPDATED", gson.toJson(chatMessage));

        if (cache) {
            messageDatabaseHelper.updateChatProfile(response.getResult());
        }


    }


    private void handleUpdateLastSeen(ChatMessage chatMessage) {


        ChatResponse<ResultNotSeenDuration> response = new ChatResponse<>();

        JsonObject jsonObject = Util.objectToJson(chatMessage.getContent(), parser);

        Map<String, Long> idLastSeen = new HashMap<>();

        for (String key :
                jsonObject.keySet()) {

            idLastSeen.put(key, jsonObject.get(key).getAsLong());

        }


        ResultNotSeenDuration resultNotSeenDuration = new ResultNotSeenDuration();

        resultNotSeenDuration.setIdNotSeenPair(idLastSeen);

        response.setResult(resultNotSeenDuration);


        showLog("LAST SEEN UPDATED", gson.toJson(chatMessage));


        listenerManager.callOnContactsLastSeenUpdated(response);

        listenerManager.callOnContactsLastSeenUpdated(chatMessage.getContent());


    }

    private void handleOnGetMentionList(ChatMessage chatMessage) {

        ChatResponse<ResultHistory> response = Mention.getMentionListResponse(chatMessage);
        listenerManager.callOnGetMentionList(response);
        showLog("RECEIVED MENTION LIST", gson.toJson(response));

        //todo cache ?

    }

    private void handleOnPinMessage(ChatMessage chatMessage) {


        ChatResponse<ResultPinMessage> response = PinMessage.handleOnMessagePinned(chatMessage);
        listenerManager.callOnPinMessage(response);
        showLog("MESSAGE_PINNED", gson.toJson(response));
        if (cache) {
            messageDatabaseHelper.savePinMessage(response, chatMessage.getSubjectId());
        }

    }

    private void handleOnUnPinMessage(ChatMessage chatMessage) {

        ChatResponse<ResultPinMessage> response = PinMessage.handleOnMessageUnPinned(chatMessage);
        listenerManager.callOnUnPinMessage(response);
        showLog("MESSAGE_UNPINNED", gson.toJson(chatMessage));
        if (cache) {
            messageDatabaseHelper.deletePinnedMessageByThreadId(chatMessage.getSubjectId());
//            messageDatabaseHelper.deletePinnedMessageById(result.getMessageId());
        }
    }

    private void handleOnGetUserRoles(ChatMessage chatMessage) {

        ChatResponse<ResultCurrentUserRoles> response = UserRoles.handleOnGetUserRoles(chatMessage);

        listenerManager.callOnGetUserRoles(response);

        showLog("RECEIVE CURRENT USER ROLES", gson.toJson(response));

    }

    private void handOnUnPinThread(ChatMessage chatMessage) {

        ChatResponse<ResultPinThread> response = PinThread.handleOnThreadUnPinned(chatMessage);

        listenerManager.callOnUnPinThread(response);

        showLog("RECEIVE_UNPIN_THREAD", gson.toJson(chatMessage));

        if (cache)
            messageDatabaseHelper.setThreadUnPinned(chatMessage);


    }

    private void handleOnPinThread(ChatMessage chatMessage) {

        ChatResponse<ResultPinThread> response = PinThread.handleOnThreadPinned(chatMessage);

        listenerManager.callOnPinThread(response);

        showLog("RECEIVE_PIN_THREAD", gson.toJson(chatMessage));

        if (cache)
            messageDatabaseHelper.setThreadPinned(chatMessage);


    }

    private void handleOutPutSpamPVThread(ChatMessage chatMessage, String messageUniqueId) {


        chatMessage.setUniqueId(messageUniqueId);

        showLog("RECEIVE_SPAM_PV_THREAD", gson.toJson(chatMessage));

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
        socketLog(log);
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
     * @param serverName    {**REQUIRED**}  Name of the server
     * @param appId         {**REQUIRED**}  Id of the app
     * @param token         {**REQUIRED**}  Token for Authentication
     * @param fileServer    {**REQUIRED**}  Address of the file server
     * @param ssoHost       {**REQUIRED**}  Address of the SSO Host
     */
    public void connect(String socketAddress, String appId, String serverName, String token,
                        String ssoHost, String platformHost, String fileServer, String typeCode) {
        try {
            if (platformHost.endsWith("/")) {

                resetAsync();
                setupContactApi(platformHost);
                setPlatformHost(platformHost);
                setToken(token);
                setSsoHost(ssoHost);
                setTypeCode(typeCode);
                setFileServer(fileServer);
                setSocketAddress(socketAddress);
                setAppId(appId);
                setServerName(serverName);

                connectToAsync(socketAddress, appId, serverName, token, ssoHost);

                if (isNetworkStateListenerEnable) enableNetworkStateListener();

                scheduleForReconnect();


            } else {
                getErrorOutPut("PlatformHost " + ChatConstant.ERROR_CHECK_URL
                        , ChatConstant.ERROR_CODE_CHECK_URL, null);

            }
        } catch (Throwable throwable) {
            if (log) {
                showLog("CONNECTION_ERROR", throwable.getMessage());
//                Log.e(TAG, throwable.getMessage());
//                listenerManager.callOnLogEvent(throwable.getMessage());
            }
        }
    }


    public void setNetworkStateConfig(NetworkPingSender.NetworkStateConfig networkStateConfig) {
        this.networkStateConfig = networkStateConfig;
    }

    private void connectToAsync(String socketAddress, String appId, String severName, String token, String ssoHost) {
        async.addListener(this);
        async.connect(socketAddress, appId, severName, token, ssoHost, "");
        isWebSocketNull = false;
    }

    private void setupContactApi(String platformHost) {
        RetrofitHelperPlatformHost retrofitHelperPlatformHost = new RetrofitHelperPlatformHost(platformHost, getContext());
        contactApi = retrofitHelperPlatformHost.getService(ContactApi.class);
    }

    private void setServerName(String serverName) {

        this.serverName = serverName;

    }

    private void setAppId(String appId) {

        this.appId = appId;
    }

    private void setSocketAddress(String socketAddress) {

        this.socketAddress = socketAddress;

    }

    private void resetAsync() {


        async.clearListeners();

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
                sendAsyncMessage(asyncContentWaitQueue, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE");
                stopTyping();
            } else {
                onChatNotReady(uniqueId);
            }
        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
        return uniqueId;
    }


    public String pinThread(RequestPinThread request) {


        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message = PinThread.pinThread(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_PIN_THREAD");
        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }


    public String unPinThread(RequestPinThread request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {
            String message = PinThread.unPinThread(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_UNPIN_THREAD");
        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;

    }


    /**
     * @param request You can add or remove someone as admin to some thread set
     *                and roles to them
     */

    public String addAdmin(RequestSetAdmin request) {


        SetRuleVO setRuleVO = new SetRuleVO();
        setRuleVO.setRoles(request.getRoles());
        setRuleVO.setThreadId(request.getThreadId());
        setRuleVO.setTypeCode(getTypeCode());


        return setRole(setRuleVO);
    }

    public String addAuditor(RequestSetAuditor request) {

        SetRuleVO setRuleVO = new SetRuleVO();
        setRuleVO.setRoles(request.getRoles());
        setRuleVO.setThreadId(request.getThreadId());
        setRuleVO.setTypeCode(getTypeCode());


        return setRole(setRuleVO);

    }

    public String removeAuditor(RequestSetAuditor request) {


        SetRuleVO setRuleVO = new SetRuleVO();
        setRuleVO.setRoles(request.getRoles());
        setRuleVO.setThreadId(request.getThreadId());
        setRuleVO.setTypeCode(getTypeCode());


        return removeRole(setRuleVO);
    }

    public String removeAdmin(RequestSetAdmin request) {


        SetRuleVO setRuleVO = new SetRuleVO();
        setRuleVO.setRoles(request.getRoles());
        setRuleVO.setThreadId(request.getThreadId());
        setRuleVO.setTypeCode(getTypeCode());


        return removeRole(setRuleVO);
    }

    private String setRole(SetRuleVO request) {

        long threadId = request.getThreadId();
        ArrayList<RequestRole> roles = request.getRoles();
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(Constants.SET_ROLE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTypeCode(getTypeCode());

            setCallBacks(null, null, null, true, Constants.SET_ROLE_TO_USER, null, uniqueId);
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SET_ROLE_TO_USER");
        }
        return uniqueId;
    }


    public String updateChatProfile(RequestUpdateProfile request) {


        String uniqueId = generateUniqueId();


        if (chatReady) {


            String message = UserProfile.setProfile(request, uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "UPDATE_CHAT_PROFILE");

        } else {

            onChatNotReady(uniqueId);
        }


        return uniqueId;


    }


    /**
     * @param request request to get mentioned message of user
     *                -unreadMentioned
     *                -allMentioned
     */

    public String getMentionList(RequestGetMentionList request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = Mention.getMentionList(request, uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "GET_MENTION_LIST");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }


    /**
     * @param request request that contains name to check if is available to create a public thread or channel
     * @return
     */

    public String checkIsNameAvailable(RequestCheckIsNameAvailable request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = PublicThread.isNameAvailable(request, uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "CHECK IS NAME AVAILABLE");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }


    /**
     * @param request request for pin message
     *                messageId: id of message that you want to pin
     *                notifyAll: if you want notify all thread member about this message
     */

    public String pinMessage(RequestPinMessage request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = PinMessage.pinMessage(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "PIN_MESSAGE");

        } else {

            onChatNotReady(uniqueId);
        }

        return uniqueId;

    }


    /**
     * @param request request for unpin message
     *                messageId: id of message that you want to unpin
     */


    public String unPinMessage(RequestPinMessage request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = PinMessage.unPinMessage(request, uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "UNPIN_MESSAGE");

        } else {

            onChatNotReady(uniqueId);
        }

        return uniqueId;

    }


    public String getCurrentUserRoles(RequestGetUserRoles request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = UserRoles.getUserRoles(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "GET_USER_ROLES");

        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    private void onChatNotReady(String uniqueId) {
        String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        if (log) Log.e(TAG, jsonError);
    }


    private String removeRole(SetRuleVO request) {


        long threadId = request.getThreadId();
        ArrayList<RequestRole> roles = request.getRoles();
        String uniqueId = generateUniqueId();

        if (chatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(getToken());
            chatMessage.setType(Constants.REMOVE_ROLE_FROM_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTypeCode(getTypeCode());


            setCallBacks(null, null, null, true, Constants.REMOVE_ROLE_FROM_USER, null, uniqueId);
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "REMOVE_ROLE_FROM_USER");
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
     *                 {@link #getPhoneContact(Context, OnContactLoaded)}
     */
    public String syncContact(Activity activity) {


        Log.i(TAG, ">>> Start Syncing... " + new Date());

        String uniqueId = generateUniqueId();

        if (Permission.Check_READ_CONTACTS(activity)) {
            if (chatReady) {

                getPhoneContact(getContext(), phoneContacts -> {


                    if (phoneContacts.size() > 0) {

                        Log.i(TAG, ">>> Adding " + phoneContacts.size() + " Contacts to Server at " + new Date());
                        Log.d(TAG, "***::: Contacts: " + phoneContacts.toString());

                        addContacts(phoneContacts, uniqueId);
                    } else {

                        Log.i(TAG, ">>> No New Contact Found. Everything synced " + new Date());

                        ChatResponse<Contacts> chatResponse = new ChatResponse<>();
                        listenerManager.callOnSyncContact("", chatResponse);

                        if (log)
                            Log.i(TAG, "SYNC_CONTACT_COMPLETED");
                    }


                });

            } else {
                onChatNotReady(uniqueId);
            }
        } else {

            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_CONTACT_PERMISSION, ChatConstant.ERROR_CODE_READ_CONTACT_PERMISSION
                    , uniqueId);

            Permission.Request_READ_CONTACTS(activity, READ_CONTACTS_CODE);

            if (log) Log.e(TAG, jsonError);
        }
        return uniqueId;
    }

    /**
     * This method first check the messageType of the file and then choose the right
     * server and send that
     *
     * @param description    Its the description that you want to send with file in the thread
     * @param fileUri        Uri of the file that you want to send to thread
     * @param threadId       Id of the thread that you want to send file
     * @param systemMetaData [optional]
     * @param handler        it is for send file message with progress
     */
    @Deprecated
    public String sendFileMessage(Activity activity, String description, long threadId
            , Uri fileUri, String systemMetaData, Integer messageType, ProgressHandler.sendFileMessage handler) {
        String uniqueId;

        uniqueId = generateUniqueId();

        if (needReadStoragePermission(activity)) return uniqueId;

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
                lFileUpload.setFile(file);

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
//                listenerManager.callOnLogEvent(jsonError);
                if (handler != null) {
                    handler.onError(jsonError, error);
                }
            }
        } catch (Exception e) {

            getErrorOutPut(e.getMessage()
                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
            if (log) Log.e(TAG, e.getMessage());
            return uniqueId;
        }
        return uniqueId;
    }

    private boolean needReadStoragePermission(Activity activity) {

        if (!Permission.Check_READ_STORAGE(activity)) {

            Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);

            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);


            return true;
        }
        return false;
    }


    private boolean hasReadAndWriteStoragePermission() {


        return Permission.Check_READ_STORAGE(getContext()) &&
                Permission.Check_Write_STORAGE(getContext());
    }

    /**
     * This method first check the messageType of the file and then choose the right
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
    public String uploadImageProgress(Activity activity, Uri fileUri, ProgressHandler.onProgress handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (chatReady) {
            if (fileServer != null) {
                if (Permission.Check_READ_STORAGE(activity)) {
                    String mimeType = getMimType(fileUri);
                    RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                    FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

                    String path = FilePick.getSmartFilePath(getContext(), fileUri);

                    File file = new File(path);

                    if (!Util.isNullOrEmpty(mimeType) && FileUtils.isImage(mimeType)) {


                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                                handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                handler.onProgressUpdate(bytesSent);
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFinish() {

                            }
                        });


                        JsonObject jLog = new JsonObject();


                        jLog.addProperty("name", file.getName());
                        jLog.addProperty("token", getToken());
                        jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
                        jLog.addProperty("uniqueId", uniqueId);

                        showLog("UPLOADING_IMAGE", getJsonForLog(jLog));


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

                                    resultImageFile.setUrl(getImage(resultImageFile.getId(), resultImageFile.getHashCode(), true));


                                    String imageJson = gson.toJson(chatResponse);

//                                    if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
//                                    listenerManager.callOnLogEvent(imageJson);

                                    showLog("RECEIVE_UPLOAD_IMAGE", imageJson);

                                    listenerManager.callOnUploadImageFile(imageJson, chatResponse);
                                    handler.onFinish(imageJson, chatResponse);
                                }
                            }
                        }, throwable -> {
                            getErrorOutPut(throwable.getMessage(), 0, uniqueId);

                            ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, null);
                            String jsonError = gson.toJson(error);
                            handler.onError(jsonError, error);
                            if (log) Log.e(TAG, throwable.getMessage());
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        handler.onError(jsonError, error);
                        if (log) Log.e(TAG, jsonError);
                        return uniqueId;
                    }
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                    handler.onError(jsonError, error);
                    Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
                    if (log) Log.e(TAG, jsonError);
                    return uniqueId;
                }
            } else {
                String jsonError = getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, null);
                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                handler.onError(jsonError, error);
                if (log) Log.e(TAG, "FileServer url Is null");
                return uniqueId;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            listenerManager.callOnLogEvent(jsonError);
            return uniqueId;
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


                                JsonObject jLog = new JsonObject();
                                jLog.addProperty("name", file.getName());
                                jLog.addProperty("token", getToken());
                                jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
                                jLog.addProperty("uniqueId", uniqueId);
                                showLog("UPLOADING_IMAGE", getJsonForLog(jLog));


                                Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);

                                uploadObservable
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fileUploadResponse -> {
                                            if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                                                boolean hasError = fileUploadResponse.body().isHasError();
                                                if (hasError) {
                                                    String errorMessage = fileUploadResponse.body().getMessage();
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

                                                    resultImageFile.setUrl(getImage(resultImageFile.getId(), resultImageFile.getHashCode(), true));


                                                    String imageJson = gson.toJson(chatResponse);

                                                    listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                                                    showLog("RECEIVE_UPLOAD_IMAGE", imageJson);
                                                    //                                                if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
                                                    //                                                listenerManager.callOnLogEvent(imageJson);
                                                }
                                            }
                                        }, throwable -> {
                                            String jsonError = getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                                            if (log) Log.e(TAG, jsonError);
                                        });
                            } else {
                                String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, uniqueId);
                                if (log) Log.e(TAG, jsonError);
//                                uniqueId = null;
                            }
                        }
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                        if (log) Log.e(TAG, jsonError);
//                        uniqueId = null;
                    }
                } else {
                    getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                    if (log) Log.e(TAG, "FileServer url Is null");
//                    uniqueId = null;
                }
            } catch (Exception e) {
                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                if (log) Log.e(TAG, e.getCause().getMessage());
//                uniqueId = null;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
        return uploadImageProgress(activity, fileUri, handler);
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


                            JsonObject jLog = new JsonObject();

                            jLog.addProperty("file", file.getName());
                            jLog.addProperty("file_size", fileSize);
                            jLog.addProperty("uniqueId", uniqueId);
                            showLog("UPLOADING_FILE", getJsonForLog(jLog));


                            RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                            FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


                            Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);


                            uploadObservable.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fileUploadResponse -> {
                                        if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                                            boolean hasError = fileUploadResponse.body().isHasError();
                                            if (hasError) {
                                                String errorMessage = fileUploadResponse.body().getMessage();
                                                int errorCode = fileUploadResponse.body().getErrorCode();
                                                String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                                if (log) Log.e(TAG, jsonError);
                                            } else {
                                                ResultFile result = fileUploadResponse.body().getResult();
                                                result.setUrl(getFile(result.getId(), result.getHashCode(), true));
                                                result.setSize(fileSize);

                                                ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                                chatResponse.setUniqueId(uniqueId);
                                                chatResponse.setResult(result);


                                                String json = gson.toJson(chatResponse);
                                                listenerManager.callOnUploadFile(json, chatResponse);
                                                showLog("RECEIVE_UPLOAD_FILE", json);
//                                        if (log) Log.i(TAG, "RECEIVE_UPLOAD_FILE");
//                                        listenerManager.callOnLogEvent(json);
                                            }
                                        }
                                    }, throwable -> {
                                        String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                                        if (log) Log.e(TAG, jsonError);
                                    });

//                            uploadObservable.unsubscribeOn(Schedulers.io());

                        } else {
                            getErrorOutPut("File is not Exist", ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (log) Log.e(TAG, "File is not Exist");
                            return uniqueId;
                        }
                    } else {
                        getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);

                        if (log) Log.e(TAG, "File Server url Is null");
                        return uniqueId;
                    }
                } else {
                    Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    if (log) Log.e(TAG, jsonError);
                    return uniqueId;
                }
            } catch (Exception e) {
                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                if (log) Log.e(TAG, e.getMessage());
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

    public String uploadFileProgress(RequestUploadFile requestUploadFile, ProgressHandler.onProgressFile handler) {


        return uploadFileProgress(requestUploadFile.getActivity(), requestUploadFile.getFileUri(), handler);


    }


    @Deprecated
    public String uploadFileProgress(Activity activity, Uri uri, ProgressHandler.onProgressFile handler) {
        String uniqueId = generateUniqueId();
        try {
            if (chatReady) {
                if (Permission.Check_READ_STORAGE(activity)) {
                    if (getFileServer() != null) {
                        String mimeType = getMimType(uri);
//                    File file = new File(getRealPathFromURI(context, uri));
                        String path = FilePick.getSmartFilePath(getContext(), uri);
                        File file = new File(path);


                        JsonObject jLog = new JsonObject();

                        jLog.addProperty("name", file.getName());
                        jLog.addProperty("token", getToken());
                        jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
                        jLog.addProperty("uniqueId", uniqueId);

                        showLog("UPLOADING_FILE", getJsonForLog(jLog));

                        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                                handler.onProgress(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                handler.onProgressUpdate(bytesSent);
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
                        uploadObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(fileUploadResponse -> {
                                    if (fileUploadResponse.isSuccessful()) {
                                        boolean hasError = fileUploadResponse.body().isHasError();
                                        if (hasError) {
                                            String errorMessage = fileUploadResponse.body().getMessage();
                                            int errorCode = fileUploadResponse.body().getErrorCode();
                                            String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                            ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
                                            handler.onError(jsonError, error);
                                        } else {

                                            FileUpload result = fileUploadResponse.body();

                                            ChatResponse<ResultFile> chatResponse = new ChatResponse<>();

                                            ResultFile resultFile = result.getResult();
                                            resultFile.setUrl(getFile(resultFile.getId(), resultFile.getHashCode(), true));


                                            chatResponse.setResult(resultFile);

                                            chatResponse.setUniqueId(uniqueId);

                                            String json = gson.toJson(chatResponse);

                                            handler.onFinish(json, result);

                                            showLog("FINISH_UPLOAD_FILE", json);

                                            listenerManager.callOnUploadFile(json, chatResponse);

                                        }
                                    }
                                }, throwable -> {
                                    ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, uniqueId);
                                    String json = gson.toJson(error);
                                    getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE + " " + throwable.getMessage(), ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                    handler.onError(json, error);

                                });
                    } else {

                        if (log) Log.e(TAG, "FileServer url Is null");

                        getErrorOutPut("File Server url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);

                    }

                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    handler.onError(jsonError, error);
                    Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

        }
        return uniqueId;
    }


    //new upload file function
    private void uploadFile(RequestUploadFile requestUploadFile, String uniqueId, @Nullable ProgressHandler.onProgressFile handler, OnWorkDone listener) {


        Activity activity = requestUploadFile.getActivity();

        Uri uri = requestUploadFile.getFileUri();

        try {

            if (chatReady) {


                if (!needReadStoragePermission(activity)) {
                    if (getFileServer() != null) {
                        String mimeType = getMimType(uri);
//                    File file = new File(getRealPathFromURI(context, uri));
                        String path = FilePick.getSmartFilePath(getContext(), uri);
                        File file = new File(path);

                        if (!file.exists()) {

                            getErrorOutPut(ChatConstant.ERROR_INVALID_FILE_URI,
                                    ChatConstant.ERROR_CODE_INVALID_FILE_URI,
                                    uniqueId);

                            return;
                        }

                        JsonObject jLog = new JsonObject();

                        jLog.addProperty("name", file.getName());
                        jLog.addProperty("token", getToken());
                        jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
                        jLog.addProperty("uniqueId", uniqueId);

                        showLog("UPLOADING_FILE", getJsonForLog(jLog));

                        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                                if (handler != null) {
                                    handler.onProgress(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                    handler.onProgressUpdate(bytesSent);
                                }
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
                        uploadObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(fileUploadResponse -> {

                                    if (fileUploadResponse.isSuccessful()) {
                                        boolean hasError = false;
                                        if (fileUploadResponse.body() != null) {
                                            hasError = fileUploadResponse.body().isHasError();
                                        }
                                        if (hasError) {
                                            String errorMessage = fileUploadResponse.body().getMessage();
                                            int errorCode = fileUploadResponse.body().getErrorCode();
                                            String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                            ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
                                            if (handler != null) {
                                                handler.onError(jsonError, error);
                                            }
                                        } else {

                                            FileUpload result = fileUploadResponse.body();

                                            ChatResponse<ResultFile> chatResponse = new ChatResponse<>();

                                            ResultFile resultFile = fileUploadResponse.body().getResult();
                                            resultFile.setUrl(getFile(resultFile.getId(), resultFile.getHashCode(), true));


                                            showLog("FINISH_UPLOAD_FILE", gson.toJson(resultFile));

                                            MetaDataFile metaDataFile = new MetaDataFile();
                                            FileMetaDataContent metaDataContent = new FileMetaDataContent();
                                            metaDataContent.setHashCode(resultFile.getHashCode());
                                            metaDataContent.setId(resultFile.getId());
                                            metaDataContent.setName(resultFile.getName());
//                                            metaDataContent.setSize(file.length());
//                                            metaDataContent.setLink(getFileServer());
                                            metaDataFile.setFile(metaDataContent);


                                            chatResponse.setResult(resultFile);

                                            chatResponse.setUniqueId(uniqueId);


                                            JsonObject metadata = (JsonObject) gson.toJsonTree(metaDataFile);

                                            metadata.addProperty("name", result.getResult().getName());
                                            metadata.addProperty("id", result.getResult().getId());


                                            if (handler != null) {
                                                handler.onFinish(gson.toJson(chatResponse), result);
                                            }

                                            listener.onWorkDone(metadata.toString());

                                        }
                                    }
                                }, throwable -> {
                                    ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, uniqueId);
                                    String json = gson.toJson(error);
                                    getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE + " " + throwable.getMessage(), ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                    if (handler != null) {
                                        handler.onError(json, error);
                                    }

                                });
                    } else {

                        if (log) Log.e(TAG, "FileServer url Is null");

                        getErrorOutPut("File Server url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);

                    }

                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    if (handler != null) {
                        handler.onError(jsonError, error);
                    }
                    Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

        }
    }


    //new upload image function
    private void uploadImage(RequestUploadImage request, String uniqueId, @Nullable ProgressHandler.onProgressFile handler, OnWorkDone listener) {


        Uri fileUri = request.getFileUri();

        Activity activity = request.getActivity();

        int xC = request.getxC();
        int yC = request.getyC();
        int hC = request.gethC();
        int wC = request.getwC();

        if (chatReady) {
            try {

                if (fileServer != null && fileUri != null) {

                    if (!needReadStoragePermission(activity)) {
                        String path = FilePick.getSmartFilePath(getContext(), fileUri);
                        if (Util.isNullOrEmpty(path)) {
                            path = "";
                        }
                        File file = new File(path);

                        if (file.exists()) {

                            String mimeType = handleMimType(fileUri, file);

                            if (FileUtils.isImage(mimeType)) {


                                //TODO handle crop image with hC and wC here
//                           if (!Util.isNullOrEmpty(hC) && !Util.isNullOrEmpty(wC)) {
////                            BufferedImage originalImage = ImageIO.read(file);
////
////                            BufferedImage subImage = originalImage.getSubimage(xC, yC, wC, hC);
////
////                            File outputFile = File.createTempFile("test", null);
////
////                            ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);
////
////                            fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);
////
////                            requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
////                        } else {
////
////                            fileApi = RetrofitHelperFileServer.getInstance(getFileServer()).create(FileApi.class);
////
////                            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
////                        }

                                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());


                                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);


                                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                                    @Override
                                    public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

                                        if (handler != null) {
                                            handler.onProgress(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                            handler.onProgressUpdate(bytesSent);
                                        }
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


                                JsonObject jLog = new JsonObject();
                                jLog.addProperty("name", file.getName());
                                jLog.addProperty("token", getToken());
                                jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
                                jLog.addProperty("uniqueId", uniqueId);
                                showLog("UPLOADING_IMAGE", getJsonForLog(jLog));


                                Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);

                                uploadObservable.subscribeOn(Schedulers.io())

                                        .observeOn(AndroidSchedulers.mainThread())

                                        .subscribe(fileUploadResponse -> {


                                            if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
                                                boolean hasError = fileUploadResponse.body().isHasError();
                                                if (hasError) {
                                                    String errorMessage = fileUploadResponse.body().getMessage();
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

                                                    resultImageFile.setUrl(getImage(resultImageFile.getId(), resultImageFile.getHashCode(), true));

                                                    chatResponse.setResult(resultImageFile);


                                                    JsonObject metadata = (JsonObject) gson.toJsonTree(chatResponse);

                                                    metadata.addProperty("name", resultImageFile.getName());
                                                    metadata.addProperty("id", resultImageFile.getId());


                                                    showLog("RECEIVE_UPLOAD_IMAGE", metadata.toString());

                                                    listener.onWorkDone(metadata.toString());

                                                    listenerManager.callOnLogEvent(metadata.toString());

                                                    if (handler != null)
                                                        handler.onImageFinish(uniqueId, chatResponse);
                                                }
                                            }
                                        }, throwable -> {
                                            String jsonError = getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                                            if (log) Log.e(TAG, jsonError);
                                            if (handler != null) handler.onError(uniqueId, null);

                                        });
                            } else {
                                String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, uniqueId);
                                if (log) Log.e(TAG, jsonError);
                                if (handler != null) handler.onError(uniqueId, null);

                            }
                        }
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
                        if (log) Log.e(TAG, jsonError);
                        if (handler != null) handler.onError(uniqueId, null);

                    }
                } else {
                    getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                    if (log) Log.e(TAG, "FileServer url Is null");
                    if (handler != null) handler.onError(uniqueId, null);

                }
            } catch (Exception e) {
                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                if (log) Log.e(TAG, e.getCause().getMessage());
                if (handler != null) handler.onError(uniqueId, null);

            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            if (handler != null) handler.onError(uniqueId, null);
        }

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

            JsonObject jsonObject = null;
            if (sendingQueueCache != null) {
                jsonObject = (new JsonParser()).parse(sendingQueueCache.getAsyncContent()).getAsJsonObject();
            }
            if (jsonObject != null) {
                jsonObject.remove("token");
                jsonObject.addProperty("token", getToken());
                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
            }

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
            removeFromUploadQueue(uniqueId);
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

                removeFromUploadQueue(uniqueId);
                uploadImageFileMessage(lFileUpload);
            } else {
                removeFromUploadQueue(uniqueId);
                uploadFileMessage(lFileUpload);
            }
        }
    }

    /**
     * This method generate url that you can use to get your file
     */
    private String getFile(long fileId, String hashCode, boolean downloadable) {
        return getFileServer() + "nzh/file/" + "?fileId=" + fileId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
    }

    /**
     * This method generate url that you can use to get your file
     */
    private String getFile(RequestGetFile requestGetFile) {
        return getFileServer() + "nzh/file/"
                + "?fileId=" + requestGetFile.getFileId()
                + "&downloadable=" + requestGetFile.isDownloadable()
                + "&hashCode=" + requestGetFile.getHashCode();
    }

    /**
     * This method generate url based on your input params that you can use to get your image
     */
    private String getImage(long imageId, String hashCode, boolean downloadable) {
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
    private String getImage(RequestGetImage requestGetImage) {
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

    private long checkFreeSpace() {

        long bytesAvailable = FileUtils.getFreeSpace();

        hasFreeSpace = bytesAvailable >= freeSpaceThreshold;

        if (!hasFreeSpace) {

            listenerManager.callOnLowFreeSpace(bytesAvailable);

            getErrorOutPut(ChatConstant.ERROR_LOW_FREE_SPACE, ChatConstant.ERROR_CODE_LOW_FREE_SPACE, "");


        }


        return bytesAvailable;
    }


    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public String getFile(RequestGetFile request, ProgressHandler.IDownloadFile progressHandler) {

        String uniqueId = generateUniqueId();

        String url = getFile(request.getFileId(), request.getHashCode(), true);

        if (!isExternalStorageWritable() || !hasReadAndWriteStoragePermission()) {

            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);

            progressHandler.onError(uniqueId, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, url);

            return uniqueId;
        }


        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);


        File destinationFolder;

        if (cache) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getOrCreateDirectory(FileUtils.FILES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getPublicFilesDirectory();

        }


        String fileName = "file_" + request.getFileId() + "_" + request.getHashCode();


        if (destinationFolder == null) {

            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);

            return uniqueId;
        }


        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile()) {

            //file exists
            ChatResponse<ResultDownloadFile> response = PodDownloader.generateDownloadResult(request.getHashCode(), request.getFileId(), cachedFile);

            progressHandler.onFileReady(response);

            return uniqueId;

        }


        //only url should return in callback
        if (!hasFreeSpace) {

            progressHandler.onLowFreeSpace(uniqueId, url);

            return uniqueId;
        }


        if (chatReady) {

            Call call = PodDownloader.download(
                    new ProgressHandler.IDownloadFile() {
                        @Override
                        public void onError(String mUniqueId, String error, String mUrl) {
                            progressHandler.onError(uniqueId, error, url);
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, long bytesDownloaded, long totalBytesToDownload) {

                            progressHandler.onProgressUpdate(uniqueId, bytesDownloaded, totalBytesToDownload);


                            if (totalBytesToDownload > checkFreeSpace()) {

                                progressHandler.onLowFreeSpace(uniqueId, url);

                            }
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, int progress) {

                            progressHandler.onProgressUpdate(uniqueId, progress);

                        }

                        @Override
                        public void onFileReady(ChatResponse<ResultDownloadFile> response) {
                            progressHandler.onFileReady(response);
                        }
                    },
                    getFileServer(),
                    url,
                    fileName,
                    destinationFolder,
                    downloaderErrorInterface,
                    request.getHashCode(),
                    request.getFileId());

            downloadCallList.put(uniqueId, call);

        } else onChatNotReady(uniqueId);

        return uniqueId;
    }

    public String getImage(RequestGetImage request, ProgressHandler.IDownloadFile progressHandler) {

        String uniqueId = generateUniqueId();

        String url = getImage(request.getImageId(), request.getHashCode(), true);

        if (!isExternalStorageWritable() || !hasReadAndWriteStoragePermission()) {

            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);

            progressHandler.onError(uniqueId, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, url);

            return uniqueId;
        }


        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);


        File destinationFolder;

        if (cache) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getOrCreateDirectory(FileUtils.PICTURES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getPublicFilesDirectory();

        }


        String fileName = "image_" + request.getImageId() + "_" + request.getHashCode();


        if (destinationFolder == null) {

            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);

            return uniqueId;
        }


        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile()) {

            //file exists
            ChatResponse<ResultDownloadFile> response = PodDownloader.generateDownloadResult(request.getHashCode(), request.getImageId(), cachedFile);

            progressHandler.onFileReady(response);

            return uniqueId;

        }


        //only url should return in callback
        if (!hasFreeSpace) {

            progressHandler.onLowFreeSpace(uniqueId, url);

            return uniqueId;
        }


        if (chatReady) {

            Call call = PodDownloader.download(
                    new ProgressHandler.IDownloadFile() {
                        @Override
                        public void onError(String mUniqueId, String error, String mUrl) {
                            progressHandler.onError(uniqueId, error, url);
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, long bytesDownloaded, long totalBytesToDownload) {

                            progressHandler.onProgressUpdate(uniqueId, bytesDownloaded, totalBytesToDownload);


                            if (totalBytesToDownload > checkFreeSpace()) {

                                progressHandler.onLowFreeSpace(uniqueId, url);

                            }
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, int progress) {

                            progressHandler.onProgressUpdate(uniqueId, progress);

                        }

                        @Override
                        public void onFileReady(ChatResponse<ResultDownloadFile> response) {
                            progressHandler.onFileReady(response);
                        }
                    },
                    getFileServer(),
                    url,
                    fileName,
                    destinationFolder,
                    downloaderErrorInterface,
                    request.getHashCode(),
                    request.getImageId());

            downloadCallList.put(uniqueId, call);

        } else onChatNotReady(uniqueId);

        return uniqueId;
    }

    private PodDownloader.IDownloaderError getDownloaderErrorInterface(ProgressHandler.IDownloadFile progressHandler, String uniqueId, String url) {
        return new PodDownloader.IDownloaderError() {
            @Override
            public void errorOnWritingToFile() {

                String error = getErrorOutPut(ChatConstant.ERROR_WRITING_FILE, ChatConstant.ERROR_CODE_WRITING_FILE, uniqueId);
                progressHandler.onError(uniqueId, error, url);

            }

            @Override
            public void errorOnDownloadingFile(int errorCode) {

                String error = getErrorOutPut(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId);
                progressHandler.onError(uniqueId, error, url);

            }

            @Override
            public void errorUnknownException(String cause) {

                String error = getErrorOutPut(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId);
                progressHandler.onError(uniqueId, error, url);


            }
        };
    }


//    public String getImage(RequestGetImage request, ProgressHandler.IDownloadFile progressHandler) {
//
//        String uniqueId = generateUniqueId();
//
//        String url = getImage(request.getImageId(), request.getHashCode(), request.isDownloadable());
//
//
//        //todo handle if not
//        isExternalStorageWritable();
//
//        if (!hasReadAndWriteStoragePermission()) {
//
//            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
//
//            progressHandler.onError(uniqueId, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, url);
//
//            return uniqueId;
//
//        }
//
//        //only url should return in callback
//        if (!hasFreeSpace) {
//
//            progressHandler.onLowFreeSpace(uniqueId, url);
//
//            return uniqueId;
//        }
//
//
//        PodDownloader.IDownloaderError downloaderErrorInterface =
//                getDownloaderErrorInterface(progressHandler, uniqueId, url);
//
//
//        File imagesFolder = cache ? FileUtils.getOrCreateDirectory(FileUtils.PICTURES) : FileUtils.getPublicFilesDirectory();
//
//        if (imagesFolder == null) {
//
//            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);
//
//            return uniqueId;
//        }
//
//        String fileName = "image_" + request.getImageId() + "_" + request.getHashCode();
//
//
//        if (cache) {
//
//
//            File cachedFile = FileUtils.findFileInFolder(imagesFolder, fileName);
//
//            if (cachedFile != null && cachedFile.isFile()) {
//
//                ChatResponse<ResultDownloadFile> response = PodDownloader.generateDownloadResult(request.getHashCode(), request.getImageId(), cachedFile);
//
//                progressHandler.onFileReady(response);
//
//                return uniqueId;
//
//            }
//
//        }
//
//        if (chatReady) {
//
//            PodDownloader.download(
//                    progressHandler,
//                    uniqueId, imagesFolder,
//                    url,
//                    fileName,
//                    request.getHashCode(),
//                    request.getImageId(),
//                    getContext(),
//                    downloaderErrorInterface,
//                    checkFreeSpace());
//
//
//        } else onChatNotReady(uniqueId);
//
//
//        return uniqueId;
//
//
//    }


    /**
     * @param request
     * @param progressHandler
     * @return it downloads file with id and hashCode in link request if enough space is available.
     * <p>
     * if free space wasn't available progressHandler @return error with url of file;
     * <p>
     * if cache is set to true, file will save in cache, else only downloaded file will return.
     */


//    public String getFile(RequestGetFile request, ProgressHandler.IDownloadFile progressHandler) {
//
//        String uniqueId = generateUniqueId();
//
//        String url = getFile(request.getFileId(), request.getHashCode(), request.isDownloadable());
//
//        isExternalStorageWritable();
//
//        if (!hasReadAndWriteStoragePermission()) {
//
//            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION,
//                    ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
//
//            progressHandler.onError(uniqueId, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, url);
//
//            return uniqueId;
//
//        }
//
//        //only url should return in callback
//
//        if (!hasFreeSpace) {
//
//            progressHandler.onLowFreeSpace(uniqueId, url);
//
//            return uniqueId;
//        }
//
//        PodDownloader.IDownloaderError downloaderErrorInterface =
//                new PodDownloader.IDownloaderError() {
//                    @Override
//                    public void errorOnWritingToFile() {
//
//                        String error = getErrorOutPut(ChatConstant.ERROR_WRITING_FILE, ChatConstant.ERROR_CODE_WRITING_FILE, uniqueId);
//                        progressHandler.onError(uniqueId, error, url);
//
//                    }
//
//                    @Override
//                    public void errorOnDownloadingFile(int errorCode) {
//
//                        String error = getErrorOutPut(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId);
//                        progressHandler.onError(uniqueId, error, url);
//
//                    }
//
//                    @Override
//                    public void errorUnknownException(String cause) {
//
//                        String error = getErrorOutPut(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId);
//                        progressHandler.onError(uniqueId, error, url);
//
//
//                    }
//                };
//
//        File filesFolder = cache ? FileUtils.getOrCreateDirectory(FileUtils.FILES) : FileUtils.getPublicFilesDirectory();
//
//        if (filesFolder == null) {
//
//            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);
//
//            return uniqueId;
//        }
//
//        String fileName = "file_" + request.getFileId() + "_" + request.getHashCode();
//
//        if (cache) {
//
//            File cachedFile = FileUtils.findFileInFolder(filesFolder, fileName);
//
//            if (cachedFile != null && cachedFile.isFile()) {
//
//                //file exists in cache
//                ChatResponse<ResultDownloadFile> response = PodDownloader.generateDownloadResult(request.getHashCode(), request.getFileId(), cachedFile);
//
//                progressHandler.onFileReady(response);
//
//                return uniqueId;
//
//            }
//        }
//
//        if (chatReady) {
//
//            PodDownloader.download(
//                    progressHandler,
//                    uniqueId, filesFolder,
//                    url,
//                    fileName,
//                    request.getHashCode(),
//                    request.getFileId(),
//                    getContext(),
//                    downloaderErrorInterface,
//                    checkFreeSpace());
//
//        } else onChatNotReady(uniqueId);
//
//        return uniqueId;
//    }


    /**
     * clearCacheDatabase interface
     * <p>
     * onCacheDatabaseCleared called when everything is done
     * <p>
     * onExceptionOccurred called when any exception occur
     */


    public interface IClearMessageCache {

        void onCacheDatabaseCleared();

        void onExceptionOccurred(String cause);

    }

    public void clearCacheDatabase(IClearMessageCache listener) {

        if (messageDatabaseHelper != null)
            messageDatabaseHelper.clearAllData(listener);
    }

    public long getCachedPicturesFolderSize() {

        return FileUtils.getStorageSize(FileUtils.PICTURES);
    }

    public long getCachedFilesFolderSize() {
        return FileUtils.getStorageSize(FileUtils.FILES);
    }

    public boolean clearCachedPictures() {
        return FileUtils.clearDirectory(FileUtils.PICTURES);
    }

    public boolean clearCachedFiles() {
        return FileUtils.clearDirectory(FileUtils.FILES);
    }

    public long getCacheSize() {
        return FileUtils.getCacheSize(getContext());
    }

    public long getStorageSize() {
        return FileUtils.getStorageSize(FileUtils.Media);
    }


    /**
     * it cancels started download with uniqueId
     *
     * @param uniqueId getImage or getFile returned value.
     * @return true if function could cancel a download with that uniqueId.
     */

    public boolean cancelDownload(String uniqueId) {

        boolean success;


        if (downloadQList.containsKey(uniqueId)) {

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);


            int result = downloadManager.remove(downloadQList.get(uniqueId));

            downloadQList.remove(uniqueId);

            success = result > 0;

            return success;

        }

        if (downloadCallList.containsKey(uniqueId)) {

            Call call = downloadCallList.get(uniqueId);

            if (call != null) {
                call.cancel();
                downloadCallList.remove(uniqueId);
                return true;
            }

            return false;

        }

        return false;
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
        chatMessage.setTypeCode(getTypeCode());

        setCallBacks(null, null, null, true, Constants.RENAME, null, uniqueId);
        String asyncContent = gson.toJson(chatMessage);
        sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_RENAME_THREAD");
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
    @Deprecated
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
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_ADD_PARTICIPANTS");
                if (handler != null) {
                    handler.onAddParticipants(uniqueId);
                }

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


            JsonArray participantsJsonArray = new JsonArray();


            if (request.getContactIds() != null) {
                for (Long p : request.getContactIds()) {
                    participantsJsonArray.add(p);
                }
            } else {

                for (String username :
                        request.getUserNames()) {

                    Invitee invitee = new Invitee();
                    invitee.setId(username);
                    invitee.setIdType(InviteType.Constants.TO_BE_USER_USERNAME);
                    JsonElement jsonElement = gson.toJsonTree(invitee);
                    participantsJsonArray.add(jsonElement);

                }

            }

            AsyncMessage chatMessage = new AsyncMessage();

            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());
            chatMessage.setContent(participantsJsonArray.toString());
            chatMessage.setSubjectId(request.getThreadId());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setType(Constants.ADD_PARTICIPANT);
            chatMessage.setTypeCode(getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            setCallBacks(null, null, null, true, Constants.ADD_PARTICIPANT, null, uniqueId);
            sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_ADD_PARTICIPANTS");
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

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REMOVE_PARTICIPANT");
            setCallBacks(null, null, null, true, Constants.REMOVE_PARTICIPANT, null, uniqueId);
            if (handler != null) {
                handler.onRemoveParticipants(uniqueId);
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_LEAVE_THREAD");

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

                    if (queueCache != null) {
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
            }
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_FORWARD_MESSAGE");
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

        Activity activity = request.getActivity();

        if (!Permission.Check_READ_STORAGE(activity)) {


            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);

            Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);


            return uniqueId;

        }


        long threadId = request.getThreadId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        Uri fileUri = request.getFileUri();
        long messageId = request.getMessageId();
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
//                    String path = FilePick.getSmartFilePath(context, fileUri);
                    uploadFileMessage(lFileUpload);
                }
                return uniqueId;
            } else {
                getErrorOutPut(ChatConstant.ERROR_INVALID_URI, ChatConstant.ERROR_CODE_INVALID_URI, uniqueId);
            }
        } catch (Exception e) {
            getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
            if (log) Log.e(TAG, e.getCause().getMessage());
            return uniqueId;
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

        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, null, handler);
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
        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, null, handler);
    }

    /**
     * It deletes message from the thread
     *
     * @param messageIds   Id of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u don't want
     *                     you can set it false or even null.
     */


    public List<String> deleteMessage(ArrayList<Long> messageIds, Boolean deleteForAll, ChatHandler handler) {

//        Log.d(MTAG, "Multiple Message delete");

        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();

        if (chatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            AsyncMessage asyncMessage = new AsyncMessage();

            for (Long id :
                    messageIds) {

                String uniqueId1 = generateUniqueId();

                uniqueIds.add(uniqueId1);

                setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId1);


            }

            JsonObject contentObj = new JsonObject();

            JsonElement messageIdsElement = gson.toJsonTree(messageIds, new TypeToken<List<Long>>() {
            }.getType());

            JsonElement uniqueIdsElement = gson.toJsonTree(uniqueIds, new TypeToken<List<String>>() {
            }.getType());


            contentObj.add("id", messageIdsElement.getAsJsonArray());
            contentObj.add("uniqueIds", uniqueIdsElement.getAsJsonArray());
            contentObj.addProperty("deleteForAll", deleteForAll);


            asyncMessage.setContent(contentObj.toString());
            asyncMessage.setToken(getToken());
            asyncMessage.setTokenIssuer("1");
            asyncMessage.setType(Constants.DELETE_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(asyncMessage);

            jsonObject.remove("subjectId");

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELETE_MESSAGE");


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

//        uniqueIds.add(uniqueId);

        return uniqueIds;
    }

    private String deleteMessage(Long messageId, Boolean deleteForAll, ChatHandler handler) {


//        Log.d(MTAG, "Single Message delete");
        String uniqueId;
        uniqueId = generateUniqueId();


        if (chatReady) {
            deleteForAll = deleteForAll != null ? deleteForAll : false;
            AsyncMessage asyncMessage = new AsyncMessage();

            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("deleteForAll", deleteForAll);


            asyncMessage.setContent(contentObj.toString());
            asyncMessage.setToken(getToken());
            asyncMessage.setTokenIssuer("1");
            asyncMessage.setType(Constants.DELETE_MESSAGE);
            asyncMessage.setUniqueId(uniqueId);
            asyncMessage.setSubjectId(messageId);
            asyncMessage.setTypeCode(getTypeCode());


            sendAsyncMessage(gson.toJson(asyncMessage), AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELETE_MESSAGE");

            setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId);


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }


        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


        if (request.getMessageIds().size() > 1) {

            return getErrorOutPut(ChatConstant.ERROR_NUMBER_MESSAGEID, ChatConstant.ERROR_CODE_NUMBER_MESSAGE_ID, null);
        }

        return deleteMessage(request.getMessageIds().get(0), request.isDeleteForAll(), handler);


//        if (request.getMessageIds().size() > 1)
//            return deleteMessage(request.getMessageIds(),
//                    request.isDeleteForAll(),
//                    handler);
//
//        else if (request.getMessageIds().size() == 1) {
//            return deleteMessage(request.getMessageIds().get(0),
//                    request.isDeleteForAll(), handler);
//        } else {
//
//            return null;
//        }


    }


    public List<String> deleteMultipleMessage(RequestDeleteMessage request, ChatHandler handler) {

        return deleteMessage(request.getMessageIds(),
                request.isDeleteForAll(),
                handler);

    }


    /**
     * Get the list of threads or you can just pass the thread id that you want
     *
     * @param count                number of thread
     * @param offset               specified offset you want
     * @param creatorCoreUserId
     * @param partnerCoreUserId
     * @param partnerCoreContactId
     * @param isNew                if true, threads with new messages will return
     */


    @Deprecated
    public String getThreads(Integer count, Long offset, ArrayList<Integer> threadIds,
                             String threadName, long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, boolean isNew, ChatHandler handler) {

        String uniqueId;

        count = count != null && count > 0 ? count : 50;

        uniqueId = generateUniqueId();

        try {
            if (cache) {
                handleCacheThread(count, offset, threadIds, threadName, isNew);
            }

            if (chatReady) {

                ChatMessageContent chatMessageContent = new ChatMessageContent();

                chatMessageContent.setNew(isNew);


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
//                jObj.remove("new");
//                jObj.remove("count");
//                jObj.remove("offset");
//                jObj.addProperty("summary", true);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(Constants.GET_THREADS);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(getTypeCode());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }

                setCallBacks(null, null, null, true, Constants.GET_THREADS, offsets, uniqueId);

                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREADS");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
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
        count = count > 0 ? count : 50;
        uniqueId = generateUniqueId();
        try {

            if (cache) {

                handleCacheThread(count, offset, threadIds, threadName, false);

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
//                jObj.remove("new");


//                jObj.addProperty("summary", true);

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


                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREADS");
                if (handler != null) {
                    handler.onGetThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }
        return uniqueId;
    }


    private void getThreadsSummaryAndUpdateThreadsCache() {

        if (cache) {

            String uniqueId = generateUniqueId();

            JsonObject content = new JsonObject();

            content.addProperty("summary", true);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content.toString());
            chatMessage.setToken(getToken());
            chatMessage.setType(Constants.GET_THREADS);
            chatMessage.setTypeCode(getTypeCode());
            chatMessage.setUniqueId(uniqueId);

            ChatHandler handler = new ChatHandler() {

                @Override
                public void onGetThread(String data) {
                    super.onGetThread(data);

                    ArrayList<ResultThreadsSummary> result = gson.fromJson(data,
                            new TypeToken<ArrayList<ResultThreadsSummary>>() {
                            }.getType());

                    ArrayList<Long> serverResultThreadIds = new ArrayList<>();

                    for (ResultThreadsSummary rts :
                            result) {
                        serverResultThreadIds.add((long) rts.getId());
                    }


                    messageDatabaseHelper.getThreadIdsList(t -> {

                        try {

                            List<Long> threadsIdsInCache = (List<Long>) t;

                            if (threadsIdsInCache != null && threadsIdsInCache.size() > 0) {

                                threadsIdsInCache.removeAll(serverResultThreadIds);

                                if (serverResultThreadIds.size() > 0) {

//                                        showLog("# " + serverResultThreadIds.size() + " thread deleted", "");

                                    messageDatabaseHelper.deleteThreads(new ArrayList<>(threadsIdsInCache));
                                }

                            }
                            showLog("THREADS CACHE UPDATED", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });

                }
            };


            //callback
            handlerSend.put(uniqueId, handler);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            jsonObject.remove("contentCount");
            jsonObject.remove("repliedTo");
            jsonObject.remove("subjectId");
            jsonObject.remove("time");

            sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "UPDATING_THREADS_CACHE");

        }


    }

    public String getThreads(RequestThread requestThread, ChatHandler handler) {

        ArrayList<Integer> threadIds = requestThread.getThreadIds();
        long offset = requestThread.getOffset();
        long creatorCoreUserId = requestThread.getCreatorCoreUserId();
        long partnerCoreContactId = requestThread.getPartnerCoreContactId();
        long partnerCoreUserId = requestThread.getPartnerCoreUserId();
        String threadName = requestThread.getThreadName();
        long count = requestThread.getCount();
        boolean isNew = requestThread.isNew();
        return getThreads((int) count, offset, threadIds, threadName, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId, isNew, handler);
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
     * {@link #updateWaitingQ(long, String, ChatHandler)} (long)} It is checked that if the message had been sent then its removed
     * it from the messageQueue.
     *
     * @param threadId Id of the thread that we want to get the history
     */
    private String getHistory(History history, long threadId, ChatHandler handler) {

        String mainUniqueId = generateUniqueId();


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

        //updating waitQ ( list or db )

        updateWaitingQ(threadId, mainUniqueId, new ChatHandler() {
            @Override
            public void onGetHistory(String uniqueId) {
                super.onGetHistory(uniqueId);


                if (cache && history.getMetadataCriteria() == null) {

                    final boolean[] loadFromCache = {true};

                    List<MessageVO> messagesFromCache = getHistoryFromCache(history, threadId, uniqueId);

                    if (Util.isNullOrEmpty(messagesFromCache)) {
                        loadFromCache[0] = false;
                    }

                    for (MessageVO msg : messagesFromCache) {

                        if (msg.hasGap()) {
                            loadFromCache[0] = false;
                            break;
                        }
                    }


                    if (chatReady) {

                        getHistoryMain(history, threadId, new ChatHandler() {


                            @Override
                            public void onGetHistory(ChatResponse<ResultHistory> chatResponse, ChatMessage chatMessage, Callback callback) {
                                super.onGetHistory(chatResponse, chatMessage, callback);

                                //insert new messages
                                updateChatHistoryCache(callback, chatMessage, chatResponse.getResult().getHistory());

                                //get inserted message
                                List<MessageVO> newMessagesFromServer = new ArrayList<>(chatResponse.getResult().getHistory());

                                findAndUpdateGaps(newMessagesFromServer, threadId);


                                if (loadFromCache[0]) {

                                    List<MessageVO> editedMessages = new ArrayList<>(newMessagesFromServer);

                                    List<MessageVO> newMessages = new ArrayList<>(newMessagesFromServer);

                                    newMessages.removeAll(messagesFromCache);

                                    findDeletedMessages(messagesFromCache, newMessagesFromServer, uniqueId);

                                    editedMessages.removeAll(newMessages);

                                    findEditedMessages(messagesFromCache, editedMessages, uniqueId, threadId);

                                    publishNewMessages(newMessages, threadId, uniqueId);

//
                                } else {
                                    //publish server result
                                    publishChatHistoryServerResult(callback, chatMessage, newMessagesFromServer);
                                }

                            }
                        }, uniqueId);
                    } else {
                        getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                    }
                    return;
                }

                if (chatReady) {
                    getHistoryMain(history, threadId, handler, uniqueId);
                } else {
                    getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }

            }
        });
        return mainUniqueId;
    }

    private List<MessageVO> getHistoryFromCache(History history, long threadId, String uniqueId) {

        List<MessageVO> messageVOS = messageDatabaseHelper.getHistories(history, threadId);

        if (messageVOS != null) {

            long contentCount = messageDatabaseHelper.getHistoryContentCount(threadId);

            ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();
            chatResponse.setCache(true);

            chatResponse.setCache(true);

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

            return messageVOS;
        }
        return messageVOS;
    }

    private List<String> getUniqueIdsInWaitQ() {

        List<String> waitingQMessagesUniqueIds = new ArrayList<>();

        if (cache) {

            for (WaitQueueCache wq :
                    messageDatabaseHelper.getAllWaitQueueMsg()) {
                waitingQMessagesUniqueIds.add(wq.getUniqueId());
            }

        } else {

            waitingQMessagesUniqueIds.addAll(waitQList.keySet());

        }

        return waitingQMessagesUniqueIds;
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


        History history = new History.Builder()
                .count(request.getCount())
                .firstMessageId(request.getFirstMessageId())
                .lastMessageId(request.getLastMessageId())
                .metadataCriteria(request.getMetadataCriteria())
                .offset(request.getOffset())
                .fromTime(request.getFromTime())
                .fromTimeNanos(request.getFromTimeNanos())
                .toTime(request.getToTime())
                .toTimeNanos(request.getToTimeNanos())
                .uniqueIds(request.getUniqueIds())
                .id(request.getId())
                .order(request.getOrder() != null ? request.getOrder() : "desc").build();


        return getHistory(history, request.getThreadId(), handler);


//        String uniqueId = generateUniqueId();
//
//
//        if (chatReady) {
//
//
//            History history = new History.Builder()
//                    .count(request.getCount())
//                    .firstMessageId(request.getFirstMessageId())
//                    .lastMessageId(request.getLastMessageId())
//                    .metadataCriteria(request.getMetadataCriteria())
//                    .offset(request.getOffset())
//                    .fromTime(request.getFromTime())
//                    .fromTimeNanos(request.getFromTimeNanos())
//                    .toTime(request.getToTime())
//                    .toTimeNanos(request.getToTimeNanos())
//                    .uniqueIds(request.getUniqueIds())
//                    .id(request.getId())
//                    .order(request.getOrder() != null ? request.getOrder() : "desc").build();
//
//
//            updateWaitingQ(request.getThreadId(), uniqueId, new ChatHandler() {
//                @Override
//                public void onGetHistory(String uniqueId) {
//                    super.onGetHistory(uniqueId);
//
//                    if (cache && history.getMetadataCriteria() == null) {
//
//                        final boolean[] loadFromCache = {true};
//
//                        List<MessageVO> messagesFromCache = getHistoryFromCache(history, request.getThreadId(), uniqueId);
//
//                        if (Util.isNullOrEmpty(messagesFromCache)) {
//                            loadFromCache[0] = false;
//                        }
//
//                        for (MessageVO msg : messagesFromCache) {
//
//                            if (msg.hasGap()) {
//                                loadFromCache[0] = false;
//                                break;
//                            }
//                        }
//
//
//                        if (chatReady) {
//
//                            getHistoryMain(history, request.getThreadId(), new ChatHandler() {
//
//
//                                @Override
//                                public void onGetHistory(ChatResponse<ResultHistory> chatResponse, ChatMessage chatMessage, Callback callback) {
//                                    super.onGetHistory(chatResponse, chatMessage, callback);
//
//                                    //delete old messages
////                                    messageDatabaseHelper.deleteMessages(cacheMessageVOS);
//
////                                    messageDatabaseHelper.deleteAllGapsFrom(request.getThreadId());
//
//                                    //insert new messages
//                                    updateChatHistoryCache(callback, chatMessage, chatResponse.getResult().getHistory());
//
//                                    //get inserted message
////                                    List<MessageVO> newMessagesFromServer = messageDatabaseHelper.getHistories(history, request.getThreadId());
//                                    List<MessageVO> newMessagesFromServer = new ArrayList<>(chatResponse.getResult().getHistory());
//
//                                    findAndUpdateGaps(newMessagesFromServer, request.getThreadId());
//
//
//                                    if (loadFromCache[0]) {
//
//                                        List<MessageVO> editedMessages = new ArrayList<>(newMessagesFromServer);
//
//                                        List<MessageVO> newMessages = new ArrayList<>(newMessagesFromServer);
//
//                                        newMessages.removeAll(messagesFromCache);
//
//                                        findDeletedMessages(messagesFromCache, newMessagesFromServer, uniqueId);
//
//                                        editedMessages.removeAll(newMessages);
//
//                                        findEditedMessages(messagesFromCache, editedMessages, uniqueId, request.getThreadId());
//
//                                        publishNewMessages(newMessages, request.getThreadId(), uniqueId);
//
////                                        Log.d(TAG, ">>>>>> New: " + newMessages.size());
////                                    Log.d(TAG, ">>>>>> Deleted: " + deletedMessagesSize);
////                                        Log.d(TAG, ">>>>>> Edited: " + editedMessages.size());
//
//                                        //todo new messages after delete some message...
//                                        // all delete messages couldn't find
//
//                                    } else {
//                                        //publish server result
//                                        publishChatHistoryServerResult(callback, chatMessage, newMessagesFromServer);
//                                    }
//
//                                }
//                            }, uniqueId);
//                        } else {
//                            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//                        }
//                        return;
//                    }
//
//                    if (chatReady) {
//                        getHistoryMain(history, request.getThreadId(), handler, uniqueId);
//                    } else {
//                        getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//                    }
//
//                }
//            });
//
//        } else {
//
//            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//        }
//
//        return uniqueId;
    }

    private void findAndUpdateGaps(List<MessageVO> newMessagesFromServer, long threadId) {


        Runnable jobFindAndInsertGap = () -> {

            if (newMessagesFromServer.size() == 0) return;


            MessageVO lastMessage = newMessagesFromServer.get(newMessagesFromServer.size() - 1);


            //if it is first message of thread, it couldn't has gap

            if (lastMessage.getPreviousId() == 0)
                return;


            List<CacheMessageVO> messages = messageDatabaseHelper.getMessageById(lastMessage.getPreviousId());

            if (Util.isNullOrEmpty(messages)) {

                GapMessageVO gapMessageVO = new GapMessageVO();

                gapMessageVO.setId(lastMessage.getId());

                gapMessageVO.setPreviousId(lastMessage.getPreviousId());

                gapMessageVO.setThreadId(threadId);

                gapMessageVO.setTime(lastMessage.getTime());

                gapMessageVO.setUniqueId(lastMessage.getUniqueId());

                messageDatabaseHelper.insertGap(gapMessageVO);

                lastMessage.setHasGap(true);

                messageDatabaseHelper.updateMessage(lastMessage, threadId);

            }

        };

        Runnable jobUpdateGaps = () -> {


            List<GapMessageVO> gaps = messageDatabaseHelper.getAllGaps(threadId);

            if (!Util.isNullOrEmpty(gaps)) {

                Map<Long, Long> msgIdAndPreviousId = new HashMap<>();

                for (GapMessageVO gapMessage :
                        gaps) {

                    msgIdAndPreviousId.put(gapMessage.getPreviousId(), gapMessage.getId());


                }

                for (MessageVO newMessage :
                        newMessagesFromServer) {

                    if (msgIdAndPreviousId.containsKey(newMessage.getId())) {

                        //delete gap that produced by this message
                        messageDatabaseHelper.deleteGapForMessageId(msgIdAndPreviousId.get(newMessage.getId()));


                        //set message gap field to false
                        messageDatabaseHelper.updateMessageGapState(msgIdAndPreviousId.get(newMessage.getId()), false);

                    }


                }


            }


        };


        runOnNewThread(jobFindAndInsertGap);

        runOnNewThread(jobUpdateGaps);


    }

    private void publishNewMessages(List<MessageVO> newMessages, long threadId, String uniqueId) {


        for (MessageVO messageVO :
                newMessages) {

            try {

                ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
                chatResponse.setUniqueId(messageVO.getUniqueId());
                chatResponse.setHasError(false);
                chatResponse.setErrorCode(0);
                chatResponse.setErrorMessage("");
                ResultNewMessage resultNewMessage = new ResultNewMessage();
                resultNewMessage.setMessageVO(messageVO);
                resultNewMessage.setThreadId(threadId);
                chatResponse.setResult(resultNewMessage);
                String json = gson.toJson(chatResponse);
                listenerManager.callOnNewMessage(json, chatResponse);
                long ownerId = 0;
                if (messageVO != null) {
                    ownerId = messageVO.getParticipant().getId();
                }
                showLog("RECEIVED_NEW_MESSAGE", json);
                if (ownerId != getUserId()) {

                    if (messageVO != null) {
                        ChatMessage message = getChatMessage(messageVO);
                        String asyncContent = gson.toJson(message);
                        async.sendMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK);
                        setThreadCallbacks(threadId, uniqueId);
                        showLog("SEND_DELIVERY_MESSAGE", asyncContent);
                    }
                }
            } catch (Exception e) {
                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                if (log) Log.e(TAG, e.getMessage());

            }

        }

    }


    private void findDeletedMessages(List<MessageVO> messagesFromCache, List<MessageVO> newMessagesFromServer, String uniqueId) {


        for (MessageVO msg :
                messagesFromCache) {


            if (!newMessagesFromServer.contains(msg)) {


                ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
                chatResponse.setUniqueId(uniqueId);
                ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
                DeleteMessageContent deleteMessage = new DeleteMessageContent();
                deleteMessage.setId(msg.getId());
                resultDeleteMessage.setDeletedMessage(deleteMessage);
                chatResponse.setResult(resultDeleteMessage);

                String jsonDeleteMsg = gson.toJson(chatResponse);


                long threadId = msg.getConversation() != null ? msg.getConversation().getId() : 0;

                runOnNewThread(() -> messageDatabaseHelper.deleteMessage(msg.getId(), threadId));

                showLog("DeleteMessage from dataBase with this messageId" + " " + msg.getId(), "");

                listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);
                showLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);


            }
        }


    }


    public void runOnNewThread(Runnable job) {


        new java.lang.Thread(job).start();

    }

    private List<MessageVO> findEditedMessages(List<MessageVO> oldMessages, List<MessageVO> newMessages, String uniqueId, long threadId) {


        Set<MessageVO> editedMessages = new HashSet<>();

        for (MessageVO newMessage :
                newMessages) {

            for (MessageVO oldMessage :
                    oldMessages) {


                if (oldMessage.isEdited(newMessage)) {

                    editedMessages.add(newMessage);

                    ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
                    ResultNewMessage editMessage = new ResultNewMessage();

                    editMessage.setMessageVO(newMessage);
                    editMessage.setThreadId(threadId);
                    chatResponse.setResult(editMessage);
                    chatResponse.setUniqueId(uniqueId);
                    String content = gson.toJson(chatResponse);
                    showLog("RECEIVE_EDIT_MESSAGE", content);

                    listenerManager.callOnEditedMessage(content, chatResponse);
                    messageCallbacks.remove(newMessage.getUniqueId());

                }

            }

        }

        return new ArrayList<>(editedMessages);
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND SEARCH0. HISTORY");
            if (handler != null) {
                handler.onSearchHistory(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        stopSignalHandlerThread();

        setSignalHandlerThread();

        String uniqueId = generateUniqueId();

        Handler handler = new Handler(signalMessageHandlerThread.getLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (signalMessageRanTime > 60000) {
                    listenerManager.callOnSignalMessageTimeout(requestSignalMsg.getThreadId());
                    signalMessageRanTime = 0;
                    return;
                }

                signalMessage(requestSignalMsg, logMessage, uniqueId);
                handler.postDelayed(this, getSignalIntervalTime());
                signalMessageRanTime += getSignalIntervalTime();
            }
        };

        handler.post(runnable);

        signalHandlerKeeper.put(uniqueId, handler);

        return uniqueId;

    }

    private String getLastThreadSignalUniqueId(Long threadId) {

        ArrayList<String> handlersIds = threadSignalsKeeper.get(threadId);

        if (handlersIds != null) {
            return handlersIds.get(handlersIds.size() - 1);
        }

        return null;
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

            signalHandlerKeeper.get(uniqueId)
                    .removeCallbacksAndMessages(null);

            showLog("STOP_SIGNAL", "");

            return true;
        }

        return false;

    }

    public void stopTyping() {

        stopSignalHandlerThread();

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
        long count = request.getCount();
        return getContacts((int) count, offset, handler);
    }

    /**
     * Get all of the contacts of the user
     */
    public String getContacts(Integer count, Long offset, ChatHandler handler) {
        return getContactMain(count, offset, false, handler);
    }


    //TODO test again on cache
    public String searchContact(RequestSearchContact requestSearchContact) {

        String uniqueId = generateUniqueId();


        if (cache) {
            List<Contact> contacts = new ArrayList<>();
            if (requestSearchContact.getId() != null) {
                Contact contact = messageDatabaseHelper.getContactById(Long.valueOf(requestSearchContact.getId()));
                contacts.add(contact);
            } else if (requestSearchContact.getFirstName() != null) {
                contacts = messageDatabaseHelper.getContactsByFirst(requestSearchContact.getFirstName());
            } else if (requestSearchContact.getFirstName() != null && requestSearchContact.getLastName() != null && !requestSearchContact.getFirstName().isEmpty() && !requestSearchContact.getLastName().isEmpty()) {
                contacts = messageDatabaseHelper.getContactsByFirstAndLast(requestSearchContact.getFirstName(), requestSearchContact.getLastName());
            } else if (requestSearchContact.getEmail() != null && !requestSearchContact.getEmail().isEmpty()) {
                contacts = messageDatabaseHelper.getContactsByEmail(requestSearchContact.getEmail());
            } else if (requestSearchContact.getCellphoneNumber() != null && !requestSearchContact.getCellphoneNumber().isEmpty()) {
                contacts = messageDatabaseHelper.getContactByCell(requestSearchContact.getCellphoneNumber());
            }

            ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
            chatResponse.setCache(true);

            ResultContact resultContact = new ResultContact();
            ArrayList<Contact> listContact = new ArrayList<>(contacts);
            resultContact.setContacts(listContact);

            chatResponse.setHasError(false);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");
            chatResponse.setResult(resultContact);

            String jsonContact = gson.toJson(chatResponse);
            listenerManager.callOnSearchContact(jsonContact, chatResponse);
            showLog("CACHE_SEARCH_CONTACT", jsonContact);


        }

        if (chatReady) {

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(requestSearchContact);

            jsonObject.addProperty("uniqueId", uniqueId);

            jsonObject.addProperty("tokenIssuer", 1);

            showLog("SEND_SEARCH_CONTACT", getJsonForLog(jsonObject));


            Observable<Response<SearchContactVO>> observable = contactApi.searchContact(
                    getToken(),
                    TOKEN_ISSUER,
                    requestSearchContact.getId()
                    , requestSearchContact.getFirstName()
                    , requestSearchContact.getLastName()
                    , requestSearchContact.getEmail()
                    , null
                    , requestSearchContact.getOffset()
                    , requestSearchContact.getSize()
                    , null
                    , requestSearchContact.getQuery()
                    , requestSearchContact.getCellphoneNumber());

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(contactResponse -> {


                        if (contactResponse.isSuccessful()) {

                            if (contactResponse.body() != null && contactResponse.body().getResult() != null) {
                                ArrayList<Contact> contacts = new ArrayList<>(contactResponse.body().getResult());

                                ResultContact resultContacts = new ResultContact();
                                resultContacts.setContacts(contacts);

                                ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
                                chatResponse.setUniqueId(uniqueId);
                                chatResponse.setResult(resultContacts);

                                String content = gson.toJson(chatResponse);


                                showLog("RECEIVE_SEARCH_CONTACT", content);

                                listenerManager.callOnSearchContact(content, chatResponse);

                            }

                        } else {

                            if (contactResponse.body() != null) {
                                String errorMessage = contactResponse.body().getMessage() != null ? contactResponse.body().getMessage() : "";
                                int errorCode = contactResponse.body().getErrorCode() != null ? contactResponse.body().getErrorCode() : 0;
                                getErrorOutPut(errorMessage, errorCode, uniqueId);
                            }
                        }

                    }, (Throwable throwable) -> Log.e(TAG, throwable.getMessage()));
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Add one contact to the contact list
     * <p>
     * firstName and cellphoneNumber or email or username are required
     *
     * @param username        username of contact
     * @param firstName       Notice: if just put fistName without lastName its ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public String addContact(String firstName, String lastName, String cellphoneNumber, String email, String typeCode, String username) {

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

        if (Util.isNullOrEmpty(username)) {
            username = "";
        }

        String uniqueId = generateUniqueId();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("uniqueId", uniqueId);
        jsonObject.addProperty("tokenIssuer", 1);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("cellphoneNumber", cellphoneNumber);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("typeCode", typeCode);
        jsonObject.addProperty("username", username);


        showLog("SEND_ADD_CONTACT", getJsonForLog(jsonObject));


        Observable<Response<Contacts>> addContactObservable;

        if (chatReady) {


            if (!Util.isNullOrEmpty(username)) {

                addContactObservable = contactApi.addContactWithUserName(getToken(), 1, firstName, lastName, username, uniqueId, typeCode);

            } else if (Util.isNullOrEmpty(typeCode)) {

                addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber);
            } else {

                addContactObservable = contactApi.addContact(getToken(), 1, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);
            }


            addContactObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addContactResponse -> {
                        if (addContactResponse.isSuccessful()) {

                            Contacts contacts = addContactResponse.body();
                            if (contacts != null) {

                                if (!contacts.getHasError()) {


                                    ChatResponse<ResultAddContact> chatResponse = Util.getReformatOutPutAddContact(contacts, uniqueId);

                                    String contactsJson = gson.toJson(chatResponse);

                                    listenerManager.callOnAddContact(contactsJson, chatResponse);

                                    showLog("RECEIVED_ADD_CONTACT", contactsJson);

                                    if (cache) {

                                        messageDatabaseHelper.saveContact(chatResponse.getResult().getContact(), getExpireAmount());
                                    }
                                } else {
                                    getErrorOutPut(contacts.getMessage(), contacts.getErrorCode(), uniqueId);
                                }
                            }
                        }
                    }, (Throwable throwable) ->
                    {
                        Log.e("Error on add contact", throwable.getMessage());
                        Log.e(TAG, throwable.getMessage());
                    });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
        String username = request.getUsername();
        String typeCode = request.getTypeCode() != null ? request.getTypeCode() : getTypeCode();

        return addContact(firstName, lastName, cellphoneNumber, email, typeCode, username);
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public String removeContact(long userId) {
        String uniqueId = generateUniqueId();


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("uniqueId", uniqueId);
        jsonObject.addProperty("id", userId);
        jsonObject.addProperty("tokenIssuer", 1);

        showLog("SEND_REMOVE_CONTACT", getJsonForLog(jsonObject));


        Observable<Response<ContactRemove>> removeContactObservable;
        if (chatReady) {
            if (Util.isNullOrEmpty(getTypeCode())) {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId);
            } else {
                removeContactObservable = contactApi.removeContact(getToken(), 1, userId, getTypeCode());
            }

            removeContactObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {

                        Log.d("MTAG", response.raw().toString());

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
                                getErrorOutPut(contactRemove.getErrorMessage(), contactRemove.getErrorCode(), uniqueId);
                            }
                        }
                    }, (Throwable throwable) ->
                            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId));
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("uniqueId", uniqueId);
        jsonObject.addProperty("id", userId);
        jsonObject.addProperty("tokenIssuer", 1);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("cellphoneNumber", cellphoneNumber);
        jsonObject.addProperty("email", email);


        showLog("SEND_UPDATE_CONTACT", getJsonForLog(jsonObject));

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
            updateContactObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {

                        UpdateContact updateContact = response.body();

                        if (updateContact != null && response.isSuccessful()) {

                            if (response.body() != null) {

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

                                    getErrorOutPut(errorMsg, errorCodeMsg, uniqueId);
                                }
                            }
                        }
                    }, (Throwable throwable) ->
                    {
                        if (throwable != null) {
                            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                        }
                    });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mapNeshanResponse -> {
                OutPutMapNeshan outPutMapNeshan = new OutPutMapNeshan();
                if (mapNeshanResponse.isSuccessful()) {
                    MapNeshan mapNeshan = mapNeshanResponse.body();

                    if (mapNeshan == null) {
                        getErrorOutPut(mapNeshanResponse.message(), mapNeshanResponse.code(), uniqueId);
                        return;
                    }
                    outPutMapNeshan = new OutPutMapNeshan();
                    outPutMapNeshan.setCount(mapNeshan.getCount());
                    ResultMap resultMap = new ResultMap();
                    resultMap.setMaps(mapNeshan.getItems());
                    outPutMapNeshan.setResult(resultMap);
                    String json = gson.toJson(outPutMapNeshan);
                    listenerManager.callOnMapSearch(json, outPutMapNeshan);
                    showLog("RECEIVE_MAP_SEARCH", json);
                } else {
//                        ErrorOutPut errorOutPut = new ErrorOutPut();
//                        errorOutPut.setErrorCode(mapNeshanResponse.code());
//                        errorOutPut.setErrorMessage(mapNeshanResponse.message());
//                        errorOutPut.setHasError(true);
//                        String json = gson.toJson(outPutMapNeshan);
//                        listenerManager.callOnError(json, errorOutPut);
                    getErrorOutPut(mapNeshanResponse.message(), mapNeshanResponse.code(), uniqueId);
                }
            }, (Throwable throwable) -> {
//                ErrorOutPut errorOutPut = new ErrorOutPut();
//                errorOutPut.setErrorMessage(throwable.getMessage());
//                errorOutPut.setHasError(true);
//                String json = gson.toJson(errorOutPut);
//
//                listenerManager.callOnError(json, errorOutPut);
                getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

            });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                if (log) Log.e(TAG, throwable.getMessage());
            });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
        responseObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mapRoutResponse -> {
            if (mapRoutResponse.isSuccessful()) {
                MapRout mapRout = mapRoutResponse.body();
                OutPutMapRout outPutMapRout = new OutPutMapRout();
                outPutMapRout.setResult(mapRout);
                String jsonMapRout = gson.toJson(outPutMapRout);
                listenerManager.callOnMapRouting(jsonMapRout);
                showLog("RECEIVE_MAP_ROUTING", jsonMapRout);
            } else {
                getErrorOutPut(mapRoutResponse.message(), mapRoutResponse.code(), finalUniqueId);
            }
        }, (Throwable throwable) -> getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId));
        return uniqueId;
    }

    /**
     * [Required] center
     * if these params don't set they have default values :
     * [default value] messageType = "standard-night"
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

            if (needReadStoragePermission(activity)) return uniqueId;

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


                JsonObject jsonObject = (JsonObject) gson.toJsonTree(request);

                jsonObject.addProperty("uniqueId", uniqueId);

                showLog("SEND_GET_MAP_STATIC", getJsonForLog(jsonObject));


                RetrofitHelperMap retrofitHelperMap = new RetrofitHelperMap("https://api.neshan.org/");
                MapApi mapApi = retrofitHelperMap.getService(MapApi.class);

                Call<ResponseBody> call = mapApi.mapStaticCall(API_KEY_MAP,
                        type,
                        zoom,
                        center,
                        width,
                        height);


                String finalUniqueId = uniqueId;

                String finalUniqueId1 = uniqueId;
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
                                showLog("RECEIVE_MAP_STATIC", "[]");
                                if (!call.isCanceled()) {
                                    call.cancel();
                                }

                                if (isMessage) {
                                    File file = FileUtils.saveBitmap(bitmap, "map");

                                    if (file == null) {

                                        getErrorOutPut(ChatConstant.ERROR_WRITING_FILE,
                                                ChatConstant.ERROR_CODE_WRITING_FILE,
                                                finalUniqueId1);

                                        return;
                                    }

                                    Uri fileUri = Uri.fromFile(file);
//                                    String newPath = FilePick.getSmartFilePath(getContext(), fileUri);

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
                                    getErrorOutPut(errorMessage, errorCode, finalUniqueId);
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
                        getErrorOutPut(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                    }
                });


            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


            if (needReadStoragePermission(activity)) return uniqueId;

            if (chatReady) {

                JsonObject jsonLog = new JsonObject();


                String type = request.getType();


                int zoom = request.getZoom();


                int width = request.getWidth();


                int height = request.getHeight();


                String center = request.getCenter();


                long threadId = request.getThreadId();

                int messageType = request.getMessageType() > 0 ? request.getMessageType() : TextMessageType.Constants.PICTURE;


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

                jsonLog.addProperty("type", type);
                jsonLog.addProperty("zoom", zoom);
                jsonLog.addProperty("width", width);
                jsonLog.addProperty("height", height);
                jsonLog.addProperty("center", center);
                jsonLog.addProperty("threadId", threadId);
                jsonLog.addProperty("messageType", messageType);
                jsonLog.addProperty("systemMetadata", systemMetadata);
                jsonLog.addProperty("uniqueId", uniqueId);


                showLog("SEND_LOCATION_MESSAGE", getJsonForLog(jsonLog));

                String finalUniqueId1 = uniqueId;
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

                                    if (file == null) {

                                        getErrorOutPut(ChatConstant.ERROR_WRITING_FILE,
                                                ChatConstant.ERROR_CODE_WRITING_FILE,
                                                finalUniqueId1);

                                        return;
                                    }


                                    Uri fileUri = Uri.fromFile(file);
//                                    String newPath = FilePick.getSmartFilePath(getContext(), fileUri);

                                    String mimType = handleMimType(fileUri, file);

                                    LFileUpload lFileUpload = new LFileUpload();

                                    lFileUpload.setFileUri(fileUri);
                                    //noinspection ConstantConditions
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
                                    getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                }

                            } catch (JSONException e) {
                                if (log) Log.e(TAG, e.getMessage());
                            } catch (IOException e) {
                                if (log) Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        getErrorOutPut(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                    }
                });
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


        return mainMapStaticImage(requestLMsg, null, null, false, null);
    }

    public String sendLocationMessage(RequestLocationMessage request) {

        String uniqueId = generateUniqueId();
        Activity activity = request.getActivity();
        mainMapStaticImage(request, activity, uniqueId, true, null);

        return uniqueId;
    }

    public String sendLocationMessage(RequestLocationMessage request, ProgressHandler.sendFileMessage handler) {


        String uniqueId = generateUniqueId();

        Activity activity = request.getActivity();

        mainMapStaticImage(request, activity, uniqueId, true, handler);

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
                    .subscribe(mapReverseResponse -> {

                        if (mapReverseResponse.isSuccessful()) {
                            MapReverse mapReverse = mapReverseResponse.body();

                            if (mapReverse == null) {

                                getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);

                                return;
                            }

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
                                    getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                }

                            } catch (JSONException e) {

                                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);
                                if (log) Log.e(TAG, e.getMessage());
                            } catch (IOException e) {
                                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);

                                if (log) Log.e(TAG, e.getMessage());
                            }
                        }
                    }, (Throwable throwable) -> getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId));
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
            chatMessage.setTypeCode(getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            setCallBacks(null, null, null, true, Constants.BLOCK, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_BLOCK");
            if (handler != null) {
                handler.onBlock(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
     * @param blockId   the id that came from getBlockList
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_UN_BLOCK");
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPORT_SPAM");
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

                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPORT_SPAM");
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


        if (cache) {

            List<BlockedContact> cacheContacts = messageDatabaseHelper.getBlockedContacts(count, offset);
            if (!Util.isNullOrEmpty(cacheContacts)) {

                ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
                chatResponse.setErrorCode(0);
                chatResponse.setHasError(false);
                chatResponse.setUniqueId(uniqueId);
                ResultBlockList resultBlockList = new ResultBlockList();

                resultBlockList.setContacts(cacheContacts);
                chatResponse.setResult(resultBlockList);
                String jsonGetBlock = gson.toJson(chatResponse);
                listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);
                showLog("RECEIVE_GET_BLOCK_LIST_FROM_CACHE", jsonGetBlock);
            }
        }


        if (chatReady) {

            JsonObject content = new JsonObject();

            if (offset != null) {
                content.addProperty("offset", offset);
            }
            if (count != null) {
                content.addProperty("count", count);
            } else {
                content.addProperty("count", 50);

            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content.toString());
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_BLOCK_LIST");
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


    public String createThread(RequestCreateThread request) {

        String uniqueId = generateUniqueId();


        if (chatReady) {

            JsonObject chatMessageContent = (JsonObject) gson.toJsonTree(request);

            if (request instanceof RequestCreatePublicThread) {

                String uniqueName = ((RequestCreatePublicThread) request).getUniqueName();

                chatMessageContent.addProperty("uniqueName", uniqueName);


            }

            AsyncMessage chatMessage = new AsyncMessage();
            chatMessage.setContent(chatMessageContent.toString());
            chatMessage.setType(Constants.INVITATION);
            chatMessage.setToken(getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer("1");
            chatMessage.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? getTypeCode() : request.getTypeCode() );

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD");

        } else {
            onChatNotReady(uniqueId);
        }


        return uniqueId;
    }


    public String joinPublicThread(RequestJoinPublicThread request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String asyncMessage = PublicThread.joinPublicThread(request, uniqueId);

            sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND JOIN PUBLIC THREAD");

        } else onChatNotReady(uniqueId);


        return uniqueId;

    }


    /**
     * Create the thread to p to p/channel/group. The list below is showing all of the threads messageType
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD");
            if (handler != null) {
                handler.onCreateThread(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }


    public ArrayList<String> createThreadWithFile(RequestCreateThreadWithFile request, @Nullable ProgressHandler.onProgressFile progressHandler) {

        ArrayList<String> uniqueIds = new ArrayList<>();

        String requestUniqueId = generateUniqueId();

        uniqueIds.add(requestUniqueId);

        if (needReadStoragePermission(request.getFile().getActivity())) return uniqueIds;


        String innerMessageUniqueId = generateMessageUniqueId(request, t -> uniqueIds.add((String) t));


        List<String> forwardUniqueIds = generateForwardingMessageId(request, t -> uniqueIds.addAll((Collection<? extends String>) t));


//        addToUploadQueue(
//                Constants.INVITATION,
//                request.getMessage().getText(),
//                uniqueIds.get(0));

        if (chatReady) {

            if (request.getFile() != null && request.getFile() instanceof RequestUploadImage) {

                if (FileUtils.isGif(getMimType(request.getFile().getFileUri())))
                    uploadFile(request.getFile(), requestUniqueId, progressHandler, metaData -> prepareCreateThreadWithFile(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds, (String) metaData));
                else
                    uploadImage((RequestUploadImage) request.getFile(), requestUniqueId, progressHandler, metaData -> prepareCreateThreadWithFile(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds, (String) metaData));

            } else if (request.getFile() != null)
                uploadFile(request.getFile(), requestUniqueId, progressHandler, metaData -> prepareCreateThreadWithFile(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds, (String) metaData));


        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, requestUniqueId);
        }

        return uniqueIds;
    }

    private void prepareCreateThreadWithFile(RequestCreateThreadWithFile request, String requestUniqueId, String innerMessageUniqueId, List<String> forwardUniqueIds, String metaData) {

        RequestThreadInnerMessage requestThreadInnerMessage = generateInnerMessageForCreateThreadWithFile(request, metaData);

        request.setMessage(requestThreadInnerMessage);

        request.setMessageType(request.getMessageType());

        request.setFile(null);

        createThreadWithMessage(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds);
    }

    private String getMimType(Uri fileUri) {

        return getContext().getContentResolver().getType(fileUri);
    }

    private RequestThreadInnerMessage generateInnerMessageForCreateThreadWithFile(RequestCreateThreadWithFile request, String metaData) {

        RequestThreadInnerMessage requestThreadInnerMessage;

        if (request.getMessage() == null) {
            requestThreadInnerMessage = new RequestThreadInnerMessage
                    .Builder(request.getType())
                    .metadata(metaData)
                    .type(request.getMessageType())
                    .build();
        } else {
            requestThreadInnerMessage = request.getMessage();
            requestThreadInnerMessage.setMetadata(metaData);
            requestThreadInnerMessage.setType(request.getMessageType());
        }


        return requestThreadInnerMessage;
    }

    private void createThreadWithMessage(
            RequestCreateThreadWithFile request,
            String requestUniqueId,
            String innerMessageUniqueId,
            List<String> forwardUniqueIds) {


        RequestCreateThreadWithMessage rctm = new RequestCreateThreadWithMessage
                .Builder(request.getType(), request.getInvitees(), request.getMessageType())
                .message(request.getMessage())
                .build();


        JsonObject asyncRequestObject = null;
        JsonObject innerMessageObj = null;

        try {

            if (chatReady) {

                innerMessageObj = (JsonObject) gson.toJsonTree(request.getMessage());

                if (Util.isNullOrEmpty(request.getMessage().getType())) {
                    innerMessageObj.remove("type");
                }
                if (Util.isNullOrEmpty(request.getMessage().getText())) {
                    innerMessageObj.remove("message");
                } else {
                    innerMessageObj.addProperty("uniqueId", innerMessageUniqueId);
                    setCallBacks(true, true, true, true, Constants.MESSAGE, null, innerMessageUniqueId);
                }
                if (!Util.isNullOrEmpty(forwardUniqueIds)) {
                    //todo add set callback for each forwardid
                    JsonElement forwardMessageUniqueIdsJsonElement = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                    }.getType());
                    JsonArray jsonArray = forwardMessageUniqueIdsJsonElement.getAsJsonArray();
                    innerMessageObj.add("forwardedUniqueIds", jsonArray);
                } else {
                    innerMessageObj.remove("forwardedUniqueIds");
                    innerMessageObj.remove("forwardedMessageIds");
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(rctm);
                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);


                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(Constants.INVITATION);
                chatMessage.setUniqueId(requestUniqueId);
                chatMessage.setToken(getToken());
                chatMessage.setTokenIssuer("1");

                asyncRequestObject = (JsonObject) gson.toJsonTree(chatMessage);

                asyncRequestObject.remove("repliedTo");
                asyncRequestObject.remove("subjectId");
                asyncRequestObject.remove("systemMetadata");
                asyncRequestObject.remove("contentCount");

                String typeCode = request.getTypeCode();

                if (Util.isNullOrEmpty(typeCode)) {

                    if (Util.isNullOrEmpty(getTypeCode())) {
                        asyncRequestObject.remove("typeCode");
                    } else {
                        asyncRequestObject.addProperty("typeCode", getTypeCode());
                    }
                } else {
                    asyncRequestObject.addProperty("typeCode", typeCode);
                }
                setCallBacks(null, null, null, true, Constants.INVITATION, null, requestUniqueId);
                sendAsyncMessage(asyncRequestObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD_WITH_FILE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, requestUniqueId);
            }

        } catch (Exception e) {
            getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, requestUniqueId);
            Log.e(TAG, e.getMessage());
        }
    }

    private List<String> generateForwardingMessageId(RequestCreateThreadWithFile request, OnWorkDone listener) {


        List<String> uniqueIds = new ArrayList<>();

        if (request.getMessage() != null)
            if (!Util.isNullOrEmpty(request.getMessage().getForwardedMessageIds())) {


                for (Long ignored :
                        request.getMessage().getForwardedMessageIds()) {


                    uniqueIds.add(generateUniqueId());

                }

                listener.onWorkDone(uniqueIds);
                return uniqueIds;
            }


        return null;
    }

    private String generateMessageUniqueId(RequestCreateThreadWithFile request, OnWorkDone listener) {

        String newMsgUniqueId = null;
        if (request.getMessage() != null && !Util.isNullOrEmpty(request.getMessage().getText())) {
            newMsgUniqueId = generateUniqueId();

            listener.onWorkDone(newMsgUniqueId);
        }
        return newMsgUniqueId;
    }


    /**
     * Create the thread with message is just for  p to p.( Thread Type is int NORMAL = 0)
     *
     * @return The first UniqueId is for create thread and the rest of them are for new message or forward messages
     * Its have three kind of Unique Ids. One of them is for message. One of them for Create Thread
     * and the others for Forward Message or Messages.
     * <p>
     * int messageType  Type of the Thread (You can have Thread Type from ThreadType.class)
     * String ownerSsoId  [Optional]
     * List<Invitee> invitees  you can add your invite list here
     * String title  [Optional] title of the group thread
     * <p>
     * RequestThreadInnerMessage message{  object of the inner message
     * <p>
     * -------------  String text  text of the message
     * -------------  int messageType  messageType of the message  [Optional]
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

                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {


                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);

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
                    jsonObject.addProperty("typeCode", typeCode);

                }

                setCallBacks(null, null, null, true, Constants.UPDATE_THREAD_INFO, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_UPDATE_THREAD_INFO");
                if (handler != null) {
                    handler.onUpdateThreadInfo(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        return getThreadAdminsMain((int) count, offset, threadId, getAdmin, typeCode, handler);
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
        count = count != null ? count != 0 ? count : 50 : 50;


        if (cache) {
            List<Participant> participants = messageDatabaseHelper.getThreadParticipant(offset, count, threadId, false);
            //noinspection ConstantConditions
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
                showLog("PARTICIPANT FROM CACHE", jsonParticipant);

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

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_PARTICIPANT");

            if (handler != null) {
                handler.onGetThreadParticipant(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;

    }


    private String getThreadAdminsMain(Integer count, Long offset, long threadId, boolean getAdmin, String typeCode, ChatHandler handler) {


        String uniqueId;
        uniqueId = generateUniqueId();

        if (!chatReady) return uniqueId;


        offset = offset != null ? offset : 0;
        count = count != null ? count != 0 ? count : 50 : 50;


        if (cache) {
            List<Participant> participants = messageDatabaseHelper.getThreadParticipant(offset, count, threadId, getAdmin);
            //noinspection ConstantConditions
            if (participants != null) {
                long participantCount = messageDatabaseHelper.getParticipantCount(threadId);
                ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
                chatResponse.setCache(true);

                ResultParticipant resultParticipant = new ResultParticipant();
                resultParticipant.setThreadId(threadId);

                resultParticipant.setContentCount(participants.size());
                if (participants.size() + offset < participantCount) {
                    resultParticipant.setHasNext(true);
                } else {
                    resultParticipant.setHasNext(false);
                }
                resultParticipant.setParticipants(participants);
                chatResponse.setResult(resultParticipant);
                chatResponse.setCache(true);
                chatResponse.setSubjectId(threadId);

                resultParticipant.setNextOffset(offset + participants.size());
                String jsonParticipant = gson.toJson(chatResponse);


                OutPutParticipant outPutParticipant = new OutPutParticipant();

                outPutParticipant.setResult(resultParticipant);


                listenerManager.callOnGetThreadAdmin(jsonParticipant, chatResponse);

                showLog("RECEIVE ADMINS FROM CACHE", jsonParticipant);

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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_ADMINS");
            if (handler != null) {
                handler.onGetThreadParticipant(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_SEEN_MESSAGE");
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

        if (permit) {


            try {
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
            } catch (Exception e) {
                e.printStackTrace();
                if (log) Log.e(TAG, e.getMessage());
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
            async.sendMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK);

            if (handler != null) {
                handler.onGetUserInfo(uniqueId);
            }
        }


        return uniqueId;
    }

    /**
     * It Mutes the thread so notification is set to off for that thread
     */
    public String muteThread(long threadId, ChatHandler handler) {
        String uniqueId;
        uniqueId = generateUniqueId();
        JsonObject jsonObject;
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
                    jsonObject.addProperty("typeCode", typeCode);

                }

                setCallBacks(null, null, null, true, Constants.MUTE_THREAD, null, uniqueId);
                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_MUTE_THREAD");

                if (handler != null) {
                    handler.onMuteThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_UN_MUTE_THREAD");
                if (handler != null) {
                    handler.onUnMuteThread(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
//                chatMessage.setMessageType(TextMessageType.Constants.TEXT);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", getTypeCode());
                }


                jsonObject.remove("metadata");
                jsonObject.remove("contentCount");
                jsonObject.remove("repliedTo");
                jsonObject.remove("time");
                jsonObject.remove("messageType");

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_EDIT_MESSAGE");
                stopTyping();
                if (handler != null) {
                    handler.onEditMessage(uniqueId);
                }
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        return editMessage((int) request.getMessageId(), request.getMessageContent(), request.getMetaData(), handler);


//        String uniqueId = generateUniqueId();
//
//        try {
//            JsonObject jsonObject = null;
//            if (chatReady) {
//
//                String messageContent = request.getMessageContent();
//                long messageId = request.getMessageId();
//                String metaData = request.getMetaData();
//
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setMessageType(Constants.EDIT_MESSAGE);
//                chatMessage.setToken(getToken());
//                chatMessage.setUniqueId(uniqueId);
//                chatMessage.setSubjectId(messageId);
//                chatMessage.setContent(messageContent);
//                chatMessage.setSystemMetadata(metaData);
//                chatMessage.setTokenIssuer("1");
//
//                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
//                jsonObject.remove("contentCount");
//                jsonObject.remove("systemMetadata");
//                jsonObject.remove("metadata");
//                jsonObject.remove("repliedTo");
//
//                if (Util.isNullOrEmpty(typeCode)) {
//                    if (Util.isNullOrEmpty(getTypeCode())) {
//                        jsonObject.remove("typeCode");
//                    } else {
//                        jsonObject.addProperty("typeCode", getTypeCode());
//                    }
//                } else {
//                    jsonObject.addProperty("typeCode", typeCode);
//                }
//
//                setCallBacks(null, null, null, true, Constants.EDIT_MESSAGE, null, uniqueId);
//                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_EDIT_MESSAGE");
//            } else {
//                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            }
//            if (handler != null) {
//                handler.onEditMessage(uniqueId);
//            }
//        } catch (Exception e) {
//            if (log) Log.e(TAG, e.getCause().getMessage());
//        }
//        return uniqueId;
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


    public Chat setListener(ChatListener listener) {

        listenerManager.clearListeners();

        listenerManager.addListener(listener, log);

        return this;

    }

    public void clearListeners() {

        listenerManager.clearListeners();


    }

    public void clearAllListeners() {

        listenerManager.clearListeners();
        async.clearListeners();
    }

    public List<ChatListener> getLsteners() {

        return listenerManager.getListeners();
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


    public void setReconnectOnClose(boolean reconnectOnClose) {

        if (async != null) {

            async.setReconnectOnClose(reconnectOnClose);

        }

    }


    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        CoreConfig.typeCode = typeCode;
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

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CLEAR_HISTORY");
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

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_NOT_SEEN_DURATION");
        }

        return uniqueId;


    }

    public String getAdminList(RequestGetAdmin requestGetAdmin) {


        return getThreadParticipants(requestGetAdmin, requestGetAdmin.isAdmin(), null);
//        String uniqueId = generateUniqueId();
//        long threadId = requestGetAdmin.getThreadId();
//        if (chatReady) {
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setMessageType(Constants.PIN_THREAD);
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
//
////            setCallBacks(null, null, null, true, Constants.CLEAR_HISTORY, null, uniqueId);
//
//            setCallBacks(null, null, null, true, Constants.PIN_THREAD, null, uniqueId);
//
//            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_ADMINS");
//
//
//        }
//        return uniqueId;
    }


    public String getAdminList(RequestGetAdmin requestGetAdmin, ChatHandler handler) {


        return getThreadParticipants(requestGetAdmin, requestGetAdmin.isAdmin(), handler);
//        String uniqueId = generateUniqueId();
//        long threadId = requestGetAdmin.getThreadId();
//        if (chatReady) {
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setMessageType(Constants.PIN_THREAD);
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
//
////            setCallBacks(null, null, null, true, Constants.CLEAR_HISTORY, null, uniqueId);
//
//            setCallBacks(null, null, null, true, Constants.PIN_THREAD, null, uniqueId);
//
//            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_ADMINS");
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
        }, PING_INTERVAL);

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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITH_ACK, "CHAT PING");
        }
    }

    private void pingAfterSetToken() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(Constants.PING);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());

        checkToken = true;


        String asyncContent = gson.toJson(chatMessage);
        async.sendMessage(asyncContent, AsyncAckType.Constants.WITH_ACK);
        showLog("**SEND_CHAT PING FOR CHECK TOKEN AUTHENTICATION", asyncContent);
    }

    private void showLog(String i, String json) {
        if (log) {
            Log.i(TAG, i);
            Log.i(TAG, json);
            if (!Util.isNullOrEmpty(json)) {
                listenerManager.callOnLogEvent(json);
                listenerManager.callOnLogEvent(i, json);
            }
        }
    }

    private String handleMimType(Uri uri, File file) {
        String mimType;

        if (getMimType(uri) != null) {
            mimType = getMimType(uri);
        } else {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            int index = file.getName().lastIndexOf('.') + 1;
            String ext = file.getName().substring(index).toLowerCase();
            mimType = mime.getMimeTypeFromExtension(ext);
        }
        return mimType;
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
//            retrySetToken = 1;

            String errorMessage = error.getMessage();
            long errorCode = error.getCode();
            getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());

            pingHandler.removeCallbacksAndMessages(null);

            /*we are Changing the state of the chat because of the Client is not Authenticate*/
            listenerManager.callOnChatState("ASYNC_READY");

            stopTyping();

            return;
        }
        String errorMessage = error.getMessage();
        long errorCode = error.getCode();

        getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());
    }


    /**
     * called when thread last seen message updated
     * <p>
     * result is updated thread info with new thread unreadCount
     */

    private void handleLastSeenUpdated(ChatMessage chatMessage) {

        //thread last seen message updated
        //
        //result is updated thread info
        //

        try {
            ResultThread resultThread = new ResultThread();
            Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
            resultThread.setThread(thread);

            ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
            chatResponse.setResult(resultThread);
            chatResponse.setUniqueId(chatMessage.getUniqueId());


            showLog("THREAD_LAST_SEEN_MESSAGE_UPDATED", "");
            showLog(chatMessage.getContent(), "");
            listenerManager.callOnThreadInfoUpdated(chatMessage.getContent(), chatResponse);

            if (cache) {
                messageDatabaseHelper.saveNewThread(resultThread.getThread());
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Exception in Thread Last Seen Message Updated");
        }

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

        if (cache) {
            messageDatabaseHelper.saveNewThread(resultThread.getThread());
        }

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

        //todo
        if (cache) {

            messageDatabaseHelper.deleteThread(chatMessage.getSubjectId());

        }
    }

    /**
     * After the set Token, we send ping for checking client Authenticated or not
     * the (boolean)checkToken is for that reason
     */
    private void handleOnPing(ChatMessage chatMessage) {

        showLog("RECEIVED_CHAT_PING", "");

        if (checkToken) {

            chatReady = true;
            chatState = CHAT_READY;
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
                messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId(), false);
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
            if (ownerId != getUserId()) {

                if (messageVO != null) {
                    ChatMessage message = getChatMessage(messageVO);
                    String asyncContent = gson.toJson(message);
                    async.sendMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK);
                    setThreadCallbacks(chatMessage.getSubjectId(), chatMessage.getUniqueId());
                    showLog("SEND_DELIVERY_MESSAGE", asyncContent);
                }
            }
        } catch (Exception e) {
            getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId());
            if (log) Log.e(TAG, e.getMessage());

        }

    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage, String messageUniqueId, long threadId) {


        if (cache) {
            messageDatabaseHelper.deleteWaitQueueMsgs(messageUniqueId);
        } else {
            waitQList.remove(messageUniqueId);
        }


        boolean found = false;
        try {
            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
                if (callbacks != null) {
                    for (Callback callback : callbacks) {
                        if (messageUniqueId.equals(callback.getUniqueId())) {
                            int indexUnique = callbacks.indexOf(callback);
                            if (callbacks.get(indexUnique).isSent()) {

                                found = true;

                                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                                ResultMessage resultMessage = new ResultMessage();

                                chatResponse.setErrorCode(0);
                                chatResponse.setErrorMessage("");
                                chatResponse.setHasError(false);
                                chatResponse.setUniqueId(callback.getUniqueId());
                                chatResponse.setSubjectId(chatMessage.getSubjectId());
                                resultMessage.setConversationId(chatMessage.getSubjectId());
                                resultMessage.setMessageId(Long.valueOf(chatMessage.getContent()));
                                chatResponse.setResult(resultMessage);

                                String json = gson.toJson(chatResponse);
                                listenerManager.callOnSentMessage(json, chatResponse);

                                runOnUIThread(() -> {
                                    if (handlerSend.get(callback.getUniqueId()) != null) {


                                        ChatHandler handler = handlerSend.get(callback.getUniqueId());

                                        if (handler != null) {


                                            handler.onSentResult(chatMessage.getContent());
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
            }

            if (!found) {


                ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();

                ResultMessage resultMessage = new ResultMessage();

                chatResponse.setErrorCode(0);
                chatResponse.setErrorMessage("");
                chatResponse.setHasError(false);
                chatResponse.setUniqueId(messageUniqueId);
                chatResponse.setSubjectId(chatMessage.getSubjectId());
                resultMessage.setConversationId(chatMessage.getSubjectId());
                resultMessage.setMessageId(Long.valueOf(chatMessage.getContent()));
                chatResponse.setResult(resultMessage);

                String json = gson.toJson(chatResponse);
                listenerManager.callOnSentMessage(json, chatResponse);

//                Log.d("MTAG", "threadid isn't in callbacks!");


                runOnUIThread(() -> {
                    if (handlerSend.get(messageUniqueId) != null) {

                        handlerSend.get(messageUniqueId).onSentResult(chatMessage.getContent());
                    }
                });

                Callback callbackUpdateSent = new Callback();
                callbackUpdateSent.setSent(false);
                callbackUpdateSent.setUniqueId(messageUniqueId);

//                callbacks.set(indexUnique, callbackUpdateSent);
//                threadCallbacks.put(threadId, callbacks);
                showLog("RECEIVED_SENT_MESSAGE", json);


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
            if (log) Log.i(TAG, "checkMessageQueue");
            if (cache) {
                List<SendingQueueCache> sendingQueueCaches = messageDatabaseHelper.getAllSendingQueue();
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
                        sendAsyncMessage(async, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
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
                        sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
                    }
                    sendingQList.clear();
                }
            }

        } catch (Throwable throwable) {
            if (log) Log.e(TAG, throwable.getMessage());
        }
    }

    private void handleSeen(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        if (threadCallbacks.containsKey(threadId)) {
            ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
            if (callbacks != null) {
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
    }

    private void handleDelivery(ChatMessage chatMessage, String messageUniqueId, long threadId) {

        try {
            if (threadCallbacks.containsKey(threadId)) {
                ArrayList<Callback> callbacks = threadCallbacks.get(threadId);
                if (callbacks != null) {
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
            ChatMessage message = null;
            if (messageVO != null) {
                message = getChatMessage(messageVO);
            }

            String asyncContent = gson.toJson(message);
            showLog("SEND_DELIVERY_MESSAGE", asyncContent);

            async.sendMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK);
        }
    }

    private void handleResponseMessage(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        try {
            if (callback != null) {
                if (callback.getRequestType() >= 1) {
                    switch (callback.getRequestType()) {

                        case Constants.GET_HISTORY:

                            handleOutPutGetHistory(callback, chatMessage);
                            break;
                        case Constants.GET_CONTACTS:

                            handleGetContact(callback, chatMessage, messageUniqueId);
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
                                String unMuteThreadJson = reformatMuteThread(chatMessage, chatResponse);
                                listenerManager.callOnUnmuteThread(unMuteThreadJson, chatResponse);
                                messageCallbacks.remove(messageUniqueId);
                                showLog("RECEIVE_UN_MUTE_THREAD", unMuteThreadJson);

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
            if (log) Log.e(TAG, e.getMessage());
        }
    }

    private void handleOnGetParticipants(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        if (callback.isResult()) {

            ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

            String jsonParticipant = gson.toJson(chatResponse);


//            OutPutParticipant output = new OutPutParticipant();
//
//            output.setResult(chatResponse.getResult());
//
//            output.setSubjectId(chatMessage.getSubjectId());
//
//


            if (chatResponse.getResult().getParticipants().size() > 0)
                if (!Util.isNullOrEmpty(chatResponse.getResult().getParticipants().get(0).getRoles())) {


                    listenerManager.callOnGetThreadAdmin(jsonParticipant, chatResponse);

                    showLog("RECEIVE_ADMINS", jsonParticipant);


                } else {

                    listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);

                    listenerManager.callOnGetThreadParticipant(chatResponse);

                    showLog("RECEIVE_PARTICIPANT", jsonParticipant);

                }
            else {

                listenerManager.callOnGetThreadAdmin(jsonParticipant, chatResponse);

                showLog("RECEIVE_ADMINS", jsonParticipant);
            }


            messageCallbacks.remove(messageUniqueId);


        }
    }


    private void handleEditMessage(ChatMessage chatMessage, String messageUniqueId) {


        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage newMessage = new ResultNewMessage();
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);


        if (cache) {
            CacheMessageVO cacheMessageVO = gson.fromJson(chatMessage.getContent(), CacheMessageVO.class);
            messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId(), true);
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


        String bToken = "Bearer " + getToken();


        Call<EncResponse> responseCallback = api.generateEncryptionKey(bToken,
                algorithm, keySize, false, validity);


        responseCallback.enqueue(new retrofit2.Callback<EncResponse>() {
            @Override
            public void onResponse(Call<EncResponse> call, Response<EncResponse> response) {

                if (response.body() != null) {

                    if (response.isSuccessful()) {


                        if (response.body().getSecretKey() != null) {

                            initDatabaseWithKey(response.body().getSecretKey());

                            setKey(response.body().getSecretKey());

                        } else {

                            initDatabase();
                        }

                        setChatReady("CHAT_READY");


                    } else {


                        if (log) if (response.errorBody() != null) {
                            Log.e(TAG, response.errorBody().toString());
                        }

                        initDatabase();

                        setChatReady("CHAT_READY_WITHOUT_ENCRYPTION_US");


                    }


                } else {


                    if (log) if (response.errorBody() != null) {
                        Log.e(TAG, response.errorBody().toString());
                    }
                    initDatabase();
                    setChatReady("CHAT_READY_WITHOUT_ENCRYPTION_NS");

                }


            }

            @Override
            public void onFailure(Call<EncResponse> call, Throwable t) {

                Log.e(TAG, "Failure On: " + t.getMessage());

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
//                            DaggerMessageComponent.newBuilder()
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

    private void setChatReady(String state) {

        listenerManager.callOnChatState("CHAT_READY");
        chatReady = true;
        chatState = CHAT_READY;
        checkMessageQueue();

        getThreadsSummaryAndUpdateThreadsCache();
        showLog(state, "");
        permit = true;
        checkFreeSpace();
    }


    private void initDatabase() {

        DaggerMessageComponent.builder()
                .appDatabaseModule(new AppDatabaseModule(getContext()))
                .appModule(new AppModule(context))
                .build()
                .inject(instance);


    }

    private void initDatabaseWithKey(String secretKey) {


        DaggerMessageComponent.builder()
                .appDatabaseModule(new AppDatabaseModule(getContext(), secretKey))
                .appModule(new AppModule(context))
                .build()
                .inject(instance);


    }


    // we have handleRemoveFromWaitQueue and checkMessageValidation
    //if callBacks in 'onReceivedMessage' equal null it means we are removing messages from waitQ
    //todo get uniqueIds from waitQ in 'checkMessageValidation' so it's done!

    /**
     * It is sent uniqueIds of messages from waitQueue to ensure all of them have been sent.
     */
    private void updateWaitingQ(long threadId, String uniqueId, ChatHandler handler) {

        /*  if waitQueue had these messages then send request's getHistory and in onSent remove them from wait queue
         */
        List<String> waitingQMessagesUniqueIds = getUniqueIdsInWaitQ();

        String[] uniqueIds = waitingQMessagesUniqueIds.toArray(new String[0]);


        handlerSend.put(uniqueId, handler);


        if (!Util.isNullOrEmpty(uniqueIds)) {

            if (chatReady)
                getHistoryWithUniqueIds(threadId, uniqueId, uniqueIds);
            else {
                handler.onGetHistory(uniqueId);
                onChatNotReady(uniqueId);
            }

        } else {

            handler.onGetHistory(uniqueId);

        }
    }

    private void getHistoryWithUniqueIds(long threadId, String uniqueId, String[] uniqueIds) {

        RequestGetHistory request = new RequestGetHistory
                .Builder(threadId)
                .offset(0)
                .count(uniqueIds.length)
                .uniqueIds(uniqueIds)
                .build();

        String content = gson.toJson(request);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
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

        sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "CHECK_HISTORY_MESSAGES");
    }

    /**
     * order If order is empty [default = desc] and also you have two option [ asc | desc ]
     * order should be set with lower case
     */
    private void getHistoryMain(History history, long threadId, ChatHandler handler, String uniqueId) {
//        long offsets = history.getOffset();
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

        if (history.getUniqueIds() == null) {

            jObj.remove("uniqueIds");

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
//        if (handler != null) {
//            handler.onGetHistory(uniqueId);
//        }

        handlerSend.put(uniqueId, handler);

        sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND GET THREAD HISTORY");
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


        if (cache) {

            messageDatabaseHelper.saveNewThread(chatResponse.getResult().getThread());

        }

    }

    private void handleGetThreads(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage, callback);
        String threadJson = gson.toJson(chatResponse);

        if (cache) {


            //if true, it is a getThreadSummary request
            if (handlerSend.containsKey(chatMessage.getUniqueId())) {

                Objects.requireNonNull(handlerSend.get(chatMessage.getUniqueId()))
                        .onGetThread(chatMessage.getContent());
                return;
            }
        }

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
         *                             messageType: 'IS_TYPING',
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

        listenerManager.callOnGetSignalMessage(result);

        if (result == null) {
            return;
        }

        OutputSignalMessage output = new OutputSignalMessage();

        output.setResultSignalMessage(result.getResult());

        output.setSubjectId(result.getSubjectId());

        output.setUniqueId(result.getUniqueId());

        ResultSignalMessage sm = result.getResult();

        output.setSignalMessageType(getSignalMessageType(sm.getSmt()));

        output.setSignalSenderName(sm.getUser());

        listenerManager.callOnGetSignalMessage(output);

        showLog("RECEIVE_SIGNAL_MESSAGE", gson.toJson(output));


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


        ResultSignalMessage signalMessage;

        try {
            signalMessage = gson.fromJson(chatMessage.getContent(), ResultSignalMessage.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId());
//            listenerManager.callOnError("Exception in reformatting signal message content", null);

            return null;

        }

        result.setSubjectId(chatMessage.getSubjectId());

        result.setUniqueId(chatMessage.getUniqueId());

        result.setResult(signalMessage);


        return result;
    }


    private void handleGetNotSeenDuration(Callback callback, ChatMessage chatMessage, String messageUniqueId) {


        messageCallbacks.remove(messageUniqueId);


        if (callback != null && callback.isResult()) {

            ChatResponse<ResultNotSeenDuration> result = reformatNotSeenDuration(chatMessage, callback);

            if (result == null) {

                messageCallbacks.remove(messageUniqueId);

                getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId());

                return;

            }


            OutPutNotSeenDurations output = new OutPutNotSeenDurations();

            output.setResultNotSeenDuration(result.getResult());

            listenerManager.callOnGetNotSeenDuration(output);

            showLog("RECEIVE_NOT_SEEN_DURATION", chatMessage.getContent());

        }
//        else {
//
//            getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId());
//
//        }


    }

    private ChatResponse<ResultNotSeenDuration> reformatNotSeenDuration(ChatMessage chatMessage, Callback callback) {

        ChatResponse<ResultNotSeenDuration> response = new ChatResponse<>();

        Map<String, Long> idNotSeenPairs = new HashMap<>();

        JsonParser parser = new JsonParser();

        JsonObject jsonObject;

        try {
            jsonObject = parser
                    .parse(chatMessage.getContent())
                    .getAsJsonObject();

        } catch (Exception e) {
            e.printStackTrace();
            getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId());
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

    /**
     * Its check the Failed Queue {@link #checkMessageQueue()} to send all the message that is waiting to be send.
     */
    private void handleOnGetUserInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        if (callback.isResult()) {
            userInfoResponse = true;
            ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
            UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);
            String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
            showLog("RECEIVE_USER_INFO", userInfoJson);
            listenerManager.callOnUserInfo(userInfoJson, chatResponse);
            messageCallbacks.remove(messageUniqueId);

//            if there is a key its ok if not it will go for the key and then chat ready

            if (permit) {
                listenerManager.callOnChatState("CHAT_READY");
                chatReady = true;
                chatState = CHAT_READY;
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

    private String mainReplyMessage(String messageContent, long threadId, long messageId, String systemMetaData, Integer messageType, String metaData, String iUniqueId, ChatHandler handler) {


        String uniqueId = iUniqueId;

        if (iUniqueId == null)
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


        //todo need more check

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

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPLY_MESSAGE");
            stopTyping();
            if (handler != null) {
                handler.onReplyMessage(uniqueId);
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    private String getKey() {


        return mSecurePrefs.getString("KEY", null);
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
                    if (getUserInfoNumberOfTry < getUserInfoRetryCount - 1) {
                        if (retryStepUserInfo < 60) retryStepUserInfo *= 4;
                        getUserInfo(null);
                        getUserInfoNumberOfTry++;
                        runOnUIUserInfoThread(this, retryStepUserInfo * 1000);
                        showLog("getUserInfo " + " retry in " + retryStepUserInfo + " s ", "");
                    } else {
                        getErrorOutPut(ChatConstant.ERROR_CANT_GET_USER_INFO, ChatConstant.ERROR_CODE_CANT_GET_USER_INFO, getUserInfo(null));
                    }
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

            if (!Util.isNullOrEmpty(cacheParticipants))
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


        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

        long messageId = messageVO.getId();


        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageId);
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);

        String jsonDeleteMsg = gson.toJson(chatResponse);

        if (cache) {
            messageDatabaseHelper.deleteMessage(messageId, chatMessage.getSubjectId());
            showLog("DeleteMessage from dataBase with this messageId" + " " + messageId, "");
        }

        listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);
        showLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);
    }

    private void handleOutPutBlock(ChatMessage chatMessage, String messageUniqueId) {

        BlockedContact contact = gson.fromJson(chatMessage.getContent(), BlockedContact.class);
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

        if (cache) {
            messageDatabaseHelper.saveBlockedContact(contact, getExpireAmount());
        }


    }


    public String startTyping(RequestSignalMsg signalMsg) {

        return startSignalMessage(signalMsg, "SEND IS TYPING");

    }


    @Deprecated
    public boolean stopTyping(String uniqueId) {

        try {
            stopSignalHandlerThread();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    private void signalMessage(RequestSignalMsg requestSignalMsg, String logMessage, String uniqueId) {

        int signalType = requestSignalMsg.getSignalType();

        long threadId = requestSignalMsg.getThreadId();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", String.valueOf(signalType));

        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setContent(jsonObject.toString());
        chatMessage.setType(Constants.SYSTEM_MESSAGE);
        chatMessage.setToken(getToken());
        chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        sendAsyncMessage(gson.toJson(chatMessage), AsyncAckType.Constants.WITHOUT_ACK, logMessage);
    }

    private void handleClearHistory(ChatMessage chatMessage) {

        ChatResponse<ResultClearHistory> chatResponseClrHistory = new ChatResponse<>();

        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        chatResponseClrHistory.setSubjectId(chatMessage.getSubjectId());
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

        chatResponse.setSubjectId(chatMessage.getSubjectId());

        String responseJson = gson.toJson(chatResponse);

        OutputSetRoleToUser output = new OutputSetRoleToUser();

        output.setResultSetAdmin(resultSetAdmin);

        long threadId = chatMessage.getSubjectId();


        if (cache) {

            for (Admin a :
                    admins) {

                messageDatabaseHelper.updateParticipantRoles(a.getId(), threadId, a.getRoles());

            }

        }

        listenerManager.callOnSetRoleToUser(responseJson, chatResponse);

        showLog("RECEIVE_SET_ROLE", responseJson);
    }

    private void handleRemoveRole(ChatMessage chatMessage) {

        ChatResponse<ResultSetAdmin> chatResponse = new ChatResponse<>();

        ResultSetAdmin resultSetAdmin = new ResultSetAdmin();

        ArrayList<Admin> admins = gson
                .fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
                }.getType());

        resultSetAdmin.setAdmins(admins);

        chatResponse.setResult(resultSetAdmin);

        chatResponse.setUniqueId(chatMessage.getUniqueId());

        chatResponse.setSubjectId(chatMessage.getSubjectId());

        String responseJson = gson.toJson(chatResponse);

        OutputSetRoleToUser output = new OutputSetRoleToUser();

        output.setResultSetAdmin(resultSetAdmin);

        long threadId = chatMessage.getSubjectId();


        if (cache) {
            for (Admin a :
                    admins) {
                messageDatabaseHelper.updateParticipantRoles(a.getId(), threadId, a.getRoles());
            }
        }

        listenerManager.callOnRemoveRoleFromUser(responseJson, chatResponse);

        showLog("RECEIVE_REMOVE_ROLE", responseJson);
    }

    private void handleUnBlock(ChatMessage chatMessage, String messageUniqueId) {

        BlockedContact contact = gson.fromJson(chatMessage.getContent(), BlockedContact.class);
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

        if (cache) {

            messageDatabaseHelper.deleteBlockedContactById(contact.getBlockId());

        }

    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();

        List<BlockedContact> blockedContacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<BlockedContact>>() {
        }.getType());
        resultBlockList.setContacts(blockedContacts);
        chatResponse.setResult(resultBlockList);
        String jsonGetBlock = gson.toJson(chatResponse);
        listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);
        showLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);


        if (cache) {
            if (blockedContacts.size() > 0)
                messageDatabaseHelper.saveBlockedContacts(blockedContacts, getExpireAmount());
        }

    }

    private void handleOutPutRemoveParticipant(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

        String jsonRmParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOutPutGetHistory(Callback callback, ChatMessage chatMessage) {

        List<MessageVO> messageVOS = Mention.getMessageVOSFromChatMessage(chatMessage);

        if (cache) {

            if (handlerSend.get(chatMessage.getUniqueId()) != null) {

                notifyChatHistoryReceived(callback, chatMessage, messageVOS);

                return;
            }
        }

        publishChatHistoryServerResult(callback, chatMessage, messageVOS);
    }


    private void publishChatHistoryServerResult(Callback callback, ChatMessage chatMessage, List<MessageVO> messageVOS) {

        ResultHistory resultHistory = new ResultHistory();


        if (cache) {
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


        chatResponse.setSubjectId(chatMessage.getSubjectId());
        resultHistory.setHistory(messageVOS);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        String json = gson.toJson(chatResponse);
        listenerManager.callOnGetThreadHistory(json, chatResponse);
        showLog("RECEIVE_GET_HISTORY", json);
        messageCallbacks.remove(chatMessage.getUniqueId());
    }

    private void updateChatHistoryCache(Callback callback, ChatMessage chatMessage, List<MessageVO> messageVOS) {

        new java.lang.Thread(() -> {

            List<CacheMessageVO> cMessageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheMessageVO>>() {
            }.getType());

            messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, chatMessage.getSubjectId(), cMessageVOS);
            messageDatabaseHelper.saveHistory(cMessageVOS, chatMessage.getSubjectId());


        }).start();


    }

    private void notifyChatHistoryReceived(Callback callback, ChatMessage chatMessage, List<MessageVO> messageVOS) {

        ChatResponse<ResultHistory> chr = new ChatResponse<>();

        ResultHistory rh = new ResultHistory();

        rh.setHistory(messageVOS);

        chr.setResult(rh);

        Objects.requireNonNull(handlerSend.get(chatMessage.getUniqueId()))
                .onGetHistory(chr, chatMessage, callback);

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
            //added

            chatMessage.setTokenIssuer("1");

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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "GET_CONTACT_SEND");
            if (handler != null) {
                handler.onGetContact(uniqueId);
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;

    }

    @NonNull
    private String getErrorOutPut(String errorMessage, long errorCode, String uniqueId) {
        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
        String jsonError = gson.toJson(error);
        listenerManager.callOnError(jsonError, error);
//        listenerManager.callOnLogEvent(jsonError);
        showLog("Error", jsonError);

        if (log) {
            Log.e(TAG, "ErrorMessage: " + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);
        }
        return jsonError;
    }

    private String getTypeCode() {
        if (Util.isNullOrEmpty(typeCode)) {
            typeCode = "default";
        }
        return typeCode;
    }

    private void handleCacheThread(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, boolean isNew) {

        ChatResponse<ResultThreads> chatResponse = new ChatResponse<>();
        chatResponse.setCache(true);

        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;

        List<Thread> threads = messageDatabaseHelper.getThreadRaw(count, offset, threadIds, threadName, isNew);

        if (!Util.isNullOrEmpty(threads)) {

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
            showLog("CACHE_GET_THREAD", threadJson);
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
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_SEEN_MESSAGE_LIST");
            } catch (Throwable e) {
                if (log) Log.e(TAG, e.getCause().getMessage());
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    //TODO test cache
    private ChatResponse<ResultParticipant> reformatThreadParticipants(Callback callback, ChatMessage chatMessage) {


        ArrayList<Participant> participants = new ArrayList<>();

        if (!Util.isNullOrEmpty(chatMessage.getContent())) {

            try {
                participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        if (cache) {
            List<CacheParticipant> cacheParticipants = new ArrayList<>();

            if (!Util.isNullOrEmpty(chatMessage.getContent())) {

                try {
                    cacheParticipants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheParticipant>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }


//            Log.d("MTAG","Cache Participant to Save: " + cacheParticipants.toString() );

            if (!cacheParticipants.isEmpty())
                messageDatabaseHelper.saveParticipants(cacheParticipants, chatMessage.getSubjectId(), getExpireAmount());
        }

        ChatResponse<ResultParticipant> outPutParticipant = new ChatResponse<>();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        outPutParticipant.setUniqueId(chatMessage.getUniqueId());
        outPutParticipant.setSubjectId(chatMessage.getSubjectId());

        ResultParticipant resultParticipant = new ResultParticipant();

        resultParticipant.setContentCount(chatMessage.getContentCount());

        resultParticipant.setThreadId(chatMessage.getSubjectId());


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

        if (systemMetadata != null)
            sendingQueue.setSystemMetadata(systemMetadata);
        else
            sendingQueue.setSystemMetadata("");

        if (messageType != null)
            sendingQueue.setMessageType(messageType);
        else
            sendingQueue.setMessageType(0);

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
        chatMessage.setSystemMetadata(systemMetadata);
        chatMessage.setMessageType(messageType);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTypeCode(typeCode != null ? typeCode : getTypeCode());

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetadata)) {
            jsonObject.remove("systemMetadata");
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TXT_MSG_WITH_FILE");
            stopTyping();
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
            if (log) Log.e(TAG, e.getMessage());
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


    private void getPhoneContact(Context context, OnContactLoaded listener) {


        try {

            Log.i(TAG, ">>> Getting phone contacts " + new Date());


            List<PhoneContact> cachePhoneContacts = new ArrayList<>();

//            List<PhoneContact> cachePhoneContacts = phoneContactDbHelper.getPhoneContacts();

            PhoneContactAsyncTask task = new PhoneContactAsyncTask(phoneContactDbHelper, contacts -> {

                String firstName;
                String phoneNumber;
                String lastName;
                String empty = "";
                int version;
                ArrayList<PhoneContact> newPhoneContact = new ArrayList<>();

                Log.d(TAG, "#" + contacts.size() + " Contacts Loaded From Cache");

                cachePhoneContacts.addAll(contacts);

                HashMap<String, PhoneContact> mapCacheContactKeeper = new HashMap<>();

                if (cachePhoneContacts.size() > 0) {
                    for (PhoneContact contact : cachePhoneContacts) {
                        mapCacheContactKeeper.put(contact.getPhoneNumber(), contact);
                    }
                }

                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                if (cursor == null)
                    throw new AssertionError();


                while (cursor.moveToNext()) {
                    firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    lastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    version = cursor.getInt(cursor.getColumnIndex(ContactsContract.RawContacts.VERSION));
                    phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    PhoneContact phoneContact = new PhoneContact();

                    if (!Util.isNullOrEmpty(phoneNumber)) {

                        phoneContact.setPhoneNumber(phoneNumber);

                        if (!Util.isNullOrEmpty(firstName)) {
                            phoneContact.setName(firstName);
                        } else {
                            phoneContact.setName(empty);
                        }

                        if (!Util.isNullOrEmpty(lastName)) {
                            phoneContact.setLastName(lastName);
                        } else {
                            phoneContact.setLastName(empty);
                        }
                        if (!Util.isNullOrEmpty(version)) {
                            phoneContact.setVersion(version);
                        }
                        if (cachePhoneContacts.size() > 0) {
                            // if its not in PhoneContactCache and its a contact that added recently
                            if (mapCacheContactKeeper.get(phoneNumber) != null) {

                                if (version != mapCacheContactKeeper.get(phoneNumber).getVersion()) {
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


                Log.d(TAG, "#" + newPhoneContact.size() + " New Contact Found");

                listener.onLoad(newPhoneContact);

            });

            task.execute();

        } catch (Exception e) {
            Log.e(TAG, e.getCause().getMessage());
        }

    }


    private static class PhoneContactAsyncTask extends AsyncTask<Void, Void, List<PhoneContact>> {

        private PhoneContactDbHelper pcDbHelper;

        private OnContactLoaded listener;

        PhoneContactAsyncTask(PhoneContactDbHelper dbHelper, OnContactLoaded onContactLoaded) {

            pcDbHelper = dbHelper;

            listener = onContactLoaded;
        }


        @Override
        protected List<PhoneContact> doInBackground(Void... voidd) {


            try {
                return pcDbHelper.getPhoneContacts();
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }

        }

        @Override
        protected void onPostExecute(List<PhoneContact> contacts) {
            super.onPostExecute(contacts);

            listener.onLoad(contacts);

        }
    }


    interface OnContactLoaded {

        void onLoad(List<PhoneContact> contacts);
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


        JsonObject metadata = (JsonObject) gson.toJsonTree(metaDataFile);

        metadata.addProperty("name", file.getName());
        metadata.addProperty("id", fileId);


        return metadata.toString();
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


            JsonObject metaDataWithName = (JsonObject) gson.toJsonTree(locationFile);

            metaDataWithName.addProperty("name", originalName);
            metaDataWithName.addProperty("id", imageId);

            return metaDataWithName.toString();

        } else {


            MetaDataImageFile metaData = new MetaDataImageFile();
            metaData.setFile(fileMetaData);


            JsonObject metaDataWithName = (JsonObject) gson.toJsonTree(metaData);

            metaDataWithName.addProperty("name", originalName);
            metaDataWithName.addProperty("id", imageId);

            return metaDataWithName.toString();
        }
    }


    /**
     * Add list of contacts with their mobile numbers and their cellphoneNumbers
     */
    private void addContacts(List<PhoneContact> phoneContacts, String uniqueId) {


        HandlerThread handlerThread = new HandlerThread("contacts-thread");
        handlerThread.start();


        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> cellphoneNumbers = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> typeCodes = new ArrayList<>();

        for (PhoneContact contact : phoneContacts) {
            firstNames.add(contact.getName());
            lastNames.add(contact.getLastName());


            String phoneNum = String.valueOf(contact.getPhoneNumber());

            if (phoneNum.startsWith("9")) {
                phoneNum = phoneNum.replaceFirst("9", "09");
            }

            cellphoneNumbers.add(phoneNum);
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

            Log.d(TAG, "Call to add contact");

            addContactsObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(contactsResponse -> {

                        Log.i(TAG, ">>> Server Respond at " + new Date());

                        boolean error = false;

                        if (contactsResponse.body() != null) {
                            error = contactsResponse.body().getHasError();
                        }

                        if (contactsResponse.isSuccessful()) {

                            if (error) {
                                getErrorOutPut(contactsResponse.body().getMessage(), contactsResponse.body().getErrorCode()
                                        , uniqueId);


                                //successful response
                            } else {

                                Contacts contacts = contactsResponse.body();
                                ChatResponse<Contacts> chatResponse = new ChatResponse<>();

                                chatResponse.setResult(contacts);
                                chatResponse.setUniqueId(uniqueId);

                                String contactsJson = gson.toJson(chatResponse);

                                if (cache) {

                                    try {
                                        new Handler(handlerThread.getLooper()).post(() -> messageDatabaseHelper.saveContacts(chatResponse.getResult().getResult(), getExpireAmount()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                listenerManager.callOnSyncContact(contactsJson, chatResponse);

                                try {
                                    new Handler(handlerThread.getLooper()).post(() -> {
                                        try {
                                            phoneContactDbHelper.addPhoneContacts(phoneContacts);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                showLog("SYNC_CONTACT_COMPLETED", contactsJson);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    handlerThread.quitSafely();
                                } else {
                                    handlerThread.quit();
                                }
                            }
                        }
                    }, throwable ->
                            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId));


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
     * mimeType       mime messageType of the file
     * systemMetaData metadata of the message
     * messageType    messageType of a message
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

                addToUploadQueue(description, fileUri, messageType, threadId, uniqueId, systemMetaData, messageId, mimeType, center, methodName, file, fileSize);

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

            getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
//            listenerManager.callOnLogEvent(jsonError);
        }
    }


    private void mainUploadImageFileMsg(LFileUpload fileUpload) {

        JsonObject jsonLog = new JsonObject();

        String description = fileUpload.getDescription();

        jsonLog.addProperty("description", description);

        ProgressHandler.sendFileMessage handler = fileUpload.getHandler();

        Integer messageType = fileUpload.getMessageType();

        jsonLog.addProperty("messageType", messageType);

        long threadId = fileUpload.getThreadId();

        jsonLog.addProperty("threadId", threadId);

        String uniqueId = fileUpload.getUniqueId();

        jsonLog.addProperty("uniqueId", uniqueId);

        String systemMetaData = fileUpload.getSystemMetaData();

        jsonLog.addProperty("systemMetaData", systemMetaData);

        long messageId = fileUpload.getMessageId();

        jsonLog.addProperty("messageId", messageId);

        String mimeType = fileUpload.getMimeType();

        jsonLog.addProperty("mimeType", mimeType);

        String methodName = fileUpload.getMethodName();

        jsonLog.addProperty("methodName", methodName);

        long fileSize = fileUpload.getFileSize();

        jsonLog.addProperty("fileSize", fileSize);

        String center = fileUpload.getCenter();

        jsonLog.addProperty("center", center);

        File file = fileUpload.getFile();

        if (chatReady) {

            if (fileServer != null) {

                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());

                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

                try {
                    showLog("UPLOAD_FILE_TO_SERVER", getJsonForLog(jsonLog));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId,

                        new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

                                Log.i(TAG, "on progress");
                                if (handler != null) {
                                    handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
                                    if (log)
                                        Log.i(TAG, "uniqueId " + uniqueId + " bytesSent " + bytesSent);
                                } else {

                                    if (log)
                                        Log.i(TAG, "Handler is null");

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
//                            listenerManager.callOnLogEvent(jsonError);
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

                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);
//                            if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
//                            listenerManager.callOnLogEvent(imageJson);
                            String metaJson;
                            if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_LOCATION_MSG)) {
                                metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                        , result.getActualWidth(), mimeType, fileSize, null, true, center);

                            } else {
                                metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                        , result.getActualWidth(), mimeType, fileSize, null, false, null);
                            }
//
                            JsonObject js = new JsonObject();
                            js.addProperty("metadata", metaJson);
                            js.addProperty("uniqueId", uniqueId);

                            //remove from Uploading Queue
                            removeFromUploadQueue(uniqueId);

                            // send to handler
                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {

                                showLog("SEND_REPLY_FILE_MESSAGE", getJsonForLog(js));

                                mainReplyMessage(description, threadId, messageId, systemMetaData, messageType, metaJson, uniqueId, null);
//                                if (log) Log.i(TAG, "SEND_REPLY_FILE_MESSAGE");
//                                listenerManager.callOnLogEvent(metaJson);
                            } else {
                                sendTextMessageWithFile(description, threadId, metaJson, systemMetaData, uniqueId, typeCode, messageType);
                            }
//                            listenerManager.callOnLogEvent(metaJson);
                        }
                    }
                }, throwable -> {

                    try {
                        String jsonError = getErrorOutPut(throwable.getMessage()
                                , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);


                        ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage()
                                , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

                        if (log) Log.e(TAG, jsonError);
                        if (handler != null) {
                            handler.onError(jsonError, error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private void removeFromUploadQueue(String uniqueId) {
        if (cache) {
            messageDatabaseHelper.deleteUploadingQueue(uniqueId);
        } else {
            uploadingQList.remove(uniqueId);
        }
    }

    private String getJsonForLog(JsonObject jsonLog) {
        return gson.toJson(jsonLog);
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

                    addToUploadQueue(description, fileUri, messageType, threadId, uniqueId, systemMetadata, mimeType, file, file_size);

//                    showLog("Message with this" + "  uniqueId  " + uniqueId + "  has been added to Uploading Queue", null);

                    lFileUpload.setFileSize(file_size);

                    lFileUpload.setFile(file);

                    mainUploadFileMessage(lFileUpload);

                } else {
                    if (log) Log.e(TAG, "File Is Not Exist");
                }
            } else {
                Permission.Request_STORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
                String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                        , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                if (log) Log.e(TAG, jsonError);
            }
        } catch (Throwable e) {
            if (log) Log.e(TAG, e.getCause().getMessage());
        }
    }

    private void addToUploadQueue(String description, Uri fileUri, Integer messageType, long threadId, String uniqueId, String systemMetadata, String mimeType, File file, long file_size) {
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
    }


    private void addToUploadQueue(String description, Uri fileUri, Integer messageType, long threadId, String uniqueId, String systemMetaData, long messageId, String mimeType, String center, String methodName, File file, long fileSize) {

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
    }


    private void addToUploadQueue(Integer chatMessageType, String message, String uniqueId) {

        UploadingQueueCache uploadingQueue = new UploadingQueueCache();
        uploadingQueue.setMessageType(chatMessageType);
        uploadingQueue.setUniqueId(uniqueId);
        uploadingQueue.setMessage(message);


        if (cache) {
            messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
        } else {
            uploadingQList.put(uniqueId, uploadingQueue);
        }
    }


    private void mainUploadFileMessage(LFileUpload lFileUpload) {


        JsonObject jsonLog = new JsonObject();

        String description = lFileUpload.getDescription();

        jsonLog.addProperty("description", description);


        Integer messageType = lFileUpload.getMessageType();

        jsonLog.addProperty("messageType", messageType);

        long threadId = lFileUpload.getThreadId();

        jsonLog.addProperty("threadId", threadId);

        String uniqueId = lFileUpload.getUniqueId();

        jsonLog.addProperty("uniqueId", uniqueId);

        String systemMetadata = lFileUpload.getSystemMetaData();

        jsonLog.addProperty("systemMetadata", systemMetadata);

        long messageId = lFileUpload.getMessageId();

        jsonLog.addProperty("messageId", messageId);

        String mimeType = lFileUpload.getMimeType();
        jsonLog.addProperty("mimeType", mimeType);

        File file = lFileUpload.getFile();

        ProgressHandler.sendFileMessage handler = lFileUpload.getHandler();

        long file_size = lFileUpload.getFileSize();

        jsonLog.addProperty("file_size", file_size);

        String methodName = lFileUpload.getMethodName();

        jsonLog.addProperty("methodName", methodName);

        jsonLog.addProperty("name", file.getName());

        showLog("UPLOADING_FILE_TO_SERVER", getJsonForLog(jsonLog));


        if (chatReady) {
            if (getFileServer() != null) {
                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);

                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {

                    @Override
                    public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

                        if (handler != null)
                            handler.onProgressUpdate(uniqueId, bytesSent, totalBytesSent, totalBytesToSend);
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

                Subscription subscription = uploadObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(fileUploadResponse -> {
                            if (fileUploadResponse.isSuccessful() && fileUploadResponse.body() != null) {
                                boolean error = fileUploadResponse.body().isHasError();
                                if (error) {
                                    String errorMessage = fileUploadResponse.body().getMessage();
                                    if (log) Log.e(TAG, errorMessage);

                                    if (handler != null) {

                                        String jsonError = getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                        ErrorOutPut errorOutPut = new ErrorOutPut(true, ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                        handler.onError(jsonError, errorOutPut);

                                    }

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


                                        showLog("FILE_UPLOADED_TO_SERVER", json);

                                        listenerManager.callOnUploadFile(json, chatResponse);

                                        if (handler != null) {

                                            handler.onFinishFile(json, chatResponse);
                                        }

                                        String jsonMeta = createFileMetadata(file, hashCode, fileId, mimeType, file_size, "");

//                                        listenerManager.callOnLogEvent(jsonMeta);
                                        if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {

                                            showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);

                                            mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, uniqueId, null);
//                                            if (log) Log.i(TAG, "SEND_REPLY_FILE_MESSAGE");

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
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            listenerManager.callOnLogEvent(jsonError);
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
                        ArrayList<Device> devices = null;
                        if (deviceResults.body() != null) {
                            devices = deviceResults.body().getDevices();
                        }
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


            // get threads summary shouldn't update cache
            if (!handlerSend.containsKey(chatMessage.getUniqueId()))
                messageDatabaseHelper.saveThreads(threads);
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
            try {
                if (log)
                    Log.e(TAG, throwable.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {

            if (handlerSend.get(chatMessage.getUniqueId()) != null) {

                Objects.requireNonNull(handlerSend.get(chatMessage.getUniqueId()))
                        .onGetHistory(chatMessage.getUniqueId());

            }

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
        CoreConfig.token = token;

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

//    public void setSignalIntervalTime(int signalIntervalTime) {
//        this.signalIntervalTime = signalIntervalTime;
//    }


    @Override
    public void onDisconnected(String textMessage) {
        super.onDisconnected(textMessage);
        getErrorOutPut("On Async Disconnected: " + textMessage, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");
    }

    @Override
    public void onError(String textMessage) {
        super.onError(textMessage);

        getErrorOutPut("On Async Error: " + textMessage, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");

    }

    @Override
    public void handleCallbackError(Throwable cause) {
        super.handleCallbackError(cause);

        try {

            getErrorOutPut("Async Callback Error: " + cause.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

