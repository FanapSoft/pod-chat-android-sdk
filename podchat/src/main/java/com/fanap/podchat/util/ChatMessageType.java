package com.fanap.podchat.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChatMessageType {

    private final int messageType;

    public ChatMessageType(@Constants int messageType) {
        this.messageType = messageType;
    }

    @IntDef({Constants.INVITATION, Constants.MESSAGE,
            Constants.SENT,
            Constants.DELIVERY,
            Constants.SEEN,
            Constants.PING,
            Constants.BLOCK,
            Constants.UNBLOCK,
            Constants.LEAVE_THREAD,
            Constants.RENAME,
            Constants.ADD_PARTICIPANT,
            Constants.GET_STATUS,
            Constants.GET_CONTACTS,
            Constants.GET_THREADS,
            Constants.GET_HISTORY,
            Constants.REMOVED_FROM_THREAD,
            Constants.REMOVE_PARTICIPANT,
            Constants.MUTE_THREAD,
            Constants.CHANGE_TYPE,
            Constants.UN_MUTE_THREAD,
            Constants.UPDATE_THREAD_INFO,
            Constants.FORWARD_MESSAGE,
            Constants.USER_INFO,
            Constants.USER_STATUS,
            Constants.GET_BLOCKED,
            Constants.RELATION_INFO,
            Constants.THREAD_PARTICIPANTS,
            Constants.EDIT_MESSAGE,
            Constants.THREAD_INFO_UPDATED,
            Constants.LAST_SEEN_UPDATED,
            Constants.DELETE_MESSAGE,
            Constants.ERROR,
            Constants.SPAM_PV_THREAD,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants {
        int INVITATION = 1;
        int MESSAGE = 2;
        int SENT = 3;
        int DELIVERY = 4;
        int SEEN = 5;
        int PING = 6;
        int BLOCK = 7;
        int UNBLOCK = 8;
        int LEAVE_THREAD = 9;
        int RENAME = 10;
        int ADD_PARTICIPANT = 11;
        int GET_STATUS = 12;
        int GET_CONTACTS = 13;
        int GET_THREADS = 14;
        int GET_HISTORY = 15;
        int CHANGE_TYPE = 16;
        int REMOVED_FROM_THREAD = 17;
        int REMOVE_PARTICIPANT = 18;
        int MUTE_THREAD = 19;
        int UN_MUTE_THREAD = 20;
        int UPDATE_THREAD_INFO = 21;
        int FORWARD_MESSAGE = 22;
        int USER_INFO = 23;
        int USER_STATUS = 24;
        int GET_BLOCKED = 25;
        int RELATION_INFO = 26;
        int THREAD_PARTICIPANTS = 27;
        int EDIT_MESSAGE = 28;
        int DELETE_MESSAGE = 29;
        int THREAD_INFO_UPDATED = 30;
        int LAST_SEEN_UPDATED = 31;
        int SPAM_PV_THREAD = 41;
        int ERROR = 999;
    }
}
