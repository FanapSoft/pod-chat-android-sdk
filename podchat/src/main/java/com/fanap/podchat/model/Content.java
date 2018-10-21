package com.fanap.podchat.model;

import com.google.gson.annotations.SerializedName;

public class Content {
    private int count;
    private int offset;
    private String name;
    private int[] threadIds;

    @SerializedName("new")
    private
    boolean New;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getThreadIds() {
        return threadIds;
    }

    public void setThreadIds(int[] threadIds) {
        this.threadIds = threadIds;
    }

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean aNew) {
        New = aNew;
    }
}
