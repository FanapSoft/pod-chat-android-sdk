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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.UpdateContact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.notification.PodNotificationManager;
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
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
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
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.fanap.podchat.requestobject.RequestUploadFile;
import com.fanap.podchat.requestobject.RequestUploadImage;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.FilePick;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.TextMessageType;
import com.fanap.podchat.util.ThreadType;
import com.fanap.podchat.util.Util;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener, ChatContract.view {
    private static final int FILE_REQUEST_CODE = 2;
    public static final String APP_ID = "appid";
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1007;


    private ChatContract.presenter presenter;
    private EditText editText;
    private EditText editTextToken;
    private EditText editTextThread;
    private Button buttonFileChoose;
    private String selectedFilePath;
    private Button buttonConnect;
    private Button buttonToken;
    private ImageView imageMap;
    private TextView textViewState;
    private TextView percentage;
    private TextView percentageFile;
    private Gson gson = new GsonBuilder().create();

    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;

    private Uri uri;

    private Button btnUploadFile;

    private Button btnUploadImage;


    //main and sandbox

//    private static String TOKEN = "869221a3923f49879ecd38824f7d787e";
//    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
//    private static String serverName = "chat-server";


    //local


    private static String TOKEN = BaseApplication.getInstance().getString(R.string.Farhad_Kheirkhah);
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    private static String serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);


    //test

//    private static String TOKEN = BaseApplication.getInstance().getString(R.string.token_zabbix_bot_2);
//    private static String ssoHost = BaseApplication.getInstance().getString(R.string.test_ssoHost);
//    private static String serverName = BaseApplication.getInstance().getString(R.string.test_serverName);


    private static String appId = "POD-Chat";
    private static String podSpaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_main);


    /**
     * Integration server setting:
     */
////
    private static String name = BaseApplication.getInstance().getString(R.string.integration_serverName);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);

    /**
     * Nemati
     //     */
//    private static String name = BaseApplication.getInstance().getString(R.string.nemati_serverName);
//    private static String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);


    /**
     * Test server setting:
     */

//    private static String name = BaseApplication.getInstance().getString(R.string.test_server_name);
//    private static String socketAddress = BaseApplication.getInstance().getString(R.string.test_socketAddress);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.test_platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.test_fileServer);


    /**
     * Main Server Setting:
     */
//
//    private static String name = BaseApplication.getInstance().getString(R.string.main_server_name);
//    private static String socketAddress = BaseApplication.getInstance().getString(R.string.socketAddress);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.fileServer);

    /**
     * Sandbox setting:
     */
//
//    private static String name = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
//    private static String socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);


    //sand box / group
////
//    public static int TEST_THREAD_ID = 35311; //amjadi, //sharifi //kheirkhah
//    private static final String TEST_THREAD_HASH = "2JS6BC7L4MGCYT";


//    main server / p2p

//    public static int TEST_THREAD_ID = 8182;
//    private static final String TEST_THREAD_HASH = "7691JPIS2VG4XM";

    // main server / group

//    public static int TEST_THREAD_ID = 47528;
//    private static final String TEST_THREAD_HASH = "4S5U1G4EH82BVB";


//    integration /group: fifi,jiji and ...
//    public static int TEST_THREAD_ID = 6886;

    //integration /p2p: fifi, jiji

    public static int TEST_THREAD_ID = 7488;
    private static final String TEST_THREAD_HASH = "7691JPIS2VG4XM";


    //test server thread
