package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.requestobject.GeneralRequestObject;
import java.util.List;

public class RegisterAssistantRequest extends GeneralRequestObject {

    List<AssistantVo> assistantVos;

    RegisterAssistantRequest(RegisterAssistantRequest.Builder builder) {
        super(builder);
        this.assistantVos = builder.assistantVos;

    }

    public List<AssistantVo> getAssistantVos() {
        return assistantVos;
    }

    public static class Builder extends GeneralRequestObject.Builder<RegisterAssistantRequest.Builder> {
        List<AssistantVo> assistantVos;

        public Builder(List<AssistantVo> assistantVos) {
            this.assistantVos = assistantVos;
        }

        public RegisterAssistantRequest build() {
            return new RegisterAssistantRequest(this);
        }

        @Override
        protected RegisterAssistantRequest.Builder self() {
            return this;
        }
    }

}
