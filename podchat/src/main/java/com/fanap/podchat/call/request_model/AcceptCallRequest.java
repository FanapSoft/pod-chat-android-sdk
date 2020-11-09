package com.fanap.podchat.call.request_model;

public class AcceptCallRequest {

    private long callId;
    private boolean mute;


    AcceptCallRequest(Builder builder) {
        this.callId = builder.callId;
        this.mute = builder.mute;
    }

    public long getCallId() {
        return callId;
    }

    public boolean isMute() {
        return mute;
    }

    public static class Builder{

        private long callId;
        private boolean mute;


        public Builder(long callId) {
            this.callId = callId;
        }


        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public Builder mute() {
            this.mute = true;
            return this;
        }

        public AcceptCallRequest build() {

            return new AcceptCallRequest(this);
        }


    }
}
