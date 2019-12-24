package com.fanap.podchat.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AsyncAckType {


    private final int ackType;


    public AsyncAckType(int ackType) {
        this.ackType = ackType;
    }


    @IntDef({
         Constants.WITH_ACK,
         Constants.WITHOUT_ACK
    })



    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {

        int WITH_ACK = 4;
        int WITHOUT_ACK = 3;




    }
}
