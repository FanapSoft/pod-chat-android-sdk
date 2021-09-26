package com.fanap.podchat.mainmodel;

import android.support.annotation.NonNull;

public class ListMessageCriteriaVO {

    private String query;
    private long count;
    private long offset;

    public String getQuery() {
        return query;
    }

    public long getCount() {
        return count;
    }

    public long getOffset() {
        return offset;
    }

    public ListMessageCriteriaVO(Builder builder) {
        this.query = builder.query;
        this.count = builder.count;
        this.offset = builder.offset;
    }

    public static class Builder {
        private String query;
        private long count = 50;
        private long offset = 0;

        public Builder(String query) {
            this.query = query;
        }


        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Builder count(long count) {
            this.count = count;
            return this;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        @NonNull
        public ListMessageCriteriaVO build() {
            return new ListMessageCriteriaVO(this);
        }

    }
}
