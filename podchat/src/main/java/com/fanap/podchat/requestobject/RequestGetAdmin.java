package com.fanap.podchat.requestobject;

public class RequestGetAdmin {
    private long threadId;

    private RequestGetAdmin(Builder builder) {
        this.threadId = builder.threadId;
    }


    public static class Builder {
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public RequestGetAdmin build() {
            return new RequestGetAdmin(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
