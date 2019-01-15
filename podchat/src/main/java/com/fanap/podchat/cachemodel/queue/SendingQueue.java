package com.fanap.podchat.cachemodel.queue;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.persistance.Converters;

import java.util.ArrayList;

@Entity
public class SendingQueue {

    private String uniqueId;

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String forwardUniqueIds;

    private long forwardId;

    @Embedded
    private ArrayList<Long> messageIds;

    private int chatMessageType;

    private String asyncContent;

    private long previousId;

    private long timeStamp;

    private long time;
    private long timeNanos;
    private int messageType;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(long previousId) {
        this.previousId = previousId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimeNanos() {
        return timeNanos;
    }

    public void setTimeNanos(long timeNanos) {
        this.timeNanos = timeNanos;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public CacheParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CacheParticipant participant) {
        this.participant = participant;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public ThreadVo getConversation() {
        return conversation;
    }

    public void setConversation(ThreadVo conversation) {
        this.conversation = conversation;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getThreadVoId() {
        return threadVoId;
    }

    public void setThreadVoId(Long threadVoId) {
        this.threadVoId = threadVoId;
    }

    public CacheReplyInfoVO getReplyInfoVO() {
        return replyInfoVO;
    }

    public void setReplyInfoVO(CacheReplyInfoVO replyInfoVO) {
        this.replyInfoVO = replyInfoVO;
    }

    public Long getReplyInfoVOId() {
        return replyInfoVOId;
    }

    public void setReplyInfoVOId(Long replyInfoVOId) {
        this.replyInfoVOId = replyInfoVOId;
    }

    public CacheForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(CacheForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    public Long getForwardInfoId() {
        return forwardInfoId;
    }

    public void setForwardInfoId(Long forwardInfoId) {
        this.forwardInfoId = forwardInfoId;
    }

    public String getForwardUniqueIds() {
        return forwardUniqueIds;
    }

    public void setForwardUniqueIds(String forwardUniqueIds) {
        this.forwardUniqueIds = forwardUniqueIds;
    }

    public long getForwardId() {
        return forwardId;
    }

    public void setForwardId(long forwardId) {
        this.forwardId = forwardId;
    }

    public ArrayList<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(ArrayList<Long> messageIds) {
        this.messageIds = messageIds;
    }

    public int getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(int chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    public String getAsyncContent() {
        return asyncContent;
    }

    public void setAsyncContent(String asyncContent) {
        this.asyncContent = asyncContent;
    }
}
