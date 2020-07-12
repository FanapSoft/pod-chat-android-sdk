package com.fanap.podchat.call.model;

import java.io.Serializable;

public class ClientDTO implements Serializable {
    private String clientId;
    private String topicReceive;
    private String topicSend;
    private String brokerAddress;
    private String desc;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopicReceive() {
        return topicReceive;
    }

    public void setTopicReceive(String topicReceive) {
        this.topicReceive = topicReceive;
    }

    public String getTopicSend() {
        return topicSend;
    }

    public void setTopicSend(String topicSend) {
        this.topicSend = topicSend;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
