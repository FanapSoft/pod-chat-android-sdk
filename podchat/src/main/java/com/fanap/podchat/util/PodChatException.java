package com.fanap.podchat.util;

public class PodChatException extends Exception {

    private String uniqueId;
    private String token;

    public PodChatException(String uniqueId, String token) {
        this.uniqueId = uniqueId;
        this.token = token;
    }

    public PodChatException(String message, String uniqueId, String token) {
        super(message);
        this.uniqueId = uniqueId;
        this.token = token;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
