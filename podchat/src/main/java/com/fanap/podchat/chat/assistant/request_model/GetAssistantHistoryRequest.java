package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class GetAssistantHistoryRequest extends GeneralRequestObject {

    GetAssistantHistoryRequest(GetAssistantHistoryRequest.Builder builder) {
        super(builder);

    }

    public static class Builder extends GeneralRequestObject.Builder<GetAssistantHistoryRequest.Builder> {


        public Builder() {

        }

        public GetAssistantHistoryRequest build() {
            return new GetAssistantHistoryRequest(this);
        }

        @Override
        protected GetAssistantHistoryRequest.Builder self() {
            return this;
        }
    }

}
