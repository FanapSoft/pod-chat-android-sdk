package com.fanap.podchat.call.result_model;

import com.fanap.podchat.mainmodel.Participant;

public class CallDeliverResult {

   private Participant participant;


    public CallDeliverResult(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }
}
