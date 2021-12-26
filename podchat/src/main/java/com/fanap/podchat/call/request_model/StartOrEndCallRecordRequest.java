package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class StartOrEndCallRecordRequest extends GeneralRequestObject {

    private final long callId;

    StartOrEndCallRecordRequest(Builder builder) {
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

        public StartOrEndCallRecordRequest build() {
            return new StartOrEndCallRecordRequest(this);
        }
    }


}
