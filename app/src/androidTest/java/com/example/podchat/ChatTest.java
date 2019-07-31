package com.example.podchat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.example.chat.application.chatexample.ChatSandBoxActivity;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultThreads;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ChatTest extends ChatAdapter {

    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;
    @Mock
    private Activity activity;
    private Context appContext;

    private static String TOKEN = "9a270004e8f44cff8fd43ae5dca23db0";
    private static String NAME = "SandBox";

    private static String socketAddres = "wss://chat-sandbox.pod.land/ws";
    private static String serverName = "chat-server";
    private static String appId = "POD-Chat";
    private static String ssoHost = "https://accounts.pod.land/";
    private static String platformHost = "https://sandbox.pod.land:8043/srv/basic-platform/";
    private static String fileServer = "http://sandbox.pod.land:8080/";
    private static String TYPE_CODE = "";

    private ChatSandBoxActivity chatSandBoxActivity;

    @Before
    public void setUp() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        presenter = new ChatPresenter(appContext, view, chatSandBoxActivity);
        presenter.connect(socketAddres,
                appId, serverName, TOKEN, ssoHost,
                platformHost, fileServer, "");
    }

    @Test
    @MediumTest
    public void getUserInfo() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getUserInfo(null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetUserInfo();
    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);

    }

    @Test
    @MediumTest
    public void createThreadWithMetaData() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Invitee[] invite = new Invitee[]{new Invitee(589, 2)
                , new Invitee(1162, 2)
                , new Invitee(2404, 2)
//                        , new Invitee(824, 2)
        };
        Contact contac = new Contact();
        contac.setLastName("mamadi");

//        String  metaData =
//                JsonUtil.getJson(contac);
//        presenter.createThread(0,invite,null,null,null,metaData,null);

    }

    @Test
    @MediumTest
    public void getThreadList() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getThreads(10, null, null, null, null);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        view = new ChatContract.view() {
            @Override
            public void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {
                Assert.assertNotNull(thread);
            }
        };
        Mockito.verify(view, Mockito.times(1));
    }

    // long creatorCoreUserId
    @Test
    @MediumTest
    public void threadWithCreatorCoreUserId() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Integer> threadIds = new ArrayList<>();
