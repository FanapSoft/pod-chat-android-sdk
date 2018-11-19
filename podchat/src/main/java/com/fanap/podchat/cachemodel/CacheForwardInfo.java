package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ConversationSummery;

@Entity
public class CacheForwardInfo {

    //This field is just for using cache
    @PrimaryKey
    @ColumnInfo(name = "forwardInfo_Id")
    private long id;

    @Ignore
    @Nullable
    private CacheParticipant participant;

    @Nullable
    private Long participantId;

    @Ignore
    private ConversationSummery conversation;

    private long conversationId;

    public CacheParticipant getParticipant() {
        return participant;
    }

    @Nullable
    public void setParticipant(CacheParticipant participant) {
        this.participant = participant;
    }

    public ConversationSummery getConversation() {
        return conversation;
    }

    public void setConversation(ConversationSummery conversation) {
        this.conversation = conversation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @Nullable
    public Long getParticipantId() {
        return participantId;
    }
    @Nullable
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
