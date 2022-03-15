package com.fanap.podchat.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ConversationSummery {
    @PrimaryKey
    private long id;
    private String title;
    private String metadata;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
