package com.fanap.podchat.model;

import com.fanap.podchat.chat.mainmodel.MessageVO;

public class ResultNewMessage {
    private long threadId;
    private MessageVO messageVO ;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public MessageVO getMessageVO() {
        return messageVO;
    }

    public void setMessageVO(MessageVO messageVO) {
        this.messageVO = messageVO;
    }
}
