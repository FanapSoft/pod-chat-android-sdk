package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RetryUpload;
import com.fanap.podchat.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int FILE_REQUEST_CODE = 2;
    private ChatContract.presenter presenter;
    private EditText editText;
    private EditText editTextThread;
    private Button buttonFileChoose;
    private String selectedFilePath;
    private Button buttonConnect;

    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;

    private Uri uri;

    //fel token
//    private String name = "felfeli";
//    private static String TOKEN = "e4f1d5da7b254d9381d0487387eabb0a";
    //Fifi
    private String name = "Fifi";
    private static String TOKEN = "5fb88da4c6914d07a501a76d68a62363";

//    private static String name = "Alexi";
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";

    //Masoud
//    private String name = "jiji";
//    private static String TOKEN = "fbd4ecedb898426394646e65c6b1d5d1";

//    private String name = "zizi";
//    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    //Token Alexi
//

    private String socketAddress = "ws://172.16.106.26:8003/ws"; // {**REQUIRED**} Socket Address
    private String ssoHost = "http://172.16.110.76"; // {**REQUIRED**} Socket Address
    private String platformHost = "http://172.16.106.26:8080/hamsam/"; // {**REQUIRED**} Platform Core Address
    private String fileServer = "http://172.16.106.26:8080/hamsam/"; // {**REQUIRED**} File Server Address
    private String serverName = "chat-server";
    private String typeCode = null;

    private String fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
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

        };

        presenter = new ChatPresenter(this, view, this);
        presenter.getLiveState().observe(this, textViewState::setText);

        setupSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);
    }

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
                        long bThreadId = 1573;
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
                    case 4:
                        Long ubThreadId = 1573L;
                        Long ubUserId = null;
                        Long ubContactId = null;
                        Long unblockId= null;
                        presenter.unBlock(unblockId, ubUserId, ubThreadId, ubContactId
                                , new ChatHandler() {
                            @Override
                            public void onUnBlock(String uniqueId) {
                                super.onUnBlock(uniqueId);
                            }
                        });
                        break;
                    case 5:
                        presenter.getBlockList(null, null, new ChatHandler() {
                            @Override
                            public void onGetBlockList(String uniqueId) {
                                super.onGetBlockList(uniqueId);
                            }
                        });

                        break;
                    case 6:
                        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().description("this is test description")
                                .title("flower").build();
                        presenter.updateThreadInfo(1211, threadInfoVO, new ChatHandler() {
                            @Override
                            public void onUpdateThreadInfo(String uniqueId) {
                                super.onUpdateThreadInfo(uniqueId);
                            }
                        });
                    case 7:
                        RequestSeenMessageList requests = new RequestSeenMessageList.Builder(17374).build();
                        presenter.seenMessageList(requests);
                        break;
                    case 8:
                        RequestDeliveredMessageList requestD = new RequestDeliveredMessageList.Builder(17374).build();
                        presenter.deliveredMessageList(requestD);
                        break;
                    case 9:
                        List<Invitee> invite = new ArrayList<>();
                        invite.add(new Invitee(122, 1));

//                                new Invitee[]{
//                                new Invitee(122, 1)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
                        List<Long> listForwardIds = new ArrayList<>();
                        listForwardIds.add(1346L);
                        RequestThreadInnerMessage message = new RequestThreadInnerMessage
                                .Builder("hello")
                                .forwardedMessageIds(listForwardIds)
                                .build();

                        RequestCreateThread requestCreateThread = new RequestCreateThread
                                .Builder(0
                                , invite)
