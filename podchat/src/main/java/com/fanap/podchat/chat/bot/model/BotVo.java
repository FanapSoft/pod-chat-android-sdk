package com.fanap.podchat.chat.bot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BotVo {

    @SerializedName("botName")
    private String botName;

    @SerializedName("botUserId")
    private int botUserId;

    @SerializedName("commandList")
    private List<String> commandList;

    @SerializedName("start")
    private boolean start;


    public BotVo() {
    }

    public String getBotName() {
        return botName;
    }

    public BotVo setBotName(String botName) {
        this.botName = botName;
        return this;
    }

    public int getBotUserId() {
        return botUserId;
    }

    public BotVo setBotUserId(int botUserId) {
        this.botUserId = botUserId;
        return this;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public BotVo setCommandList(List<String> commandList) {
        this.commandList = commandList;
        return this;
    }

    public boolean isStart() {
        return start;
    }

    public BotVo setStart(boolean start) {
        this.start = start;
        return this;
    }
}
