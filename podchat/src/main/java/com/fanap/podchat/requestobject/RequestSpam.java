package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestSpam extends GeneralRequestObject {
    private long threadId;

    RequestSpam(@NonNull Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long threadId;

        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public RequestSpam build() {
            return new RequestSpam(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
