package com.fanap.podchat.call;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CallStatus {

    private int callStatus;

    public CallStatus(@CallStatus.Constants int callStatus) {
        this.callStatus = callStatus;
    }

    @IntDef({
            Constants.REQUESTED,
            Constants.CANCELED,
            Constants.MISS,
            Constants.DECLINED,
            Constants.ACCEPTED,
            Constants.STARTED,
            Constants.ENDED,
            Constants.LEAVE,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {

       int REQUESTED = 1;
       int CANCELED=2;
       int MISS=3;
       int DECLINED=4;
       int ACCEPTED=5;
       int STARTED=6;
       int ENDED=7;
       int LEAVE=8;

    }
}
