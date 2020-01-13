package com.fanap.podasync.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AsyncConstant {

    private final String constant;

    public AsyncConstant(@Constants String constant) {
        this.constant = constant;
    }


    @StringDef({Constants.PREFERENCE, Constants.PEER_ID, Constants.DEVICE_ID})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        String PREFERENCE = "PREFERENCE";
        String PEER_ID = "PEER_ID";
        String DEVICE_ID = "DEVICE_ID";

    }
}
