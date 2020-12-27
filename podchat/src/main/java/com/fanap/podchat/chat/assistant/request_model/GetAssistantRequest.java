package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.GeneralRequestObject;
import com.fanap.podchat.requestobject.RequestRole;

import java.util.ArrayList;
import java.util.List;

public class GetAssistantRequest extends GeneralRequestObject {

    GetAssistantRequest(GetAssistantRequest.Builder builder) {
        super(builder);


    }



    public static class Builder extends GeneralRequestObject.Builder<GetAssistantRequest.Builder> {

        public Builder(){

        }

        public GetAssistantRequest build() {
            return new GetAssistantRequest(this);
        }

        @Override
        public Builder typeCode(String typeCode) {
            return super.typeCode(typeCode);
        }

        @Override
        protected GetAssistantRequest.Builder self() {
            return this;
        }
    }

}
