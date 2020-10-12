package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

public class CallCancelResult extends CallDeliverResult {
    public CallCancelResult(CallParticipantVO callParticipantVO) {
        super(callParticipantVO);
    }
}
