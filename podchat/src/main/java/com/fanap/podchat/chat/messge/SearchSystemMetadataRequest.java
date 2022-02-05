package com.fanap.podchat.chat.messge;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;
import com.fanap.podchat.requestobject.BaseRequestObject;

public class SearchSystemMetadataRequest extends BaseRequestObject {
    private long messageThreadId;
    private long userId;
    private Long firstMessageId;
    private Long lastMessageId;
    private Long id;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;
    private String order;

    public SearchSystemMetadataRequest(Builder builder) {
        this.messageThreadId = builder.messageThreadId;
        this.userId = builder.userId;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
        this.id = builder.id;
        this.query = builder.query;
        this.order = builder.order;
        this.metadataCriteria = builder.metadataCriteria;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private final long messageThreadId;
        private long userId;
        private Long firstMessageId;
        private Long lastMessageId;
        private Long id;
        private String query;
        private NosqlSearchMetadataCriteria metadataCriteria;
        private String order;

        public Builder(long threadId) {
            this.messageThreadId = threadId;
        }

        @NonNull
        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        @NonNull
        public Builder firstMessageId(Long firstMessageId) {
            this.firstMessageId = firstMessageId;
            return this;
        }

        @NonNull
        public Builder lastMessageId(Long lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }


        @Override
        protected Builder self() {
            return this;
        }
        @NonNull
        public Builder order(String order) {
            this.order = order;
            return this;
        }

        @NonNull
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        @NonNull
        public Builder metadataCriteria(NosqlSearchMetadataCriteria metadataCriteria) {
            this.metadataCriteria = metadataCriteria;
            return this;
        }

        @NonNull
        public SearchSystemMetadataRequest build() {
            return new SearchSystemMetadataRequest(this);
        }
    }

    public long getMessageThreadId() {
        return messageThreadId;
    }

    public void setMessageThreadId(long messageThreadId) {
        this.messageThreadId = messageThreadId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(Long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public NosqlSearchMetadataCriteria getMetadataCriteria() {
        return metadataCriteria;
    }

    public void setMetadataCriteria(NosqlSearchMetadataCriteria metadataCriteria) {
        this.metadataCriteria = metadataCriteria;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
