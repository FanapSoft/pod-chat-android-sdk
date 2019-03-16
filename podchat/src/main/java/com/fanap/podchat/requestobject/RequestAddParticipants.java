package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class RequestAddParticipants extends GeneralRequestObject {
    private long threadId;
    private List<Long> contactIds;

    RequestAddParticipants(@NonNull Builder builder) {
        super(builder);
        this.contactIds = builder.contactIds;
        this.threadId = builder.threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private long threadId;
        private List<Long> contactIds;

        public Builder(long threadId, List<Long> contactIds) {
            this.contactIds = contactIds;
            this.threadId = threadId;
        }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }

        @NonNull
        public RequestAddParticipants build() {
            return new RequestAddParticipants(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public List<Long> getContactIds() {
        return contactIds;
    }

    public void setContactIds(List<Long> contactIds) {
        this.contactIds = contactIds;
    }


}
