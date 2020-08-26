package com.fanap.podchat.chat.thread.public_thread;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class JoinPublicThreadRequest extends GeneralRequestObject {


    private String uniqueName;

    public JoinPublicThreadRequest(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private String uniqueName;


        public Builder(String uniqueName) {
            this.uniqueName = uniqueName;
        }

        public Builder setUniqueName(String uniqueName) {
            this.uniqueName = uniqueName;
            return this;
        }

        @Override
        public JoinPublicThreadRequest build() {
            return new JoinPublicThreadRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
