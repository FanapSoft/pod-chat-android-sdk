package com.fanap.podchat.chat.bot.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class CreateBotRequest extends GeneralRequestObject {

    private String botName;

    CreateBotRequest(Builder builder) {
        super(builder);
        this.botName = builder.botName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String botName;


        public Builder(String botName) {
            this.botName = botName;
        }


        public CreateBotRequest build() {
            return new CreateBotRequest(this);
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
}