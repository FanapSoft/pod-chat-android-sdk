package com.example.chat.application.chatexample;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

public class BaseApplication extends MultiDexApplication {

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        MultiDex.install(this);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }


    public static BaseApplication getInstance() {

        return instance;
    }
}
