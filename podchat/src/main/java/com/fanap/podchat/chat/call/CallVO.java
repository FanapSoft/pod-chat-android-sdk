package com.fanap.podchat.chat.call;

import com.fanap.podchat.mainmodel.Invitee;

import java.io.Serializable;
import java.util.List;

public class CallVO implements Serializable {

    private List<Invitee> invitees;
    private int type;
    private long creatorId;
    private long partnerId;


    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }


}
