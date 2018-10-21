package com.fanap.podchat.util;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChatStateType {

    private final String chatState;

    public ChatStateType(@ChatSateConstant String chatState) {
        this.chatState = chatState;
    }

    @StringDef({ChatSateConstant.OPEN,
            ChatSateConstant.CLOSED,
            ChatSateConstant.CLOSING,
            ChatSateConstant.CONNECTING,
            ChatSateConstant.ASYNC_READY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatSateConstant {
        String CONNECTING = "CONNECTING";
        String CLOSING = "CLOSING";
        String CLOSED = "CLOSED";
        String OPEN = "OPEN";
        String ASYNC_READY = "ASYNC_READY";
    }
}
