package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

public class LeaveCallResult {

    private long callId;

    private CallParticipantVO callParticipantVO;


    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public CallParticipantVO getCallParticipantVO() {
        return callParticipantVO;
    }

    public void setCallParticipantVO(CallParticipantVO callParticipantVO) {
        this.callParticipantVO = callParticipantVO;
    }

}
