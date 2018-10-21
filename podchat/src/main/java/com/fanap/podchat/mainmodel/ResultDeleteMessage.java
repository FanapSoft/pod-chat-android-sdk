package com.fanap.podchat.mainmodel;

import com.fanap.podchat.model.DeleteMessageContent;

public class ResultDeleteMessage {
    private DeleteMessageContent deletedMessage;

    public DeleteMessageContent getDeletedMessage() {
        return deletedMessage;
    }

    public void setDeletedMessage(DeleteMessageContent deleteMessageContent) {
        this.deletedMessage = deleteMessageContent;
    }
}