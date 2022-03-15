package com.example.podchat;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.example.R;
import com.fanap.podchat.chat.messge.SearchSystemMetadataRequest;
import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultMessage;
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
import java.util.List;
import java.util.Random;


@RunWith(AndroidJUnit4.class)
public class SearchMetadataTest {

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


    @BeforeClass
    public static void initial() {


        appContext = InstrumentationRegistry.getTargetContext();

        chat = Chat.init(appContext);

    }

    @Before
    public void createChat() {
        Looper.prepare();

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


    final ArrayList<Thread> threads = new ArrayList<>();

    //requests for list of threads
    @Test
    public void populateThreadsListFromServerOrCache() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                System.out.println("Received List: " + content);
                threads.addAll(thread.getResult().getThreads());
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
    @Test
    public void populateThreadsListFromServerOnly() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                if (!thread.isCache()) {
                    System.out.println("Received List: " + content);
                    threads.addAll(thread.getResult().getThreads());
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
    @Test
    public void populateThreadsListFromCacheOnly() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                if (thread.isCache()) {
                    System.out.println("Received List: " + content);
                    threads.addAll(thread.getResult().getThreads());
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

    public static class DummyMetadataVO {
        Integer count = 1;
        String desc = "type";
    }

    @Test
    @LargeTest
    public void sendMessageWithMetadataTest() {

        populateThreadsListFromServerOnly();


        assert threads.size() > 0;

        Thread thread = threads.get(0);

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);


        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
        dummyMetadataVO.count = new Random().nextInt();
        dummyMetadataVO.desc = dummyMetadataVO.desc + "" + dummyMetadataVO.count;
        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage request =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(request, null);

        sleep(2000);

        for (int i = 0; i < 16; i++) {
            dummyMetadataVO.count++;
            dummyMetadataVO.desc = String.format("%s%d", dummyMetadataVO.desc, dummyMetadataVO.count);
            String meta2 = App.getGson().toJson(dummyMetadataVO);

            request =
                    new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                            .messageType(TextMessageType.Constants.TEXT)
                            .jsonMetaData(meta2)
                            .build();

            chat.sendTextMessage(request, null);

            sleep(6000);
        }

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce()).onNewMessage(Mockito.any(),
                Mockito.argThat((ChatResponse<ResultNewMessage> resp) -> resp.getResult().getMessageVO().getSystemMetadata().equals(meta)));

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce()).onNewMessage(Mockito.any(),
                Mockito.argThat((ChatResponse<ResultNewMessage> resp) -> !resp.getResult().getMessageVO().getSystemMetadata().equals("I'm something else!")));

    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_LT_Test() {

        populateThreadsListFromServerOnly();


        assert threads.size() > 0;

        Thread thread = threads.get(0);

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .lte("4")
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);

        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) -> resp.getResult().getHistory().size() > 0));
    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_GT_Test() {

        populateThreadsListFromServerOnly();


        assert threads.size() > 0;

        Thread thread = threads.get(0);

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
        dummyMetadataVO.desc = "GT test";
        dummyMetadataVO.count = 12;
        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage sendMessageWithMetadataRequest =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);


        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .gte("12")
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class), Mockito.argThat((ChatResponse<ResultHistory> resp) -> resp.getResult().getHistory().size() > 0));

    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_Has_Test() {

        populateThreadsListFromServerOnly();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);


        assert threads.size() > 0;

        Thread thread = threads.get(0);


        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
        dummyMetadataVO.desc = "Find me before  " + System.currentTimeMillis() + (1_000_000 * 6);
        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage sendMessageWithMetadataRequest =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("desc")
                        .has(" me ")
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                resp.getResult().getHistory().stream().anyMatch(
                                        messageVO -> messageVO.getSystemMetadata().contains(dummyMetadataVO.desc))));

    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_Is_StringTest() {

        populateThreadsListFromServerOnly();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        assert threads.size() > 0;

        Thread thread = threads.get(0);

        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();

        dummyMetadataVO.desc = "This is me!";
//        dummyMetadataVO.desc = UUID.randomUUID().toString();

        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage sendMessageWithMetadataRequest =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("desc")
                        .is(dummyMetadataVO.desc)
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                resp.getResult().getHistory().stream().anyMatch(
                                        messageVO -> messageVO.getSystemMetadata().equals(meta))));

    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_Is_NumberTest() {

        populateThreadsListFromServerOnly();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);

        assert threads.size() > 0;

        Thread thread = threads.get(0);

        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();

        dummyMetadataVO.desc = "876";

        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage sendMessageWithMetadataRequest =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("desc")
                        .is(dummyMetadataVO.desc)
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                resp.getResult().getHistory().stream().anyMatch(
                                        messageVO -> messageVO.getSystemMetadata().equals(meta))));

    }

    @Test
    @LargeTest
    public void searchHistoryWithMetadata_AND_Test() {

        populateThreadsListFromServerOnly();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);


        assert threads.size() > 0;

        Thread thread = threads.get(0);


        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
        dummyMetadataVO.count = 1001;
//        String meta = App.getGson().toJson(dummyMetadataVO);
//
//        RequestMessage sendMessageWithMetadataRequest =
//                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
//                        .messageType(TextMessageType.Constants.TEXT)
//                        .jsonMetaData(meta)
//                        .build();
//
//        chat.sendTextMessage(sendMessageWithMetadataRequest, null);
//
//        sleep(6000);
//
//        dummyMetadataVO.count = 1002;
//        meta = App.getGson().toJson(dummyMetadataVO);
//
//        sendMessageWithMetadataRequest.setJsonMetaData(meta);
//        chat.sendTextMessage(sendMessageWithMetadataRequest, null);
//
//        sleep(6000);


