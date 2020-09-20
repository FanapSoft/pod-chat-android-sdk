package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class LeaveThreadRequest extends GeneralRequestObject {

    private long threadId;

     LeaveThreadRequest(@NonNull Builder builder) {
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

        @NonNull
        public LeaveThreadRequest build() {
            return new LeaveThreadRequest(this);
        }

        @NonNull
        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }
    }

}
