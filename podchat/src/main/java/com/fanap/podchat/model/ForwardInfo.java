package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Participant;

public class ForwardInfo {
    private Participant participant;
    private ConversationSummery conversation;

    public ForwardInfo(Participant participant, ConversationSummery conversation){
        this.participant = participant;
        this.conversation = conversation;
    }

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
