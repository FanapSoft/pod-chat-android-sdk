package com.fanap.podchat.cachemodel.queue;

import com.fanap.podchat.mainmodel.MessageVO;

public class Sending {

    private MessageVO messageVo;

    private long threadId;

    private String uniqueId;

    public MessageVO getMessageVo() {
        return messageVo;
    }

    public void setMessageVo(MessageVO messageVo) {
        this.messageVo = messageVo;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
