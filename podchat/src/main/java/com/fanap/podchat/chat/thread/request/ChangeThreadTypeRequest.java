package com.fanap.podchat.chat.thread.request;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class ChangeThreadTypeRequest extends GeneralRequestObject {

    private long threadId;
    private long type;
    private String uniqname;

    public ChangeThreadTypeRequest(Builder builder) {
        this.threadId = builder.threadId;
        this.type = builder.type;
        this.uniqname = builder.uniqname;

    }

    public String getUniqname() {
        return uniqname;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public static class Builder extends GeneralRequestObject.Builder {
        private long threadId;
        private long type;
        private String uniqname = null;

        public Builder(long threadId, long type) {
            this.threadId = threadId;
            this.type = type;
        }

        public Builder setUniqname(String uniqname) {
            this.uniqname = uniqname;
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        public ChangeThreadTypeRequest build() {
            return new ChangeThreadTypeRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }
}
