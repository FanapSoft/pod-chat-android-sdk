package com.fanap.podasync.model;

public class MessageVo {

    private String peerName;
    private String content;
    private long[] receivers;
    private long messageId;
    private long ttl = 10 * 60 * 1000;

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
