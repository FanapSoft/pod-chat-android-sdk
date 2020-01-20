package com.fanap.podchat.model;

import com.google.gson.annotations.SerializedName;

public class ResultPinMessage{

	@SerializedName("notifyAll")
	private boolean notifyAll;

	@SerializedName("messageId")
	private int messageId;

	@SerializedName("text")
	private String text;

	public void setNotifyAll(boolean notifyAll){
		this.notifyAll = notifyAll;
	}

	public boolean isNotifyAll(){
		return notifyAll;
	}

	public void setMessageId(int messageId){
		this.messageId = messageId;
	}

	public int getMessageId(){
		return messageId;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getText(){
		return text;
	}

	@Override
 	public String toString(){
		return 
			"ResultPinMessage{" + 
			"notifyAll = '" + notifyAll + '\'' + 
			",messageId = '" + messageId + '\'' + 
			",text = '" + text + '\'' + 
			"}";
		}
}