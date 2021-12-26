package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.List;

public class JoinCallParticipantResult {

    private long callId;

    private List<CallParticipantVO> joinedParticipants;

    public JoinCallParticipantResult() {
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public List<CallParticipantVO> getJoinedParticipants() {
        return joinedParticipants;
    }

    public void setJoinedParticipants(List<CallParticipantVO> joinedParticipants) {
        this.joinedParticipants = joinedParticipants;
    }
}
