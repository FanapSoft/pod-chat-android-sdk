package com.fanap.podchat.chat.thread.public_thread;

import androidx.annotation.NonNull;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.RequestCreateThread;

import java.util.List;

public class RequestCreatePublicThread extends RequestCreateThread {

    private String uniqueName;


    private RequestCreatePublicThread(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;
    }


    public String getUniqueName() {
        return uniqueName;
    }

    public static class Builder extends RequestCreateThread.Builder {

        private String uniqueName;


        public Builder(int type, List<Invitee> invitees, String uniqueName) {
            super(type, invitees);
            this.uniqueName = uniqueName;
        }


        @NonNull
        @Override
        public Builder title(String title) {
            super.title(title);
            return this;
        }

        @NonNull
        @Override
        public Builder withDescription(String description) {
            super.withDescription(description);
            return this;

        }

        @NonNull
        @Override
        public Builder withImage(String image) {
            super.withImage(image);
            return this;

        }

        @NonNull
        @Override
        public Builder withMetadata(String metadata) {
            super.withMetadata(metadata);
            return this;

        }

        @Override
        protected Builder self() {
            super.self();
            return this;
        }


        @NonNull
        @Override
        public RequestCreatePublicThread build() {
            return new RequestCreatePublicThread(this);
        }


    }


}
