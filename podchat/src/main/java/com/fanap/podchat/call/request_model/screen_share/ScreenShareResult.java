package com.fanap.podchat.call.request_model.screen_share;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.io.Serializable;


public class ScreenShareResult implements Serializable {

    private CallParticipantVO screenOwner;
    private String topicReceive;
    private String topicSend;
    private String screenshare;
    private boolean isScreenSharer;

    public CallParticipantVO getScreenOwner() {
        return screenOwner;
    }

    public String getTopicReceive() {
        return topicReceive;
    }

    public String getTopicSend() {
        return topicSend;
    }

    public boolean isScreenSharer() {
        return isScreenSharer;
    }

    public String getScreenShare() {
        return screenshare;
    }

}
