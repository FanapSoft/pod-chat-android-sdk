package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.util.List;

public class DeletedCallsFromHistory {

    private List<Long> callIds;


    public DeletedCallsFromHistory(List<Long> callIds) {
        this.callIds = callIds;
    }

    public List<Long> getCallIds() {
        return callIds;
    }

    public DeletedCallsFromHistory setCallIds(List<Long> callIds) {
        this.callIds = callIds;
        return this;
    }

}
