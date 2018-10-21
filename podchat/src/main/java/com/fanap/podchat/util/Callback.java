package com.fanap.podchat.util;

public class Callback {
    private long offset;
    private int requestType;
    private boolean delivery;
    private boolean seen;
    private boolean sent;
    private boolean result;
    private String uniqueId;

    public Callback(long offset, int requestType, boolean delivery, boolean seen, boolean sent, boolean result) {
        this.offset = offset;
        this.requestType = requestType;
        this.delivery = delivery;
        this.sent = sent;
        this.seen = seen;
        this.result = result;
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

}
