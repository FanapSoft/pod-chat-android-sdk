package com.fanap.podasync.model;

/**
 * {@param peerName } name of receiver peer
 * {@param receivers} array of receiver peer ids (if you use this, peerName will be ignored)
 * {@param priority} priority of message 1-10, lower has more priority
 * {@param ttl} time to live for message in millisecond
 */
public class Message {

    private String peerName;
    private String content;
    private long[] receivers;
    private int priority;
    private long messageId;
    private long ttl;

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long[] getReceivers() {
        return receivers;
    }

    public void setReceivers(long[] receivers) {
        this.receivers = receivers;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
