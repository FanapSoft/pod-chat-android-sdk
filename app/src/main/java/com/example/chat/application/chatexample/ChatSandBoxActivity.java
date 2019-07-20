package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.mainmodel.FileUpload;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestUnBlock;
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.fanap.podchat.example.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatSandBoxActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int FILE_REQUEST_CODE = 2;
    private ChatContract.presenter presenter;
    private EditText editText;
    private EditText editTextThread;
    private EditText editTextToken;
    private Button buttonFileChoose;
    private Button buttonConnect;
    private Button buttonToken;
    private ImageView imageMap;
    private String selectedFilePath;
    private ProgressBar progressBar;
    private TextView percentage;
    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private Gson gson = new GsonBuilder().create();

    private Uri uri;
    private String fileUri;
    private static String name = "SandBox";
    private static String TOKEN = "0cc5b265bb8b4ab59aeaf818143a3e88";
    private TextView percentageFile;
    private static String socketAddres = "wss://chat-sandbox.pod.land/ws";
    private static String serverName = "chat-server";
    private static String appId = "POD-Chat";
    private static String ssoHost = "https://accounts.pod.land/";
    private static String platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/";
    private static String fileServer = "http://sandbox.pod.land:8443/";
    private static String TYPE_CODE = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sqlScoutServer = SqlScoutServer.create(this, getPackageName());

        setContentView(R.layout.activity_chat);
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);

        percentage = findViewById(R.id.percentage);
        percentageFile = findViewById(R.id.percentageFile);


        buttonConnect = findViewById(R.id.buttonConnect);
        buttonToken = findViewById(R.id.buttonToken);
        editText = findViewById(R.id.editTextMessage);
        editTextToken = findViewById(R.id.editTextToken);
        editTextThread = findViewById(R.id.editTextThread);
        imageMap = findViewById(R.id.imageMap);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        buttonFileChoose = findViewById(R.id.buttonFileChoose);
        progressBar = findViewById(R.id.progressbar);

        buttonFileChoose.setOnClickListener(this);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);
        Spinner spinnerThird = findViewById(R.id.spinnerThird);
        ChatContract.view view = new ChatContract.view() {

            @Override
            public void onError() {

            }

            @Override
            public void onMapStaticImage(ChatResponse<ResultStaticMapImage> chatResponse) {
                Bitmap bitmap = chatResponse.getResult().getBitmap();
                imageMap.setImageBitmap(bitmap);
            }
        };
        presenter = new ChatPresenter(this, view, this);

        setupSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);
        buttonConnect.setOnClickListener(this);
        buttonToken.setOnClickListener(this);


        // PodNotificationActivity
        //not appId : NotificationService
        //notification serverName : SendPushByAppId

//        PodNotify.setApplication(this);

    }

