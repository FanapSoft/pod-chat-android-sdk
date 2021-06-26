package com.fanap.podchat.chat.assistant.request_model;

import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class BlockUnblockAssistantRequest extends GeneralRequestObject {
   private List<AssistantVo> assistantVos;
    private boolean toBeBlocked;

    BlockUnblockAssistantRequest(BlockUnblockAssistantRequest.Builder builder) {
        super(builder);
        this.assistantVos = builder.assistantVos;
        this.toBeBlocked = builder.toBeBlocked;

    }

    public List<AssistantVo> getAssistantVos() {
        return assistantVos;
    }

    public static class Builder extends GeneralRequestObject.Builder<BlockUnblockAssistantRequest.Builder> {

        private List<AssistantVo> assistantVos;
        private boolean toBeBlocked;

        public Builder(List<AssistantVo> assistantVos, boolean toBeBlocked) {
            this.assistantVos = assistantVos;
            this.toBeBlocked = toBeBlocked;
        }

        public BlockUnblockAssistantRequest build() {
            return new BlockUnblockAssistantRequest(this);
        }


        @Override
        protected BlockUnblockAssistantRequest.Builder self() {
            return this;
        }
    }

}
