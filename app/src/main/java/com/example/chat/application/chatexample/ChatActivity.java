package com.example.chat.application.chatexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat.application.chatexample.enums.ServerType;
import com.example.chat.application.chatexample.model.Method;
import com.example.chat.application.chatexample.ui.custom.ChildView;
import com.example.chat.application.chatexample.ui.custom.HeaderView;
import com.example.chat.application.chatexample.utils.Utils;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.RoleType;
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
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.GetTagListRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.thread.request.ChangeThreadTypeRequest;
import com.fanap.podchat.chat.thread.request.GetMutualGroupRequest;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.notification.PodNotificationManager;
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
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetFile;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetImage;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetPodSpaceImage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
import com.fanap.podchat.requestobject.RequestUploadFile;
import com.fanap.podchat.requestobject.RequestUploadImage;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.TextMessageType;
import com.fanap.podchat.util.ThreadType;
import com.fanap.podchat.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.mindorks.placeholderview.annotations.Resolve;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity
        implements View.OnClickListener, ChatContract.view {
    public static final String APP_ID = "appid";
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1007;
    public static final String THREAD_UNIQUE_NAME = "unique_name_4_1584016531111";
    private static final int FILE_REQUEST_CODE = 2;
    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private static final String TEST_THREAD_HASH = "JNLGU4YQE5DZTD9Z";
    public static int TEST_THREAD_ID = 53255;


    ArrayList<String> runningSignals = new ArrayList<>();
    long notificationThreadId = 0;
    long notificationMessageId = 0;

    private ChatContract.presenter presenter;


    private final Gson gson = new GsonBuilder().create();
    private Uri uri;

    private long tagId = 23;
    private String fileUri, signalUniq, downloadingId;

    private long TEST_THREAD_LAST_SEEN_MESSAGE_TIME;


    // Chat server config
    private final Enum<ServerType> serverType = ServerType.Sandbox;
//    private String TOKEN = BaseApplication.getInstance().getString(R.string.Pooria_Pahlevani);
        private String TOKEN = "52678dcd8a2b48e7b8660cbd0dfea02a";
//        private String TOKEN = "c13546cffb4d4bd682f19edbc6084977";
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String serverName = "chat-server";
    private String podSpaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_main);
    private String socketAddress;
    private String platformHost;
    private String fileServer;


    //views
    private Button btConnect, btSendMsg, btChangeThreadId, btSettoken, btGoToUpload;
    private TextView tvState, tvUserInfo, tvServerType;
    private EditText et_text;

    //upload views
    private ImageButton ibClose;
    private TextView tvPercent;
    private ImageView imImage, imImagedownloaded;
    private Button btChooseFile, btUploadImage, btDownloadFile, btDownloadImage, btUploadFile, btSentFile;
    private ConstraintLayout vUpload;


    //other variables
    private Map<String, List<Method>> categoryMap;
    private List<Method> movieList;
    private ExpandablePlaceHolderView expandablePlaceHolderView;


    private String fileHash = "UDXVMRM5E8R2KR44";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();


        setListeners();


        getNotificationData();

        setExpandableList();
    }

    private void setExpandableList() {
        movieList = new ArrayList<>();
        categoryMap = new HashMap<>();
        expandablePlaceHolderView = (ExpandablePlaceHolderView) findViewById(R.id.expandablePlaceHolder);
        loadData();
    }

    private void onMethodClick(String methosName) {
        final String[] fileUnique = new String[1];
        switch (methosName) {
            case "SyncContact":
                presenter.syncContact(ChatActivity.this);
                break;

            case "SearchContact":
                searchContact();
                break;

            case "BlockContact":
                blockContact();

                break;

            case "UnBlockContact":
                unblockContact();
                break;

            case "GetContacts":
                getContacts();
                break;

            case "AddContact":
                addContact();
                break;

            case "RemoveContact":
                removeContact();
                break;

            case "UpdateContact":
                updateContact();
                break;

            case "UploadFile":
                uploadImage();
                break;

            case "DownloadFile":
                downloadFile();
                break;

            case "UploadImage":
                uploadImage();
                break;

            case "DownloadImage":
                downloadFile();
                break;

            case "CancelDownloadImage":
                cancelDownloadImage();

                break;

            case "DownloadFileFromThread":
                downloadFile();
                break;

            case "UploadFileToThread":
                uploadImage();
                break;

            case "RetryUpload":
                retryUpload();
                break;

            case "CancelUpload":
                cancelUpload(fileUnique[0]);
                break;

            case "SendReplyFileMessage":
                sendFileMessage(fileUnique);
                break;

            case "CreateThread":
                createThread();
                break;

            case "AddParticipant":
                addParticipants();
                break;

            case "RemoveParticipant":
                removeParticipants();
                break;

            case "GetAllThread":
                getThreads();
                break;

            case "PinThread":
                pinThreadToTop();
                break;

            case "UnPinThread":
                unPinThread();
                break;

            case "SpamThread":
                spamThread();
                break;
            case "LeaveThread":
                leaveThread();
                break;

            case "GetAdmins":
                getAdminList();
                break;

            case "ClearHistory":
                clearHistory();
                break;

            case "CloseThread":
                closeThread();
                break;

            case "RenameThread":
                renameTHread();
                break;

            case "GetThreadParticipants":
                getThreadParticipants();
                break;

            case "GetThreadHistory":
                getThreadHistory();
                break;

            case "MuteThread":
                muteThread();
                break;
            case "UnMuteThread":
                unMuteThread();
                break;

            case "UpdateThreadInfo":
                updateThreadInfo();
                break;

            case "GetDeliverMessageList":
                getDeliverMessageList();
                break;

            case "CreateThreadWithMessage":
                createThreadWithMessage();
                break;

            case "GetThreadWithCoreUser":
                getThreadWithCoreUser();
                break;

            case "CreateThreadWithFile":
                createThreadWithFile();
                break;

            case "GetHashTagHistory":
                getHashTagHistory();
                break;

            case "ChangePrivateThreadToPublic":
                changePrivateThreadToPublic();
                break;

            case "SearchHistory":
                searchHistory();
                break;

            case "SetAdminRules":
                setAdminRules();

                break;
            case "RemoveAdminRules":

                removeAdminRules();
                break;
            case "SafeLeave":
                break;

            case "GetMentionList":
                getMentionList();
                break;

            case "CheckIsNameAvailable":
                checkIsNameAvailable();
                break;

            case "CreatePublicThread":
                createPublicThread();
                break;

            case "JoinPublicThread":
                joinPublicThread();
                break;

            case "DeleteGroup":

                break;

            case "SetRole":
                addAuditor();
                break;

            case "GetRole":
                getUserRoles();
                break;

            case "RemoveRole":
                removeAuditor();
                break;

            case "SendTextMessage":
                sendMessage(null);
                break;

            case "SendFileMessage":
                sendFileMessage(fileUnique);
                break;

            case "PingMessage":
                pinMessageToTop();
                break;

            case "ForwardMessage":
                forwardMessage();
                break;

            case "ReplyMessage":
                replyMessage();
                break;

            case "SeenMessage":
                seenMessage();
                break;

            case "SeenMessageList":
                seenMessageList();
                break;

            case "GetNotSeenDur":
                getNotSeenDur();
                break;

            case "DeleteMessage":
                deleteMessage();
                break;

            case "EditMessage":
                editMessage();
                break;

            case "StartTyping":
                startTyping();

                break;

            case "StopTyping":
                stopTyping();
                break;

            case "CreateBot":
                createBot();
                break;

            case "DefineBotCommand":
                defineBotCommand();
                break;

            case "StartBot":
                startBot();
                break;

            case "StopBot":
                stopBot();
                break;

            case "GetUserBots":
                getUserBots();
                break;

            case "AddTag":
                createTag();
                break;

            case "EditTag":
                editTag();
                break;

            case "DeleteTag":
                deleteTag();
                break;

            case "AddTagParticipant":
                addTagParticipant();
                break;

            case "RemoveTagParticipant":
                removeTagParticipant();
                break;

            case "GetTagList":
                getTagList();
                break;

            case "GetUserInfo":
                getUserInfo();
                break;

            case "GetBlockedList":
                getBlockedList();

                break;

            case "GetMutualGroup":
                getMutualGroup();
                break;

            case "UpdateProfile":
                updateUserProfile();
                break;

            case "GetCacheSize":
                presenter.getCacheSize();
                break;

            case "ClearCache":
                clearCache();
                break;

            case "GetStorageSize":
                getStorageSize();
                break;

            case "ClearStorage":
                clearStorage();
                break;

            case "RegisterAssistant":
                registerAssistant();
                break;

            case "DeactiveAssistant":
                deactiveAssistant();
                break;

            case "GetAssistants":
                getAssistants();
                break;

            case "GetAssistantHistory":
                getAssistantHistory();
                break;

            case "BlockAssistant":
                blockAssistant();
                break;

            case "UnBlockAssistant":
                unBlockAssistant();
                break;

            case "GetBlocksAssistant":
                getBlocksAssistant();
                break;

            case "MapStatic":
                mapStatic();
                break;

            case "MapReverse":
                mapReverse();
                break;

            case "SendLocationMsg":
                sendLocationMsg();
                break;

            case "GetSentryLogs":
                getSentryLogs();
                break;

            case "SearchMap":
                searchMap();
                break;

            case "RouteMap":
                routeMap();
                break;
        }
    }


    private void loadData() {
        movieList = Utils.loadJSONFromAsset(this);
        getHeaderAndChild(movieList);
    }

    private void getHeaderAndChild(List<Method> movieList) {
        for (Method method : movieList) {
            List<Method> movieList1 = categoryMap.get(method.getCategoty());
            if (movieList1 == null) {
                movieList1 = new ArrayList<>();
            }
            movieList1.add(method);
            categoryMap.put(method.getCategoty(), movieList1);
        }

        Log.d("Map", categoryMap.toString());
        Iterator<Map.Entry<String, List<Method>>> it = categoryMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<Method>> pair = it.next();
            Log.d("Key", pair.getKey());
            expandablePlaceHolderView.addView(new HeaderView(this, pair.getKey()));
            List<Method> movieList1 = pair.getValue();
            for (Method movie : movieList1) {
                expandablePlaceHolderView.addView(new ChildView(this, movie, this::onMethodClick));
            }
            it.remove();
        }
    }


    private void setListeners() {
        //new
        btConnect.setOnClickListener(this);
        btSendMsg.setOnClickListener(this::sendMessage);
        btChangeThreadId.setOnClickListener(this::setThreadId);
        btSettoken.setOnClickListener(this::setToken);
        btGoToUpload.setOnClickListener(v -> vUpload.setVisibility(View.VISIBLE));

        //upload view listeners
        ibClose.setOnClickListener(v -> vUpload.setVisibility(View.GONE));
        btChooseFile.setOnClickListener(v -> showPicChooser());

        btChooseFile.setOnClickListener(v -> showPicChooser());

        btUploadImage.setOnClickListener(v -> uploadImageProgress());
        btDownloadFile.setOnClickListener(v -> downloadFile());
        btDownloadImage.setOnClickListener(v -> downloadImage());
        btUploadFile.setOnClickListener(v -> uploadFileProgress());
        btSentFile.setOnClickListener(v -> sendFileMessage(new String[1]));
        // end new
    }

    private void init() {

        Logger.addLogAdapter(new AndroidLogAdapter());
        setContentView(R.layout.activity_chat);


        // new ui
        btConnect = findViewById(R.id.btConnect);
        tvState = findViewById(R.id.tvState);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvServerType = findViewById(R.id.tvServerType);
        et_text = findViewById(R.id.et_text);
        btSendMsg = findViewById(R.id.btSendMsg);
        btChangeThreadId = findViewById(R.id.btChangeThreadId);
        btSettoken = findViewById(R.id.btSettoken);
        btGoToUpload = findViewById(R.id.btGoToUpload);

        //upload - download views
        tvPercent = findViewById(R.id.tvPercent);
        ibClose = findViewById(R.id.ibClose);
        imImage = findViewById(R.id.imImage);
        imImagedownloaded = findViewById(R.id.imImagedownloaded);
        vUpload = findViewById(R.id.vUpload);
        btChooseFile = findViewById(R.id.btChooseFile);
        btSentFile = findViewById(R.id.btSentFile);

        btUploadImage = findViewById(R.id.btUploadImage);
        btDownloadFile = findViewById(R.id.btDownloadFile);
        btDownloadImage = findViewById(R.id.btDownloadImage);
        btUploadFile = findViewById(R.id.btUploadFile);
        // end of

        presenter = new ChatPresenter(this, this, this);
        presenter.clearNotifications();
    }


    @Override
    public void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {
        tvUserInfo.setText(outPutUserInfo.getResult().getUser().getName());

    }

    @Override
    public void onGetSentryLogs(String logs) {
        Toast.makeText(this, logs, Toast.LENGTH_SHORT).show();
    }

    private void getNotificationData() {
        if (getIntent() != null && getIntent().getExtras() != null) {

            Bundle a = getIntent().getExtras();

            String threadId = a.getString("threadId");

            String messageId = a.getString("messageId");

            Log.d("CHAT_ACTIVITY", "Notification Data: ");
            Log.d("CHAT_ACTIVITY", "Thread Id: " + threadId);
            Log.d("CHAT_ACTIVITY", "Message Id: " + messageId);

            if (Util.isNotNullOrEmpty(threadId))
                presenter.deliverNotification(threadId);

        }
    }

    //for log events

    //this is for showing sample notification on current device
    private void showNotification() {

        Map<String, String> data2 = new HashMap<>();
        data2.put("messageId", "488779");
        data2.put("messageType", "1");
        data2.put("MessageSenderName", "پوریا");
        data2.put("text", "3");
        data2.put("senderImage", "http://sandbox.pod.ir:8080/nzh/image/?imageId=62606&width=1272&height=1272&hashCode=16bf4878c16-0.7172112194509095");
        data2.put("threadId", "12269");
        data2.put("isGroup", "false");
        PodNotificationManager.showNotification(data2, this);


        String d = "{messageId=488779, messageType=1, MessageSenderName=پوریا, text=3, senderImage=http://sandbox.pod.ir:8080/nzh/image/?imageId=62606&width=1272&height=1272&hashCode=16bf4878c16-0.7172112194509095, threadId=12269, isGroup=false}";

        Map<String, String> data = new HashMap<>();

        data.put("threadName", "گروه کاری " + notificationThreadId);
        data.put("MessageSenderName", "رضا احمدی" + notificationThreadId % 2);
        data.put("text", "سلام چه خبر؟ " + notificationMessageId);
        data.put("isGroup", "true");
        data.put("MessageSenderUserName", "a.ahmadi" + notificationThreadId % 2);
        data.put("messageId", String.valueOf(++notificationMessageId));
        data.put("threadId", String.valueOf(++notificationThreadId));
        data.put("messageType", "1");

        if (notificationThreadId > 2)
            notificationThreadId = 0;

    }

    @Override
    public void onLogEvent(String log) {
        Logger.json(log);
    }

    @Override
    public void onState(String state) {
        runOnUiThread(() -> {
            tvState.setText(state);
        });
    }

    private void getStorageSize() {
        presenter.getStorageSize();
    }

    private void getBlockedList() {
        RequestBlockList request =
                new RequestBlockList.Builder()
                        .count(50)
                        .offset(0)
//    .withNoCache()
                        .build();

        presenter.getBlockList(request);
    }

    private void clearStorage() {
        presenter.clearPictures();
        presenter.clearFiles();
    }

    private void clearCache() {
        presenter.clearDatabaseCache(new Chat.IClearMessageCache() {

            @Override
            public void onCacheDatabaseCleared() {

                Log.e("CACHE", "Cache Cleared Successfully");

            }

            @Override
            public void onExceptionOccurred(String cause) {
                Log.e("CACHE", "Exception Occurred");

            }
        });
    }

    private void stopTyping() {
        if (runningSignals.size() > 0) {

            String uniqueId = runningSignals.get(runningSignals.size() - 1);

            stopTyping(uniqueId);

            runningSignals.remove(uniqueId);
        }
        return;
    }

    private void getDeliverMessageList() {
        RequestDeliveredMessageList requestD = new RequestDeliveredMessageList
                .Builder(50532).build();
        presenter.deliveredMessageList(requestD);
    }

    private void seenMessageList() {

        RequestSeenMessageList requests = new RequestSeenMessageList
                .Builder(TEST_THREAD_ID).build();
        presenter.getSeenMessageList(requests);
    }

    private void updateThreadInfo() {


//        RequestUploadImage requestUploadImage =
//       new RequestUploadImage.Builder(ChatActivity.this, getUri())
//      .sethC(140)
//      .setwC(140)
//      .setUserGroupHashCode(TEST_THREAD_HASH)
//      .build();


        RequestThreadInfo request =
                new RequestThreadInfo.Builder(TEST_THREAD_ID)
                        .name("Chat sample thread") // required. if not set, thread name will set to null
//   .metadata("{}") // required. if not set, thread metadata will set to null
                        .image("https://podspace.pod.ir/nzh/drive/downloadImage?hash=ELJIHZN9NP37ZIDA") // required. if not set, thread image will set to null
                        .description("this is test description updated on " + new Date().toString()) // required. if not set, thread name will set to null
//   .setUploadThreadImageRequest(requestUploadImage) // set when you wanna upload thread image
//   .setUserGroupHash(TEST_THREAD_HASH) // set when you wanna upload thread image
                        .build();

        presenter.updateThreadInfo(request);
    }

    private void unblockContact() {
        Long ubThreadId = null;
//     Long ubUserId = 121L;
        Long ubUserId = null;
        Long ubContactId = null;
        Long unblockId = (long) TEST_THREAD_ID;
        presenter.unBlock(unblockId, ubUserId, ubThreadId, ubContactId
                , null);
        return;
    }

    private void blockContact() {

        long bThreadId = TEST_THREAD_ID;
        long bUserId = TEST_THREAD_ID;
        long bContactId = TEST_THREAD_ID;

        presenter.block(bContactId, null, null
                , new ChatHandler() {
                    @Override
                    public void onBlock(String uniqueId) {
                        super.onBlock(uniqueId);
                    }
                });

    }

    private void routeMap() {
        presenter.mapRouting("35.7003510,51.3376472", "35.7343510,50.3376472");
    }

    private void searchMap() {
        presenter.searchMap("haram", 36.285671, 59.6034264);
    }

    private void createThreadWithMessage() {

        List<Invitee> invite = new ArrayList<>();
        invite.add(new Invitee(1151568, InviteType.Constants.TO_BE_USER_CONTACT_ID));
        invite.add(new Invitee(1512305, InviteType.Constants.TO_BE_USER_CONTACT_ID));

        RequestUploadImage requestUploadThreadImageImage = new RequestUploadImage
                .Builder(ChatActivity.this, getUri())
                .setwC(140)
                .sethC(140)
                .build();


        RequestThreadInnerMessage message = new RequestThreadInnerMessage
                .Builder("Hi at " + new Date().toString(), TextMessageType.Constants.TEXT)
//     .forwardedMessageIds(listForwardIds)
                .build();

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.Constants.OWNER_GROUP, invite)
                .message(message)
                .setUploadThreadImageRequest(requestUploadThreadImageImage)
                .build();

        presenter.createThreadWithMessage(requestCreateThread);

    }

    private void cancelDownloadImage() {


        boolean result = presenter.cancelDownload(downloadingId);

        Log.e("DOWNLOAD", "Cancel Download Result " + result);


    }

    private void downloadWithGlide() {

        try {
            String url = "https://podspace.pod.ir/nzh/drive/downloadImage?hash=V61H4MEOY488X7KJ";

            RequestOptions requestOptions = new RequestOptions();

            requestOptions = requestOptions
                    .placeholder(R.mipmap.ic_group)
                    .error(R.mipmap.ic_profile);


            LazyHeaders header = new LazyHeaders.Builder()
                    .addHeader("_token_", TOKEN)
                    .addHeader("_token_issuer_", "1")
                    .build();

            GlideUrl glideUrl = new GlideUrl(url, header);

//            Glide.with(this)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(glideUrl)
//                    .into(imageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void downloadFile() {


        String url = "https://core.pod.ir/nzh/image?imageId=222808&hashCode=16c3cd2b93f-0.527719303638482";

        //image
//        long imageId = 404143;
        long imageId = 68349;

        long fileId = 68684;


//        String imageHashCode = "17024463974-0.5588549950419339";
        String imageHashCode = "16feadc7bf1-0.5248624596970302";

        String fileHashCode = "17077360d4b-0.2366487166443898";

        RequestGetImage requestGetImage = new RequestGetImage
                .Builder(imageId, imageHashCode, true)
                .build();

        RequestGetFile requestGetFile = new RequestGetFile.Builder(fileId, fileHashCode, true).build();


        RequestGetPodSpaceFile rePod = new RequestGetPodSpaceFile.Builder("GAUPE1ZRK76VOXBS")
                .build();

        RequestGetPodSpaceImage rePodImage = new RequestGetPodSpaceImage
                .Builder(fileHash)
//       .setCrop(true)
                .setQuality(0.45f)
                .setCheckUserGroupAccess(true)
                .build();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            return;

        }

        downloadingId = presenter.downloadFile(rePodImage, new ProgressHandler.IDownloadFile() {


            @Override
            public void onProgressUpdate(String uniqueId, long bytesDownloaded, long totalBytesToDownload) {
                Log.e("DOWNLOAD", "IN ACTIVITY: " + "Downloaded: " + bytesDownloaded + " Left: " + totalBytesToDownload);
            }

            @Override
            public void onProgressUpdate(String uniqueId, int progress) {
                Log.e("DOWNLOAD", "IN ACTIVITY: " + "Progress: " + progress);
            }

            @Override
            public void onError(String uniqueId, String error, String url) {
                Log.e("DOWNLOAD", "IN ACTIVITY: ERROR :(((");
            }

            @Override
            public void onLowFreeSpace(String uniqueId, String url) {
                Log.e("DOWNLOAD", "Low Space...");
            }

            @Override
            public void onFileReady(ChatResponse<ResultDownloadFile> response) {
                Log.e("DOWNLOAD", "IN ACTIVITY: Finish File!!!!");
                Log.e("DOWNLOAD", "File name: " + response.getResult().getFile().getName());
                Log.e("DOWNLOAD", "Uri " + response.getResult().getUri());
                Log.e("DOWNLOAD", "File Exist " + response.getResult().getFile().exists());


                if (response.getResult().getFile().exists()) {


                    try {
                        Bitmap v = BitmapFactory.decodeFile(response.getResult().getFile().getAbsolutePath());
                        runOnUiThread(() -> {
//                            imageMap.setImageBitmap(v)
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DOWNLOAD", "Not Image");
                    }

                }


            }

        });

//        downloadingId = presenter.downloadFile(requestGetImage, new ProgressHandler.IDownloadFile() {
//
//
//   @Override
//   public void onProgressUpdate(String uniqueId, int bytesDownloaded, int totalBytesToDownload) {
//       Log.e("DOWNLOAD", "IN ACTIVITY: " + "Downloaded: " + bytesDownloaded + " Left: " + totalBytesToDownload);
//
//   }
//
//   @Override
//   public void onProgressUpdate(String uniqueId, int progress) {
//
//   }
//
//   @Override
//   public void onError(String uniqueId, String error, String url) {
//       Log.e("DOWNLOAD", "IN ACTIVITY: ERROR :(((");
//
//   }
//
//   @Override
//   public void onLowFreeSpace(String uniqueId, String url) {
//       Log.e("DOWNLOAD", "Low Space...");
//
//   }
//
//   @Override
//   public void onFileReady(ChatResponse<ResultDownloadFile> response) {
//       Log.e("DOWNLOAD", "IN ACTIVITY: Finish File!!!!");
//       Log.e("DOWNLOAD", "File name: " + response.getResult().getFile().getName());
//       Log.e("DOWNLOAD", "Uri " + response.getResult().getUri());
//       Log.e("DOWNLOAD", "File Exist " + response.getResult().getFile().exists());
//
//
//       if (response.getResult().getFile().exists()) {
//
//
//  try {
//      Bitmap v = BitmapFactory.decodeFile(response.getResult().getFile().getAbsolutePath());
//      runOnUiThread(() -> imageMap.setImageBitmap(v));
//  } catch (Exception e) {
//      e.printStackTrace();
//  }
//
//       }
//
//
//   }
//
//        });


    }


    private void downloadImage() {

        RequestGetPodSpaceImage rePodImage = new RequestGetPodSpaceImage
                .Builder(fileHash)
//       .setCrop(true)
                .setQuality(0.45f)
                .setCheckUserGroupAccess(true)
                .build();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
            return;

        }

        downloadingId = presenter.downloadFile(rePodImage, new ProgressHandler.IDownloadFile() {
            @Override
            public void onProgressUpdate(String uniqueId, long bytesDownloaded, long totalBytesToDownload) {
                Log.e("DOWNLOAD", "IN ACTIVITY: " + "Downloaded: " + bytesDownloaded + " Left: " + totalBytesToDownload);
            }

            @Override
            public void onProgressUpdate(String uniqueId, int progress) {
                Log.e("DOWNLOAD", "IN ACTIVITY: " + "Progress: " + progress);
            }

            @Override
            public void onError(String uniqueId, String error, String url) {
                Log.e("DOWNLOAD", "IN ACTIVITY: ERROR :(((");
            }

            @Override
            public void onLowFreeSpace(String uniqueId, String url) {
                Log.e("DOWNLOAD", "Low Space...");
            }

            @Override
            public void onFileReady(ChatResponse<ResultDownloadFile> response) {
                Log.e("DOWNLOAD", "IN ACTIVITY: Finish File!!!!");
                Log.e("DOWNLOAD", "File name: " + response.getResult().getFile().getName());
                Log.e("DOWNLOAD", "Uri " + response.getResult().getUri());
                Log.e("DOWNLOAD", "File Exist " + response.getResult().getFile().exists());
                if (response.getResult().getFile().exists()) {
                    try {
                        Bitmap v = BitmapFactory.decodeFile(response.getResult().getFile().getAbsolutePath());
                        runOnUiThread(() -> {
                            imImagedownloaded.setImageBitmap(v);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DOWNLOAD", "Not Image");
                    }
                }
            }
        });

    }

    private void getUserRoles() {

        RequestGetUserRoles req = new RequestGetUserRoles.Builder()
                .setThreadId(TEST_THREAD_ID)
                .build();


        presenter.getUserRoles(req);


    }

    private void createThreadWithFile() {


        if (getUri() == null) {
            Toast.makeText(this, "Pick a file", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestUploadImage requestUploadThreadImageImage = new RequestUploadImage
                .Builder(this, getUri())
                .setwC(140)
                .sethC(140)
                .build();

        RequestUploadImage requestUploadImage = new RequestUploadImage.Builder(this, getUri())
                .setwC(120)
                .sethC(120)
                .setxC(1)
                .setyC(1)
                .build();

        RequestUploadFile requestUploadFile = new RequestUploadFile.Builder(
                this, getUri()).build();


        List<Invitee> invite = new ArrayList<>();

        //f.kh sandbox
//        invite.add(new Invitee("4893", InviteType.Constants.TO_BE_USER_CONTACT_ID));

        //POURIA main
        invite.add(new Invitee(1151568, InviteType.Constants.TO_BE_USER_CONTACT_ID));
        //MASOUD
//        invite.add(new Invitee(1511971, InviteType.Constants.TO_BE_USER_CONTACT_ID));
        //ARVIN
        invite.add(new Invitee(1512305, InviteType.Constants.TO_BE_USER_CONTACT_ID));
        //MAHYAR
//        invite.add(new Invitee(1196793, InviteType.Constants.TO_BE_USER_CONTACT_ID));

//        RequestThreadInnerMessage innerMessage = new RequestThreadInnerMessage
//       .Builder(TextMessageType.Constants.PICTURE)
//       //       .message("Create thread for File Message Test " + new Date().toString())
////     .forwardedMessageIds(listForwardIds)
//       .build();


        RequestCreateThreadWithFile request = new RequestCreateThreadWithFile
                .Builder(ThreadType.Constants.OWNER_GROUP,
                invite,
                requestUploadFile,
                TextMessageType.Constants.POD_SPACE_FILE)
                .title("Test File PodSpace")
                .setUploadThreadImageRequest(requestUploadThreadImageImage)
//       .message(innerMessage)
                .build();


        presenter.createThreadWithFile(request, new ProgressHandler.sendFileMessage() {


                    @Override
                    public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                        Log.e("CTF", "Upload Progress: " + progress);

                    }

                    @Override
                    public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
                        Log.e("CTF", "Upload Finish (IMAGE): ");

                    }

                    @Override
                    public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {

                        Log.e("CTF", "Upload Finish (FILE): ");

                    }

                    @Override
                    public void onError(String jsonError, ErrorOutPut error) {

                        Log.e("CTF", "Upload Error");


                    }

                }


        );


    }

    private void removeAuditor() {


        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);

        RequestRole requestRole = new RequestRole();
        requestRole.setId(2);
        requestRole.setRoleTypes(typeRoles);

        ArrayList<RequestRole> requestRoles = new ArrayList<>();

        requestRoles.add(requestRole);

        RequestSetAuditor requestAddAdmin = new RequestSetAuditor
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.removeAuditor(requestAddAdmin);


    }


//    private void stopSignalMessage() {
//        presenter.stopSignalMessage(getSignalUniq());
//    }
//
//    private void startSignalMessage() {
//        RequestSignalMsg requestSignalMsg = new RequestSignalMsg.Builder()
//       .signalType(ChatMessageType.SignalMsg.IS_TYPING)
//       .threadId(1961)
//       .build();
//        String uniq = presenter.startSignalMessage(requestSignalMsg);
//        setSignalUniq(uniq);
//    }

    private void addAuditor() {

        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);
        RequestRole requestRole = new RequestRole();
        requestRole.setId(2);
        requestRole.setRoleTypes(typeRoles);
        ArrayList<RequestRole> requestRoles = new ArrayList<>();
        requestRoles.add(requestRole);


        RequestSetAuditor requestAddAdmin = new RequestSetAuditor
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.setAuditor(requestAddAdmin);


    }

    private void setAdminRules() {
        //core 1507
        // thread id 1961
        ArrayList<String> typeRoles = new ArrayList<>();


//        typeRoles.add(RoleType.Constants.THREAD_ADMIN);
//        typeRoles.add(RoleType.Constants.ADD_NEW_USER);
//        typeRoles.add(RoleType.Constants.REMOVE_USER);


        //roles to set auditor
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);


//        typeRoles.add(RoleType.Constants.ADD_RULE_TO_USER);
//        typeRoles.add(RoleType.Constants.CHANGE_THREAD_INFO);
//        typeRoles.add(RoleType.Constants.DELETE_MESSAGE_OF_OTHERS);
//        typeRoles.add(RoleType.Constants.EDIT_MESSAGE_OF_OTHERS);
//        typeRoles.add(RoleType.Constants.POST_CHANNEL_MESSAGE);
//        typeRoles.add(RoleType.Constants.REMOVE_ROLE_FROM_USER);
//

//        ArrayList<String> typeRoles2 = new ArrayList<>();
//        typeRoles2.add(RoleType.Constants.REMOVE_USER);
//        typeRoles2.add(RoleType.Constants.ADD_RULE_TO_USER);


        RequestRole requestRole = new RequestRole();
        requestRole.setId(2);
        requestRole.setRoleTypes(typeRoles);

//
//        RequestRole requestRole2 = new RequestRole();
////        requestRole2.setId(41);
////        requestRole2.setId(123);
//        requestRole2.setRoleOperation("remove");
//        requestRole2.setRoleTypes(typeRoles2);


        ArrayList<RequestRole> requestRoles = new ArrayList<>();

        requestRoles.add(requestRole);
//        requestRoles.add(requestRole2);


        RequestSetAdmin requestAddAdmin = new RequestSetAdmin
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.setAdmin(requestAddAdmin);
    }

    private void removeAdminRules() {

        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.THREAD_ADMIN);
