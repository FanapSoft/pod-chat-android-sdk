package com.example.podchat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class RetrofitApiTest {

    private MockWebServer mockWebServer;
    private Context appContext;


    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getContext();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

//        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

    }
}
