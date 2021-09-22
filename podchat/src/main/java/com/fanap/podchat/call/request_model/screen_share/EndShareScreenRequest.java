package com.fanap.podchat.call.request_model.screen_share;

import android.app.Activity;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class EndShareScreenRequest extends GeneralRequestObject {

    private final long callId;

    public EndShareScreenRequest(Builder builder) {
        this.callId = builder.callId;
    }

    public long getCallId() {
        return callId;
    }

    public static class Builder extends GeneralRequestObject.Builder<EndShareScreenRequest.Builder> {

        private long callId;

        public Builder(long callId) {
            this.callId = callId;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }


        @Override
        public EndShareScreenRequest build() {
            return new EndShareScreenRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
