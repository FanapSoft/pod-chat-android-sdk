package com.example.podchat;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.util.TextMessageType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;


@RunWith(AndroidJUnit4.class)
public class HashTagCacheTest {

    public static final boolean CACHE = true;
    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;

    @Mock
    private Activity activity;
    private static Context appContext;

    private static String serverName = "chat-server";
    private static String appId = "POD-Chat";
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String NAME = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);
    private static String TOKEN = "5ac67f162bda4ac69d6d489079b97022";

    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;

    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);


    final ArrayList<Thread> threads = new ArrayList<>();
    final ArrayList<MessageVO> threadHashTagMessagesListInCache = new ArrayList<>();
    final ArrayList<MessageVO> threadHashTagMessagesListInServer = new ArrayList<>();

    @BeforeClass
    public static void initial() {
        appContext = InstrumentationRegistry.getTargetContext();

        chat = Chat.init(appContext);
        Looper.prepare();
    }

    @Before
    public void createChat() {


        view = Mockito.mock(ChatContract.view.class);

        chatActivity = chatActivityRule.getActivity();
        presenter = new ChatPresenter(appContext, view, chatActivity);

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

        chat.isCacheables(CACHE);

        pauseProcess();


    }

    public void sendHashTagMessage(long threadId) {
        chatListeners = new ChatListener() {

            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {
                resumeProcess();
            }
        };

        RequestMessage request =
                new RequestMessage.Builder("#test", threadId)
                        .messageType(TextMessageType.Constants.TEXT)
                        .build();

        chat.setListener(chatListeners);
        chat.sendTextMessage(request, null);
        pauseProcess();
        sleep(6000);
    }

    public void populateHashTagListFromServer(long threadId) {
        threadHashTagMessagesListInServer.clear();
        chatListeners = new ChatListener() {

            @Override
            public void onGetHashTagList(ChatResponse<ResultHistory> response) {
                if (!response.isCache()) {
                    System.out.println("Received HashTag Message List Server: " + response.getJson());
                    threadHashTagMessagesListInServer.addAll(response.getResult().getHistory()
                            .stream()
                            .filter(messageVO ->
                                    messageVO.getMessage() != null)
                            .collect(Collectors.toList()));

                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.setListener(chatListeners);

        RequestGetHashTagList request = new RequestGetHashTagList
                .Builder(threadId)
                .offset(0)
                .count(25)
                .setHashtag("test")
                .build();
        presenter.getHashTagList(request, null);

        pauseProcess();
        System.out.println("Received HashTag Message List Server: " + threadHashTagMessagesListInServer.size());
    }

    public void populateHashTagListFromCache(long threadId) {
        threadHashTagMessagesListInCache.clear();
        chatListeners = new ChatListener() {
            @Override
            public void onGetHashTagList(ChatResponse<ResultHistory> response) {
                if (response.isCache()) {
                    System.out.println("Received HashTag Message List Server: " + response.getJson());
                    threadHashTagMessagesListInCache.addAll(response.getResult().getHistory()
                            .stream()
                            .filter(messageVO ->
                                    messageVO.getMessage() != null)
                            .collect(Collectors.toList()));

                    chat.removeListener(chatListeners);
                    resumeProcess();
                } else {
                    Assert.assertEquals(5, 10);
                    resumeProcess();
                }
            }
        };

        chat.setListener(chatListeners);


        RequestGetHashTagList request = new RequestGetHashTagList
                .Builder(threadId)
                .offset(0)
                .count(25)
                .setHashtag("test")
                .build();

        presenter.getHashTagList(request, null);

        pauseProcess();
        System.out.println("Received HashTag Message List Server: " + threadHashTagMessagesListInCache.size());

    }

    @Test
    public void sendMessageAndTestIfHashTagListAreSame() {
        populateMessageFromServer();
        assert threads.size() > 0;
        Thread thread = threads.get(0);
        long threadId = thread.getId();

//        long threadId = 60512;
        System.out.println("Received ThreadId: " + threadId);
        populateHashTagListFromServer(threadId);
        sleep(2000);
        populateHashTagListFromCache(threadId);
        checkHashTagsAreSameInCacheAndServer();

        sendHashTagMessage(threadId);


    }

    public void testIfHashTagListAreSame() {
        populateMessageFromServer();
        assert threads.size() > 0;
        Thread thread = threads.get(0);
        long threadId = thread.getId();

        System.out.println("Received ThreadId: " + threadId);
        populateHashTagListFromServer(threadId);
        populateHashTagListFromCache(threadId);
        checkHashTagsAreSameInCacheAndServer();
    }


    public void checkHashTagsAreSameInCacheAndServer() {
        Assert.assertEquals(threadHashTagMessagesListInCache.size(), threadHashTagMessagesListInServer.size());
        Assert.assertEquals(threadHashTagMessagesListInCache.size() > 0, threadHashTagMessagesListInServer.size() > 0);


        for (MessageVO msg :
                threadHashTagMessagesListInServer) {

            if (msg.getMessage() == null) continue;

            MessageVO msgInCache = threadHashTagMessagesListInCache.stream().filter(messageVO -> messageVO.getId() == msg.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Server HashTag Message " + msg.getMessage());
            System.out.println(">>>>>>>>>>> Cache HashTag Message " + msgInCache.getMessage());

            Assert.assertEquals(msg.getMessage(), msgInCache.getMessage());
            Assert.assertEquals(msg.getId(), msgInCache.getId());
        }

    }



    public void populateMessageFromServer() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> response) {
                if (!response.isCache()) {
                    System.out.println("Received List: " + content);
                    for (Thread thread : response.getResult().getThreads()) {
                        if (thread.isGroup()) {
                            threads.add(thread);
                        }
                    }
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }

            }
        };

        chat.setListener(chatListeners);

        RequestThread requestThread =
                new RequestThread.Builder()
                        .count(25)
                        .withNoCache()
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

    private void sleep(int i) {
        try {
            java.lang.Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
