package com.fanap.podchat.call.result_model;

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
