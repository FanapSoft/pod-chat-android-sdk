package com.example.podchat;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.example.chat.application.chatexample.ChatSandBoxActivity;
import com.fanap.podchat.mainmodel.History;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class LocalChatTest {
    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;
    @Mock
    private Activity activity;
    private Context appContext;


    //fel token
//    private String name = "felfeli";
//    private static String TOKEN = "e4f1d5da7b254d9381d0487387eabb0a";
    //Fifi
    private String name = "Fifi";
    private static String TOKEN = "5fb88da4c6914d07a501a76d68a62363";

//    private static String name = "Alexi";
//    private static String TOKEN = "bebc31c4ead6458c90b607496dae25c6";

    //Masoud
//    private String name = "jiji";
//    private static String TOKEN = "fbd4ecedb898426394646e65c6b1d5d1";

//    private String name = "zizi";
//    private static String TOKEN = "7cba09ff83554fc98726430c30afcfc6";
    //Token Alexi
//

    private String ssoHost = "http://172.16.110.76"; // {**REQUIRED**} Socket Address
    private String platformHost = "http://172.16.106.26:8080/hamsam/"; // {**REQUIRED**} Platform Core Address
    private String fileServer = "http://172.16.106.26:8080/hamsam/"; // {**REQUIRED**} File Server Address
    private String serverName = "chat-server";

    private static String socketAddres = "ws://172.16.106.26:8003/ws";
    private static String appId = "POD-Chat";

    private static String TYPE_CODE = "";

    private ChatSandBoxActivity activy;

    @Before
    public void setUp() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        presenter = new ChatPresenter(appContext, view, activy);
        presenter.connect(socketAddres,
                appId, serverName, TOKEN, ssoHost,
                platformHost, fileServer, TYPE_CODE);
    }

    /*old method*/
    @Test
    @MediumTest
    public void getHistoryFromTime() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long threadId = 1288;
        History history = new History.Builder().fromTime(235566).fromTimeNanos(2).build();
        presenter.getHistory(history, threadId, null);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Mockito.verify(view, Mockito.times(1)).onGetThreadHistory();
    }
}
