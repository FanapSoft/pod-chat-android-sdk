package com.fanap.podchat.chat.call.result_model;

public class CallReconnectResult {

    private String clientId;

    private long callId;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }
}