//    public static int TEST_THREAD_ID = 7608;


    private String fileUri;

    private String signalUniq;

    ArrayList<String> runningSignals = new ArrayList<>();


    Faker faker;
    private String downloadingId = "";

    long notificationThreadId = 0;
    long notificationMessageId = 0;

    int offset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();


        setListeners();


        getNotificationData();


    }

    private void setListeners() {

        imageMap.setOnLongClickListener((v) -> {

            showNotification();

            return true;
        });
        imageMap.setOnClickListener(v -> downloadWithGlide());
        buttonFileChoose.setOnClickListener(this);
        buttonFileChoose.setOnLongClickListener(v -> {

            presenter.shareLogs();
            return true;
        });
        buttonConnect.setOnClickListener(this);
        btnUploadFile.setOnClickListener(this::onUploadFile);

        btnUploadFile.setOnLongClickListener(v -> {
            offset = 0;
            return true;
        });

        btnUploadImage.setOnClickListener(this::onUploadImage);

        buttonToken.setOnClickListener(v -> {


            TOKEN = editTextToken.getText().toString();

            presenter.setToken(TOKEN);

        });

        buttonToken.setOnLongClickListener(v -> {

            String entry = editTextToken.getText().toString();
            editTextToken.setText("");
            editTextToken.setHint("Enter OTP or Number");
            presenter.enableAutoRefresh(this, entry);


            return true;

        });
    }

    private void init() {

        faker = new Faker();
        Logger.addLogAdapter(new AndroidLogAdapter());
        setContentView(R.layout.activity_chat);
        imageMap = findViewById(R.id.imageMap);
        textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        percentage = findViewById(R.id.percentage);
        percentageFile = findViewById(R.id.percentageFile);
        editText = findViewById(R.id.editTextMessage);
        editTextToken = findViewById(R.id.editTextToken);
        editTextThread = findViewById(R.id.editTextThread);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        buttonFileChoose = findViewById(R.id.buttonFileChoose);
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonToken = findViewById(R.id.buttonToken);
        btnUploadFile = findViewById(R.id.buttonUploadFileProgress);
        btnUploadImage = findViewById(R.id.buttonUploadImageProgress);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);
        Spinner spinnerThird = findViewById(R.id.spinnerThird);
        setupFirstSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);


        presenter = new ChatPresenter(this, this, this);

        presenter.clearNotifications();

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

    //for log events

    @Override
    public void onLogEvent(String log) {
        Logger.json(log);
    }

    @Override
    public void onState(String state) {
        runOnUiThread(() -> textViewState.setText(state));
    }

    private void setupThirdSpinner(Spinner spinnerThird) {


        ArrayAdapter<String> adapterSpinner =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        ConstantSample.funcThird);

        adapterSpinner
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThird
                .setAdapter(adapterSpinner);
        spinnerThird
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                searchMap();
                                break;
                            case 2:
                                routeMap();
                                break;
                            case 3:
                                blockContact();
                                break;
                            case 4:
                                unblockContact();
                                break;
                            case 5: {
                                getBlockedList();
                                break;
                            }
                            case 6: {
                                updateThreadInfo();
                                break;
                            }

                            case 7: {
                                seenMessageList();
                                break;

                            }
                            case 8:
                                getDeliverMessageList();
                                break;
                            case 9: {
                                createThreadWithMessage();
                                break;
                            }
                            case 10:
                                getThreadWithCoreUser();
                                break;
                            case 11:
                                mapStatic();
                                break;
                            case 12:
                                mapReverse();
                                break;
                            case 13:
                                sendLocationMsg();
                                break;
                            case 14:
                                setAdminRules();
                                break;
                            case 15:
                                startTyping();
                                break;
                            case 16: {
                                stopTyping();
                                break;
                            }
                            case 17: {
                                removeAdminRules();
                                break;
                            }
                            case 18: {
                                addAuditor();
                                break;
                            }
                            case 19: {
                                removeAuditor();
                                break;
                            }
                            case 20: {
                                createThreadWithFile();
                                break;
                            }
                            case 21: {
                                getUserRoles();
                                break;
                            }
                            case 22: {
                                downloadFile();
                                break;
                            }
                            case 23: {
                                cancelDownloadImage();
                                break;
                            }

                            case 24: {
                                presenter.getCacheSize();
                                break;
                            }

                            case 25: {

                                clearCache();

                                break;
                            }

                            case 26: {

                                getStorageSize();

                                break;
                            }

                            case 27: {
                                clearStorage();
                                break;
                            }


                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(ChatActivity.this, "\\__('?')__/", Toast.LENGTH_SHORT).show();
                    }
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
//                                        .withNoCache()
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
//                new RequestUploadImage.Builder(ChatActivity.this, getUri())
//                        .sethC(140)
//                        .setwC(140)
//                        .setUserGroupHashCode(TEST_THREAD_HASH)
//                        .build();


        RequestThreadInfo request =
                new RequestThreadInfo.Builder(TEST_THREAD_ID)
                                                .name("Chat sample thread") // required. if not set, thread name will set to null
