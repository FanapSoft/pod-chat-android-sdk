package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagVo {

    @SerializedName("id")
    long tagId;

    @SerializedName("name")
    String tagName;

    @SerializedName("owner")
    Participant owner;

    @SerializedName("active")
    boolean active;

    @SerializedName("tagParticipantVOList")
    List<TagParticipantVO> tagParticipants;

    private long allUnreadCount = 0;

    public TagVo(long tagId, String tagName, boolean active) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Participant getOwner() {
        return owner;
    }

    public long getAllUnreadCount() {
        return allUnreadCount;
    }

    public void setAllUnreadCount(long allUnreadCount) {
        this.allUnreadCount = allUnreadCount;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
    }

    public List<TagParticipantVO> getTagParticipants() {
        return tagParticipants;
    }

    public void setTagParticipants(List<TagParticipantVO> tagParticipants) {
        this.tagParticipants = tagParticipants;
    }
}
