package com.fanap.podchat.chat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {
    Handler handler;

    public MainThreadExecutor() {
       handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
