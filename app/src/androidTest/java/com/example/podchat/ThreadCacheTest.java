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
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestThread;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
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
    private static String TOKEN = "e94904e70bd04c9f8c58a4c0f4d7ff40";

    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;

    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);


    final ArrayList<Thread> threads = new ArrayList<>();

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
        populateThreadsFromServer();
        populateThreadsFromCache();
        checkThreadMessagesAreSameInCacheAndServer();
    }

    @Test
    public void testThreadInfoAreSameAfterDeleteMessage() {
        populateThreadsFromServer();
        populateThreadsFromCache();
        deleteMessage();
        checkThreadInfoAreSameInCacheAndServer();
    }

    @Test
    public void testThreadInfoAreSameAfterSendNewMessage() {
        populateThreadsFromServer();
        populateThreadsFromCache();
        sendNewMesage();
        checkThreadInfoAreSameInCacheAndServer();
    }

    @Test
    public void testThreadInfoAreSameAfterUpdateThreadInfo() {
        populateThreadsFromServer();
        populateThreadsFromCache();
        updateThreadInfo();
        checkThreadInfoAreSameInCacheAndServer();
    }

    public void updateThreadInfo() {

    }

    public void deleteMessage() {

    }

    public void sendNewMesage() {

    }
    public void checkThreadInfoAreSameInCacheAndServer() {

    }

    public void populateThreadsFromServer() {

    }

    public void populateThreadsFromCache() {

    }

    public void checkThreadsAreSameInCacheAndServer() {

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
