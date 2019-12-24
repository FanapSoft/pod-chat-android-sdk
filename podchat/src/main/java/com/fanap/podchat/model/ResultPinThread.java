package com.fanap.podchat.model;

import com.google.gson.annotations.SerializedName;

public class ResultPinThread{

	@SerializedName("time")
	private long time;

	@SerializedName("uniqueId")
	private String uniqueId;

	@SerializedName("content")
	private String content;

	public void setTime(long time){
		this.time = time;
	}

	public long getTime(){
		return time;
	}

	public void setUniqueId(String uniqueId){
		this.uniqueId = uniqueId;
	}

	public String getUniqueId(){
		return uniqueId;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}
}