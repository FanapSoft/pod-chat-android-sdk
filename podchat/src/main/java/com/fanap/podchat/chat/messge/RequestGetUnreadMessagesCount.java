package com.fanap.podchat.chat.messge;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class RequestGetUnreadMessagesCount extends GeneralRequestObject {

    private boolean mute;

    public RequestGetUnreadMessagesCount(Builder builder) {
        super(builder);
        this.mute = builder.mute;
    }

    public boolean withMuteThreads() {
        return mute;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private boolean mute;

        public Builder withMuteThreads() {
            this.mute = true;
            return this;
        }

        @Override
        public RequestGetUnreadMessagesCount build() {
            return new RequestGetUnreadMessagesCount(this);
        }

        @Override
        public Builder withNoCache() {
            super.withNoCache();
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


}
