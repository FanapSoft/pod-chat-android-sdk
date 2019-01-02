package com.example.podchat;

import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.persistance.MessageDatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class DbTest {
    private Context appContext;
    private MessageDatabaseHelper messageDatabaseHelper;

    @Before
    public void setUp() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getTargetContext();
        MockitoAnnotations.initMocks(this);
        messageDatabaseHelper = new MessageDatabaseHelper(appContext);
    }

    @Test
    public void CheckDataBase() {

    }

}
