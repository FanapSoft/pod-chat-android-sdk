package com.example.podchat;


import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.BlockUnblockAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantHistoryRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetBlockedAssistantsRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class AssistantCacheTest {


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
    final ArrayList<Participant> threadParticipant = new ArrayList<>();
    final ArrayList<Contact> contacts = new ArrayList<>();

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

    /////////////////////////////////////////////////////////////////////////////////


    //requests for list of threads
    public void populateThreadsListFromServerOrCache() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                print("Received List: " + content);
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
                    print("Received List: " + content);
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
        print("Received List: " + threads.size());

    }

    //requests for list of threads from cache

    public void populateThreadsListFromCacheOnly() {


        chatListeners = new ChatListener() {
            @Override
            public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

                if (thread.isCache()) {
                    print("Received List: " + content);
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
        print("Received List: " + threads.size());

    }


    public void populateMessagesFromServer() {
        populateThreadsListFromServerOnly();
        assert threads.size() > 0;

        Thread thread = threads.get(0);

        chatListeners = new ChatListener() {
            @Override
            public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

                if (!history.isCache()) {
                    print("Received Message List Server: " + content);

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
                    print("Received Message List Cache: " + content);
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

    public void populateContactsFromServer() {
        chatListeners = new ChatListener() {
            @Override
            public void onGetContacts(String content, ChatResponse<ResultContact> response) {
                if (!response.isCache()) {
                    print("Received List: " + content);
                    contacts.addAll(response.getResult().getContacts());
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
        print("Received Contact List: " + contacts.size());
    }

    @Test
    public void populateParticipantList() {
        populateThreadsListFromServerOnly();
        assert threads.size() > 0;

        Thread thread = getValidGroupThread();

        chatListeners = new ChatListener() {
            @Override
            public void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> response) {

                System.out.println("Received Participant, Cache: " + response.isCache() + " Content: " + content);
                if (response.getResult().getParticipants().size() > 1) {
                    threadParticipant.addAll(response.getResult().getParticipants());
                    chat.removeListener(chatListeners);
                    resumeProcess();
                } else {
                    threads.remove(thread);
                    Thread t2 = getValidGroupThread();
                    print("try again changing thread...");
                    RequestThreadParticipant request =
                            new RequestThreadParticipant.Builder()
                                    .threadId(t2.getId())
                                    .build();

                    chat.getThreadParticipants(request, null);
                }


            }
        };

        chat.addListener(chatListeners);

        RequestThreadParticipant request =
                new RequestThreadParticipant.Builder()
                        .threadId(thread.getId())
                        .build();

        chat.getThreadParticipants(request, null);
        pauseProcess();
    }


    @Test
    public void registerNewAssistant() {
        populateContactsFromServer();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);
        chat.addListener(mTestListener);
        assert contacts.size() > 0;
        Contact contact = getValidContact();
        print("Selected contact for register as assistant " + App.getGson().toJson(contact));
        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.ADD_ROLE_TO_USER);
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();

        Invitee invite = new Invitee(contact.getId(), InviteType.Constants.TO_BE_USER_CONTACT_ID);

        assistantVo.setInvitees(invite);
        assistantVo.setContactType("default");
        assistantVo.setRoles(typeRoles);

        assistantVos.add(assistantVo);

        RegisterAssistantRequest request = new RegisterAssistantRequest.Builder(assistantVos).build();

        chat.registerAssistant(request);

        Mockito.verify(mTestListener, Mockito.after(5000).atLeastOnce()).onRegisterAssistant(
                Mockito.argThat((ChatResponse<List<AssistantVo>> response) -> response.getResult().stream().anyMatch(
                        assistantVo1 -> assistantVo1.getParticipantVO().getCoreUserId() == contact.getLinkedUser().getCoreUserId()
                ))
        );

    }


    @Test
    public void getAssistantList() {

        ChatListener mTestListener = Mockito.mock(ChatListener.class);
        chat.addListener(mTestListener);
        GetAssistantRequest request = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(request);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce()).onGetAssistants(Mockito.any());

    }

    //Register new assistant and check if it is in cache

    @LargeTest
    @Test
    public void checkRegisteredAssistantInCache() {
        populateContactsFromServer();
        assert contacts.size() > 0;
        Collections.shuffle(contacts);
        Contact contact = getValidContact();
        print("Selected contact for register as assistant " + App.getGson().toJson(contact));
        ArrayList<String> typeRoles = new ArrayList<>();
        typeRoles.add(RoleType.Constants.ADD_ROLE_TO_USER);
        typeRoles.add(RoleType.Constants.READ_THREAD);
        typeRoles.add(RoleType.Constants.EDIT_THREAD);


        List<AssistantVo> assistantVos = new ArrayList<>();
        AssistantVo assistantVo = new AssistantVo();

        Invitee invite = new Invitee(contact.getId(), InviteType.Constants.TO_BE_USER_CONTACT_ID);

        assistantVo.setInvitees(invite);
        assistantVo.setContactType("default");
        assistantVo.setRoles(typeRoles);

        assistantVos.add(assistantVo);

        final AssistantVo[] registeredAssistant = new AssistantVo[1];

        ChatListener mListener = new ChatListener() {
            @Override
            public void onRegisterAssistant(ChatResponse<List<AssistantVo>> response) {

                registeredAssistant[0] = response.getResult().get(0);
                chat.removeListener(this);
                resumeProcess();
            }
        };
        chat.setListener(mListener);
        RegisterAssistantRequest request = new RegisterAssistantRequest.Builder(assistantVos).build();

        chat.registerAssistant(request);
        pauseProcess();

        ChatListener mTestListener = Mockito.mock(ChatListener.class);
        chat.addListener(mTestListener);
        GetAssistantRequest requestGet = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(requestGet);

        Mockito.verify(mTestListener, Mockito.after(2000).atLeastOnce()).onGetAssistants(
                Mockito.argThat((ChatResponse<List<AssistantVo>> response) -> (response.isCache() && validateAssistantsAreSame(registeredAssistant[0], response.getResult())))
        );

    }

    @Test
    public void checkCacheAndServerAssistantList() {
        ArrayList<AssistantVo> inCache = new ArrayList<>();
        ArrayList<AssistantVo> inServer = new ArrayList<>();

        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {

                if (response.isCache()) {
                    inCache.addAll(response.getResult());
                } else {
                    inServer.addAll(response.getResult());
                }
                if (inCache.size() > 0 && inServer.size() > 0) {
                    chat.removeListener(this);
                    resumeProcess();
                }

            }
        });

        GetAssistantRequest request = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(request);
        pauseProcess();

        Assert.assertEquals(inCache.size(), inServer.size());
        for (AssistantVo assistantInServer :
                inServer) {
            Assert.assertTrue(validateAssistantsAreSame(assistantInServer, inCache));
        }
    }

    @Test
    public void blockAssistantAndCheckCache() {
        populateContactsFromServer();
        List<AssistantVo> assistantVos = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {
                if (!response.isCache()) {
                    prettyLog(response.getJson());
                    assistantVos.addAll(response.getResult());
                    chat.removeListener(this);
                    resumeProcess();
                }

            }
        });
        GetAssistantRequest request = new GetAssistantRequest.Builder()
                .setCount(25)
                .withNoCache()
                .setOffset(0)
                .build();
        chat.getAssistants(request);
        pauseProcess();


        //the block assistant method accepts only Invitee

        Collections.shuffle(assistantVos);

        List<AssistantVo> notBlockedAssistants = assistantVos.stream().filter(assistantVo -> !assistantVo.getBlock()).collect(Collectors.toList());

        Contact assistantContactToBlock =
                contacts.stream().filter(
                        contact ->
                                contact.getLinkedUser() != null
                                        && Objects.equals(contact.getLinkedUser().getUsername(),
                                        notBlockedAssistants.get(0).getParticipantVO().getUsername()))
                        .findFirst().get();

        print("Going to block " + assistantContactToBlock.getFirstName());
        prettyLog(App.getGson().toJson(assistantContactToBlock));

        Invitee invitee = new Invitee(assistantContactToBlock.getId(), InviteType.Constants.TO_BE_USER_CONTACT_ID);
        AssistantVo assistantToBlock = new AssistantVo();
        assistantToBlock.setInvitees(invitee);
        List<AssistantVo> toBlockAssistantList = new ArrayList<>();
        toBlockAssistantList.add(assistantToBlock);


        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantBlocked(ChatResponse<List<AssistantVo>> response) {
                prettyLog(response.getJson());
                toBlockAssistantList.clear();
                toBlockAssistantList.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });


        BlockUnblockAssistantRequest requestBlock =
                new BlockUnblockAssistantRequest.Builder(toBlockAssistantList, true)
                        .build();
        chat.blockAssistant(requestBlock);
        pauseProcess();


        ArrayList<AssistantVo> inCache = new ArrayList<>();

        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {

                if (response.isCache()) {
                    prettyLog(response.getJson());
                    inCache.addAll(response.getResult());
                    chat.removeListener(this);
                    resumeProcess();
                }

            }
        });

        GetAssistantRequest requestGetAssistantFromCache = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(requestGetAssistantFromCache);
        pauseProcess();

        assert inCache.size() > 0;

        Assert.assertTrue(inCache.stream().filter(anAssistantInCache ->
                toBlockAssistantList.stream().anyMatch(blockListAssistant ->
                        anAssistantInCache.getParticipantVO().getId() == blockListAssistant.getParticipantVO().getId())
        ).findFirst().get().getBlock());


    }

    @Test
    public void getBlockedAssistantsList() {


        ArrayList<AssistantVo> blockedListFromServer = new ArrayList<>();
        ArrayList<AssistantVo> blockedListInCache = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantBlocks(ChatResponse<List<AssistantVo>> response) {
                print("Blocked assistants list cache: " + response.isCache());
                prettyLog(response.getJson());
                if (response.isCache()) {
                    blockedListInCache.addAll(response.getResult());
                } else {
                    blockedListFromServer.addAll(response.getResult());
                }

                if (blockedListFromServer.size() == blockedListInCache.size()) {
                    print("Cache and Server data are filled now");
                    chat.removeListener(this);
                    resumeProcess();
                }
            }
        });


        GetBlockedAssistantsRequest getBlockedAssistantsRequest =
                new GetBlockedAssistantsRequest.Builder()
                        .build();
        chat.getBlocksAssistant(getBlockedAssistantsRequest);
        pauseProcess();


        for (AssistantVo assistantCache :
                blockedListInCache) {

            AssistantVo assistantServer = blockedListFromServer.stream().filter(assistantVo -> assistantVo.getParticipantVO().getId()
                    == assistantCache.getParticipantVO().getId()).findFirst().get();


            if (!assistantCache.getRoles().containsAll(assistantServer.getRoles())) {
                Assert.fail("Roles are not same => " + assistantCache.getRoles() + " != " + assistantServer.getRoles());
            }

        }


        Assert.assertTrue(true);
    }

    @Test
    public void unBlockAssistantAndCheckBlockListInCache() {
        populateContactsFromServer();


        ArrayList<AssistantVo> blockedListFromServer = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantBlocks(ChatResponse<List<AssistantVo>> response) {
                print("Blocked assistants list cache: " + response.isCache());
                prettyLog(response.getJson());
                blockedListFromServer.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });
        GetBlockedAssistantsRequest getBlockedAssistantsRequest =
                new GetBlockedAssistantsRequest.Builder()
                        .withNoCache()
                        .build();
        chat.getBlocksAssistant(getBlockedAssistantsRequest);
        pauseProcess();


        Contact assistantContactToUnBlock =
                contacts.stream().filter(
                        contact ->
                                contact.getLinkedUser() != null
                                        && Objects.equals(contact.getLinkedUser().getUsername(),
                                        blockedListFromServer.get(0).getParticipantVO().getUsername()))
                        .findFirst().get();

        print("Going to unblock " + assistantContactToUnBlock.getFirstName());
        prettyLog(App.getGson().toJson(assistantContactToUnBlock));

        Invitee invitee = new Invitee(assistantContactToUnBlock.getId(), InviteType.Constants.TO_BE_USER_CONTACT_ID);
        AssistantVo assistantToUnBlock = new AssistantVo();
        assistantToUnBlock.setInvitees(invitee);
        List<AssistantVo> toUnBlockAssistantList = new ArrayList<>();
        toUnBlockAssistantList.add(assistantToUnBlock);


        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantUnBlocked(ChatResponse<List<AssistantVo>> response) {
                print("Assistant unblocked");
                prettyLog(response.getJson());
                toUnBlockAssistantList.clear();
                toUnBlockAssistantList.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });


        ChatListener mMockedListener = Mockito.mock(ChatListener.class);
        chat.addListener(mMockedListener);

        BlockUnblockAssistantRequest requestBlock =
                new BlockUnblockAssistantRequest.Builder(toUnBlockAssistantList, false)
                        .build();
        chat.unBlockAssistant(requestBlock);
        pauseProcess();


        ArrayList<AssistantVo> blockedListInCache = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantBlocks(ChatResponse<List<AssistantVo>> response) {
                print("Blocked assistants list cache: " + response.isCache());
                prettyLog(response.getJson());
                if (response.isCache()) {
                    blockedListInCache.addAll(response.getResult());
                    chat.removeListener(this);
                    resumeProcess();
                }
            }
        });


        GetBlockedAssistantsRequest getBlockedAssistantsRequest2 =
                new GetBlockedAssistantsRequest.Builder()
                        .build();
        chat.getBlocksAssistant(getBlockedAssistantsRequest2);
        pauseProcess();


        Assert.assertTrue(blockedListInCache.stream().noneMatch(assistantVo ->
                assistantVo.getParticipantVO().getId() == toUnBlockAssistantList.get(0).getParticipantVO().getId()));


    }


    @Test
    public void unBlockAssistantAndCheckAssistantListInCache() {
        populateContactsFromServer();


        ArrayList<AssistantVo> blockedListFromServer = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantBlocks(ChatResponse<List<AssistantVo>> response) {
                print("Blocked assistants list cache: " + response.isCache());
                prettyLog(response.getJson());
                blockedListFromServer.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });
        GetBlockedAssistantsRequest getBlockedAssistantsRequest =
                new GetBlockedAssistantsRequest.Builder()
                        .withNoCache()
                        .build();
        chat.getBlocksAssistant(getBlockedAssistantsRequest);
        pauseProcess();


        Contact assistantContactToUnBlock =
                contacts.stream().filter(
                        contact ->
                                contact.getLinkedUser() != null
                                        && Objects.equals(contact.getLinkedUser().getUsername(),
                                        blockedListFromServer.get(0).getParticipantVO().getUsername()))
                        .findFirst().get();

        print("Going to unblock " + assistantContactToUnBlock.getFirstName());
        prettyLog(App.getGson().toJson(assistantContactToUnBlock));

        Invitee invitee = new Invitee(assistantContactToUnBlock.getId(), InviteType.Constants.TO_BE_USER_CONTACT_ID);
        AssistantVo assistantToUnBlock = new AssistantVo();
        assistantToUnBlock.setInvitees(invitee);
        List<AssistantVo> toUnBlockAssistantList = new ArrayList<>();
        toUnBlockAssistantList.add(assistantToUnBlock);


        chat.addListener(new ChatListener() {
            @Override
            public void onAssistantUnBlocked(ChatResponse<List<AssistantVo>> response) {
                print("Assistant unblocked");
                prettyLog(response.getJson());
                toUnBlockAssistantList.clear();
                toUnBlockAssistantList.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });


        ChatListener mMockedListener = Mockito.mock(ChatListener.class);
        chat.addListener(mMockedListener);

        BlockUnblockAssistantRequest requestBlock =
                new BlockUnblockAssistantRequest.Builder(toUnBlockAssistantList, false)
                        .build();
        chat.unBlockAssistant(requestBlock);
        pauseProcess();


        ArrayList<AssistantVo> assistantListInCache = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {
                print("Receive assistants list cache: " + response.isCache());
                prettyLog(response.getJson());
                if (response.isCache()) {
                    assistantListInCache.addAll(response.getResult());
                    chat.removeListener(this);
                    resumeProcess();
                }
            }
        });


        GetAssistantRequest getAssistantRequest =
                new GetAssistantRequest.Builder()
                        .build();
        chat.getAssistants(getAssistantRequest);
        pauseProcess();


        Assert.assertTrue(assistantListInCache.stream().anyMatch(assistantInCache ->
                assistantInCache.getParticipantVO().getId() == toUnBlockAssistantList.get(0).getParticipantVO().getId()
                        && !assistantInCache.getBlock()));


    }


    @Test
    public void getAssistantHistories() {


        ArrayList<AssistantVo> assistantVoArrayList = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {

                assistantVoArrayList.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });
        GetAssistantRequest request = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(request);
        pauseProcess();


        Collections.shuffle(assistantVoArrayList);

        ChatListener mMockedListener = Mockito.mock(ChatListener.class);
        chat.addListener(mMockedListener);
        GetAssistantHistoryRequest getAssistantHistoryRequest = new GetAssistantHistoryRequest.Builder()
                .setCount(50)
                .build();
        chat.getAssistantHistory(getAssistantHistoryRequest);

        Mockito.verify(mMockedListener, Mockito.after(3000).atLeastOnce())
                .onGetAssistantHistory(Mockito.any());

    }

    @Test
    public void getAndValidateAssistantHistoriesCache() {


        ArrayList<AssistantVo> assistantVoArrayList = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistants(ChatResponse<List<AssistantVo>> response) {

                assistantVoArrayList.addAll(response.getResult());
                chat.removeListener(this);
                resumeProcess();
            }
        });
        GetAssistantRequest request = new GetAssistantRequest.Builder()
                .setCount(25)
                .setOffset(0)
                .build();
        chat.getAssistants(request);
        pauseProcess();


        Collections.shuffle(assistantVoArrayList);


        ArrayList<AssistantHistoryVo> assistantHistoryVos = new ArrayList<>();
        ArrayList<AssistantHistoryVo> assistantHistoryVosCache = new ArrayList<>();
        chat.addListener(new ChatListener() {
            @Override
            public void onGetAssistantHistory(ChatResponse<List<AssistantHistoryVo>> response) {
                print("Received Assistant Histories cache: " + response.isCache());
                prettyLog(App.getGson().toJson(response.getResult()));
                if(response.isCache()){
                    assistantHistoryVosCache.addAll(response.getResult());
                }else {
                    assistantHistoryVos.addAll(response.getResult());
                }

                if(assistantHistoryVos.size() == assistantHistoryVosCache.size()){
                    chat.removeListener(this);
                    resumeProcess();
                }
            }
        });
        GetAssistantHistoryRequest getAssistantHistoryRequest = new GetAssistantHistoryRequest.Builder()
                .setCount(50)
                .build();
        chat.getAssistantHistory(getAssistantHistoryRequest);
        pauseProcess();
        Assert.assertTrue(true);

    }

    private boolean checkWithBlocked(AssistantVo assistantVoInCache, List<AssistantVo> toBlockAssistantList) {

        AssistantVo blocked = toBlockAssistantList.stream().filter(assist -> assist.getParticipantVO().getId() == assistantVoInCache.getParticipantVO().getId()).findFirst().get();


        return false;
    }

    private boolean validateAssistantsAreSame(AssistantVo assistantInServer, List<AssistantVo> assistantVoListFromCache) {


        return assistantVoListFromCache.stream().anyMatch(
                cacheAssistant ->
                        (assistantInServer.getParticipantVO().getId() == cacheAssistant.getParticipantVO().getId()) // existence of assistant in cache
                                && (assistantInServer.getBlock() == cacheAssistant.getBlock())
                                && (assistantInServer.getRoles().containsAll(cacheAssistant.getRoles()))
                                && (assistantInServer.getContactType().equals(cacheAssistant.getContactType()))
                                && (assistantInServer.getParticipantVO().getName().equals(cacheAssistant.getParticipantVO().getName()))
                                && (assistantInServer.getParticipantVO().getAdmin().equals(cacheAssistant.getParticipantVO().getAdmin()))
                                && (assistantInServer.getParticipantVO().getChatProfileVO() == null || assistantInServer.getParticipantVO().getChatProfileVO().getBio().equals(cacheAssistant.getParticipantVO().getChatProfileVO().getBio()))
                                && (assistantInServer.getParticipantVO().getContactFirstName() == null || assistantInServer.getParticipantVO().getContactFirstName().equals(cacheAssistant.getParticipantVO().getContactFirstName()))


        );

    }

    private Contact getValidContact() {
        return contacts.stream().filter(contact -> contact.isHasUser() && contact.getLinkedUser() != null).collect(
                Collectors.toList()
        ).get(0);
    }


    private Thread getValidGroupThread() {
        Thread selected = threads.stream()
                .filter(thread1 -> (thread1.isGroup() && !thread1.isClosed() && thread1.getAdmin())).collect(Collectors.toList()).get(0);

        print("Selected thread: " + App.getGson().toJson(selected));
        return selected;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
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

    private void print(String s) {
        System.out.println(s);
    }

    private void prettyLog(String jsonInfo) {
        Logger.json(jsonInfo);
    }
}
