package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

public class CallCancelResult {

    private CallParticipantVO callParticipant;


    public CallCancelResult(CallParticipantVO callParticipant) {
        this.callParticipant = callParticipant;
    }

    public CallParticipantVO getCallParticipant() {
        return callParticipant;
    }

    public CallCancelResult setCallParticipant(CallParticipantVO callParticipant) {
        this.callParticipant = callParticipant;
        return this;
    }
}
