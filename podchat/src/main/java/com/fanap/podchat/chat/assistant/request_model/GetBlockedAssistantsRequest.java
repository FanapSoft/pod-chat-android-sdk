package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class GetBlockedAssistantsRequest extends GeneralRequestObject {
    private long count;
    private long offset;

    GetBlockedAssistantsRequest(GetBlockedAssistantsRequest.Builder builder) {
        super(builder);

        this.count = builder.count;
        this.offset = builder.offset;
    }

    public long getCount() {
        return count;
    }

    public long getOffset() {
        return offset;
    }

    public static class Builder extends GeneralRequestObject.Builder<GetBlockedAssistantsRequest.Builder> {
        private long count;
        private long offset;

        public Builder() {

        }

        public GetBlockedAssistantsRequest build() {
            return new GetBlockedAssistantsRequest(this);
        }

        @Override
        public Builder typeCode(String typeCode) {
            return super.typeCode(typeCode);
        }

        public Builder setCount(long count) {
            this.count = count;
            return this;
        }

        public Builder setOffset(long offset) {
            this.offset = offset;
            return this;
        }

        @Override
        protected GetBlockedAssistantsRequest.Builder self() {
            return this;
        }
    }

}
