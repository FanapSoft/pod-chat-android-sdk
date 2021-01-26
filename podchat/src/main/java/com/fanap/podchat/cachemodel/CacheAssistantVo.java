package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.util.List;

@Entity
public class CacheAssistantVo {

    //TODO
    //inviteeId == participantVOId --- One of these can be deleted from db
    @PrimaryKey()
    private long inviteeId;

    private List<String> roles;

    String contactType;

    private long participantVOId;

    public long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public long getParticipantVOId() {
        return participantVOId;
    }

    public void setParticipantVOId(long participantVOId) {
        this.participantVOId = participantVOId;
    }
}
