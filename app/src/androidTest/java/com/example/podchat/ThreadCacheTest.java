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
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.util.TextMessageType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;


@RunWith(AndroidJUnit4.class)
public class ThreadCacheTest {

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
    private static String TOKEN = "8c1c715aa4084a9e91d41f89f965d8b2";

    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;

    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);

    final ArrayList<Thread> threadsInServer = new ArrayList<>();
    final ArrayList<Thread> threadsInCache = new ArrayList<>();

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

    @Test
    public void testThreadsAreSame() {
        populateThreadsFromServer();
        populateThreadsFromCache();
        checkThreadsAreSameInCacheAndServer();
    }

    @Test
    public void testThreadMessagesAreSame() {

    }

    @Test
    public void testThreadInfoAreSameAfterDeleteMessage() {
        deleteMessage();
        populateThreadsFromServer();
        populateThreadsFromCache();
        checkThreadsAreSameInCacheAndServer();
    }

    @Test
    public void testThreadInfoAreSameAfterSendNewMessage() {
        sendMessage();
        populateThreadsFromServer();
        populateThreadsFromCache();
        checkThreadsAreSameInCacheAndServer();
    }


    public void deleteMessage() {
        populateThreadsFromServer();
        Collections.shuffle(threadsInServer);
        Thread thread = threadsInServer.get(0);
        Assert.assertNotNull(thread);
        ArrayList<Long> msgIds = new ArrayList<>();

        //send new message
        chatListeners = new ChatListener() {
            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {
                resumeProcess();
                msgIds.add( response.getResult().getMessageVO().getId());
                System.out.println("new message sent -> msg : " + content);
                chat.removeListener(chatListeners);
            }

            @Override
            public void onError(String content, ErrorOutPut error) {
                resumeProcess();
                System.out.println("Error: " + content);
                Assert.assertEquals(0, 1);
                chat.removeListener(chatListeners);
            }
        };
        chat.addListener(chatListeners);


        RequestMessage request =
                new RequestMessage.Builder("Message for test delete :" + thread.getId(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .build();

        chat.sendTextMessage(request, null);
        pauseProcess();

        //delete last message
        chatListeners = new ChatListener() {
            @Override
            public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> response) {
                resumeProcess();
                System.out.println("Deleted: " + content);
                chat.removeListener(chatListeners);
            }

            @Override
            public void onError(String content, ErrorOutPut error) {
                resumeProcess();
                System.out.println("Error: " + content);
                Assert.assertEquals(0, 1);
                chat.removeListener(chatListeners);
            }
        };
        chat.addListener(chatListeners);

        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder()
                .messageIds(msgIds)
                .deleteForAll(true)
                .build();

        presenter.deleteMessage(requestDeleteMessage, null);
        pauseProcess();


    }

    public void sendMessage() {
        populateThreadsFromServer();
        Collections.shuffle(threadsInServer);
        Thread thread = threadsInServer.get(0);
        System.out.println("Thread : " + thread.getTitle());
        Assert.assertNotNull(thread);
        chatListeners = new ChatListener() {
            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {
                resumeProcess();
                chat.removeListener(chatListeners);
            }

            @Override
            public void onError(String content, ErrorOutPut error) {
                resumeProcess();
                System.out.println("Error: " + content);
                Assert.assertEquals(0, 1);
                chat.removeListener(chatListeners);
            }
        };
        chat.addListener(chatListeners);


        RequestMessage request =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .build();

        chat.sendTextMessage(request, null);


    }

    public void populateThreadsFromServer() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
                assert thread.getResult().getThreads().size() > 0;
                if (!thread.isCache()) {
                    threadsInServer.addAll(new ArrayList<>(thread.getResult().getThreads()));
                    chat.removeListener(chatListeners);
                }

            }
        };

        chat.addListener(chatListeners);

        RequestThread requestThread =
                new RequestThread.Builder()
                        .count(25)
                        .build();

        presenter.getThreads(requestThread, null);

        long t1 = System.currentTimeMillis();
        sleep(2500);
        long t2 = System.currentTimeMillis();
        System.out.println("Received List: " + threadsInServer.size() + " after: " + (t2 - t1) + " ms");
    }

    public void populateThreadsFromCache() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
                if (thread.isCache()) {
                    threadsInCache.addAll(thread.getResult().getThreads());
                    chat.removeListener(chatListeners);
                } else
                    Assert.assertEquals(0, 1);
                resumeProcess();
            }
        };

        chat.addListener(chatListeners);


        RequestThread requestThread =
                new RequestThread.Builder()
                        .count(25)
                        .build();
        presenter.getThreads(requestThread, null);

        long t1 = System.currentTimeMillis();
        pauseProcess();
        long t2 = System.currentTimeMillis();
        System.out.println("Received List: " + threadsInCache.size() + " after: " + (t2 - t1) + " ms");
    }

    public void checkThreadsAreSameInCacheAndServer() {

        for (Thread threadInServer :
                threadsInServer) {

            if (threadInServer == null) continue;

            Thread threadInCache = threadsInCache.stream().filter(thread -> thread.getId() == threadInServer.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Thread in server " + threadInServer.getTitle());
            System.out.println(">>>>>>>>>>> Thread in cache " + threadInCache.getTitle());

            Assert.assertEquals(threadInServer.getId(), threadInCache.getId());
            System.out.println(">>>>>>>>>>> Threads are same " + threadInCache.getTitle() + " threadId : " + threadInCache.getId());
            Assert.assertNotNull(threadInServer.getLastMessageVO());
            Assert.assertNotNull(threadInCache.getLastMessageVO());
            System.out.println(">>>>>>>>>>> Last message in cache " + threadInCache.getLastMessageVO().getMessage());
            System.out.println(">>>>>>>>>>> Last message in server " + threadInServer.getLastMessageVO().getMessage());
            Assert.assertEquals(threadInCache.getLastMessage(), threadInCache.getLastMessageVO().getMessage());
        }
    }

    public void checkThreadMessagesAreSameInCacheAndServer() {

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
