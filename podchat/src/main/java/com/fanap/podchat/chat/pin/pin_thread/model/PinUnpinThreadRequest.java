package com.fanap.podchat.chat.pin.pin_thread.model;

import android.support.annotation.NonNull;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class PinUnpinThreadRequest extends GeneralRequestObject {
    private long threadId;

    PinUnpinThreadRequest(@NonNull Builder builder) {
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

        @NonNull
        public PinUnpinThreadRequest build() {
            return new PinUnpinThreadRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }
}
