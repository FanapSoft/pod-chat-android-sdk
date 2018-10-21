package com.fanap.podchat.mainmodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.ReplyInfoVO;

public class LastMessageVO  {
    @ColumnInfo(name = "last_message_vo_id")
    private long id;
    private String uniqueId;
    private String message;
    private boolean edited;
    private boolean editable;
    @ColumnInfo(name = "last_message_vo_time")
    private long time;
    @Embedded
    private Participant participant;
    @Embedded
    private ReplyInfoVO replyInfoVO;
    @Embedded
    private ForwardInfo forwardInfo;

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
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

    public ReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(ReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public ForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(ForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }
}
