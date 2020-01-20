package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

import com.fanap.chatcore.model.base.BaseRequestObject;

import java.util.ArrayList;

public class RequestThread extends BaseRequestObject {

    private ArrayList<Integer> threadIds;
    private String threadName;
    private long creatorCoreUserId;
    private long partnerCoreUserId;
    private long partnerCoreContactId;

     RequestThread(@NonNull Builder builder) {
        super(builder);
        this.threadIds = builder.threadIds;
        this.threadName = builder.threadName;
        this.creatorCoreUserId = builder.creatorCoreUserId;
        this.partnerCoreUserId = builder.partnerCoreUserId;
        this.partnerCoreContactId = builder.partnerCoreContactId;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private ArrayList<Integer> threadIds;
        private String threadName;
        private long creatorCoreUserId;
        private long partnerCoreUserId;
        private long partnerCoreContactId;

        @NonNull
        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        @NonNull
        public Builder creatorCoreUserId(long creatorCoreUserId) {
            this.creatorCoreUserId = creatorCoreUserId;
            return this;
        }

        @NonNull
        public Builder partnerCoreUserId(long partnerCoreUserId) {
            this.partnerCoreUserId = partnerCoreUserId;
            return this;
        }

        @NonNull
        public Builder partnerCoreContactId(long partnerCoreContactId) {
            this.partnerCoreContactId = partnerCoreContactId;
            return this;
        }

        @NonNull
        public Builder threadIds(ArrayList<Integer> threadIds) {
            this.threadIds = threadIds;
            return this;
        }


        @NonNull
        public RequestThread build() {
            return new RequestThread(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public ArrayList<Integer> getThreadIds() {
        return threadIds;
    }

    public long getCreatorCoreUserId() {
        return creatorCoreUserId;
    }

    public void setCreatorCoreUserId(long creatorCoreUserId) {
        this.creatorCoreUserId = creatorCoreUserId;
    }

    public long getPartnerCoreUserId() {
        return partnerCoreUserId;
    }

    public void setPartnerCoreUserId(long partnerCoreUserId) {
        this.partnerCoreUserId = partnerCoreUserId;
    }

    public long getPartnerCoreContactId() {
        return partnerCoreContactId;
    }

    public void setPartnerCoreContactId(long partnerCoreContactId) {
        this.partnerCoreContactId = partnerCoreContactId;
    }

    public void setThreadIds(ArrayList<Integer> threadIds) {
        this.threadIds = threadIds;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
