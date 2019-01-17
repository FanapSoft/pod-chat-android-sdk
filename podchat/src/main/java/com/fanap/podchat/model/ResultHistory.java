package com.fanap.podchat.model;


import com.fanap.podchat.cachemodel.queue.SendingQueue;
import com.fanap.podchat.cachemodel.queue.UploadingQueue;
import com.fanap.podchat.cachemodel.queue.WaitQueue;
import com.fanap.podchat.mainmodel.MessageVO;

import java.util.List;

public class ResultHistory {

    private List<MessageVO> history;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    private List<SendingQueue> sendingQueue;

    private List<WaitQueue> failedQueue;

    private List<UploadingQueue> uploadingQueue;

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

    public List<SendingQueue> getSendingQueue() {
        return sendingQueue;
    }

    public void setSendingQueue(List<SendingQueue> sendingQueue) {
        this.sendingQueue = sendingQueue;
    }

    public List<WaitQueue> getFailedQueue() {
        return failedQueue;
    }

    public void setFailedQueue(List<WaitQueue> failedQueue) {
        this.failedQueue = failedQueue;
    }

    public List<UploadingQueue> getUploadingQueue() {
        return uploadingQueue;
    }

    public void setUploadingQueue(List<UploadingQueue> uploadingQueue) {
        this.uploadingQueue = uploadingQueue;
    }
}
