package com.fanap.podchat.mainmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/** [ asc | desc ] change sort of order message. default is desc */
public class ChatMessageContent {
    private long count;
    private String name;
    @SerializedName("new")
    private boolean New;
    private int firstMessageId;
    private int lastMessageId;
    private long offset;
    private String order;
    private List<Integer> threadIds;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(int firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean aNew) {
        New = aNew;
    }

    public int getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public List<Integer> getThreadIds() {
        return threadIds;
    }

    public void setThreadIds(ArrayList<Integer> threadIds) {
        this.threadIds = threadIds;
    }
}
