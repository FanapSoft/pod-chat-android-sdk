package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class RequestDeleteMessage extends GeneralRequestObject {

    private ArrayList<Long> messageIds;
    private boolean deleteForAll;

    private RequestDeleteMessage(@NonNull Builder builder) {
        super(builder);
        this.deleteForAll = builder.deleteForAll;
        this.messageIds = builder.messageIds;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private boolean deleteForAll;
        private ArrayList<Long> messageIds;




        public Builder messageIds(ArrayList<Long> messageIds) {
            this.messageIds = messageIds;
            return this;
        }



        @NonNull
        public Builder deleteForAll(boolean deleteForAll) {
            this.deleteForAll = deleteForAll;
            return this;
        }

        @NonNull
        public RequestDeleteMessage build() {
            return new RequestDeleteMessage(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }

    }

    public ArrayList<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(ArrayList<Long> messageIds) {
        this.messageIds = messageIds;
    }

    public boolean isDeleteForAll() {
        return deleteForAll;
    }

    public void setDeleteForAll(boolean deleteForAll) {
        this.deleteForAll = deleteForAll;
    }

}
