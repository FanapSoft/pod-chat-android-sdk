package com.fanap.podchat.chat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.fanap.podchat.BuildConfig;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.GapMessageVO;
import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.chat.bot.BotManager;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.bot.result_model.StartStopBotResult;
import com.fanap.podchat.chat.contact.ContactManager;
import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.chat.file_manager.download_file.PodDownloader;
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.chat.file_manager.upload_file.PodUploader;
import com.fanap.podchat.chat.file_manager.upload_file.UploadToPodSpaceResult;
import com.fanap.podchat.chat.mention.Mention;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.MessageManager;
import com.fanap.podchat.chat.messge.RequestGetUnreadMessagesCount;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.participant.ParticipantsManager;
import com.fanap.podchat.chat.pin.pin_message.PinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.PinThread;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread;
import com.fanap.podchat.chat.ping.PingManager;
import com.fanap.podchat.chat.ping.request.StatusPingRequest;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.search.SearchManager;
import com.fanap.podchat.repository.CacheDataSource;
import com.fanap.podchat.repository.ChatDataSource;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.chat.thread.public_thread.PublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.profile.UserProfile;
import com.fanap.podchat.chat.user.user_roles.UserRoles;
import com.fanap.podchat.chat.user.user_roles.model.CacheUserRoles;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.localmodel.LFileUpload;
import com.fanap.podchat.localmodel.SetRuleVO;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ChatMessageContent;
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
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.mainmodel.UpdateContact;
import com.fanap.podchat.mainmodel.UserInfo;
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
import com.fanap.podchat.model.ResultThreadsSummary;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.networking.ProgressRequestBody;
import com.fanap.podchat.networking.ProgressResponseBody;
import com.fanap.podchat.networking.api.ContactApi;
import com.fanap.podchat.networking.api.FileApi;
import com.fanap.podchat.networking.api.MapApi;
import com.fanap.podchat.networking.api.SSOApi;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperFileServer;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperMap;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperPlatformHost;
import com.fanap.podchat.networking.retrofithelper.RetrofitHelperSsoHost;
import com.fanap.podchat.networking.retrofithelper.TimeoutConfig;
import com.fanap.podchat.notification.CustomNotificationConfig;
import com.fanap.podchat.notification.PodNotificationManager;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.persistance.PhoneContactDbHelper;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.persistance.module.AppDatabaseModule;
import com.fanap.podchat.persistance.module.AppModule;
import com.fanap.podchat.persistance.module.DaggerMessageComponent;
import com.fanap.podchat.repository.MemoryDataSource;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestBlock;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestCreateThreadWithMessage;
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
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType.Constants;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.FileUtils;
import com.fanap.podchat.util.NetworkUtils.NetworkPingSender;
import com.fanap.podchat.util.NetworkUtils.NetworkStateListener;
import com.fanap.podchat.util.NetworkUtils.NetworkStateReceiver;
import com.fanap.podchat.util.OnWorkDone;
import com.fanap.podchat.util.Permission;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.PodThreadManager;
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

import io.sentry.core.Breadcrumb;
import io.sentry.core.Sentry;
import io.sentry.core.SentryLevel;
import io.sentry.core.protocol.User;
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
import rx.subjects.PublishSubject;

import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.ASYNC_READY;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CLOSED;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CLOSING;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CONNECTING;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.OPEN;

@SuppressWarnings("unchecked")
public class Chat extends AsyncAdapter {
    private static final String MTAG = "MTAG";
    public static final String PING = "PING";
    public static final int READ_EXTERNAL_STORAGE_CODE = 1007;
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
    private String podSpaceServer;
    private static
    Chat instance;
    private static SecurePreferences mSecurePrefs;
    private static ChatListenerManager listenerManager;
    private long userId;
    private ContactApi contactApi;
    private static HashMap<String, Callback> messageCallbacks;
    private static HashMap<Long, ArrayList<Callback>> threadCallbacks;
    public static final String TAG = "CHAT_SDK";
    private String chatState = "CLOSED";
    private boolean isWebSocketNull = true;


    private int getUserInfoRetryCount = 5;

    private int getUserInfoNumberOfTry = 0;

    private long maxReconnectStepTime = 64000;

    private long connectNumberOfRetry = 1000;


    private NetworkPingSender.NetworkStateConfig networkStateConfig;


//    private Map<Long, LinkedHashMap<String, Handler>> threadSignalsManager = new HashMap<>();

    private HashMap<String, Long> downloadQList = new HashMap<>();
    private HashMap<String, Call> downloadCallList = new HashMap<>();

    private HashMap<String, Handler> signalHandlerKeeper = new HashMap<>();
    private HashMap<String, RequestSignalMsg> requestSignalsKeeper = new HashMap<>();
    private HashMap<Long, ArrayList<String>> threadSignalsKeeper = new HashMap<>();
    private static JsonParser parser;
    private static HashMap<String, ChatHandler> handlerSend;
    private static HashMap<String, SendingQueueCache> sendingQList;
    private static HashMap<String, UploadingQueueCache> uploadingQList;
    private static HashMap<String, WaitQueueCache> waitQList;
    private HashMap<String, ThreadManager.IThreadInfoCompleter> threadInfoCompletor = new HashMap<>();

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
    private boolean connectionPing = false;
    private boolean userInfoResponse = false;
    private long ttl;
    private String ssoHost;
    private boolean isNetworkStateListenerEnable = true;


    private static NetworkStateReceiver networkStateReceiver;

    private NetworkPingSender pinger;

    private static HandlerThread signalMessageHandlerThread = null;
//    private static HandlerThread signalMessageHandlerThread = null;

    @Inject
    public MessageDatabaseHelper messageDatabaseHelper;

    @Inject
    public PhoneContactDbHelper phoneContactDbHelper;

    BroadcastReceiver fcmRefreshTokenReceiver;

    private String socketAddress;
    private String appId;
    private String serverName;
    private boolean hasFreeSpace = true;

    static ChatDataSource dataSource;

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

            runDatabase(context);

            sendingQList = new HashMap();
            uploadingQList = new HashMap();
            waitQList = new HashMap<>();


            messageCallbacks = new HashMap<>();
            handlerSend = new HashMap<>();
            gson = new GsonBuilder().create();


            Sentry.setExtra("chat-sdk-version-name", BuildConfig.VERSION_NAME);
            Sentry.setExtra("chat-sdk-version-code", String.valueOf(BuildConfig.VERSION_CODE));
            Sentry.setExtra("chat-sdk-build-type", BuildConfig.BUILD_TYPE);

            dataSource = new ChatDataSource(new MemoryDataSource(), new CacheDataSource(context, instance.getKey()));
        }

        return instance;
    }


    /**
     * Setting notification essential configuration.
     *
     * @param notificationConfig Includes parameters to create channel and show notification.
     */

    public void setupNotification(CustomNotificationConfig notificationConfig) {


        PodNotificationManager.listenLogs(new PodNotificationManager.IPodNotificationManager() {
            @Override
            public void onNotificationEvent(String log) {
                showLog(log);
            }

            @Override
            public void onNotificationError(String log) {

                captureError(log, ChatConstant.ERROR_CODE_NOTIFICATION_ERROR, "");

            }

            @Override
            public void sendAsyncMessage(String message, String info) {

                if (chatReady) {
                    showLog(info, message);
                    async.sendMessage(message, AsyncAckType.Constants.WITHOUT_ACK);
                }

            }
        });

        PodNotificationManager.withConfig(notificationConfig, context);

        PodNotificationManager.registerFCMTokenReceiver(context);

        PodNotificationManager.registerClickReceiver(context);

    }

    public void shouldShowNotification(boolean shouldShow) {

        PodNotificationManager.setShouldShowNotification(shouldShow);

    }

    public void deliverNotification(String threadId) {

        PodNotificationManager.deliverThreadMessages(threadId);

    }

    public void clearAllNotifications() {

        PodNotificationManager.clearNotifications(context);

    }


    private static void runDatabase(Context context) {

        if (Util.isNullOrEmpty(instance.getKey())) {
            String key = generateUniqueId();
            instance.setKey(key);
        }

        try {
            DaggerMessageComponent.builder()
                    .appDatabaseModule(new AppDatabaseModule(context, instance.getKey()))
                    .appModule(new AppModule(context))
                    .build()
                    .inject(instance);

            permit = true;

        } catch (Exception e) {
            instance.showErrorLog("Exception init database");
            permit = false;
            cache = false;
        }

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

            pinger = new NetworkPingSender(context, new NetworkStateListener() {
                @Override
                public void networkAvailable() {

                    tryToConnectOrReconnect();

                }

                @Override
                public void networkUnavailable() {

                    closeSocketServer();

                }

                @Override
                public void sendPingToServer() {

                    pingForCheckConnection();

                }

                @Override
                public void onConnectionLost() {

                    chatState = CLOSED;

                    scheduleForReconnect();

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

            registerNetworkReceiver();

        }
    }

    public void registerNetworkReceiver() {

        try {
            if (networkStateReceiver != null)
                context.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (IllegalArgumentException e) {
            showErrorLog(e.getMessage());
            showErrorLog("Registering Receiver failed");
        }
    }

    public void unregisterNetworkReceiver() {

        try {
            if (networkStateReceiver != null)
                context.unregisterReceiver(networkStateReceiver);
        } catch (IllegalArgumentException e) {
            showErrorLog(e.getMessage());
            showErrorLog("Unregistering Receiver failed");
        }

    }


    public void killChat() {

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void closeChat() {
        try {
            stopTyping();
            context.unregisterReceiver(networkStateReceiver);
            if (pinger != null) pinger.stopPing();
            closeSocketServer();
            PodNotificationManager.unRegisterReceiver(context);
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


            chatState = CLOSED;

            scheduleForReconnect();


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
                        || shouldReconnectAtOpenState() || shouldReconnectAtConnectingState();

                if (shouldReconnect) {

                    async.connect(socketAddress, appId, serverName, token, ssoHost, "");

                } else if (!chatReady) {
                    if (isAsyncReady()) {
                        pingAfterSetToken();
                    }
                }
                connectHandler.postDelayed(this, connectNumberOfRetry);
            }
        }, connectNumberOfRetry);

    }

    private boolean shouldReconnectAtOpenState() {
        return TextUtils.equals(chatState, OPEN) && connectNumberOfRetry == maxReconnectStepTime;
    }

    private boolean shouldReconnectAtConnectingState() {
        return TextUtils.equals(chatState, CONNECTING) && connectNumberOfRetry >= (maxReconnectStepTime / 2);
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
            case Constants.LOCATION_PING:
                handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;


            case Constants.CONTACT_SYNCED: {
                handleOnContactsSynced(chatMessage);
                break;
            }


            case Constants.CREATE_BOT: {
                handleOnBotCreated(chatMessage);
                break;
            }
            case Constants.DEFINE_BOT_COMMAND: {
                handleOnBotCommandDefined(chatMessage);
                break;
            }
            case Constants.START_BOT: {
                handleOnBotStarted(chatMessage);
                break;
            }
            case Constants.STOP_BOT: {
                handleOnBotStopped(chatMessage);
                break;
            }


            case Constants.REGISTER_FCM_USER_DEVICE: {
                PodNotificationManager.handleOnUserAndDeviceRegistered(chatMessage, context);
                break;
            }

            case Constants.UPDATE_FCM_APP_USERS_DEVICE: {
                PodNotificationManager.handleOnFCMTokenRefreshed(chatMessage, context);
                break;
            }


            case Constants.ALL_UNREAD_MESSAGE_COUNT:
                handleOnGetUnreadMessagesCount(chatMessage);
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

                } else {
                    handleOnGetThreadHistory(callback, chatMessage);
                }
                break;
            case Constants.GET_THREADS: {
                if (threadInfoCompletor.containsKey(messageUniqueId)) {
                    threadInfoCompletor.get(messageUniqueId)
                            .onThreadInfoReceived(chatMessage);
                } else if (callback == null) {
                    handleGetThreads(null, chatMessage, messageUniqueId);
                } else handleResponseMessage(callback, chatMessage, messageUniqueId);
                break;
            }

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

            case Constants.LAST_MESSAGE_DELETED: {
                handleOnLastMessageDeleted(chatMessage);
                break;
            }
            case Constants.LAST_MESSAGE_EDITED: {
                handleOnLastMessageEdited(chatMessage);
                break;
            }
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

    private void handleOnContactsSynced(ChatMessage chatMessage) {

        ChatResponse<ContactSyncedResult> response = ContactManager.prepareContactSyncedResult(chatMessage);

        listenerManager.callOnContactsSynced(response);

        showLog("ON_CONTACTS_SYNCED", gson.toJson(chatMessage));
    }

    private void handleOnBotStopped(ChatMessage chatMessage) {

        ChatResponse<StartStopBotResult> response = BotManager
                .handleOnBotStartedOrStopped(chatMessage);

        listenerManager.callOnBotStopped(response);


        showLog("ON_BOT_STOPPED", gson.toJson(chatMessage));

    }

    private void handleOnBotStarted(ChatMessage chatMessage) {

        ChatResponse<StartStopBotResult> response = BotManager
                .handleOnBotStartedOrStopped(chatMessage);


        listenerManager.callOnBotStarted(response);


        showLog("ON_BOT_STARTED", gson.toJson(chatMessage));
    }

    private void handleOnBotCommandDefined(ChatMessage chatMessage) {

        ChatResponse<DefineBotCommandResult> response =
                BotManager.handleOnBotCommandDefined(chatMessage);


        listenerManager.callOnBotCommandsDefined(response);

        showLog("ON_BOT_COMMANDS_DEFINED", gson.toJson(chatMessage));
    }

    private void handleOnBotCreated(ChatMessage chatMessage) {

        ChatResponse<CreateBotResult> response =
                BotManager.handleOnBotCreated(chatMessage);

        listenerManager.callOnBotCreated(response);

        showLog("ON_BOT_CREATED", gson.toJson(chatMessage));

    }

    private void handleOnGetUnreadMessagesCount(ChatMessage chatMessage) {

        ChatResponse<ResultUnreadMessagesCount> response =
                MessageManager.handleUnreadMessagesResponse(chatMessage);

        listenerManager.callOnGetUnreadMessagesCount(response);

        showLog("ON GET UNREAD MESSAGES COUNT", gson.toJson(chatMessage));


    }

    private void handleOnJoinPublicThread(ChatMessage chatMessage) {

        ChatResponse<ResultJoinPublicThread> response = PublicThread.handleJoinThread(chatMessage);

        listenerManager.callOnJoinPublicThread(response);

        showLog("ON JOIN PUBLIC THREAD", gson.toJson(chatMessage));


        if (cache) {

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

        ChatResponse<ResultNotSeenDuration> response = ContactManager.prepareUpdateLastSeenResponse(chatMessage, parser);


        showLog("LAST SEEN UPDATED", gson.toJson(chatMessage));


        listenerManager.callOnContactsLastSeenUpdated(response);

        listenerManager.callOnContactsLastSeenUpdated(chatMessage.getContent());


    }

    private void handleOnGetMentionList(ChatMessage chatMessage) {

        ChatResponse<ResultHistory> response = Mention.getMentionListResponse(chatMessage);
        listenerManager.callOnGetMentionList(response);
        showLog("RECEIVED MENTION LIST", gson.toJson(response));


        if (cache) {

            dataSource.saveMessageResultFromServer(response.getResult().getHistory(), chatMessage.getSubjectId());
//            messageDatabaseHelper.saveMessageHistory(response.getResult().getHistory(), chatMessage.getSubjectId());

        }

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
        showLog("MESSAGE_UNPINNED", gson.toJson(response));
        if (cache) {
            messageDatabaseHelper.deletePinnedMessageByThreadId(chatMessage.getSubjectId());
        }
    }

    private void handleOnStatusPingSent(ChatMessage chatMessage) {

        ChatResponse<StatusPingResult> response = PingManager.handleOnPingStatusSent(chatMessage);

        listenerManager.callOnPingStatusSent(response);

        showLog("RECEIVE_PING_STATUS_SENT", gson.toJson(response));

    }

    private void handleOnGetUserRoles(ChatMessage chatMessage) {

        ChatResponse<ResultCurrentUserRoles> response = UserRoles.handleOnGetUserRoles(chatMessage);

        listenerManager.callOnGetUserRoles(response);

        showLog("RECEIVE_CURRENT_USER_ROLES", gson.toJson(response));


        if (cache) {
            messageDatabaseHelper.saveCurrentUserRoles(response);
        }

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
        String podSpaceUrl = requestConnect.getPodSpaceServer();


        connect(socketAddress, appId, severName, token, ssoHost, platformHost, fileServer, podSpaceUrl, typeCode);
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
                        String ssoHost, String platformHost, String fileServer, String podSpaceServer,
                        String typeCode) {
        try {

            Sentry.setExtra("token", token);
            Sentry.setExtra("typeCode", typeCode);
            Sentry.setExtra("socketAddress", socketAddress);
            Sentry.setExtra("appId", appId);
            Sentry.setExtra("serverName", serverName);
            Sentry.setExtra("platformHost", platformHost);
            Sentry.setExtra("ssoHost", ssoHost);
            Sentry.setExtra("fileServer", fileServer);
            Sentry.setExtra("podSpaceServer", podSpaceServer);

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
                setPodSpaceServer(podSpaceServer);

                connectToAsync(socketAddress, appId, serverName, token, ssoHost);

                setupNetworkStateListener();

                scheduleForReconnect();


            } else {
                captureError("PlatformHost " + ChatConstant.ERROR_CHECK_URL
                        , ChatConstant.ERROR_CODE_CHECK_URL, null);

            }
        } catch (Throwable throwable) {
            if (log) {
                showLog("CONNECTION_ERROR", throwable.getMessage());
            }
        }
    }

    private void setPodSpaceServer(String podSpaceServer) {
        this.podSpaceServer = podSpaceServer;
    }

    private void setupNetworkStateListener() {
        if (isNetworkStateListenerEnable) enableNetworkStateListener();
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

            JsonObject jsonObject = (MessageManager.prepareSendTextMessageRequest(textMessage, threadId, messageType, jsonSystemMetadata, uniqueId, getTypeCode(), getToken()));

            SendingQueueCache sendingQueue = new SendingQueueCache();
            sendingQueue.setSystemMetadata(jsonSystemMetadata);
            sendingQueue.setMessageType(messageType);
            sendingQueue.setThreadId(threadId);
            sendingQueue.setUniqueId(uniqueId);
            sendingQueue.setMessage(textMessage);

            asyncContentWaitQueue = jsonObject.toString();

            sendingQueue.setAsyncContent(asyncContentWaitQueue);

            insertToSendQueue(uniqueId, sendingQueue);

            if (log) {
                Log.i(TAG, "Message with this" + "  uniqueId  " + uniqueId + "  has been added to Message Queue");
            }
            if (chatReady) {

                if (handler != null) {
                    handler.onSent(uniqueId, threadId);
                    handler.onSentResult(null);
                    handlerSend.put(uniqueId, handler);
                }

                moveFromSendingQueueToWaitQueue(uniqueId, sendingQueue);

                setThreadCallbacks(threadId, uniqueId);
                sendAsyncMessage(asyncContentWaitQueue, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE");
                stopTyping();
            } else {
                onChatNotReady(uniqueId);
            }
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
            onUnknownException(uniqueId, throwable);
        }
        return uniqueId;
    }

    private void insertToSendQueue(String uniqueId, SendingQueueCache sendingQueue) {
        if (cache) {
//            messageDatabaseHelper.insertSendingMessageQueue(sendingQueue);
            dataSource.addToSendingQueue(sendingQueue);
        } else {
            sendingQList.put(uniqueId, sendingQueue);
        }
    }

    public String sendStatusPing(StatusPingRequest request) {

        String uniqueId = generateUniqueId();
        if (chatReady) {
            String message;
            try {
                message = PingManager.createStatusPingRequest(request, uniqueId);

                setCallBacks(null, null, null, true, Constants.LOCATION_PING, null, uniqueId);

            } catch (PodChatException e) {
                captureError(e);
                return uniqueId;
            }
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_STATUS_PING");
        } else {
            onChatNotReady(uniqueId);
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

        String uniqueId = generateUniqueId();

        if (chatReady) {

            setCallBacks(null, null, null, true, Constants.SET_ROLE_TO_USER, null, uniqueId);
            String asyncContent = ContactManager.prepareSetRoleRequest(request, uniqueId, getTypeCode(), getToken(), String.valueOf(TOKEN_ISSUER));
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


    /*
    Bot
     */
    public String createBot(CreateBotRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = null;
            try {
                message = BotManager.createCreateBotRequest(request, uniqueId);
            } catch (PodChatException e) {
                new PodThreadManager().doThisAndGo(() -> {
                    e.setUniqueId(uniqueId);
                    e.setToken(getToken());
                    captureError(e);
                });
                return uniqueId;
            }

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_BOT_REQUEST");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;


    }

    public String addBotCommand(DefineBotCommandRequest request) {


        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = null;
            try {
                message = BotManager.createDefineBotCommandRequest(request, uniqueId);
            } catch (PodChatException e) {
                new PodThreadManager().doThisAndGo(() -> {
                    e.setUniqueId(uniqueId);
                    e.setToken(getToken());
                    captureError(e);
                });
                return uniqueId;
            }

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DEFINE_BOT_COMMAND_REQUEST");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;


    }

    public String startBot(StartAndStopBotRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = null;
            try {
                message = BotManager.createStartBotRequest(request, uniqueId);
            } catch (PodChatException e) {
                new PodThreadManager().doThisAndGo(() -> {
                    e.setUniqueId(uniqueId);
                    e.setToken(getToken());
                    captureError(e);
                });
                return uniqueId;
            }

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_START_BOT_REQUEST");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;


    }

    public String stopBot(StartAndStopBotRequest request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = null;
            try {
                message = BotManager.createStopBotRequest(request, uniqueId);
            } catch (PodChatException e) {
                new PodThreadManager().doThisAndGo(() -> {
                    e.setUniqueId(uniqueId);
                    e.setToken(getToken());
                    captureError(e);
                });
                return uniqueId;
            }

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "SEND_STOP_BOT_REQUEST");

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

        if (cache && request.useCacheData()) {

            loadMentionsFromCache(request, uniqueId);

        }

        if (chatReady) {

            String message = Mention.getMentionList(request, uniqueId);

            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "GET_MENTION_LIST");

        } else {
            onChatNotReady(uniqueId);
        }
        return uniqueId;
    }

    @SuppressWarnings("unchecked")
    private void loadMentionsFromCache(RequestGetMentionList request, String uniqueId) {

        messageDatabaseHelper.getMentionList(request, (messages, contentCount) -> {

            ChatResponse<ResultHistory> cacheResponse = Mention
                    .getMentionListCacheResponse(
                            request,
                            (List<MessageVO>) messages,
                            uniqueId,
                            (Long) contentCount);

            listenerManager.callOnGetMentionList(cacheResponse);

            showLog("CACHE MENTION LIST", gson.toJson(cacheResponse));


        });
    }


    /**
     * @param request request that contains name to check if is available to create a public thread or channel
     * @return
     */

    public String isNameAvailable(RequestCheckIsNameAvailable request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String message = PublicThread.checkIfNameIsAvailable(request, uniqueId);

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

        if (cache && request.useCacheData()) {

            loadUserRolesFromCache(request, uniqueId);

            return uniqueId;
        }

        if (chatReady) {

            String message = UserRoles.getUserRoles(request, uniqueId);
            sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "GET_USER_ROLES");

        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    private void loadUserRolesFromCache(RequestGetUserRoles request, String uniqueId) {

        messageDatabaseHelper.getCurrentUserRoles(request, cacheRole -> {

            if (cacheRole != null) {
                ChatResponse<ResultCurrentUserRoles> response = UserRoles
                        .handleOnGetUserRolesFromCache(uniqueId, request, (CacheUserRoles) cacheRole);

                listenerManager.callOnGetUserRoles(response);

                showLog("RECEIVE CURRENT USER ROLES FROM CACHE", gson.toJson(response));
            }

            if (chatReady) {

                String message = UserRoles.getUserRoles(request, uniqueId);
                sendAsyncMessage(message, AsyncAckType.Constants.WITHOUT_ACK, "GET_USER_ROLES");

            } else {
                onChatNotReady(uniqueId);
            }


        });
    }

    private void onChatNotReady(String uniqueId) {
        String jsonError = captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        if (log) Log.e(TAG, jsonError);
    }


    private String removeRole(SetRuleVO request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            setCallBacks(null, null, null, true, Constants.REMOVE_ROLE_FROM_USER, null, uniqueId);
            String asyncContent = ContactManager.prepareRemoveRoleRequest(request, uniqueId, getTypeCode(), getToken(), String.valueOf(TOKEN_ISSUER));
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
     *                 {@link #getPhoneContact(Context, String, OnContactLoaded)}
     */
    public String syncContact(Activity activity) {

        showLog(">>> Start Syncing... " + new Date());

        String uniqueId = generateUniqueId();

        if (Permission.Check_READ_CONTACTS(activity)) {

            if (chatReady) {

                getPhoneContact(getContext(), uniqueId, phoneContacts -> {

                    if (phoneContacts.size() > 0) {

                        showLog(">>> Synchronizing " + phoneContacts.size() + " with server at " + new Date());


                        handleAddContacts(uniqueId, phoneContacts);


                    } else {

                        showLog(">>> No New Contact Found. Everything is synced ");

                        ChatResponse<Contacts> chatResponse = new ChatResponse<>();
                        chatResponse.setUniqueId(uniqueId);
                        Contacts contacts = new Contacts();
                        contacts.setCount(0);
                        contacts.setResult(new ArrayList<>());
                        chatResponse.setResult(contacts);
                        listenerManager.callOnSyncContact(gson.toJson(chatResponse), chatResponse);

                        if (log)
                            showLog("SYNC_CONTACT_COMPLETED");
                    }


                });

            } else {
                onChatNotReady(uniqueId);
            }
        } else {

            String jsonError = captureError(ChatConstant.ERROR_READ_CONTACT_PERMISSION, ChatConstant.ERROR_CODE_READ_CONTACT_PERMISSION
                    , uniqueId);

            Permission.Request_READ_CONTACTS(activity, READ_CONTACTS_CODE);

            if (log) Log.e(TAG, jsonError);
        }
        return uniqueId;
    }

    private void handleAddContacts(String uniqueId, List<PhoneContact> phoneContacts) {
        if (phoneContacts.size() < 100) {

            addContacts(phoneContacts, uniqueId);


        } else {

            showLog(">>> More than 100 contact");

            List<PhoneContact> group = phoneContacts.subList(0, 100);

            showLog(">>> adding a group of #" + group.size() + " contact");

            PublishSubject<List<PhoneContact>> publishSubject = addGroupContacts(group, uniqueId);

            publishSubject.subscribe(
                    addedContacts -> {

                        showLog(">>> adding a group of #" + group.size() + " contact done!");

                        phoneContacts.removeAll(addedContacts);

                        showLog(">>> #" + phoneContacts.size() + " contacts need sync");

                        handleAddContacts(uniqueId, phoneContacts);
                    }, throwable -> showErrorLog(throwable.getMessage()));


        }
    }


//    public String syncContactTest(Activity activity) {
//
//        Log.i(TAG, ">>> Start Syncing... " + new Date());
//
//        String uniqueId = generateUniqueId();
//
//        if (Permission.Check_READ_CONTACTS(activity)) {
//
//            getPhoneContact(getContext(), phoneContacts -> {
//
//                if (phoneContacts.size() > 0) {
//
//                    Log.i(TAG, ">>> Synchronizing " + phoneContacts.size() + " with server at " + new Date());
//                    addContactsTest(phoneContacts, uniqueId);
//                } else {
//
//                    Log.i(TAG, ">>> No New Contact Found. Everything is synced " + new Date());
//
//                    ChatResponse<Contacts> chatResponse = new ChatResponse<>();
//                    chatResponse.setUniqueId(uniqueId);
//                    Contacts contacts = new Contacts();
//                    contacts.setCount(0);
//                    contacts.setResult(new ArrayList<>());
//                    chatResponse.setResult(contacts);
//                    listenerManager.callOnSyncContact(gson.toJson(chatResponse), chatResponse);
//
//                    if (log)
//                        Log.i(TAG, "SYNC_CONTACT_COMPLETED");
//                }
//
//
//            });
//
//        } else {
//
//            String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_CONTACT_PERMISSION, ChatConstant.ERROR_CODE_READ_CONTACT_PERMISSION
//                    , uniqueId);
//
//            Permission.Request_READ_CONTACTS(activity, READ_CONTACTS_CODE);
//
//            if (log) Log.e(TAG, jsonError);
//        }
//        return uniqueId;
//    }
//
//    private void addContactsTest(List<PhoneContact> phoneContacts, String uniqueId) {
//
//        Log.d(TAG, "Call to add contact");
//
//        Runnable updatePhoneContactsDBTask = () -> {
//            try {
//                phoneContactDbHelper.addPhoneContacts(phoneContacts);
//            } catch (Exception e) {
//                showErrorLog("Updating Contacts cache failed: " + e.getMessage());
//            }
//        };
//
//
//            Runnable updateCachedContactsTask = () -> {
//
//                ArrayList<Contact> c = new ArrayList<>();
//                if (cache) {
//                    for (PhoneContact pc :
//                            phoneContacts) {
//                        Contact ccc = new Contact();
//                        ccc.setFirstName(pc.getName());
//                        ccc.setLastName(pc.getLastName());
//                        ccc.setCellphoneNumber(pc.getPhoneNumber());
//                        ccc.setEmail("");
//                        ccc.setUniqueId(generateUniqueId());
//                        c.add(ccc);
//                    }
//                    try {
//                        messageDatabaseHelper.saveContacts(c, getExpireAmount());
//                    } catch (Exception e) {
//                        showErrorLog("Saving Contacts Failed: " + e.getMessage());
//                    }
//                }
//            };
//
//        new PodThreadManager()
//                .addNewTask(updatePhoneContactsDBTask)
//                .addNewTask(updateCachedContactsTask)
//                .runTasksSynced();
//
//    }


    private boolean needReadStoragePermission(Activity activity) {

        if (!Permission.Check_READ_STORAGE(activity)) {

            Permission.Request_READ_STORAGE(activity, READ_EXTERNAL_STORAGE_CODE);

            captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);


            return true;
        }
        return false;
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
//    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
//
//        long threadId = requestFileMessage.getThreadId();
//        Activity activity = requestFileMessage.getActivity();
//        Uri fileUri = requestFileMessage.getFileUri();
//        String description = requestFileMessage.getDescription();
//        int messageType = requestFileMessage.getMessageType();
//        String systemMetadata = requestFileMessage.getSystemMetadata();
//
//        return sendFileMessage(activity, description, threadId, fileUri, systemMetadata, messageType, handler);
//    }
    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {

        String uniqueId = generateUniqueId();

        if (needReadStoragePermission(requestFileMessage.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return uniqueId;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return uniqueId;
        }

        if (getPodSpaceServer() == null) {

            captureError("PodSpace server is null", 0, uniqueId);

            return uniqueId;
        }

        try {

            Subscription subscription = PodUploader.uploadToPodSpace(
                    uniqueId,
                    requestFileMessage.getFileUri(),
                    requestFileMessage.getUserGroupHash(),
                    context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    requestFileMessage.getImageXc(),
                    requestFileMessage.getImageYc(),
                    requestFileMessage.getImageHc(),
                    requestFileMessage.getImageWc(),
                    new PodUploader.IPodUploadFileToPodSpace() {
                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId);

                            String json = gson.toJson(chatResponse);
                            showLog("FILE_UPLOADED_TO_SERVER", json);
                            listenerManager.callOnUploadFile(json, chatResponse);

                            if (handler != null) {
                                handler.onFinishFile(json, chatResponse);
                            }

                            String jsonMeta = createFileMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    mimeType,
                                    length,
                                    response.getParentHash());


                            sendTextMessageWithFile(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getThreadId(),
                                    jsonMeta,
                                    requestFileMessage.getSystemMetadata(),
                                    uniqueId,
                                    typeCode,
                                    requestFileMessage.getMessageType());

                        }

                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);

                            String jsonMeta = createImageMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    height,
                                    width,
                                    mimeType,
                                    length,
                                    response.getParentHash(),
                                    false,
                                    null);


                            sendTextMessageWithFile(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getThreadId(),
                                    jsonMeta,
                                    requestFileMessage.getSystemMetadata(),
                                    uniqueId,
                                    typeCode,
                                    requestFileMessage.getMessageType());

                        }

                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }
                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {

                            addToUploadQueue(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getFileUri(),
                                    requestFileMessage.getMessageType(),
                                    requestFileMessage.getThreadId(),
                                    requestFileMessage.getUserGroupHash(),
                                    uniqueId,
                                    requestFileMessage.getSystemMetadata(),
                                    mimeType, file, length);

                            showLog("UPLOAD_FILE_TO_SERVER_STARTED");
                            showLog(requestFileMessage.toString());


                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                            if (handler != null)
                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                        }
                    });


            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {
            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }

        return uniqueId;
    }

    private void sendFileMessage(RequestFileMessage requestFileMessage, String uniqueId, ProgressHandler.sendFileMessage handler) {

        if (needReadStoragePermission(requestFileMessage.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return;
        }

        if (getPodSpaceServer() == null) {

            captureError("File server is null", 0, uniqueId);

            return;
        }

        try {

            Subscription subscription = PodUploader.uploadToPodSpace(
                    uniqueId,
                    requestFileMessage.getFileUri(),
                    requestFileMessage.getUserGroupHash(),
                    context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    requestFileMessage.getImageXc(),
                    requestFileMessage.getImageYc(),
                    requestFileMessage.getImageHc(),
                    requestFileMessage.getImageWc(),
                    new PodUploader.IPodUploadFileToPodSpace() {
                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId);
                            String json = gson.toJson(chatResponse);
                            showLog("FILE_UPLOADED_TO_SERVER", json);
                            listenerManager.callOnUploadFile(json, chatResponse);

                            if (handler != null) {
                                handler.onFinishFile(json, chatResponse);
                            }

                            String jsonMeta = createFileMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    mimeType,
                                    length,
                                    response.getParentHash());


                            sendTextMessageWithFile(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getThreadId(),
                                    jsonMeta,
                                    requestFileMessage.getSystemMetadata(),
                                    uniqueId,
                                    typeCode,
                                    requestFileMessage.getMessageType());

                        }

                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);


                            String jsonMeta = createImageMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    height,
                                    width,
                                    mimeType,
                                    length,
                                    response.getParentHash(),
                                    false,
                                    null);


                            sendTextMessageWithFile(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getThreadId(),
                                    jsonMeta,
                                    requestFileMessage.getSystemMetadata(),
                                    uniqueId,
                                    typeCode,
                                    requestFileMessage.getMessageType());

                        }


                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }
                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {

                            addToUploadQueue(
                                    requestFileMessage.getDescription(),
                                    requestFileMessage.getFileUri(),
                                    requestFileMessage.getMessageType(),
                                    requestFileMessage.getThreadId(),
                                    requestFileMessage.getUserGroupHash(),
                                    uniqueId,
                                    requestFileMessage.getSystemMetadata(),
                                    mimeType, file, length);

                            showLog("UPLOAD_FILE_TO_SERVER");
                            showLog(requestFileMessage.toString());

                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {
                            if (handler != null)
                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                        }
                    });

            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {
            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }
    }

    private void initCancelUpload(String uniqueId, Subscription subscription) {
        cancelUpload = new ProgressHandler.cancelUpload() {
            @Override
            public void cancelUpload(String uniqueCancel) {
                if (uniqueCancel.equals(uniqueId) && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                    if (log) Log.e(TAG, "Uploaded Canceled");
                }
            }
        };
    }

