package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.model.ForwardInfo;

@Entity
public class CacheLastMessageVO {
    @PrimaryKey
    private long id;
    private String uniqueId;
    private String message;
    private boolean edited;
    private boolean editable;
    private long time;

    @Ignore
    private CacheParticipant participant;

    private Long participantId;

    @Ignore
    @Nullable
    private CacheReplyInfoVO replyInfoVO;

    private Long replyInfoVOId;

    @Ignore
    private CacheForwardInfo forwardInfo;

    @Nullable
    private Long forwardInfoId;

    public CacheParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CacheParticipant participant) {
        this.participant = participant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    public CacheReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    @Nullable
    public void setReplyInfoVO(CacheReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public CacheForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(CacheForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    @Nullable
    public Long getParticipantId() {
        return participantId;
    }

    @Nullable
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @Nullable
    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    @Nullable
    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    @Nullable
    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    @Nullable
    public void setForwardInfoId(Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }
}
