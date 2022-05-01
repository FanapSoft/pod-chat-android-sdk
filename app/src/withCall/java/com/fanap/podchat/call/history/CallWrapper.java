package com.fanap.podchat.call.history;

import android.support.annotation.IntDef;

import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;

import java.util.List;
import java.util.Objects;

public class CallWrapper extends CallVO {


    @IntDef({CallItemType.HISTORY,CallItemType.ACTIVE})
    public @interface CallItemType{
        int HISTORY = 0;
        int ACTIVE = 1;
    }

    private int callItemType;


    public int getCallItemType() {
        return callItemType;
    }

    public void setCallItemType(int callItemType) {
        this.callItemType = callItemType;
    }


    public static CallWrapper fromCall(CallVO call) {
        CallWrapper wrapper = new CallWrapper();
        wrapper.setId(call.getId());
          wrapper.setCreatorId(call.getCreatorId());
          wrapper.setType(call.getType());
          wrapper.setCreateTime(call.getCreateTime());
          wrapper.setStartTime(call.getStartTime());
          wrapper.setEndTime(call.getEndTime());
          wrapper.setStatus(call.getStatus());
          wrapper.setGroup(call.isGroup());
          wrapper.setCallParticipants(call.getCallParticipants());
          wrapper.setConversationVO(call.getConversationVO());
          wrapper.setPartnerParticipantVO(call.getPartnerParticipantVO());
        return wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallWrapper that = (CallWrapper) o;
        return ((this.getId() == that.getId()) && (this.getCreateTime() == that.getCreateTime()));

    }


}
