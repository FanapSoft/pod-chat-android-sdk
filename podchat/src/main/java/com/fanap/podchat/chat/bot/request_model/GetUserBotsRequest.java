package com.fanap.podchat.chat.bot.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class GetUserBotsRequest extends GeneralRequestObject {

    GetUserBotsRequest(Builder builder) {
        super(builder);
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        public Builder() {

        }

        public GetUserBotsRequest build() {
            return new GetUserBotsRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}