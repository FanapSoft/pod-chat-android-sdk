package com.fanap.podchat.requestobject;

public class RequestRemoveContact extends GeneralRequestObject {
    private long userId;

    RequestRemoveContact(Builder builder) {
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

        public RequestRemoveContact build() {
            return new RequestRemoveContact(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