//        typeRoles.add(RoleType.Constants.READ_THREAD);
//        typeRoles.add(RoleType.Constants.ADD_NEW_USER);
        typeRoles.add(RoleType.Constants.REMOVE_USER);

        RequestRole requestRole = new RequestRole();
        requestRole.setId(2);
        requestRole.setRoleTypes(typeRoles);


//        RequestRole requestRole2 = new RequestRole();
//        requestRole2.setId(41);
//        requestRole2.setRoleOperation("remove");
//        requestRole2.setRoleTypes(typeRoles);


        ArrayList<RequestRole> requestRoles = new ArrayList<>();

        requestRoles.add(requestRole);
//        requestRoles.add(requestRole2);

        RequestSetAdmin requestAddAdmin = new RequestSetAdmin
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.removeAdminRules(requestAddAdmin);


    }

    private void sendLocationMsg() {

        String center = "35.7003510,51.3376472";


        String meta = getMetaData();


        RequestLocationMessage requestLocationMessage = new RequestLocationMessage
                .Builder()
                .systemMetadata(meta)
                .center(center)
                .message("Im here now    :   ) ")
                .setUserGroupHash(TEST_THREAD_HASH)
                .activity(ChatActivity.this)
                .threadId(TEST_THREAD_ID)
                .build();


//        presenter.sendLocationMessage(requestLocationMessage);

        presenter.sendLocationMessage(requestLocationMessage, new ProgressHandler.sendFileMessage() {

            @Override
            public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                Log.d("MTAG", "Update progress: " + "Total Bytes sent: " + totalBytesSent + " Total Bytes left " + totalBytesToSend);
            }

            @Override
            public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {

                Log.d("MTAG", "Finish upload");

            }

            @Override
            public void onError(String jsonError, ErrorOutPut error) {

                Log.d("MTAG", "Error upload");

            }
        });


    }

    @Override
    public void onTokenExpired() {

        Toast.makeText(this, "Token Expired ! ! !", Toast.LENGTH_LONG).show();

    }

    private String getMetaData() {

//        return "";
        JsonObject a = new JsonObject();

        a.addProperty("actionType", 1);
        a.addProperty("amount", 100);
        a.addProperty("id", 161);
        a.addProperty("state", 1);
        a.addProperty("destUserId", 1900896);
        return gson.toJson(a);

//
//        a.addProperty("name", "farhad");
//        a.addProperty("family", "kheirkhah");
//        a.addProperty("phoneNumber", "989157770684");

//        return "{\"actionType\":1,\"data\":\"{\"amount\":100,\"description\":\"\",\"id\":161,\"state\":1,\"destUserId\":1900896}\"}";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


//        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
//
//   if (grantResults.length > 0
//  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//       String center = "35.7003510,51.3376472";
//
//       RequestLocationMessage requestLocationMessage = new RequestLocationMessage
//      .Builder()
//      .center(center)
//      .message("This is location ")
//      .activity(ChatActivity.this)
//      .threadId(TEST_THREAD_ID)
//      .build();
//
//       presenter.sendLocationMessage(requestLocationMessage);
//
//
//   }
//        }


    }

    public void mapReverse() {
        double lat = 35.7003510;
        double lng = 51.3376472;
        RequestMapReverse requestMapReverse = new RequestMapReverse.Builder(lat, lng).build();
        presenter.mapReverse(requestMapReverse);
    }

    public void mapStatic() {
        String center = "35.7003510,51.3376472";

        RequestMapStaticImage staticImage = new RequestMapStaticImage.Builder()
                .center(center)
                .build();

        presenter.mapStaticImage(staticImage);
    }


    private void leaveThread() {
        presenter.leaveThread(TEST_THREAD_ID, new ChatHandler() {
            @Override
            public void onLeaveThread(String uniqueId) {
                super.onLeaveThread(uniqueId);
            }
        });
    }

    private void clearHistory() {
        //clear history
        RequestClearHistory requestClearHistory = new RequestClearHistory
                .Builder(TEST_THREAD_ID)
                .build();
        presenter.clearHistory(requestClearHistory);
    }

    private void retryUpload() {
        /*
         * For file you should override onFinishFile or onFinishImage because their respond is different
         *
         * */

        String uniqueId = "2a28e2e8-bae2-4f92-80dd-ccb86bf1a17b";
        RetryUpload retryUpload = new RetryUpload.Builder().activity(ChatActivity.this).uniqueId(uniqueId).build();
        presenter.retryUpload(retryUpload, new ProgressHandler.sendFileMessage() {
            @Override
            public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
            }

            @Override
            public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {
            }

            @Override
            public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
            }
        });
    }

    private void cancelUpload(String uniqueId1) {
        presenter.cancelUpload(uniqueId1);
    }

    private void searchHistory() {
        NosqlSearchMetadataCriteria builderMeta = new NosqlSearchMetadataCriteria
                .Builder("name")
                .is("sina")
                .build();
        NosqlListMessageCriteriaVO criteriaVO = new NosqlListMessageCriteriaVO.Builder(231)
                .count(10)
                .metadataCriteria(builderMeta)
                .build();
        presenter.searchHistory(criteriaVO, null);
    }

    private void searchContact() {
        RequestSearchContact requestSearchContact = new RequestSearchContact
                .Builder("0", "20")
//     .id("2247")
//     .cellphoneNumber("0938")
//     .lastName("kHeI")
//     .firstName("fAr")
                .query("pO")
//     .order("desc")
//     .email("masoudmanson@gmail.com")
                .build();

        presenter.searchContact(requestSearchContact);
    }

    private void uploadImage() {
        if (getUri() != null) {
            presenter.uploadImage(ChatActivity.this, getUri());
        } else {
            showUriEmptyError();
        }
    }

    private void showUriEmptyError() {
        Toast.makeText(this, "please choose a file", Toast.LENGTH_SHORT).show();
    }

    private void sendFileMessage(String[] fileUnique) {
        RequestFileMessage request = new RequestFileMessage.Builder(
                ChatActivity.this,
                TEST_THREAD_ID,
                getUri(),
                TextMessageType.Constants.POD_SPACE_PICTURE) // constructor
                .description("test file message")
//     .systemMetadata(getMetaData())
                .setUserGroupHash(TEST_THREAD_HASH)
//     .setImageHc("100")
//     .setImageWc("100")
//     .setImageXc("1")
//     .setImageYc("1")
                .build();

        fileUnique[0] = presenter.sendFileMessage(request,
                new ProgressHandler.sendFileMessage() {
                    @Override
                    public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                        Log.e("SFM", "Sending files message: " + progress + " * " + totalBytesSent + " * " + totalBytesToSend);
                    }

                    @Override
                    public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {

                        Log.e("SFM", "onFinishImage");
                        fileHash = chatResponse.getResult().getHashCode();

                    }

                    @Override
                    public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {

                        Log.e("SFM", "onFinishFile");
                        fileHash = chatResponse.getResult().getHashCode();
                    }

                    @Override
                    public void onError(String jsonError, ErrorOutPut error) {

                        Log.e("SFM", "onError");


                    }
                });


