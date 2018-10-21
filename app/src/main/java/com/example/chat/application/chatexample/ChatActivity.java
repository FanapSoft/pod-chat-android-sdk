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
import android.widget.Toast;

import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.util.JsonUtil;
import com.fanap.podchat.chat.ChatHandler;

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
    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private static final String[] func = {
            "Choose function",
            "get thread",
            "rename thread",
            "get user info",
            "reply message",
            "forward message",
            "send text message",
            "get thread participant",
            "create thread",
            "get thread history",
            "mute thread",
            "un mute thread",
            "get contacts",
            "edit message"
            , "add contact"
            , "remove contact"
            , "update contact"
    };

    private static final String[] funcSecond = {
            "Choose function"
            , "Sync Contact"
            , "Send file"
            , "Upload Image"
            , "Upload File"
            , "Remove Thread Participant"
            , "Add Thread Participant"
            , "Leave Thread"
            , "Delete Message"
            , "Search Contact"
            , "Search History"
    };
    private static final String[] funcThird = {
            "Choose Map function"
            , "Search Map"
            , "Map Routing"
            , "Block"
            , "UnBlock"
            , "GetBlockList"
            , "Update the thread info"
    };
    private Uri uri;

    //fel token
//    private String name = "felfeli";
//    private static String TOKEN = "e4f1d5da7b254d9381d0487387eabb0a";
    //Fifi
//    private String name = "Fifi";
//    private static String TOKEN = "5fb88da4c6914d07a501a76d68a62363";

//    private String name = "jiji";
//    private static String TOKEN = "fbd4ecedb898426394646e65c6b1d5d1";
//    private String name = "zizi";
//    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    //Token Alexi
    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";
    private static String name = "Alexi";
    private String fileUri;
