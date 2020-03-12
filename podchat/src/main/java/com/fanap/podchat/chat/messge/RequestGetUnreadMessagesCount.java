package com.fanap.podchat.chat.messge;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class RequestGetUnreadMessagesCount extends GeneralRequestObject {


    public static class Builder extends GeneralRequestObject.Builder<Builder> {


        @Override
        public RequestGetUnreadMessagesCount build() {
            return new RequestGetUnreadMessagesCount();
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
