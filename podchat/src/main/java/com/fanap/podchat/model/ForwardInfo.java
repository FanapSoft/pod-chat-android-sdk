package com.fanap.podchat.model;

import android.arch.persistence.room.Embedded;

import com.fanap.podchat.mainmodel.Participant;

public class ForwardInfo {
    @Embedded
    private Participant participant;
    @Embedded
    private ConversationSummery conversation;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public ConversationSummery getConversation() {
        return conversation;
    }

    public void setConversation(ConversationSummery conversation) {
        this.conversation = conversation;
    }
}
