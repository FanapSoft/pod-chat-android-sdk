package com.fanap.podchat.model;


import java.util.List;

public class ResultHistory {

    private List<MessageVO> history;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<MessageVO> getHistory() {
        return history;
    }

    public void setHistory(List<MessageVO> history) {
        this.history = history;
    }
}
