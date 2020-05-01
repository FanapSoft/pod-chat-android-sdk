package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestGetAdmin extends RequestThreadParticipant {

    private boolean admin;
    private long count;
    private int offset;


    public RequestGetAdmin(Builder builder) {
        super(builder);
        this.admin = builder.admin;
        this.count = builder.count;
        this.offset = builder.offset;

    }


    public static class Builder extends RequestThreadParticipant.Builder {

        public long count;
        public int offset;
        boolean admin = true;

        public Builder(long threadId, boolean admin) {
            super(threadId);
            this.admin = admin;
        }

        public Builder(long threadId, boolean admin,long count,int offset) {
            this.admin = admin;
            this.count = count;
            this.offset =offset;
        }

        public Builder(long threadId) {
            super(threadId);
        }

        public Builder admin(boolean admin) {
            this.admin = admin;

            return this;
        }


        public Builder count(long count) {
            this.count = count;
            super.count(count);
            return this;
        }


        public Builder offset(int offset) {
            this.offset = offset;
            super.offset(offset);
            return this;
        }


        @Override
        public Builder withNoCache() {
            super.withNoCache();
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            super.threadId(threadId);
            return this;
        }




        public RequestGetAdmin build() {
            return new RequestGetAdmin(this);
        }
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    @Override
    public long getCount() {
        return count;
    }

    @Override
    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
