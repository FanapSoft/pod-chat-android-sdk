package com.fanap.podchat.chat.call.persist;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.chat.call.model.CallVO;
import com.fanap.podchat.mainmodel.Participant;

import java.util.List;

@Entity
public class CacheCall {

    @PrimaryKey
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

    private long partnerParticipantId;

    @Ignore
    private Participant partnerParticipantVO;


    public CacheCall() {
    }

    public CacheCall(long id, long creatorId, int type, long createTime, long startTime, long endTime, int status, boolean isGroup, List<Participant> callParticipants, long partnerParticipantId, Participant partnerParticipantVO) {
        this.id = id;
        this.creatorId = creatorId;
        this.type = type;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.isGroup = isGroup;
        this.callParticipants = callParticipants;
        this.partnerParticipantId = partnerParticipantId;
        this.partnerParticipantVO = partnerParticipantVO;
    }

    public CacheCall fromCall(CallVO call) {
        this.id = call.getId();
        this.creatorId = call.getCreatorId();
        this.type = call.getType();
        this.createTime = call.getCreateTime();
        this.startTime = call.getStartTime();
        this.endTime = call.getEndTime();
        this.status = call.getStatus();
        this.isGroup = call.isGroup();
        this.callParticipants = call.getCallParticipants();
        this.partnerParticipantVO = call.getPartnerParticipantVO();
        this.partnerParticipantId = call.getPartnerParticipantVO() != null ?
                call.getPartnerParticipantVO().getId() : 0;

        return this;
    }

    public long getPartnerParticipantId() {
        return partnerParticipantId;
    }

    public void setPartnerParticipantId(long partnerParticipantId) {
        this.partnerParticipantId = partnerParticipantId;
    }

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

    public void setPartnerParticipantVO(Participant partnerParticipantVO) {
        this.partnerParticipantVO = partnerParticipantVO;
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

    public Participant getPartnerParticipantVO() {
        return partnerParticipantVO;
    }

    public CallVO toCallVo() {

        CallVO callVO = new CallVO();

        callVO.setPartnerParticipantVO(this.partnerParticipantVO);
        callVO.setCallParticipants(this.callParticipants);
        callVO.setCreateTime(this.createTime);
        callVO.setCreatorId(this.creatorId);
        callVO.setEndTime(this.endTime);
        callVO.setGroup(this.isGroup);
        callVO.setType(this.type);
        callVO.setId(this.id);
        callVO.setStartTime(this.startTime);
        callVO.setStatus(this.status);

        return callVO;
    }
}