//                                                .metadata("{}") // required. if not set, thread metadata will set to null
                                                .image("https://podspace.pod.ir/nzh/drive/downloadImage?hash=ELJIHZN9NP37ZIDA") // required. if not set, thread image will set to null
                        .description("this is test description updated on " + new Date().toString()) // required. if not set, thread name will set to null
//                                                .setUploadThreadImageRequest(requestUploadImage) // set when you wanna upload thread image
//                                                .setUserGroupHash(TEST_THREAD_HASH) // set when you wanna upload thread image
                        .build();

        presenter.updateThreadInfo(request);
    }

    private void unblockContact() {
        Long ubThreadId = null;
//                                Long ubUserId = 121L;
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
//                                .forwardedMessageIds(listForwardIds)
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

            Glide.with(this)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(glideUrl)
                    .into(imageMap);
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
                .Builder("613Q7WCCEXZ1DGY5")
//                .setCrop(true)
                .setQuality(0.45f)
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
                        runOnUiThread(() -> imageMap.setImageBitmap(v));
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
//            @Override
//            public void onProgressUpdate(String uniqueId, int bytesDownloaded, int totalBytesToDownload) {
//                Log.e("DOWNLOAD", "IN ACTIVITY: " + "Downloaded: " + bytesDownloaded + " Left: " + totalBytesToDownload);
//
//            }
//
//            @Override
//            public void onProgressUpdate(String uniqueId, int progress) {
//
//            }
//
//            @Override
//            public void onError(String uniqueId, String error, String url) {
//                Log.e("DOWNLOAD", "IN ACTIVITY: ERROR :(((");
//
//            }
//
//            @Override
//            public void onLowFreeSpace(String uniqueId, String url) {
//                Log.e("DOWNLOAD", "Low Space...");
//
//            }
//
//            @Override
//            public void onFileReady(ChatResponse<ResultDownloadFile> response) {
//                Log.e("DOWNLOAD", "IN ACTIVITY: Finish File!!!!");
//                Log.e("DOWNLOAD", "File name: " + response.getResult().getFile().getName());
//                Log.e("DOWNLOAD", "Uri " + response.getResult().getUri());
//                Log.e("DOWNLOAD", "File Exist " + response.getResult().getFile().exists());
//
//
//                if (response.getResult().getFile().exists()) {
//
//
//                    try {
//                        Bitmap v = BitmapFactory.decodeFile(response.getResult().getFile().getAbsolutePath());
//                        runOnUiThread(() -> imageMap.setImageBitmap(v));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//            }
//
//        });


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
//                .Builder(TextMessageType.Constants.PICTURE)
//                //                .message("Create thread for File Message Test " + new Date().toString())
////                                .forwardedMessageIds(listForwardIds)
//                .build();


        RequestCreateThreadWithFile request = new RequestCreateThreadWithFile
                .Builder(ThreadType.Constants.OWNER_GROUP,
                invite,
                requestUploadFile,
                TextMessageType.Constants.POD_SPACE_FILE)
                .title("Test File PodSpace")
                .setUploadThreadImageRequest(requestUploadThreadImageImage)
//                .message(innerMessage)
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


//    private void stopSignalMessage() {
//        presenter.stopSignalMessage(getSignalUniq());
//    }
//
//    private void startSignalMessage() {
//        RequestSignalMsg requestSignalMsg = new RequestSignalMsg.Builder()
//                .signalType(ChatMessageType.SignalMsg.IS_TYPING)
//                .threadId(1961)
//                .build();
//        String uniq = presenter.startSignalMessage(requestSignalMsg);
//        setSignalUniq(uniq);
//    }

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
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                String center = "35.7003510,51.3376472";
//
//                RequestLocationMessage requestLocationMessage = new RequestLocationMessage
//                        .Builder()
//                        .center(center)
//                        .message("This is location ")
//                        .activity(ChatActivity.this)
//                        .threadId(TEST_THREAD_ID)
//                        .build();
//
//                presenter.sendLocationMessage(requestLocationMessage);
//
//
//            }
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

    //Function second
    private void setupSecondSpinner(Spinner spinnerSecond) {

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ConstantSample.funcSecond);
        final String[] fileUnique = new String[1];
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSecond.setAdapter(adapterSpinner);
        spinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//            "Choose function",
                        break;
                    case 1:

                        presenter.syncContact(ChatActivity.this);
                        break;
                    case 2:

                        sendFileMessage(fileUnique);

                        break;
                    case 3:
                        uploadImage();
                    case 4:
//                        presenter.uploadFile(ChatSandBoxActivity.this, getUri());
                        break;
                    case 5:
                        removeParticipants();

                        break;
                    case 6:
                        addParticipants();

                        break;
                    case 7:
                        leaveThread();

                        break;
                    case 8:
                        deleteMessage();

                        break;
                    case 9:
                        searchContact();
                        break;
                    case 10:
                        searchHistory();

                        break;
                    case 11:
                        break;
                    case 12:
                        cancelUpload(fileUnique[0]);
                        break;
                    case 13:
                        retryUpload();

                        break;
                    case 14: {
                        clearHistory();
                    }
                    break;
                    case 15: {
                        //2139
                        getAdminList();
                        break;
                    }
                    case 16: {
                        spamThread();
                        break;
                    }
                    case 17: {
                        seenMessage();
                        break;
                    }
                    case 18: {
                        updateUserProfile();
                        break;
                    }
                    case 19: {
                        createBot();
                        break;
                    }
                    case 20: {
                        defineBotCommand();
                        break;
                    }
                    case 21: {
                        startBot();
                        break;
                    }
                    case 22: {
                        stopBot();
                        break;
                    }
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ChatActivity.this, "\\__('?')__/", Toast.LENGTH_SHORT).show();
            }
        });
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
//                                .id("2247")
//                                .cellphoneNumber("0938")
//                                .lastName("kHeI")
//                                .firstName("fAr")
                .query("pO")