//        NosqlSearchMetadataCriteria
//                metadataCriteriaLB =
//                new NosqlSearchMetadataCriteria.Builder("count")
//                        .gt("1000")
//                        .build();

        NosqlSearchMetadataCriteria
                metadataCriteriaUB =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .lte("1002")
                        .build();

        List<NosqlSearchMetadataCriteria> criteriaList = new ArrayList<>();

//        criteriaList.add(metadataCriteriaLB);
        criteriaList.add(metadataCriteriaUB);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .gt("1000")
                        .and(criteriaList)
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                resp.getResult().getHistory().stream().anyMatch(
                                        messageVO -> messageVO.getSystemMetadata().contains(String.valueOf(dummyMetadataVO.count)))));

    }


    @Test
    @LargeTest
    public void searchHistoryWithMetadata_OR_Test() {

        populateThreadsListFromServerOnly();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);

        chat.setListener(mTestListener);


        assert threads.size() > 0;

        Thread thread = threads.get(0);


        DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
        int random1 = Math.abs(new Random().nextInt());
        dummyMetadataVO.count = random1;
        String meta = App.getGson().toJson(dummyMetadataVO);

        RequestMessage sendMessageWithMetadataRequest =
                new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                        .messageType(TextMessageType.Constants.TEXT)
                        .jsonMetaData(meta)
                        .build();

        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);

        int random2 = Math.abs(new Random().nextInt());
        dummyMetadataVO.count = random2;
        meta = App.getGson().toJson(dummyMetadataVO);

        sendMessageWithMetadataRequest.setJsonMetaData(meta);
        chat.sendTextMessage(sendMessageWithMetadataRequest, null);

        sleep(6000);


//        NosqlSearchMetadataCriteria
//                metadataCriteriaLB =
//                new NosqlSearchMetadataCriteria.Builder("count")
//                        .is(String.valueOf(random1))
//                        .build();

        NosqlSearchMetadataCriteria
                metadataCriteriaUB =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .is(String.valueOf(random2))
                        .build();

        List<NosqlSearchMetadataCriteria> criteriaList = new ArrayList<>();

//        criteriaList.add(metadataCriteriaLB);
        criteriaList.add(metadataCriteriaUB);

        NosqlSearchMetadataCriteria
                metadataCriteria =
                new NosqlSearchMetadataCriteria.Builder("count")
                        .is(String.valueOf(random1))
                        .or(criteriaList)
                        .build();

        SearchSystemMetadataRequest request =
                new SearchSystemMetadataRequest.Builder(thread.getId())
                        .metadataCriteria(metadataCriteria)
                        .count(50)
                        .build();

        chat.searchHistory(request, null);


        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                .onGetHistory(Mockito.any(String.class),
                        Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                resp.getResult().getHistory().stream().anyMatch(
                                        messageVO -> messageVO.getSystemMetadata().contains(String.valueOf(dummyMetadataVO.count)))));

    }

     @Test
    @LargeTest
    public void searchHistoryWithMetadata_NOT_Test() {

         populateThreadsListFromServerOnly();

         ChatListener mTestListener = Mockito.mock(ChatListener.class);

         chat.setListener(mTestListener);


         assert threads.size() > 0;

         Thread thread = threads.get(0);

         DummyMetadataVO dummyMetadataVO = new DummyMetadataVO();
         RequestMessage.Builder sendMessageWithMetadataRequest;
         sendMessageWithMetadataRequest = new RequestMessage.Builder("Android Test " + new Date(), thread.getId())
                 .messageType(TextMessageType.Constants.TEXT);


         //Meta1
         int random1 = Math.abs(new Random().nextInt());
         dummyMetadataVO.count = random1;
         String meta1 = App.getGson().toJson(dummyMetadataVO);
         sendMessageWithMetadataRequest.jsonMetaData(meta1);
         chat.sendTextMessage(sendMessageWithMetadataRequest.build(), null);
         sleep(6000);


         //Meta2
         int random2 = Math.abs(new Random().nextInt());
         dummyMetadataVO.count = random2;
         String meta2 = App.getGson().toJson(dummyMetadataVO);
         sendMessageWithMetadataRequest.jsonMetaData(meta2);
         chat.sendTextMessage(sendMessageWithMetadataRequest.build(), null);
         sleep(6000);



         NosqlSearchMetadataCriteria
                 metadataCriteriaUB =
                 new NosqlSearchMetadataCriteria.Builder("count")
                         .is(String.valueOf(random2))
                         .build();

         List<NosqlSearchMetadataCriteria> criteriaList = new ArrayList<>();

         criteriaList.add(metadataCriteriaUB);

         NosqlSearchMetadataCriteria
                 metadataCriteria =
                 new NosqlSearchMetadataCriteria.Builder("count")
                         .gte(String.valueOf(random1))
                         .not(criteriaList)
                         .build();

         SearchSystemMetadataRequest request =
                 new SearchSystemMetadataRequest.Builder(thread.getId())
                         .metadataCriteria(metadataCriteria)
                         .count(50)
                         .build();

         chat.searchHistory(request, null);


         Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce())
                 .onGetHistory(Mockito.any(String.class),
                         Mockito.argThat((ChatResponse<ResultHistory> resp) ->
                                 resp.getResult().getHistory().stream().noneMatch(
                                         messageVO -> messageVO.getSystemMetadata().contains(meta2))));

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
