package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CreateCallVO;

public class CallRequestResult extends CreateCallVO {
    public CallRequestResult(CreateCallVO createCallVO) {
        this.setCreatorId(createCallVO.getCreatorId());
        this.setInvitees(createCallVO.getInvitees());
        this.setType(createCallVO.getType());
        this.setCreatorVO(createCallVO.getCreatorVO());
    }
}
