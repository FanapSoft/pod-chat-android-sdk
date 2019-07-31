package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestGetAdmin extends RequestThreadParticipant {

    private boolean admin;


    public RequestGetAdmin(Builder builder) {
        super(builder);
        this.admin = builder.admin;

    }


    public static class Builder extends RequestThreadParticipant.Builder  {

        boolean admin;

        public Builder(long threadId, boolean admin) {
            super(threadId);
            this.admin = admin;
        }

        public Builder() { }

        public Builder admin(boolean admin) {
            this.admin = admin;
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
}
