package com.fanap.podchat.util;

import java.util.ArrayList;

public class ThreadCallbacks {
    private long threadId;
    private ArrayList<Callback> callbacks;

    public ThreadCallbacks(long threadId, ArrayList<Callback> callbacks) {
        this.threadId = threadId;
        this.callbacks = callbacks;
    }

    public ThreadCallbacks() {
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public ArrayList<Callback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(ArrayList<Callback> callbacks) {
        this.callbacks = callbacks;
    }
}
