package com.fanap.podchat.chat.mention.model;

import com.fanap.podchat.requestobject.RequestGetHistory;

public class RequestGetMentionList extends RequestGetHistory {

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


    public static class Builder extends RequestGetHistory.Builder {

        private Boolean allMentioned;

        private Boolean unreadMentioned;

        private Long threadId;


        public Builder setAllMentioned(Boolean allMentioned) {
            this.allMentioned = allMentioned;
            return this;
        }

        public Builder setUnreadMentioned(Boolean unreadMentioned) {
            this.unreadMentioned = unreadMentioned;
            return this;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }  public Builder setThreadId(long threadId) {
            this.threadId = threadId;
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