//                                .message(message)
                                .build();
                        presenter.createThreadWithMessage(requestCreateThread);
                        break;
                    case 10:

                        getthreadWithCoreUser();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                                381,
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
                        presenter.leaveThread(1576, new ChatHandler() {
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void deleteMessage() {
        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(17462)
//                .deleteForAll(true)
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
        participantIds.add(123L);
        long threadId = 691;
        RequestRemoveParticipants request = new RequestRemoveParticipants.Builder(threadId, participantIds).build();
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
        long threadId = 691;
//        presenter.addParticipants(691, participantIds, new ChatHandler() {
//            @Override
//            public void onAddParticipants(String uniqueId) {
//                super.onAddParticipants(uniqueId);
//            }
//        });

        RequestAddParticipants request = new RequestAddParticipants.Builder(threadId, participantIds).build();
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
        inviter.setName("sina");
        String meta = JsonUtil.getJson(inviter);

        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
                , 1576, 2, meta, null);
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
                presenter.renameThread(634, "***new group amiri *", new ChatHandler() {
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
                presenter.getThreadParticipant(20, null, 351, new ChatHandler() {
                    @Override
                    public void onGetThreadParticipant(String uniqueId) {
                        super.onGetThreadParticipant(uniqueId);
                    }
                });

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
                //alexi 570
                //felfeli 571
                //jiji 122
                //"firstName": "Ziiii",     │         "userId": 121,
                Invitee[] invite = new Invitee[]{
                        new Invitee(2952, 2)
//                        , new Invitee(1967, 2)
//                        ,new Invitee(123, 5)
//                        , new Invitee(824, 2)
                };
                Inviter inviterw = new Inviter();
                inviterw.setName("this is sample metadata");
                String metac = JsonUtil.getJson(inviterw);
                presenter.createThread(0, invite, null, "sina thread"
                        , null, metac, new ChatHandler() {
                            @Override
                            public void onCreateThread(String uniqueId) {
                                super.onCreateThread(uniqueId);
                            }
                        });

                break;
            case 9:
                //get thread history
                getHistory();

                break;
            case 10:
                //"mute thread",
                presenter.muteThread(22, new ChatHandler() {
                    @Override
                    public void onMuteThread(String uniqueId) {
                        super.onMuteThread(uniqueId);
                    }
                });

                break;
            case 11:
                //"un mute thread"
                presenter.unMuteThread(22, new ChatHandler() {
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
                String meta = JsonUtil.getJson(inviter);
                presenter.editMessage(17552,
                        "hi this is edit at" + new Date().getTime() + "by" + name, meta, null);

                break;
            case 14:
                // add contact
                presenter.addContact("Pouria", "Pahlevani", "09387181694", "min" + new Date().getTime());
                break;
            case 15:
                // remove contact
                presenter.removeContact(890);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                updateContact();
        }
    }

    private void updateContact() {
        presenter.updateContact(2404, "Farhad Amjadi", "Amjadi", "09148401824", "zi@gmail.com"
        );
    }

    public void getHistory() {
//        RequestGetHistory request = new RequestGetHistory
//                .Builder(1288)
//                .count(5)
//                .firstMessageId(1733)
//                .lastMessageId(1780)
//                .typeCode("6")
//                .build();
//
//        presenter.getHistory(request, null);

        History history = new History.Builder().build();
        presenter.getHistory(history, 1288, new ChatHandler() {
            @Override
            public void onGetHistory(String uniqueId) {
                super.onGetHistory(uniqueId);
            }
        });
    }

    public void getThreads() {
        ArrayList<Integer> threadIds = new ArrayList<>();
        threadIds.add(1576);
//                threadIds.add(1573);
//                threadIds.add(351);
//        RequestThread requestThread = new RequestThread
//                .Builder()
//                .partnerCoreContactId(566)
//                .count(5)
//                .build();
//        presenter.getThreads(requestThread);

        presenter.getThreads(6, null, null, null, null);
    }

    public void replyMessage() {
        RequestReplyMessage message = new RequestReplyMessage.Builder("this is reply from john", 381, 14103).build();
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
        long threadId = 1288;
        presenter.forwardMessage(293, messageIds);

//        RequestForwardMessage forwardMessage = new RequestForwardMessage
//                .Builder(threadId, messageIds)
//                .build();
//        presenter.forwardMessage(forwardMessage);
    }

    private void getContacts() {
        presenter.getContact(15, 0L, null);
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

            presenter.connect(socketAddress,
                    "POD-Chat", serverName, TOKEN, ssoHost,
                    platformHost, fileServer, typeCode);
        }
    }

    private void showPicChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public void getthreadWithCoreUser() {
        RequestThread requestThread = new RequestThread.Builder().partnerCoreContactId(566).build();
        presenter.getThreads(requestThread, null);
    }
}
