package com.fanap.podchat.call.request_model.screen_share;

import android.app.Activity;
import android.content.Intent;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class ScreenShareRequest extends GeneralRequestObject {
    private Intent data;
    private final long callId;

    public ScreenShareRequest(Builder builder) {

        this.data = builder.data;
        this.callId = builder.callId;
    }

    public Intent getData() {
        return data;
    }

    public long getCallId() {
        return callId;
    }

    public static class Builder extends GeneralRequestObject.Builder<ScreenShareRequest.Builder> {


        private Intent data;
        private long callId;

        public Builder(Intent data, long callId) {
            this.data = data;
            this.callId = callId;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        public ScreenShareRequest build() {
            return new ScreenShareRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
