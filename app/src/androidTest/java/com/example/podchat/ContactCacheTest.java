package com.example.podchat;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
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
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;


@RunWith(AndroidJUnit4.class)
public class ContactCacheTest {

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
    private static String TOKEN = "4efac6081fb348b6992b06c9bada213a";

    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;

    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);

    final ArrayList<Contact> serverContacts = new ArrayList<>();
    final ArrayList<Contact> cacheContacts = new ArrayList<>();

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
    public void testIfContactsAreSame() {
        populateContactsFromServer();
        populateContactsFromCache();

        assert serverContacts.size() > 0;
        assert cacheContacts.size() > 0;
        assert cacheContacts.size() == serverContacts.size();

        for (Contact serverContact :
                serverContacts) {
            if (serverContact == null) continue;
            System.out.println(">>>>>>>>>>> Server Contact " + serverContact.getCellphoneNumber());
            Contact contactInCache = cacheContacts.stream().filter(cachContact -> cachContact.getId() == serverContact.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Cache Contact " + contactInCache.getCellphoneNumber());

            Assert.assertEquals(serverContact.getCellphoneNumber() != null, contactInCache.getCellphoneNumber() != null);
            Assert.assertEquals(serverContact.getFirstName(), contactInCache.getFirstName());
            Assert.assertEquals(serverContact.getId(), contactInCache.getId());
            if (serverContact.getLinkedUser() == null) {
                System.out.println(">>>>>>>>>>> linked user is null ");
                System.out.println(">>>>>>>>>>> linked user in cache " + contactInCache.getLinkedUser());
                continue;
            }
            Assert.assertEquals(serverContact.getLinkedUser().getName(), contactInCache.getLinkedUser().getName());
            Assert.assertEquals(serverContact.getLinkedUser().getUsername(), contactInCache.getLinkedUser().getUsername());
            Assert.assertEquals(serverContact.getLinkedUser().getNickname(), contactInCache.getLinkedUser().getNickname());
            System.out.println(">>>>>>>>>>> Server Contact linked " + serverContact.getLinkedUser().getName());
            System.out.println(">>>>>>>>>>> Cache Contact linked" + contactInCache.getLinkedUser().getName());
            System.out.println(">>>>>>>>>>>Contacts are same");
        }
    }

    public void populateContactsFromServer() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetContacts(String content, ChatResponse<ResultContact> response) {
                if (!response.isCache()) {
                    System.out.println("Received List: " + content);
                    serverContacts.addAll(response.getResult().getContacts());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.setListener(chatListeners);

        RequestGetContact request =
                new RequestGetContact.Builder()
                        .count(50)
                        .offset(0)
                        .build();

        chat.getContacts(request, null);
        pauseProcess();
        System.out.println("Received List: " + serverContacts.size());
    }


    public void populateContactsFromCache() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetContacts(String content, ChatResponse<ResultContact> response) {
                if (response.isCache()) {
                    System.out.println("Received Cache List: " + content);
                    cacheContacts.addAll(response.getResult().getContacts());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.setListener(chatListeners);

        RequestGetContact request =
                new RequestGetContact.Builder()
                        .count(50)
                        .offset(0)
                        .build();

        chat.getContacts(request, null);
        pauseProcess();
        System.out.println("Received Cache List: " + cacheContacts.size());
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
