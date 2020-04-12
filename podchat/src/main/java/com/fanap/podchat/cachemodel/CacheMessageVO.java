package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.MessageVO;

@Entity()
public class CacheMessageVO {

    @PrimaryKey
    private long id;
    private long previousId;

    private long timeStamp;

    private long time;
    private long timeNanos;

    private boolean edited;
    private boolean editable;
    private boolean delivered;
    private boolean deletable;
    private boolean seen;
    private int messageType;
    private String uniqueId;
    private String message;
    private String metadata;
    private String systemMetadata;
    private boolean hasGap = false;
    private boolean mentioned = false;
    private boolean pinned = false;


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

    @Nullable
    private Long forwardInfoId;

    public boolean hasGap() {
        return hasGap;
    }

    public void setHasGap(boolean hasGap) {
        this.hasGap = hasGap;
    }

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

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeNanos() {
        return timeNanos;
    }

    public void setTimeNanos(long timeNanos) {
        this.timeNanos = timeNanos;
    }

    public boolean isMentioned() {
        return mentioned;
    }

    public void setMentioned(boolean mentioned) {
        this.mentioned = mentioned;
    }

    public CacheMessageVO(MessageVO messageVO) {

        this.message = messageVO.getMessage();
        this.id = messageVO.getId();
        this.previousId = messageVO.getPreviousId();
        this.time = messageVO.getTime();
        this.timeNanos = messageVO.getTimeNanos();
        this.edited = messageVO.isEdited();
        this.editable = messageVO.isEditable();
        this.delivered = messageVO.isDelivered();
        this.deletable = messageVO.isDeletable();
        this.seen = messageVO.isSeen();
        this.messageType = messageVO.getMessageType();
        this.uniqueId = messageVO.getUniqueId();
        this.metadata = messageVO.getMetadata();
        this.systemMetadata = messageVO.getSystemMetadata();
        this.hasGap = messageVO.hasGap();
        this.mentioned = messageVO.isMentioned();
        this.pinned = messageVO.isPinned();
        try {


            long time = messageVO.getTime();
            long timeNanos = messageVO.getTimeNanos();
            long pow = (long) Math.pow(10, 9);
            this.timeStamp = ((time / 1000) * pow) + timeNanos;


            if (messageVO.getParticipant() != null)
                this.participantId = messageVO.getParticipant().getId();

            if (messageVO.getConversation() != null) {
                this.conversationId = messageVO.getConversation().getId();
                this.threadVoId = this.conversationId;
            }

            if (messageVO.getReplyInfoVO() != null)
                this.replyInfoVOId = messageVO.getReplyInfoVO().getRepliedToMessageId();

            if (messageVO.getForwardInfo() != null && messageVO.getForwardInfo().getConversation() != null)
                this.forwardInfoId = messageVO.getForwardInfo().getConversation().getId();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
