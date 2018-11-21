package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestThread extends BaseObject {

    private ArrayList<Integer> threadIds;
    private String threadName;
    private String typeCode;
    private long creatorCoreUserId;
    private long partnerCoreUserId;
    private long partnerCoreContactId;

    public RequestThread(Builder builder) {
        super(builder);
        this.threadIds = builder.threadIds;
        this.threadName = builder.threadName;
        this.typeCode = builder.typeCode;
        this.creatorCoreUserId = builder.creatorCoreUserId;
        this.partnerCoreUserId = builder.partnerCoreUserId;
        this.partnerCoreContactId = builder.partnerCoreContactId;
    }


    public static class Builder extends BaseObject.Builder<Builder> {
        private ArrayList<Integer> threadIds;
        private String threadName;
        private String typeCode;
        private long creatorCoreUserId;
        private long partnerCoreUserId;
        private long partnerCoreContactId;

        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder typeCode(String typeCode) {
            this.typeCode = typeCode;
            return this;
        }

        public Builder creatorCoreUserId(long creatorCoreUserId) {
            this.creatorCoreUserId = creatorCoreUserId;
            return this;
        }

        public Builder partnerCoreUserId(long partnerCoreUserId) {
            this.partnerCoreUserId = partnerCoreUserId;
            return this;
        }

        public Builder partnerCoreContactId(long partnerCoreContactId) {
            this.partnerCoreContactId = partnerCoreContactId;
            return this;
        }

        public Builder threadIds(ArrayList<Integer> threadIds) {
            this.threadIds = threadIds;
            return this;
        }


        public RequestThread build() {
            return new RequestThread(this);
        }

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

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
