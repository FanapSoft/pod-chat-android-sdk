package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.GeneralRequestObject;
import com.fanap.podchat.requestobject.RequestRole;

import java.util.ArrayList;
import java.util.List;

public class GetAssistantRequest extends GeneralRequestObject {
    private long count;
    private long offset;

    GetAssistantRequest(GetAssistantRequest.Builder builder) {
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

    public static class Builder extends GeneralRequestObject.Builder<GetAssistantRequest.Builder> {
        private long count;
        private long offset;

        public Builder() {

        }

        public GetAssistantRequest build() {
            return new GetAssistantRequest(this);
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
        protected GetAssistantRequest.Builder self() {
            return this;
        }
    }

}
