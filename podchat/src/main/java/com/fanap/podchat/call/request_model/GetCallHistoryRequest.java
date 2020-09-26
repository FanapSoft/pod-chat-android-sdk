package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.BaseRequestObject;

import java.util.ArrayList;

public class GetCallHistoryRequest extends BaseRequestObject {

    private long creatorSsoId;
    private long creatorCoreUserId;
    private String name;
    private int type;
    private ArrayList<Long> callIds;

    private GetCallHistoryRequest(Builder builder) {
        super(builder);
        this.creatorSsoId = builder.creatorSsoId;
        this.creatorCoreUserId = builder.creatorCoreUserId;
        this.name = builder.name;
        this.type = builder.type;
        this.callIds = builder.callIds;
    }


    public long getCreatorSsoId() {
        return creatorSsoId;
    }

    public long getCreatorCoreUserId() {
        return creatorCoreUserId;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public ArrayList<Long> getCallIds() {
        return callIds;
    }

    public static class Builder extends BaseRequestObject.Builder {

        private long creatorSsoId;
        private long creatorCoreUserId;
        private String name;
        private int type;
        private ArrayList<Long> callIds;


        public Builder setCreatorSsoId(long creatorSsoId) {
            this.creatorSsoId = creatorSsoId;
            return this;
        }

        public Builder setCreatorCoreUserId(long creatorCoreUserId) {
            this.creatorCoreUserId = creatorCoreUserId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setCallIds(ArrayList<Long> callIds) {
            this.callIds = callIds;
            return this;
        }

        @Override
        public Builder count(long count) {
            super.count(count);
            return this;
        }

        @Override
        public Builder offset(long offset) {
            super.offset(offset);
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        public GetCallHistoryRequest build() {
            return new GetCallHistoryRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }


    }
}