//      fileUnique[0] = presenter.sendFileMessage(
//     ChatActivity.this,
//     ChatActivity.this,
//     "test file message",
//     TEST_THREAD_ID,
//     getUri(),
//     getMetaData(),
//     TextMessageType.Constants.PICTURE,
//     new ProgressHandler.sendFileMessage() {
//@Override
//public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
//
//    Log.e("SFM", "Sending files message: " + progress + " * " + totalBytesSent + " * " + totalBytesToSend);
//}
//
//@Override
//public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
//
//    Log.e("SFM", "onFinishImage");
//
//}
//
//@Override
//public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {
//
//    Log.e("SFM", "onFinishFile");
//
//}
//
//@Override
//public void onError(String jsonError, ErrorOutPut error) {
//
//    Log.e("SFM", "onError");
//
//
//}
//     });
    }

    private void stopBot() {

        StartAndStopBotRequest request = new StartAndStopBotRequest.Builder(TEST_THREAD_ID,
                "TEST2BOT")
                .build();

        presenter.stopBot(request);

    }

    private void getUserBots() {

        GetUserBotsRequest request = new GetUserBotsRequest.Builder().build();

        presenter.getUserBots(request);

    }

    public void createTag() {
        CreateTagRequest request = new CreateTagRequest.Builder("Tag_" + System.currentTimeMillis() / 1000).build();
        presenter.createTag(request);
    }

    public void editTag() {
        EditTagRequest request = new EditTagRequest.Builder(tagId, "PrivateThreads" + System.currentTimeMillis()).build();
        presenter.editTag(request);
    }

    public void deleteTag() {
        DeleteTagRequest request = new DeleteTagRequest.Builder(tagId).build();
        presenter.deleteTag(request);
    }

    public void addTagParticipant() {
        List<Long> threadIds = new ArrayList<>();
        threadIds.add(8688l);
        threadIds.add(8730l);
        threadIds.add(8729l);
        AddTagParticipantRequest request = new AddTagParticipantRequest.Builder(tagId, threadIds).build();
        presenter.addTagParticipant(request);
    }

    public void getTagList() {
        GetTagListRequest request = new GetTagListRequest.Builder().build();
        presenter.getTagList(request);
    }

    public void removeTagParticipant() {
        List<Long> threadIds = new ArrayList<>();
        threadIds.add(8688l);
        threadIds.add(8730l);
        threadIds.add(8729l);
        RemoveTagParticipantRequest request = new RemoveTagParticipantRequest.Builder(tagId, threadIds).build();
        presenter.removeTagParticipant(request);
    }

    @Override
    public void onTagCreated(TagResult result) {
        tagId = result.getTag().getTagId();
    }

    private void startBot() {

        StartAndStopBotRequest request = new StartAndStopBotRequest.Builder(TEST_THREAD_ID,
                "TEST2BOT")
                .build();

        presenter.startBot(request);

    }

    private void defineBotCommand() {

        List<String> commands = new ArrayList<>();
        commands.add("/command1");
        commands.add("/command2");

        DefineBotCommandRequest request = new DefineBotCommandRequest.Builder("TEST2BOT", commands)
                .build();

        presenter.defineBotCommand(request);

    }

    private void createBot() {

        CreateBotRequest request = new CreateBotRequest.Builder("TEST123BOT")
                .build();

        presenter.createBot(request);

    }

    private void updateUserProfile() {


        RequestUpdateProfile request = new RequestUpdateProfile
                .Builder("عِیب رِندان مَکُن ای زاهِدِ پاکیزه‌سِرِشت")
                .setMetadata(getMetaData())
                .build();


        presenter.updateChatProfile(request);


    }

    private void seenMessage() {


        presenter.seenMessage(TEST_THREAD_ID, 0, null);
    }

    private void spamThread() {

        RequestSpam requestSpam = new RequestSpam.Builder()
                .threadId(TEST_THREAD_ID)
                .build();

        presenter.spam(requestSpam);


    }

    private void getAdminList() {
        //2116
        //2115
        //2107
        RequestGetAdmin requestGetAdmin = new RequestGetAdmin
//       .Builder(10654,true)
                .Builder(TEST_THREAD_ID)
//       .admin(true)
//       .count(50)
//       .threadId(TEST_THREAD_ID)
//       .withNoCache()
                .build();

        presenter.getAdminList(requestGetAdmin);
    }

    public void deleteMessage() {


        ArrayList<Long> msgIds = new ArrayList<>();

        msgIds.add((long) TEST_THREAD_ID);
//        msgIds.add(47566L);
//        msgIds.add(47564L);


        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder()
                .messageIds(msgIds)
                .deleteForAll(true)
                .build();

        presenter.deleteMessage(requestDeleteMessage, null);


//        presenter.deleteMessage(16804, true, new ChatHandler() {
//   @Override
//   public void onDeleteMessage(String uniqueId) {
//       super.onDeleteMessage(uniqueId);
//   }
//        });


    }

    private void removeParticipants() {
        List<Invitee> inviteeList = new ArrayList<>();

//        List<Long> participantIds = new ArrayList<>();
//        participantIds.add(18476L);
//        participantIds.add(5581L);
//        participantIds.add(1261L);


//        //SET INVITEE LIST
//        Invitee invite = new Invitee("30150", InviteType.Constants.TO_BE_CORE_USER_ID);
//        Invitee invite2 = new Invitee("43593", InviteType.Constants.TO_BE_CORE_USER_ID);
//        //  Invitee invite2 = new Invitee("5736", InviteType.Constants.TO_BE_CORE_USER_ID);
//        inviteeList.add(invite);
//        // inviteeList.add(invite2);


//        inviteeList.add(new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        inviteeList.add(new Invitee("63255", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        inviteeList.add(new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        inviteeList.add(new Invitee("18479", InviteType.Constants.TO_BE_USER_ID));
//        63253L
        long threadId = TEST_THREAD_ID;
        RemoveParticipantRequest request = new RemoveParticipantRequest
                .Builder(threadId, null)
                .setInvitees(inviteeList)
                .build();
        presenter.removeParticipants(request, null);

    }

    private void addParticipants() {


        //add with coreUserIds
//        inviteeList.add(new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        inviteeList.add(new Invitee("63255", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        inviteeList.add(new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID));

        RequestAddParticipants request = RequestAddParticipants
                .newBuilder()
                .threadId((long) TEST_THREAD_ID)
//       .withCoreUserIds(982L, 5241L)
                .withUserNames("nadia.anvari")
//      "ms.alavizadeh",
//      "bhamidpour",
//      "z.morshedi",
//      "m.rashed")
//       .withContactIds(10001L,1000L)
//       .withCoreUserId(18478L)
//                .withUsername("TEST2BOT")
                .build();


        presenter.addParticipants(request, null);


    }

    public void sendMessage(View view) {

//        Inviter inviter = new Inviter();
//        inviter.setName("farhad");
//        String meta = gson.toJson(inviter);

        String meta = getMetaData();

        RequestMessage request = new RequestMessage.Builder("Salam", 1000)
                .messageType(TextMessageType.Constants.TEXT)
                .build();
//
//
        presenter.sendTextMessage(et_text.getText().toString(), TEST_THREAD_ID, TextMessageType.Constants.TEXT, meta, null);
//
//
//        editText.setText("");

//        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
//       , 1576, 2, meta, null);
//
//       new ChatHandler() {
//  @Override
//  public void onSent(String uniqueId, long threadId) {
//      super.onSent(uniqueId, threadId);
//      Toast.makeText(ChatActivity.this, "its worked", Toast.LENGTH_SHORT).show();
//  }
//
//  @Override
//  public void onSentResult(String content) {
//      super.onSentResult(content);
//      if (content != null) {
// Toast.makeText(ChatActivity.this, "no null", Toast.LENGTH_SHORT).show();
//
//      }
//  }
//       });

//        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
//       , 312, meta, new Chat.SendTextMessageHandler() {
//  @Override
//  public void onSent(String uniqueId, long threadId) {
//
//  }
//       });


// String text = editText.getText().toString();
////        long textThread = Long.valueOf(editTextThread.getText().toString());
////        if (!text.equals("")) {
////   presenter.sendTextMessage(text, 381, null);
////        } else {
////   Toast.makeText(this, "Message is Empty", Toast.LENGTH_SHORT).show();
////        }
    }


    private void closeThread() {

        presenter.closeThread(TEST_THREAD_ID);
    }

    private void getSentryLogs() {

        presenter.getSentryLogs();
    }

    private void getMentionList() {
        RequestGetMentionList req = new RequestGetMentionList
                .Builder(TEST_THREAD_ID)
//      .setAllMentioned(true)
//      .setUnreadMentioned(true)
//      .unreadMentions()
                .offset(0)
                .count(25)
//      .withNoCache()
                .build();

        presenter.getMentionList(req);
    }

    private void unpinMessage() {
        RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                .setMessageId(TEST_THREAD_ID)
                .build();


        presenter.unPinMessage(requestPinMessage);
    }

    private void pinMessageToTop() {
        RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                .setMessageId(TEST_THREAD_ID)
                .setNotifyAll(true)
                .build();

        presenter.pinMessage(requestPinMessage);
    }

    private void unPinThread() {
        RequestPinThread requestPinThread = new RequestPinThread.Builder(TEST_THREAD_ID)
                .build();

        presenter.unPinThread(requestPinThread);
    }

    private void pinThreadToTop() {
        RequestPinThread requestPinThread = new RequestPinThread.Builder(TEST_THREAD_ID)
                .build();

        presenter.pinThread(requestPinThread);
    }

    private void registerAssistant() {
        //63263 kheirkhah
        ////63264 anvari

        //63254 sajadi
        //63256 amjadi
        //63257 zhiani
        //invite list

        //103187 nemati sandbox
//        52987 sajadi 9063 sandbox
        Invitee invite = new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID);

        //roles
        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.REMOVE_ROLE_FROM_USER);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVo.setContactType("default");
        assistantVo.setRoles(typeRoles);

        assistantVos.add(assistantVo);

        RegisterAssistantRequest request = new RegisterAssistantRequest.Builder(assistantVos).build();

        presenter.registerAssistant(request);
    }

    private void deactiveAssistant() {

        //invite
//        Invitee invite = new Invitee("52987", InviteType.Constants.TO_BE_USER_CONTACT_ID);
        Invitee invite = new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVos.add(assistantVo);


        DeActiveAssistantRequest request = new DeActiveAssistantRequest.Builder(assistantVos).build();
        presenter.deActiveAssistant(request);
    }

    private void getAssistants() {
        GetAssistantRequest request = new GetAssistantRequest.Builder().typeCode("default").setOffset(0).setCount(2).build();

        presenter.getAssistants(request);

    }

    private void getAssistantHistory() {
        GetAssistantHistoryRequest request = new GetAssistantHistoryRequest.Builder().build();
        presenter.getAssistantHistory(request);
    }

    private void changePublicThreadToPrivate() {
        ChangeThreadTypeRequest request = new ChangeThreadTypeRequest.Builder(8093, 1).build();
        presenter.changeThreadType(request);

    }

    private void getMutualGroup() {
        Invitee invite = new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID);
        GetMutualGroupRequest request = new GetMutualGroupRequest.Builder(invite).build();
        presenter.getMutualGroups(request);

    }

    private void changePrivateThreadToPublic() {
        String UniqName = "PrivatethreadConverted_" + System.currentTimeMillis() / 1000;
        ChangeThreadTypeRequest request = new ChangeThreadTypeRequest.Builder(8093, 2).setUniqname(UniqName).build();
        presenter.changeThreadType(request);
    }

    private void getNotSeenDur() {
        ArrayList<Integer> testArray = new ArrayList<>();
        testArray.add(2);
        testArray.add(1);
//       testArray.add(123);
        getNotSeenDuration(testArray);
    }

    private void removeContact() {
        presenter.removeContact(TEST_THREAD_ID);
    }

    private void addContact() {

//        {"id":344016830,"senderName":"chat-server","senderId":0,"type":4,"content":"{\"type\":23,\"messageType\":0,\"subjectId\":0,\"uniqueId\":\"fe08a72b-09eb-45b2-b6b2-03cd4dab4855\",\"content\":\"{\\\"id\\\":15424,\\\"coreUserId\\\":31257,\\\"sendEnable\\\":true,\\\"receiveEnable\\\":true,\\\"name\\\":\\\"رضا شریفی\\\",\\\"cellphoneNumber\\\":\\\"09365090061\\\",\\\"email\\\":\\\"rezasharify1993.rsmi@gmail.com\\\",\\\"username\\\":\\\"user-15778858615492\\\",\\\"contactSynced\\\":true}\",\"time\":1606317640273}"}
        // add contact
//       presenter.addContact("",
//      "",
//      "",
//      faker.name().username() + "@gmail.com",
//      "");
//
//       presenter.addContact("",
//      "",
//      "",
//      "",
//      "");
// 16844 zabix1


        List<String> usernames = new ArrayList<>();
//        usernames.add("z.mohammadi");
//        usernames.add("p.khoshghadam");
//        usernames.add("m.hasanpour");
//        usernames.add("z.ershad");
//        usernames.add("Samira.amiri");
//        usernames.add("s.heydarizadeh");
//        usernames.add("p.pahlavani");
        usernames.add("ma.amjadi");


//       for (String user :
//      usernames) {
//
//
//  try {
//      RequestAddContact request = new RequestAddContact.Builder()
//     .firstName(user + " n ")
//     .lastName(user + " i ")
//     .username(user)
//     .build();
//
//      presenter.addContact(request);
//
//      Thread.sleep(7000);
//  } catch (InterruptedException e) {
//      e.printStackTrace();
//  }
//
//
//       }
        RequestAddContact request = new RequestAddContact.Builder()
                .firstName("Leila")
                .lastName("Nemati")
                .cellphoneNumber("09126975376") //80617 //80618
                // .cellphoneNumber()
                // .email()
                .build();

        presenter.addContact(request);
    }

    private void editMessage() {
        Inviter inviter = new Inviter();
        inviter.setName("farhad");
//       String meta = "\"{\\\"actionType\\\":1,\\\"data\\\":\\\"{\\\"amount\\\":1500,\\\"description\\\":\\\"\\\",\\\"id\\\":161,\\\"state\\\":2,\\\"destUserId\\\":1900896}\\\"}\"";

        JsonObject a = new JsonObject();
//
        a.addProperty("actionType", 7);
        a.addProperty("amount", 12);
        a.addProperty("id", 161);
        a.addProperty("state", 2);
        a.addProperty("destUserId", 1900897);

//       a.addProperty("name", "farhad");
//       a.addProperty("family", "kheirkhah");
//       a.addProperty("phoneNumber", "989157770684");


        String meta = gson.toJson(a);


        presenter.editMessage(TEST_THREAD_ID, // message id
                "1111", // message new content
                meta, null);
    }

    private void unMuteThread() {
        presenter.unMuteThread(TEST_THREAD_ID, new ChatHandler() {
            @Override
            public void onUnMuteThread(String uniqueId) {
                super.onUnMuteThread(uniqueId);
            }
        });
    }

    private void muteThread() {
        presenter.muteThread(TEST_THREAD_ID, new ChatHandler() {
            @Override
            public void onMuteThread(String uniqueId) {
                super.onMuteThread(uniqueId);
            }
        });
    }

    private void getUserInfo() {
        //"get user info",
        presenter.getUserInfo(new ChatHandler() {
            @Override
            public void onGetUserInfo(String uniqueId) {
                super.onGetUserInfo(uniqueId);
            }
        });
    }

    private void renameTHread() {
        presenter.renameThread(TEST_THREAD_ID, "*** Rename thread to " + new Date().toString() + " ***", new ChatHandler() {
            @Override
            public void onRenameThread(String uniqueId) {
                super.onRenameThread(uniqueId);
            }
        });
    }

    private void getThreadParticipants() {

        RequestThreadParticipant request =
                new RequestThreadParticipant.Builder()
                        .count(20)
                        .offset(0)
                        .threadId(TEST_THREAD_ID)
//      .withNoCache()
                        .build();


        presenter.getThreadParticipant(request);


//        presenter.getThreadParticipant(20, null, TEST_THREAD_ID, new ChatHandler() {
//   @Override
//   public void onGetThreadParticipant(String uniqueId) {
//       super.onGetThreadParticipant(uniqueId);
//   }
//        });
    }
