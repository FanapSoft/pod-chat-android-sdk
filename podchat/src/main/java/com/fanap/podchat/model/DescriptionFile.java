package com.fanap.podchat.model;

import java.util.ArrayList;

public class DescriptionFile {
    private String userMetadata;
    private String description;
    private ArrayList tags;

    public String getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(String userMetadata) {
        this.userMetadata = userMetadata;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList getTags() {
        return tags;
    }

    public void setTags(ArrayList tags) {
        this.tags = tags;
    }
}
