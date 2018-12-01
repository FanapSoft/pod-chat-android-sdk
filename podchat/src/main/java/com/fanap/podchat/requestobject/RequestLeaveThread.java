package com.fanap.podchat.requestobject;

public class RequestLeaveThread extends GeneralRequestObject {

    private long threadId;

     RequestLeaveThread(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;

    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder {
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public RequestLeaveThread build() {
            return new RequestLeaveThread(this);
        }

        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }
    }

}
