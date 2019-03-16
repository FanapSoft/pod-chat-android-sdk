package com.fanap.podchat.model;


import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.mainmodel.MessageVO;

import java.util.List;

public class ResultHistory {

    private List<MessageVO> history;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    private List<Sending> sending;

    private List<Failed> failed;

    private List<Uploading> uploadingQueue;

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

    public List<Sending> getSending() {
        return sending;
    }

    public void setSending(List<Sending> sending) {
        this.sending = sending;
    }

    public List<Failed> getFailed() {
        return failed;
    }

    public void setFailed(List<Failed> failed) {
        this.failed = failed;
    }

    public List<Uploading> getUploadingQueue() {
        return uploadingQueue;
    }

    public void setUploadingQueue(List<Uploading> uploadingQueue) {
        this.uploadingQueue = uploadingQueue;
    }
}
