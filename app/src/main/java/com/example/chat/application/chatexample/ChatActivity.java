package com.example.chat.application.chatexample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.requestobject.RequestAddAdmin;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.ThreadType;
import com.fanap.podchat.util.Util;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.fanap.podchat.example.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener, ChatContract.view {
    private static final int FILE_REQUEST_CODE = 2;
    public static final String APP_ID = "POD-Chat";
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1007;
    public static int TEST_THREAD_ID = 10956;
    private ChatContract.presenter presenter;
    private EditText editText;
    private EditText editTextThread;
    private Button buttonFileChoose;
    private String selectedFilePath;
    private Button buttonConnect;
    private ImageView imageMap;
    private TextView textViewState;
    private TextView percentage;
    private TextView percentageFile;
    private Gson gson = new GsonBuilder().create();

    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;

    private Uri uri;

    //fel token
//    private String name = "felfeli";
//    private static String TOKEN = "e4f1d5da7b254d9381d0487387eabb0a";
    //Fifi
//    private String name = "Fifi";
//    private static String TOKEN = "5fb88da4c6914d07a501a76d68a62363";
//    private static String TOKEN = "6421ecebd40b4d09923bcf6379663d87";
//    private static String TOKEN = "7a18deb4a4b64339a81056089f5e5922";

//    private static String name = "Alexi";
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";

    //Masoud
//    private String name = "jiji";
//    private static String TOKEN = "fbd4ecedb898426394646e65c6b1d5d1";


    /**
     *
     *
     *
     * Sandbox setting:
     *
     */


//    private static String name = "SandBox";
//    private static String TOKEN = "81e5632d6c8b41efbabed1f5a5af7fdb";
//    private static String socketAddress = "wss://chat-sandbox.pod.land/ws";
//    private static String serverName = "chat-server";
//    private static String appId = "POD-Chat";
//    private static String ssoHost = "https://accounts.pod.land/";
//    //    private static String platformHost = "http://sandbox.pod.land:8080/";
//    private static String platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/";
//    private static String fileServer = "https://sandbox.pod.land:8443/";
//






//    //used for 122,123 id to get not seen

//    //Token Alexi
////
////    private static String appId = "POD-Chat";

////    Mehrara
//    private String socketAddress = "ws://172.16.106.26:8003/ws"; // {**REQUIRED**} Socket Address
//        private String socketAddress = "ws://172.16.106.221:8003/ws"; // {**REQUIRED**} Socket Address
    //    private String platformHost = "http://172.16.106.26:8080/hamsam/";
//        private String platformHost = "http://172.16.110.131:8080/"; // {**REQUIRED**} Platform Core Address
//     {**REQUIRED**} Platform Core Address

    // private String serverName = "chat-server";



    /**
     * Mehdi Sheikh Hosseini
     */

//    works:

    private String name = "zizi";
    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    private String socketAddress = "ws://172.16.110.131:8003/ws"; // {**REQUIRED**} Socket Address
    private String ssoHost = "http://172.16.110.76"; // {**REQUIRED**} Socket Address
    private String platformHost = "http://172.16.110.131:8080/";
    private String fileServer = "http://172.16.110.131:8080/"; // {**REQUIRED**} File Server Address
    private String serverName = "chat-server2";
    private String typeCode = null;
//




    /**
     *
     *
     *
     *  SERVICE_ADDRESSES = {
     *         SSO_ADDRESS: params.ssoHost || 'http://172.16.110.76',
     *                 PLATFORM_ADDRESS: params.platformHost || 'http://172.16.106.26:8080/hamsam',
     *                 FILESERVER_ADDRESS: params.fileServer || 'http://172.16.106.26:8080/hamsam',
     *                 POD_DRIVE_ADDRESS: params.podDrive || 'http://172.16.106.26:8080/hamsam',
     *                 MAP_ADDRESS: params.mapServer || 'https://api.neshan.org/v1'
     *     },
     *
     */












    private String fileUri;

    private String signalUniq;


    ArrayList<String> runningSignals = new ArrayList<>();

    Faker faker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        faker = new Faker();


        Logger.addLogAdapter(new AndroidLogAdapter());
        setContentView(R.layout.activity_chat);
        imageMap = findViewById(R.id.imageMap);

        textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        percentage = findViewById(R.id.percentage);
        percentageFile = findViewById(R.id.percentageFile);
        editText = findViewById(R.id.editTextMessage);
        editTextThread = findViewById(R.id.editTextThread);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        buttonFileChoose = findViewById(R.id.buttonFileChoose);
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonFileChoose.setOnClickListener(this);

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

        setupSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);

        // PodNotificationActivity

