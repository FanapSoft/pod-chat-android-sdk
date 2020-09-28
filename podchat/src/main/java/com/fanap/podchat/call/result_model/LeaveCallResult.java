package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.ArrayList;

public class LeaveCallResult {

    private long callId;

    private ArrayList<CallParticipantVO> callParticipants;


    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    public ArrayList<CallParticipantVO> getCallParticipants() {
        return callParticipants;
    }

    public void setCallParticipants(ArrayList<CallParticipantVO> callParticipants) {
        this.callParticipants = callParticipants;
    }

}
