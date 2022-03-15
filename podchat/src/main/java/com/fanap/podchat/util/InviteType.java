package com.fanap.podchat.util;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class InviteType {

    private int chatType;

    public InviteType(@Constants int chatType) {
        this.chatType = chatType;
    }

    @IntDef({Constants.TO_BE_USER_SSO_ID,
            Constants.TO_BE_USER_CONTACT_ID,
            Constants.TO_BE_USER_CELLPHONE_NUMBER,
            Constants.TO_BE_USER_USERNAME,
            Constants.TO_BE_USER_ID,
            Constants.TO_BE_CORE_USER_ID,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        int TO_BE_USER_SSO_ID = 1;
        int TO_BE_USER_CONTACT_ID = 2;
        int TO_BE_USER_CELLPHONE_NUMBER = 3;
        int TO_BE_USER_USERNAME = 4;
        int TO_BE_USER_ID = 5;
        int TO_BE_CORE_USER_ID = 6;
    }
}
