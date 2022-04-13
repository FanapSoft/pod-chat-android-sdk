package com.fanap.podchat.call.request_model.screen_share;

import com.fanap.podchat.call.model.CallParticipantVO;

import java.io.Serializable;


public class ScreenShareResult implements Serializable {

    private CallParticipantVO screenOwner;
    private String topicReceive;
    private String topicSend;
    private String screenshare;
    private boolean isScreenSharer;

    public void setScreenOwner(CallParticipantVO screenOwner) {
        this.screenOwner = screenOwner;
    }

    public void setTopicReceive(String topicReceive) {
        this.topicReceive = topicReceive;
    }

    public void setTopicSend(String topicSend) {
        this.topicSend = topicSend;
    }

    public void setScreenshare(String screenshare) {
        this.screenshare = screenshare;
    }

    public void setScreenSharer(boolean screenSharer) {
        isScreenSharer = screenSharer;
    }

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