//                                .order("desc")
//                                .email("masoudmanson@gmail.com")
                .build();

        presenter.searchContact(requestSearchContact);
    }

    private void uploadImage() {
        presenter.uploadImage(ChatActivity.this, getUri());
    }

    private void sendFileMessage(String[] fileUnique) {
        RequestFileMessage request = new RequestFileMessage.Builder(
                ChatActivity.this,
                TEST_THREAD_ID,
                getUri(),
                TextMessageType.Constants.POD_SPACE_PICTURE) // constructor
                .description("test file message")
//                                .systemMetadata(getMetaData())
                .setUserGroupHash(TEST_THREAD_HASH)
//                                .setImageHc("100")
//                                .setImageWc("100")
//                                .setImageXc("1")
//                                .setImageYc("1")
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

                    }

                    @Override
                    public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {

                        Log.e("SFM", "onFinishFile");

                    }

                    @Override
                    public void onError(String jsonError, ErrorOutPut error) {

                        Log.e("SFM", "onError");


                    }
                });


//                        fileUnique[0] = presenter.sendFileMessage(
//                                ChatActivity.this,
//                                ChatActivity.this,
//                                "test file message",
//                                TEST_THREAD_ID,
//                                getUri(),
//                                getMetaData(),
//                                TextMessageType.Constants.PICTURE,
//                                new ProgressHandler.sendFileMessage() {
//                                    @Override
//                                    public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {
//
//                                        Log.e("SFM", "Sending files message: " + progress + " * " + totalBytesSent + " * " + totalBytesToSend);
//                                    }
//
//                                    @Override
//                                    public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
//
//                                        Log.e("SFM", "onFinishImage");
//
//                                    }
//
//                                    @Override
//                                    public void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {
//
//                                        Log.e("SFM", "onFinishFile");
//
//                                    }
//
//                                    @Override
//                                    public void onError(String jsonError, ErrorOutPut error) {
//
//                                        Log.e("SFM", "onError");
//
//
//                                    }
//                                });
    }


    private void stopBot() {

        StartAndStopBotRequest request = new StartAndStopBotRequest.Builder(TEST_THREAD_ID,
                "TEST2BOT")
                .build();

        presenter.stopBot(request);

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

        CreateBotRequest request = new CreateBotRequest.Builder("TEST2BOT")
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
//                .Builder(10654,true)
                .Builder(TEST_THREAD_ID)
//                .admin(true)
//                .count(50)
//                .threadId(TEST_THREAD_ID)
//                .withNoCache()
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
//            @Override
//            public void onDeleteMessage(String uniqueId) {
//                super.onDeleteMessage(uniqueId);
//            }
//        });


    }

    private void removeParticipants() {

        List<Long> participantIds = new ArrayList<>();
        participantIds.add(11925L);
        participantIds.add(5581L);
        participantIds.add(1261L);
        long threadId = TEST_THREAD_ID;
        RequestRemoveParticipants request = new RequestRemoveParticipants
                .Builder(threadId, participantIds)
                .build();
        presenter.removeParticipants(request, null);

    }

    private void addParticipants() {


        //add with coreUserIds

        RequestAddParticipants request = RequestAddParticipants
                .newBuilder()
                .threadId((long) TEST_THREAD_ID)
//                .withCoreUserIds(982L, 5241L)
//                .withUserNames("a.rokni",
//                        "ms.alavizadeh",
//                        "bhamidpour",
//                        "z.morshedi",
//                        "m.rashed")
//                .withContactIds(10001L,1000L)
//                .withContactId(1000L)
                .withUsername("TEST2BOT")
                .build();


        presenter.addParticipants(request, null);


    }

    private void setupFirstSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ConstantSample.func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void sendMessage(View view) {

//        Inviter inviter = new Inviter();
//        inviter.setName("farhad");
//        String meta = gson.toJson(inviter);

        String meta = getMetaData();

        RequestMessage request = new RequestMessage.Builder("Salam", 1000)
                .messageType(TextMessageType.Constants.TEXT)
                .build();


        presenter.sendTextMessage(editText.getText().toString(), TEST_THREAD_ID, TextMessageType.Constants.TEXT, meta, null);

        editText.setText("");

//        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
//                , 1576, 2, meta, null);
//
//                new ChatHandler() {
//                    @Override
//                    public void onSent(String uniqueId, long threadId) {
//                        super.onSent(uniqueId, threadId);
//                        Toast.makeText(ChatActivity.this, "its worked", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSentResult(String content) {
//                        super.onSentResult(content);
//                        if (content != null) {
//                            Toast.makeText(ChatActivity.this, "no null", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });

//        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
//                , 312, meta, new Chat.SendTextMessageHandler() {
//                    @Override
//                    public void onSent(String uniqueId, long threadId) {
//
//                    }
//                });


// String text = editText.getText().toString();
////        long textThread = Long.valueOf(editTextThread.getText().toString());
////        if (!text.equals("")) {
////            presenter.sendTextMessage(text, 381, null);
////        } else {
////            Toast.makeText(this, "Message is Empty", Toast.LENGTH_SHORT).show();
////        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
//            "Choose function",
                break;
            case 1:
                //"get thread"
                getThreads();
                break;
            case 2:
                //"rename thread",
                renameTHread();
                break;
            case 3:
                getUserInfo();

                break;
            case 4:
                //"reply message",
//                replyMessage();

                replyFileMessage();

                break;
            case 5:
                /**forward message */
                forwardMessage();
                break;
            case 6:
                //"send text message",
                break;
            case 7:
                //"get thread participant",
                getThreadParticipants();


                break;
            case 8:
                createThread();

                break;
            case 9:
                //get thread history
                getThreadHistory();

                break;
            case 10:
                //"mute thread",
                muteThread();

                break;
            case 11:
                //"un mute thread"
                unMuteThread();

                break;
            case 12:
                //"get contacts"
                getContacts();

                break;
            case 13:
                //"edit message"
                editMessage();

                break;
            case 14:
                addContact();
                break;
            case 15:
                // remove contact
                removeContact();
                break;
            case 16:
                /** UPDATE CONTACTS*/
                updateContact();
            case 17: {
                /** GET LAST SEEN **/

                getNotSeenDur();

                break;
            }
            case 18: {
                /**
                 * Pin ConversationVO
                 */

                pinThreadToTop();

                break;

            }
            case 19: {
                /**
                 * UnPin ConversationVO
                 */

                unPinThread();

                break;

            }


            case 20: {
                pinMessageToTop();
                break;
            }

            case 21: {
                unpinMessage();
                break;
            }

            case 22: {

                getMentionList();

                break;
            }

            case 23: {

                checkIsNameAvailable();

                break;
            }

            case 24: {

                createPublicThread();

                break;

            }

            case 25: {

                joinPublicThread();

                break;
            }
            case 26:{

                closeThread();

                break;
            }
            case 27:{

                getSentryLogs();

                break;
            }

        }
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
//                        .setAllMentioned(true)
//                        .setUnreadMentioned(true)
//                        .unreadMentions()
                .offset(0)
                .count(25)