//        PodNotify.setApplication(this);
//
//        PodNotify podNotify = new PodNotify.builder()
//                .setAppId(appId)
//                .setServerName(serverName)
//                .setSocketServerAddress("172.16.110.61:8017")
//                .setSsoHost(ssoHost)
//                .setToken(TOKEN)
//                .build(this);
//
//        podNotify.start(this);

    }

//    public class Notification extends PodMessagingService {
//        @Override
//        public void onMessageReceived(@NonNull com.fanap.podnotify.model.Notification notification) {
//            super.onMessageReceived(notification);
//
//            Log.i("NOtification", notification.getText());
//        }
//    }

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
                                Long bUserId;
                                Long bContactId;

                                presenter.block(null, null, bThreadId
                                        , new ChatHandler() {
                                            @Override
                                            public void onBlock(String uniqueId) {
                                                super.onBlock(uniqueId);
                                            }
                                        });
                                break;
                            case 4: {
                                Long ubThreadId = null;
                                Long ubUserId = 121L;
                                Long ubContactId = null;
                                Long unblockId = null;
                                presenter.unBlock(unblockId, ubUserId, ubThreadId, ubContactId
                                        , null);
                                break;
                            }
                            case 5: {
                                presenter.getBlockList(null, null, new ChatHandler() {
                                    @Override
                                    public void onGetBlockList(String uniqueId) {
                                        super.onGetBlockList(uniqueId);
                                    }
                                });

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
                                        .Builder(46482).build();
                                presenter.seenMessageList(requests);
                                break;

                            }
                            case 8: {
                                RequestDeliveredMessageList requestD = new RequestDeliveredMessageList.Builder(46482).build();
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
//                                invite.add(new Invitee(121, 1)); // user zizi
//                                invite.add(new Invitee(9981084527L, 3)); zizi cellphone
                                invite.add(new Invitee(123, 5)); //user fifi

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
                                        .Builder()
                                        .message("Hello fifi " + new Date().toString())
//                                .forwardedMessageIds(listForwardIds)
                                        .build();

                                RequestCreateThread requestCreateThread = new RequestCreateThread
                                        .Builder(ThreadType.Constants.NORMAL
                                        , invite)
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
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
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
        typeRoles.add(RoleType.Constants.THREAD_ADMIN);
        typeRoles.add(RoleType.Constants.ADD_NEW_USER);
        typeRoles.add(RoleType.Constants.REMOVE_USER);

        //        typeRoles.add(RoleType.Constants.READ_THREAD);
//        typeRoles.add(RoleType.Constants.ADD_RULE_TO_USER);
//        typeRoles.add(RoleType.Constants.CHANGE_THREAD_INFO);
//        typeRoles.add(RoleType.Constants.DELETE_MESSAGE_OF_OTHERS);
//        typeRoles.add(RoleType.Constants.EDIT_MESSAGE_OF_OTHERS);
//        typeRoles.add(RoleType.Constants.EDIT_THREAD);
//        typeRoles.add(RoleType.Constants.POST_CHANNEL_MESSAGE);
//        typeRoles.add(RoleType.Constants.REMOVE_ROLE_FROM_USER);
//

//        ArrayList<String> typeRoles2 = new ArrayList<>();
//        typeRoles2.add(RoleType.Constants.REMOVE_USER);
//        typeRoles2.add(RoleType.Constants.ADD_RULE_TO_USER);






        RequestRole requestRole = new RequestRole();
        requestRole.setId(123);
        requestRole.setRoleOperation("add");
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


        RequestAddAdmin requestAddAdmin = new RequestAddAdmin
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.setAdmin(requestAddAdmin);
    }

    private void removeAdminRules() {

        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.THREAD_ADMIN);
