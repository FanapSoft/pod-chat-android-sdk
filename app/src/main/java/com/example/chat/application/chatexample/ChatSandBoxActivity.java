package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.util.JsonUtil;
import com.fanap.podchat.chat.ChatHandler;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

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
    private String selectedFilePath;
    private ProgressBar progressBar;
    private TextView percentage;
    private static final int PICK_IMAGE_FILE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;

    private Uri uri;
    private String fileUri;
    private static String name = "SandBox";
    private static String TOKEN = "ee371d61b1b94d1184c5228c59839421";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        TextView textViewState = findViewById(R.id.textViewStateChat);
        TextView textViewToken = findViewById(R.id.textViewUserId);
        percentage = findViewById(R.id.percentage);
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonToken = findViewById(R.id.buttonToken);
        editText = findViewById(R.id.editTextMessage);
        editTextToken = findViewById(R.id.editTextToken);
        editTextThread = findViewById(R.id.editTextThread);
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
        };
        presenter = new ChatPresenter(this, view, this);
        presenter.getLiveState().observe(this, textViewState::setText);

        setupSpinner(spinner);
        setupSecondSpinner(spinnerSecond);
        setupThirdSpinner(spinnerThird);
        buttonConnect.setOnClickListener(this);
        buttonToken.setOnClickListener(this);
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
                        presenter.block(1382L, new ChatHandler() {
                            @Override
                            public void onBlock(String uniqueId) {
                                super.onBlock(uniqueId);
                            }
                        });

                        break;
                    case 4:
                        presenter.unBlock(1382L, new ChatHandler() {
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
//                        1104 is a group
                        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().description("description + mine").title("new flower").build();
                        presenter.updateThreadInfo(1104, threadInfoVO, new ChatHandler() {
                            @Override
                            public void onUpdateThreadInfo(String uniqueId) {
                                super.onUpdateThreadInfo(uniqueId);
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
                        presenter.sendFileMessage(ChatSandBoxActivity.this, ChatSandBoxActivity.this,
                                "test file message",
                                381
                                , getUri(), null);
                        break;
                    case 3:
//                        presenter.uploadImage(ChatSandBoxActivity.this, ChatSandBoxActivity.this, getUri());
                        presenter.uploadImageProgress(ChatSandBoxActivity.this, ChatSandBoxActivity.this, getUri()
                                , new ProgressHandler.onProgress() {
                                    @Override
                                    public void onProgressUpdate(int bytesSent) {
                                        percentage.setText(String.valueOf(bytesSent));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            progressBar.setProgress(bytesSent, true);
                                        }
                                    }

                                    @Override
                                    public void onFinish(String imageJson, FileImageUpload fileImageUpload) {
                                        progressBar.setProgress(100);
                                        percentage.setText(String.valueOf(100));
                                    }

                                    @Override
                                    public void onError(String jsonError, ErrorOutPut error) {

                                    }
                                });
//                        case 4:
//                        presenter.uploadFile(ChatSandBoxActivity.this, ChatSandBoxActivity.this, getFileUri(), getUri());
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
//                        participantIds.add(577L);
//                        participantIds.add(824L);
                        presenter.addParticipants(661, participantIds, new ChatHandler() {
                            @Override
                            public void onAddParticipants(String uniqueId) {
                                super.onAddParticipants(uniqueId);
                            }
                        });

                        break;
                    case 7:
                        presenter.leaveThread(661, new ChatHandler() {
                            @Override
                            public void onLeaveThread(String uniqueId) {
                                super.onLeaveThread(uniqueId);
                            }
                        });

                        break;
                    case 8:
                        presenter.deleteMessage(4684, true, new ChatHandler() {
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
                android.R.layout.simple_spinner_item, ConstantSample.func);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void connect() {

//socketAddress: "wss://chat-sandbox.pod.land/ws",
// {**REQUIRED**} Socket Address ssoHost: "
//https://accounts.pod.land", // {**REQUIRED**} Socket Address
// ssoGrantDevicesAddress: "/oauth2/grants/devices",
// {**REQUIRED**} Socket Address platformHost: "//https://sandbox.pod.land:8043/srv/basic-platform", fileServer: "
        http:
//sandbox.fanapium.com:8080", serverName: "chat-server", // {**REQUIRED**} Server to to register on
//        presenter.connect("ws://172.16.106.26:8003/ws",
//                "POD-Chat", "chat-server", TOKEN, "http://172.16.110.76",
//                "http://172.16.106.26:8080/hamsam/", "http://172.16.106.26:8080/hamsam/");
        presenter.connect("ws://chat-sandbox.pod.land/ws",
                "POD-Chat", "chat-server", TOKEN, "https://accounts.pod.land",
                "https://sandbox.pod.land:8043/srv/basic-platform/", "http://sandbox.fanapium.com:8080/");
    }

    public void sendMessage(View view) {
        Inviter inviter = new Inviter();
        inviter.setName("sina");
        String meta = JsonUtil.getJson(inviter);
        presenter.sendTextMessage("test at" + " " + new Date().getTime() + name
                , 381, meta, null);


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
                threadIds.add(1104);
//                threadIds.add(1031);
//                presenter.getThread(2, 0, null, null);
                presenter.getThread(10, null, threadIds, null, null);
//                        new ChatHandler() {
//                    @Override
//                    public void onGetThread(String uniqueId) {
//                        super.onGetThread(uniqueId);
//                        Toast.makeText(ChatSandBoxActivity.this,uniqueId,Toast.LENGTH_SHORT).show();
//                    }
//                });

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
                messageIds.add(4313L);
                messageIds.add(4303L);
                presenter.forwardMessage(107, messageIds);
                break;
            case 6:
                //"send text message",
                break;
            case 7:
                //"get thread participant",
                presenter.getThreadParticipant(10, 0L, 381, null);

                break;
            case 8:
                /**
                 * int TO_BE_USER_SSO_ID = 1;
                 * int TO_BE_USER_CONTACT_ID = 2;
                 * int TO_BE_USER_CELLPHONE_NUMBER = 3;
                 * int TO_BE_USER_USERNAME = 4;
                 */
                /**"create thread"
                 * This is Invitee object
                 * ---->private int id;
                 * ---->private int idType;
                 *
                 */
                //alexi 570
                //felfeli 571
                Invitee[] invite = new Invitee[]{new Invitee(589, 2)
                        , new Invitee(381, 2)
                        , new Invitee(22, 2)
//                        , new Invitee(824, 2)
                };
                presenter.createThread(0, invite, null, new ChatHandler() {
                    @Override
                    public void onCreateThread(String uniqueId) {
                        super.onCreateThread(uniqueId);
                        Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 9:
                //get thread history
                History history = new History.Builder().count(2).build();
                presenter.getHistory(history, 381, new ChatHandler() {
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
                presenter.editMessage(13530,
                        "hi this is edit at" + new Date().getTime() + "by" + name, new ChatHandler() {
                            @Override
                            public void onEditMessage(String uniqueId) {
                                super.onEditMessage(uniqueId);
                                Toast.makeText(ChatSandBoxActivity.this, uniqueId, Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
            case 14:
                // add contact
                presenter.addContact("hani", "ra", "09128854535", "");
                break;
            case 15:
                // remove contact
                presenter.removeContact(1041);
                break;
            case 16:
                /**UPDATE CONTACTS*/
                presenter.updateContact(588, "masoudian", "", "", ""
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
        if (v == buttonConnect) {
            presenter.connect("ws://chat-sandbox.pod.land/ws",
                    "POD-Chat", "chat-server", TOKEN, "https://accounts.pod.land",
                    "https://sandbox.pod.land:8043/srv/basic-platform/", "http://sandbox.fanapium.com:8080/");
        }
        if (v == buttonToken) {

//            String token = editText.getText().toString();
//            if (token != null) {
                presenter.setToke("6c185fe056b7427eb4c9397029e20741");
//            }
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
