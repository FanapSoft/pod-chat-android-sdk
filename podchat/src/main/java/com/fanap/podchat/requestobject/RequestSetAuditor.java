package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestSetAuditor {

    private long threadId;
    private long userId;

    private RequestSetAuditor(Builder builder) {
        this.threadId = builder.threadId;
        this.userId = builder.userId;
    }


    public static class Builder {
        private long threadId;
        private long userId;

        public Builder(long threadId, long userId) {
            this.threadId = threadId;
            this.userId = userId;
        }

        public RequestSetAuditor build() {
            return new RequestSetAuditor(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
