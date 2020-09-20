package com.fanap.podchat.chat.pin.pin_message.model;


import com.fanap.podchat.requestobject.GeneralRequestObject;

public class PinUnpinMessageRequest extends GeneralRequestObject {

    private long messageId;
    private boolean notifyAll;


    public PinUnpinMessageRequest(Builder builder) {
        super(builder);
        this.messageId = builder.messageId;
        this.notifyAll = builder.notifyAll;
    }


    public long getMessageId() {
        return messageId;
    }

    public boolean isNotifyAll() {
        return notifyAll;
    }

    public static class Builder extends GeneralRequestObject.Builder {
        long messageId;
        boolean notifyAll;


        public Builder setMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder setNotifyAll(boolean notifyAll) {
            this.notifyAll = notifyAll;
            return this;
        }

        @Override
        public PinUnpinMessageRequest build() {
            return new PinUnpinMessageRequest(this);
        }

        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }
    }


}
