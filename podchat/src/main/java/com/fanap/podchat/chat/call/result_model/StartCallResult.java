package com.fanap.podchat.chat.call.result_model;

import com.google.gson.annotations.SerializedName;

public class StartCallResult {

	@SerializedName("topicSend")
	private String topicSend;

	@SerializedName("clientId")
	private String clientId;

	@SerializedName("topicReceive")
	private String topicReceive;

	@SerializedName("brokerAddress")
	private String brokerAddress;



	public String getTopicSend(){
		return topicSend;
	}

	public String getClientId(){
		return clientId;
	}

	public String getTopicReceive(){
		return topicReceive;
	}

	public String getBrokerAddress() {
		return brokerAddress;
	}

}