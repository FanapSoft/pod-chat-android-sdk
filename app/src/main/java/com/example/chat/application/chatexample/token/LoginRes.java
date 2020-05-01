package com.example.chat.application.chatexample.token;

import com.google.gson.annotations.SerializedName;

public class LoginRes extends PodResult{


    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("identity")
    private String identity;
    @SerializedName("type")
    private String type;
    @SerializedName("user_id")
    private int userId;

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
