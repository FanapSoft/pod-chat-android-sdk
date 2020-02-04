package com.example.podchat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.FlakyTest;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.util.ChatMessageType;

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

    private static String serverName = "chat-server";
    private static String appId = "POD-Chat";
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String NAME = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);
    private static String TOKEN = "d1fd97824200469c8fedf8c1d010d829";


    private ChatActivity chatActivity;

    @Before
    public void setUp() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        presenter = new ChatPresenter(appContext, view, chatActivity);
        presenter.connect(socketAddress,
                appId, serverName, TOKEN, ssoHost,
                platformHost, fileServer, "");
    }


    @Test
    public void getRoutsSize() {

        long totalSize = presenter.getStorageSize();

        System.out.println(totalSize);

        long imagesSize = presenter.getImageFolderSize();

        System.out.println(imagesSize);

        long filesSize = presenter.getFilesFolderSize();

        System.out.println(filesSize);

        boolean imagesClear = presenter.clearPictures();

        System.out.println(imagesClear);

        boolean clearFiles = presenter.clearFiles();

        System.out.println(clearFiles);

        Assert.assertEquals(0, presenter.getFilesFolderSize());

        Assert.assertEquals(0, presenter.getImageFolderSize());



    }


    @Test
    public void clearDataBase() {

        sleep(10000);

        presenter.getCacheSize();

        sleep(2000);

        presenter.clearDatabaseCache(new Chat.IClearMessageCache() {
            @Override
            public void onCacheDatabaseCleared() {

            }

            @Override
            public void onExceptionOccurred(String cause) {

            }
        });

        sleep(5000);

        presenter.getCacheSize();

    }


    @Test
    public void getDatabaseSize() {

        sleep(10000);

        presenter.getCacheSize();

    }

    @Test
    @LargeTest
    public void getCurrentUserRoles() {


        sleep(25000);

        RequestGetUserRoles req = new RequestGetUserRoles.Builder()
                .setThreadId(5801)
                .build();

        presenter.getUserRoles(req);

        sleep(1000);

        Mockito.verify(view, Mockito.atLeastOnce()).onGetCurrentUserRoles(Mockito.any());


    }


    @Test
    @MediumTest
    public void getUserInfo() {
        sleep(5000);
        presenter.getUserInfo(null);
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onGetUserInfo();

    }


    @Test
    @FlakyTest
    public void startTyping() {

        sleep(7000);

        RequestSignalMsg req = new RequestSignalMsg.Builder()
                .signalType(ChatMessageType.SignalMsg.IS_TYPING)
                .threadId(6630)
                .build();

        presenter.startTyping(req);

    }


    @Test
    @MediumTest
    public void pinMessage() {

        sleep(25000);

        RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                .setMessageId(76306)
                .setNotifyAll(true)
                .build();

        presenter.pinMessage(requestPinMessage);
        sleep(1000);


        Mockito.verify(view, Mockito.times(1)).onPinMessage(Mockito.any());


    }

    @Test
    @MediumTest
    public void unpinMessage() {

        sleep(25000);

        RequestPinMessage requestPinMessage = new RequestPinMessage.Builder()
                .setMessageId(76306)
                .build();

        presenter.unPinMessage(requestPinMessage);
        sleep(1000);


        Mockito.verify(view, Mockito.times(1)).onUnPinMessage(Mockito.any());


    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);

    }

    @Test
    @MediumTest
    public void createThreadWithMetaData() {
        sleep(7000);


        Invitee[] invite = new Invitee[]{new Invitee("589", 2)
                , new Invitee("1162", 2)
                , new Invitee("2404", 2)
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
        sleep(7000);
        presenter.getThreads(10, null, null, null, null);
        sleep(7000);
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
        sleep(7000);
        ArrayList<Integer> threadIds = new ArrayList<>();
//        threadIds.add(1105);
//        threadIds.add(1031);
        long count = 5;
        long offset;
        long creatorCoreUserId = 2;
        presenter.getThreads(null, null, null, null, creatorCoreUserId
                , 0, 0, null);
        sleep(7000);
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
        sleep(3000);
        History history = new History.Builder().count(5).build();
        presenter.getHistory(history, 381, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory();
    }


    @Test
    @MediumTest
    public void getContacts() {
        sleep(3000);
        presenter.getContact(50, 0L, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onGetContacts();
    }

    @Test
    @MediumTest
    public void getThreadParticipant() {
        sleep(3000);
        presenter.getThreadParticipant(10, 0L, 352, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onGetThreadParticipant();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnSent() {
        presenter.sendTextMessage("this is test", 381, 5, null, null);
        view.onSentMessage();
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
    }

    @Test
    @MediumTest
    public void sendTestMessageOnDeliver() {
        sleep(5000);
        presenter.sendTextMessage("this is test", 381, null, null, null);
        view.onSentMessage();
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onGetDeliverMessage();
    }

    @Test
    @LargeTest
    public void sendTestMessageOnSeen() {
        sleep(3000);
        presenter.sendTextMessage("this is test", 381, null, "name", null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onGetSeenMessage();
    }


    @Test
    @MediumTest
    public void editMessage() {
        view.onSentMessage();
        sleep(5000);
        presenter.sendTextMessage("this is test", 381, 2, "", null);
        Mockito.verify(view, Mockito.times(1)).onSentMessage();
        sleep(3000);
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
        sleep(5000);
        Invitee[] invite = new Invitee[]{new Invitee("566", 2)};
        presenter.createThread(0, invite, "yes", "first description", null, null, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onCreateThread();
    }

    @Test
    @MediumTest
    public void muteThread() {
        sleep(5000);
        presenter.muteThread(381, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onMuteThread();
    }

    @Test
    @MediumTest
    public void unMuteThread() {
        sleep(5000);
        presenter.unMuteThread(352, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onUnMuteThread();
    }

    @Test
    @MediumTest
    public void replyMessage() {
        sleep(5000);

        presenter.replyMessage("this is reply to all of you at" + new Date().getTime()
                , 381, 14103, null, null);
        sleep(3000);
//        Mockito.verify(view, Mockito.times(1)).
    }

    //fifi is the admin of groupId = 632
    @Test
    @MediumTest
    public void renameThread() {
        sleep(3000);
        presenter.renameThread(632, "5_گروه خودمونی", null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onRenameGroupThread();
    }

    @Test
    @MediumTest
    public void addContact() {
        sleep(5000);
        presenter.addContact("maman", "sadeghi", "091224858169", "dev55@gmail.com");
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onAddContact();
//        Mockito.verify(view,Mockit.)

    }

    //    fifiUser
    @Test
    @MediumTest
    public void updateContact() {
        sleep(3000);
        presenter.updateContact(861, "masoud", "rahimi2", "09122488169"
                , "dev@gmail.com");
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onUpdateContact();
    }

    @Test
    @MediumTest
    public void removeContact() {
        sleep(3000);
        presenter.removeContact(861);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onRemoveContact();
    }

    @Test
    @MediumTest
    public void uploadFile() {
        sleep(3000);
        Uri uri = Uri.parse("content://com.android.providers.downloads.documents/document/230");
        String fileUri = "/storage/emulated/0/Download/Manager.v6.31.Build.3.Crack.Only_pd.zip";
        presenter.uploadFile(activity, uri);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onUploadFile();
    }

    @Test
    @MediumTest
    public void UploadImageFile() {
        sleep(3000);
        Uri uri = Uri.parse("content://media/external/images/media/781");
        presenter.uploadImage(activity, uri);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onUploadImageFile();
    }

    @Test
    @MediumTest
    public void uploadProgressImage() {
        sleep(3000);
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
        sleep(3000);
        presenter.spam(0L);

    }

    @Test
    @MediumTest
    public void syncContact() {
        sleep(3000);
        presenter.syncContact(activity);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onAddContact();
    }

    @Test
    @MediumTest
    public void AddParticipant() {
        sleep(5000);
        List<Long> participantIds = new ArrayList<>();
        participantIds.add(824L);
        participantIds.add(577L);
        presenter.addParticipants(691, participantIds, null);
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onAddParticipant();
    }

    @Test
    @MediumTest
    public void removeParticipant() {
        sleep(5000);
        List<Long> contactIds = new ArrayList<>();
        contactIds.add(123L);
        contactIds.add(121L);
        presenter.removeParticipants(691, contactIds, null);
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onRemoveParticipant();
    }

    @Test
    @MediumTest
    public void leaveThread() {
        sleep(5000);
        presenter.leaveThread(657, null);
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onLeaveThread();
    }

    @Test
    @MediumTest
    public void deleteMessage() {
        sleep(5000);


        ArrayList<Long> messageIds = new ArrayList<>();
        messageIds.add(14380L);

        presenter.deleteMessage(messageIds, 0L, true, null);
        sleep(5000);
        Mockito.verify(view, Mockito.times(1)).onDeleteMessage();
    }

    //getConversationVO possibility test

    @Test
    @MediumTest
    public void searchInThreads() {
        sleep(3000);
        presenter.getThreads(20, 0L, null, "FiFi", null);
        sleep(3000);
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
        sleep(3000);
//        presenter.block(575L, null);

        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onBlock();
    }

    @Test
    @MediumTest
    public void unBlock() {
        sleep(3000);
//        presenter.unBlock(62L, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onUnblock();
    }

    @Test
    @MediumTest
    public void searchContact() {

        sleep(3000);
        RequestSearchContact requestSearchContact = new RequestSearchContact.Builder("0", "2").id("1063").build();
        presenter.searchContact(requestSearchContact);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onSearchContact();
    }

    @Test
    @MediumTest
    public void searchHistory() {

    }

    @Test
    @MediumTest
    public void getBlockList() {
        sleep(3000);
        presenter.getBlockList(null, null, null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).ongetBlockList();
    }

    @Test
    @MediumTest
    public void routingMap() {
        sleep(3000);
        presenter.mapRouting("35.7003510,51.3376472", "35.7343510,50.3376472");
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onMapRouting();
    }

    @Test
    @MediumTest
    public void searchMap() {
        sleep(3000);
        presenter.mapSearch("میدان آزادی", 35.7003510, 51.3376472);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onMapSearch();
    }
}