//    public class OnMsgRecieved extends PodMessagingService {
//        @Override
//        public void onMessageReceived(PodNotificationActivity notification) {
//            super.onMessageReceived(notification);
//            Log.d("notifiy", notification.getText());
//        }
//    }

    //funcThird
    private void setupThirdSpinner(Spinner spinnerThird) {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ConstantSample.funcThird);

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThird.setAdapter(adapterSpinner);
        spinnerThird.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        presenter.mapSearch("میدان آزادی", 35.7003510, 51.3376472);
                        break;
                    case 2:
                        presenter.mapRouting("35.7003510,51.3376472", "35.7343510,50.3376472");
                        break;
                    case 3:
                        presenter.block(1382L, null, null, null
                        );

                        break;
                    case 4:
                        unBlock();

                        break;
                    case 5:
                        presenter.getBlockList(null, null, null);

                        break;
                    case 6:
                        updateThreadInfo();

                        break;
                    case 7:
                        RequestSeenMessageList requests = new RequestSeenMessageList.Builder(17374).build();
                        presenter.seenMessageList(requests);
                        break;
                    case 8:
                        RequestDeliveredMessageList requestD = new RequestDeliveredMessageList.Builder(17374).build();
                        presenter.deliveredMessageList(requestD);
                        break;
                    case 9:
                        createThreadWithMsg();
                        break;
                    case 10:

                        getthreadWithCoreUser();
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
//                        add admin
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendLocationMsg() {
        String center = "35.7003510,51.3376472";

        RequestLocationMessage requestLocationMessage = new RequestLocationMessage.Builder()
                .center(center)
                .message("This is location ")
                .activity(ChatSandBoxActivity.this)
                .threadId(2)
                .build();
        presenter.sendLocationMessage(requestLocationMessage);
    }

    public void mapReverse() {
        double lat = 35.7003510;
        double lng = 51.3376472;
        RequestMapReverse requestMapReverse = new RequestMapReverse.Builder(lat, lng).build();
        presenter.mapReverse(requestMapReverse);
    }

    public void mapStatic() {
//        String center = "35.7003510,51.3376472";
        String center = "35.7003510,35.7003510";
        RequestMapStaticImage staticImage = new RequestMapStaticImage.Builder()
                .center(center)
                .build();
        presenter.mapStaticImage(staticImage);
    }

    public void createThreadWithMsg() {

        List<Invitee> invite = new ArrayList<>();
        invite.add(new Invitee(122, 1));

//                                new Invitee[]{
//                                new Invitee(122, 1)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
//        List<Long> listForwardIds = new ArrayList<>();
//        listForwardIds.add(1346L);
        RequestThreadInnerMessage message = new RequestThreadInnerMessage
                .Builder()
                .message("create thread with msg")
//                .forwardedMessageIds(listForwardIds)
                .build();

        RequestCreateThread requestCreateThread = new RequestCreateThread
                .Builder(0
                , invite)
                .message(message)
                .build();
        presenter.createThreadWithMessage(requestCreateThread);
    }

    public void updateThreadInfo() {
        //                        1104 is a group
        RequestThreadInfo threadInfo = new RequestThreadInfo.Builder().threadId(1104).description("yes").name("this is test").build();

        presenter.updateThreadInfo(threadInfo, null);

//        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().description("description + mine").title("new flower").build();
//        presenter.updateThreadInfo(1104, threadInfoVO, new ChatHandler() {
//            @Override
//            public void onUpdateThreadInfo(String uniqueId) {
//                super.onUpdateThreadInfo(uniqueId);
//            }
//        });
    }

    public void unBlock() {
        RequestUnBlock requestUnBlock = new RequestUnBlock.Builder().build();
//        RequestUnBlock requestUnBlock = new RequestUnBlock.Builder(1382).build();
        presenter.unBlock(requestUnBlock, null);

//        presenter.unBlock(1382L, new ChatHandler() {
//            @Override
//            public void onUnBlock(String uniqueId) {
//                super.onUnBlock(uniqueId);
//            }
//        });
    }

    private void getthreadWithCoreUser() {
//        RequestThread requestThread = new RequestThread.Builder().partnerCoreUserId(982).build();
//        presenter.getThreads(requestThread);

        ArrayList<Integer> threadIds = new ArrayList<>();
//        threadIds.add(1105);
//        threadIds.add(1031);
        long count = 5;
        long offset;
        long creatorCoreUserId = 2;
        presenter.getThreads(null, null, null, null, creatorCoreUserId
                , 0, 0, null);
    }

    //funcSecond
    private void setupSecondSpinner(Spinner spinnerSecond) {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ConstantSample.funcSecond);

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
                        presenter.syncContact(ChatSandBoxActivity.this);
                        break;
                    case 2:

                        sendFileMessage();
                        break;
                    case 3:
                        presenter.uploadImage(ChatSandBoxActivity.this, getUri());
