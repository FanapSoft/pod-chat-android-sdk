package com.example.podchat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.FlakyTest;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.InviteType;
import com.orhanobut.logger.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;


@RunWith(AndroidJUnit4.class)
public class ChatTestIntegration extends ChatAdapter {

    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;


    @Mock
    private Activity activity;
    private static Context appContext;

    private static String appId = "POD-Chat";


    private static String NAME = BaseApplication.getInstance().getString(R.string.test_server_name);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.test_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.test_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.test_fileServer);
    private static String TOKEN = BaseApplication.getInstance().getString(R.string.Ahmad_Sajadi);
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    private static String serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);


    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;


    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);


    @BeforeClass
    public static void initial() {


        appContext = InstrumentationRegistry.getTargetContext();

        chat = Chat.init(appContext);

//        chat.isCacheables(true);
//
//        chat.isLoggable(true);
//        chat.rawLog(true);
//        chat.isSentryLogActive(true);
//        chat.isSentryResponseLogActive(true);
//
//        chat.setDownloadDirectory(appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
//
//        TimeoutConfig timeout = new TimeoutConfig()
//                .newConfigBuilder()
//                .withConnectTimeout(30, TimeUnit.SECONDS)
//                .withWriteTimeout(30, TimeUnit.MINUTES)
//                .withReadTimeout(30, TimeUnit.MINUTES)
//                .build();
//
//
//        chat.setUploadTimeoutConfig(timeout);
//
//        chat.setDownloadTimeoutConfig(timeout);
//
//        NetworkPingSender.NetworkStateConfig build = new NetworkPingSender.NetworkStateConfig()
//                .setHostName("msg.pod.ir")
//                .setPort(443)
//                .setDisConnectionThreshold(2)
//                .setInterval(7000)
//                .setConnectTimeout(10000)
//                .build();


//        TimeoutConfig uploadConfig = new TimeoutConfig()
//                .newConfigBuilder()
//                .withConnectTimeout(120, TimeUnit.MINUTES)
//                .withWriteTimeout(120, TimeUnit.MINUTES)
//                .withReadTimeout(120, TimeUnit.MINUTES)
//                .build();
//
//        TimeoutConfig downloadConfig = new TimeoutConfig()
//                .newConfigBuilder()
//                .withConnectTimeout(20, TimeUnit.SECONDS)
//                .withWriteTimeout(0, TimeUnit.SECONDS)
//                .withReadTimeout(5, TimeUnit.MINUTES)
//                .build();

//        chat.setNetworkStateConfig(build);
//
//        chat.setUploadConfig(uploadConfig);
//
//        chat.setDownloadConfig(downloadConfig);


    }

    @Before
    public void createChat() {
        Looper.prepare();
        MockitoAnnotations.initMocks(this);

        RequestConnect rc = new RequestConnect.Builder(
                socketAddress,
                APP_ID,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer,
                "podSpaceServer")
                .build();


        chatListeners = new ChatListener() {
            @Override
            public void onChatState(String state) {
                if (state.equals(CHAT_READY)) {
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        chat.connect(rc);

        pauseProcess();


    }

    @Before
    public void setUp() {
//        Looper.prepare();
//        appContext = InstrumentationRegistry.getTargetContext();
//        MockitoAnnotations.initMocks(this);
        chatActivity = chatActivityRule.getActivity();
        presenter = new ChatPresenter(appContext, view, chatActivity);

//        RequestConnect rc = new RequestConnect.Builder(
//                socketAddress,
//                APP_ID,
//                serverName,
//                "f29512343de1472fa15d1e497e264c54",
//                ssoHost,
//                platformHost,
//                fileServer,
//                "podSpaceServer")
//                .build();

//        presenter.connect(rc);

    }

    final ArrayList<Thread> threads = new ArrayList<>();


    @Test
    public void populateThreadsListFromServerOrCache() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                System.out.println("Received List: " + content);
                threads.addAll(thread.getResult().getThreads());
                resumeProcess();
            }
        };

        chat.addListener(chatListeners);

        RequestThread requestThread =
                new RequestThread.Builder()
                        .count(25)
                        .build();

        presenter.getThreads(requestThread, null);

        pauseProcess();
        System.out.println("Received List: " + threads.size());

    }


    private void resumeProcess() {
        synchronized (sync) {
            sync.notify();
        }
    }

    private void pauseProcess() {
        synchronized (sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    @After
//    public void closeChat() {
//        if (chat != null) {
//            chat.closeChat();
//        } else if (presenter != null) {
//            presenter.closeChat();
//        }
//    }


    @Test
    public void chatListeners() {

        ChatListener listener1 = new ChatListener() {
            @Override
            public void onSent(String content, ChatResponse<ResultMessage> response) {

            }
        };
        chat.addListener(listener1);
        ChatListener listener2 = new ChatListener() {
            @Override
            public void onSent(String content, ChatResponse<ResultMessage> response) {

            }
        };
        chat.addListener(listener2);
        ChatListener listener3 = new ChatListener() {
            @Override
            public void onSent(String content, ChatResponse<ResultMessage> response) {

            }
        };
        chat.addListener(listener3);

        chat.removeListener(listener2);

        Assert.assertFalse(chat.getListeners().contains(listener2));

    }


    @Test
    @LargeTest
    public void getUserRolesInThread() {

        populateThreadsListFromServerOrCache();

        for (Thread t : threads) {
            if (t.getAdmin()) {
                System.out.println("Get roles in " + t.getId());
                getCurrentUserRoles(t.getId());
                break;
            }
        }
        System.out.println("** Get roles in " + threads.get(0).getId());
        getCurrentUserRoles(threads.get(0).getId());

    }

    public void getCurrentUserRoles(long threadID) {

        RequestGetUserRoles req = new RequestGetUserRoles.Builder()
                .setThreadId(threadID)
                .build();

        presenter.getUserRoles(req);

        sleep(1000);

        Mockito.verify(view, Mockito.atLeastOnce()).onGetCurrentUserRoles(Mockito.any());


    }


    @Test
    @LargeTest
    public void getThreadHistoryIns() {
        populateThreadsListFromServerOrCache();
        System.out.println("** Get history of " + threads.get(0).getId());
        for (Thread thread :
                new ArrayList<>(threads)) {
            getThreadHistory(thread.getId());
        }
//        getThreadHistory(threads.get(0).getId());
    }


    public void getThreadHistory(long threadId) {
        threadId = 8093;
        RequestGetHashTagList request = new RequestGetHashTagList
                .Builder(threadId)
                .setHashtag("test")
                .offset(0)
                .count(25)
                .build();
        presenter.getHashTagLIst(request, new ChatHandler() {
            @Override
            public void onGetHistory(String uniqueId) {
                super.onGetHistory(uniqueId);
            }
        });

    }


    @Test
    @LargeTest
    //get 25 message before and after last seen
    //messages NanoTimes should be lower than last seen in first case
    //and greater in second case
    public void getThreadHistoryBeforeAndAfterLastSeenMessage() {

        populateThreadsListFromServerOrCache();
        System.out.println("** Get history of " + threads.get(0).getTitle());

        Thread thread = threads.get(0);

        final long lastSeen = thread.getLastSeenMessageTime() + thread.getLastSeenMessageNanos();

        AtomicInteger numOfCacheResp = new AtomicInteger(0);

        ChatListener historyListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
                int invokeTimes = 0;
                if (history.isCache()) {
                    invokeTimes = numOfCacheResp.getAndIncrement();
                }
                checkTimesIsValid(history, invokeTimes, lastSeen);
                if (invokeTimes >= 2) {
                    resumeProcess();
                }
            }
        };
        chat.addListener(historyListeners);
        RequestGetHistory requestGetHistoryBeforeLastSeenTime = new RequestGetHistory
                .Builder(thread.getId())
                .toTimeNanos(lastSeen)
                .offset(0)
                .count(25)
                .order("desc")
                .build();

        presenter.getHistory(requestGetHistoryBeforeLastSeenTime, null);

        RequestGetHistory requestGetHistoryAfterLastSeenTime = new RequestGetHistory
                .Builder(thread.getId())
                .fromTimeNanos(lastSeen)
                .offset(0)
                .count(25)
                .order("asc")
                .build();
        presenter.getHistory(requestGetHistoryAfterLastSeenTime, null);

        pauseProcess();
        sleep(2000);
        Mockito.verify(view, Mockito.atLeast(2)).onGetThreadHistory(Mockito.any());

    }


    @Test
    @LargeTest
    //get threads histories one by one
    //performance should be acceptable
    public void getAllThreadsHistories() {
        populateThreadsListFromServerOrCache();
        long startTime = System.currentTimeMillis();
        for (Thread thread :
                new ArrayList<>(threads)) {
            System.out.println("NEXT: " + thread.getTitle());
            System.out.println(thread.getId());
            getThreadFullHistory(thread);
        }
//        getThreadFullHistory(threads.get(0));
        long endTime = System.currentTimeMillis();
        Assert.assertTrue(true);
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println("TEST IS DONE FOR ");
        System.out.println(threads.size());
        System.out.println("THREADS IN");
        System.out.println(endTime - startTime + " MILLISECONDS");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");

    }


    @Test
    @LargeTest
    public void setHashTagTest() {

        ChatListener historyListeners = new ChatListener() {

            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
                System.out.println("onGetHistory: ");
                Logger.json(history.getJson());
                Assert.assertTrue(true);
                resumeProcess();
            }

            @Override
            public void onSent(String content, ChatResponse<ResultMessage> response) {
                System.out.println("onSent: ");
                Logger.json(response.getJson());
                Assert.assertTrue(true);
                resumeProcess();
            }
        };

        chat.addListener(historyListeners);

        RequestMessage requestMessage = new RequestMessage
                .Builder("#android fragment", 8085)

                .build();
        presenter.sendTextMessage(requestMessage, null);
        pauseProcess();
    }

    @Test
    @LargeTest
    public void getHistoryHashtagTest() {
        long startTime = System.currentTimeMillis();

        ChatListener historyListeners = new ChatListener() {

            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
                System.out.println("onGetHistory: ");
                Logger.json(history.getJson());
                Assert.assertTrue(true);
                resumeProcess();
            }


        };
        chat.setListener(historyListeners);
        RequestGetHistory request = new RequestGetHistory
                .Builder(8085)
                .offset(0)
                .count(25)
                .order("desc")
                .build();

        presenter.getHistory(request, null);
        pauseProcess();

    }

    @Test
    @LargeTest
    public void registerAssistantsTest() {

        ChatListener historyListeners = new ChatListener() {
            @Override
            public void onRegisterAssistant(ChatResponse<List<AssistantVo>> response) {

                System.out.println("onRegisterAssistant: " + response.getJson());
                Assert.assertTrue(true);
                resumeProcess();
            }
        };

        chat.addListener(historyListeners);

        registerAssistant();

    }

    @Test
    @LargeTest
    public void getAssistantTest() {
        long startTime = System.currentTimeMillis();

        ChatListener historyListeners = new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {
                System.out.println("onGetAssistants: " + response);
                Assert.assertTrue(true);
                resumeProcess();
            }
        };

        chat.addListener(historyListeners);

        getAssistants();


    }

    @Test
    @LargeTest
    public void deActiveAssistantTest() {
        long startTime = System.currentTimeMillis();

        ChatListener historyListeners = new ChatListener() {
            @Override
            public void onDeActiveAssistant(ChatResponse<List<AssistantVo>> response) {
                System.out.println("onDeActiveAssistant: " + response);
                Assert.assertTrue(true);
                resumeProcess();
            }
        };

        chat.addListener(historyListeners);

        deActiveAssistant();

    }

    private void getAssistants() {
        GetAssistantRequest request = new GetAssistantRequest.Builder().typeCode("default").build();

        presenter.getAssistants(request);
        pauseProcess();
    }

    private void deActiveAssistant() {

        //invite
        Invitee invite = new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVos.add(assistantVo);


        DeActiveAssistantRequest request = new DeActiveAssistantRequest.Builder(assistantVos).build();
        presenter.deActiveAssistant(request);
        pauseProcess();
    }

    private void registerAssistant() {
        //63253 kheirkhah
        //63254 sajadi
        ////63255 anvari
        //63256 amjadi
        //63257 zhiani
        //invite list
        Invitee invite = new Invitee("63253", InviteType.Constants.TO_BE_USER_CONTACT_ID);

        //roles
        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);
        typeRoles.add(RoleType.Constants.ADD_ROLE_TO_USER);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setInvitees(invite);
        assistantVo.setContactType("default");
        assistantVo.setRoles(typeRoles);

        assistantVos.add(assistantVo);

        RegisterAssistantRequest request = new RegisterAssistantRequest.Builder(assistantVos).build();

        presenter.registerAssistant(request);
        pauseProcess();
    }

    private void getThreadFullHistory(Thread thread) {


        long startTime = System.currentTimeMillis();

        AtomicBoolean hasNext = new AtomicBoolean(true);
        int count = 25;
        int offset = 0;
        AtomicLong threadMessagesCount = new AtomicLong(-1);
        AtomicLong threadReceivedHistory = new AtomicLong(0);
        ChatListener historyListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                threadMessagesCount.set(history.getResult().getContentCount());
                long received = threadReceivedHistory.get();
                threadReceivedHistory.set(received + history.getResult().getHistory().size());
                hasNext.set(history.getResult().isHasNext());
                resumeProcess();
            }
        };
        chat.addListener(historyListeners);

        while (hasNext.get()) {
            RequestGetHistory requestGetHistory = new RequestGetHistory
                    .Builder(thread.getId())
                    .offset(offset)
                    .count(count)
                    .order("desc")
                    .build();
            String uniqueId = presenter.getHistory(requestGetHistory, null);
            offset = offset + count;
            pauseProcess();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println("RUNNING TEST ON THREAD:");
        System.out.println(thread.getTitle());
        System.out.println(thread.getId());
        System.out.println("RECEIVED TOTAL " + threadReceivedHistory + " MESSAGES OF HISTORY IN");
        System.out.println(endTime - startTime + " MILLISECONDS");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
        System.out.println(">>> >>> >>>");
//        Assert.assertEquals(threadMessagesCount.get(), threadReceivedHistory.get());


    }

    private void checkTimesIsValid(ChatResponse<ResultHistory> history, int invokeTimes, long lastSeen) {
        switch (invokeTimes) {
            case 1: {
                for (MessageVO message :
                        history.getResult().getHistory()) {
                    Assert.assertTrue(message.getTimeNanos() <= lastSeen);
                }
                break;
            }
            case 2: {
                for (MessageVO message :
                        history.getResult().getHistory()) {
                    Assert.assertTrue(message.getTimeNanos() >= lastSeen);
                }
                break;
            }
        }
    }

    //    @Test
