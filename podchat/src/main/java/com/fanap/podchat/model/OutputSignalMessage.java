package com.fanap.podchat.model;

public class OutputSignalMessage extends BaseOutPut {


    private ResultSignalMessage resultSignalMessage;

    private String signalMessageType;

    private String signalSenderName;


    public ResultSignalMessage getResultSignalMessage() {
        return resultSignalMessage;
    }

    public void setResultSignalMessage(ResultSignalMessage resultSignalMessage) {
        this.resultSignalMessage = resultSignalMessage;
    }

    public String getSignalMessageType() {
        return signalMessageType;
    }

    public void setSignalMessageType(String signalMessageType) {
        this.signalMessageType = signalMessageType;
    }

    public String getSignalSenderName() {
        return signalSenderName;
    }

    public void setSignalSenderName(String signalSenderName) {
        this.signalSenderName = signalSenderName;
    }
}
