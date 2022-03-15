package com.fanap.podchat.call.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ClientType {

    private int clientType;

    public ClientType(@Constants int clientType) {
        this.clientType = clientType;
    }

    @IntDef({Constants.WEB,
            Constants.ANDROID,
            Constants.DESKTOP
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        int WEB = 1;
        int ANDROID = 2;
        int DESKTOP = 3;
    }
}
