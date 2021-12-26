package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.ArrayList;

public class MuteUnMuteCallParticipantResult {

    private ArrayList<CallParticipantVO> callParticipants;
    private long callId;


    public MuteUnMuteCallParticipantResult() {
    }
    public MuteUnMuteCallParticipantResult(ArrayList<CallParticipantVO> callParticipants) {
        this.callParticipants = callParticipants;
    }
    public MuteUnMuteCallParticipantResult setCallParticipants(ArrayList<CallParticipantVO> callParticipants) {
        this.callParticipants = callParticipants;
        return this;
    }
    public ArrayList<CallParticipantVO> getCallParticipants() {
        return callParticipants;
    }

    public long getCallId() {
        return callId;
    }

    public MuteUnMuteCallParticipantResult setCallId(long callId) {
        this.callId = callId;
        return this;
    }
}
