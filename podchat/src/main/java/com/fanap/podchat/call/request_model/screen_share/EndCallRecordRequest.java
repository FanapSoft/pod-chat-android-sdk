package com.fanap.podchat.call.request_model.screen_share;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class EndCallRecordRequest extends GeneralRequestObject {

    private final long callId;

    EndCallRecordRequest(Builder builder) {
        this.callId = builder.callId;
    }

    public long getCallId() {
        return callId;
    }

    public static class Builder {

        private long callId;

        public Builder(long subjectId) {
            this.callId = subjectId;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public EndCallRecordRequest build() {
            return new EndCallRecordRequest(this);
        }
    }


}
