package com.fanap.podchat.chat.bot.result_model;

import com.fanap.podchat.chat.bot.model.ThingVO;
import com.google.gson.annotations.SerializedName;

public class GetUserBotsResult {

	@SerializedName("thingVO")
	private ThingVO thingVO;

	@SerializedName("apiToken")
	private String apiToken;

	public ThingVO getThingVO(){
		return thingVO;
	}

	public String getApiToken(){
		return apiToken;
	}
}