package com.example.podchat;

import com.example.chat.application.chatexample.BaseApplication;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.example.R;
import com.fanap.podchat.requestobject.RequestConnect;

import org.junit.Assert;

public class TestClass {



    public static void main(String[] args) {

    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }
}
