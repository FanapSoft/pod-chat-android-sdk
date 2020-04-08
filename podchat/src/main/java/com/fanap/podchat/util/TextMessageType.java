package com.fanap.podchat.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextMessageType {

    private final int textMessageType;

    public TextMessageType(@TextMessageType.Constants int textMessageType) {
        this.textMessageType = textMessageType;
    }

    @IntDef({
            TextMessageType.Constants.TEXT,
            TextMessageType.Constants.VOICE,
            TextMessageType.Constants.PICTURE,
            TextMessageType.Constants.VIDEO,
            TextMessageType.Constants.SOUND,
            TextMessageType.Constants.FILE,
            TextMessageType.Constants.LINK,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {

        int TEXT = 1;
        int VOICE = 2;
        int PICTURE = 3;
        int VIDEO = 4;
        int SOUND = 5;
        int FILE = 6;
        int LINK = 7;

    }

}
