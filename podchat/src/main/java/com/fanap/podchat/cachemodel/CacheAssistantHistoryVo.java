package com.fanap.podchat.cachemodel;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CacheAssistantHistoryVo {

    @PrimaryKey()
    private long actionTime;

    private int actionType;

    private String actionName;

    private long participantVOId;

    public long getParticipantVOId() {
        return participantVOId;
    }

    public void setParticipantVOId(long participantVOId) {
        this.participantVOId = participantVOId;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }



}
