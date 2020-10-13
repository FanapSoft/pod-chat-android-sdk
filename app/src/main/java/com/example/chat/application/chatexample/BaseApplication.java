package com.example.chat.application.chatexample;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


public class BaseApplication extends MultiDexApplication {

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        MultiDex.install(this);

    }


    public static BaseApplication getInstance() {

        return instance;
    }
}
