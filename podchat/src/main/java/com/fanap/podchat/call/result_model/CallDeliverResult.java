package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

public class CallDeliverResult {

   private CallParticipantVO callParticipantVO;


    public CallDeliverResult(CallParticipantVO callParticipantVO) {
        this.callParticipantVO = callParticipantVO;
    }

    public CallParticipantVO getCallParticipantVO() {
        return callParticipantVO;
    }
}
