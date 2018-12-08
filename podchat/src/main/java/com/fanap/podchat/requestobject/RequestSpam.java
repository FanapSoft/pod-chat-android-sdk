package com.fanap.podchat.requestobject;

public class RequestSpam extends GeneralRequestObject {
    private long threadId;

    RequestSpam(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public RequestSpam build() {
            return new RequestSpam(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
