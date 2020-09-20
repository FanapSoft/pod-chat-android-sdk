package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RemoveContactRequest extends GeneralRequestObject {
    private long userId;

    RemoveContactRequest(@NonNull Builder builder) {
        super(builder);
        this.userId = builder.userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long userId;

        public Builder(long userId) {
            this.userId = userId;
        }

        @NonNull
        public RemoveContactRequest build() {
            return new RemoveContactRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }
}
