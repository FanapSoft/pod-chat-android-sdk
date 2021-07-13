package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class DeleteCallFromHistoryRequest extends GeneralRequestObject {

    private List<Long> callIds;
    private DeleteCallFromHistoryRequest(Builder builder) {
        this.callIds = builder.callIds;
    }

    public DeleteCallFromHistoryRequest() {

    }

    public List<Long> getCallIds() {
        return callIds;
    }

    public static class Builder extends GeneralRequestObject.Builder{

        private List<Long> callIds;
        public Builder setCallIds(List<Long> callIds) {
            this.callIds = callIds;
            return this;
        }

        public List<Long> getCallIds() {
            return callIds;
        }

        @Override
        public DeleteCallFromHistoryRequest build() {

            return new DeleteCallFromHistoryRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public GeneralRequestObject.Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

    }
}
