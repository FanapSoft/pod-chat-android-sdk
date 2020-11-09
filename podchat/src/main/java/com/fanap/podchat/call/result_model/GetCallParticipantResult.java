package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.ArrayList;

public class GetCallParticipantResult {

    private ArrayList<CallParticipantVO> callParticipantVOS;
    private long threadId;


    public GetCallParticipantResult(ArrayList<CallParticipantVO> callParticipantVOS) {
        this.callParticipantVOS = callParticipantVOS;
    }


    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public ArrayList<CallParticipantVO> getCallParticipantVOS() {
        return callParticipantVOS;
    }

    public long getThreadId() {
        return threadId;
    }
}
