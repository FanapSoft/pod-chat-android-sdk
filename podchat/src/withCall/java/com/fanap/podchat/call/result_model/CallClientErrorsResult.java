package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallErrorVO;

public class CallClientErrorsResult {

    CallErrorVO callErrorVO;

    private long callId;

    public CallClientErrorsResult(CallErrorVO callErrorVO) {
        this.callErrorVO = callErrorVO;
    }

    public CallErrorVO getCallErrorVO() {
        return callErrorVO;
    }

    public void setCallErrorVO(CallErrorVO callErrorVO) {
        this.callErrorVO = callErrorVO;
    }


    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }
}
