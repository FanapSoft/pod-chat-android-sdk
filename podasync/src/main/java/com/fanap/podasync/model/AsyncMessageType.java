package com.fanap.podasync.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AsyncMessageType {

    private final int messageType;

    public AsyncMessageType(@MessageType int messageType) {
        this.messageType = messageType;
    }

    @IntDef({MessageType.PING, MessageType.SERVER_REGISTER, MessageType.DEVICE_REGISTER,
            MessageType.MESSAGE, MessageType.MESSAGE_ACK_NEEDED, MessageType.MESSAGE_SENDER_ACK_NEEDED,
            MessageType.ACK, MessageType.PEER_REMOVED, MessageType.ERROR_MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {
        int PING = 0;
        int SERVER_REGISTER = 1;
        int DEVICE_REGISTER = 2;
        int MESSAGE = 3;
        int MESSAGE_ACK_NEEDED = 4;
        int MESSAGE_SENDER_ACK_NEEDED = 5;
        int ACK = 6;
        int PEER_REMOVED = -3;
        int ERROR_MESSAGE = -99;
    }

    @MessageType
    public int getCurrentMessageType() {
        return messageType;
    }

    public void setMessageType(@MessageType int messageType) {
        messageType = messageType;
    }
}
