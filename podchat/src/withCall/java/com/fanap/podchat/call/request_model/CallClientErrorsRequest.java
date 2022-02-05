package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class CallClientErrorsRequest  extends GeneralRequestObject {

    private long callId;
    private int errorCode;

    CallClientErrorsRequest(Builder builder) {
        this.callId = builder.callId;
        this.errorCode = builder.errorCode;
    }

    public long getCallId() {
        return callId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static class Builder extends GeneralRequestObject.Builder{
        private long callId;
        private int errorCode;

        public Builder(long callId, int errorCode) {
            this.callId = callId;
            this.errorCode = errorCode;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public Builder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }


        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        protected CallClientErrorsRequest.Builder self() {
            return this;
        }


        public CallClientErrorsRequest build() {
            return new CallClientErrorsRequest(this);
        }
    }

}
