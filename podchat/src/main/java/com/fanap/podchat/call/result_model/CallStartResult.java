package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.ArrayList;

public class CallStartResult {

    private String callName;
    private String callImage;


    private ArrayList<CallParticipantVO> callPartners;

    public CallStartResult(String callName, String callImage, ArrayList<CallParticipantVO> callPartners) {
        this.callName = callName;
        this.callImage = callImage;
        this.callPartners = callPartners;
    }

    public String getCallName() {
        return callName;
    }

    public ArrayList<CallParticipantVO> getCallPartners() {
        return callPartners;
    }

    public String getCallImage() {
        return callImage;
    }
}
