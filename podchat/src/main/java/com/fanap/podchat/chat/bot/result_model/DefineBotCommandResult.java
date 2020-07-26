package com.fanap.podchat.chat.bot.result_model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DefineBotCommandResult{

	@SerializedName("botName")
	private String botName;

	@SerializedName("botUserId")
	private int botUserId;

	@SerializedName("commandList")
	private List<String> commandList;

	public String getBotName(){
		return botName;
	}

	public int getBotUserId(){
		return botUserId;
	}

	public List<String> getCommandList(){
		return commandList;
	}
}