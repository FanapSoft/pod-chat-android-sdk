package com.fanap.podchat.call.request_model;

public class TerminateCallRequest extends EndCallRequest {

    public TerminateCallRequest(Builder builder) {
        setCallId(builder.getCallId());
    }

    public static class Builder extends EndCallRequest.Builder {

        @Override
        public Builder setCallId(long callId) {
            super.setCallId(callId);
            return this;
        }

        public TerminateCallRequest build() {
            return new TerminateCallRequest(this);
        }
    }
}
