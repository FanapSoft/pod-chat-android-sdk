package com.fanap.podchat.chat.bot.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class DefineBotCommandRequest extends GeneralRequestObject {

    private String botName;
    private List<String> commandList;

    DefineBotCommandRequest(Builder builder) {
        super(builder);
        this.botName = builder.botName;
        this.commandList = builder.commandList;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String botName;
        private List<String> commandList;


        public Builder(String botName, List<String> commandList) {
            this.botName = botName;
            this.commandList = commandList;
        }


        public DefineBotCommandRequest build() {
            return new DefineBotCommandRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }
}