//                        .withNoCache()
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

    private void getNotSeenDur() {
        ArrayList<Integer> testArray = new ArrayList<>();
        testArray.add(2);
        testArray.add(1);
//                testArray.add(123);
        getNotSeenDuration(testArray);
    }

    private void removeContact() {
        presenter.removeContact(TEST_THREAD_ID);
    }

    private void addContact() {

//        {"id":344016830,"senderName":"chat-server","senderId":0,"type":4,"content":"{\"type\":23,\"messageType\":0,\"subjectId\":0,\"uniqueId\":\"fe08a72b-09eb-45b2-b6b2-03cd4dab4855\",\"content\":\"{\\\"id\\\":15424,\\\"coreUserId\\\":31257,\\\"sendEnable\\\":true,\\\"receiveEnable\\\":true,\\\"name\\\":\\\"رضا شریفی\\\",\\\"cellphoneNumber\\\":\\\"09365090061\\\",\\\"email\\\":\\\"rezasharify1993.rsmi@gmail.com\\\",\\\"username\\\":\\\"user-15778858615492\\\",\\\"contactSynced\\\":true}\",\"time\":1606317640273}"}
        // add contact
//                presenter.addContact("",
//                        "",
//                        "",
//                        faker.name().username() + "@gmail.com",
//                        "");
//
//                presenter.addContact("",
//                        "",
//                        "",
//                        "",
//                        "");
// 16844 zabix1


        List<String> usernames = new ArrayList<>();
        usernames.add("z.mohammadi");
        usernames.add("p.khoshghadam");
        usernames.add("m.hasanpour");
        usernames.add("z.ershad");
        usernames.add("Samira.amiri");
        usernames.add("s.heydarizadeh");
        usernames.add("p.pahlavani");
        usernames.add("ma.amjadi");


//                for (String user :
//                        usernames) {
//
//
//                    try {
//                        RequestAddContact request = new RequestAddContact.Builder()
//                                .firstName(user + " n ")
//                                .lastName(user + " i ")
//                                .username(user)
//                                .build();
//
//                        presenter.addContact(request);
//
//                        Thread.sleep(7000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
        RequestAddContact request = new RequestAddContact.Builder()
                .firstName("فرهاد")
                .lastName("خیرخواه")
                .cellphoneNumber("09157770684") //80617 //80618
                // .cellphoneNumber()
                // .email()
                .build();

        presenter.addContact(request);
    }

    private void editMessage() {
        Inviter inviter = new Inviter();
        inviter.setName("farhad");
//                String meta = "\"{\\\"actionType\\\":1,\\\"data\\\":\\\"{\\\"amount\\\":1500,\\\"description\\\":\\\"\\\",\\\"id\\\":161,\\\"state\\\":2,\\\"destUserId\\\":1900896}\\\"}\"";

        JsonObject a = new JsonObject();
//
        a.addProperty("actionType", 7);
        a.addProperty("amount", 12);
        a.addProperty("id", 161);
        a.addProperty("state", 2);
        a.addProperty("destUserId", 1900897);

//                a.addProperty("name", "farhad");
//                a.addProperty("family", "kheirkhah");
//                a.addProperty("phoneNumber", "989157770684");


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
//                        .withNoCache()
                        .build();


        presenter.getThreadParticipant(request);


//        presenter.getThreadParticipant(20, null, TEST_THREAD_ID, new ChatHandler() {
//            @Override
//            public void onGetThreadParticipant(String uniqueId) {
//                super.onGetThreadParticipant(uniqueId);
//            }
//        });
    }

    //21622
    public static final String THREAD_UNIQUE_NAME = "unique_name_4_1584016531111";
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
                        .title("My Public Group")
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
         *         NORMAL: 0,
         *         OWNER_GROUP: 1,
         *         PUBLIC_GROUP: 2,
         *         CHANNEL_GROUP: 4,
         *         CHANNEL: 8
         *       }
         */

