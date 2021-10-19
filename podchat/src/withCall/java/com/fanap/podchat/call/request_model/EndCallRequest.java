package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class EndCallRequest extends GeneralRequestObject {

    private long callId;

    private EndCallRequest(Builder builder) {
        this.callId = builder.callId;
    }

    public EndCallRequest() {

    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public static class Builder extends GeneralRequestObject.Builder{

        private long callId;

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public long getCallId() {
            return callId;
        }

        @Override
        public EndCallRequest build() {

            return new EndCallRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GeneralRequestObject.Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

    }
}
