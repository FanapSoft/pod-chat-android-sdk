package com.fanap.podchat.model;

import com.fanap.podchat.chat.mainmodel.MessageVO;

public class OutPutNewMessage extends BaseOutPut {
    private MessageVO result;

    public MessageVO getResult() {
        return result;
    }

    public void setResult(MessageVO result) {
        this.result = result;
    }
}
