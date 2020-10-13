package com.fanap.podchat.chat.thread.request;

import com.fanap.podchat.requestobject.GeneralRequestObject;
import com.fanap.podchat.requestobject.RequestLeaveThread;
import com.fanap.podchat.requestobject.RequestSetAdmin;

import java.util.List;

public class SafeLeaveRequest extends RequestLeaveThread {

    private RequestSetAdmin requestSetAdmin;

    public SafeLeaveRequest(Builder builder) {
        super(builder);
        this.requestSetAdmin = builder.requestSetAdmin;
    }

    public RequestSetAdmin getRequestSetAdmin() {
        return requestSetAdmin;
    }


    public static class Builder extends RequestLeaveThread.Builder {

        private RequestSetAdmin requestSetAdmin;

        public Builder(long threadId) {
            super(threadId);
        }



        @Override
        public Builder shouldKeepHistory() {
            super.shouldKeepHistory();
            return this;
        }

        public Builder setRequestSetAdmin(RequestSetAdmin requestSetAdmin) {
            this.requestSetAdmin = requestSetAdmin;
            return this;
        }

        @Override
        public SafeLeaveRequest build() {
            return new SafeLeaveRequest(this);
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
