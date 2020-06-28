package com.fanap.podchat.chat.call.persist;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.fanap.podchat.mainmodel.Participant;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class CacheCall {

    private long id;
    private long creatorId;
    private int type;
    private long createTime;
    private long startTime;
    private long endTime;
    private int status;
    private boolean isGroup;

    @Ignore
    private List<Participant> callParticipants;

    @Ignore
    private Participant partnerParticipant;


    public void setId(long id) {
        this.id = id;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public void setCallParticipants(List<Participant> callParticipants) {
        this.callParticipants = callParticipants;
    }

    public void setPartnerParticipant(Participant partnerParticipant) {
        this.partnerParticipant = partnerParticipant;
    }

    public long getId() {
        return id;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public int getType() {
        return type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getStatus() {
        return status;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public List<Participant> getCallParticipants() {
        return callParticipants;
    }

    public Participant getPartnerParticipant() {
        return partnerParticipant;
    }
}
