package com.fanap.podchat.call.result_model;

import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.chat.CoreConfig;

import java.util.ArrayList;

public class RemoveFromCallResult extends LeaveCallResult {

    boolean isUserRemoved = false;


    public boolean isUserRemoved() {
        return isUserRemoved;
    }

    public RemoveFromCallResult setUserRemoved(boolean userRemoved) {
        isUserRemoved = userRemoved;
        return this;
    }
}
