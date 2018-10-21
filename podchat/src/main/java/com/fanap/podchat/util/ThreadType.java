package com.fanap.podchat.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class ThreadType {

    private int threadType;

    public ThreadType(@Constants int threadType) {
        this.threadType = threadType;
    }

    @IntDef({Constants.NORMAL, Constants.OWNER_GROUP,
            Constants.PUBLIC_GROUP,
            Constants.CHANNEL_GROUP,
            Constants.CHANNEL,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        int NORMAL = 0;
        int OWNER_GROUP = 1;
        int PUBLIC_GROUP = 2;
        int CHANNEL_GROUP = 4;
        int CHANNEL = 8;
    }
}
