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
//            TextMessageType.Constants.LINK,

            Constants.POD_SPACE_FILE,
            Constants.POD_SPACE_VOICE,
            Constants.POD_SPACE_SOUND,
            Constants.POD_SPACE_VIDEO,
            Constants.POD_SPACE_PICTURE,

    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {

        int TEXT = 1;
        int VOICE = 2;
        int PICTURE = 3;
        int VIDEO = 4;
        int SOUND = 5;
        int FILE = 6;
//        int LINK = 7;

        int POD_SPACE_PICTURE = 7;
        int POD_SPACE_VIDEO = 8;
        int POD_SPACE_SOUND = 9;
        int POD_SPACE_VOICE = 10;
        int POD_SPACE_FILE = 11;


    }

}
