package com.fanap.podchat.chat.call;

import com.google.gson.annotations.SerializedName;

public class ResultStartCall {

	@SerializedName("topicSend")
	private String topicSend;

	@SerializedName("clientId")
	private String clientId;

	@SerializedName("topicReceive")
	private String topicReceive;

	public String getTopicSend(){
		return topicSend;
	}

	public String getClientId(){
		return clientId;
	}

	public String getTopicReceive(){
		return topicReceive;
	}
}