//    @Deprecated
//    public String uploadImageProgress(Activity activity, Uri fileUri, ProgressHandler.onProgress handler) {
//        String uniqueId;
//        uniqueId = generateUniqueId();
//        if (chatReady) {
//            if (fileServer != null) {
//                if (Permission.Check_READ_STORAGE(activity)) {
//                    String mimeType = getMimType(fileUri);
//                    RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
//                    FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
//
//                    String path = FilePick.getSmartFilePath(getContext(), fileUri);
//
//                    File file = new File(path);
//
//                    if (!Util.isNullOrEmpty(mimeType) && FileUtils.isImage(mimeType)) {
//
//
//                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {
//
//                            @Override
//                            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
//                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
//                                handler.onProgressUpdate(progress);
//                            }
//
//                            @Override
//                            public void onError() {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//
//                            }
//                        });
//
//
//                        JsonObject jLog = new JsonObject();
//
//
//                        jLog.addProperty("name", file.getName());
//                        jLog.addProperty("token", getToken());
//                        jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
//                        jLog.addProperty("uniqueId", uniqueId);
//
//                        showLog("UPLOADING_IMAGE", getJsonForLog(jLog));
//
//
//                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//
//                        Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
//                        uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(fileUploadResponse -> {
//                            if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
//
//                                boolean hasError = fileUploadResponse.body().isHasError();
//                                if (hasError) {
//                                    String errorMessage = fileUploadResponse.body().getMessage();
//                                    if (Util.isNullOrEmpty(errorMessage)) {
//                                        errorMessage = "";
//                                    }
//                                    int errorCode = fileUploadResponse.body().getErrorCode();
//                                    String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
//                                    if (log) Log.e(TAG, jsonError);
//                                } else {
//                                    FileImageUpload fileImageUpload = fileUploadResponse.body();
//                                    ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
//                                    ResultImageFile resultImageFile = new ResultImageFile();
//                                    chatResponse.setUniqueId(uniqueId);
//                                    resultImageFile.setId(fileImageUpload.getResult().getId());
//                                    resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
//                                    resultImageFile.setName(fileImageUpload.getResult().getName());
//                                    resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
//                                    resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
//                                    resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
//                                    resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());
//
//                                    chatResponse.setResult(resultImageFile);
//
//                                    resultImageFile.setUrl(getImage(resultImageFile.getId(), resultImageFile.getHashCode(), true));
//
//
//                                    String imageJson = gson.toJson(chatResponse);
//
////                                    if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
////                                    listenerManager.callOnLogEvent(imageJson);
//
//                                    showLog("RECEIVE_UPLOAD_IMAGE", imageJson);
//
//                                    listenerManager.callOnUploadImageFile(imageJson, chatResponse);
//                                    handler.onFinish(imageJson, chatResponse);
//                                }
//                            }
//                        }, throwable -> {
//                            getErrorOutPut(throwable.getMessage(), 0, uniqueId);
//
//                            ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, null);
//                            String jsonError = gson.toJson(error);
//                            handler.onError(jsonError, error);
//                            if (log) Log.e(TAG, throwable.getMessage());
//                        });
//                    } else {
//                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
//                        ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
//                        handler.onError(jsonError, error);
//                        if (log) Log.e(TAG, jsonError);
//                        return uniqueId;
//                    }
//                } else {
//                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
//                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
//                    handler.onError(jsonError, error);
//                    Permission.Request_READ_STORAGE(activity, READ_EXTERNAL_STORAGE_CODE);
//                    if (log) Log.e(TAG, jsonError);
//                    return uniqueId;
//                }
//            } else {
//                String jsonError = getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, null);
//                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
//                handler.onError(jsonError, error);
//                if (log) Log.e(TAG, "FileServer url Is null");
//                return uniqueId;
//            }
//        } else {
//            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
////            listenerManager.callOnLogEvent(jsonError);
//            return uniqueId;
//        }
//        return uniqueId;
//    }

    /**
     * It uploads image to the server just by pass image uri
     */
