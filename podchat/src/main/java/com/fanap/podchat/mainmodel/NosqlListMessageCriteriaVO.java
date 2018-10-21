package com.fanap.podchat.mainmodel;

public class NosqlListMessageCriteriaVO {
    private long messageThreadId;
    private long userId;
    private Long firstMessageId;
    private Long lastMessageId;
    private Long id;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;
    private long offset;
    private int count;
    private String order;

    public NosqlListMessageCriteriaVO(Builder builder) {
        this.messageThreadId = builder.messageThreadId;
        this.userId = builder.userId;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
        this.id = builder.id;
        this.query = builder.query;
        this.offset = builder.offset;
        this.count = builder.count;
        this.order = builder.order;
        this.metadataCriteria = builder.metadataCriteria;
    }

    public static class Builder {
        private final long messageThreadId;
        private long userId;
        private Long firstMessageId;
        private Long lastMessageId;
        private Long id;
        private String query;
        private NosqlSearchMetadataCriteria metadataCriteria;
        private long offset;
        private int count;
        private String order;

        public Builder(long threadId) {
            this.messageThreadId = threadId;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder firstMessageId(Long firstMessageId) {
            this.firstMessageId = firstMessageId;
            return this;
        }

        public Builder lastMessageId(Long lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder order(String order) {
            this.order = order;
            return this;
        }

        public Builder id(Long id) {
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

        public NosqlListMessageCriteriaVO build() {
            return new NosqlListMessageCriteriaVO(this);
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

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
