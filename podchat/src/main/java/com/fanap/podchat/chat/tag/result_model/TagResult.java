package com.fanap.podchat.chat.tag.result_model;

import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.annotations.SerializedName;

public class TagResult {

    @SerializedName("id")
    long tagId;

    @SerializedName("name")
    String tagName;

    @SerializedName("owner")
    Participant owner;

    @SerializedName("active")
    boolean active;

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

    public void setOwner(Participant owner) {
        this.owner = owner;
    }
}
