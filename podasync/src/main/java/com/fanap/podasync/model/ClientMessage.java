package com.fanap.podasync.model;

/**
 * Messages that send from server to client
 *
 * {@param senderId } shows that the message was received from which device(Peer id)
 * <p>
 * {@param type }type of message
 * {@param address} address of sender (if sender registered on your peer)
 * {@param origin} origin of sender (if sender registered on your peer)
 * {@param tracker}Id generate by platform (if message sent in sync mode)
 */
public class ClientMessage {
    private long id;
    private int type;
    private long senderMessageId;
    private long senderId;
    private long trackerId;
    private String senderName;
    private String content;
    private String address;
    private String origin;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderMessageId() {
        return senderMessageId;
    }

    public void setSenderMessageId(long senderMessageId) {
        this.senderMessageId = senderMessageId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(long trackerId) {
        this.trackerId = trackerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
