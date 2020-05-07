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

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.file_manager.download_file.model.ResultDownloadFile;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetFile;
import com.fanap.podchat.requestobject.RequestGetImage;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
import com.fanap.podchat.requestobject.RequestUploadFile;
import com.fanap.podchat.requestobject.RequestUploadImage;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.TextMessageType;
import com.fanap.podchat.util.ThreadType;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.fanap.podchat.example.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ChatActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener, ChatContract.view {
    private static final int FILE_REQUEST_CODE = 2;
    public static final String APP_ID = "POD-Chat";
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1007;
    private static final String TEST_THREAD_HASH = "X6NO3WJRWTUMN8";


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

    private static String TOKEN = "fb26a44d110b4e2ebdbb626708a6c633";
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String serverName = "chat-server";


    //local

//    private static String TOKEN = BaseApplication.getInstance().getString(R.string.token_jiji);
//    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
//    private static String serverName = "chatlocal";


    //test

//    private static String TOKEN = BaseApplication.getInstance().getString(R.string.token_zabbix_bot_2);
//    private static String ssoHost = BaseApplication.getInstance().getString(R.string.test_ssoHost);
//    private static String serverName = BaseApplication.getInstance().getString(R.string.test_serverName);


    private static String appId = "POD-Chat";
    private static String podSpaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_sand);


    /**
     * Integration server setting:
     */

//    private static String name = BaseApplication.getInstance().getString(R.string.integration_serverName);
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

//    private static String name = BaseApplication.getInstance().getString(R.string.main_server_name);
//    private static String socketAddress = BaseApplication.getInstance().getString(R.string.socketAddress);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.fileServer);

    /**
     * Sandbox setting:
     */

    private static String name = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);


//    //sand box / group

    public static int TEST_THREAD_ID = 8032;


//    main server / p2p

//    public static int TEST_THREAD_ID = 19868;

    // main server / group

//    public static int TEST_THREAD_ID = 46887;


    //integration /group

//    public static int TEST_THREAD_ID = 7090;


    //test server thread
//    public static int TEST_THREAD_ID = 7608;


    private String fileUri;

    private String signalUniq;


    ArrayList<String> runningSignals = new ArrayList<>();

    Faker faker;
    private String downloadingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        faker = new Faker();


