package com.fanap.podchat.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.PrimaryKey;


public class ConversationSummery {
    @Embedded(prefix = "ConversationSummery_")
    @PrimaryKey
    private long id;
    private String title;
    @Embedded(prefix = "ConversationSummery_")
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
