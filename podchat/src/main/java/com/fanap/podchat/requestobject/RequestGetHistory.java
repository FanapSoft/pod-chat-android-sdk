package com.fanap.podchat.requestobject;

import com.fanap.podchat.mainmodel.NosqlSearchMetadataCriteria;

public class RequestGetHistory extends BaseRequestObject {
    private long threadId;
    private String order;
    private long userId;
    private long id;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;
    private long firstMessageId;
    private long lastMessageId;

    RequestGetHistory(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.order = builder.order;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private long threadId;
        private String order;
        private long firstMessageId;
        private long lastMessageId;
        private long userId;
        private long id;
        private String query;
        private NosqlSearchMetadataCriteria metadataCriteria;

        public Builder(long threadId) {
            this.threadId = threadId;
        }

        public Builder firstMessageId(long firstMessageId) {
            this.firstMessageId = firstMessageId;
            return this;
        }

        public Builder lastMessageId(long lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }

        public Builder order(String order) {
            this.order = order;
            return this;
        }

        //    private NosqlSearchMetadataCriteria metadataCriteria;
        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder metadataCriteria(NosqlSearchMetadataCriteria metadataCriteria) {
            this.metadataCriteria = metadataCriteria;
            return this;
        }

        public RequestGetHistory build() {
            return new RequestGetHistory(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
