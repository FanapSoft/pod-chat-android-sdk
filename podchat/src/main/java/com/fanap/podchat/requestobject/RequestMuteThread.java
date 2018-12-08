package com.fanap.podchat.requestobject;

public class RequestMuteThread extends GeneralRequestObject {
    private long threadId;

    RequestMuteThread(Builder builder) {
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

        public RequestMuteThread build() {
            return new RequestMuteThread(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
