package com.fanap.podchat.model;

public class Error {

    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
