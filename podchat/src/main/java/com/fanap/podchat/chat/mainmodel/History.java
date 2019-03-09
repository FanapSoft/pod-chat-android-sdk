package com.fanap.podchat.chat.mainmodel;

import android.support.annotation.NonNull;

public class History {
    private long id;

    @Deprecated
    private long firstMessageId;
    @Deprecated
    private long lastMessageId;

    private long fromTime;
    private long fromTimeNanos;
    private long toTime;
    private long toTimeNanos;
    private long offset;
    private long count;
    private String order;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;

    public History(Builder builder) {
        this.count = builder.count;
        this.offset = builder.offset;
        this.order = builder.order;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
        this.id = builder.id;
        this.query = builder.query;
        this.metadataCriteria = builder.metadataCriteria;
        this.fromTime = builder.fromTime;
        this.fromTimeNanos = builder.fromTimeNanos;
        this.toTime = builder.toTime;
        this.toTimeNanos = builder.toTimeNanos;
    }

    private History() {
    }

    public static class Builder {
        private long offset;
        private long count;
        private String order;
        private long firstMessageId;
        private long lastMessageId;
        private NosqlSearchMetadataCriteria metadataCriteria;
        private long id;
        private String query;
        private long fromTime;
        private long fromTimeNanos;
        private long toTime;
        private long toTimeNanos;

        @NonNull
        public Builder id(long id) {
            this.id = id;
            return this;
        }

        @NonNull
        public Builder fromTime(long fromTime) {
            this.fromTime = fromTime;
            return this;
        }

        @NonNull
        public Builder fromTimeNanos(long fromTimeNanos) {
            this.fromTimeNanos = fromTimeNanos;
            return this;
        }

        @NonNull
        public Builder toTime(long toTime) {
            this.toTime = toTime;
            return this;
        }


        @NonNull
        public Builder toTimeNanos(long toTimeNanos) {
            this.toTimeNanos = toTimeNanos;
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
        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        @NonNull
        public Builder count(long count) {
            this.count = count;
            return this;
        }

        @NonNull
        public Builder order(String order) {
            this.order = order;
            return this;
        }

        @NonNull
        @Deprecated
        public Builder firstMessageId(long firstMessageId) {
            this.firstMessageId = firstMessageId;
            return this;
        }

        @NonNull
        @Deprecated
        public Builder lastMessageId(long lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }

        @NonNull
        public History build() {
            return new History(this);
        }
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getFromTimeNanos() {
        return fromTimeNanos;
    }

    public void setFromTimeNanos(long fromTimeNanos) {
        this.fromTimeNanos = fromTimeNanos;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public long getToTimeNanos() {
        return toTimeNanos;
    }

    public void setToTimeNanos(long toTimeNanos) {
        this.toTimeNanos = toTimeNanos;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
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