//                        presenter.uploadImageProgress(ChatSandBoxActivity.this, ChatSandBoxActivity.this, getUri()
//                                , new ProgressHandler.onProgress() {
//                                    @Override
//                                    public void onProgressUpdate(int bytesSent) {
//                                        percentage.setText(String.valueOf(bytesSent));
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                            progressBar.setProgress(bytesSent, true);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFinish(String imageJson, FileImageUpload fileImageUpload) {
//                                        progressBar.setProgress(100);
//                                        percentage.setText(String.valueOf(100));
//                                    }
//
//                                    @Override
//                                    public void onError(String jsonError, ErrorOutPut ErrorOutPut) {
//
//                                    }
//                                });
                        break;
                    case 4:
                        presenter.uploadFile(ChatSandBoxActivity.this, getUri());
                        break;
                    case 5:
                        List<Long> contactIds = new ArrayList<>();
                        contactIds.add(2L);
//                       contactIds.add(121L);
                        presenter.removeParticipants(1201, contactIds, new ChatHandler() {
                            @Override
                            public void onRemoveParticipants(String uniqueId) {
                                super.onRemoveParticipants(uniqueId);
                            }
                        });

                        break;
                    case 6:
                        List<Long> participantIds = new ArrayList<>();
                        participantIds.add(822L);
//                        participantIds.add(577L);
//                        participantIds.add(824L);
                        presenter.addParticipants(1105, participantIds, new ChatHandler() {
                            @Override
                            public void onAddParticipants(String uniqueId) {
                                super.onAddParticipants(uniqueId);
                            }
                        });
//2404
                        break;
                    case 7:
                        //1482
                        presenter.leaveThread(1482, new ChatHandler() {
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
                        SearchContact searchContact = new SearchContact.Builder("0", "2").id("1063").build();
                        presenter.searchContact(searchContact);
                        break;
                    case 10:
                        NosqlSearchMetadataCriteria builderMeta = new NosqlSearchMetadataCriteria.Builder("name").is("sina").build();
                        NosqlListMessageCriteriaVO criteriaVO = new NosqlListMessageCriteriaVO.Builder(231)
                                .count(10).metadataCriteria(builderMeta).build();
                        presenter.searchHistory(criteriaVO, new ChatHandler() {
                            @Override
                            public void onSearchHistory(String uniqueId) {
                                super.onSearchHistory(uniqueId);
                            }
                        });

                        break;
                    case 11:

                        replyFileMessage();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void replyFileMessage() {
        String messageContent = "this is reply from john";
        long threadId = 1105;
        long messageId = 9321;
        Uri fileUri = getUri();
        Inviter inviter = new Inviter();
        inviter.setName("sina");
        String meta = gson.toJson(inviter);
        RequestReplyFileMessage fileMessage = new RequestReplyFileMessage
                .Builder(messageContent, threadId, messageId, fileUri, this).systemMetaData(meta).build();
        presenter.replyFileMessage(fileMessage, null);
    }

    public void deleteMessage() {
        ArrayList<Long> msgIds = new ArrayList<>();
        msgIds.add(37443L);
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder()
                .messageIds(msgIds)
//                .deleteForAll(true)
                .typeCode("5")
                .build();
        presenter.deleteMessage(requestDeleteMessage, null);
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ConstantSample.func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void sendFileMessage() {

        RequestFileMessage requestFileMessage = new RequestFileMessage
                .Builder(this, 1105, getUri())
                .systemMetadata("name")
                .build();

        presenter.sendFileMessage(requestFileMessage, null);

//      presenter.sendFileMessage(ChatSandBoxActivity.this, ChatSandBoxActivity.this,
//        "test file message",
//        381
//       , getUri(), null, null);
    }

    public void sendMessage(View view) {
        Inviter inviter = new Inviter();
        inviter.setName("sina");
        String meta = gson.toJson(inviter);
        RequestMessage requestMessage = new RequestMessage
                .Builder("test at" + " " + new Date().getTime() + name, 1105)
                .jsonMetaData(meta)
                .build();
        presenter.sendTextMessage(requestMessage, null);


//        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
//                , 22, null, meta, null);

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
                presenter.renameThread(543, "***new group amiri*", new ChatHandler() {
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
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 4:
                //"reply message",
                presenter.replyMessage("this is reply from john", 381, 14103, null, new ChatHandler() {
                    @Override
                    public void onReplyMessage(String uniqueId) {
                        super.onReplyMessage(uniqueId);
                    }
                });

                break;
            case 5:
                /**forward message */
                ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(9262L);
                messageIds.add(9261L);
                presenter.forwardMessage(2, messageIds);
                break;
            case 6:

                break;
            case 7:
                //"get thread participant",
                getThreadParticipant();

                break;
            case 8:
                /**
                 * int TO_BE_USER_SSO_ID = 1;
                 * int TO_BE_USER_CONTACT_ID = 2;
                 * int TO_BE_USER_CELLPHONE_NUMBER = 3;
                 * int TO_BE_USER_USERNAME = 4;
                 * int TO_BE_USER_ID = 5;
                 */
                /**"create thread"
                 * This is Invitee object
                 * ---->private int id;
                 * ---->private int idType;
                 *
                 */
                // 589 poria
                Invitee[] invite = new Invitee[]{new Invitee(2404, 2)
//                        , new Invitee(1162, 2)
//                        , new Invitee(2404, 2)
//                        , new Invitee(824, 2)
                };
                CacheMessageVO cacheMessageVO = new CacheMessageVO();
                cacheMessageVO.setConversationId(5464);
                String metacreat = gson.toJson(cacheMessageVO);
                String image = "https://core.pod.land/nzh/image/?imageId=17006&width=476&height=476&hashCode=1666eedb75b-0.7473066083939505";
                presenter.createThread(0, invite, null, "this is the test description"
                        , image, metacreat, new ChatHandler() {
                            @Override
                            public void onCreateThread(String uniqueId) {
                                super.onCreateThread(uniqueId);
                                Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
            case 9:
                //get thread history
                History history = new History.Builder().build();

                presenter.getHistory(history, 22, new ChatHandler() {
                    @Override
                    public void onGetHistory(String uniqueId) {
                        super.onGetHistory(uniqueId);
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 10:
                //"mute thread",
                presenter.muteThread(543, new ChatHandler() {
                    @Override
                    public void onMuteThread(String uniqueId) {
                        super.onMuteThread(uniqueId);
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 11:
                //"un mute thread"
                //"un mute thread"
                presenter.unMuteThread(543, new ChatHandler() {
                    @Override
                    public void onUnMuteThread(String uniqueId) {
                        super.onUnMuteThread(uniqueId);
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 12:
                //"get contacts"
                presenter.getContact(10, null, new ChatHandler() {
                    @Override
                    public void onGetContact(String uniqueId) {
                        super.onGetContact(uniqueId);
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 13:
                //"edit message"
                Inviter inviter = new Inviter();
                inviter.setName("sina");
                String meta = gson.toJson(inviter);
                presenter.editMessage(9261,
                        "hi this is edit at" + new Date().getTime() + "by" + name, meta, null);

                break;
            case 14:
                // add contact
                presenter.addContact("Mehran", "Atash", "09338854885", "");
                break;
            case 15:
                // remove contact
                presenter.removeContact(1041);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                RequestUpdateContact requestUpdateContact = new RequestUpdateContact.Builder(2404)
                        .cellphoneNumber("09148401824")
                        .firstName("Black Masoudi")
                        .lastName("Amjadi")
                        .build();

                presenter.updateContact(requestUpdateContact);
//                presenter.updateContact(2404, "Asghar masoudi", "Amjadi", "09148401824", ""
//                );
        }
    }

    private void getThreads() {
        ArrayList<Integer> threadIds = new ArrayList<>();
        threadIds.add(2);
//        threadIds.add(1031);
        presenter.getThreads(10, null, threadIds, null, null);
    }

    private void getThreadParticipant() {
        presenter.getThreadParticipant(500, 0L, 2, null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onUploadImage(View view) {
        presenter.uploadImageProgress(this, ChatSandBoxActivity.this, getUri(), new ProgressHandler.onProgress() {
            @Override
            public void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        percentage.setText(bytesSent);
                    }
                });
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
            presenter.uploadFileProgress(ChatSandBoxActivity.this, this, getUri(), new ProgressHandler.onProgressFile() {
                @Override
                public void onProgressUpdate(int bytesSent) {
                }

                @Override
                public void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    percentageFile.setText(bytesSent);
                                }
                            });

                        }
                    });

                }

                @Override
                public void onFinish(String imageJson, FileUpload fileImageUpload) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            percentageFile.setText("100");
                            percentage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFileChoose) {
            showPicChooser();
        }
        if (v == buttonConnect) {
            presenter.connect(socketAddres,
                    appId, serverName, TOKEN, ssoHost,
                    platformHost, fileServer, TYPE_CODE);

//            PodNotify podNotify = new PodNotify.builder()
//                    .setAppId("NotificationService")
//                    .setServerName("SendPushByAppId")
//                    .setSocketServerAddress("http://172.16.110.61:8017")
//                    .setToken(TOKEN)
//                    .build(this);
//
//            podNotify.start(this);
        }
        if (v == buttonToken) {

            String freshtoken = editTextToken.getText().toString();
            if (!freshtoken.isEmpty()) {
                presenter.setToke(freshtoken);
            }
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

                    String pathFile = fileUri.toString();
//                    setFileUri(path);
                    setUri(Uri.parse(pathFile));
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

    @Override
    protected void onResume() {
//        sqlScoutServer.resume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        sqlScoutServer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
//        sqlScoutServer.destroy();
        super.onPause();
    }

    @Override
    protected void onStop() {
//        sqlScoutServer.destroy();
        super.onStop();
    }
}