//        Invitee[] invite = new Invitee[]{
//                new Invitee(2951, 2)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
//        };



        /**
         *
         *
         * CHANNEL_GROUP: 4,
         *
         *
         */

//        Invitee[] invite = new Invitee[]{
////                new Invitee(3361, 2)
////                , new Invitee(3102, 2)
////                new Invitee(091, 1),
////                new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
//                new Invitee("29782", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////                new Invitee("27774", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////                new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
////                new Invitee(5638, 2),
////                new Invitee("z.mohammadi", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("p.khoshghadam", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("m.hasanpour", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("z.ershad", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("samira.amiri", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("s.heydarizadeh", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("p.pahlavani", InviteType.Constants.TO_BE_USER_USERNAME),
////                        new Invitee("ma.amjadi", InviteType.Constants.TO_BE_USER_USERNAME),
////                new Invitee(5638, 2),
//        };
        Inviter inviterw = new Inviter();
        inviterw.setName("this is sample metadata");
        String metac = gson.toJson(inviterw);


        List<Invitee> invite = new ArrayList<>();
        // add by user SSO_ID
//                                invite.add(new Invitee(122, 1));  //user jiji
//        invite.add(new Invitee("121", 1)); // user zizi
//        invite.add(new Invitee("63270", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63271", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63269", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//        invite.add(new Invitee("63268", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        invite.add(new Invitee("80617", InviteType.Constants.TO_BE_USER_CONTACT_ID));
        invite.add(new Invitee("80618", InviteType.Constants.TO_BE_USER_CONTACT_ID));
