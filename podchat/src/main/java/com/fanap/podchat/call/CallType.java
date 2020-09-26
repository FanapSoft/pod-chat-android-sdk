package com.fanap.podchat.call;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CallType {

    private int callType;

    public CallType(@Constants int callType) {
        this.callType = callType;
    }

    @IntDef({Constants.VOICE_CALL,
            Constants.VIDEO_CALL,})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        int VOICE_CALL = 0X0;
        int VIDEO_CALL = 0X1;
    }
}
