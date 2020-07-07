package com.fanap.podchat.chat.call.result_model;

import com.fanap.podchat.chat.call.model.CreateCallVO;

public class CallRequestResult extends CreateCallVO {
    public CallRequestResult(CreateCallVO createCallVO) {
        this.setCreatorId(createCallVO.getCreatorId());
        this.setInvitees(createCallVO.getInvitees());
        this.setType(createCallVO.getType());
    }
}
