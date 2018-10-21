package com.fanap.podchat.chat;

public abstract class ChatHandler {

    public void onSent(String uniqueId, long threadId) {
    }

    public void onSentResult( String content) {
    }

    public void onDelivered(String i){}

    public void onGetThread(String uniqueId){}

    public void onRenameThread(String uniqueId){}

    public void onAddParticipants(String uniqueId){}

    public void onRemoveParticipants(String uniqueId) {

    }

    public void onLeaveThread(String uniqueId){}

    public void onReplyMessage(String uniqueId) {

    }

    public void onDeleteMessage(String uniqueId) {

    }

    public  void onGetHistory(String uniqueId){}

    public  void onSearchHistory(String uniqueId){}

    public void onGetContact(String uniqueId) {

    }

    public void onBlock(String uniqueId) {

    }

    public void onUnBlock(String uniqueId) {

    }

    public void onGetBlockList(String uniqueId) {

    }

    public void onCreateThread(String uniqueId) {

    }

    public void onUpdateThreadInfo(String uniqueId) {

    }

    public void onGetThreadParticipant(String uniqueId) {

    }

    public void onSeen(String uniqueId) {

    }

    public void onGetUserInfo(String uniqueId) {

    }

    public void onMuteThread(String uniqueId) {

    }

    public void onUnMuteThread(String uniqueId) {

    }

    public void onEditMessage(String uniqueId) {


    }
}
