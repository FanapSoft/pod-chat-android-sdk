package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class StartOrEndCallRecordRequest extends GeneralRequestObject {

    private long subjectId;

    StartOrEndCallRecordRequest(Builder builder) {

        this.subjectId = builder.subjectId;

    }

    public long getSubjectId() {
        return subjectId;
    }

    public static class Builder {

        private long subjectId;

        public Builder(long subjectId) {
            this.subjectId = subjectId;
        }

        public Builder setSubjectId(long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public StartOrEndCallRecordRequest build() {
            return new StartOrEndCallRecordRequest(this);
        }
    }


}
