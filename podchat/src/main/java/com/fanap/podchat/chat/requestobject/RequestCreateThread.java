package com.fanap.podchat.chat.requestobject;

import android.support.annotation.NonNull;

import com.fanap.podchat.chat.mainmodel.Invitee;
import com.fanap.podchat.chat.mainmodel.RequestThreadInnerMessage;

import java.util.List;

public class RequestCreateThread extends BaseRequestObject {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private RequestThreadInnerMessage message;

    RequestCreateThread(@NonNull Builder builder) {
        super(builder);
        this.type = builder.type;
        this.message = builder.message;
        this.title = builder.title;
        this.invitees = builder.invitees;
        this.message = builder.message;

    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private final int type;
        private final List<Invitee> invitees;
        private String title;
        private RequestThreadInnerMessage message;

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
}
