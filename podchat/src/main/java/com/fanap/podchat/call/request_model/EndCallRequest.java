package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class EndCallRequest extends GeneralRequestObject {

    private long subjectId;

    private EndCallRequest(Builder builder) {
        this.subjectId = builder.subjectId;
    }

    public EndCallRequest() {

    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public static class Builder extends GeneralRequestObject.Builder{

        private long subjectId;

        public Builder setSubjectId(long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        @Override
        public EndCallRequest build() {

            return new EndCallRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GeneralRequestObject.Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

    }
}
