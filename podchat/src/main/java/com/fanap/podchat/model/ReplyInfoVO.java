package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Participant;

public class ReplyInfoVO {
    private Participant participant;
    private long repliedToMessageId;
    private String repliedToMessage;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public long getRepliedToMessageId() {
        return repliedToMessageId;
    }

    public void setRepliedToMessageId(long repliedToMessageId) {
        this.repliedToMessageId = repliedToMessageId;
    }

    public String getRepliedToMessage() {
        return repliedToMessage;
    }

    public void setRepliedToMessage(String repliedToMessage) {
        this.repliedToMessage = repliedToMessage;
    }
}
