package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.GeneralRequestObject;
import com.fanap.podchat.requestobject.RequestRole;

import java.util.ArrayList;
import java.util.List;

public class DeActiveAssistantRequest extends GeneralRequestObject {
    List<AssistantVo> assistantVos;

    DeActiveAssistantRequest(DeActiveAssistantRequest.Builder builder) {
        super(builder);
        this.assistantVos = builder.assistantVos;

    }

    public List<AssistantVo> getAssistantVos() {
        return assistantVos;
    }

    public static class Builder extends GeneralRequestObject.Builder<DeActiveAssistantRequest.Builder> {

        List<AssistantVo> assistantVos;

        public Builder(List<AssistantVo> assistantVos) {
            this.assistantVos = assistantVos;
        }

        public DeActiveAssistantRequest build() {
            return new DeActiveAssistantRequest(this);
        }


        @Override
        protected DeActiveAssistantRequest.Builder self() {
            return this;
        }
    }

}
