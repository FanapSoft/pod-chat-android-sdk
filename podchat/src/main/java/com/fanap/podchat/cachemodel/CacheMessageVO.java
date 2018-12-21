package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

@Entity()
public class CacheMessageVO {

    @PrimaryKey
    private long id;
    private long previousId;
    private long time;
    private boolean edited;
    private boolean editable;
    private boolean delivered;
    private boolean seen;
    private int messageType;
    private String uniqueId;
    private String message;
    private String metadata;
    private String systemMetadata;

    @Ignore
    private CacheParticipant participant;

    private Long participantId;

    @Ignore
    private ThreadVo conversation;

    private long conversationId;

    private Long threadVoId;

    @Ignore
    private CacheReplyInfoVO replyInfoVO;

    private Long replyInfoVOId;

    @Ignore
    private CacheForwardInfo forwardInfo;

    private Long forwardInfoId;

       public CacheMessageVO() {
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

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
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

    public long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(long previousId) {
        this.previousId = previousId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public ThreadVo getConversation() {
        return conversation;
    }

    public void setConversation(ThreadVo conversation) {
        this.conversation = conversation;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getThreadVoId() {
        return threadVoId;
    }

    public void setThreadVoId(Long threadVoId) {
        this.threadVoId = threadVoId;
    }

    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    @Nullable
    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    public void setForwardInfoId(@Nullable Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }

    public CacheParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CacheParticipant participant) {
        this.participant = participant;
    }

    public CacheReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(CacheReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public CacheForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(CacheForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