//                                invite.add(new Invitee(9981084527L, 3)); zizi cellphone
//                                invite.add(new Invitee(123, 5)); //user fifi
//                                invite.add(new Invitee(121, 5)); // user zizi

//                                invite.add(new Invitee(122, 1));  //user jiji


        RequestUploadImage requestUploadImage =
                new RequestUploadImage.Builder(ChatActivity.this, getUri())
                        .sethC(140)
                        .setwC(140)
                        .build();


        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(ThreadType.Constants.NORMAL, invite)
                .title("A New Thread " + (new Date().getTime() / 1000))
                .withDescription("Description created at "
                        + new Date().getTime())
//                .withImage("URL")
                .withMetadata(metac)
//                .typeCode("podspace")
//                .setUploadThreadImageRequest(requestUploadImage)
                .build();

        presenter.createThread(requestCreateThread);


    }

    private void updateContact() {

        presenter.updateContact(2951, "Farhad Amjadi",
                "Amjadi", "--------", "zi@gmail.com"
        );
    }


    public void getThreadHistory() {


        RequestGetHistory request = new RequestGetHistory
                .Builder(TEST_THREAD_ID)
                .offset(0)
                .count(25)
                .order("desc") //.order("asc")
//                .fromTime(new Date().getTime())
             //   .toTime(new Date().getTime())
//                .setMessageType(TextMessageType.Constants.POD_SPACE_PICTURE)
//                .withNoCache()
                .build();

        presenter.getHistory(request, null);


    }

    public void getThreads() {

        ArrayList<Integer> threadIds = new ArrayList<>();
        threadIds.add(TEST_THREAD_ID);
        threadIds.add(1573);
        threadIds.add(351);


        RequestThread requestThread = new RequestThread
                .Builder()
//                    .threadName("Te")
//                    .newMessages()
//                .partnerCoreContactId(566)
                .offset(0)
                .count(50)
//                    .threadIds(threadIds)
//                .withNoCache()
//                .typeCode("default")
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
//            @Override
//            public void onReplyMessage(String uniqueId) {
//                super.onReplyMessage(uniqueId);
//            }
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
//                .withNoCache()
                    .build();

//        presenter.getContact(0, 0L, null);

            presenter.getContact(request);

        }).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(() -> {

            RequestGetContact request2 = new RequestGetContact.Builder()
                    .count(10)
                    .offset(0)
//                .withNoCache()
                    .build();

//        presenter.getContact(0, 0L, null);

            presenter.getContact(request2);

//        offset = offset + 50;


        }).start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(ChatActivity.this, "\\__('?')__/", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFileChoose) {
            showPicChooser();
        }
        if (v == buttonConnect) {


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

//            presenter.connect(socketAddress,
//                    APP_ID, serverName, TOKEN, ssoHost,
//                    platformHost, fileServer, typeCode);
//

            presenter.connect(rc);


        }

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

                } else if (requestCode == FILE_REQUEST_CODE) {
                    Uri fileUri = data.getData();
                    String path = FilePick.getSmartFilePath(this, fileUri);
                    setFileUri(path);
                    setUri(fileUri);
                }
            }
        }


    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
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

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void getThreadWithCoreUser() {
        RequestThread requestThread = new RequestThread.Builder()
                .partnerCoreContactId(566)
//                .threadIds()
//                .threadName()
//                .partnerCoreContactId()
//                .creatorCoreUserId()
//                .partnerCoreUserId()
//                .count()
//                .offset()
                .build();
        presenter.getThreads(requestThread, null);
    }

    public void setSignalUniq(String signalUniq) {
        this.signalUniq = signalUniq;
    }

    public String getSignalUniq() {
        return signalUniq;
    }

    public void onUploadImage(View view) {


        presenter.uploadImageProgress(this, ChatActivity.this, getUri(), new ProgressHandler.onProgress() {
            @Override
            public void onProgressUpdate(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                runOnUiThread(() -> percentage.setText(progress + "%"));

                Log.e("UPLOAD_IMAGE", "op " + progress + " sent " + totalBytesSent + " toSend " + totalBytesToSend);

            }

            @Override
            public void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {

                Log.e("UPLAOD", imageJson);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Finish Upload", Toast.LENGTH_SHORT).show();
                    percentage.setTextColor(getResources().getColor(R.color.colorAccent));
                    percentage.setText("100");
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
        presenter.onStart();
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

    public void onUploadFile(View view) {

        if (getUri() != null) {

            presenter.uploadFileProgress(ChatActivity.this, this, getUri(), new ProgressHandler.onProgressFile() {
                @Override
                public void onProgressUpdate(int progress) {

                    Log.e("UFP", "opu: " + progress);
                }

                @Override
                public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                    Log.e("UFP", "op" + progress + " sent" + totalBytesSent + " toSend" + totalBytesToSend);

                    runOnUiThread(() -> percentageFile.setText(progress + "%"));


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
                        percentageFile.setText("100");
                        percentageFile.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    });

                }
            });
        }
    }

    public void SetThreadId(View view) {


        String tId = editTextThread.getText().toString();

        if (!tId.isEmpty())
            TEST_THREAD_ID = Integer.valueOf(tId);

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
}
