package com.fanap.podchat.call.request_model;

public class AcceptCallRequest {

    private long callId;
    private boolean mute;
    private boolean videoCall;


    AcceptCallRequest(Builder builder) {
        this.callId = builder.callId;
        this.mute = builder.mute;
        this.videoCall = builder.videoCall;
    }

    public long getCallId() {
        return callId;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isVideoCall() {
        return videoCall;
    }

    public static class Builder{

        private long callId;
        private boolean mute;
        private boolean videoCall;



        public Builder(long callId) {
            this.callId = callId;
        }


        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public Builder withVideo() {
            this.videoCall = true;
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