//    @LargeTest
//    public void updateUserProfile() {
//
//
//        sleep(10000);
//
//        RequestUpdateProfile request = new RequestUpdateProfile
//                .Builder("عیب رندان مکن ای زاهد پاکیزه سرشت")
//                .build();
//
//        chat.updateChatProfile(request);
//
//        sleep(1000);
//
//        Mockito.verify(chatListeners, Mockito.atLeastOnce()).onChatProfileUpdated(Mockito.any());
//
//
//        Assert.assertTrue(true);
//
//
//    }
    @Test
    @LargeTest
    public void updateUserProfile() {


        String bio = "عیب رندان مکن ای زاهد پاکیزه سرشت";
        RequestUpdateProfile request = new RequestUpdateProfile
                .Builder(bio)
                .build();
//
//        presenter.updateChatProfile(request);
//
//        sleep(1000);

        chat.updateChatProfile(request);

        sleep(500);

        ChatResponse<ResultUpdateProfile> response = new ChatResponse<>();
        ResultUpdateProfile result = new ResultUpdateProfile();
        result.setBio(bio);
        response.setResult(result);

        Mockito.verify(view, Mockito.atLeastOnce()).onChatProfileUpdated(Mockito.any());

    }

    @Test
    @LargeTest
    public void getUserInfo() {

        presenter.getUserInfo(null);

        sleep(500);
        Mockito.verify(view, Mockito.atMost(3)).onGetUserInfo(Mockito.any());


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

        sleep(500);

        presenter.getCacheSize();

        sleep(500);

        presenter.clearDatabaseCache(new Chat.IClearMessageCache() {
            @Override
            public void onCacheDatabaseCleared() {

            }

            @Override
            public void onExceptionOccurred(String cause) {

            }
        });

        sleep(500);

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

        RequestGetUserRoles req = new RequestGetUserRoles.Builder()
                .setThreadId(5801)
                .build();

        chat.getCurrentUserRoles(req);

        sleep(1000);

//        Mockito.verify(view, Mockito.atLeastOnce()).onGetCurrentUserRoles(Mockito.any());
        Mockito.verify(chatListeners, Mockito.atLeastOnce()).onError(Mockito.any(), Mockito.any());


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
            java.lang.Thread.sleep(i);
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
//        presenter.getThreads(10, null, null, null, null);
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
        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory(Mockito.any());
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
        presenter.renameThread(9162, "5 گروه خودمونی ما", null);
        sleep(3000);
        Mockito.verify(view, Mockito.times(1)).onRenameGroupThread();
    }

    @Test
    @MediumTest
    public void addContact() {
        sleep(5000);
        presenter.addContact("maman", "sadeghi", "091224858169", "dev55@gmail.com", "");
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
            public void onProgressUpdate(int progress) {
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
//        sleep(5000);
//        List<Long> participantIds = new ArrayList<>();
//        participantIds.add(824L);
//        participantIds.add(577L);
//
//        sleep(5000);
//        Mockito.verify(view, Mockito.times(1)).onAddParticipant();
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
//        presenter.getThreads(20, 0L, null, "FiFi", null);
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
