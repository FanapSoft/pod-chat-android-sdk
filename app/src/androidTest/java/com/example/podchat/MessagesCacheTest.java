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
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestForwardMessage;
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
public class MessagesCacheTest {

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
    private static String TOKEN = "cf06e0e5cc3f41fba837f4d05b9a4138";


    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;


    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);

    final ArrayList<Thread> threads = new ArrayList<>();
    final ArrayList<MessageVO> threadMessagesList = new ArrayList<>();

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


    //requests for list of threads
    public void populateThreadsListFromServerOrCache() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                System.out.println("Received List: " + content);
                threads.addAll(thread.getResult().getThreads());
                chat.removeListener(chatListeners);

            }
        };

        chat.addListener(chatListeners);

        RequestThread requestThread =
                new RequestThread.Builder()
                        .count(25)
                        .build();

        presenter.getThreads(requestThread, null);

        long t1 = System.currentTimeMillis();
        Mockito.verify(view, Mockito.after(10000).atLeastOnce()).onGetThreadList(Mockito.any(), Mockito.any());
        long t2 = System.currentTimeMillis();
        System.out.println("Received List: " + threads.size() + " after: " + (t2 - t1) + " ms");

    }

    //requests for list of threads from server

    public void populateThreadsListFromServerOnly() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> response) {

                if (!response.isCache()) {
                    System.out.println("Received List: " + content);
                    threads.addAll(response.getResult().getThreads()
                            .stream()
                            .filter(thread -> thread.getTitle() != null
                                    && thread.getId() > 0
                                    && !thread.isClosed()
                                    && thread.getLastMessageVO() != null)
                            .collect(Collectors.toList()));

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

    //requests for list of threads from cache

    public void populateThreadsListFromCacheOnly() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                if (thread.isCache()) {
                    System.out.println("Received List: " + content);
                    threads.addAll(thread.getResult().getThreads());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }

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


    public void populateMessagesFromServer() {
        populateThreadsListFromServerOnly();
        assert threads.size() > 0;

        Thread thread = threads.get(0);

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (!history.isCache()) {
                    System.out.println("Received Message List Server: " + content);

                    threadMessagesList.addAll(history.getResult().getHistory()
                            .stream()
                            .filter(messageVO ->
                                    messageVO.getMessage() != null)
                            .collect(Collectors.toList()));

                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .withNoCache()
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();
    }


    public void populateMessagesFromCache() {
        populateThreadsListFromServerOnly();
        assert threads.size() > 0;

        Thread thread = threads.get(0);

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (history.isCache()) {
                    System.out.println("Received Message List Cache: " + content);
                    threadMessagesList.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();
    }


    public void populateMessagesFromServerOrCache() {
        populateThreadsListFromServerOnly();
        assert threads.size() > 0;

        Thread thread = threads.get(0);

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                System.out.println("Received Message List, Cache: " + history.isCache() + " Content: " + content);
                threadMessagesList.addAll(history.getResult().getHistory());
                chat.removeListener(chatListeners);
                resumeProcess();

            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();
    }

    @Test
    public void sendMessageToThread() {
        populateThreadsListFromServerOnly();

        assert threads.size() > 0;

        Thread thread = threads.get(0);

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        RequestMessage request =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .build();

        chat.sendTextMessage(request, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onNewMessage(Mockito.any(), Mockito.any());

        sleep(2000);


    }

    @Test
    public void sendMessageToThreadAndGetFromCache() {
        populateThreadsListFromServerOrCache();

        assert threads.size() > 0;

        Thread thread = threads.get(0);

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        String msgText = "Android Test " + new Date();
        RequestMessage request =
                new RequestMessage.Builder(msgText, thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .build();

        chat.sendTextMessage(request, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onNewMessage(Mockito.any(), Mockito.any());

        sleep(2000);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistory, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onGetHistory(Mockito.any(), Mockito.argThat((ChatResponse<ResultHistory> response) -> response.isCache() && response.getResult().getHistory()
                        .stream().anyMatch(messageVO -> messageVO.getMessage().equals(msgText))));


    }

    @Test
    public void sendReplyMessage() {
        populateMessagesFromServer();
        Collections.shuffle(threadMessagesList);
        MessageVO message = threadMessagesList.get(0);
        Assert.assertNotNull(message);
        Thread thread = message.getConversation();
        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        RequestReplyMessage repRequest
                = new RequestReplyMessage.Builder("Reply to " + message.getMessage(), thread.getId(), message.getId(), TextMessageType.Constants.TEXT)
                .build();

        chat.replyMessage(repRequest, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onNewMessage(Mockito.any(), Mockito.any());

        sleep(2000);

    }

    @Test
    public void sendReplyMessageAndCheckCache() {
        populateThreadsListFromServerOnly();
        populateMessagesFromServer();
        assert threads.size() > 0;
        assert threadMessagesList.size() > 0;

        Thread thread = threads.get(0);
        MessageVO message = threadMessagesList.get(0);
        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        String replyTXT = "Reply to " + message.getMessage();

        RequestReplyMessage repRequest
                = new RequestReplyMessage.Builder(replyTXT, thread.getId(), message.getId(), TextMessageType.Constants.TEXT)
                .build();

        chat.replyMessage(repRequest, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onNewMessage(Mockito.any(), Mockito.any());

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistory, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onGetHistory(Mockito.any(), Mockito.argThat((ChatResponse<ResultHistory> response) -> response.isCache() && response.getResult().getHistory()
                        .stream().anyMatch(messageVO -> messageVO.getMessage().equals(replyTXT))));


    }


    @Test
    public void forwardMessage() {
        populateThreadsListFromServerOnly();
        populateMessagesFromServer();
        assert threads.size() > 1;
        assert threadMessagesList.size() > 0;

        Thread thread = threads.get(0);
        Thread thread2 = threads.get(1);
        MessageVO message = threadMessagesList.get(0);
        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        RequestForwardMessage forRequest
                = new RequestForwardMessage.Builder(thread2.getId(),
                new ArrayList<>(Collections.singletonList(message.getId())))
                .build();

        chat.forwardMessage(forRequest);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onNewMessage(Mockito.any(), Mockito.any());

        sleep(2000);

    }

    @Test
    public void forwardMessageAndCheckCache() {
        populateThreadsListFromServerOnly();
        populateMessagesFromServer();
        assert threads.size() > 1;
        assert threadMessagesList.size() > 0;

        Collections.shuffle(threads);
        Thread thread2 = threads.get(1);
        MessageVO message = threadMessagesList.get(0);
        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        RequestForwardMessage forRequest
                = new RequestForwardMessage.Builder(thread2.getId(),
                new ArrayList<>(Collections.singletonList(message.getId())))
                .build();

        chat.forwardMessage(forRequest);

        sleep(2000);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread2.getId())
                .build();

        chat.getHistory(requestGetHistory, null);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce())
                .onGetHistory(Mockito.any(), Mockito.argThat((ChatResponse<ResultHistory> response) -> response.isCache() && response.getResult().getHistory()
                        .stream().anyMatch(messageVO -> messageVO.getForwardInfo() != null)));

    }

    @Test
    public void testIfReplyInfoVOsAreTheSame() {
        populateMessagesFromServer();

        assert threadMessagesList.size() > 0;

        Collections.shuffle(threadMessagesList);
        MessageVO message = threadMessagesList.get(0);

        assert message.getConversation() != null;

        Thread thread = message.getConversation();


        chat.setListener(new ChatListener() {
            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {

                resumeProcess();
            }
        });

        RequestReplyMessage repRequest
                = new RequestReplyMessage.Builder("Reply to " + message.getMessage(), thread.getId(), message.getId(), TextMessageType.Constants.TEXT)
                .build();

        chat.replyMessage(repRequest, null);

        pauseProcess();


        // Load Server messages
        ArrayList<MessageVO> msgFromServer = new ArrayList<>();
        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (!history.isCache()) {
                    System.out.println("Received Message List Server: " + content);
                    msgFromServer.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .withNoCache()
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();

        // Load cache
        ArrayList<MessageVO> msgFromCache = new ArrayList<>();

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (history.isCache()) {
                    System.out.println("Received Message List Cache: " + content);
                    msgFromCache.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistoryCache
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistoryCache, null);
        pauseProcess();


        assert msgFromServer.size() > 0;
        assert msgFromCache.size() > 0;
        assert msgFromCache.size() == msgFromServer.size();

        for (MessageVO msg :
                msgFromServer) {

            if(msg.getMessage()==null) continue;

            System.out.println(">>>>>>>>>>> Server Message " + msg.getMessage() + " RepInfo: " + msg.getReplyInfoVO());
            MessageVO msgInCache = msgFromCache.stream().filter(messageVO -> messageVO.getId() == msg.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Cache Message " + msgInCache.getMessage() + " RepInfo: " + msgInCache.getReplyInfoVO());


            Assert.assertEquals(msg.getReplyInfoVO() != null, msgInCache.getReplyInfoVO() != null);

            if (msg.getReplyInfoVO() != null) {
                Assert.assertEquals(msg.getReplyInfoVO().getMessage(), msgInCache.getReplyInfoVO().getMessage());
                Assert.assertEquals(msg.getReplyInfoVO().getMetadata(), msgInCache.getReplyInfoVO().getMetadata());
                Assert.assertEquals(msg.getReplyInfoVO().getRepliedToMessageTime(), msgInCache.getReplyInfoVO().getRepliedToMessageTime());
                Assert.assertEquals(msg.getReplyInfoVO().getRepliedToMessageNanos(), msgInCache.getReplyInfoVO().getRepliedToMessageNanos());
                Assert.assertEquals(msg.getReplyInfoVO().getSystemMetadata(), msgInCache.getReplyInfoVO().getSystemMetadata());

                if (msg.getReplyInfoVO().getParticipant() != null) {
                    Assert.assertEquals(msg.getReplyInfoVO().getParticipant().getName(), msgInCache.getReplyInfoVO().getParticipant().getName());
                    Assert.assertEquals(msg.getReplyInfoVO().getParticipant().getContactFirstName(), msgInCache.getReplyInfoVO().getParticipant().getContactFirstName());
                    Assert.assertEquals(msg.getReplyInfoVO().getParticipant().getCellphoneNumber(), msgInCache.getReplyInfoVO().getParticipant().getCellphoneNumber());
                    Assert.assertEquals(msg.getReplyInfoVO().getParticipant().getAdmin(), msgInCache.getReplyInfoVO().getParticipant().getAdmin());
                }
            }
        }

    }


    @Test
    public void testIfConversationAreSame(){
        populateMessagesFromServer();

        assert threadMessagesList.size() > 0;

        Collections.shuffle(threadMessagesList);
        MessageVO message = threadMessagesList.get(0);

        assert message.getConversation() != null;

        Thread thread = message.getConversation();


        chat.setListener(new ChatListener() {
            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {

                resumeProcess();
            }
        });

        RequestMessage repRequest
                = new RequestMessage.Builder("Message at " + new Date() , thread.getId())
                .messageType( TextMessageType.Constants.TEXT)
                .build();

        chat.sendTextMessage(repRequest, null);

        pauseProcess();


        // Load Server messages
        ArrayList<MessageVO> msgFromServer = new ArrayList<>();
        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (!history.isCache()) {
                    System.out.println("Received Message List Server: " + content);
                    msgFromServer.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .withNoCache()
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();

        // Load cache
        ArrayList<MessageVO> msgFromCache = new ArrayList<>();

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (history.isCache()) {
                    System.out.println("Received Message List Cache: " + content);
                    msgFromCache.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistoryCache
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistoryCache, null);
        pauseProcess();


        assert msgFromServer.size() > 0;
        assert msgFromCache.size() > 0;
        assert msgFromCache.size() == msgFromServer.size();

        for (MessageVO msg :
                msgFromServer) {

            if(msg.getMessage()==null) continue;

            System.out.println(">>>>>>>>>>> Server Message " + msg.getMessage() + " Conversation: " + msg.getConversation());
            MessageVO msgInCache = msgFromCache.stream().filter(messageVO -> messageVO.getId() == msg.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Cache Message " + msgInCache.getMessage() + " RepInfo: " + msgInCache.getConversation());


            Assert.assertEquals(msg.getConversation() != null, msgInCache.getConversation() != null);

            if (msg.getConversation() != null) {
                Assert.assertEquals(msg.getConversation().getId(),msgInCache.getConversation().getId());

            }
        }

    }

    @Test
    public void testIfParticipantsAreSame(){
        populateMessagesFromServer();

        assert threadMessagesList.size() > 0;

        Collections.shuffle(threadMessagesList);
        MessageVO message = threadMessagesList.get(0);

        assert message.getConversation() != null;

        Thread thread = message.getConversation();


        chat.setListener(new ChatListener() {
            @Override
            public void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {

                resumeProcess();
            }
        });

        RequestMessage repRequest
                = new RequestMessage.Builder("Message at " + new Date() , thread.getId())
                .messageType( TextMessageType.Constants.TEXT)
                .build();

        chat.sendTextMessage(repRequest, null);

        pauseProcess();


        // Load Server messages
        ArrayList<MessageVO> msgFromServer = new ArrayList<>();
        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (!history.isCache()) {
                    System.out.println("Received Message List Server: " + content);
                    msgFromServer.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistory
                = new RequestGetHistory.Builder(thread.getId())
                .withNoCache()
                .build();

        chat.getHistory(requestGetHistory, null);
        pauseProcess();

        // Load cache
        ArrayList<MessageVO> msgFromCache = new ArrayList<>();

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (history.isCache()) {
                    System.out.println("Received Message List Cache: " + content);
                    msgFromCache.addAll(history.getResult().getHistory());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        RequestGetHistory requestGetHistoryCache
                = new RequestGetHistory.Builder(thread.getId())
                .build();

        chat.getHistory(requestGetHistoryCache, null);
        pauseProcess();


        assert msgFromServer.size() > 0;
        assert msgFromCache.size() > 0;
        assert msgFromCache.size() == msgFromServer.size();

        for (MessageVO msg :
                msgFromServer) {

            if(msg.getMessage()==null) continue;

            System.out.println(">>>>>>>>>>> Server Message " + msg.getMessage() + " Conversation: " + msg.getConversation());
            MessageVO msgInCache = msgFromCache.stream().filter(messageVO -> messageVO.getId() == msg.getId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Cache Message " + msgInCache.getMessage() + " RepInfo: " + msgInCache.getConversation());


            Assert.assertEquals(msg.getParticipant() != null, msgInCache.getParticipant() != null);

            if (msg.getParticipant() != null) {
                Assert.assertEquals(msg.getParticipant().getId(),msgInCache.getParticipant().getId());
                Assert.assertEquals(msg.getParticipant().getName(),msgInCache.getParticipant().getName());
                Assert.assertEquals(msg.getParticipant().getChatProfileVO()!=null,msgInCache.getParticipant().getChatProfileVO()!=null);
            }
        }

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
