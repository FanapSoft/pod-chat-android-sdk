package com.fanap.podchat.call.model;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;

import java.io.Serializable;
import java.util.List;

public class CreateCallVO implements Serializable {

    private List<Invitee> invitees;
    private int type;
    private long creatorId;
    private Participant creatorVO;
    private Thread conversationVO;
    private long threadId;
    private boolean isGroup;


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

    public Participant getCreatorVO() {
        return creatorVO;
    }

    public void setCreatorVO(Participant creatorVO) {
        this.creatorVO = creatorVO;
    }

    public Thread getConversationVO() {
        return conversationVO;
    }

    public void setConversationVO(Thread conversationVO) {
        this.conversationVO = conversationVO;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