//        Generator.generateFakeContact(5,getApplicationContext());

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

        buttonFileChoose.setOnClickListener(this);

        buttonFileChoose.setOnLongClickListener(v -> {

            presenter.shareLogs();
            return true;
        });

        ArrayList<Contact> contacts = new ArrayList<>();
        One<Contact> one = new One<>();
        one.setList(contacts);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);
        Spinner spinnerThird = findViewById(R.id.spinnerThird);
        buttonConnect.setOnClickListener(this);
        ChatContract.view view = new ChatContract.view() {

            @Override
            public void onMapStaticImage(ChatResponse<ResultStaticMapImage> chatResponse) {
                imageMap.setImageBitmap(chatResponse.getResult().getBitmap());
            }


        };

        presenter = new ChatPresenter(this, this, this);

        setupFirstSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);

        btnUploadFile.setOnClickListener(this::onUploadFile);

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

    /*
    "Choose Map function"
            , "Search Map"
            , "Map Routing"
            , "Block"
            , "UnBlock"
            , "GetBlockList"
            , "Update the thread info"
            , "Seen Message List"
            , "delivered Message List"
            , "Create thread with new Message"
            , "Get thread with coreUserId"
            ,"map static image"
            ,"map reverse"
            ,"Send MapLocation Message"
            ,"Add Admin"
            ,"Signal Message"
            */

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
//                        presenter.mapSearch("میدان آزادی", 35.7003510, 51.3376472);
                                presenter.isDatabaseOpen();
                                break;
                            case 2:
                                presenter.mapRouting("35.7003510,51.3376472", "35.7343510,50.3376472");
                                break;
                            case 3:
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
                                break;
                            case 4: {
                                Long ubThreadId = null;
//                                Long ubUserId = 121L;
                                Long ubUserId = null;
                                Long ubContactId = null;
                                Long unblockId = (long) TEST_THREAD_ID;
                                presenter.unBlock(unblockId, ubUserId, ubThreadId, ubContactId
                                        , null);
                                break;
                            }
                            case 5: {
                                getBlockList();

                                break;
                            }
                            case 6: {
                                ThreadInfoVO threadInfoVO = new ThreadInfoVO
                                        .Builder()
                                        .description("this is test description updated on " + new Date().toString())
                                        .title("flower").build();

                                presenter.updateThreadInfo(TEST_THREAD_ID, threadInfoVO, new ChatHandler() {
                                    @Override
                                    public void onUpdateThreadInfo(String uniqueId) {
                                        super.onUpdateThreadInfo(uniqueId);
                                    }
                                });
                                break;
                            }

                            case 7: {
                                RequestSeenMessageList requests = new RequestSeenMessageList
                                        .Builder(TEST_THREAD_ID).build();
                                presenter.seenMessageList(requests);
                                break;

                            }
                            case 8: {
                                RequestDeliveredMessageList requestD = new RequestDeliveredMessageList
                                        .Builder(50532).build();
                                presenter.deliveredMessageList(requestD);
                            }
                            break;
                            case 9: {

                                /*available invitee types*/
                                /*int TO_BE_USER_SSO_ID = 1;
                                 *int TO_BE_USER_CONTACT_ID = 2;
                                 *int TO_BE_USER_CELLPHONE_NUMBER = 3;
                                 *int TO_BE_USER_USERNAME = 4;
                                 *TO_BE_USER_ID = 5  // just for p2p
                                 */

                                List<Invitee> invite = new ArrayList<>();
                                // add by user SSO_ID
//                                invite.add(new Invitee(122, 1));  //user jiji
                                invite.add(new Invitee("121", 1)); // user zizi
//                                invite.add(new Invitee(9981084527L, 3)); zizi cellphone
//                                invite.add(new Invitee(123, 5)); //user fifi
//                                invite.add(new Invitee(121, 5)); // user zizi

//                                invite.add(new Invitee(122, 1));  //user jiji


                                //add by user contact id:
//                        invite.add(new Invitee(5638,2));

//                                new Invitee[]{
//                                new Invitee(122, 1)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
//                        List<Long> listForwardIds = new ArrayList<>();
//                        listForwardIds.add(1346L);

                                RequestThreadInnerMessage message = new RequestThreadInnerMessage
                                        .Builder("Hi at " + new Date().toString(), TextMessageType.Constants.TEXT)
//                                .forwardedMessageIds(listForwardIds)
                                        .build();

                                RequestCreateThread requestCreateThread = new RequestCreateThread
                                        .Builder(ThreadType.Constants.NORMAL, invite)
                                        .message(message)
                                        .build();

                                presenter.createThreadWithMessage(requestCreateThread);

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

                                if (runningSignals.size() > 0) {

                                    String uniqueId = runningSignals.get(runningSignals.size() - 1);

                                    stopTyping(uniqueId);

                                    runningSignals.remove(uniqueId);
                                }
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

                                break;
                            }

                            case 26: {

                                presenter.getStorageSize();

                                break;
                            }

                            case 27: {

//                                presenter.clearStorage();

                                break;
                            }


                        }
                    }

                    private void getBlockList() {

                        RequestBlockList request =
                                new RequestBlockList.Builder()
                                        .count(50)
                                        .offset(0)
//                                        .withNoCache()
                                        .build();

                        presenter.getBlockList(request);

//                        presenter.getBlockList(10L, 0L, new ChatHandler() {
//                            @Override
//                            public void onGetBlockList(String uniqueId) {
//                                super.onGetBlockList(uniqueId);
//                            }
//                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }


    private void cancelDownloadImage() {


        boolean result = presenter.cancelDownload(downloadingId);

        Log.e("DOWNLOAD", "Cancel Download Result " + result);


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

        RequestGetImage requestGetImage = new RequestGetImage.Builder(imageId, imageHashCode, true)
                .build();

        RequestGetFile requestGetFile = new RequestGetFile.Builder(fileId, fileHashCode, true).build();


        RequestGetPodSpaceFile rePod = new RequestGetPodSpaceFile.Builder("8GQRBPY93WHN9SGW")
                .build();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

            return;

        }

        downloadingId = presenter.downloadFile(rePod, new ProgressHandler.IDownloadFile() {


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


        RequestUploadImage requestUploadImage = new RequestUploadImage.Builder(this, getUri())
                .build();

        RequestUploadFile requestUploadFile = new RequestUploadFile.Builder(
                this, getUri()).build();


        List<Invitee> invite = new ArrayList<>();

        //f.kh sandbox
        invite.add(new Invitee("4893", InviteType.Constants.TO_BE_USER_CONTACT_ID));

        //p.pa main
//        invite.add(new Invitee(1151568, InviteType.Constants.TO_BE_USER_CONTACT_ID));

//        RequestThreadInnerMessage innerMessage = new RequestThreadInnerMessage
//                .Builder(TextMessageType.Constants.PICTURE)
//                //                .message("Create thread for File Message Test " + new Date().toString())
////                                .forwardedMessageIds(listForwardIds)
//                .build();


        RequestCreateThreadWithFile request = new RequestCreateThreadWithFile
                .Builder(ThreadType.Constants.OWNER_GROUP, invite, requestUploadFile, TextMessageType.Constants.PICTURE)
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


        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String center = "35.7003510,51.3376472";

                RequestLocationMessage requestLocationMessage = new RequestLocationMessage
                        .Builder()
                        .center(center)
                        .message("This is location ")
                        .activity(ChatActivity.this)
                        .threadId(TEST_THREAD_ID)
                        .build();

                presenter.sendLocationMessage(requestLocationMessage);


            }
        }


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

                        RequestFileMessage request = new RequestFileMessage.Builder(
                                ChatActivity.this,
                                TEST_THREAD_ID,
                                getUri(),
                                TextMessageType.Constants.POD_SPACE_PICTURE) // constructor
                                .description("test file message")
                                .systemMetadata(getMetaData())
                                .setUserGroupHash(TEST_THREAD_HASH)
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

                        break;
                    case 3:
                        presenter.uploadImage(ChatActivity.this, getUri());
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
                        presenter.leaveThread(TEST_THREAD_ID, new ChatHandler() {
                            @Override
                            public void onLeaveThread(String uniqueId) {
                                super.onLeaveThread(uniqueId);
                            }
                        });

                        break;
                    case 8:
                        deleteMessage();

                        break;
                    case 9:
                        RequestSearchContact requestSearchContact = new RequestSearchContact
                                .Builder("0", "50")
//                                .id("1063")
//                                .cellphoneNumber("09")
//                                .lastName("Khei")
//                                .firstName("pooria")
                                .query("pooria")
                                .build();
                        presenter.searchContact(requestSearchContact);
                        break;
                    case 10:
                        NosqlSearchMetadataCriteria builderMeta = new NosqlSearchMetadataCriteria
                                .Builder("name")
                                .is("sina")
                                .build();
                        NosqlListMessageCriteriaVO criteriaVO = new NosqlListMessageCriteriaVO.Builder(231)
                                .count(10)
                                .metadataCriteria(builderMeta)
                                .build();
                        presenter.searchHistory(criteriaVO, null);

                        break;
                    case 11:
                        break;
                    case 12:
                        presenter.cancelUpload(fileUnique[0]);
                        break;
                    case 13:

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
                        break;
                    case 14: {
                        //clear history
                        RequestClearHistory requestClearHistory = new RequestClearHistory
                                .Builder(TEST_THREAD_ID)
                                .build();
                        presenter.clearHistory(requestClearHistory);
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

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

//                       contactIds.add(121L);
//        presenter.removeParticipants(691, contactIds, new ChatHandler() {
//            @Override
//            public void onRemoveParticipants(String uniqueId) {
//                super.onRemoveParticipants(uniqueId);
//            }
//        });
    }

    private void addParticipants() {


        //deprecated


//        List<Long> participantIds = new ArrayList<>();
//        participantIds.add(521L);
//        participantIds.add(4101L);
//        participantIds.add(23116L);
//        participantIds.add(824L);
        //        presenter.addParticipants(691, participantIds, new ChatHandler() {
//            @Override
//            public void onAddParticipants(String uniqueId) {
//                super.onAddParticipants(uniqueId);
//            }
//        });

//        RequestAddParticipants request = new RequestAddParticipants
//                .Builder((long) TEST_THREAD_ID, participantIds)
//                .build();


        //add with username
//
//        RequestAddParticipants request = RequestAddParticipants
//                .newBuilder()
//                .threadId((long) TEST_THREAD_ID)
//                .withUserNames("fatemeh", "pooria")
//                .build();

        // add with contactId
//
//        RequestAddParticipants request = RequestAddParticipants.Builder
//                .newBuilder()
//                .addParticipantWithContactIdTo((long) TEST_THREAD_ID)
//                .withContactIds(participantIds)
////                .withContactIds(4101L)
//                .build();
//


        //add with coreUserIds

        RequestAddParticipants request = RequestAddParticipants
                .newBuilder()
                .threadId((long) TEST_THREAD_ID)
//                .withCoreUserIds(982L, 5241L)
                .withUserNames("a.rokni",
                        "ms.alavizadeh",
                        "bhamidpour",
                        "z.morshedi",
                        "m.rashed")
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
                presenter.renameThread(TEST_THREAD_ID, "*** Rename thread to " + new Date().toString() + " ***", new ChatHandler() {
                    @Override
                    public void onRenameThread(String uniqueId) {
                        super.onRenameThread(uniqueId);
                    }
                });

                break;
            case 3:
                //"get user info",
                presenter.getUserInfo(new ChatHandler() {
                    @Override
                    public void onGetUserInfo(String uniqueId) {
                        super.onGetUserInfo(uniqueId);
                    }
                });

                break;
            case 4:
                //"reply message",
//                replyMessage();

                replyFileMessage();

                break;
            case 5:
                /**forward message */
                ForwardMessage();
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
                presenter.muteThread(TEST_THREAD_ID, new ChatHandler() {
                    @Override
                    public void onMuteThread(String uniqueId) {
                        super.onMuteThread(uniqueId);
                    }
                });

                break;
            case 11:
                //"un mute thread"
                presenter.unMuteThread(TEST_THREAD_ID, new ChatHandler() {
                    @Override
                    public void onUnMuteThread(String uniqueId) {
                        super.onUnMuteThread(uniqueId);
                    }
                });

                break;
            case 12:
                //"get contacts"
                getContacts();

                break;
            case 13:
                //"edit message"
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


                presenter.editMessage(TEST_THREAD_ID,
                        "1111", meta, null);

                break;
            case 14:

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

                RequestAddContact request = new RequestAddContact.Builder()
                        .firstName("Pooria")
                        .lastName("Pahlavani")
                        .cellphoneNumber("989387181694")
                        .build();

                presenter.addContact(request);


                break;
            case 15:
                // remove contact
                presenter.removeContact(TEST_THREAD_ID);
                break;
            case 16:
                /** UPDATE CONTACTS*/
                updateContact();
            case 17: {
                /** GET LAST SEEN **/

                ArrayList<Integer> testArray = new ArrayList<>();
                testArray.add(2);
                testArray.add(1);
//                testArray.add(123);
                getNotSeenDuration(testArray);

                break;
            }
            case 18: {
                /**
                 * Pin ConversationVO
                 */

                RequestPinThread requestPinThread = new RequestPinThread.Builder(TEST_THREAD_ID)
                        .build();

                presenter.pinThread(requestPinThread);

                break;

            }
            case 19: {
                /**
                 * UnPin ConversationVO
                 */

                RequestPinThread requestPinThread = new RequestPinThread.Builder(TEST_THREAD_ID)
                        .build();

                presenter.unPinThread(requestPinThread);

                break;

            }


            case 20: {


                RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                        .setMessageId(TEST_THREAD_ID)
                        .setNotifyAll(true)
                        .build();

                presenter.pinMessage(requestPinMessage);

                break;
            }

            case 21: {


                RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                        .setMessageId(TEST_THREAD_ID)
                        .build();


                presenter.unPinMessage(requestPinMessage);


                break;
            }

            case 22: {

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


//            case 18: {
//
//                startTyping();
//
//                break;
//            }
//            case 19: {
//
//
//                if (runningSignals.size() > 0) {
//
//                    String uniqueId = runningSignals.get(runningSignals.size() - 1);
//
//                    stopTyping(uniqueId);
//
//                    runningSignals.remove(uniqueId);
//                }
//
//
//                break;
//            }


        }
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

        //if signal not stopped
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
        //alexi 570
        //felfeli 571
        //jiji 122
        //"firstName": "Ziiii",
        // masoud  : 2951
        // │         "userId": 121,
//        Invitee[] invite = new Invitee[]{
//                new Invitee(2951, 2)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
//        };
//        Inviter inviterw = new Inviter();
//        inviterw.setName("this is sample metadata");
//        String metac = gson.toJson(inviterw);
//        presenter.createThread(1, invite, null, "sina thread"
//                , null, metac, null);

        /**
         *
         *
         * CHANNEL_GROUP: 4,
         *
         *
         */

        Invitee[] invite = new Invitee[]{
//                new Invitee(3361, 2)
//                , new Invitee(3102, 2)
//                new Invitee(091, 1),
//                new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
                new Invitee("27751", InviteType.Constants.TO_BE_USER_CONTACT_ID),
                new Invitee("27774", InviteType.Constants.TO_BE_USER_CONTACT_ID),
                new Invitee("22835", InviteType.Constants.TO_BE_USER_CONTACT_ID),
//                new Invitee(5638, 2),
//                new Invitee(5638, 2),
        };
        Inviter inviterw = new Inviter();
        inviterw.setName("this is sample metadata");
        String metac = gson.toJson(inviterw);

        presenter.createThread(ThreadType.Constants.OWNER_GROUP, invite,
                "A New Thread " + (new Date().getTime() / 1000), "Description created at "
                        + new Date().getTime()
                , null, metac, null);

    }

    private void updateContact() {
        presenter.updateContact(2951, "Farhad Amjadi",
                "Amjadi", "--------", "zi@gmail.com"
        );
    }


    public void getThreadHistory() {
//        RequestGetHistory request = new RequestGetHistory
//                .Builder(1288)
//                .count(5)
//                .firstMessageId(1733)
//                .lastMessageId(1780)
//                .typeCode("6")
//                .build();

//        presenter.getThreadHistory(request, null);

//        RequestGetHistory requestGetHistory = new RequestGetHistory.Builder(threadId)
//                .fromTime()
//                .fromTimeNanos()
//                .id()
//                .toTime()
//                .toTimeNanos()
//                .build();

//
//        String[] uniqueIds = new String[2];
//
//        uniqueIds[0] = "9dc1025b-94ea-47e2-8847-335aaa277f64";
//        uniqueIds[1] = "5e4f5f88-2387-497c-8768-c6189e4eb6a2";
//        uniqueIds[1] = "212ls;dfk";
//        uniqueIds[2] = "212ls;dfk";

        RequestGetHistory request = new RequestGetHistory
                .Builder(TEST_THREAD_ID)
                .offset(0)
                .count(50)
//                .uniqueIds(uniqueIds)
//                .withNoCache()
//                .toTime(System.currentTimeMillis())
                .build();

        //            history.setToTime(System.currentTimeMillis());
//
//            history.setFromTimeNanos(298708000);

//            history.setCount(7);

        presenter.getHistory(request, null);


//        History history = new History.
//                Builder()
//                .id(TEST_THREAD_ID)
//                .build();
//        presenter.getHistory(history, TEST_THREAD_ID, new ChatHandler() {
//            @Override
//            public void onGetHistory(String uniqueId) {
//                super.onGetHistory(uniqueId);
//            }
//        });
    }

    public void getThreads() {
//        ArrayList<Integer> threadIds = new ArrayList<>();
//        threadIds.add(TEST_THREAD_ID);
//                threadIds.add(1573);
//                threadIds.add(351);
        RequestThread requestThread = new RequestThread
                .Builder()
//                .newMessages()
//                .partnerCoreContactId(566)
                .offset(0)
                .count(50)
//                .withNoCache()
                .build();


        presenter.getThreads(requestThread, null);

//        presenter.getConversationVOS(5, null, null, null, null);
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

    public void ForwardMessage() {

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

        RequestGetContact request = new RequestGetContact.Builder()
                .count(50)
                .offset(0)
//                .withNoCache()
                .build();

//        presenter.getContact(0, 0L, null);

        presenter.getContact(request);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
            }

            @Override
            public void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {

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
        long messageId = 108292;
        Uri fileUri = getUri();
        Inviter inviter = new Inviter();
        inviter.setName("Me");
        String meta = gson.toJson(inviter);
        RequestReplyFileMessage fileMessage = new RequestReplyFileMessage
                .Builder(messageContent, threadId, messageId, fileUri, this,
                TextMessageType.Constants.POD_SPACE_PICTURE)
                .systemMetaData(meta)
                .setUserGroupHashCode(TEST_THREAD_HASH)
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

                    Log.e("UFP", "opu");
                }

                @Override
                public void onProgress(String uniqueId, int progress, int totalBytesSent, int totalBytesToSend) {

                    Log.e("UFP", "op");

                    runOnUiThread(() -> percentageFile.setText(progress + "%"));


                }


                @Override
                public void onFinish(String imageJson, FileUpload fileImageUpload) {
                    Log.e("UFP", "of");

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

        showToast("There is " + response.getResult().getUnreadsCount() + " Unread message");
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
}
