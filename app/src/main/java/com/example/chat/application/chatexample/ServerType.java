package com.example.chat.application.chatexample;

public enum ServerType {

    INTEGRATION("INTEGRATION"),
    SANDBOX("SANDBOX"),
    MAIN("MAIN");


    private final String name;

    ServerType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
