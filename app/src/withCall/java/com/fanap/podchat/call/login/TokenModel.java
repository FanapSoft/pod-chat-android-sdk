package com.fanap.podchat.call.login;

public class TokenModel {

    private String name;
    private String token;

    public TokenModel(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public TokenModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getToken() {
        return token;
    }

    public TokenModel setToken(String token) {
        this.token = token;
        return this;
    }
}
