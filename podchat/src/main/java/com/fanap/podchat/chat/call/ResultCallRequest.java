package com.fanap.podchat.chat.call;

public class ResultCallRequest extends CallVO {
    public ResultCallRequest(CallVO callVO) {
        this.setCreatorId(callVO.getCreatorId());
        this.setPartnerId(callVO.getPartnerId());
        this.setInvitees(callVO.getInvitees());
        this.setType(callVO.getType());
    }
}