//    private static String name = "SandBox";
//    private static String TOKEN = "d57010813e744e5fb1d12ce986209b86";

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
        buttonFileChoose.setOnClickListener(this);

        ArrayList<Contact> contacts = new ArrayList<>();
        One<Contact> one = new One<>();
        one.setList(contacts);

        textViewToken.setText(TOKEN + name);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinnerSecond = findViewById(R.id.spinnerSecond);
        Spinner spinnerThird = findViewById(R.id.spinnerThird);
        ChatContract.view view = new ChatContract.view() {
            @Override
            public void onGetThreadList() {

            }

            @Override
            public void onGetThreadHistory() {

            }

            @Override
            public void onGetContacts() {

            }

            @Override
            public void onGetThreadParticipant() {

            }

            @Override
            public void onSentMessage() {

            }

            @Override
            public void onGetDeliverMessage() {

            }

            @Override
            public void onGetSeenMessage() {

            }

            @Override
            public void onEditMessage() {

            }

            @Override
            public void onDeleteMessage() {

            }

            @Override
            public void onCreateThread() {

            }

            @Override
            public void onMuteThread() {

            }

            @Override
            public void onUnMuteThread() {

            }

            @Override
            public void onRenameGroupThread() {

            }

            @Override
            public void onAddContact() {

            }

            @Override
            public void onUpdateContact() {

            }

            @Override
            public void onUploadFile() {

            }

            @Override
            public void onUploadImageFile() {

            }

            @Override
            public void onRemoveContact() {

            }

            @Override
            public void onAddParticipant() {

            }

            @Override
            public void onRemoveParticipant() {

            }

            @Override
            public void onLeaveThread() {

            }

            @Override
            public void onBlock() {

            }

            @Override
            public void onUnblock() {

            }

            @Override
            public void onSearchContact() {

            }

            @Override
            public void onSearchHisory() {

            }

            @Override
            public void ongetBlockList() {

            }

            @Override
            public void onMapSearch() {

            }

            @Override
            public void onMapRouting() {

            }

            @Override
            public void onError() {

            }
        };

        presenter = new ChatPresenter(this, view);
        presenter.getLiveState().observe(this, textViewState::setText);

        setupSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);
    }

    private void setupThirdSpinner(Spinner spinnerThird) {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, funcThird);

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
                        presenter.block(1063L, new ChatHandler() {
                            @Override
                            public void onBlock(String uniqueId) {
                                super.onBlock(uniqueId);
                            }
                        });
                        break;
                    case 4:
                        presenter.unBlock(25L, new ChatHandler() {
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
                        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().description("this is test description").build();
                        presenter.updateThreadInfo(1031, threadInfoVO, new ChatHandler() {
                            @Override
                            public void onUpdateThreadInfo(String uniqueId) {
                                super.onUpdateThreadInfo(uniqueId);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSecondSpinner(Spinner spinnerSecond) {
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, funcSecond);

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
                        presenter.sendFileMessage(ChatActivity.this, ChatActivity.this,
                                "test file message",
                                381
                                , getUri(), null);
                        break;
                    case 3:
                        presenter.uploadImage(ChatActivity.this, ChatActivity.this, getUri());
                    case 4:
                        presenter.uploadFile(ChatActivity.this, ChatActivity.this, getFileUri(), getUri());
                        break;
                    case 5:
                        List<Long> contactIds = new ArrayList<>();
                        contactIds.add(123L);
//                       contactIds.add(121L);
                        presenter.removeParticipants(691, contactIds, new ChatHandler() {
                            @Override
                            public void onRemoveParticipants(String uniqueId) {
                                super.onRemoveParticipants(uniqueId);
                            }
                        });

                        break;
                    case 6:
                        List<Long> participantIds = new ArrayList<>();
                        participantIds.add(485L);
                        participantIds.add(577L);
                        participantIds.add(824L);
                        presenter.addParticipants(691, participantIds, new ChatHandler() {
                            @Override
                            public void onAddParticipants(String uniqueId) {
                                super.onAddParticipants(uniqueId);
                            }
                        });

                        break;
                    case 7:
                        presenter.leaveThread(691, new ChatHandler() {
                            @Override
                            public void onLeaveThread(String uniqueId) {
                                super.onLeaveThread(uniqueId);
                            }
                        });

                        break;
                    case 8:
                        presenter.deleteMessage(1921, true, new ChatHandler() {
                            @Override
                            public void onDeleteMessage(String uniqueId) {
                                super.onDeleteMessage(uniqueId);
                            }
                        });

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void connect(View view) {

//socketAddress: "wss://chat-sandbox.pod.land/ws",
// {**REQUIRED**} Socket Address ssoHost: "
//https://accounts.pod.land", // {**REQUIRED**} Socket Address
// ssoGrantDevicesAddress: "/oauth2/grants/devices",
// {**REQUIRED**} Socket Address platformHost: "//https://sandbox.pod.land:8043/srv/basic-platform", fileServer: "
        http:
//sandbox.fanapium.com:8080", serverName: "chat-server", // {**REQUIRED**} Server to to register on
        presenter.connect("ws://172.16.106.26:8003/ws",
                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
                "http://172.16.106.26:8080/hamsam/", "http://172.16.106.26:8080/hamsam/");

//        presenter.connect("ws://chat-sandbox.pod.land/ws",
//                "POD-Chat", "chat-server", TOKEN, "https://accounts.pod.land",
//                "https://sandbox.pod.land:8043/srv/basic-platform/","http://sandbox.fanapium.com:8080/");
    }

    public void sendMessage(View view) {
        Inviter inviter = new Inviter();
        inviter.setName("sina");
        String meta = JsonUtil.getJson(inviter);

        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
                , 312, meta, new ChatHandler() {
                    @Override
                    public void onSent(String uniqueId, long threadId) {
                        super.onSent(uniqueId, threadId);
                        Toast.makeText(ChatActivity.this,"its worked",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSentResult(String content) {
                        super.onSentResult(content);
                        if (content != null) {
                            Toast.makeText(ChatActivity.this,"no null",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

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
                ArrayList<Integer> threadIds = new ArrayList<>();
//                threadIds.add(22);
                threadIds.add(1031);
                presenter.getThread(2, null, null, null, new ChatHandler() {
                    @Override
                    public void onGetThread(String uniqueId) {
                        super.onGetThread(uniqueId);
                    }
                });
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
                presenter.replyMessage("this is reply from john", 381, 14103, new ChatHandler() {
                    @Override
                    public void onReplyMessage(String uniqueId) {
                        super.onReplyMessage(uniqueId);
                    }
                });

                break;
            case 5:
                /**forward message */
                ArrayList<Long> messageIds = new ArrayList<>();
                messageIds.add(15255L);
                messageIds.add(15256L);
                messageIds.add(15257L);
                presenter.forwardMessage(293, messageIds);
                break;
            case 6:
                //"send text message",
                break;
            case 7:
                //"get thread participant",
                presenter.getThreadParticipant(10, null, 22, new ChatHandler() {
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
                Invitee[] invite = new Invitee[]{new Invitee(123, 5)
//                        , new Invitee(565, 2)
//                        , new Invitee(578, 2)
//                        , new Invitee(824, 2)
                };
                presenter.createThread(0, invite, null, new ChatHandler() {
                    @Override
                    public void onCreateThread(String uniqueId) {
                        super.onCreateThread(uniqueId);
                    }
                });

                break;
            case 9:
                //get thread history
                History history = new History.Builder().build();
                presenter.getHistory(history, 312, new ChatHandler() {
                    @Override
                    public void onGetHistory(String uniqueId) {
                        super.onGetHistory(uniqueId);
                    }
                });

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
                presenter.getContact(2, 0L, new ChatHandler() {
                    @Override
                    public void onGetContact(String uniqueId) {
                        super.onGetContact(uniqueId);
                    }
                });

                break;
            case 13:
                //"edit message"
                presenter.editMessage(13530, "hi this is edit at" + new Date().getTime() + "by" + name, new ChatHandler() {
                    @Override
                    public void onEditMessage(String uniqueId) {
                        super.onEditMessage(uniqueId);
                    }
                });

                break;
            case 14:
                // add contact
                presenter.addContact("masodi", "ra", "09128054535", "");
                break;
            case 15:
                // remove contact
                presenter.removeContact(890);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                presenter.updateContact(571, "Fel", "", "", "devfelfel@gmail.com"
                );
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFileChoose) {
            showPicChooser();
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
}
