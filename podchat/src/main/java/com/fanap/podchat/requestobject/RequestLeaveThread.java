package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestLeaveThread extends GeneralRequestObject {

    private long threadId;

    private boolean clearHistory;

     public RequestLeaveThread(@NonNull Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.clearHistory = builder.clearHistory;

    }

    public boolean clearHistory() {
        return clearHistory;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder {
        private long threadId;
        private boolean clearHistory = true;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public Builder shouldKeepHistory() {
            this.clearHistory = false;
         return  this;
        }

        @NonNull
        public RequestLeaveThread build() {
            return new RequestLeaveThread(this);
        }

        @NonNull
        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }
    }

}