//        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.ADD_NEW_USER);
        typeRoles.add(RoleType.Constants.REMOVE_USER);

        RequestRole requestRole = new RequestRole();
        requestRole.setId(123);
        requestRole.setRoleOperation("remove");
        requestRole.setRoleTypes(typeRoles);


//        RequestRole requestRole2 = new RequestRole();
//        requestRole2.setId(41);
//        requestRole2.setRoleOperation("remove");
//        requestRole2.setRoleTypes(typeRoles);


        ArrayList<RequestRole> requestRoles = new ArrayList<>();

        requestRoles.add(requestRole);
//        requestRoles.add(requestRole2);

        RequestAddAdmin requestAddAdmin = new RequestAddAdmin
                .Builder(TEST_THREAD_ID, requestRoles)
                .build();

        presenter.removeAdminRules(requestAddAdmin);


    }

    private void sendLocationMsg() {

        String center = "35.7003510,51.3376472";

        RequestLocationMessage requestLocationMessage = new RequestLocationMessage
                .Builder()
                .center(center)
                .message("This is location ")
                .activity(ChatActivity.this)
                .threadId(TEST_THREAD_ID)
                .build();


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
////            ActivityCompat.requestPermissions(this,
////                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                    REQUEST_WRITE_EXTERNAL_STORAGE);
//
//
//
//
//            return;
//
//        }

//        presenter.sendLocationMessage(requestLocationMessage);

        presenter.sendLocationMessage(requestLocationMessage, new ProgressHandler.sendFileMessage(){

            @Override
            public void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

                Log.d("MTAG","Update progress: " + "Total Bytes sent: "+totalBytesSent + " Total Bytes left " + totalBytesToSend);
            }

            @Override
            public void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {

                Log.d("MTAG","Finish upload");

            }

            @Override
            public void onError(String jsonError, ErrorOutPut error) {

                Log.d("MTAG","Error upload");

            }
        });



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
                        fileUnique[0] = presenter.sendFileMessage(ChatActivity.this, ChatActivity.this,
                                "test file message",
                                TEST_THREAD_ID,
                                getUri(), null, null, new ProgressHandler.sendFileMessage() {
                                    @Override
                                    public void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

                                    }
                                });

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
                        SearchContact searchContact = new SearchContact
                                .Builder("0", "50")
