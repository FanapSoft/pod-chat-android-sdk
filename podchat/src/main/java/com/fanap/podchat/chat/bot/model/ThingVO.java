package com.fanap.podchat.chat.bot.model;

import com.fanap.podchat.mainmodel.Participant;
import com.google.gson.annotations.SerializedName;

public class ThingVO{

	@SerializedName("owner")
	private Participant owner;

	@SerializedName("creator")
	private Participant creator;

	@SerializedName("chatReceiveEnable")
	private boolean chatReceiveEnable;

	@SerializedName("bot")
	private boolean bot;

	@SerializedName("name")
	private String name;

	@SerializedName("active")
	private boolean active;

	@SerializedName("chatSendEnable")
	private boolean chatSendEnable;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("type")
	private String type;

	public Participant getOwner(){
		return owner;
	}

	public Participant getCreator(){
		return creator;
	}

	public boolean isChatReceiveEnable(){
		return chatReceiveEnable;
	}

	public boolean isBot(){
		return bot;
	}

	public String getName(){
		return name;
	}

	public boolean isActive(){
		return active;
	}

	public boolean isChatSendEnable(){
		return chatSendEnable;
	}

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getType(){
		return type;
	}
}