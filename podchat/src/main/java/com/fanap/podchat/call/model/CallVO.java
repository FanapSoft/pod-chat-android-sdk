package com.fanap.podchat.call.model;

import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;

import java.util.List;

public class CallVO {


    private long id;
    private long creatorId;
    private int type;
    private long createTime;
    private long startTime;
    private long endTime;
    private int status;
    private boolean isGroup;
    private List<Participant> callParticipants;
    private Thread conversationVO;
    private Participant partnerParticipantVO;



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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
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

    public Participant getPartnerParticipantVO() {
        return partnerParticipantVO;
    }

    public void setPartnerParticipantVO(Participant partnerParticipantVO) {
        this.partnerParticipantVO = partnerParticipantVO;
    }

    public Thread getConversationVO() {
        return conversationVO;
    }

    public void setConversationVO(Thread conversationVO) {
        this.conversationVO = conversationVO;
    }
}