//                                .id("1063")
//                                .cellphoneNumber("09")
//                                .lastName("Khei")
                                .firstName("Moha")
                                .build();
                        presenter.searchContact(searchContact);
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
                        String uniqueId = "f874f03d-88b7-48b2-ac7f-3796c81d8ef8";
                        RetryUpload retryUpload = new RetryUpload.Builder().activity(ChatActivity.this).uniqueId(uniqueId).build();
                        presenter.retryUpload(retryUpload, new ProgressHandler.sendFileMessage() {
                            @Override
                            public void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {

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
                    case 15:
                    {
                        //2139
                        getAdminList();
                        break;
                    }
                    case 16:{

                        spamThread();
                        break;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                .Builder()
                .admin(true)
                .threadId(TEST_THREAD_ID)
                .build();

        presenter.getAdminList(requestGetAdmin);
    }

    public void deleteMessage() {



        ArrayList<Long> msgIds = new ArrayList<>();

        msgIds.add(50513L);
//        msgIds.add(50508L);
//        msgIds.add(50507L);

        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder()
                .messageIds(msgIds)
                .threadId((long) TEST_THREAD_ID)
                .deleteForAll(true)
                .typeCode("5")
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
        participantIds.add(41L);
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
        List<Long> participantIds = new ArrayList<>();
        participantIds.add(485L);
        participantIds.add(577L);
        participantIds.add(824L);
        //        presenter.addParticipants(691, participantIds, new ChatHandler() {
//            @Override
//            public void onAddParticipants(String uniqueId) {
//                super.onAddParticipants(uniqueId);
//            }
//        });

        RequestAddParticipants request = new RequestAddParticipants
                .Builder((long) TEST_THREAD_ID, participantIds)
                .build();
        presenter.addParticipants(request, null);

    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ConstantSample.func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void sendMessage(View view) {

        Inviter inviter = new Inviter();
        inviter.setName("farhad");
        String meta = gson.toJson(inviter);

        presenter.sendTextMessage(editText.getText().toString(), TEST_THREAD_ID, 2, meta, null);

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
                replyMessage();

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
                presenter.getThreadParticipant(20, null, TEST_THREAD_ID, new ChatHandler() {
                    @Override
                    public void onGetThreadParticipant(String uniqueId) {
                        super.onGetThreadParticipant(uniqueId);
                    }
                });


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
                inviter.setName("sina");
                String meta = gson.toJson(inviter);
                presenter.editMessage(17552,
                        "hi this is edit at" + new Date().getTime() + "by" + name, meta, null);

                break;
            case 14:
                // add contact
                presenter.addContact(faker.name().firstName(),
                        faker.name().lastName(),
                        faker.phoneNumber().cellPhone(),
                        faker.name().username() + "@gmail.com");
                break;
            case 15:
                // remove contact
                presenter.removeContact(2847);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                updateContact();
            case 17: {
                /** GET LAST SEEN **/

                ArrayList<Integer> testArray = new ArrayList<>();
                testArray.add(122);
                testArray.add(123);
                getNotSeenDuration(testArray);

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

        /**CHANNEL_GROUP: 4,
         */
        Invitee[] invite = new Invitee[]{
//                new Invitee(3361, 2)
//                , new Invitee(3102, 2)
//                        new Invitee(123, 5),
                new Invitee(824, 2),
                new Invitee(5638, 2),
        };
        Inviter inviterw = new Inviter();
        inviterw.setName("this is sample metadata");
        String metac = gson.toJson(inviterw);
        presenter.createThread(1, invite,
                "another thread", "zizi thread." +
                        " this thread created to test admin rules"
                , null, metac, null);

    }

    private void updateContact() {
        presenter.updateContact(2951, "Farhad Amjadi",
                "Amjadi", "09148401824", "zi@gmail.com"
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


        RequestGetHistory request = new RequestGetHistory
                .Builder(TEST_THREAD_ID)
                .offset(0)
                .count(50)
                .build();

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

//                .partnerCoreContactId(566)
//                .count(5)
                .build();
        presenter.getThreads(requestThread,null);

//        presenter.getThreads(5, null, null, null, null);
    }

    public void replyMessage() {
        RequestReplyMessage message = new RequestReplyMessage
                .Builder("this is reply from john", TEST_THREAD_ID, 31849)
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
        messageIds.add(15255L);
        messageIds.add(15256L);
        messageIds.add(15257L);
        long threadId = TEST_THREAD_ID;
        presenter.forwardMessage(threadId, messageIds);

//        RequestForwardMessage forwardMessage = new RequestForwardMessage
//                .Builder(threadId, messageIds)
//                .build();
//        presenter.forwardMessage(forwardMessage);


    }

    private void getContacts() {
        presenter.getContact(0, 0L, null);
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
                    fileServer)
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
            public void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                runOnUiThread(() -> percentage.setText(bytesSent));
            }

            @Override
            public void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {
                Toast.makeText(getApplicationContext(), "Finish Upload", Toast.LENGTH_SHORT).show();
                percentage.setTextColor(getResources().getColor(R.color.colorAccent));
                percentage.setText("100");

            }
        });
    }

    public void onUploadFile(View view) {
        if (getUri() != null) {
            presenter.uploadFileProgress(ChatActivity.this, this, getUri(), new ProgressHandler.onProgressFile() {
                @Override
                public void onProgressUpdate(int bytesSent) {
                }

                @Override
                public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    runOnUiThread(() -> runOnUiThread(() -> percentageFile.setText(bytesSent)));

                }

                @Override
                public void onFinish(String imageJson, FileUpload fileImageUpload) {
                    runOnUiThread(() -> {
                        percentageFile.setText("100");
                        percentage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
}
