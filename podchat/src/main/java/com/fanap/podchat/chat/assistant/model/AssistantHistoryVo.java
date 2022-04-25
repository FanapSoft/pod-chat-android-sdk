package com.fanap.podchat.chat.assistant.model;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class AssistantHistoryVo {

    @SerializedName("actionType")
    int actionType;

    @SerializedName("actionName")
    String actionName;

    @SerializedName("actionTime")
    long actionTime;

    @SerializedName("participantVO")
    Participant participantVO;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public Participant getParticipantVO() {
        return participantVO;
    }

    public void setParticipantVO(Participant participantVO) {
        this.participantVO = participantVO;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
