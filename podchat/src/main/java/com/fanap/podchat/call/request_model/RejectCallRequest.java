package com.fanap.podchat.call.request_model;

public class RejectCallRequest extends AcceptCallRequest {

    private RejectCallRequest(Builder builder) {
        super(builder);
    }


    public static class Builder extends AcceptCallRequest.Builder {


        public Builder(long callId) {
            super(callId);
        }

        public RejectCallRequest build() {

            return new RejectCallRequest(this);
        }
    }


}
