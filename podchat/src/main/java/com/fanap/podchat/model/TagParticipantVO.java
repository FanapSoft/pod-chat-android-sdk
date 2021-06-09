package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Thread;
import com.google.gson.annotations.SerializedName;

public class TagParticipantVO {
    @SerializedName("id")
    private Long id;
    @SerializedName("active")
    private boolean active = true;
    @SerializedName("threadId")
    private Long threadId;
    @SerializedName("conversationVO")
    private Thread conversationVO;


    public TagParticipantVO(Long id, boolean active, Long threadId) {
        this.id = id;
        this.active = active;
        this.threadId = threadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Thread getConversationVO() {
        return conversationVO;
    }

    public void setConversationVO(Thread conversationVO) {
        this.conversationVO = conversationVO;
    }

}
