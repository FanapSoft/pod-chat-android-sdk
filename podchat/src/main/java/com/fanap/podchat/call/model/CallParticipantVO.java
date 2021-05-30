package com.fanap.podchat.call.model;

import com.fanap.podchat.mainmodel.Participant;

import java.io.Serializable;

import static com.fanap.podchat.call.model.ClientDTO.VIDEO_TOPIC_SUFFIX;
import static com.fanap.podchat.call.model.ClientDTO.VOICE_TOPIC_SUFFIX;

public class CallParticipantVO implements Serializable {


    private Long id;
    private Long joinTime;
    private Long leaveTime;
    private Long userId;
    private String sendTopic;
    private String receiveTopic;
    private Boolean active;
    private int callStatus;
    private boolean mute;
    private boolean video;
    private Participant participantVO;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public Long getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Long leaveTime) {
        this.leaveTime = leaveTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSendTopic() {
        return sendTopic;
    }

    public String getSendTopicVideo() {
        if (sendTopic != null && sendTopic.endsWith(VIDEO_TOPIC_SUFFIX))
            return sendTopic;
        if (sendTopic != null && sendTopic.endsWith(VOICE_TOPIC_SUFFIX))
            return sendTopic.replace(VOICE_TOPIC_SUFFIX,VIDEO_TOPIC_SUFFIX);
        if (sendTopic != null)
            return sendTopic + VIDEO_TOPIC_SUFFIX;
        return null;
    }

    public String getSendTopicAudio() {
        if (sendTopic != null && sendTopic.endsWith(VOICE_TOPIC_SUFFIX))
            return sendTopic;
        if (sendTopic != null && sendTopic.endsWith(VIDEO_TOPIC_SUFFIX))
            return sendTopic.replace(VIDEO_TOPIC_SUFFIX,VOICE_TOPIC_SUFFIX);
        if (sendTopic != null)
            return sendTopic + VOICE_TOPIC_SUFFIX;
        return null;
    }

    public void setSendTopic(String sendTopic) {
        this.sendTopic = sendTopic;
    }

    public String getReceiveTopic() {
        return receiveTopic;
    }

    public void setReceiveTopic(String receiveTopic) {
        this.receiveTopic = receiveTopic;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public Participant getParticipantVO() {
        return participantVO;
    }

    public void setParticipantVO(Participant participantVO) {
        this.participantVO = participantVO;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean hasVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}
