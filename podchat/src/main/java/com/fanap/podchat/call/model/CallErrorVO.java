package com.fanap.podchat.call.model;

import com.fanap.podchat.mainmodel.Participant;

public class CallErrorVO {
    private Integer code;
    private String message;
    private Participant participant;

    public Integer getCode() {
        return code;
    }

    public CallErrorVO setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CallErrorVO setMessage(String message) {
        this.message = message;
        return this;
    }

    public Participant getParticipant() {
        return participant;
    }

    public CallErrorVO setParticipant(Participant participant) {
        this.participant = participant;
        return this;
    }
}