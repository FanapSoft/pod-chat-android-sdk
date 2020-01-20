package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestThreadParticipant extends GeneralRequestObject {
    private long count;
    private long offset;
    private long threadId;

    RequestThreadParticipant(@NonNull Builder builder) {
        super(builder);
        this.count = builder.count;
        this.offset = builder.offset;
        this.threadId = builder.threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long count;
        private long offset;
        private long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }


        public Builder() {
        }

        @NonNull
        public Builder count(long count) {
            this.count = count;
            return this;
        }

        @NonNull
        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public RequestThreadParticipant build() {
            return new RequestThreadParticipant(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
