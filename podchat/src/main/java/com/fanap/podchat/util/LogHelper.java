package com.fanap.podchat.util;

import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class LogHelper {
    private static LogHelper logHelper;
    private static boolean log;

    public static LogHelper init(boolean isLoggable) {
        if (logHelper == null) {
            logHelper = new LogHelper();
            Logger.addLogAdapter(new AndroidLogAdapter() {
                @Override
                public boolean isLoggable(int priority, @Nullable String tag) {
                    return isLoggable;
                }
            });
        }
        return logHelper;
    }
}
