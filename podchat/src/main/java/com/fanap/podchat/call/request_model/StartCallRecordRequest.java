package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class StartCallRecordRequest extends GeneralRequestObject {

    private final long callId;
    private List<String> tags;
    private String content;


    StartCallRecordRequest(Builder builder) {
        this.callId = builder.callId;
        this.tags = builder.tags;
        this.content = builder.content;
    }

    public long getCallId() {
        return callId;
    }


    public List<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }

    public static class Builder {

        private long callId;
        private List<String> tags;
        private String content;


        public Builder(long subjectId) {
            this.callId = subjectId;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public Builder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }


        public StartCallRecordRequest build() {
            return new StartCallRecordRequest(this);
        }
    }


}
