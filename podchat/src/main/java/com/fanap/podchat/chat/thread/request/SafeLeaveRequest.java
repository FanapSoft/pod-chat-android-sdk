package com.fanap.podchat.chat.thread.request;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class SafeLeaveRequest extends GeneralRequestObject{

    private long threadId;
    private long successorParticipantId;
    private boolean clearHistory;


    public SafeLeaveRequest(Builder builder) {
       this.threadId = builder.threadId;
       this.successorParticipantId = builder.successorParticipantId;
       this.clearHistory = builder.clearHistory;

    }

    public long getThreadId() {
        return threadId;
    }

    public long getSuccessorParticipantId() {
        return successorParticipantId;
    }

    public boolean clearHistory() {
        return clearHistory;
    }

    public static class Builder extends GeneralRequestObject.Builder{
        private long threadId;
        private long successorParticipantId;
        private boolean clearHistory = true;

        public Builder(long threadId, long successorParticipantId) {
            this.threadId = threadId;
            this.successorParticipantId = successorParticipantId;
        }


        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        public Builder shouldKeepHistory() {
            this.clearHistory = false;
            return  this;
        }


        public SafeLeaveRequest build() {
            return new SafeLeaveRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }
}
