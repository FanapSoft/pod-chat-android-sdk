package com.example.chat.application.chatexample;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
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
