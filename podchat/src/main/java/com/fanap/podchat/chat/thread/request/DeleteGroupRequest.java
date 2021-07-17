package com.fanap.podchat.chat.thread.request;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class DeleteGroupRequest extends GeneralRequestObject {

    private long threadId;

    public DeleteGroupRequest(Builder builder) {
        this.threadId = builder.threadId;
    }

    public DeleteGroupRequest() {

    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getThreadId() {
        return threadId;
    }


    public static class Builder extends GeneralRequestObject.Builder {

        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        public DeleteGroupRequest build() {

            return new DeleteGroupRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