//    @Deprecated
//    public String uploadImage(Activity activity, Uri fileUri) {
//        String uniqueId;
//        uniqueId = generateUniqueId();
//        if (chatReady) {
//            try {
//                if (fileServer != null && fileUri != null) {
//                    if (Permission.Check_READ_STORAGE(activity)) {
//                        String path = FilePick.getSmartFilePath(getContext(), fileUri);
//                        if (Util.isNullOrEmpty(path)) {
//                            path = "";
//                        }
//                        File file = new File(path);
//                        if (file.exists()) {
//                            String mimeType = handleMimType(fileUri, file);
//                            if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
//                                RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
//                                FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
//                                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//                                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//                                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//
//
//                                JsonObject jLog = new JsonObject();
//                                jLog.addProperty("name", file.getName());
//                                jLog.addProperty("token", getToken());
//                                jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
//                                jLog.addProperty("uniqueId", uniqueId);
//                                showLog("UPLOADING_IMAGE", getJsonForLog(jLog));
//
//
//                                Observable<Response<FileImageUpload>> uploadObservable = fileApi.sendImageFile(body, getToken(), TOKEN_ISSUER, name);
//
//                                uploadObservable
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(fileUploadResponse -> {
//                                            if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
//                                                boolean hasError = fileUploadResponse.body().isHasError();
//                                                if (hasError) {
//                                                    String errorMessage = fileUploadResponse.body().getMessage();
//                                                    int errorCode = fileUploadResponse.body().getErrorCode();
//                                                    String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
//                                                    if (log) Log.e(TAG, jsonError);
//                                                } else {
//                                                    FileImageUpload fileImageUpload = fileUploadResponse.body();
//                                                    ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
//                                                    ResultImageFile resultImageFile = new ResultImageFile();
//                                                    chatResponse.setUniqueId(uniqueId);
//                                                    resultImageFile.setId(fileImageUpload.getResult().getId());
//                                                    resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
//                                                    resultImageFile.setName(fileImageUpload.getResult().getName());
//                                                    resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
//                                                    resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
//                                                    resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
//                                                    resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());
//
//                                                    chatResponse.setResult(resultImageFile);
//
//                                                    resultImageFile.setUrl(getImage(resultImageFile.getId(), resultImageFile.getHashCode(), true));
//
//
//                                                    String imageJson = gson.toJson(chatResponse);
//
//                                                    listenerManager.callOnUploadImageFile(imageJson, chatResponse);
//
//                                                    showLog("RECEIVE_UPLOAD_IMAGE", imageJson);
//                                                    //                                                if (log) Log.i(TAG, "RECEIVE_UPLOAD_IMAGE");
//                                                    //                                                listenerManager.callOnLogEvent(imageJson);
//                                                }
//                                            }
//                                        }, throwable -> {
//                                            String jsonError = getErrorOutPut(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
//                                            if (log) Log.e(TAG, jsonError);
//                                        });
//                            } else {
//                                String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, uniqueId);
//                                if (log) Log.e(TAG, jsonError);
////                                uniqueId = null;
//                            }
//                        }
//                    } else {
//                        String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, null);
//                        if (log) Log.e(TAG, jsonError);
////                        uniqueId = null;
//                    }
//                } else {
//                    getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
//                    if (log) Log.e(TAG, "FileServer url Is null");
////                    uniqueId = null;
//                }
//            } catch (Exception e) {
//                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
//                if (log) Log.e(TAG, e.getCause().getMessage());
////                uniqueId = null;
//            }
//        } else {
//            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//        }
//        return uniqueId;
//    }

    /**
     * It uploads image to the server just by pass image uri
     */
    public String uploadImage(RequestUploadImage requestUploadImage) {
        return uploadImageProgress(requestUploadImage, null);
    }

    public String uploadImageProgress(RequestUploadImage request, @Nullable ProgressHandler.onProgress handler) {

        String uniqueId = generateUniqueId();

        if (needReadStoragePermission(request.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return uniqueId;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return uniqueId;
        }

        if (getPodSpaceServer() == null) {

            captureError("File server is null", 0, uniqueId);

            return uniqueId;
        }

        try {

            Subscription subscription = PodUploader.uploadPublicToPodSpace(
                    uniqueId,
                    request.getFileUri(),
                    context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    String.valueOf(request.getxC()),
                    String.valueOf(request.getyC()),
                    String.valueOf(request.gethC()),
                    String.valueOf(request.getwC()),
                    request.isPublic(),
                    new PodUploader.IPodUploadFileToPodSpace() {

                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                            ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (handler != null) {
                                handler.onFinish(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);

                        }

                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }
                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {


                            showLog("UPLOADING_FILE");

                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                            if (handler != null) {
                                handler.onProgressUpdate(progress);
                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                            }
                        }
                    });


            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {

            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);

            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }


        return uniqueId;

    }

    /**
     * It uploads file to file server
     */

//    @Deprecated
//    private String uploadFile(@NonNull Activity activity, @NonNull Uri uri) {
//
//        String uniqueId;
//
//        uniqueId = generateUniqueId();
//
//        if (chatReady) {
//            try {
//                if (Permission.Check_READ_STORAGE(activity)) {
//
//                    if (getFileServer() != null) {
//                        String path = FilePick.getSmartFilePath(getContext(), uri);
//                        if (Util.isNullOrEmpty(path)) {
//                            path = "";
//                        }
//
//                        File file = new File(path);
//                        String mimeType = handleMimType(uri, file);
//                        if (file.exists()) {
//                            long fileSize = file.length();
//
//
//                            JsonObject jLog = new JsonObject();
//
//                            jLog.addProperty("file", file.getName());
//                            jLog.addProperty("file_size", fileSize);
//                            jLog.addProperty("uniqueId", uniqueId);
//                            showLog("UPLOADING_FILE", getJsonForLog(jLog));
//
//
//                            RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
//                            FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
//                            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//                            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
//
//                            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
//
//                            Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
//
//
//                            uploadObservable.subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(fileUploadResponse -> {
//                                        if (fileUploadResponse.body() != null && fileUploadResponse.isSuccessful()) {
//                                            boolean hasError = fileUploadResponse.body().isHasError();
//                                            if (hasError) {
//                                                String errorMessage = fileUploadResponse.body().getMessage();
//                                                int errorCode = fileUploadResponse.body().getErrorCode();
//                                                String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
//                                                if (log) Log.e(TAG, jsonError);
//                                            } else {
//                                                ResultFile result = fileUploadResponse.body().getResult();
//                                                result.setUrl(getFile(result.getId(), result.getHashCode(), true));
//                                                result.setSize(fileSize);
//
//                                                ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
//                                                chatResponse.setUniqueId(uniqueId);
//                                                chatResponse.setResult(result);
//
//
//                                                String json = gson.toJson(chatResponse);
//                                                listenerManager.callOnUploadFile(json, chatResponse);
//                                                showLog("RECEIVE_UPLOAD_FILE", json);
////                                        if (log) Log.i(TAG, "RECEIVE_UPLOAD_FILE");
////                                        listenerManager.callOnLogEvent(json);
//                                            }
//                                        }
//                                    }, throwable -> {
//                                        String jsonError = getErrorOutPut(throwable.getCause().getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
//                                        if (log) Log.e(TAG, jsonError);
//                                    });
//
////                            uploadObservable.unsubscribeOn(Schedulers.io());
//
//                        } else {
//                            getErrorOutPut("File is not Exist", ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
//                            if (log) Log.e(TAG, "File is not Exist");
//                            return uniqueId;
//                        }
//                    } else {
//                        getErrorOutPut("FileServer url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
//
//                        if (log) Log.e(TAG, "File Server url Is null");
//                        return uniqueId;
//                    }
//                } else {
//                    Permission.Request_WRITE_TORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
//                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
//                    if (log) Log.e(TAG, jsonError);
//                    return uniqueId;
//                }
//            } catch (Exception e) {
//                getErrorOutPut(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
//                if (log) Log.e(TAG, e.getMessage());
//                return uniqueId;
//            }
//        } else {
//            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//        }
//
//        return uniqueId;
//    }
    public String uploadFile(@NonNull RequestUploadFile requestUploadFile) {

        return uploadFileProgress(requestUploadFile, null);

    }

    /**
     * It uploads file and it shows progress of the file downloading
     */

    public String uploadFileProgress(RequestUploadFile request, @Nullable ProgressHandler.onProgressFile handler) {

        String uniqueId = generateUniqueId();

        if (needReadStoragePermission(request.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return uniqueId;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return uniqueId;
        }

        if (getPodSpaceServer() == null) {

            captureError("File server is null", 0, uniqueId);

            return uniqueId;
        }

        try {

            Subscription subscription = PodUploader.uploadPublicToPodSpace(
                    uniqueId,
                    request.getFileUri(),
                    context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    request.isPublic(),
                    new PodUploader.IPodUploadFileToPodSpace() {

                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                            ResultFile resultFile = PodUploader.generateFileUploadResult(response);
                            FileUpload result = new FileUpload();
                            result.setResult(resultFile);
                            ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                            resultFile.setUrl(getPodSpaceFileUrl(resultFile.getHashCode()));
                            showLog("FINISH_UPLOAD_FILE", gson.toJson(resultFile));
                            chatResponse.setResult(resultFile);
                            chatResponse.setUniqueId(uniqueId);

                            if (handler != null) {
                                handler.onFinish(gson.toJson(chatResponse), result);
                            }

                            listenerManager.callOnUploadFile(gson.toJson(resultFile), chatResponse);

                        }

                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, cause, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }
                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {


                            showLog("UPLOADING_FILE");

                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                            if (handler != null) {
                                handler.onProgressUpdate(progress);
                                handler.onProgress(uniqueId, progress, totalBytesSent, totalBytesToSend);
                            }

                        }
                    });


            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {

            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);

            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }

        return uniqueId;


    }

//    private String uploadFileProgress(Activity activity, Uri uri, ProgressHandler.onProgressFile handler) {
//        String uniqueId = generateUniqueId();
//        try {
//            if (chatReady) {
//
//                if (Permission.Check_READ_STORAGE(activity)) {
//
//                    if (getFileServer() != null) {
//
//                        String mimeType = getMimType(uri);
////                    File file = new File(getRealPathFromURI(context, uri));
//                        String path = FilePick.getSmartFilePath(getContext(), uri);
//                        File file = new File(path);
//
//
//                        JsonObject jLog = new JsonObject();
//
//                        jLog.addProperty("name", file.getName());
//                        jLog.addProperty("token", getToken());
//                        jLog.addProperty("tokenIssuer", TOKEN_ISSUER);
//                        jLog.addProperty("uniqueId", uniqueId);
//
//                        showLog("UPLOADING_FILE", getJsonForLog(jLog));
//
//                        RetrofitHelperFileServer retrofitHelperFileServer = new RetrofitHelperFileServer(getFileServer());
//                        FileApi fileApi = retrofitHelperFileServer.getService(FileApi.class);
//                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//                        ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId, new ProgressRequestBody.UploadCallbacks() {
//
//                            @Override
//                            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
//                                handler.onProgress(uniqueId, progress, totalBytesSent, totalBytesToSend);
//                                handler.onProgressUpdate(progress);
//                            }
//
//                        });
//
//                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//                        Observable<Response<FileUpload>> uploadObservable = fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);
//                        uploadObservable.subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(fileUploadResponse -> {
//                                    if (fileUploadResponse.isSuccessful()) {
//                                        boolean hasError = fileUploadResponse.body().isHasError();
//                                        if (hasError) {
//                                            String errorMessage = fileUploadResponse.body().getMessage();
//                                            int errorCode = fileUploadResponse.body().getErrorCode();
//                                            String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
//                                            ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
//                                            handler.onError(jsonError, error);
//                                        } else {
//
//                                            FileUpload result = fileUploadResponse.body();
//                                            ResultFile resultFile = result.getResult();
//                                            resultFile.setUrl(getFile(resultFile.getId(), resultFile.getHashCode(), true));
//
//
//                                            ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
//                                            chatResponse.setResult(resultFile);
//                                            chatResponse.setUniqueId(uniqueId);
//                                            String json = gson.toJson(chatResponse);
//                                            showLog("FINISH_UPLOAD_FILE", json);
//                                            listenerManager.callOnUploadFile(json, chatResponse);
//
//                                            handler.onFinish(json, result);
//                                        }
//                                    }
//                                }, throwable -> {
//                                    ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage(), 0, uniqueId);
//                                    String json = gson.toJson(error);
//                                    getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE + " " + throwable.getMessage(), ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
//                                    handler.onError(json, error);
//
//                                });
//                    } else {
//
//                        if (log) Log.e(TAG, "FileServer url Is null");
//
//                        getErrorOutPut("File Server url Is null", ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
//
//                    }
//
//                } else {
//                    String jsonError = getErrorOutPut(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
//                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
//                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
//                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
//                    handler.onError(jsonError, error);
//                    Permission.Request_WRITE_TORAGE(activity, WRITE_EXTERNAL_STORAGE_CODE);
//                }
//            } else {
//                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            }
//
//        } catch (Throwable throwable) {
//            if (log) Log.e(TAG, throwable.getMessage());
//            getErrorOutPut(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
//
//        }
//        return uniqueId;
//    }

    //new upload file function
    private void uploadFileToThread(RequestUploadFile request, String userGroupHash, String uniqueId, @Nullable ProgressHandler.onProgressFile handler, OnWorkDone listener) {
        if (needReadStoragePermission(request.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return;
        }

        if (getPodSpaceServer() == null) {

            captureError("File server is null", 0, uniqueId);

            return;
        }

        try {
            Subscription subscription =
                    PodUploader.uploadToPodSpace(
                            uniqueId,
                            request.getFileUri(),
                            request.getUserGroupHashCode(),
                            context,
                            getPodSpaceServer(),
                            getToken(),
                            TOKEN_ISSUER,
                            new PodUploader.IPodUploadFileToPodSpace() {
                                @Override
                                public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                                    ResultFile resultFile = PodUploader.generateFileUploadResult(response);
                                    FileUpload result = new FileUpload();
                                    result.setResult(resultFile);
                                    ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                    resultFile.setUrl(getFile(resultFile.getId(), resultFile.getHashCode(), true));
                                    showLog("FINISH_UPLOAD_FILE", gson.toJson(resultFile));
                                    MetaDataFile metaDataFile = new MetaDataFile();
                                    FileMetaDataContent metaDataContent = new FileMetaDataContent();
                                    metaDataContent.setHashCode(resultFile.getHashCode());
                                    metaDataContent.setId(resultFile.getId());
                                    metaDataContent.setName(resultFile.getName());
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

                                @Override
                                public void onFailure(String cause, Throwable t) {

                                    String jsonError = captureError(cause
                                            , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                                    ErrorOutPut error = new ErrorOutPut(true, cause, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                    if (handler != null) {
                                        handler.onError(jsonError, error);
                                    }

                                }

                                @Override
                                public void onUploadStarted(String mimeType, File file, long length) {

                                    showLog("UPLOADING_FILE");

                                }

                                @Override
                                public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {
                                    if (handler != null) {
                                        handler.onProgressUpdate(progress);
                                        handler.onProgress(uniqueId, progress, totalBytesSent, totalBytesToSend);
                                    }
                                }
                            }
                    );

            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {

            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);

            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }

    }

    //new upload image function
    private void uploadImageToThread(RequestUploadImage request, String userGroupHash, String uniqueId, @Nullable ProgressHandler.onProgress handler, OnWorkDone listener) {

        if (needReadStoragePermission(request.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            listener.onWorkDone(null);

            return;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            listener.onWorkDone(null);

            return;
        }

        if (getPodSpaceServer() == null) {

            listener.onWorkDone(null);

            captureError("File server is null", 0, uniqueId);

            return;
        }

        try {

            Subscription subscription = PodUploader.uploadToPodSpace(
                    uniqueId,
                    request.getFileUri(),
                    userGroupHash,
                    context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    String.valueOf(request.getxC()),
                    String.valueOf(request.getyC()),
                    String.valueOf(request.gethC()),
                    String.valueOf(request.getwC()),
                    new PodUploader.IPodUploadFileToPodSpace() {

                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                            ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (handler != null) {
                                handler.onFinish(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);

                            listener.onWorkDone(response.getHashCode());

                        }

                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }

                            listener.onWorkDone(null);

                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {


                            showLog("UPLOADING_FILE");

                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                            if (handler != null) {
                                handler.onProgressUpdate(progress);
                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                            }
                        }
                    });


            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {

            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);

            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);

            listener.onWorkDone(null);
            if (handler != null) {
                handler.onError(jsonError, error);
            }
        }

    }


    /**
     * If you want to resend the message that is stored in waitQueue
     *
     * @param uniqueId the unique id of the waitQueue message
     */
    public void resendMessage(String uniqueId) {
        if (uniqueId != null) {
            if (cache) {


                dataSource.moveFromWaitingToSendingQueue(uniqueId)
                        .subscribe(sendQueueData -> {

                            if (sendQueueData != null) {
                                setThreadCallbacks((sendQueueData).getThreadId(), uniqueId);
                                sendSendQueueMessage(uniqueId, sendQueueData);
                            }
                        });

//                messageDatabaseHelper.moveFromWaitQueueToSendQueue(uniqueId, (sendQueueData) -> {
//
//                    SendingQueueCache sendingQueue = (SendingQueueCache) sendQueueData;
//
//                    if (sendingQueue != null) {
//                        setThreadCallbacks(sendingQueue.getThreadId(), uniqueId);
//                        sendSendQueueMessage(uniqueId, sendingQueue);
//                    }
//
//
//                });

            } else {
                SendingQueueCache sendingQueueCache = sendingQList.get(uniqueId);
                sendSendQueueMessage(uniqueId, sendingQueueCache);
            }


        }
    }

    private void sendSendQueueMessage(String uniqueId, SendingQueueCache sendingQueueCache) {
        JsonObject jsonObject = null;
        if (sendingQueueCache != null) {
            jsonObject = (new JsonParser())
                    .parse(sendingQueueCache.getAsyncContent()).getAsJsonObject();
        }
        if (jsonObject != null) {
            jsonObject.remove("token");
            jsonObject.addProperty("token", getToken());

            if (chatReady) {
                moveFromSendingQueueToWaitQueue(uniqueId, sendingQueueCache);
                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
            } else {
                onChatNotReady(uniqueId);
            }

        }
    }

    /**
     * It cancels message if its still in the Queue
     */
    public void cancelMessage(String uniqueId) {
        if (uniqueId != null) {
            if (cache) {
                dataSource.cancelMessage(uniqueId);
//                messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
//                messageDatabaseHelper.deleteWaitQueueMsgs(uniqueId);
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

//    public void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler) {
//
//        Runnable retryTask = () -> {
//            String uniqueId = retry.getUniqueId();
//            Activity activity = retry.getActivity();
//
//            UploadingQueueCache uploadingQ;
//            if (cache) {
//                uploadingQ = messageDatabaseHelper.getUploadingQ(uniqueId);
//            } else {
//                uploadingQ = uploadingQList.get(uniqueId);
//            }
//
//            if (uploadingQ != null) {
//                long messageId = uploadingQ.getId();
//                int messageType = uploadingQ.getMessageType();
//                long threadId = uploadingQ.getThreadId();
//                String message = uploadingQ.getMessage();
//                String systemMetadata = uploadingQ.getSystemMetadata();
//                MetaDataFile metaDataFile = gson.fromJson(systemMetadata, MetaDataFile.class);
//                String link = metaDataFile.getFile().getLink();
//                String mimeType = metaDataFile.getFile().getMimeType();
//
//                LFileUpload lFileUpload = new LFileUpload();
//                lFileUpload.setActivity(activity);
//                lFileUpload.setDescription(message);
//                lFileUpload.setFileUri(Uri.parse(link));
//                lFileUpload.setHandler(handler);
//                lFileUpload.setMessageType(messageType);
//                lFileUpload.setThreadId(threadId);
//                lFileUpload.setUniqueId(uniqueId);
//                lFileUpload.setSystemMetaData(systemMetadata);
//                lFileUpload.setHandler(handler);
//                lFileUpload.setMimeType(mimeType);
//
//                if (!Util.isNullOrEmpty(messageId)) {
//                    String methodName = ChatConstant.METHOD_REPLY_MSG;
//                    lFileUpload.setMethodName(methodName);
//                }
//
//                removeFromUploadQueue(uniqueId);
//
//                if (FileUtils.isImage(mimeType) && !FileUtils.isGif(mimeType)) {
//                    uploadImageFileMessage(lFileUpload);
//                } else {
//                    uploadFileMessage(lFileUpload);
//                }
//
//            }
//        };
//
//        new PodThreadManager()
//                .doThisAndGo(retryTask);
//
//    }
    public void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler) {

        Runnable retryTask = () -> {

            String uniqueId = retry.getUniqueId();
            Activity activity = retry.getActivity();

            UploadingQueueCache uploadingQ;
            if (cache) {
                uploadingQ = dataSource.getUploadingQ(uniqueId);
//                uploadingQ = messageDatabaseHelper.getUploadingQ(uniqueId);
            } else {
                uploadingQ = uploadingQList.get(uniqueId);
            }

            if (uploadingQ != null) {

                long messageId = uploadingQ.getId();
                int messageType = uploadingQ.getMessageType();
                long threadId = uploadingQ.getThreadId();
                String userGroupHash = uploadingQ.getUserGroupHash();
                String methodName = !Util.isNullOrEmpty(messageId) ? ChatConstant.METHOD_REPLY_MSG : null;
                String message = uploadingQ.getMessage();
                String systemMetadata = uploadingQ.getSystemMetadata();
                String metadata = uploadingQ.getMetadata();
                MetaDataFile metaDataFile = gson.fromJson(metadata, MetaDataFile.class);
                String link = null;
                try {
                    link = metaDataFile.getFile().getLink();
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't retrieve link");

                }

                if (needReadStoragePermission(activity)) {

                    String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                            , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                    if (handler != null) {
                        handler.onError(jsonError, error);
                    }

                    return;

                }

                if (!chatReady) {

                    onChatNotReady(uniqueId);

                    return;
                }

                if (getPodSpaceServer() == null) {

                    captureError("File server is null", 0, uniqueId);

                    return;
                }

                removeFromUploadQueue(uniqueId);

                try {
                    String finalLink = link;
                    Subscription subscription = PodUploader.uploadToPodSpace(
                            uniqueId,
                            Util.isNullOrEmpty(link) ? null : Uri.parse(link),
                            userGroupHash, context,
                            getPodSpaceServer(),
                            getToken(),
                            TOKEN_ISSUER,
                            new PodUploader.IPodUploadFileToPodSpace() {
                                @Override
                                public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                                    removeFromUploadQueue(uniqueId);

                                    ChatResponse<ResultFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId);

                                    String json = gson.toJson(chatResponse);
                                    showLog("FILE_UPLOADED_TO_SERVER", json);
                                    listenerManager.callOnUploadFile(json, chatResponse);

                                    if (handler != null) {
                                        handler.onFinishFile(json, chatResponse);
                                    }

                                    String jsonMeta = createFileMetadata(
                                            file,
                                            response.getHashCode(),
                                            0,
                                            mimeType,
                                            length,
                                            response.getParentHash());


                                    if (isReplyMessage(methodName)) {

                                        showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);
                                        mainReplyMessage(message, threadId, messageId, systemMetadata, messageType, jsonMeta, uniqueId, null);

                                    } else {
                                        sendTextMessageWithFile(message, threadId, jsonMeta, systemMetadata, uniqueId, typeCode, messageType);
                                    }


                                }


                                @Override
                                public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                                    removeFromUploadQueue(uniqueId);

                                    ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                                    String imageJson = gson.toJson(chatResponse);

                                    listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                                    if (handler != null) {
                                        handler.onFinishImage(imageJson, chatResponse);
                                    }

                                    showLog("RECEIVE_UPLOAD_IMAGE", imageJson);


                                    String jsonMeta = createImageMetadata(
                                            file,
                                            response.getHashCode(),
                                            0,
                                            height,
                                            width,
                                            mimeType,
                                            length,
                                            response.getParentHash(),
                                            false,
                                            null);


                                    if (isReplyMessage(methodName)) {

                                        showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);
                                        mainReplyMessage(message, threadId, messageId, systemMetadata, messageType, jsonMeta, uniqueId, null);

                                    } else {
                                        sendTextMessageWithFile(message, threadId, jsonMeta, systemMetadata, uniqueId, typeCode, messageType);
                                    }


                                }

                                @Override
                                public void onFailure(String cause, Throwable t) {

                                    String jsonError = captureError(cause
                                            , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                                    if (handler != null) {
                                        handler.onError(jsonError, error);
                                    }

                                }

                                @Override
                                public void onUploadStarted(String mimeType, File file, long length) {

                                    addToUploadQueue(message,
                                            Util.isNullOrEmpty(finalLink) ? null : Uri.parse(finalLink),
                                            messageType,
                                            threadId,
                                            userGroupHash,
                                            uniqueId,
                                            systemMetadata,
                                            messageId,
                                            mimeType,
                                            null,
                                            methodName,
                                            file,
                                            length);
                                    showLog("UPLOAD_FILE_TO_SERVER");

                                }

                                @Override
                                public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                                    if (handler != null)
                                        handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                                }

                            }
                    );

                    initCancelUpload(uniqueId, subscription);


                } catch (Exception e) {
                    String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                            , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);
                    ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                    if (handler != null) {
                        handler.onError(jsonError, error);
                    }
                }
            }
        };

        new PodThreadManager()
                .doThisAndGo(retryTask);

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

            captureError(ChatConstant.ERROR_LOW_FREE_SPACE, ChatConstant.ERROR_CODE_LOW_FREE_SPACE, "");

        }
        return bytesAvailable;
    }


    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public String getFile(RequestGetPodSpaceFile request, ProgressHandler.IDownloadFile progressHandler) {

        String uniqueId = generateUniqueId();

        String url = getPodSpaceFileUrl(request.getHashCode());

        showLog("DOWNLOAD FILE: " + url);

        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);

        File destinationFolder;

        if (cache && request.canUseCache()) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getOrCreateDirectory(FileUtils.FILES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getPublicFilesDirectory();

        }

        showLog("Save in folder: " + destinationFolder);


        String fileName = "file_" + request.getHashCode();


        if (destinationFolder == null) {

            showErrorLog("Error Creating destination folder");

            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);

            return uniqueId;
        }


        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

            showLog("File Exist in cache: " + cachedFile);

            //file exists
            ChatResponse<ResultDownloadFile> response = PodDownloader.generatePodSpaceDownloadResult(request.getHashCode(), cachedFile);

            progressHandler.onFileReady(response);

            return uniqueId;

        }


        //only url should return in callback
        if (!hasFreeSpace) {

            showErrorLog("Download couldn't start. cause: LOW FREE SPACE");

            progressHandler.onLowFreeSpace(uniqueId, url);

            return uniqueId;
        }


        if (chatReady) {

            showLog("Download Started");


            Call call = PodDownloader.downloadFileFromPodSpace(
                    new ProgressHandler.IDownloadFile() {
                        @Override
                        public void onError(String mUniqueId, String error, String mUrl) {
                            progressHandler.onError(uniqueId, error, url);

                            showErrorLog("Download Error. cause: " + error);
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, long bytesDownloaded, long totalBytesToDownload) {

                            progressHandler.onProgressUpdate(uniqueId, bytesDownloaded, totalBytesToDownload);


                            if (totalBytesToDownload > checkFreeSpace()) {

                                showErrorLog("Total file space is more than free space");

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
                            showLog("Download is complete!");

                        }
                    },
                    getToken(),
                    TOKEN_ISSUER,
                    request.getHashCode(),
                    getPodSpaceServer(),
                    fileName,
                    destinationFolder,
                    downloaderErrorInterface);

            downloadCallList.put(uniqueId, call);

        } else onChatNotReady(uniqueId);

        return uniqueId;
    }

    public boolean isInCache(RequestGetPodSpaceFile request) {

        File destinationFolder;

        if (cache && request.canUseCache()) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getOrCreateDirectory(FileUtils.FILES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.FILES) : FileUtils.getPublicFilesDirectory();

        }

        showLog("Save in folder: " + destinationFolder);


        String fileName = "file_" + request.getHashCode();


        if (destinationFolder == null) {

            showErrorLog("Error Creating destination folder");

            return false;
        }


        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

            showLog("File Exist in cache: " + cachedFile);

            return true;
        }

        return false;
    }


    public String getImage(RequestGetPodSpaceImage request, ProgressHandler.IDownloadFile progressHandler) {

        String uniqueId = generateUniqueId();

        String url = getPodSpaceImageUrl(request.getHashCode());

        showLog("DOWNLOAD IMAGE: " + url);

        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);


        File destinationFolder;

        if (cache && request.canUseCache()) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getOrCreateDirectory(FileUtils.PICTURES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getPublicFilesDirectory();

        }

        showLog("Save in folder: " + destinationFolder);


        String fileName = "image_" + request.getHashCode();


        if (destinationFolder == null) {

            showErrorLog("Error Creating destination folder");

            progressHandler.onError(uniqueId, ChatConstant.ERROR_WRITING_FILE, url);

            return uniqueId;
        }


        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

            showLog("File Exist in cache: " + cachedFile);

            //file exists
            ChatResponse<ResultDownloadFile> response = PodDownloader.generatePodSpaceDownloadResult(request.getHashCode(), cachedFile);

            progressHandler.onFileReady(response);

            return uniqueId;

        }


        //only url should return in callback
        if (!hasFreeSpace) {

            showErrorLog("Download couldn't start. cause: LOW FREE SPACE");

            progressHandler.onLowFreeSpace(uniqueId, url);

            return uniqueId;
        }


        if (chatReady) {

            showLog("Download Started");


            Call call = PodDownloader.downloadImageFromPodSpace(
                    new ProgressHandler.IDownloadFile() {
                        @Override
                        public void onError(String mUniqueId, String error, String mUrl) {
                            progressHandler.onError(uniqueId, error, url);

                            showErrorLog("Download Error. cause: " + error);
                        }

                        @Override
                        public void onProgressUpdate(String mUniqueId, long bytesDownloaded, long totalBytesToDownload) {

                            progressHandler.onProgressUpdate(uniqueId, bytesDownloaded, totalBytesToDownload);


                            if (totalBytesToDownload > checkFreeSpace()) {

                                showErrorLog("Total file space is more than free space");

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
                            showLog("Download is complete!");

                        }
                    },
                    getToken(),
                    TOKEN_ISSUER,
                    request.getHashCode(),
                    getPodSpaceServer(),
                    fileName,
                    destinationFolder,
                    downloaderErrorInterface,
                    request.getSize(),
                    request.getQuality(),
                    request.getCrop());

            downloadCallList.put(uniqueId, call);

        } else onChatNotReady(uniqueId);

        return uniqueId;
    }

    public boolean isInCache(RequestGetPodSpaceImage request) {

        File destinationFolder;

        if (cache && request.canUseCache()) {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getOrCreateDirectory(FileUtils.PICTURES);

        } else {

            destinationFolder = FileUtils.getDownloadDirectory() != null ? FileUtils.getOrCreateDownloadDirectory(FileUtils.PICTURES) : FileUtils.getPublicFilesDirectory();

        }

        String fileName = "image_" + request.getHashCode();


        if (destinationFolder == null) {

            showErrorLog("Error Creating destination folder");

            return false;
        }

        File cachedFile = FileUtils.findFileInFolder(destinationFolder, fileName);

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

            showLog("File Exist in cache: " + cachedFile);

            return true;

        }

        return false;
    }


    private String getPodSpaceFileUrl(String hashCode) {
        return getPodSpaceServer() + "nzh/drive/downloadFile?hash=" + hashCode;
    }

    private String getPodSpaceImageUrl(String hashCode) {
        return getPodSpaceServer() + "nzh/drive/downloadImage?hash=" + hashCode;
    }

    private String getPodSpaceServer() {
        return podSpaceServer;
    }

    public String getFile(RequestGetFile request, ProgressHandler.IDownloadFile progressHandler) {

        String uniqueId = generateUniqueId();

        String url = getFile(request.getFileId(), request.getHashCode(), true);


        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);


        File destinationFolder;

        if (cache && request.canUseCache()) {

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

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

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

        PodDownloader.IDownloaderError downloaderErrorInterface =
                getDownloaderErrorInterface(progressHandler, uniqueId, url);


        File destinationFolder;

        if (cache && request.canUseCache()) {

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

        if (cachedFile != null && cachedFile.isFile() && request.canUseCache()) {

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

                String error = captureError(ChatConstant.ERROR_WRITING_FILE, ChatConstant.ERROR_CODE_WRITING_FILE, uniqueId);
                progressHandler.onError(uniqueId, error, url);

            }

            @Override
            public void errorOnDownloadingFile(int errorCode) {

                String error = captureError(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId);
                progressHandler.onError(uniqueId, error, url);

            }

            @Override
            public void errorUnknownException(String cause) {

                String error = captureError(ChatConstant.ERROR_DOWNLOAD_FILE, ChatConstant.ERROR_CODE_DOWNLOAD_FILE, uniqueId, new PodChatException(cause, uniqueId, token));
                progressHandler.onError(uniqueId, error, url);


            }
        };
    }

    public void setUploadTimeoutConfig(TimeoutConfig config) {

        RetrofitHelperFileServer.setTimeoutConfig(config);
    }

    public void setDownloadTimeoutConfig(TimeoutConfig config) {

        ProgressResponseBody.setTimeoutConfig(config);
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


            int result = 0;
            if (downloadManager != null) {
                result = downloadManager.remove(downloadQList.get(uniqueId));
            }

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


        if (signalMessageHandlerThread != null)
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

        setCallBacks(null, null, null, true, Constants.RENAME, null, uniqueId);
        String asyncContent = ThreadManager.prepareRenameThreadRequest(threadId, title, uniqueId, getTypeCode(), getToken());
        sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_RENAME_THREAD");
        if (handler != null) {
            handler.onRenameThread(uniqueId);
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

            String content = ParticipantsManager.prepareAddParticipantsRequest(request, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.ADD_PARTICIPANT, null, uniqueId);
            sendAsyncMessage(content, AsyncAckType.Constants.WITHOUT_ACK, "SEND_ADD_PARTICIPANTS");
            if (handler != null) {
                handler.onAddParticipants(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

            String asyncContent = ParticipantsManager.prepareRemoveParticipantsRequest(threadId, participantIds, uniqueId, getTypeCode(), getToken());

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REMOVE_PARTICIPANT");
            setCallBacks(null, null, null, true, Constants.REMOVE_PARTICIPANT, null, uniqueId);
            if (handler != null) {
                handler.onRemoveParticipants(uniqueId);
            }

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
    public String leaveThread(long threadId, boolean clearHistory, ChatHandler handler) {
        String uniqueId = generateUniqueId();
        if (chatReady) {
            String asyncContent = ThreadManager.prepareleaveThreadRequest(threadId, clearHistory, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.LEAVE_THREAD, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_LEAVE_THREAD");

            if (handler != null) {
                handler.onLeaveThread(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * leaves the thread
     *
     * @ param threadId id of the thread
     */
    public String leaveThread(RequestLeaveThread request, ChatHandler handler) {

        return leaveThread(request.getThreadId(), request.clearHistory(), handler);
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
            callbacks.add(MessageManager.generateCallback(uniqueId));
            /*
             * add to message Queue
             * */
            SendingQueueCache sendingQueue = MessageManager.generateSendingQueueCache(threadId, messageId, uniqueId, getTypeCode(), getToken());
            insertToSendQueue(uniqueId, sendingQueue);
        }


        if (log)
            Log.i(TAG, "Messages " + messageIds + "with this" + "uniqueIds" + uniqueIds + "has been added to Message Queue");

        if (chatReady) {

            threadCallbacks.put(threadId, callbacks);
            String jsonUniqueIds = Util.listToJson(uniqueIds, gson);

            String asyncContent = MessageManager.generateForwardMessage(threadId, messageIds.toString(), jsonUniqueIds, getTypeCode(), getToken());

            for (String uniqueId : uniqueIds) {
                moveFromSendingQueueToWaitQueue(uniqueId);
            }
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_FORWARD_MESSAGE");
        } else {
            if (Util.isNullOrEmpty(uniqueIds)) {
                for (String uniqueId : uniqueIds) {
                    captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }
            }
        }
        return uniqueIds;
    }

    private void moveFromSendingQueueToWaitQueue(String uniqueId) {
        SendingQueueCache queueCache;
        if (cache) {
//            messageDatabaseHelper.moveFromSendQueueToWaitQueue(uniqueId);
            dataSource.moveFromSendingToWaitingQueue(uniqueId);
        } else {
            queueCache = sendingQList.get(uniqueId);
            if (queueCache != null) {
                WaitQueueCache waitMessageQueue = getWaitQueueCacheFromSendQueue(queueCache, uniqueId);
                waitQList.put(uniqueId, waitMessageQueue);
            }
        }
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

        if (needReadStoragePermission(request.getActivity())) {

            String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                    , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION, ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }

            return uniqueId;

        }

        if (!chatReady) {

            onChatNotReady(uniqueId);

            return uniqueId;
        }

        if (getPodSpaceServer() == null) {

            captureError("PodSpace server is null", 0, uniqueId);

            return uniqueId;
        }

        long threadId = request.getThreadId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        Uri fileUri = request.getFileUri();
        long messageId = request.getMessageId();
        int messageType = request.getMessageType();
        String methodName = ChatConstant.METHOD_REPLY_MSG;

        try {
            Subscription subscription = PodUploader.uploadToPodSpace(
                    uniqueId, request.getFileUri(),
                    request.getUserGroupHashCode(), context,
                    getPodSpaceServer(),
                    getToken(),
                    TOKEN_ISSUER,
                    request.getImageXc(),
                    request.getImageYc(),
                    request.getImageHc(),
                    request.getImageWc(),
                    new PodUploader.IPodUploadFileToPodSpace() {
                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId);

                            String json = gson.toJson(chatResponse);
                            showLog("FILE_UPLOADED_TO_SERVER", json);
                            listenerManager.callOnUploadFile(json, chatResponse);

                            if (handler != null) {
                                handler.onFinishFile(json, chatResponse);
                            }

                            String jsonMeta = createFileMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    mimeType,
                                    length,
                                    response.getParentHash());

                            showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);
                            mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, jsonMeta, uniqueId, null);

                        }


                        @Override
                        public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                            removeFromUploadQueue(uniqueId);

                            ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, uniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                            String imageJson = gson.toJson(chatResponse);

                            listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            showLog("RECEIVE_UPLOAD_IMAGE", imageJson);


                            String jsonMeta = createImageMetadata(
                                    file,
                                    response.getHashCode(),
                                    0,
                                    height,
                                    width,
                                    mimeType,
                                    length,
                                    response.getParentHash(),
                                    false,
                                    null);


                            showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);
                            mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, jsonMeta, uniqueId, null);

                        }

                        @Override
                        public void onFailure(String cause, Throwable t) {

                            String jsonError = captureError(cause
                                    , ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId, t);
                            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }

                        }

                        @Override
                        public void onUploadStarted(String mimeType, File file, long length) {

                            addToUploadQueue(messageContent,
                                    fileUri,
                                    messageType,
                                    threadId,
                                    request.getUserGroupHashCode(),
                                    uniqueId,
                                    systemMetaData,
                                    messageId,
                                    mimeType,
                                    null, methodName, file, length);
                            showLog("UPLOAD_FILE_TO_SERVER");

                        }

                        @Override
                        public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                            if (handler != null)
                                handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                        }

                    }
            );

            initCancelUpload(uniqueId, subscription);

        } catch (Exception e) {
            String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                    , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId, e);
            ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
            if (handler != null) {
                handler.onError(jsonError, error);
            }
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
     * @param messageId    Id of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u don't want
     *                     you can set it false or even null.
     */

    private String deleteMessage(Long messageId, Boolean deleteForAll, ChatHandler handler) {

//        Log.d(MTAG, "Single Message delete");
        String uniqueId;
        uniqueId = generateUniqueId();

        if (chatReady) {
            deleteForAll = deleteForAll != null ? deleteForAll : false;

            AsyncMessage asyncMessage = MessageManager.generateDeleteMessageRequest(deleteForAll, uniqueId, messageId, getTypeCode(), getToken());

            sendAsyncMessage(gson.toJson(asyncMessage), AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELETE_MESSAGE");

            setCallBacks(null, null, null, true, Constants.DELETE_MESSAGE, null, uniqueId);

            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

            return captureError(ChatConstant.ERROR_NUMBER_MESSAGEID, ChatConstant.ERROR_CODE_NUMBER_MESSAGE_ID, null);
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

        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();

        List<Long> messageIds = request.getMessageIds();

        if (chatReady) {


            boolean deleteForAll = request.isDeleteForAll();

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

            contentObj.add("ids", messageIdsElement.getAsJsonArray());
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
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELETE_MULTIPLE_MESSAGE");


            if (handler != null) {
                handler.onDeleteMessage(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueIds;

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


    private String getThreads(Integer count, Long offset,
                              ArrayList<Integer> threadIds,
                              String threadName, long creatorCoreUserId,
                              long partnerCoreUserId,
                              long partnerCoreContactId,
                              boolean isNew,
                              boolean useCache,
                              String typeCode,
                              ChatHandler handler) {

        String uniqueId = generateUniqueId();

        count = count != null && count > 0 ? count : 50;

        offset = offset != null ? offset : 0;

        try {

            Integer finalCount = count;
            Long finalOffset = offset;


            Runnable getThreadsFromServerJob = () -> {

                if (chatReady) {

                    String asyncMessage = ThreadManager.prepareGetThreadRequest(isNew, finalCount, finalOffset, threadName, threadIds, creatorCoreUserId, partnerCoreUserId, partnerCoreContactId, uniqueId, typeCode, getTypeCode(), token);

                    setCallBacks(null, null, null, true, Constants.GET_THREADS, finalOffset, uniqueId);

                    sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREADS");

                    if (handler != null) {
                        handler.onGetThread(uniqueId);
                    }
                } else {
                    captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }

            };

            Runnable getThreadsFromCacheJob = () -> loadThreadsFromCache(finalCount,
                    finalOffset,
                    threadIds,
                    threadName,
                    isNew,
                    uniqueId);

            if (cache && useCache) {

//                new PodThreadManager()
//                        .addNewTask(getThreadsFromCacheJob)
//                        .addNewTask(getThreadsFromServerJob)
//                        .runTasksSynced();

                dataSource.getThreadData(
                        finalCount,
                        finalOffset,
                        threadIds,
                        threadName,
                        isNew)
                        .doOnCompleted(getThreadsFromServerJob::run)
                        .subscribe(response -> {
                            if (response != null && Util.isNotNullOrEmpty(response.getThreadList())) {
                                String threadJson = publishThreadsList(uniqueId, finalOffset, response);
                                showLog("CACHE_GET_THREAD", threadJson);
                            }
                        });


            } else {

                getThreadsFromServerJob.run();

            }


        } catch (Throwable e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
        }
        return uniqueId;
    }

//    /**
//     * Get the list of threads or you can just pass the thread id that you want
//     *
//     * @param creatorCoreUserId    if it sets to '0' its considered as it was'nt set
//     * @param partnerCoreUserId    if it sets to '0' its considered as it was'nt set -
//     *                             it gets threads of p2p not groups
//     * @param partnerCoreContactId if it sets to '0' its considered as it was'nt set-
//     *                             it gets threads of p2p not groups
//     * @param count                Count of the list
//     * @param offset               Offset of the list
//     * @param handler              Its not working yet
//     * @param threadIds            List of thread ids that you want to get
//     * @param threadName           Name of the thread that you want to get
//     */
//    @Deprecated
//    public String getThreads(Integer count,
//                             Long offset, ArrayList<Integer> threadIds,
//                             String threadName,
//                             long creatorCoreUserId,
//                             long partnerCoreUserId,
//                             long partnerCoreContactId,
//                             ChatHandler handler) {
//
//        String uniqueId;
//        count = count != null ? count : 50;
//        count = count > 0 ? count : 50;
//        uniqueId = generateUniqueId();
//        try {
//
//            if (cache) {
//
//                loadThreadsFromCache(count, offset, threadIds, threadName, false, uniqueId);
//
//            }
//
//            if (chatReady) {
//                ChatMessageContent chatMessageContent = new ChatMessageContent();
//
//                Long offsets;
//                if (offset != null) {
//                    chatMessageContent.setOffset(offset);
//                    offsets = offset;
//                } else {
//                    chatMessageContent.setOffset(0);
//                    offsets = 0L;
//                }
//
//                chatMessageContent.setCount(count);
//                if (threadName != null) {
//                    chatMessageContent.setName(threadName);
//                }
//                JsonObject jObj;
//
//                if (threadIds != null && threadIds.size() > 0) {
//                    chatMessageContent.setThreadIds(threadIds);
//                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
//
//                } else {
//                    jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
//                    jObj.remove("threadIds");
//                }
//
//
//                if (creatorCoreUserId > 0) {
//                    jObj.addProperty("creatorCoreUserId", creatorCoreUserId);
//                }
//                if (partnerCoreUserId > 0) {
//                    jObj.addProperty("partnerCoreUserId", partnerCoreUserId);
//                }
//                if (partnerCoreContactId > 0) {
//                    jObj.addProperty("partnerCoreContactId", partnerCoreContactId);
//                }
//
//                jObj.remove("lastMessageId");
//                jObj.remove("firstMessageId");
////                jObj.remove("new");
//
//
////                jObj.addProperty("summary", true);
//
//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setContent(jObj.toString());
//                chatMessage.setType(Constants.GET_THREADS);
//                chatMessage.setTokenIssuer("1");
//                chatMessage.setToken(getToken());
//                chatMessage.setUniqueId(uniqueId);
//
//
//                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
//
//                if (Util.isNullOrEmpty(getTypeCode())) {
//                    jsonObject.remove("typeCode");
//                } else {
//                    jsonObject.remove("typeCode");
//                    jsonObject.addProperty("typeCode", getTypeCode());
//                }
//
//                setCallBacks(null, null, null, true, Constants.GET_THREADS, offsets, uniqueId);
//
//
//                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREADS");
//                if (handler != null) {
//                    handler.onGetThread(uniqueId);
//                }
//            } else {
//                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            }
//
//        } catch (Throwable e) {
//            Log.e(TAG, e.getMessage());
//        }
//        return uniqueId;
//    }


    private void getAllThreads() {

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
                            showErrorLog(e.getMessage());
                            onUnknownException(uniqueId, e);
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
        boolean useCache = requestThread.useCacheData();
        String typeCode = requestThread.getTypeCode();


        return getThreads((int) count,
                offset,
                threadIds,
                threadName,
                creatorCoreUserId,
                partnerCoreUserId,
                partnerCoreContactId,
                isNew,
                useCache,
                typeCode,
                handler);
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
    private String getHistory(History history,
                              long threadId,
                              boolean useCache,
                              ChatHandler handler) {

        String mainUniqueId = generateUniqueId();

        history.setCount(history.getCount() > 0 ? history.getCount() : 50);

        //updating waitQ ( list or db )

        updateWaitingQ(threadId, mainUniqueId, new ChatHandler() {
            @Override
            public void onGetHistory(String uniqueId) {
                super.onGetHistory(uniqueId);


                if (cache && useCache && history.getMetadataCriteria() == null) {

                    final boolean[] loadFromCache = {true};

                    getHistoryFromCache(history, threadId, uniqueId)
                            .filter(messages -> !MessageManager.hasGap(messages))
                            .subscribe(messagesFromCache -> {

                                if (Util.isNullOrEmpty(messagesFromCache)) {
                                    loadFromCache[0] = false;
                                }

                                if (chatReady) {

                                    getHistoryMain(history, threadId, new ChatHandler() {

                                        @Override
                                        public void onGetHistory(ChatResponse<ResultHistory> chatResponse, ChatMessage chatMessage, Callback callback) {
                                            super.onGetHistory(chatResponse, chatMessage, callback);

                                            if (!chatResponse.getUniqueId().equals(uniqueId)) {

                                                showLog("This response has not been requested!");

                                                return;
                                            }

                                            //insert new messages
                                            updateChatHistoryCache(callback, chatMessage, chatResponse.getResult().getHistory());

                                            //get inserted message
                                            List<MessageVO> newMessagesFromServer = new ArrayList<>(chatResponse.getResult().getHistory());

                                            findAndUpdateGaps(newMessagesFromServer, threadId);


                                            if (loadFromCache[0]) {

                                                List<MessageVO> editedMessages = new ArrayList<>(newMessagesFromServer);

                                                List<MessageVO> newMessages = new ArrayList<>(newMessagesFromServer);

                                                newMessages.removeAll(messagesFromCache);

                                                findDeletedMessages(messagesFromCache, newMessagesFromServer, uniqueId, threadId);

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
                                    captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                                }
                            });

                } else if (chatReady) {
                    getHistoryMain(history, threadId, handler, uniqueId);
                } else {
                    captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }
            }
        });
        return mainUniqueId;
    }

//    @Deprecated
//    private List<MessageVO> getHistoryFromCache(History history, long threadId, String uniqueId) {
//
//        List<MessageVO> messageVOS = messageDatabaseHelper.getHistories(history, threadId);
//
//        if (messageVOS != null) {
//
//            long contentCount = messageDatabaseHelper.getHistoryContentCount(threadId);
//
//            ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();
//            chatResponse.setCache(true);
//
//            ResultHistory resultHistory = new ResultHistory();
//            resultHistory.setHistory(messageVOS);
//
//            resultHistory.setNextOffset(history.getOffset() + messageVOS.size());
//            resultHistory.setContentCount(contentCount);
//            if (messageVOS.size() + history.getOffset() < contentCount) {
//                resultHistory.setHasNext(true);
//            } else {
//                resultHistory.setHasNext(false);
//            }
//
//            resultHistory.setHistory(messageVOS);
//
//            resultHistory.setSending(messageDatabaseHelper.getAllSendingQueueByThreadId(threadId));
//            resultHistory.setUploadingQueue(messageDatabaseHelper.getAllUploadingQueueByThreadId(threadId));
//            resultHistory.setFailed(messageDatabaseHelper.getAllWaitQueueCacheByThreadId(threadId));
//
//            chatResponse.setErrorCode(0);
//            chatResponse.setHasError(false);
//            chatResponse.setErrorMessage("");
//            chatResponse.setResult(resultHistory);
//            chatResponse.setUniqueId(uniqueId);
//            chatResponse.setCache(true);
//            chatResponse.setSubjectId(threadId);
//
//
//            String json = gson.toJson(chatResponse);
//            listenerManager.callOnGetThreadHistory(json, chatResponse);
//            showLog("CACHE_GET_HISTORY", json);
//
//            return messageVOS;
//        }
//
//
//        return messageVOS;
//    }

    private void getHistoryFromCache(History history, long threadId, String uniqueId, OnWorkDone listener) {


        dataSource.getMessagesData(history, threadId)
                .doOnError(exception -> captureError(new PodChatException(exception.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION)))
                .onErrorResumeNext(Observable.empty())
                .subscribe(historyResponse -> {
                    ChatResponse<ResultHistory> chatResponse = historyResponse.getResponse();

                    if (chatResponse != null && chatResponse.getResult().getHistory().size() > 0) {
                        chatResponse.setUniqueId(uniqueId);
                        String json = gson.toJson(chatResponse);
                        listenerManager.callOnGetThreadHistory(json, chatResponse);
                        showLog("CACHE_GET_HISTORY", json);
                        listener.onWorkDone(chatResponse.getResult().getHistory());
                    } else {
                        listener.onWorkDone(new ArrayList<>());
                    }
                });

//        messageDatabaseHelper.getHistories(history, threadId, (response) -> {
//
//            ChatResponse<ResultHistory> chatResponse = (ChatResponse<ResultHistory>) response;
//
//            if (chatResponse != null && chatResponse.getResult().getHistory().size() > 0) {
//                chatResponse.setUniqueId(uniqueId);
//                String json = gson.toJson(chatResponse);
//                listenerManager.callOnGetThreadHistory(json, chatResponse);
//                showLog("CACHE_GET_HISTORY", json);
//                listener.onWorkDone(chatResponse.getResult().getHistory());
//            } else {
//                listener.onWorkDone(new ArrayList<>());
//            }
//        });

    }


    private Observable<List<MessageVO>> getHistoryFromCache(History history, long threadId, String uniqueId) {

        return dataSource.getMessagesData(history, threadId)
                .doOnError(exception -> captureError(new PodChatException(exception.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION)))
                .onErrorResumeNext(Observable.empty())
                .map(historyResponse -> {
                    ChatResponse<ResultHistory> chatResponse = historyResponse.getResponse();

                    if (chatResponse != null && chatResponse.getResult().getHistory().size() > 0) {
                        chatResponse.setUniqueId(uniqueId);
                        String json = gson.toJson(chatResponse);
                        listenerManager.callOnGetThreadHistory(json, chatResponse);
                        showLog("CACHE_GET_HISTORY", json);
                        return chatResponse.getResult().getHistory();
                    } else {
                        return new ArrayList<>();
                    }
                });

//        messageDatabaseHelper.getHistories(history, threadId, (response) -> {
//
//            ChatResponse<ResultHistory> chatResponse = (ChatResponse<ResultHistory>) response;
//
//            if (chatResponse != null && chatResponse.getResult().getHistory().size() > 0) {
//                chatResponse.setUniqueId(uniqueId);
//                String json = gson.toJson(chatResponse);
//                listenerManager.callOnGetThreadHistory(json, chatResponse);
//                showLog("CACHE_GET_HISTORY", json);
//                listener.onWorkDone(chatResponse.getResult().getHistory());
//            } else {
//                listener.onWorkDone(new ArrayList<>());
//            }
//        });

    }

    @SuppressWarnings("unchecked")
    private void getUniqueIdsInWaitQ(OnWorkDone listener) {

        List<String> waitingQMessagesUniqueIds = new ArrayList<>();

        if (cache) {
            dataSource.getWaitQueueUniqueIdList()
                    .doOnError(exception -> {
                        if (exception instanceof RoomIntegrityException) {
                            resetCache();
                        } else {
                            showErrorLog(exception.getMessage());
                        }

                        listener.onWorkDone(null);
                    })
                    .onErrorResumeNext(Observable.empty())
                    .subscribe(uniqueIds -> {

                        if (Util.isNotNullOrEmpty(uniqueIds)) {

                            waitingQMessagesUniqueIds.addAll(uniqueIds);

                        }

                        listener.onWorkDone(waitingQMessagesUniqueIds);

                    });


        } else {
            waitingQMessagesUniqueIds.addAll(waitQList.keySet());
            listener.onWorkDone(waitingQMessagesUniqueIds);
        }
    }


    private Observable<List<String>> getUniqueIdsInWaitQ() {


        if (cache)
            return dataSource.getWaitQueueUniqueIdList();

        return Observable.from(waitQList.keySet()).toList();

    }

    //If some tables had changed, using room cause an IntegrityException
    //so we can write a migration every time something changes or "resetCache()"
    //to make it useful again. this is the resetCache:

    private void resetCache() {

        //If something bad happened during resetting DB,
        //we will stop using cache.

        cache = false;
        showLog("Reset database");
        boolean result = messageDatabaseHelper.resetDatabase();
        if (result) {
            initDatabaseWithKey(getKey());
            showLog("Database reset successfully");
            cache = true;
        } else {
            showErrorLog("Database Reset Failed");
            showLog("Please clear app data");
        }
    }

    private void resetCache(Runnable onDone) {
        cache = false;
        showLog("Reset database");
        boolean result = messageDatabaseHelper.resetDatabase();
        if (result) {
            initDatabaseWithKey(getKey());
            showLog("Database reset successfully");
            cache = true;
            onDone.run();
        } else {
            showErrorLog("Database Reset Failed");
            showLog("Please clear app data");
        }
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

        History history = getHistoryModelFromRequestGetHistory(request);

        return getHistory(history, request.getThreadId(),
                request.useCacheData(),
                handler);

    }

    private History getHistoryModelFromRequestGetHistory(RequestGetHistory request) {
        return new History.Builder()
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
                .setMessageType(request.getMessageType())
                .order(request.getOrder() != null ? request.getOrder() : "desc").build();
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


                dataSource.updateMessage(lastMessage, threadId);

//                messageDatabaseHelper.updateMessage(lastMessage, threadId);

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


        PodThreadManager podThreadManager = new PodThreadManager();
        podThreadManager.addTask(jobFindAndInsertGap);
        podThreadManager.addTask(jobUpdateGaps);
        podThreadManager.runTasksSynced();

    }

    private void publishNewMessages(List<MessageVO> newMessages, long threadId, String uniqueId) {


        for (MessageVO messageVO :
                newMessages) {

            try {

                ChatResponse<ResultNewMessage> chatResponse = MessageManager.preparepublishNewMessagesResponse(messageVO, threadId);//gson.toJson(chatResponse);

                String json = gson.toJson(chatResponse);
                listenerManager.callOnNewMessage(json, chatResponse);
                long ownerId = 0;
                if (messageVO.getParticipant() != null) {
                    ownerId = messageVO.getParticipant().getId();
                }
                showLog("RECEIVED_NEW_MESSAGE", json);
                if (ownerId > 0 && ownerId != getUserId()) {

                    ChatMessage message = getChatMessage(messageVO);
                    String asyncContent = gson.toJson(message);
                    async.sendMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK);
                    setThreadCallbacks(threadId, uniqueId);
                    showLog("SEND_DELIVERY_MESSAGE", asyncContent);
                }

            } catch (Exception e) {
                showErrorLog(e.getMessage());
                onUnknownException(uniqueId, e);

            }

        }


    }


    private void findDeletedMessages(List<MessageVO> messagesFromCache, List<MessageVO> newMessagesFromServer, String uniqueId, long threadId) {


        for (MessageVO msg :
                messagesFromCache) {


            if (!newMessagesFromServer.contains(msg)) {

                ChatResponse<ResultDeleteMessage> chatResponse = MessageManager.prepareDeleteMessageResponseForFind(msg, uniqueId, threadId);

                String jsonDeleteMsg = gson.toJson(chatResponse);
                listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);
                showLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);

                if (cache) {

                    dataSource.deleteMessage(msg, threadId);

                    messageDatabaseHelper.deleteMessage(msg.getId(), threadId);

                    showLog("Delete message from database with this messageId" + " " + msg.getId(), "");

                }
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

                    ChatResponse<ResultNewMessage> chatResponse = MessageManager.prepareNewMessageResponse(newMessage, threadId, uniqueId);//gson.toJson(chatResponse);
                    showLog("RECEIVE_EDIT_MESSAGE", chatResponse.toString());

                    listenerManager.callOnEditedMessage(chatResponse.toString(), chatResponse);
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
            String asyncContent = SearchManager.prepareSearchRequest(messageCriteriaVO, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.GET_HISTORY, messageCriteriaVO.getOffset(), uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND SEARCH0. HISTORY");
            if (handler != null) {
                handler.onSearchHistory(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

        long offset = request.getOffset();
        long count = request.getCount();
        boolean useCache = request.useCacheData();
        String typeCode = request.getTypeCode();
        return getContactMain((int) count, offset, false, typeCode, useCache, handler);
    }


    /**
     * Get all of the contacts of the user
     */
    @Deprecated
    public String getContacts(Integer count, Long offset, ChatHandler handler) {
        return getContactMain(count, offset, false, typeCode, true, handler);
    }


//    public String searchContact(RequestSearchContact requestSearchContact) {
//
//        String uniqueId = generateUniqueId();
//
//        Runnable cacheLoading = () -> {
//
//            if (cache && requestSearchContact.canUseCache()) {
//                List<Contact> contacts = new ArrayList<>();
//                if (requestSearchContact.getId() != null) {
//                    Contact contact = messageDatabaseHelper.getContactById(Long.parseLong(requestSearchContact.getId()));
//                    contacts.add(contact);
//                } else if (requestSearchContact.getFirstName() != null) {
//                    contacts = messageDatabaseHelper.getContactsByFirst(requestSearchContact.getFirstName());
//                } else if (requestSearchContact.getFirstName() != null && requestSearchContact.getLastName() != null && !requestSearchContact.getFirstName().isEmpty() && !requestSearchContact.getLastName().isEmpty()) {
//                    contacts = messageDatabaseHelper.getContactsByFirstAndLast(requestSearchContact.getFirstName(), requestSearchContact.getLastName());
//                } else if (requestSearchContact.getEmail() != null && !requestSearchContact.getEmail().isEmpty()) {
//                    contacts = messageDatabaseHelper.getContactsByEmail(requestSearchContact.getEmail());
//                } else if (requestSearchContact.getCellphoneNumber() != null && !requestSearchContact.getCellphoneNumber().isEmpty()) {
//                    contacts = messageDatabaseHelper.getContactByCell(requestSearchContact.getCellphoneNumber());
//                }
//
//                ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
//                chatResponse.setCache(true);
//
//                ResultContact resultContact = new ResultContact();
//                ArrayList<Contact> listContact = new ArrayList<>(contacts);
//                resultContact.setContacts(listContact);
//                chatResponse.setUniqueId(uniqueId);
//                chatResponse.setHasError(false);
//                chatResponse.setErrorCode(0);
//                chatResponse.setErrorMessage("");
//                chatResponse.setResult(resultContact);
//
//                String jsonContact = gson.toJson(chatResponse);
//                listenerManager.callOnSearchContact(jsonContact, chatResponse);
//                showLog("CACHE_SEARCH_CONTACT", jsonContact);
//
//
//            }
//        };
//
//        Runnable requestServer = () -> {
//            if (chatReady) {
//
//                JsonObject jsonObject = (JsonObject) gson.toJsonTree(requestSearchContact);
//
//                jsonObject.addProperty("uniqueId", uniqueId);
//
//                jsonObject.addProperty("tokenIssuer", 1);
//
//                showLog("SEND_SEARCH_CONTACT", getJsonForLog(jsonObject));
//
//
//                Observable<Response<SearchContactVO>> observable = contactApi.searchContact(
//                        getToken(),
//                        TOKEN_ISSUER,
//                        requestSearchContact.getId()
//                        , requestSearchContact.getFirstName()
//                        , requestSearchContact.getLastName()
//                        , requestSearchContact.getEmail()
//                        , null
//                        , requestSearchContact.getOffset()
//                        , requestSearchContact.getSize()
//                        , null
//                        , requestSearchContact.getQuery()
//                        , requestSearchContact.getCellphoneNumber());
//
//                observable
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(contactResponse -> {
//
//
//                            if (contactResponse.isSuccessful()) {
//
//                                if (contactResponse.body() != null && contactResponse.body().getResult() != null) {
//
//                                    ArrayList<Contact> contacts = new ArrayList<>(contactResponse.body().getResult());
//
//                                    ResultContact resultContacts = new ResultContact();
//                                    resultContacts.setContacts(contacts);
//
//                                    ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
//                                    chatResponse.setUniqueId(uniqueId);
//                                    chatResponse.setResult(resultContacts);
//
//                                    String content = gson.toJson(chatResponse);
//
//
//                                    showLog("RECEIVE_SEARCH_CONTACT", content);
//
//                                    listenerManager.callOnSearchContact(content, chatResponse);
//
//                                }
//
//                            } else {
//
//                                if (contactResponse.body() != null) {
//                                    String errorMessage = contactResponse.body().getMessage() != null ? contactResponse.body().getMessage() : "";
//                                    int errorCode = contactResponse.body().getErrorCode() != null ? contactResponse.body().getErrorCode() : 0;
//                                    getErrorOutPut(errorMessage, errorCode, uniqueId);
//                                }
//                            }
//
//                        }, (Throwable throwable) -> Log.e(TAG, throwable.getMessage()));
//            } else {
//                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            }
//        };
//
//
//        new PodThreadManager()
//                .addNewTask(cacheLoading)
//                .addNewTask(requestServer)
//                .runTasksSynced();
//
//        return uniqueId;
//    }


    public String searchContact(RequestSearchContact requestSearchContact) {

        String uniqueId = generateUniqueId();

        String size = !Util.isNullOrEmpty(requestSearchContact.getSize()) &&
                !requestSearchContact.getSize().equals("0") ? requestSearchContact.getSize()
                : "50";

        String offset = !Util.isNullOrEmpty(requestSearchContact.getOffset()) ?
                requestSearchContact.getOffset()
                : "0";

        String order = !Util.isNullOrEmpty(requestSearchContact.getOrder()) ?
                requestSearchContact.getOrder()
                : "asc";


        Runnable cacheLoading = () -> {

            if (cache && requestSearchContact.canUseCache()) {


                ChatResponse<ResultContact> chatResponse = messageDatabaseHelper.searchContacts(requestSearchContact, size, offset);
                chatResponse.setUniqueId(uniqueId);

                String jsonContact = gson.toJson(chatResponse);
                listenerManager.callOnSearchContact(jsonContact, chatResponse);
                showLog("CACHE_SEARCH_CONTACT", jsonContact);

            }
        };


        Runnable requestServer = () -> {

            if (chatReady) {

                JsonObject jObj = new JsonObject();

                String query = requestSearchContact.getQuery();

                if (Util.isNullOrEmpty(query)) {
                    query = "";

                    if (!Util.isNullOrEmpty(requestSearchContact.getFirstName())) {
                        query += requestSearchContact.getFirstName() + " ";
                    }

                    if (!Util.isNullOrEmpty(requestSearchContact.getLastName())) {
                        query += requestSearchContact.getLastName();
                    }
                }

                if (!Util.isNullOrEmpty(query)) {
                    jObj.addProperty("query", query);
                }

                if (!Util.isNullOrEmpty(requestSearchContact.getEmail())) {
                    jObj.addProperty("email", requestSearchContact.getEmail());
                }

                if (!Util.isNullOrEmpty(requestSearchContact.getCellphoneNumber())) {
                    jObj.addProperty("cellphoneNumber", requestSearchContact.getCellphoneNumber());
                }

                if (!Util.isNullOrEmpty(requestSearchContact.getId())) {
                    jObj.addProperty("id", requestSearchContact.getId());
                }

                jObj.addProperty("size", size);

                jObj.addProperty("offset", offset);

                jObj.addProperty("order", order);

                AsyncMessage chatMessage = new AsyncMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(Constants.GET_CONTACTS);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(!Util.isNullOrEmpty(typeCode) ? typeCode : getTypeCode());

                chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.GET_CONTACTS, Long.valueOf(offset), uniqueId);

                handlerSend.put(uniqueId, new ChatHandler() {
                    @Override
                    public void onGetContact(String contactJson, ChatResponse<ResultContact> chatResponse) {
                        super.onGetContact(contactJson, chatResponse);

                        showLog("RECEIVE_SEARCH_CONTACT", contactJson);

                        listenerManager.callOnSearchContact(contactJson, chatResponse);

                        handlerSend.remove(uniqueId);

                    }
                });

                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_SEARCH_CONTACT");


            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        };


        new PodThreadManager()
                .addNewTask(cacheLoading)
                .addNewTask(requestServer)
                .runTasksSynced();

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

    @Deprecated
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

        typeCode = Util.isNullOrEmpty(typeCode) ? getTypeCode() : typeCode;


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("uniqueId", uniqueId);
        jsonObject.addProperty("tokenIssuer", 1);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        if (!Util.isNullOrEmpty(cellphoneNumber))
            jsonObject.addProperty("cellphoneNumber", cellphoneNumber);
        if (!Util.isNullOrEmpty(email))
            jsonObject.addProperty("email", email);
        if (!Util.isNullOrEmpty(typeCode))
            jsonObject.addProperty("typeCode", typeCode);
        if (!Util.isNullOrEmpty(username))
            jsonObject.addProperty("username", username);


        showLog("SEND_ADD_CONTACT", getJsonForLog(jsonObject));


        Observable<Response<Contacts>> addContactObservable;

        if (chatReady) {

            if (!Util.isNullOrEmpty(username)) {

                addContactObservable = contactApi.addContactWithUserName(getToken(), 1, firstName, lastName, username, uniqueId, typeCode, "", "");

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

//                                        messageDatabaseHelper.saveContact(chatResponse.getResult().getContact(), getExpireAmount());

                                        dataSource.saveContactResultFromServer(chatResponse.getResult().getContact());

                                    }
                                } else {
                                    captureError(contacts.getMessage(), contacts.getErrorCode(), uniqueId);
                                }
                            }
                        }
                    }, (Throwable throwable) ->
                    {
                        captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable);
                        Log.e(TAG, throwable.getMessage());
                    });
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                                    dataSource.deleteContactById(userId);
//                                    messageDatabaseHelper.deleteContactById(userId);
                                }
                                listenerManager.callOnRemoveContact(json, chatResponse);
                                showLog("RECEIVED_REMOVE_CONTACT", json);
                            } else {
                                captureError(contactRemove.getErrorMessage(), contactRemove.getErrorCode(), uniqueId);
                            }
                        }
                    }, (Throwable throwable) ->
                            captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable));
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
//                                        messageDatabaseHelper.saveContact(updateContact.getResult().get(0), getExpireAmount());
                                        dataSource.saveContactsResultFromServer(updateContact.getResult());
                                    }

                                    showLog("RECEIVE_UPDATE_CONTACT", json);
                                } else {
                                    String errorMsg = response.body().getMessage();
                                    int errorCodeMsg = response.body().getErrorCode();

                                    errorMsg = errorMsg != null ? errorMsg : "";

                                    captureError(errorMsg, errorCodeMsg, uniqueId);
                                }
                            }
                        }
                    }, (Throwable throwable) ->
                    {
                        if (throwable != null) {
                            captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable);
                        }
                    });
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                        captureError(mapNeshanResponse.message(), mapNeshanResponse.code(), uniqueId);
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
                    captureError(mapNeshanResponse.message(), mapNeshanResponse.code(), uniqueId);
                }
            }, (Throwable throwable) -> captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable));
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
            }, (Throwable throwable) -> captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId, throwable));
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                captureError(mapRoutResponse.message(), mapRoutResponse.code(), finalUniqueId);
            }
        }, (Throwable throwable) -> captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, throwable));
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
                                    File file = null;
                                    try {
                                        file = FileUtils.saveBitmap(bitmap, "map");
                                    } catch (Exception e) {
                                        captureError(ChatConstant.ERROR_WRITING_FILE,
                                                ChatConstant.ERROR_CODE_WRITING_FILE,
                                                finalUniqueId1);
                                    }

                                    if (file == null) {

                                        captureError(ChatConstant.ERROR_WRITING_FILE,
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

                            captureError(ChatConstant.ERROR_CALL_NESHAN_API,
                                    ChatConstant.ERROR_CODE_CALL_NESHAN_API, finalUniqueId);
                            showErrorLog(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        captureError(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, t);
                    }
                });


            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable);

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

                int messageType = request.getMessageType() > 0 ? request.getMessageType() : TextMessageType.Constants.POD_SPACE_PICTURE;


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

                                showLog("RECEIVE_MAP_STATIC");

                                if (!call.isCanceled()) {
                                    call.cancel();
                                }

                                if (isMessage) {

                                    File file = null;
                                    try {
                                        file = FileUtils.saveBitmap(bitmap, "map");
                                    } catch (Exception e) {
                                        captureError(ChatConstant.ERROR_WRITING_FILE,
                                                ChatConstant.ERROR_CODE_WRITING_FILE,
                                                finalUniqueId, e);
                                    }
                                    if (file == null) {

                                        captureError(ChatConstant.ERROR_WRITING_FILE,
                                                ChatConstant.ERROR_CODE_WRITING_FILE,
                                                finalUniqueId);

                                        return;
                                    }
                                    Uri fileUri = Uri.fromFile(file);


                                    if (!chatReady) {

                                        onChatNotReady(finalUniqueId);

                                        return;
                                    }

                                    if (getPodSpaceServer() == null) {

                                        captureError("PodSpace server is null", 0, finalUniqueId);

                                        return;
                                    }

                                    removeFromUploadQueue(finalUniqueId);

                                    try {

                                        Subscription subscription = PodUploader.uploadToPodSpace(
                                                finalUniqueId, fileUri,
                                                request.getUserGroupHash(),
                                                context,
                                                getPodSpaceServer(),
                                                getToken(),
                                                TOKEN_ISSUER,
                                                new PodUploader.IPodUploadFileToPodSpace() {
                                                    @Override
                                                    public void onSuccess(UploadToPodSpaceResult response, File file, String mimeType, long length, int actualWidth, int actualHeight, int width, int height) {

                                                        removeFromUploadQueue(finalUniqueId);

                                                        ChatResponse<ResultImageFile> chatResponse = PodUploader.generateImageUploadResultForSendMessage(response, finalUniqueId, actualWidth, actualHeight, width, height, getPodSpaceImageUrl(response.getHashCode()));

                                                        String imageJson = gson.toJson(chatResponse);

                                                        listenerManager.callOnUploadImageFile(imageJson, chatResponse);

                                                        if (handler != null) {
                                                            handler.onFinishImage(imageJson, chatResponse);
                                                        }

                                                        showLog("RECEIVE_UPLOAD_IMAGE", imageJson);


                                                        String jsonMeta = createImageMetadata(
                                                                file,
                                                                response.getHashCode(),
                                                                0,
                                                                height,
                                                                width,
                                                                mimeType,
                                                                length,
                                                                response.getParentHash(),
                                                                false,
                                                                null);


                                                        sendTextMessageWithFile(request.getMessage(), threadId, jsonMeta, systemMetadata, finalUniqueId, typeCode, messageType);

                                                    }

                                                    @Override
                                                    public void onFailure(String cause, Throwable t) {

                                                        String jsonError = captureError(cause
                                                                , ChatConstant.ERROR_CODE_UPLOAD_FILE, finalUniqueId, t);
                                                        ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, finalUniqueId);
                                                        if (handler != null) {
                                                            handler.onError(jsonError, error);
                                                        }
                                                    }

                                                    @Override
                                                    public void onUploadStarted(String mimeType, File file, long length) {
                                                        addToUploadQueue(request.getMessage(),
                                                                fileUri,
                                                                messageType,
                                                                threadId,
                                                                request.getUserGroupHash(),
                                                                finalUniqueId,
                                                                systemMetadata,
                                                                request.getMessageId(),
                                                                mimeType,
                                                                request.getCenter(),
                                                                ChatConstant.METHOD_LOCATION_MSG,
                                                                file,
                                                                length);
                                                        showLog("UPLOAD_FILE_TO_SERVER");
                                                    }

                                                    @Override
                                                    public void onProgressUpdate(int progress, int totalBytesSent, int totalBytesToSend) {

                                                        if (handler != null)
                                                            handler.onProgressUpdate(finalUniqueId, progress, totalBytesSent, totalBytesToSend);
                                                    }
                                                }
                                        );

                                        initCancelUpload(finalUniqueId, subscription);


                                    } catch (Exception e) {
                                        String jsonError = captureError(ChatConstant.ERROR_INVALID_FILE_URI
                                                , ChatConstant.ERROR_CODE_INVALID_FILE_URI, finalUniqueId, e);
                                        ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, finalUniqueId);
                                        if (handler != null) {
                                            handler.onError(jsonError, error);
                                        }
                                    }

                                }
                            }
                        } else {


                            captureError(ChatConstant.ERROR_CALL_NESHAN_API,
                                    ChatConstant.ERROR_CODE_CALL_NESHAN_API, finalUniqueId);
                            showErrorLog(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        captureError(t.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, t);
                    }
                });
            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
            onUnknownException(uniqueId, throwable);
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

                                captureError(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId);

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
                                    captureError(errorMessage, errorCode, finalUniqueId);
                                }

                            } catch (JSONException e) {

                                captureError(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, e);
                            } catch (IOException e) {
                                captureError(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, e);

                            }
                        }
                    }, (Throwable throwable) -> captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, finalUniqueId, throwable));
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

            String asyncContent = ContactManager.prepareBlockRequest(contactId, userId, threadId, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.BLOCK, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_BLOCK");
            if (handler != null) {
                handler.onBlock(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


            String asyncContent = ContactManager.prepareUnBlockRequest(blockId
                    , userId
                    , threadId
                    , contactId, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.UNBLOCK, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_UN_BLOCK");
            if (handler != null) {
                handler.onUnBlock(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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

            String content = MessageManager.prepareSpamRequest(uniqueId, getTypeCode(), threadId, getToken());

            setCallBacks(null, null, null, true, Constants.SPAM_PV_THREAD, null, uniqueId);
            sendAsyncMessage(content, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPORT_SPAM");
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * If someone that is not in your contact list tries to send message to you
     * their spam value is true and you can call this method in order to report them.
     *
     * @ param long threadId Id of the thread
     */
    public String spam(RequestSpam request) {

        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (chatReady) {

                String content = MessageManager.prepareSpamRequest(uniqueId, request, getTypeCode(), getToken());

                setCallBacks(null, null, null, true, Constants.SPAM_PV_THREAD, null, uniqueId);

                sendAsyncMessage(content, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPORT_SPAM");
            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
        }
        return uniqueId;
    }


    /**
     * It gets the list of the contacts that is on block list
     * <p>
     * it is deprecated and will be removed
     */
    @Deprecated
    public String getBlockList(Long count, Long offset, ChatHandler handler) {

        String uniqueId = generateUniqueId();


        if (cache) {

            List<BlockedContact> cacheContacts = messageDatabaseHelper.getBlockedContacts(count, offset);
            if (!Util.isNullOrEmpty(cacheContacts)) {


                ChatResponse<ResultBlockList> chatResponse = ContactManager.prepareGetBlockList_Cach(uniqueId, cacheContacts);

                listenerManager.callOnGetBlockList(gson.toJson(chatResponse), chatResponse);
                showLog("RECEIVE_GET_BLOCK_LIST_FROM_CACHE", gson.toJson(chatResponse));
            }
        }


        if (chatReady) {

            String asyncContent = ContactManager.prepareGetBlockListRequest(count, offset, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.GET_BLOCKED, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_BLOCK_LIST");
            if (handler != null) {
                handler.onGetBlockList(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }


    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(RequestBlockList request, ChatHandler handler) {

        String uniqueId = generateUniqueId();

        PodThreadManager threadManager = new PodThreadManager();

        threadManager.addTask(() -> {
            if (cache && request.useCacheData()) {

                List<BlockedContact> cacheContacts = messageDatabaseHelper.getBlockedContacts(request.getCount(), request.getOffset());
                if (!Util.isNullOrEmpty(cacheContacts)) {

                    ChatResponse<ResultBlockList> chatResponse = ContactManager.prepareGetBlockList_Cach(uniqueId, cacheContacts);
                    listenerManager.callOnGetBlockList(gson.toJson(chatResponse), chatResponse);
                    showLog("RECEIVE_GET_BLOCK_LIST_FROM_CACHE", gson.toJson(chatResponse));
                }
            }

        });


        threadManager.addTask(() -> {
            if (chatReady) {

                String asyncContent = ContactManager.prepareGetBlockListRequest(request.getCount(), request.getOffset(), uniqueId, getTypeCode(), getToken());

                setCallBacks(null, null, null, true, Constants.GET_BLOCKED, null, uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_BLOCK_LIST");
                if (handler != null) {
                    handler.onGetBlockList(uniqueId);
                }
            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        });


        threadManager.runTasksSynced();

        return uniqueId;
    }


    public String createThread(RequestCreateThread request) {

        String uniqueId = generateUniqueId();

        if (chatReady) {

            String asyncMessage = ThreadManager.prepareCreateThread(request, uniqueId, getTypeCode(), getToken());
            //if upload thread image request is set
            //upload selected image and updateThreadInfo

            if (request.getUploadThreadImageRequest() != null) {

                handlerSend.put(uniqueId, new ChatHandler() {
                    @Override
                    public void onThreadCreated(ResultThread thread) {
                        super.onThreadCreated(thread);

                        updateThreadImage(thread, request.getUploadThreadImageRequest());

                    }

                });
            }

            sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD");

        } else {
            onChatNotReady(uniqueId);
        }

        return uniqueId;
    }

    private void updateThreadImage(ResultThread thread, RequestUploadImage uploadImageRequest) {


        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder()
                .title(thread.getThread().getTitle())
                .description(thread.getThread().getDescription())
                .image(thread.getThread().getImage())
                .metadata(thread.getThread().getMetadata())
                .build();


        updateThreadInfo(thread.getThread().getId(),
                threadInfoVO, thread.getThread().getUserGroupHash(),
                uploadImageRequest, null);


    }

    private String createThread(RequestCreateThread request, String uniqueId) {

        if (chatReady) {
            String asyncMessage = ThreadManager.prepareCreateThread(request, uniqueId, getTypeCode(), getToken());
            sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD");
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

    public String getAllUnreadMessagesCount(RequestGetUnreadMessagesCount request) {

        String uniqueId = generateUniqueId();


        if (cache && request.useCacheData()) {

            loadUnreadMessagesCountFromCache(request, uniqueId);

            return uniqueId;

        }

        if (chatReady) {

            String asyncMessage = MessageManager.getAllUnreadMessgesCount(request, uniqueId);

            sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND GET UNREAD MESSAGES COUNT");

        } else onChatNotReady(uniqueId);


        return uniqueId;

    }

    private void loadUnreadMessagesCountFromCache(RequestGetUnreadMessagesCount request, String uniqueId) {

        try {
            messageDatabaseHelper.loadAllUnreadMessagesCount(request, count -> {

                ChatResponse<ResultUnreadMessagesCount> response =
                        MessageManager.handleUnreadMessagesCacheResponse((Long) count);

                listenerManager.callOnGetUnreadMessagesCount(response);

                showLog("ON GET UNREAD MESSAGES COUNT FROM CACHE", gson.toJson(response));

                if (chatReady) {

                    String asyncMessage = MessageManager.getAllUnreadMessgesCount(request, uniqueId);

                    sendAsyncMessage(asyncMessage, AsyncAckType.Constants.WITHOUT_ACK, "SEND GET UNREAD MESSAGES COUNT");

                } else onChatNotReady(uniqueId);


            });
        } catch (RoomIntegrityException e) {

            resetCache();
        }
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
    public String createThread(int threadType,
                               Invitee[] invitee,
                               String threadTitle,
                               String description,
                               String image
            , String metadata, ChatHandler handler) {

        String uniqueId;
        uniqueId = generateUniqueId();

        if (chatReady) {


            String asyncContent = ThreadManager.prepareCreateThread(threadType, invitee, threadTitle, description, image, metadata, uniqueId, getTypeCode(), getToken());

            setCallBacks(null, null, null, true, Constants.INVITATION, null, uniqueId);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD");
            if (handler != null) {
                handler.onCreateThread(uniqueId);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }


    //
    // 1. Create thread.
    // 2.1. Upload thread image and update thread info if RequestUploadImage is set.
    // 2.2. Upload file to server with thread userGroupHash and send uploaded file as message by sendFileMessage.
    //

    public ArrayList<String> createThreadWithFile(RequestCreateThreadWithFile request, @Nullable ProgressHandler.sendFileMessage progressHandler) {

        ArrayList<String> uniqueIds = new ArrayList<>();

        String requestUniqueId = generateUniqueId();

        uniqueIds.add(requestUniqueId);

        if (needReadStoragePermission(request.getFile().getActivity())) return uniqueIds;

        if (chatReady) {

            handlerSend.put(requestUniqueId, new ChatHandler() {
                @Override
                public void onThreadCreated(ResultThread thread) {
                    super.onThreadCreated(thread);


                    if (request.getUploadThreadImageRequest() != null)
                        new PodThreadManager()
                                .doThisAndGo(() -> updateThreadImage(thread, request.getUploadThreadImageRequest()));

                    RequestFileMessage.Builder requestFileBuilder =
                            new RequestFileMessage.Builder(
                                    request.getMessage() != null ? request.getMessage().getText() : null,
                                    request.getFile().getActivity(),
                                    thread.getThread().getId(),
                                    request.getFile().getFileUri(),
                                    request.getMessage() != null ? request.getMessage().getSystemMetadata() : null,
                                    request.getMessageType(),
                                    thread.getThread().getUserGroupHash());


                    if (request.getFile() instanceof RequestUploadImage) {

                        requestFileBuilder.setImageHc(String.valueOf(((RequestUploadImage) request.getFile()).gethC()));
                        requestFileBuilder.setImageWc(String.valueOf(((RequestUploadImage) request.getFile()).getwC()));
                        requestFileBuilder.setImageXc(String.valueOf(((RequestUploadImage) request.getFile()).getxC()));
                        requestFileBuilder.setImageYc(String.valueOf(((RequestUploadImage) request.getFile()).getyC()));

                    }

                    RequestFileMessage requestFile = requestFileBuilder.build();

                    sendFileMessage(requestFile, requestUniqueId, progressHandler);

                }
            });

            RequestCreateThread requestCreateThread =
                    new RequestCreateThread.Builder(
                            request.getType(),
                            request.getInvitees())
                            .title(request.getTitle())
                            .withDescription(request.getDescription())
                            .withImage(request.getImage())
                            .withMetadata(request.getMessage() != null ? request.getMessage().getSystemMetadata() : null)
                            .build();

            createThread(requestCreateThread, requestUniqueId);

//            prepareCreateThreadWithFile(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds, "");


        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, requestUniqueId);
        }

        return uniqueIds;
    }

    private void prepareCreateThreadWithFile(RequestCreateThreadWithFile request, String requestUniqueId, String innerMessageUniqueId, List<String> forwardUniqueIds, String metaData) {

        RequestThreadInnerMessage requestThreadInnerMessage = generateInnerMessageForCreateThreadWithFile(request, metaData);

        request.setMessage(requestThreadInnerMessage);

        request.setMessageType(request.getMessageType());

        request.setFile(null);


//        createThreadWithMessage(request, requestUniqueId, innerMessageUniqueId, forwardUniqueIds);
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


                handlerSend.put(requestUniqueId, new ChatHandler() {

                });

                sendAsyncMessage(asyncRequestObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD_WITH_FILE");

            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, requestUniqueId);
            }

        } catch (Exception e) {
            captureError(ChatConstant.ERROR_UNKNOWN_EXCEPTION, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, requestUniqueId, e);
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


                if (threadRequest.getUploadThreadImageRequest() != null) {


                    handlerSend.put(threadUniqueId, new ChatHandler() {
                        @Override
                        public void onThreadCreated(ResultThread thread) {
                            super.onThreadCreated(thread);

                            updateThreadImage(thread, threadRequest.getUploadThreadImageRequest());

                        }
                    });


                }


                sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {


                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);

            }

        } catch (Throwable e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueIds.get(0), e);
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
    @Deprecated
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

                AsyncMessage chatMessage = new AsyncMessage();
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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
        }

        return uniqueId;
    }

    private String updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, String userGroupHash, RequestUploadImage requestUploadThreadPicture, ChatHandler handler) {

        String uniqueId = generateUniqueId();

        try {

            if (Util.isNullOrEmpty(userGroupHash)) {

                captureError(ChatConstant.ERROR_INVALID_USER_GROUP_HASH,
                        ChatConstant.ERROR_CODE_INVALID_USER_GROUP_HASH, uniqueId);

                return uniqueId;
            }

            if (chatReady) {

                JsonObject threadMetadataObject;

                if (Util.isNullOrEmpty(threadInfoVO.getMetadata())) {

                    threadMetadataObject = new JsonObject();

                } else {
                    threadMetadataObject = parser.parse(threadInfoVO.getMetadata()).getAsJsonObject();

                }

                JsonObject contentObject = new JsonObject();
                contentObject.addProperty("name", threadInfoVO.getTitle());
                contentObject.addProperty("description", threadInfoVO.getDescription());

                Runnable updateThreadTask = () -> {

                    String content = contentObject.toString();

                    AsyncMessage chatMessage = new AsyncMessage();
                    chatMessage.setContent(content);
                    chatMessage.setTokenIssuer("1");
                    chatMessage.setToken(getToken());
                    chatMessage.setSubjectId(threadId);
                    chatMessage.setUniqueId(uniqueId);
                    chatMessage.setType(Constants.UPDATE_THREAD_INFO);
                    JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                    jsonObject.remove("contentCount");
                    jsonObject.remove("systemMetadata");
//                    jsonObject.remove("metadata");
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

                };

                uploadImageToThread(requestUploadThreadPicture, userGroupHash, uniqueId, null, hashCode -> {

                    if (hashCode != null) {

                        String imageHashCode = (String) hashCode;

                        String imageLink = getPodSpaceImageUrl(imageHashCode);

                        threadMetadataObject.addProperty("fileHash", imageHashCode);
                        contentObject.addProperty("metadata", threadMetadataObject.toString());
                        contentObject.addProperty("image", imageLink);
                        updateThreadTask.run();

                    }
                });

            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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

        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder()
                .title(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .metadata(request.getMetadata())
                .build();

        if (request.getUploadThreadImageRequest() != null) {
            return updateThreadInfo(request.getThreadId(), threadInfoVO, request.getUserGroupHash(), request.getUploadThreadImageRequest(), handler);
        }

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

    public String getThreadParticipants(RequestThreadParticipant request, ChatHandler handler) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();
        boolean useCache = request.useCacheData();


        return getThreadParticipantsMain((int) count, (int) offset, threadId, typeCode, useCache, handler);
    }

    private String getThreadParticipantsMain(int count,
                                             int offset,
                                             long threadId,
                                             String typeCode,
                                             boolean useCache,
                                             ChatHandler handler) {

        String uniqueId = generateUniqueId();

        final int mCount = count != 0 ? count : 50;

        PodThreadManager podThreadManager = new PodThreadManager();

        podThreadManager.addTask(() -> {

            if (cache && useCache) {
                loadParticipantsFromCache(uniqueId, mCount, offset, threadId);
            }

        });

        podThreadManager.addTask(() -> {

            if (chatReady) {

                JsonObject content = new JsonObject();
                content.addProperty("count", mCount);
                content.addProperty("offset", offset);
                AsyncMessage chatMessage = new AsyncMessage();
                chatMessage.setContent(content.toString());
                chatMessage.setType(Constants.THREAD_PARTICIPANTS);
                chatMessage.setTokenIssuer("1");
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(threadId);
                chatMessage.setTypeCode(Util.isNullOrEmpty(typeCode) ? getTypeCode() : typeCode);
                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                String asyncContent = jsonObject.toString();
                setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, (long) offset, uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_PARTICIPANT");

                if (handler != null) {
                    handler.onGetThreadParticipant(uniqueId);
                }
            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        });

        podThreadManager.runTasksSynced();


        return uniqueId;

    }

    @SuppressWarnings("unchecked")
    private void loadParticipantsFromCache(String uniqueId, int count, int offset, long threadId) {

        try {
            messageDatabaseHelper.getThreadParticipant(offset, count, threadId, (obj, listData) -> {

                if (listData != null) {

                    List<Participant> participantsList = (List<Participant>) listData;
                    long participantCount = (long) obj;

                    ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
                    ResultParticipant resultParticipant = new ResultParticipant();

                    resultParticipant.setContentCount(participantsList.size());
                    if (participantsList.size() + offset < participantCount) {
                        resultParticipant.setHasNext(true);
                    } else {
                        resultParticipant.setHasNext(false);
                    }
                    resultParticipant.setParticipants(participantsList);
                    chatResponse.setResult(resultParticipant);
                    chatResponse.setCache(true);
                    chatResponse.setUniqueId(uniqueId);

                    resultParticipant.setNextOffset(offset + participantsList.size());
                    String jsonParticipant = gson.toJson(chatResponse);
                    listenerManager.callOnGetThreadParticipant(jsonParticipant, chatResponse);
                    showLog("PARTICIPANT FROM CACHE", jsonParticipant);

                }


            });
        } catch (RoomIntegrityException e) {
            resetCache();
        }
    }


    private String getThreadAdminsMain(int count,
                                       int offset,
                                       long threadId,
                                       String typeCode,
                                       boolean useCache,
                                       ChatHandler handler) {


        String uniqueId = generateUniqueId();

        final int mCount = count != 0 ? count : 50;

        PodThreadManager podThreadManager = new PodThreadManager();

        podThreadManager.addTask(() -> {

            if (cache && useCache) {
                loadAdminsFromCache(uniqueId, mCount, offset, threadId);
            }

        });


        podThreadManager.addTask(() -> {

            if (chatReady) {

                JsonObject content = new JsonObject();

                content.addProperty("count", mCount);
                content.addProperty("offset", offset);
                content.addProperty("admin", true);


                AsyncMessage chatMessage = new AsyncMessage();
                chatMessage.setContent(content.toString());
                chatMessage.setType(Constants.THREAD_PARTICIPANTS);
                chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(threadId);
                chatMessage.setTypeCode(typeCode != null ? typeCode : getTypeCode());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                String asyncContent = jsonObject.toString();

                setCallBacks(null, null, null, true, Constants.THREAD_PARTICIPANTS, (long) offset, uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_ADMINS");
                if (handler != null) {
                    handler.onGetThreadParticipant(uniqueId);
                }
            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        });


        podThreadManager.runTasksSynced();


        return uniqueId;

    }

    private void loadAdminsFromCache(String uniqueId, int count, int offset, long threadId) {

        try {
            messageDatabaseHelper.getThreadAdmins(offset, count, threadId, (obj, listData) -> {

                List<Participant> participants = (List<Participant>) listData;
                long participantCount = (long) obj;

                if (participants != null) {
                    ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();

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
                    chatResponse.setUniqueId(uniqueId);
                    chatResponse.setSubjectId(threadId);

                    resultParticipant.setNextOffset(offset + participants.size());
                    String jsonParticipant = gson.toJson(chatResponse);


                    OutPutParticipant outPutParticipant = new OutPutParticipant();

                    outPutParticipant.setResult(resultParticipant);


                    listenerManager.callOnGetThreadAdmin(jsonParticipant, chatResponse);

                    showLog("RECEIVE ADMINS FROM CACHE", jsonParticipant);

                }

            });
        } catch (RoomIntegrityException e) {
            resetCache();
        }
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
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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


        Runnable cacheLoading = () -> {
            if (permit && cache && handler != null) {

                try {
                    UserInfo userInfo = messageDatabaseHelper.getUserInfo();

                    if (userInfo != null) {

                        ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();

                        ResultUserInfo result = new ResultUserInfo();

                        setUserId(userInfo.getId());

                        result.setUser(userInfo);

                        chatResponse.setResult(result);

                        chatResponse.setCache(true);
                        chatResponse.setUniqueId(uniqueId);

                        String userInfoJson = gson.toJson(chatResponse);

                        listenerManager.callOnUserInfo(userInfoJson, chatResponse);
                        showLog("CACHE_USER_INFO", userInfoJson);
                    }
                } catch (Exception e) {
                    showErrorLog(e.getMessage());
                    onUnknownException(uniqueId, e);
                }


            }
        };


        Runnable requestServer = () -> {
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
        };


        new PodThreadManager()
                .addNewTask(cacheLoading)
                .addNewTask(requestServer)
                .runTasksSynced();

        return uniqueId;
    }

    /**
     * It Gets the information of the current user
     * <p>
     * set useCache = false to disable cache for only this function
     */
    public String getUserInfo(boolean useCache, ChatHandler handler) {

        String uniqueId = generateUniqueId();


        Runnable cacheLoading = () -> {
            if (cache && useCache) {


                try {
                    UserInfo userInfo = messageDatabaseHelper.getUserInfo();

                    if (userInfo != null) {

                        ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();

                        ResultUserInfo result = new ResultUserInfo();

                        setUserId(userInfo.getId());

                        result.setUser(userInfo);

                        chatResponse.setResult(result);

                        chatResponse.setCache(true);
                        chatResponse.setUniqueId(uniqueId);

                        String userInfoJson = gson.toJson(chatResponse);

                        listenerManager.callOnUserInfo(userInfoJson, chatResponse);
                        showLog("CACHE_USER_INFO", userInfoJson);
                    }
                } catch (Exception e) {
                    showErrorLog(e.getMessage());
                    onUnknownException(uniqueId, e);
                }


            }
        };
        Runnable requestServer = () -> {
            if (chatReady) {

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
            } else onChatNotReady(uniqueId);
        };

        new PodThreadManager()
                .addNewTask(cacheLoading)
                .addNewTask(requestServer)
                .runTasksSynced();


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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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

        if (Util.isNotNullOrEmpty(typeCode))
            CoreConfig.typeCode = typeCode;
    }


    /**
     * @param expireSecond participants and contacts have an expire date in cache and after expireSecond
     *                     they are going to delete from the cache/
     */
    public void setExpireAmount(int expireSecond) {
        this.expireAmount = expireSecond;
        if (dataSource != null)
            dataSource.changeExpireAmount(expireSecond);
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

        long count = requestGetAdmin.getCount();
        long offset = requestGetAdmin.getOffset();
        long threadId = requestGetAdmin.getThreadId();
        String typeCode = requestGetAdmin.getTypeCode();
        boolean useCache = requestGetAdmin.useCacheData();
        return getThreadAdminsMain((int) count, (int) offset, threadId, typeCode, useCache, null);
    }


    public String getAdminList(RequestGetAdmin requestGetAdmin, ChatHandler handler) {

        long count = requestGetAdmin.getCount();
        long offset = requestGetAdmin.getOffset();
        long threadId = requestGetAdmin.getThreadId();
        String typeCode = requestGetAdmin.getTypeCode();
        boolean useCache = requestGetAdmin.useCacheData();
        return getThreadAdminsMain((int) count, (int) offset, threadId, typeCode, useCache, handler);
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
                },
                PING_INTERVAL);

    }

    /**
     * Ping for staying chat alive
     */
    private void ping() {
        if (chatReady && async.getPeerId() != null) {
            AsyncMessage chatMessage = new AsyncMessage();
            chatMessage.setType(Constants.PING);
            chatMessage.setTokenIssuer("1");
            chatMessage.setToken(getToken());

            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITH_ACK, "CHAT PING");
        }
    }

    private void pingAfterSetToken() {
        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setType(Constants.PING);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());

        checkToken = true;


        String asyncContent = gson.toJson(chatMessage);
        async.sendMessage(asyncContent, AsyncAckType.Constants.WITH_ACK);
        showLog("**SEND_CHAT PING FOR CHECK TOKEN AUTHENTICATION", asyncContent);
    }

    private void pingForCheckConnection() {

        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setType(Constants.PING);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(getToken());

        connectionPing = true;

        String asyncContent = gson.toJson(chatMessage);
        async.sendMessage(asyncContent, AsyncAckType.Constants.WITH_ACK);
        showLog("..:: SEND CHAT PING FOR CHECK CONNECTION ::..", asyncContent);

    }

    private void showLog(String i, String json) {
        if (log) {
            Log.i(TAG, i);
            Log.i(TAG, json);
            if (!Util.isNullOrEmpty(json)) {
                listenerManager.callOnLogEvent(json);
                listenerManager.callOnLogEvent(i, json);
            }
//            try {
//                FileUtils.appendLog("\n >>> " + new Date() + "\n" + i + "\n" + json + "\n <<< \n");
//            } catch (IOException e) {
//                Log.e(TAG, "Saving log failed: " + e.getMessage());
//            }
        }

        Breadcrumb c = new Breadcrumb();
        c.setCategory("LOG");
        c.setData(i, json);
        c.setLevel(SentryLevel.INFO);
        c.setMessage(i);
        Sentry.addBreadcrumb(c, "NORMAL_INFO");
    }

    private void showLog(String info) {
        if (log) {
            Log.d(TAG, info);
//            try {
//                FileUtils.appendLog("\n >>> " + new Date() + "\n" + info + "\n <<<\n");
//            } catch (IOException e) {
//                Log.e(TAG, "Saving log failed: " + e.getMessage());
//            }
        }
        Breadcrumb c = new Breadcrumb();
        c.setCategory("LOG");
        c.setLevel(SentryLevel.INFO);
        c.setMessage(info);
        Sentry.addBreadcrumb(c, "INFO");

    }

    private void showErrorLog(String message) {

        if (log) {
            Log.e(TAG, "Error");
            Log.e(TAG, message);

//            try {
//                FileUtils.appendLog("\n *** " + new Date() + " \n" + message + "\n *** \n");
//            } catch (IOException e) {
//                Log.e(TAG, "Saving log failed: " + e.getMessage());
//            }
        }

        Breadcrumb c = new Breadcrumb();
        c.setCategory("LOG");
        c.setLevel(SentryLevel.ERROR);
        c.setMessage(message);
        Sentry.addBreadcrumb(c, "ERROR_LOG");


    }

    @Deprecated
    public void shareLogs(Context context) {

        FileUtils.shareLogs(context);
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
            captureError(errorMessage, errorCode, chatMessage.getUniqueId());

            pingHandler.removeCallbacksAndMessages(null);

            /*we are Changing the state of the chat because of the Client is not Authenticate*/
            listenerManager.callOnChatState("ASYNC_READY");

            stopTyping();

            return;
        }


        String errorMessage = error.getMessage();
        long errorCode = error.getCode();

        boolean isNotif = PodNotificationManager.isNotificationError(
                chatMessage,
                error,
                context,
                getUserId());

        if (isNotif) return;

        captureError(errorMessage, errorCode, chatMessage.getUniqueId());
    }


    /**
     * called when thread last seen message updated
     * <p>
     * result is updated thread info with new thread unreadCount
     */

    private void handleLastSeenUpdated(ChatMessage chatMessage) {

        //thread last seen message updated
        //
        //result is unreadCount
        //

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

        if (thread != null && thread.getId() > 0) {

            if (cache) {

                messageDatabaseHelper.retrieveAndUpdateThreadOnLastSeenUpdated(thread, new ThreadManager.ILastMessageChanged() {
                    @Override
                    public void onThreadExistInCache(Thread thread) {
                        onThreadLastMessageUpdated(thread, chatMessage.getUniqueId());
                    }

                    @Override
                    public void threadNotFoundInCache() {
                        retrieveThreadInfoFromServer(thread.getId(), false);
                    }
                });
            } else {
                retrieveThreadInfoFromServer(thread.getId(), false);
            }
        }


//        try {
//            ResultThread resultThread = new ResultThread();
//            Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
//            resultThread.setThread(thread);
//
//            ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
//            chatResponse.setResult(resultThread);
//            chatResponse.setUniqueId(chatMessage.getUniqueId());
//
//
//            showLog("THREAD_LAST_SEEN_MESSAGE_UPDATED", "");
//            showLog(chatMessage.getContent(), "");
//            listenerManager.callOnThreadInfoUpdated(chatMessage.getContent(), chatResponse);
//
//            if (cache) {
//                messageDatabaseHelper.saveNewThread(resultThread.getThread());
//            }
//
//        } catch (JsonSyntaxException e) {
//            showErrorLog(e.getMessage());
//            onUnknownException(chatMessage.getUniqueId());
//        }

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
            dataSource.saveThreadResultFromServer(resultThread.getThread());
        }

    }


    private void handleThreadInfoUpdated(Thread thread, String uniqueId) {

        ResultThread resultThread = new ResultThread();
        resultThread.setThread(thread);

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(uniqueId);

        listenerManager.callOnThreadInfoUpdated(chatResponse.getJson(), chatResponse);

        showLog("THREAD_INFO_UPDATED", chatResponse.getJson());

        if (cache) {
//            messageDatabaseHelper.saveNewThread(resultThread.getThread());
            dataSource.saveThreadResultFromServer(thread);
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


//            if (cache && !permit) {
//
//                //todo handle condition
////                showLog("GENERATE_KEY", "");
////                generateEncryptionKey(getSsoHost());
//            } else {
            chatReady = true;
            chatState = CHAT_READY;
            checkToken = false;

            retrySetToken = 1;
            tokenHandler.removeCallbacksAndMessages(null);

            listenerManager.callOnChatState(CHAT_READY);
            showLog("** CLIENT_AUTHENTICATED_NOW", "");
            pingWithDelay();
//            }


        }


        if (connectionPing) {

            connectionPing = false;
            showLog(" ..:: CONNECTION PONG RECEIVED ::..", "");
            if (pinger != null) {
                pinger.onPong(chatMessage);
            }

        }
    }

    /**
     * When the new message arrived we send the deliver message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {

        try {
            MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

            if (cache) {
                dataSource.saveMessageResultFromServer(messageVO, chatMessage.getSubjectId());
//                CacheMessageVO cacheMessageVO = gson.fromJson(chatMessage.getContent(), CacheMessageVO.class);
//                messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId(), false);
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
            chatResponse.setSubjectId(chatMessage.getSubjectId());
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


            if (messageVO != null) {
                handleOnNewMessageAdded(messageVO.getConversation(), chatMessage.getUniqueId());
            }


        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
        }

    }

    private void handleOnNewMessageAdded(Thread thread, String uniqueId) {

        if (thread != null && thread.getId() > 0) {

            if (cache) {

                messageDatabaseHelper.retrieveAndUpdateThreadOnNewMessageAdded(thread, new ThreadManager.ILastMessageChanged() {
                    @Override
                    public void onThreadExistInCache(Thread thread) {
                        onThreadLastMessageUpdated(thread, uniqueId);
                    }

                    @Override
                    public void threadNotFoundInCache() {
                        retrieveThreadInfoFromServer(thread.getId(), false);
                    }
                });
            } else {
                retrieveThreadInfoFromServer(thread.getId(), false);
            }
        }


    }


    private void handleSent(ChatMessage chatMessage, String messageUniqueId, long threadId) {


        if (cache) {
//            messageDatabaseHelper.deleteWaitQueueMsgs(messageUniqueId);
            dataSource.deleteWaitQueueWithUniqueId(messageUniqueId);
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
                                try {
                                    resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));
                                } catch (NumberFormatException e) {
                                    Sentry.captureException(e);
                                    resultMessage.setMessageId(0);

                                }
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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
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
                dataSource.getAllSendingQueue()
                        .subscribe(sendingQueueCaches -> {
                            if (!Util.isNullOrEmpty(sendingQueueCaches)) {
                                for (SendingQueueCache sendingQueue : sendingQueueCaches) {
                                    String uniqueId = sendingQueue.getUniqueId();
                                    long threadId = sendingQueue.getThreadId();
                                    setThreadCallbacks(threadId, uniqueId);
                                    dataSource.moveFromSendingToWaitingQueue(uniqueId);
                                    JsonObject jsonObject = (new JsonParser()).parse(sendingQueue.getAsyncContent()).getAsJsonObject();

                                    jsonObject.remove("token");
                                    jsonObject.addProperty("token", getToken());

                                    if (log) Log.i(TAG, "checkMessageQueue");
                                    String async = jsonObject.toString();
                                    sendAsyncMessage(async, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
                                }
                            }

                        });

//                new PodThreadManager().doThisAndGo(() -> {
//
////                    List<SendingQueueCache> sendingQueueCaches = messageDatabaseHelper.getAllSendingQueue();
//                    List<SendingQueueCache> sendingQueueCaches = dataSource.getAllSendingQueue();
//
//                    if (!Util.isNullOrEmpty(sendingQueueCaches)) {
//                        for (SendingQueueCache sendingQueue : sendingQueueCaches) {
//                            String uniqueId = sendingQueue.getUniqueId();
//                            long threadId = sendingQueue.getThreadId();
//                            setThreadCallbacks(threadId, uniqueId);
//
////                            messageDatabaseHelper.insertWaitMessageQueue(sendingQueue);
////                            messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
//
//                            dataSource.moveFromSendingToWaitingQueue(uniqueId);
//
//                            JsonObject jsonObject = (new JsonParser()).parse(sendingQueue.getAsyncContent()).getAsJsonObject();
//
//                            jsonObject.remove("token");
//                            jsonObject.addProperty("token", getToken());
//
//                            if (log) Log.i(TAG, "checkMessageQueue");
//                            String async = jsonObject.toString();
//                            sendAsyncMessage(async, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TEXT_MESSAGE_FROM_MESSAGE_QUEUE");
//                        }
//                    }
//                });

            } else {

                if (!sendingQList.isEmpty()) {

                    for (SendingQueueCache sendingQueue : sendingQList.values()) {
                        String uniqueId = sendingQueue.getUniqueId();
                        long threadId = sendingQueue.getThreadId();
                        setThreadCallbacks(threadId, uniqueId);

                        WaitQueueCache waitMessageQueue = getWaitQueueCacheFromSendQueue(sendingQueue, uniqueId);

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
            showErrorLog(throwable.getMessage());
            onUnknownException("", throwable);
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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
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

//                        case Constants.GET_HISTORY:
//
//                            handleOutPutGetHistory(callback, chatMessage);
//                            break;
                        case Constants.LOCATION_PING:
                            handleOnStatusPingSent(chatMessage);
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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
        }
    }


    private void handleOnLastMessageDeleted(ChatMessage chatMessage) {


        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);


        if (thread != null && thread.getId() > 0) {

            if (cache) {

                messageDatabaseHelper.retrieveAndUpdateThreadOnLastMessageDeleted(thread, new ThreadManager.ILastMessageChanged() {
                    @Override
                    public void onThreadExistInCache(Thread thread) {

                        onThreadLastMessageUpdated(thread, chatMessage.getUniqueId());

                    }

                    @Override
                    public void threadNotFoundInCache() {
                        retrieveThreadInfoFromServer(thread.getId(), false);
                    }
                });
            } else {
                retrieveThreadInfoFromServer(thread.getId(), false);
            }
        }


    }

    private void handleOnLastMessageEdited(ChatMessage chatMessage) {


        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);
        if (thread != null && thread.getId() > 0) {

            if (cache) {
                messageDatabaseHelper.retrieveAndUpdateThreadOnLastMessageEdited(thread, new ThreadManager.ILastMessageChanged() {
                    @Override
                    public void onThreadExistInCache(Thread thread) {
                        onThreadLastMessageUpdated(thread, chatMessage.getUniqueId());
                    }

                    @Override
                    public void threadNotFoundInCache() {
                        retrieveThreadInfoFromServer(thread.getId(), false);
                    }
                });
            } else {
                retrieveThreadInfoFromServer(thread.getId(), false);
            }
        }
    }

    private void retrieveThreadInfoFromServer(long threadId, boolean isThreadInfoUpdate) {

        if (chatReady) {

            String uniqueId = generateUniqueId();

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(1);

            chatMessageContent.setOffset(0);

            ArrayList<Integer> threadIds = new ArrayList<>();

            threadIds.add((int) threadId);
            chatMessageContent.setThreadIds(threadIds);
            JsonObject content = (JsonObject) gson.toJsonTree(chatMessageContent);

            AsyncMessage asyncMessage = new AsyncMessage();
            asyncMessage.setContent(content.toString());
            asyncMessage.setType(Constants.GET_THREADS);
            asyncMessage.setTokenIssuer("1");
            asyncMessage.setToken(getToken());
            asyncMessage.setUniqueId(uniqueId);
            asyncMessage.setTypeCode(typeCode != null ? typeCode : getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(asyncMessage);

            threadInfoCompletor.put(uniqueId, chatMessage -> {

                ArrayList<Thread> threads = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Thread>>() {
                }.getType());

                if (!Util.isNullOrEmpty(threads)) {

                    if (isThreadInfoUpdate)
                        onThreadInfoUpdated(threads.get(0), chatMessage.getUniqueId());
                    else
                        handleThreadInfoUpdated(threads.get(0), chatMessage.getUniqueId());

                    threadInfoCompletor.remove(uniqueId);

                }

            });
            sendAsyncMessage(jsonObject.toString(), AsyncAckType.Constants.WITHOUT_ACK, "SEND_GET_THREAD_INFO");
        } else {
            showErrorLog("Couldn't complete thread info because chat is not ready");
        }

    }

    private void onThreadLastMessageUpdated(Thread thread, String uniqueId) {
        ResultThread resultThread = new ResultThread();
        resultThread.setThread(thread);
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(uniqueId);
        chatResponse.setSubjectId(thread.getId());
        listenerManager.callOnThreadInfoUpdated(chatResponse.getJson(), chatResponse);
        showLog("THREAD_INFO_UPDATED", chatResponse.getJson());
        if (cache) {
            dataSource.saveThreadResultFromCache(thread);
        }
    }

    //todo need optimization. insert to db Called twice!

    private void onThreadInfoUpdated(Thread thread, String uniqueId) {
        ResultThread resultThread = new ResultThread();
        resultThread.setThread(thread);
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(uniqueId);
        chatResponse.setSubjectId(thread.getId());
        listenerManager.callOnUpdateThreadInfo(chatResponse.getJson(), chatResponse);
        showLog("RECEIVE_UPDATE_THREAD_INFO", chatResponse.getJson());
        messageCallbacks.remove(uniqueId);
        if (cache) {
            dataSource.saveThreadResultFromServer(thread);
        }
    }

    private void handleOnGetParticipants(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        if (callback.isResult()) {

            ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

            String jsonParticipant = gson.toJson(chatResponse);

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
            dataSource.updateMessageResultFromServer(messageVO, chatMessage.getSubjectId());
//            CacheMessageVO cacheMessageVO = gson.fromJson(chatMessage.getContent(), CacheMessageVO.class);
//            messageDatabaseHelper.saveMessage(cacheMessageVO, chatMessage.getSubjectId(), true);
        }

        newMessage.setMessageVO(messageVO);
        newMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(newMessage);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setSubjectId(chatMessage.getSubjectId());

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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
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

                        setChatReady("CHAT_READY", true);


                    } else {


                        if (log) if (response.errorBody() != null) {
                            Log.e(TAG, response.errorBody().toString());
                        }

                        initDatabase();

                        setChatReady("CHAT_READY_WITHOUT_ENCRYPTION_US", false);


                    }


                } else {


                    if (log) if (response.errorBody() != null) {
                        Log.e(TAG, response.errorBody().toString());
                    }
                    initDatabase();
                    setChatReady("CHAT_READY_WITHOUT_ENCRYPTION_NS", false);

                }


            }

            @Override
            public void onFailure(Call<EncResponse> call, Throwable t) {

                Log.e(TAG, "Failure On: " + t.getMessage());

            }
        });

    }

    private void setChatReady(String state, boolean encrypt) {

        chatReady = true;
        listenerManager.callOnChatState("CHAT_READY");
        chatState = CHAT_READY;
        checkMessageQueue();
        getAllThreads();
        showLog(state, "");
        permit = true;
        checkFreeSpace();
        PodNotificationManager.onChatIsReady(context, userId);


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

    /**
     * It is sent uniqueIds of messages from waitQueue to ensure all of them have been sent.
     */

    @SuppressWarnings("unchecked")
    private void updateWaitingQ(long threadId, String uniqueId, ChatHandler handler) {

        /*  if waitQueue had these messages then send request's getHistory and in onSent remove them from wait queue
         */

        getUniqueIdsInWaitQ()
                .doOnError(exception -> {
                    if (exception instanceof RoomIntegrityException) {
                        resetCache();
                    } else {
                        showErrorLog(exception.getMessage());
                    }

                    handler.onGetHistory(uniqueId);
                })
                .onErrorResumeNext(Observable.empty())
                .subscribe(waitingQMessagesUniqueIds -> {

                    if (Util.isNullOrEmpty(waitingQMessagesUniqueIds)) {
                        handler.onGetHistory(uniqueId);
                        return;
                    }
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

                });

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
        long firstMessageId = history.getFirstMessageId();
        long lastMessageId = history.getLastMessageId();
        long id = history.getId();
        String query = history.getQuery();
        String asyncContent = MessageManager.prepareMainHistoryResponse(history, threadId, uniqueId, getTypeCode(), getToken());

        String order;

        if (Util.isNullOrEmpty(history.getOrder())) {
            order = "asc";
        } else {
            order = history.getOrder();
        }

        setCallBacks(firstMessageId, lastMessageId, order, history.getCount(), history.getOffset(), uniqueId, id, true, query);

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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
        }
    }

    private void onUnknownException(String uniqueId, Throwable throwable) {
        captureError(ChatConstant.ERROR_UNKNOWN_EXCEPTION,
                ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId, throwable);
    }

    private void handleGetContact(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage, callback);
        String contactJson = gson.toJson(chatResponse);

        if (handlerSend.containsKey(chatMessage.getUniqueId())
                && handlerSend.get(chatMessage.getUniqueId()) != null) {

            Objects.requireNonNull(handlerSend.get(chatMessage.getUniqueId()))
                    .onGetContact(contactJson, chatResponse);

            return;
        }

        listenerManager.callOnGetContacts(contactJson, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_GET_CONTACT", contactJson);

    }

    private void removeContactTest(ChatResponse<ResultContact> chatResponse) {

        for (Contact contact :
                chatResponse.getResult().getContacts()) {


            if (contact.getLinkedUser() != null) continue;


            new PodThreadManager()
                    .doThisSafe(() -> {
                        RequestRemoveContact request = new RequestRemoveContact.Builder(contact.getId())
                                .build();
                        removeContact(request);
                        try {
                            java.lang.Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

        }
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

        // when thread created we send file to thread
        if (handlerSend.containsKey(chatResponse.getUniqueId())
                && handlerSend.get(chatResponse.getUniqueId()) != null) {

            handlerSend.get(chatResponse.getUniqueId())
                    .onThreadCreated(chatResponse.getResult());

        }

    }

    private void handleGetThreads(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage, callback);
        //TODO to many data
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
            showErrorLog(e.getMessage());
            onUnknownException(chatMessage.getUniqueId(), e);
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
            captureError(e.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, chatMessage.getUniqueId(), e);
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

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

        if (thread != null && thread.getId() > 0) {

            if (cache) {

                messageDatabaseHelper.retrieveAndUpdateThreadOnThreadInfoUpdated(thread, new ThreadManager.ILastMessageChanged() {
                    @Override
                    public void onThreadExistInCache(Thread thread) {
                        onThreadInfoUpdated(thread, messageUniqueId);
                    }

                    @Override
                    public void threadNotFoundInCache() {
                        retrieveThreadInfoFromServer(thread.getId(), true);
                    }
                });
            } else {
                retrieveThreadInfoFromServer(thread.getId(), true);
            }
        }

    }

    /**
     * Its check the Failed Queue {@link #checkMessageQueue()} to send all the message that is waiting to be send.
     */
    private void handleOnGetUserInfo(ChatMessage chatMessage, String messageUniqueId, Callback callback) {

        if (callback.isResult()) {
//            if there is a key its ok if not it will go for the key and then chat ready
            setChatReady("CHAT_READY", true);
            userInfoResponse = true;
            ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
            UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);

            //add user info for sentry
            setSentryUser(userInfo);

            String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
            showLog("RECEIVE_USER_INFO", userInfoJson);

            listenerManager.callOnUserInfo(userInfoJson, chatResponse);
            messageCallbacks.remove(messageUniqueId);


//            if (permit) {
//                listenerManager.callOnChatState("CHAT_READY");
//                chatReady = true;
//                chatState = CHAT_READY;
//                checkMessageQueue();
//                showLog("CHAT_READY", "");
//            } else {
//                showLog("GENERATE_KEY", "");
//                generateEncryptionKey(getSsoHost());
//            }

            //ping start after the response of the get userInfo
            pingWithDelay();

        }
    }

    private void setSentryUser(UserInfo userInfo) {
        User sentryUser = new User();
        sentryUser.setId(String.valueOf(userInfo.getId()));
        sentryUser.setEmail(userInfo.getCellphoneNumber());
        sentryUser.setUsername(userInfo.getName());
        Map<String, String> sentryInfoMap = new HashMap<>();
        sentryInfoMap.put("token", getToken());
        sentryInfoMap.put("tokenIssuer", String.valueOf(TOKEN_ISSUER));
        sentryInfoMap.put("typeCode", getTypeCode());
        sentryInfoMap.put("socketAddress", socketAddress);
        sentryInfoMap.put("appId", appId);
        sentryInfoMap.put("serverName", serverName);
        sentryInfoMap.put("platformHost", getPlatformHost());
        sentryInfoMap.put("ssoHost", getSsoHost());
        sentryInfoMap.put("fileServer", getFileServer());
        sentryInfoMap.put("podSpaceServer", getPodSpaceServer());
        sentryUser.setOthers(sentryInfoMap);
        Sentry.setUser(sentryUser);
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
        chatMessage.setMetadata(metaData);
        chatMessage.setSystemMetadata(systemMetaData);
        chatMessage.setType(Constants.MESSAGE);
        chatMessage.setTypeCode(getTypeCode());
        chatMessage.setMessageType(messageType);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetaData)) {
            jsonObject.remove("systemMetaData");
        }

        if (Util.isNullOrEmpty(metaData)) {
            jsonObject.remove("metadata");
        }

        jsonObject.remove("time");

        String asyncContent = jsonObject.toString();

        sendingQueue.setAsyncContent(asyncContent);

        insertToSendQueue(uniqueId, sendingQueue);

        if (log)
            Log.i(TAG, "Message with this" + "uniqueId" + uniqueId + "has been added to Message Queue");

        if (chatReady) {

            moveFromSendingQueueToWaitQueue(uniqueId, sendingQueue);

            setThreadCallbacks(threadId, uniqueId);

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_REPLY_MESSAGE");
            stopTyping();
            if (handler != null) {
                handler.onReplyMessage(uniqueId);
            }

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    private WaitQueueCache getWaitQueueCacheFromSendQueue(SendingQueueCache sendingQueue, String uniqueId2) {
        WaitQueueCache waitMessageQueue = new WaitQueueCache();
        waitMessageQueue.setUniqueId(uniqueId2);
        waitMessageQueue.setId(sendingQueue.getId());
        waitMessageQueue.setAsyncContent(sendingQueue.getAsyncContent());
        waitMessageQueue.setMessage(sendingQueue.getMessage());
        waitMessageQueue.setThreadId(sendingQueue.getThreadId());
        waitMessageQueue.setMessageType(sendingQueue.getMessageType());
        waitMessageQueue.setSystemMetadata(sendingQueue.getSystemMetadata());
        waitMessageQueue.setMetadata(sendingQueue.getMetadata());
        return waitMessageQueue;
    }

    private String getKey() {


        return mSecurePrefs.getString("KEY", null);
    }

    private void setKey(String key) {
        mSecurePrefs.edit().putString("KEY", key).apply();
    }

    private static void setTheKey(String key) {
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
                        captureError(ChatConstant.ERROR_CANT_GET_USER_INFO, ChatConstant.ERROR_CODE_CANT_GET_USER_INFO, "");
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

        ChatResponse<ResultLeaveThread> chatResponse = ThreadManager.prepareLeaveThreadResponse(chatMessage);

        String jsonThread = gson.toJson(chatResponse);

        showLog("RECEIVE_LEAVE_THREAD", jsonThread);
        listenerManager.callOnThreadLeaveParticipant(jsonThread, chatResponse);

        if (callback != null) {
            messageCallbacks.remove(messageUniqueId);
        }

        if (cache) {
            messageDatabaseHelper.leaveThread(chatMessage.getSubjectId());
        }

    }

    private void handleAddParticipant(ChatMessage chatMessage, String messageUniqueId) {

        Thread thread = gson.fromJson(chatMessage.getContent(), Thread.class);

        if (cache) {
            ThreadVo threadVo = gson.fromJson(chatMessage.getContent(), ThreadVo.class);
            List<CacheParticipant> cacheParticipants = threadVo.getParticipants();

            if (!Util.isNullOrEmpty(cacheParticipants))
                messageDatabaseHelper.saveParticipants(cacheParticipants, thread.getId(), getExpireAmount());
        }

        ChatResponse<ResultAddParticipant> chatResponse = ParticipantsManager.prepareAddParticipantResponse(chatMessage, thread);

        String jsonAddParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadAddParticipant(jsonAddParticipant, chatResponse);

        messageCallbacks.remove(messageUniqueId);

        showLog("RECEIVE_ADD_PARTICIPANT", jsonAddParticipant);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {

        long messageId = App.getGson().fromJson(chatMessage.getContent(), MessageVO.class).getId();

        ChatResponse<ResultDeleteMessage> chatResponse = MessageManager.prepareDeleteMessageResponse(chatMessage, messageId);

        String jsonDeleteMsg = gson.toJson(chatResponse);

        listenerManager.callOnDeleteMessage(jsonDeleteMsg, chatResponse);
        showLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);

        if (cache) {
            messageDatabaseHelper.deleteMessage(messageId, chatMessage.getSubjectId());
            showLog("DeleteMessage from dataBase with this messageId" + " " + messageId, "");
        }

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
            dataSource.saveBlockedContactResultFromServer(contact);
//            messageDatabaseHelper.saveBlockedContact(contact, getExpireAmount());
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

        if (cache) {
            messageDatabaseHelper.deleteMessagesOfThread(chatMessage.getSubjectId());
        }

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

            dataSource.deleteBlockedContactById(contact.getBlockId());
//            messageDatabaseHelper.deleteBlockedContactById(contact.getBlockId());

        }

    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = ContactManager.prepareBlockListResponse(chatMessage);
        String jsonGetBlock = gson.toJson(chatResponse);
        listenerManager.callOnGetBlockList(jsonGetBlock, chatResponse);
        showLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);

        if (cache) {
            if (chatResponse.getResult().getContacts().size() > 0)
                dataSource.saveBlockedContactsResultFromServer(chatResponse.getResult().getContacts());
        }

    }

    private void handleOutPutRemoveParticipant(Callback callback, ChatMessage chatMessage, String messageUniqueId) {

        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(callback, chatMessage);

        String jsonRmParticipant = gson.toJson(chatResponse);

        listenerManager.callOnThreadRemoveParticipant(jsonRmParticipant, chatResponse);
        messageCallbacks.remove(messageUniqueId);
        showLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOnGetThreadHistory(Callback callback, ChatMessage chatMessage) {

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
//
//            Observable.just(resultHistory)
//                    .asObservable()
//                    .map(resultHistoryEMP -> {
//                        resultHistoryEMP.setSending(dataSource.getAllSendingQueueByThreadId(chatMessage.getSubjectId()));
//                        return resultHistoryEMP;
//                    })
//                    .map(resultHistoryWithSending -> {
//
//                        resultHistoryWithSending.setUploadingQueue(dataSource.getAllUploadingQueueByThreadId(chatMessage.getSubjectId()));
//                        return  resultHistoryWithSending;
//
//                    })
//                    .map(resultHistoryWithUploading -> {
//
//                        resultHistoryWithUploading.setFailed(dataSource.getAllWaitQueueCacheByThreadId(chatMessage.getSubjectId()));
//                        return resultHistoryWithUploading;
//
//                    });


//            resultHistory.setSending(messageDatabaseHelper.getAllSendingQueueByThreadId(chatMessage.getSubjectId()));
//            resultHistory.setUploadingQueue(messageDatabaseHelper.getAllUploadingQueueByThreadId(chatMessage.getSubjectId()));
//            resultHistory.setFailed(messageDatabaseHelper.getAllWaitQueueCacheByThreadId(chatMessage.getSubjectId()));
//
            resultHistory.setSending(dataSource.getAllSendingQueueByThreadId(chatMessage.getSubjectId()));
            resultHistory.setUploadingQueue(dataSource.getAllUploadingQueueByThreadId(chatMessage.getSubjectId()));
            resultHistory.setFailed(dataSource.getAllWaitQueueCacheByThreadId(chatMessage.getSubjectId()));
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


        List<CacheMessageVO> cMessageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheMessageVO>>() {
        }.getType());

        if (messageVOS.size() > 0) {

            new PodThreadManager()
                    .addNewTask(() -> dataSource.updateHistoryResponse(callback, messageVOS, chatMessage.getSubjectId(), cMessageVOS))
                    .addNewTask(() -> dataSource.saveMessageResultFromServer(messageVOS, chatMessage.getSubjectId()))
                    .runTasksSynced();

        }


    }

    private void notifyChatHistoryReceived(Callback callback, ChatMessage chatMessage, List<MessageVO> messageVOS) {

        ChatResponse<ResultHistory> chr = new ChatResponse<>();

        ResultHistory rh = new ResultHistory();

        rh.setHistory(messageVOS);

        chr.setResult(rh);

        chr.setSubjectId(chatMessage.getSubjectId());

        chr.setUniqueId(chatMessage.getUniqueId());

        Objects.requireNonNull(handlerSend.get(chatMessage.getUniqueId()))
                .onGetHistory(chr, chatMessage, callback);

    }

    private String getContactMain(int count, long offset, boolean syncContact, String typeCode, boolean useCache, ChatHandler handler) {
        String uniqueId = generateUniqueId();

        int mCount = count > 0 ? count : 50;

        Runnable serverRequestTask = () -> {
            if (chatReady) {

                ChatMessageContent chatMessageContent = new ChatMessageContent();

                chatMessageContent.setOffset(offset);

                JsonObject jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
                jObj.remove("lastMessageId");
                jObj.remove("firstMessageId");

                jObj.remove("count");
                jObj.addProperty("size", mCount);

                AsyncMessage chatMessage = new AsyncMessage();
                chatMessage.setContent(jObj.toString());
                chatMessage.setType(Constants.GET_CONTACTS);
                chatMessage.setToken(getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setTypeCode(Util.isNotNullOrEmpty(typeCode) ? typeCode : getTypeCode());

                chatMessage.setTokenIssuer("1");

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

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
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        };

        Runnable cacheTask = () -> {

            if (cache && useCache) {

                loadContactsFromCache(uniqueId, offset, mCount);

            }
        };


//        new PodThreadManager()
//                .addNewTask(cacheTask)
//                .addNewTask(serverRequestTask)
//                .runTasksSynced();


        dataSource.getContactData(count, offset)
                .doOnCompleted(serverRequestTask::run)
                .doOnError(exception -> {
                    if (exception instanceof RoomIntegrityException) {
                        resetCache();
                    } else {
                        captureError(exception.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                    }
                })
                .onErrorResumeNext(Observable.empty())
                .subscribe(response -> {
                    if (response != null && Util.isNotNullOrEmpty(response.getContactsList())) {
                        publishContactResult(uniqueId, offset, new ArrayList<>(response.getContactsList()), (int) response.getContentCount());
                    }
                });

        return uniqueId;

    }

    private void loadContactsFromCache(String uniqueId, long offset, int mCount) {

        ArrayList<Contact> cacheContactsList = null;
        try {
            cacheContactsList = new ArrayList<>(messageDatabaseHelper.getContacts(mCount, offset));
            int contentCount = messageDatabaseHelper.getContactCount();

            publishContactResult(uniqueId, offset, cacheContactsList, contentCount);

        } catch (RoomIntegrityException e) {
            resetCache();
        }

    }

    private void publishContactResult(String uniqueId, long offset, ArrayList<Contact> cacheContactsList, int contentCount) {
        ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
        ResultContact resultContact = new ResultContact();
        resultContact.setContacts(cacheContactsList);
        chatResponse.setResult(resultContact);
        chatResponse.setCache(true);
        chatResponse.setUniqueId(uniqueId);

        long nextOffset = cacheContactsList.size() + offset;
        boolean hasNext = nextOffset < contentCount;

        resultContact.setContentCount(contentCount);
        resultContact.setNextOffset(nextOffset);
        resultContact.setHasNext(hasNext);

        String contactJson = gson.toJson(chatResponse);

        listenerManager.callOnGetContacts(contactJson, chatResponse);
        showLog("CACHE_GET_CONTACT", contactJson);
    }

    @NonNull
    private String captureError(String errorMessage,
                                long errorCode,
                                String uniqueId) {

        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

        String jsonError = gson.toJson(error);

        listenerManager.callOnError(jsonError, error);

        showErrorLog(jsonError);

        //The chat is not ready and the client
        // is not authenticated, will not be captured.

        if (errorCode != ChatConstant.ERROR_CODE_CHAT_READY
                && errorCode != 21) {

            Sentry.captureException(new PodChatException(errorMessage, uniqueId, getToken()));

        }

        if (log) {
            Log.e(TAG, "ErrorMessage: " + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);
        }


        return jsonError;
    }


    @NonNull
    private String captureError(String errorMessage,
                                long errorCode,
                                String uniqueId,
                                Throwable throwable) {

        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

        String jsonError = gson.toJson(error);

        listenerManager.callOnError(jsonError, error);

        showErrorLog(jsonError);

        Sentry.captureException(throwable, error);

        if (log) {
            Log.e(TAG, "ErrorMessage: " + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);
        }
        return jsonError;
    }

    private String captureError(PodChatException exception) {

        ErrorOutPut error = new ErrorOutPut(true, exception.getMessage(), exception.getCode(), exception.getUniqueId());

        String jsonError = gson.toJson(error);

        listenerManager.callOnError(jsonError, error);

        showErrorLog(jsonError);

        Sentry.captureException(exception, error);

        return jsonError;
    }

    private String getTypeCode() {
        if (Util.isNullOrEmpty(typeCode)) {
            typeCode = "default";
        }
        return typeCode;
    }

    private void loadThreadsFromCache(Integer count,
                                      Long offset,
                                      ArrayList<Integer> threadIds,
                                      String threadName,
                                      boolean isNew,
                                      String uniqueId) {


        if (offset == null) {
            offset = 0L;
        }

        if (count == null || count == 0)
            count = 50;


        Long finalOffset = offset;

        try {

            messageDatabaseHelper.getThreadRaw(count, offset, threadIds, threadName, isNew, new OnWorkDone() {
                @Override
                public void onWorkDone(@Nullable Object o) {

                }

                @Override
                public void onWorkDone(@Nullable Object o, List cachedThreads) {

                    List<Thread> threads = (List<Thread>) cachedThreads;

                    long contentCount = (long) o;

                    if (!Util.isNullOrEmpty(threads)) {

                        String threadJson = publishThreadsList(uniqueId, finalOffset, new ThreadManager.ThreadResponse(threads, contentCount, "DISK"));
                        showLog("CACHE_GET_THREAD", threadJson);
                        dataSource.saveThreadResultFromCache(threads);

                    }
                }
            });

        } catch (RoomIntegrityException e) {
            resetCache();
        }


    }

    private String publishThreadsList(String uniqueId, Long finalOffset, ThreadManager.ThreadResponse cacheThreadResponse) {

        ChatResponse<ResultThreads> chatResponse = new ChatResponse<>();

        List<Thread> threadList = cacheThreadResponse.getThreadList();

        chatResponse.setCache(true);

        long contentCount = cacheThreadResponse.getContentCount();

        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(threadList);
        resultThreads.setContentCount(contentCount);
        chatResponse.setCache(true);


        if (threadList.size() + finalOffset < contentCount) {
            resultThreads.setHasNext(true);
        } else {
            resultThreads.setHasNext(false);
        }
        resultThreads.setNextOffset(finalOffset + threadList.size());
        chatResponse.setResult(resultThreads);
        chatResponse.setUniqueId(uniqueId);

        String threadJson = gson.toJson(chatResponse);
        listenerManager.callOnGetThread(threadJson, chatResponse);
        return threadJson;
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

                ChatMessage chatMessage = new ChatMessage(uniqueId, Constants.DELIVERED_MESSAGE_LIST, object.toString(), getToken());

                String asyncContent = chatMessage.getJson(requestParams.getTypeCode(), getTypeCode()).toString();

                setCallBacks(null, null, null, true, Constants.DELIVERED_MESSAGE_LIST, requestParams.getOffset(), uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            onUnknownException(uniqueId, e);
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

                ChatMessage chatMessage = new ChatMessage(uniqueId, Constants.SEEN_MESSAGE_LIST, object.toString(), getToken());

                String asyncContent = chatMessage.getJson(requestParams.getTypeCode(), getTypeCode()).toString();

                setCallBacks(null, null, null, true, Constants.SEEN_MESSAGE_LIST, requestParams.getOffset(), uniqueId);
                sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_SEEN_MESSAGE_LIST");
            } catch (Throwable e) {
                showErrorLog(e.getMessage());
                onUnknownException(uniqueId, e);
            }
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
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
                showErrorLog(e.getMessage());
                onUnknownException(chatMessage.getUniqueId(), e);
            }

        }

        if (cache) {
            List<CacheParticipant> cacheParticipants = new ArrayList<>();

            if (!Util.isNullOrEmpty(chatMessage.getContent())) {

                try {
                    cacheParticipants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<CacheParticipant>>() {
                    }.getType());
                } catch (JsonSyntaxException e) {
                    showErrorLog(e.getMessage());
                    onUnknownException(chatMessage.getUniqueId(), e);
                }
            }

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
        if (chatMessage.getRepliedTo() == 0)
            jsonObject.remove("repliedTo");

        if (chatMessage.getTime() == 0)
            jsonObject.remove("time");

        jsonObject.remove("contentCount");


        String asyncContent = jsonObject.toString();

        sendingQueue.setAsyncContent(asyncContent);

        insertToSendQueue(uniqueId, sendingQueue);

        if (chatReady) {

            setThreadCallbacks(threadId, uniqueId);

            moveFromSendingQueueToWaitQueue(uniqueId, sendingQueue);

            sendAsyncMessage(asyncContent, AsyncAckType.Constants.WITHOUT_ACK, "SEND_TXT_MSG_WITH_FILE");
            stopTyping();
        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
    }

    private void moveFromSendingQueueToWaitQueue(String uniqueId, SendingQueueCache sendingQueue) {
        if (cache) {

//            messageDatabaseHelper.deleteSendingMessageQueue(uniqueId);
//            messageDatabaseHelper.insertWaitMessageQueue(sendingQueue);
            dataSource.moveFromSendingToWaitingQueue(uniqueId);

        } else {
            sendingQList.remove(uniqueId);
            WaitQueueCache waitMessageQueue = getWaitQueueCacheFromSendQueue(sendingQueue, sendingQueue.getUniqueId());
            waitQList.put(uniqueId, waitMessageQueue);
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
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
        }
    }

    private void sendAsyncMessage(String asyncContent, int asyncMsgType, String logMessage) {
        if (chatReady) {
            showLog(logMessage, asyncContent);
            try {
                async.sendMessage(asyncContent, asyncMsgType);
            } catch (Exception e) {
                showErrorLog(e.getMessage());
                onUnknownException("", e);
                return;
            }

            pingWithDelay();

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, null);
        }
    }

    /**
     * Get the list of the Device Contact
     */

    private void getPhoneContact(Context context, String uniqueId, OnContactLoaded listener) {


        try {

            showLog(">>> Getting phone contacts ");

            List<PhoneContact> cachePhoneContacts = new ArrayList<>();

            PhoneContactAsyncTask task = new PhoneContactAsyncTask(phoneContactDbHelper, contacts -> {

                String firstName;
                String phoneNumber;
                String lastName;
                String empty = "";
                int version;
                ArrayList<PhoneContact> newPhoneContact = new ArrayList<>();
                HashMap<String, PhoneContact> newContactsMap = new HashMap<>();

                showLog("#" + contacts.size() + " Contacts Loaded From Cache");

                cachePhoneContacts.addAll(contacts);

                HashMap<String, PhoneContact> mapCacheContactKeeper = new HashMap<>();

                if (cachePhoneContacts.size() > 0) {
                    for (PhoneContact contact : cachePhoneContacts) {
                        mapCacheContactKeeper.put(contact.getPhoneNumber(), contact);
                    }
                }

                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                if (cursor == null) {
                    showLog("Contacts loader cursor is null");
                    listener.onLoad(newPhoneContact);
                    return;
                }


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
                                    newContactsMap.put(phoneNumber, phoneContact);
                                }
                            } else {
                                newContactsMap.put(phoneNumber, phoneContact);
                            }
                        } else {
                            newContactsMap.put(phoneNumber, phoneContact);
                        }
                    }
                }
                cursor.close();

                //retrieve unique contacts
                for (String key :
                        newContactsMap.keySet()) {
                    if (!mapCacheContactKeeper.containsKey(key))
                        newPhoneContact.add(newContactsMap.get(key));
                }

                showLog("#" + newPhoneContact.size() + " New Contact Found");
                listener.onLoad(newPhoneContact);


            });

            task.execute();

        } catch (Exception e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
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

        FileMetaDataContent metaDataContent = new FileMetaDataContent(fileId, file.getName(), mimeType, fileSize);

        if (hashCode != null) {
            metaDataContent.setHashCode(hashCode);
            metaDataContent.setLink(getPodSpaceFileUrl(hashCode));
        } else {
            metaDataContent.setLink(filePath);
        }

        return metaDataContent.getMetaData();
    }


    private String createImageMetadata(File fileUri, String hashCode, long imageId, int actualHeight, int actualWidth, String mimeType
            , long fileSize, String path, boolean isLocation, String center) {

        String originalName = fileUri.getName();
        FileImageMetaData fileMetaData =
                new FileImageMetaData(imageId, originalName, hashCode, originalName, actualHeight, actualWidth, fileSize, mimeType);

        if (!Util.isNullOrEmpty(hashCode)) {
            fileMetaData.setLink(getPodSpaceImageUrl(hashCode));
        } else {
            fileMetaData.setLink(path);
        }

        String metadata = fileMetaData.getMetaData(isLocation, center);
        return metadata;
    }


    /**
     * Add list of contacts with their mobile numbers and their cellphoneNumbers
     */
    private void addContacts(List<PhoneContact> phoneContacts, String uniqueId) {

        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> cellphoneNumbers = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> typeCodes = new ArrayList<>();
        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();

        for (PhoneContact contact : phoneContacts) {
            firstNames.add(contact.getName());
            lastNames.add(contact.getLastName());
            uniqueIds.add(generateUniqueId());
            String phoneNum = String.valueOf(contact.getPhoneNumber());

            if (phoneNum.startsWith("9")) {
                phoneNum = phoneNum.replaceFirst("9", "09");
            }
            cellphoneNumbers.add(phoneNum);

            emails.add("");
            typeCodes.add(getTypeCode());

        }

        Observable<Response<Contacts>> addContactsObservable;

        if (getPlatformHost() != null) {

            if (!Util.isNullOrEmpty(getTypeCode())) {

                addContactsObservable = contactApi.addContacts(getToken(),
                        TOKEN_ISSUER,
                        firstNames,
                        lastNames,
                        emails,
                        uniqueIds,
                        cellphoneNumbers,
                        typeCodes);

            } else {
                addContactsObservable = contactApi.addContacts(getToken(),
                        TOKEN_ISSUER,
                        firstNames,
                        lastNames,
                        emails,
                        uniqueIds,
                        cellphoneNumbers);
            }

            showLog("Call add contacts " + new Date());

            addContactsObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(contactsResponse -> {

                        showLog(">>> Server Respond " + new Date());

                        boolean error = false;

                        if (contactsResponse.body() != null) {
                            error = contactsResponse.body().getHasError();
                            showLog(">>>Response: " + contactsResponse.code());
                            showLog(">>>ReferenceNumber: " + contactsResponse.body().getReferenceNumber());
                            showLog(">>>Ott: " + contactsResponse.body().getOtt());
                        }

                        if (contactsResponse.isSuccessful()) {

                            if (error) {

                                captureError(contactsResponse.body().getMessage(), contactsResponse.body().getErrorCode()
                                        , uniqueId);

                                showLog("Error add Contacts: " + contactsResponse.body().getMessage());

                            } else {
                                //successful response
                                Contacts contacts = contactsResponse.body();
                                ChatResponse<Contacts> chatResponse = new ChatResponse<>();

                                chatResponse.setResult(contacts);
                                chatResponse.setUniqueId(uniqueId);

                                String contactsJson = gson.toJson(chatResponse);

                                listenerManager.callOnSyncContact(contactsJson, chatResponse);
                                showLog("SYNC_CONTACT_COMPLETED", contactsJson);

                                Runnable updatePhoneContactsDBTask = () -> {
                                    try {
                                        boolean result = phoneContactDbHelper.addPhoneContacts(phoneContacts);
                                        if (!result) {
                                            resetCache(() -> phoneContactDbHelper.addPhoneContacts(phoneContacts));
                                        }
                                    } catch (Exception e) {
                                        showErrorLog("Updating Contacts cache failed: " + e.getMessage());
                                        onUnknownException(uniqueId, e);
                                    }
                                };

                                Runnable updateCachedContactsTask = () -> {
                                    if (cache) {
                                        try {
//                                            messageDatabaseHelper.saveContacts(chatResponse.getResult().getResult(), getExpireAmount());
                                            dataSource.saveContactsResultFromServer(chatResponse.getResult().getResult());
                                        } catch (Exception e) {
                                            showErrorLog("Saving Contacts Failed: " + e.getMessage());
                                            onUnknownException(uniqueId, e);
                                        }
                                    }
                                };

                                new PodThreadManager()
                                        .addNewTask(updatePhoneContactsDBTask)
                                        .addNewTask(updateCachedContactsTask)
                                        .runTasksSynced();
                            }
                        } else {

                            captureError(contactsResponse.message(), contactsResponse.code()
                                    , uniqueId);

                            showLog("Error add Contacts: " + contactsResponse.raw());

                        }
                    }, throwable ->
                            captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId));
        }


    }

    private PublishSubject<List<PhoneContact>> addGroupContacts(List<PhoneContact> phoneContacts, String uniqueId) {


        PublishSubject<List<PhoneContact>> subject = PublishSubject.create();

        ArrayList<String> firstNames = new ArrayList<>();
        ArrayList<String> cellphoneNumbers = new ArrayList<>();
        ArrayList<String> lastNames = new ArrayList<>();
        ArrayList<String> typeCodes = new ArrayList<>();
        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();


        for (PhoneContact contact : phoneContacts) {
            firstNames.add(contact.getName());
            lastNames.add(contact.getLastName());
            uniqueIds.add(generateUniqueId());
            String phoneNum = String.valueOf(contact.getPhoneNumber());

            if (phoneNum.startsWith("9")) {
                phoneNum = phoneNum.replaceFirst("9", "09");
            }
            cellphoneNumbers.add(phoneNum);

            emails.add("");
            typeCodes.add(getTypeCode());

        }

        Observable<Response<Contacts>> addContactsObservable;

        if (getPlatformHost() != null) {

            if (!Util.isNullOrEmpty(getTypeCode())) {

                addContactsObservable = contactApi.addContacts(getToken(),
                        TOKEN_ISSUER,
                        firstNames,
                        lastNames,
                        emails,
                        uniqueIds,
                        cellphoneNumbers,
                        typeCodes);

            } else {
                addContactsObservable = contactApi.addContacts(getToken(),
                        TOKEN_ISSUER,
                        firstNames,
                        lastNames,
                        emails,
                        uniqueIds,
                        cellphoneNumbers);
            }

            showLog("Call add contacts " + new Date());

            addContactsObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(contactsResponse -> {

                        showLog(">>> Server Respond " + new Date());

                        boolean error = false;

                        if (contactsResponse.body() != null) {
                            error = contactsResponse.body().getHasError();
                            showLog(">>>Response: " + contactsResponse.code());
                            showLog(">>>ReferenceNumber: " + contactsResponse.body().getReferenceNumber());
                            showLog(">>>Ott: " + contactsResponse.body().getOtt());
                        }

                        if (contactsResponse.isSuccessful()) {

                            if (error) {

                                captureError(contactsResponse.body().getMessage(), contactsResponse.body().getErrorCode()
                                        , uniqueId);

                                showLog("Error add Contacts: " + contactsResponse.body().getMessage());

                                subject.onError(new Throwable());

                            } else {

                                //successful response
                                Contacts contacts = contactsResponse.body();
                                ChatResponse<Contacts> chatResponse = new ChatResponse<>();

                                chatResponse.setResult(contacts);
                                chatResponse.setUniqueId(uniqueId);

                                Runnable updatePhoneContactsDBTask = () -> {
                                    try {
                                        boolean result = phoneContactDbHelper.addPhoneContacts(phoneContacts);
                                        if (!result) {
                                            resetCache(() -> phoneContactDbHelper.addPhoneContacts(phoneContacts));
                                        }
                                    } catch (Exception e) {
                                        showErrorLog("Updating Contacts cache failed: " + e.getMessage());
                                        onUnknownException(uniqueId, e);
                                    }
                                };


                                Runnable updateCachedContactsTask = () -> {
                                    if (cache) {
                                        try {
//                                            messageDatabaseHelper.saveContacts(chatResponse.getResult().getResult(), getExpireAmount());
                                            dataSource.saveContactsResultFromServer(chatResponse.getResult().getResult());
                                        } catch (Exception e) {
                                            showErrorLog("Saving Contacts Failed: " + e.getMessage());
                                            onUnknownException(uniqueId, e);
                                        }
                                    }
                                };

                                new PodThreadManager()
                                        .addNewTask(updatePhoneContactsDBTask)
                                        .addNewTask(updateCachedContactsTask)
                                        .addNewTask(() -> subject.onNext(phoneContacts))
                                        .runTasksSynced();
                            }
                        } else {

                            captureError(contactsResponse.message(), contactsResponse.code()
                                    , uniqueId);

                            showLog("Error add Contacts: " + contactsResponse.raw());

                            subject.onError(new Throwable());
                        }

                    }, throwable ->
                    {
                        captureError(throwable.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                        subject.onError(throwable);
                    });
        }

        return subject;


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

            messageDatabaseHelper.saveUserInfo(userInfo, handleDBError(() -> {
            }, () -> {
            }));
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

    private MessageDatabaseHelper.IRoomIntegrity handleDBError(Runnable onSuccessAction, Runnable onErrorAction) {

        return new MessageDatabaseHelper.IRoomIntegrity() {
            @Override
            public void onDatabaseNeedReset() {

                showLog("Reset database");
                initDatabaseWithKey(getKey());
                showLog("Database reset successfully");
                cache = true;
                onSuccessAction.run();

            }

            @Override
            public void onResetFailed() {

                showErrorLog("DB reset failed...!");
                showErrorLog("Cache is disable...");
                cache = false;
                onErrorAction.run();
            }

            @Override
            public void onRoomIntegrityError() {

                showErrorLog("Room integrity error");
                cache = false;

            }

            @Override
            public void onDatabaseDown() {

                showErrorLog("Database down");
                cache = false;

            }
        };
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

                addToUploadQueue(description, fileUri, messageType, threadId, "", uniqueId, systemMetaData, messageId, mimeType, center, methodName, file, fileSize);

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
                captureError("File is not Exist", ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
            }
        } else {

            Permission.Request_READ_STORAGE(activity, READ_EXTERNAL_STORAGE_CODE);

            captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
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
                    showErrorLog(e.getMessage());
                }

                ProgressRequestBody requestFile = new ProgressRequestBody(file, mimeType, uniqueId,

                        new ProgressRequestBody.UploadCallbacks() {

                            @Override
                            public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                                Log.i(TAG, "on progress");
                                if (handler != null) {
                                    handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                                    if (log)
                                        Log.i(TAG, "uniqueId " + uniqueId + " bytesSent " + progress);
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
                            String jsonError = captureError(errorMessage, errorCode, uniqueId);
//                            listenerManager.callOnLogEvent(jsonError);
                            ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

                            if (handler != null) {
                                handler.onError(jsonError, error);
                            }

                        } else {

                            //remove from Uploading Queue
                            removeFromUploadQueue(uniqueId);

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


                            // send to handler
                            if (handler != null) {
                                handler.onFinishImage(imageJson, chatResponse);
                            }

                            if (isReplyMessage(methodName)) {

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
                        String jsonError = captureError(throwable.getMessage()
                                , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);


                        ErrorOutPut error = new ErrorOutPut(true, throwable.getMessage()
                                , ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);

                        if (log) Log.e(TAG, jsonError);
                        if (handler != null) {
                            handler.onError(jsonError, error);
                        }
                    } catch (Exception e) {
                        showErrorLog(e.getMessage());
                        onUnknownException(uniqueId, e);
                    }

                });

                /*
                 * Cancel Upload request
                 * */

                initCancelUpload(uniqueId, subscribe);


            } else {
                if (log) Log.e(TAG, "FileServer url Is null");
            }

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
    }

    private void removeFromUploadQueue(String uniqueId) {
        if (cache) {
            dataSource.deleteUploadingQueue(uniqueId);
//            messageDatabaseHelper.deleteUploadingQueue(uniqueId);
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

                    addToUploadQueue(description, fileUri, messageType, threadId, "", uniqueId, systemMetadata, mimeType, file, file_size);

                    lFileUpload.setFileSize(file_size);

                    lFileUpload.setFile(file);

                    mainUploadFileMessage(lFileUpload);

                } else {
                    if (log) Log.e(TAG, "File Is Not Exist");
                    captureError("File is not Exist", ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                }
            } else {
                Permission.Request_READ_STORAGE(activity, READ_EXTERNAL_STORAGE_CODE);
                String jsonError = captureError(ChatConstant.ERROR_READ_EXTERNAL_STORAGE_PERMISSION
                        , ChatConstant.ERROR_CODE_READ_EXTERNAL_STORAGE_PERMISSION, uniqueId);
                if (log) Log.e(TAG, jsonError);
            }
        } catch (Throwable e) {
            showErrorLog(e.getMessage());
            onUnknownException(uniqueId, e);
        }
    }

    private void addToUploadQueue(String description,
                                  Uri fileUri,
                                  Integer messageType,
                                  long threadId,
                                  String userGroupHash,
                                  String uniqueId,
                                  String systemMetadata,
                                  String mimeType,
                                  File file,
                                  long file_size) {

        UploadingQueueCache uploadingQueue = new UploadingQueueCache();
        uploadingQueue.setMessage(description);
        uploadingQueue.setMessageType(messageType);
        uploadingQueue.setSystemMetadata(systemMetadata);
        uploadingQueue.setUniqueId(uniqueId);
        uploadingQueue.setThreadId(threadId);
        uploadingQueue.setUserGroupHash(userGroupHash);

        String metaData = createFileMetadata(file, null, 0, mimeType, file_size, fileUri.toString());

        uploadingQueue.setMetadata(metaData);

        if (cache) {
            dataSource.insertUploadingQueue(uploadingQueue);
//            messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
        } else {
            uploadingQList.put(uniqueId, uploadingQueue);
        }
    }

    private void addToUploadQueue(String description,
                                  Uri fileUri,
                                  Integer messageType,
                                  long threadId,
                                  String userGroupHash,
                                  String uniqueId,
                                  String systemMetaData,
                                  long messageId,
                                  String mimeType,
                                  String center,
                                  String methodName,
                                  File file,
                                  long fileSize) {

        UploadingQueueCache uploadingQueue = new UploadingQueueCache();
        uploadingQueue.setMessage(description);
        uploadingQueue.setMessageType(messageType);
        uploadingQueue.setSystemMetadata(systemMetaData);
        uploadingQueue.setUniqueId(uniqueId);
        uploadingQueue.setThreadId(threadId);
        uploadingQueue.setId(messageId);
        uploadingQueue.setUserGroupHash(userGroupHash);

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
            dataSource.insertUploadingQueue(uploadingQueue);
//            messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
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
            dataSource.insertUploadingQueue(uploadingQueue);
//            messageDatabaseHelper.insertUploadingQueue(uploadingQueue);
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
                    public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                        if (handler != null)
                            handler.onProgressUpdate(uniqueId, progress, totalBytesSent, totalBytesToSend);
                    }
                });

                MultipartBody.Part body = MultipartBody
                        .Part.createFormData("file",
                                file.getName(),
                                requestFile);

                Observable<Response<FileUpload>> uploadObservable =
                        fileApi.sendFile(body, getToken(), TOKEN_ISSUER, name);

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

                                        String jsonError = captureError(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                        ErrorOutPut errorOutPut = new ErrorOutPut(true, ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                                        handler.onError(jsonError, errorOutPut);

                                    }

                                } else {

                                    //remove from Uploading Queue
                                    removeFromUploadQueue(uniqueId);

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

                                        if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {

                                            showLog("SEND_REPLY_FILE_MESSAGE", jsonMeta);
                                            mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, uniqueId, null);

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

                initCancelUpload(uniqueId, subscription);

            } else {
                if (log) Log.e(TAG, "FileServer url Is null");
            }

        } else {
            captureError(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
//            listenerManager.callOnLogEvent(jsonError);
        }
    }

    private boolean isReplyMessage(String methodName) {
        return !Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG);
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
                            captureError(ChatConstant.ERROR_CURRENT_DEVICE, ChatConstant.ERROR_CODE_CURRENT_DEVICE, null);
                        }
                    } else {
                        if (deviceResults.code() == 401) {
                            captureError("unauthorized", deviceResults.code(), null);

                        } else {
                            captureError(deviceResults.message(), deviceResults.code(), null);
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
            if (!handlerSend.containsKey(chatMessage.getUniqueId())) {
//                messageDatabaseHelper.saveThreads(threads);
                dataSource.saveThreadResultFromServer(threads);
            }
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
                    dataSource.deleteWaitQueueWithUniqueId(messageVO.getUniqueId());
//                    messageDatabaseHelper.deleteWaitQueueMsgs(messageVO.getUniqueId());
                }
            } else {
                for (MessageVO messageVO : messageVOS) {
                    waitQList.remove(messageVO.getUniqueId());
                }
            }
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
            onUnknownException(chatMessage.getUniqueId(), throwable);
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

        if (cache) {
//            messageDatabaseHelper.saveContacts(contacts, getExpireAmount());
            dataSource.saveContactsResultFromServer(contacts);
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
        if (userId == 0) return;
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
        captureError("On Async Disconnected: " + textMessage, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");
    }

    @Override
    public void onError(String textMessage) {
        super.onError(textMessage);

        captureError("On Async Error: " + textMessage, ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");

    }

    @Override
    public void handleCallbackError(Throwable cause) {
        super.handleCallbackError(cause);
        try {

            captureError("Async Callback Error: " + cause.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

