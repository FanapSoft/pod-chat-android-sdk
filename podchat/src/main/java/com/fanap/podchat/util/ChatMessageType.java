package com.fanap.podchat.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChatMessageType {

    private final int messageType;

    public ChatMessageType(@Constants int messageType) {
        this.messageType = messageType;
    }

    @IntDef({
            Constants.INVITATION,
            Constants.MESSAGE,
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
            Constants.SEEN_MESSAGE_LIST,
            Constants.DELIVERED_MESSAGE_LIST,
            Constants.SET_ROLE_TO_USER,
            Constants.REMOVE_ROLE_FROM_USER,
            Constants.CLEAR_HISTORY,
//            Constants.SIGNAL_MESSAGE,
            Constants.SYSTEM_MESSAGE,
            Constants.PIN_THREAD,
            Constants.UNPIN_THREAD,
            Constants.GET_NOT_SEEN_DURATION,
            Constants.GET_USER_ROLES,
            Constants.PIN_MESSAGE,
            Constants.UNPIN_MESSAGE,
            Constants.UPDATE_LAST_SEEN,
            Constants.UPDATE_CHAT_PROFILE,
            Constants.IS_NAME_AVAILABLE,
            Constants.PUBLIC_THREAD_AND_SET_NAME,
            Constants.PRIVATE_THREAD,
            Constants.SET_PRIVATE_THREAD_HASH,
            Constants.GET_THREAD_INFO,
            Constants.JOIN_THREAD,
            Constants.INTERACT_MESSAGE,
            Constants.UPDATE_USER_PROFILE,
            Constants.CHANGE_THREAD_TYPE,
            Constants.GET_THING_INFO,
            Constants.GET_REPORT_REASONS,
            Constants.REPORT_THREAD,
            Constants.REPORT_USER,
            Constants.REPORT_MESSAGE,
            Constants.ALL_UNREAD_MESSAGE_COUNT,
            Constants.CREATE_BOT,
            Constants.DEFINE_BOT_COMMAND,
            Constants.START_BOT,
            Constants.STOP_BOT,
            Constants.REGISTER_FCM_APP,
            Constants.REGISTER_FCM_USER_DEVICE,
            Constants.UPDATE_FCM_APP_USERS_DEVICE,
            Constants.LAST_MESSAGE_EDITED,
            Constants.LAST_MESSAGE_DELETED,
            Constants.CONTACT_SYNCED,
            Constants.LOCATION_PING,
            Constants.CALL_REQUEST,
            Constants.DELIVER_CALL_REQUEST,
            Constants.ACCEPT_CALL,
            Constants.REJECT_CALL,
            Constants.START_CALL,
            Constants.GET_CALLS,
            Constants.END_CALL,
            Constants.CALL_RECONNECT,
            Constants.CALL_CONNECT,
            Constants.GROUP_CALL_REQUEST,
            Constants.LEAVE_CALL,
            Constants.ADD_CALL_PARTICIPANT,
            Constants.CALL_PARTICIPANT_JOINED,
            Constants.REMOVE_CALL_PARTICIPANT,
            Constants.TERMINATE_CALL,
            Constants.CLOSE_THREAD,
            Constants.CALL_CREATED,
            Constants.GET_ACTIVE_CALL_PARTICIPANTS,
            Constants.MUTE_CALL_PARTICIPANT,
            Constants.UN_MUTE_CALL_PARTICIPANT,
            Constants.CANCEL_GROUP_CALL,
            Constants.REGISTER_ASSISTANT,



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
        int SEEN_MESSAGE_LIST = 32;
        int DELIVERED_MESSAGE_LIST = 33;
        int IS_NAME_AVAILABLE = 34;
        int PUBLIC_THREAD_AND_SET_NAME = 35;
        int PRIVATE_THREAD = 36;
        int SET_PRIVATE_THREAD_HASH = 37;
        int GET_THREAD_INFO = 38;
        int JOIN_THREAD = 39;
        int INTERACT_MESSAGE = 40;
        int SPAM_PV_THREAD = 41;
        int SET_ROLE_TO_USER = 42;
        int REMOVE_ROLE_FROM_USER = 43;
        int CLEAR_HISTORY = 44;
        int UPDATE_USER_PROFILE = 45;
        int SYSTEM_MESSAGE = 46;
        int GET_NOT_SEEN_DURATION = 47;
        int PIN_THREAD = 48;
        int UNPIN_THREAD = 49;

        int PIN_MESSAGE = 50;
        int UNPIN_MESSAGE = 51;
        int UPDATE_CHAT_PROFILE = 52;
        int CHANGE_THREAD_TYPE = 53;
        int GET_USER_ROLES = 54;
        int GET_THING_INFO = 55;
        int GET_REPORT_REASONS = 56;
        int REPORT_THREAD = 57;
        int REPORT_USER = 58;
        int REPORT_MESSAGE = 59;
        int UPDATE_LAST_SEEN = 60;
        int ALL_UNREAD_MESSAGE_COUNT = 61;

        int CREATE_BOT = 62;
        int DEFINE_BOT_COMMAND = 63;
        int START_BOT = 64;
        int STOP_BOT = 65;


        int CALL_REQUEST = 70;
        int ACCEPT_CALL = 71;
        int REJECT_CALL = 72;
        int DELIVER_CALL_REQUEST = 73;
        int START_CALL = 74;
        int END_CALL_REQUEST = 75;
        int END_CALL = 76;
        int GET_CALLS = 77;
        int CALL_RECONNECT = 78;
        int CALL_CONNECT = 79;


        int GROUP_CALL_REQUEST = 91;
        int LEAVE_CALL = 92;
        int ADD_CALL_PARTICIPANT = 93;
        int CALL_PARTICIPANT_JOINED = 94;
        int REMOVE_CALL_PARTICIPANT = 95;
        int TERMINATE_CALL = 96;
        int MUTE_CALL_PARTICIPANT = 97;
        int UN_MUTE_CALL_PARTICIPANT = 98;
        int CANCEL_GROUP_CALL = 99;


        int CALL_CREATED = 111;
        int GET_ACTIVE_CALL_PARTICIPANTS = 110;

        int LAST_MESSAGE_DELETED = 66;
        int LAST_MESSAGE_EDITED = 67;


        int REGISTER_FCM_APP = 80;
        int REGISTER_FCM_USER_DEVICE = 81;
        int UPDATE_FCM_APP_USERS_DEVICE = 82;

        int CONTACT_SYNCED = 90;


        int LOCATION_PING = 101;
        int CLOSE_THREAD = 102;
        int ERROR = 999;
        int REGISTER_ASSISTANT = 107;
        int DEACTICVE_ASSISTANT = 108;
        int GET_ASSISTANTS = 109;

    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SignalMsg {
        int IS_TYPING = 1;
        int RECORD_VOICE = 2;
        int UPLOAD_PICTURE = 3;
        int UPLOAD_VIDEO = 4;
        int UPLOAD_SOUND = 5;
        int UPLOAD_FILE = 6;
    }

}
