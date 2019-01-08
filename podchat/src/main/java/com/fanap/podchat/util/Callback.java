package com.fanap.podchat.util;

public class Callback {
    private long offset;
    private int requestType;
    private boolean delivery;
    private boolean seen;
    private boolean sent;
    private boolean result;
    private boolean metadataCriteria;
    private String uniqueId;
    private String order;
    private String query;
    private long count;
    private long firstMessageId;
    private long lastMessageId;

    private long fromTime;
    private long fromTimeNanos;
    private long toTime;
    private long toTimeNanos;

    private long messageId;

    public Callback(String order, long count, long offset, int requestType, boolean delivery, boolean seen, boolean sent, boolean result) {
        this.offset = offset;
        this.requestType = requestType;
        this.delivery = delivery;
        this.sent = sent;
        this.seen = seen;
        this.result = result;
        this.count = count;
        this.order = order;
    }

    public Callback(String order, long count, long offset, int requestType, long firstMessageId, long lastMessageId, boolean result) {
        this.offset = offset;
        this.requestType = requestType;
        this.lastMessageId = lastMessageId;
        this.firstMessageId = firstMessageId;
        this.result = result;
        this.count = count;
        this.order = order;
    }

    public Callback(String order, long count, long offset, int requestType, long messageId, boolean result) {
        this.offset = offset;
        this.requestType = requestType;
        this.messageId = messageId;
        this.result = result;
        this.count = count;
        this.order = order;
    }


    public Callback(String uniqueId, boolean delivery, boolean seen, boolean sent) {
        this.delivery = delivery;
        this.sent = sent;
        this.seen = seen;
        this.setUniqueId(uniqueId);
    }

    public Callback(boolean delivery, boolean seen, boolean sent) {
        this.delivery = delivery;
        this.sent = sent;
        this.seen = seen;
    }

    public Callback() {
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isMetadataCriteria() {
        return metadataCriteria;
    }

    public void setMetadataCriteria(boolean metadataCriteria) {
        this.metadataCriteria = metadataCriteria;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getFromTimeNanos() {
        return fromTimeNanos;
    }

    public void setFromTimeNanos(long fromTimeNanos) {
        this.fromTimeNanos = fromTimeNanos;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public long getToTimeNanos() {
        return toTimeNanos;
    }

    public void setToTimeNanos(long toTimeNanos) {
        this.toTimeNanos = toTimeNanos;
    }
}
