package com.example.podchat;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.CallActivity;
import com.example.chat.application.chatexample.CallContract;
import com.example.chat.application.chatexample.CallPresenter;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.constants.CallType;
import com.fanap.podchat.call.request_model.GetCallHistoryRequest;
import com.fanap.podchat.chat.CallPartnerViewManager;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.example.R;
import com.fanap.podchat.requestobject.RequestConnect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ConcurrentLinkedQueue;


@RunWith(AndroidJUnit4.class)
public class CallTest extends ChatAdapter {

    private static CallContract.presenter presenter;
    @Mock
    private static CallContract.view view;


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

    private CallActivity callActivty;


    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<CallActivity> callActivtyRule = new ActivityTestRule<>(CallActivity.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void initial() {


        appContext = InstrumentationRegistry.getTargetContext();

        chat = Chat.init(appContext);

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

        callActivty = callActivtyRule.getActivity();
        presenter = new CallPresenter(appContext, view, callActivty);


    }


    @Test
    public void setupCallTest() {
        CallPartnerViewManager viewManager = chat.useCallPartnerViewManager();
        Assert.assertNotNull(viewManager);
    }


    @Test
    public void getCallHistoryList() {


        GetCallHistoryRequest request = new GetCallHistoryRequest.Builder()
                .setType(CallType.Constants.VIDEO_CALL)
                .count(50)
                .build();

        ChatListener listener = Mockito.mock(ChatListener.class);

        chat.addListener(listener);

        String uniqueId = chat.getCallsHistory(request);

        Mockito.verify(listener, Mockito.after(5000).atLeast(2)).onReceiveCallHistory(Mockito.any());

    }

    @Test
    public void testAddView() {

        int viewListSize = chat.getVideoCallPartnerViews().size();

        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));

        viewListSize += 3;

        Assert.assertEquals(viewListSize, chat.getVideoCallPartnerViews().size());

    }

    @Test
    public void testUsingCallPartnerViewManager() {


        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));


        chat.useCallPartnerViewManager();

        Assert.assertEquals(0, chat.getVideoCallPartnerViews().size());

    }


    @Test
    public void assignViewInChat() {

        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));
        chat.addPartnerView(new CallPartnerView(callActivty));

        chat.useCallPartnerViewManager();

        CallPartnerView assigned = chat.assignCallPartnerViewT(14);

        Assert.assertEquals((long) assigned.getPartnerId(), 14L);
    }


    @Test
    public void generateViewTest() {

        CallPartnerViewManager manager = chat.useCallPartnerViewManager();

        CallPartnerView partnerView = chat.assignCallPartnerViewT(1);

        assert partnerView != null;

        presenter.prepareNewView(partnerView);

        partnerView.setVisibility(View.VISIBLE);

        Mockito.verify(view, Mockito.after(5000)).addNewView(
                Mockito.argThat(pView -> pView.getSurfaceView() != null)
        );
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
}
