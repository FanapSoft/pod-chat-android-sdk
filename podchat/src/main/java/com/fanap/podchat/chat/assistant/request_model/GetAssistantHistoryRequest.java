package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class GetAssistantHistoryRequest extends GeneralRequestObject {
    private final long count;
    private final long offset;

    GetAssistantHistoryRequest(GetAssistantHistoryRequest.Builder builder) {
        super(builder);
        count = builder.count;
        offset = builder.offset;
    }

    public long getCount() {
        return count;
    }

    public long getOffset() {
        return offset;
    }

    public static class Builder extends GeneralRequestObject.Builder<GetAssistantHistoryRequest.Builder> {
        private long count;
        private long offset;

        public Builder() {

        }

        public GetAssistantHistoryRequest build() {
            return new GetAssistantHistoryRequest(this);
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
        protected GetAssistantHistoryRequest.Builder self() {
            return this;
        }
    }

}
