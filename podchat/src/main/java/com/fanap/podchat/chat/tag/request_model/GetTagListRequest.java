package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class GetTagListRequest extends GeneralRequestObject {


    GetTagListRequest(GetTagListRequest.Builder builder) {
        super(builder);
    }


    public static class Builder extends GeneralRequestObject.Builder<GetTagListRequest.Builder> {


        public Builder() {

        }

        public GetTagListRequest build() {
            return new GetTagListRequest(this);
        }

        @Override
        protected GetTagListRequest.Builder self() {
            return this;
        }
    }
}
