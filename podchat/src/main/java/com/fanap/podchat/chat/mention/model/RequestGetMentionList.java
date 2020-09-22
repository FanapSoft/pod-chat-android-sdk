package com.fanap.podchat.chat.mention.model;

import com.fanap.podchat.requestobject.BaseRequestObject;

public class RequestGetMentionList extends BaseRequestObject {

    private Boolean allMentioned;

    private Boolean unreadMentioned;

    private Long threadId;

    public Boolean getAllMentioned() {
        return allMentioned;
    }

    public Boolean getUnreadMentioned() {
        return unreadMentioned;
    }

    public long getThreadId() {
        return threadId;
    }

    public RequestGetMentionList(Builder builder) {
        super(builder);
        this.allMentioned = builder.allMentioned;
        this.unreadMentioned = builder.unreadMentioned;
        this.threadId = builder.threadId;
    }


    public static class Builder extends BaseRequestObject.Builder {

        private Boolean allMentioned = true;

        private Boolean unreadMentioned = false;

        private Long threadId;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public Builder setAllMentioned(Boolean allMentioned) {
            this.allMentioned = allMentioned;
            return this;
        }

        public Builder setUnreadMentioned(Boolean unreadMentioned) {
            this.unreadMentioned = unreadMentioned;
            return this;
        }

        public Builder unreadMentions() {
            this.unreadMentioned = true;
            return this;
        }

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }


        @Override
        public Builder count(long count) {
            super.count(count);
            return this;
        }

        @Override
        public Builder offset(long offset) {
            super.offset(offset);
            return this;
        }

        @Override
        public Builder withNoCache() {
            super.withNoCache();
            return this;
        }

        @Override
        public BaseRequestObject.Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        public RequestGetMentionList build() {
            return new RequestGetMentionList(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
