package com.fanap.podchat.chat.bot.result_model;

public class StartStopBotResult {

    private String botName;


    public StartStopBotResult(String botName) {
        this.botName = botName;
    }


    public String getBotName() {
        return botName;
    }
}