//    public static final String THREAD_UNIQUE_NAME = "unique_name_4_" + new Date().getTime();

    private void joinPublicThread() {

        RequestJoinPublicThread request = new RequestJoinPublicThread
                .Builder(THREAD_UNIQUE_NAME)
                .build();


        presenter.joinPublicThread(request);


    }


    private void createPublicThread() {


        Invitee[] invite = new Invitee[]{
                new Invitee("5739", InviteType.Constants.TO_BE_USER_CONTACT_ID),
                new Invitee("5740", InviteType.Constants.TO_BE_USER_CONTACT_ID),
        };

        String metac = getMetaData();

        RequestCreatePublicThread request =
                new RequestCreatePublicThread.Builder(
                        ThreadType.Constants.PUBLIC_GROUP,
                        Arrays.asList(invite),
                        THREAD_UNIQUE_NAME)
                        .withDescription("desc at " + new Date())
                        .title("My Public Group 22")
                        .withImage("http://google.com")
                        .withMetadata(metac)
                        .build();

        presenter.createPublicThread(request);


    }

    private void checkIsNameAvailable() {


        RequestCheckIsNameAvailable request =
                new RequestCheckIsNameAvailable.Builder(THREAD_UNIQUE_NAME)
                        .build();

        presenter.checkIsNameAvailable(request);


    }


    private void stopTyping(String uniqueId) {

        Log.d("MTAG", "removing signal wid: " + uniqueId);

        boolean stopped = presenter.stopTyping(uniqueId);

        //if signal not stopped or unique id lost
        if (!stopped) {

            presenter.stopAllSignalMessages();
        }

    }

    private void startTyping() {


        String uniqueId = presenter.startTyping(TEST_THREAD_ID);

        runningSignals.add(uniqueId);


    }

    private void getNotSeenDuration(ArrayList<Integer> userIds) {


        presenter.getNotSeenDuration(userIds);


    }

    private void createThread() {
        /**
         * int TO_BE_USER_SSO_ID = 1;
         * int TO_BE_USER_CONTACT_ID = 2;
         * int TO_BE_USER_CELLPHONE_NUMBER = 3;
         * int TO_BE_USER_USERNAME = 4;
         * int TO_BE_USER_ID = 5; // just for p2p
         */
        /**"create thread"
         * This is Invitee object
         * ---->private int id;
         * ---->private int idType;
         *
         * createThreadTypes = {
         *NORMAL: 0,
         *OWNER_GROUP: 1,
         *PUBLIC_GROUP: 2,
         *CHANNEL_GROUP: 4,
         *CHANNEL: 8
         *       }
         */

//        Invitee[] invite = new Invitee[]{
//       new Invitee(2951, 2)
//      , new Invitee(1967, 2)
//      ,new Invitee(123, 5)
//      , new Invitee(824, 2)
//        };


        /**
         *
         *
         * CHANNEL_GROUP: 4,
         *
         *
         */

//        Invitee[] invite = new Invitee[]{
////       new Invitee(3361, ///       , new Invitee(3102, 2)
////        new Invitee(091, 1),
////       new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
//       new Invitee("29782", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////       new Invitee("27774", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////       new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////       new Invitee(5638, 2),
////       new Invitee("z.mohammadi", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("p.khoshghadam", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("m.hasanpour", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("z.ershad", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("samira.amiri", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("s.heydarizadeh", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("p.pahlavani", InviteType.Constants.TO_BE_USER_USERNAME),
////      new Invitee("ma.amjadi", InviteType.Constants.TO_BE_USER_USERNAME),
////       new Invitee(5638, 2),
//        };
        Inviter inviterw = new Inviter();
        inviterw.setName("this is sample metadata");
        String metac = gson.toJson(inviterw);


        List<Invitee> invite = new ArrayList<>();
        // add by user SSO_ID
//     invite.add(new Invitee(122, 1));  //user jiji
//        invite.add(new Invitee("121", 1)); // user zizi
//        invite.add(new Invitee("63270", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63271", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//      integration users
//        invite.add(new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63255", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//sand box users
//        52620 farhad
//        52979 masoud
//        52987 khodam
        //  invite.add(new Invitee("52620", InviteType.Constants.TO_BE_USER_CONTACT_ID));

        invite.add(new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        invite.add(new Invitee("63255", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        invite.add(new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID));

        //   invite.add(new Invitee("52987", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        //   invite.add(new Invitee("1", InviteType.Constants.TO_BE_USER_ID)); //amjadi
//        invite.add(new Invitee("80618", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//     invite.add(new Invitee(9981084527L, 3)); zizi cellphone
//     invite.add(new Invitee(123, 5)); //user fifi
//     invite.add(new Invitee(121, 5)); // user zizi

//     invite.add(new Invitee(122, 1));  //user jiji


        RequestUploadImage requestUploadImage =
                new RequestUploadImage.Builder(ChatActivity.this, getUri())
                        .sethC(140)
                        .setwC(140)
                        .build();


        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.Constants.NORMAL, invite)
                .title("thread for test upload 1" + (new Date().getTime() / 1000))
//       .withDescription("Description created at "
//      + new Date().getTime())
//       .withImage("URL")
                //      .withMetadata(metac)
//       .typeCode("podspace")
//       .setUploadThreadImageRequest(requestUploadImage)
                .build();

        presenter.createThread(requestCreateThread);


    }

    private void updateContact() {

        presenter.updateContact(52979, "agha masoud",
                "amjadi ", "---", "gizi@gmail.com"
        );
    }


    public void getThreadHistory() {


//        RequestGetHistory request = new RequestGetHistory
//       .Builder(TEST_THREAD_ID)
//       .offset(0)
//       .count(50)
//       .order("desc") //.order("asc")
////       .fromTime(new Date().getTime())
//       //   .toTime(new Date().getTime())
////       .setMessageType(TextMessageType.Constants.POD_SPACE_PICTURE)
////       .withNoCache()
//       .build();
//
//        presenter.getHistory(request, null);


        if (TEST_THREAD_LAST_SEEN_MESSAGE_TIME > 0) {
            showToast("Get History to time " + TEST_THREAD_LAST_SEEN_MESSAGE_TIME);
            RequestGetHistory request = new RequestGetHistory
                    .Builder(TEST_THREAD_ID)
                    .offset(0)
                    .count(50)
                    .order("desc") //.order("asc")
                    .toTime(TEST_THREAD_LAST_SEEN_MESSAGE_TIME)
                    .build();

            presenter.getHistory(request, null);

            showToast("Get History from time " + TEST_THREAD_LAST_SEEN_MESSAGE_TIME);

            request = new RequestGetHistory
                    .Builder(TEST_THREAD_ID)
                    .offset(0)
                    .count(50)
                    .order("asc") //.order("asc")
                    .fromTime(TEST_THREAD_LAST_SEEN_MESSAGE_TIME)
                    .build();

            presenter.getHistory(request, null);

        }

    }

//    public void getHashTagList() {
//        RequestGetHashTagList request = new RequestGetHashTagList
//       .Builder(TEST_THREAD_ID)
//       .offset(0)
//       .count(25)
//       .setHashtag("ahmad")
//       .build();
//
//        presenter.getHashTagLIst(request, null);
//    }

    public void getHashTagHistory() {
        RequestGetHashTagList request = new RequestGetHashTagList
                .Builder(TEST_THREAD_ID)
                .setHashtag("test2")
                .offset(0)
                .count(25)
                .build();
        presenter.getHashTagList(request, null);
    }

    public void blockAssistant() {
        Invitee invite = new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID);
        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVos.add(assistantVo);

        BlockUnblockAssistantRequest request = new BlockUnblockAssistantRequest.Builder(assistantVos, false).build();
        presenter.blockAssistant(request);
    }

    public void unBlockAssistant() {
        Invitee invite = new Invitee("63256", InviteType.Constants.TO_BE_USER_CONTACT_ID);
        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVos.add(assistantVo);

        BlockUnblockAssistantRequest request = new BlockUnblockAssistantRequest.Builder(assistantVos, true).build();
        presenter.unBlockAssistant(request);
    }

    public void getBlocksAssistant() {
        GetBlockedAssistantsRequest request = new GetBlockedAssistantsRequest.Builder().build();
        presenter.getBlocksAssistant(request);
    }

    public void getThreads() {

        ArrayList<Integer> threadIds = new ArrayList<>();
        threadIds.add(TEST_THREAD_ID);
        threadIds.add(1573);
        threadIds.add(351);


        RequestThread requestThread = new RequestThread
                .Builder()
//  .threadName("Te")
//  .newMessages()
//       .partnerCoreContactId(566)
                .offset(0)
                .count(50)
//  .threadIds(threadIds)
//       .withNoCache()
//       .typeCode("default")
                .build();

        presenter.getThreads(requestThread, null);

    }

    public void replyMessage() {
        RequestReplyMessage message = new RequestReplyMessage
                .Builder("this is reply from john", TEST_THREAD_ID, 94305,
                TextMessageType.Constants.TEXT)
                .build();
        presenter.replyMessage(message, null);


//        presenter.replyMessage("this is reply from john", 381, 14103, new ChatHandler() {
//   @Override
//   public void onReplyMessage(String uniqueId) {
//       super.onReplyMessage(uniqueId);
//   }
//        });
    }

    public void forwardMessage() {

        ArrayList<Long> messageIds = new ArrayList<>();
        messageIds.add(73328L);
        messageIds.add(73327L);
        messageIds.add(73326L);
        long threadId = TEST_THREAD_ID;
//        presenter.forwardMessage(threadId, messageIds);

        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(threadId, messageIds)
                .build();


        presenter.forwardMessage(forwardMessage);


    }

    private void getContacts() {


        new Thread(() -> {


            RequestGetContact request = new RequestGetContact.Builder()
                    .count(50)
                    .offset(0)
                    .setUserName("mahyar.zhiani")
//       .withNoCache()
                    .build();

//        presenter.getContact(0, 0L, null);

            presenter.getContact(request);

        }).start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(() -> {

            RequestGetContact request2 = new RequestGetContact.Builder()
                    .count(10)
                    .offset(0)
//       .withNoCache()
                    .build();

//        presenter.getContact(0, 0L, null);

            presenter.getContact(request2);

//        offset = offset + 50;


        }).start();
    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        Toast.makeText(ChatActivity.this, "\\__('?')__/", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onClick(View v) {
//        if (v == buttonFileChoose) {
//            showPicChooser();
//        }

        if (v == btConnect) {
            connect();
        }
    }

    private void connect() {
        if (serverType == ServerType.Main) {
            SetMainServer();
        } else if (serverType == ServerType.Sandbox) {
            SetSandBoxServer();
        } else if (serverType == ServerType.Integration) {
            SetIntgrationServer();
        } else if (serverType == ServerType.KafkaTest) {
            SetKafkaTestServer();
        } else SetMainServer();

        RequestConnect rc = new RequestConnect.Builder(
                socketAddress,
                APP_ID,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer,
                podSpaceServer)
                .build();
        presenter.connect(rc);

        tvServerType.setText(serverType.name());
    }

    private void SetMainServer() {
        ssoHost = BaseApplication.getInstance().getString(R.string.sso_host);
        serverName = "chat-server";

        socketAddress = BaseApplication.getInstance().getString(R.string.socketAddress);
        platformHost = BaseApplication.getInstance().getString(R.string.platformHost);
        fileServer = BaseApplication.getInstance().getString(R.string.fileServer);
    }

    private void SetSandBoxServer() {
        ssoHost = BaseApplication.getInstance().getString(R.string.sandbox_ssoHost);
        serverName = BaseApplication.getInstance().getString(R.string.sandbox_server_name);

        socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
        platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
        fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);
        podSpaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_integration);

    }

    private void SetKafkaTestServer() {
        ssoHost = BaseApplication.getInstance().getString(R.string.kafkaTestStream_ssoHost);
        serverName = BaseApplication.getInstance().getString(R.string.kafkaTestStream_server_name);

        socketAddress = BaseApplication.getInstance().getString(R.string.kafkaTestStream_socketAddress);
        platformHost = BaseApplication.getInstance().getString(R.string.kafkaTestStream_platformHost);
        fileServer = BaseApplication.getInstance().getString(R.string.kafkaTestStream_fileServer);

    }

    private void SetIntgrationServer() {

        ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
        serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);

        socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);
        platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
        fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);
        podSpaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_integration);

    }

    private void showPicChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (requestCode == PICK_IMAGE_FILE_REQUEST) {
                    Uri selectedFileUri = data.getData();
                    String path = selectedFileUri.toString();
                    setUri(Uri.parse(path));
                    imImage.setImageURI(Uri.parse(path));
                } else if (requestCode == FILE_REQUEST_CODE) {
                    Uri fileUri = data.getData();
                    String path = FilePick.getSmartFilePath(this, fileUri);
                    setFileUri(path);
                    setUri(fileUri);
                }
            }
        }


    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void ChooseFile(View view) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void getThreadWithCoreUser() {
        RequestThread requestThread = new RequestThread.Builder()
                .partnerCoreContactId(566)
//       .threadIds()
//       .threadName()
//       .partnerCoreContactId()
//       .creatorCoreUserId()
//       .partnerCoreUserId()
//       .count()
//       .offset()
                .build();
        presenter.getThreads(requestThread, null);
    }

    public String getSignalUniq() {
        return signalUniq;
    }

    public void setSignalUniq(String signalUniq) {
        this.signalUniq = signalUniq;
    }

    public void uploadImageProgress() {


        presenter.uploadImageProgress(this, ChatActivity.this, getUri(), new ProgressHandler.onProgress() {
            @Override
            public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                runOnUiThread(() -> tvPercent.setText(progress + "%"));

                Log.e("UPLOAD_IMAGE", "op " + progress + " sent " + totalBytesSent + " toSend " + totalBytesToSend);

            }

            @Override
            public void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {

                Log.e("UPLAOD", imageJson);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Finish Upload", Toast.LENGTH_SHORT).show();
                    tvPercent.setTextColor(getResources().getColor(R.color.colorAccent));
                    tvPercent.setText("100");
                    fileHash = chatResponse.getResult().getHashCode();
                });

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        presenter.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        Log.e("CHAT_SDK", "Stopping ping...");
//        presenter.closeChat();
    }

    private void replyFileMessage() {

        String messageContent = "Hello! just be happy!! : ) ";
        long threadId = TEST_THREAD_ID;
        long messageId = 114334;
        Uri fileUri = getUri();
        Inviter inviter = new Inviter();
        inviter.setName("Sample Name for meta data test");
        String meta = gson.toJson(inviter);

        RequestReplyFileMessage fileMessage = new RequestReplyFileMessage
                .Builder(messageContent, threadId, messageId, fileUri, this,
                TextMessageType.Constants.POD_SPACE_PICTURE) // required
                .systemMetaData(meta)
                .setUserGroupHashCode(TEST_THREAD_HASH) //required
                .setImageHc("200")
                .setImageWc("100")
                .setImageXc("5")
                .setImageYc("5")
                .build();


        presenter.replyFileMessage(fileMessage, new ProgressHandler.sendFileMessage() {
            @Override
            public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
                Log.e("CHAT_SDK_UPLOAD", "Progress %" + progress);
            }
        });
    }

    public void uploadFileProgress() {

        if (getUri() != null) {
            presenter.uploadFileProgress(ChatActivity.this, this, getUri(), new ProgressHandler.onProgressFile() {
                @Override
                public void onProgressUpdate(int progress) {

                    Log.e("UFP", "opu: " + progress);
                }

                @Override
                public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                    Log.e("UFP", "op" + progress + " sent" + totalBytesSent + " toSend" + totalBytesToSend);

                    runOnUiThread(() -> {
                        tvPercent.setText(progress + "%");
                    });


                }

                @Override
                public void onError(String jsonError, ErrorOutPut error) {
                    Log.e("UFP", "Error: " + error.getErrorMessage() + " Code: " + error.getErrorCode());

                }

                @Override
                public void onFinish(String imageJson, FileUpload fileImageUpload) {
                    Log.e("UFP", imageJson);

                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Finish Upload", Toast.LENGTH_SHORT).show();
                        tvPercent.setText("100");
                        tvPercent.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    });

                }
            });
        } else {
            showUriEmptyError();
        }
    }

    public void setThreadId(View view) {


        String tId = et_text.getText().toString();

        if (!tId.isEmpty())
            TEST_THREAD_ID = Integer.valueOf(tId);

    }

    public void setToken(View view) {
        String tId = et_text.getText().toString();
        if (!tId.isEmpty())
            TOKEN = tId;
    }

    @Override
    public void onEditMessage() {

        runOnUiThread(() -> Toast.makeText(this, "Message Edited!", Toast.LENGTH_LONG
        ).show());


    }


    @Override
    public void onDeleteMessage() {

        runOnUiThread(() -> Toast.makeText(this, "Message Deleted!", Toast.LENGTH_LONG)
                .show());

    }

    @Override
    public void onPinMessage(ChatResponse<ResultPinMessage> response) {

        runOnUiThread(() -> Toast.makeText(this, "Message Pinned! ==> " + response.getResult().getText(), Toast.LENGTH_LONG)
                .show());
    }

    @Override
    public void onUnPinMessage(ChatResponse<ResultPinMessage> response) {
        runOnUiThread(() -> Toast.makeText(this, "Message UnPinned! ==> " + response.getResult().getText(), Toast.LENGTH_LONG)
                .show());

    }

    @Override
    public void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response) {

        runOnUiThread(() -> Toast.makeText(this, "User roles " + response.getResult().getRoles(), Toast.LENGTH_LONG)
                .show());

        Log.d("ROLES", response.getJson());

    }


    @Override
    public void onTypingSignalTimeout(long threadId) {

        showToast("Typing in Thread with id " + threadId + " has been stopped");

        Log.e("IS_TYPING", "Typing in Thread with id " + threadId + " has been stopped");
    }

    private void showToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_LONG)
                .show());
    }


    @Override
    public void onUniqueNameIsAvailable(ChatResponse<ResultIsNameAvailable> response) {

        showToast("The name [ " + response.getResult().getUniqueName() + " ] is available!");

    }

    @Override
    public void onJoinPublicThread(ChatResponse<ResultJoinPublicThread> response) {

        showToast("Joining thread with unique name " + response.getResult().getThread().getUniqueName() + " was successful");

    }

    @Override
    public void onGetUnreadsMessagesCount(ChatResponse<ResultUnreadMessagesCount> response) {

        showToast("There is " + response.getResult().getUnreadsCount() + " Unread message. Cache: " + response.isCache());
    }

    @Override
    public void onGetToken(String token) {


        RequestConnect rc = new RequestConnect.Builder(
                socketAddress,
                APP_ID,
                serverName,
                token,
                ssoHost,
                platformHost,
                fileServer,
                podSpaceServer)
                .build();

        presenter.connect(rc);

    }

    @Override
    public void onBotCreated(ChatResponse<CreateBotResult> response) {
        showToast("Bot created: " + response.getResult().getThingVO().getName());
    }


    @Override
    public void onBotCommandsDefined(ChatResponse<DefineBotCommandResult> response) {
        showToast("COMMANDS DEFINED:  " + response.getResult().getCommandList());
    }

    @Override
    public void onBotStopped(String botName) {

        showToast("BOT STOPPED:  " + botName);
    }

    @Override
    public void onBotStarted(String botName) {

        showToast("BOT STARTED:  " + botName);
    }

    @Override
    public void pingStatusSent(ChatResponse<StatusPingResult> response) {
        showToast("Ping Sent: " + response.getUniqueId());
    }

    @Override
    public void onThreadClosed(long subjectId) {
        showToast("Thread with id: " + subjectId + " has been closed!");
    }

    @Override
    public void onChatProfileUpdated(ResultUpdateProfile result) {
        showToast("Profile Updated: " + result.getBio());
    }

    @Override
    public void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {


    }
}
