package com.fanap.podchat.requestobject;

import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.util.List;

public class RequestCreateThread extends BaseRequestObject {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private RequestThreadInnerMessage message;
    private String description;
    private String image;
    private String metadata;

//    @Expose(serialize = false, deserialize = false)
    private transient RequestUploadImage uploadImageRequest;


    protected RequestCreateThread(@NonNull Builder builder) {
        super(builder);
        this.type = builder.type;
        this.message = builder.message;
        this.title = builder.title;
        this.invitees = builder.invitees;
        this.description = builder.description;
        this.image = builder.image;
        this.metadata = builder.metadata;
        this.uploadImageRequest = builder.uploadImageRequest;


    }

    public RequestUploadImage getUploadThreadImageRequest() {
        return uploadImageRequest;
    }


    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private final int type;
        private final List<Invitee> invitees;
        private String title;
        private RequestThreadInnerMessage message;
        private RequestUploadImage uploadImageRequest;


        private String description;
        private String image;
        private String metadata;

        public Builder(int type, List<Invitee> invitees) {
            this.invitees = invitees;
            this.type = type;
        }

        @NonNull
        public Builder message(RequestThreadInnerMessage message) {
            this.message = message;
            return this;
        }

        @NonNull
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        @NonNull
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public Builder withImage(String image) {
            this.image = image;
            return this;
        }

        @NonNull
        public Builder withMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }


        public Builder setUploadThreadImageRequest(RequestUploadImage uploadImageRequest) {
            this.uploadImageRequest = uploadImageRequest;
            return this;
        }

        @NonNull
        @Override
        protected RequestCreateThread.Builder self() {
            return this;
        }


        @NonNull
        public RequestCreateThread build() {
            return new RequestCreateThread(this);
        }
    }

    public RequestThreadInnerMessage getMessage() {
        return message;
    }

    public void setMessage(RequestThreadInnerMessage message) {
        this.message = message;
    }

    public String getOwnerSsoId() {
        return ownerSsoId;
    }

    public void setOwnerSsoId(String ownerSsoId) {
        this.ownerSsoId = ownerSsoId;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getMetadata() {
        return metadata;
    }


}
