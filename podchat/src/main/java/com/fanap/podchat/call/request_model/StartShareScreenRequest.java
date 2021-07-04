package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.ArrayList;

public class StartShareScreenRequest extends GeneralRequestObject {

    private long callId;

    public StartShareScreenRequest(Builder builder) {
        this.callId = builder.callId;
    }

    public long getCallId() {
        return callId;
    }

    public static class Builder extends GeneralRequestObject.Builder {

        private long callId;

        public Builder(long callId) {
            this.callId = callId;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        public StartShareScreenRequest build() {
            return new StartShareScreenRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
