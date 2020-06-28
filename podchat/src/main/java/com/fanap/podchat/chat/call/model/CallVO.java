package com.fanap.podchat.chat.call;

import com.fanap.podchat.mainmodel.Participant;

import java.sql.Timestamp;
import java.util.List;

public class CallVO {


    private long id;
    private long creatorId;
    private int type;
    private Timestamp createTime;
    private Timestamp startTime;
    private Timestamp endTime;
    private int status;
    private boolean isGroup;
    private List<Participant> callParticipants;
    private Participant partnerParticipant;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public List<Participant> getCallParticipants() {
        return callParticipants;
    }

    public void setCallParticipants(List<Participant> callParticipants) {
        this.callParticipants = callParticipants;
    }

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Participant getPartnerParticipant() {
        return partnerParticipant;
    }

    public void setPartnerParticipant(Participant partnerParticipant) {
        this.partnerParticipant = partnerParticipant;
    }
}
