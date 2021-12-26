package com.fanap.podchat.chat.bot.result_model;

import com.fanap.podchat.chat.bot.model.BotVo;
import com.fanap.podchat.chat.bot.model.ThingVO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetUserBotsResult {

    @SerializedName("botVo")
    List<BotVo> bots;

    public GetUserBotsResult(List<BotVo> bots) {
        this.bots = bots;
    }

    public List<BotVo> getBots() {
        return bots;
    }

    public GetUserBotsResult setBots(List<BotVo> bots) {
        this.bots = bots;
        return this;
    }
}