//        threadIds.add(1105);
//        threadIds.add(1031);
        long count = 5;
        long offset;
        long creatorCoreUserId = 2;
        presenter.getThreads(null, null, null, null, creatorCoreUserId
                , 0, 0, null);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1));
    }

    //long partnerCoreUserId
    @Test
    @MediumTest
    public void threadWithPartnerCoreUserId() {

    }

    //long partnerCoreContactId
    @Test
    @MediumTest
    public void threadWithPartnerCoreContactId() {

    }

    //Get History
    @Test
    @MediumTest
    public void getThreadHistory() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        History history = new History.Builder().count(5).build();
        presenter.getHistory(history, 381, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory();
    }


    @Test
    @MediumTest
    public void getContacts() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getContact(50, 0L, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetContacts();
    }

    @Test
    @MediumTest
    public void getThreadParticipant() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getThreadParticipant(10, 0L, 352, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetThreadParticipant();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnSent() {
        presenter.sendTextMessage("this is test", 381, 5, null, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnDeliver() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, null, null, null);
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetDeliverMessage();
    }

    @Test
    @LargeTest
    public void sendTestMessageOnSeen() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, null, "name", null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onGetSeenMessage();
    }


    @Test
    @MediumTest
    public void editMessage() {
        view.onSentMessage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.sendTextMessage("this is test", 381, 2, "", null);
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.editMessage(1350, "salam this is edite" + new Date().getTime() + "by" + NAME, "", null);
        Mockito.verify(view, Mockito.times(1)).onEditMessage();
    }

    /**
     * int TO_BE_USER_SSO_ID = 1;
     * int TO_BE_USER_CONTACT_ID = 2;
     * int TO_BE_USER_CELLPHONE_NUMBER = 3;
     * int TO_BE_USER_USERNAME = 4;
     */
    /**
     * "create thread"
     * This is Invitee object
     * ---->private int id;
     * ---->private int idType;
     */
    @Test
    @MediumTest
    public void createThread() {
        //alexi 570
        //felfeli 571
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Invitee[] invite = new Invitee[]{new Invitee(566, 2)};
        presenter.createThread(0, invite, "yes", "first description", null, null, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onCreateThread();
    }

    @Test
    @MediumTest
    public void muteThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.muteThread(381, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onMuteThread();
    }

    @Test
    @MediumTest
    public void unMuteThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.unMuteThread(352, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUnMuteThread();
    }

    @Test
    @MediumTest
    public void replyMessage() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        presenter.replyMessage("this is reply to all of you at" + new Date().getTime()
                , 381, 14103, null, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Mockito.verify(view, Mockito.times(1)).
    }

    //fifi is the admin of groupId = 632
    @Test
    @MediumTest
    public void renameThread() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.renameThread(632, "5_گروه خودمونی", null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onRenameGroupThread();
    }

    @Test
    @MediumTest
    public void addContact() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.addContact("maman", "sadeghi", "091224858169", "dev55@gmail.com");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onAddContact();
//        Mockito.verify(view,Mockit.)

    }

    //    fifiUser
    @Test
    @MediumTest
    public void updateContact() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.updateContact(861, "masoud", "rahimi2", "09122488169"
                , "dev@gmail.com");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUpdateContact();
    }

    @Test
    @MediumTest
    public void removeContact() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.removeContact(861);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onRemoveContact();
    }

    @Test
    @MediumTest
    public void uploadFile() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("content://com.android.providers.downloads.documents/document/230");
        String fileUri = "/storage/emulated/0/Download/Manager.v6.31.Build.3.Crack.Only_pd.zip";
        presenter.uploadFile(activity, uri);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUploadFile();
    }

    @Test
    @MediumTest
    public void UploadImageFile() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("content://media/external/images/media/781");
        presenter.uploadImage(activity, uri);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUploadImageFile();
    }

    @Test
    @MediumTest
    public void uploadProgressImage() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("content://media/external/images/media/781");
        presenter.uploadImageProgress(appContext, activity, uri, new ProgressHandler.onProgress() {
            @Override
            public void onProgressUpdate(int bytesSent) {
                Mockito.anyInt();
            }

            @Override
            public void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {

            }

            @Override
            public void onError(String jsonError, ErrorOutPut error) {

            }
        });
    }

    @Test
    @MediumTest
    public void spam() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.spam(0L);

    }

    @Test
    @MediumTest
    public void syncContact() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.syncContact(activity);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onAddContact();
    }

    @Test
    @MediumTest
    public void AddParticipant() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> participantIds = new ArrayList<>();
        participantIds.add(824L);
        participantIds.add(577L);
        presenter.addParticipants(691, participantIds, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onAddParticipant();
    }

    @Test
    @MediumTest
    public void removeParticipant() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> contactIds = new ArrayList<>();
        contactIds.add(123L);
        contactIds.add(121L);
        presenter.removeParticipants(691, contactIds, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onRemoveParticipant();
    }

    @Test
    @MediumTest
    public void leaveThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.leaveThread(657, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onLeaveThread();
    }

    @Test
    @MediumTest
    public void deleteMessage() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ArrayList<Long> messageIds = new ArrayList<>();
        messageIds.add(14380L);

        presenter.deleteMessage(messageIds,0L, true, null);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onDeleteMessage();
    }

    //getThread possibility test

    @Test
    @MediumTest
    public void searchInThreads() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getThreads(20, 0L, null, "FiFi", null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1));
    }

    @Test
    @MediumTest
    public void getThreadWithIds() {

    }

    @Test
    @MediumTest
    public void getThreadWithName() {

    }

    @Test
    @MediumTest
    public void getThreadWithPartnerCoreUserId() {

    }

    @Test
    @MediumTest
    public void getThreadWithCreatorCoreUserId() {

    }

    @Test
    @MediumTest
    public void getThreadWithPartnerCoreContactId() {

    }


    @Test
    @MediumTest
    public void block() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        presenter.block(575L, null);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onBlock();
    }

    @Test
    @MediumTest
    public void unBlock() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        presenter.unBlock(62L, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onUnblock();
    }

    @Test
    @MediumTest
    public void searchContact() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SearchContact searchContact = new SearchContact.Builder("0", "2").id("1063").build();
        presenter.searchContact(searchContact);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onSearchContact();
    }

    @Test
    @MediumTest
    public void searchHistory() {

    }

    @Test
    @MediumTest
    public void getBlockList() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.getBlockList(null, null, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).ongetBlockList();
    }

    @Test
    @MediumTest
    public void routingMap() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.mapRouting("35.7003510,51.3376472", "35.7343510,50.3376472");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onMapRouting();
    }

    @Test
    @MediumTest
    public void searchMap() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        presenter.mapSearch("میدان آزادی", 35.7003510, 51.3376472);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mockito.verify(view, Mockito.times(1)).onMapSearch();
    }
}
