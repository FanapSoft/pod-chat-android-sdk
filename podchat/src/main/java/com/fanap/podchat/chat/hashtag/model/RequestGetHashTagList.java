package com.fanap.podchat.chat.hashtag.model;

import com.fanap.podchat.requestobject.BaseRequestObject;

public class RequestGetHashTagList extends BaseRequestObject {


    private Long threadId;
    private String hashtag;

    public long getThreadId() {
        return threadId;
    }

    public RequestGetHashTagList(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.hashtag = builder.hashtag;
    }

    public String getHashtag() {
        return hashtag;
    }

    public static class Builder extends BaseRequestObject.Builder {

        private Long threadId;
        private String hashtag;

        public Builder(long threadId) {
            this.threadId = threadId;
        }


        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }


        public Builder setHashtag(String hashtag) {
            this.hashtag = hashtag;
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
        public RequestGetHashTagList build() {
            return new RequestGetHashTagList(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
