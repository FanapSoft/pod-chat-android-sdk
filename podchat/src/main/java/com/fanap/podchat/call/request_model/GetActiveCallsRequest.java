package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.BaseRequestObject;

import java.util.ArrayList;
import java.util.List;

public class GetActiveCallsRequest extends BaseRequestObject {

    private String name;
    private Integer type;
    private List<Long> threadIds;

    private GetActiveCallsRequest(Builder builder) {
        super(builder);

    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public List<Long> getThreadIds() {
        return threadIds;
    }

    public static class Builder extends BaseRequestObject.Builder {


        private String name;
        private Integer type;
        private List<Long> threadIds;


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

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(Integer type) {
            this.type = type;
            return this;
        }

        public Builder setThreadIds(List<Long> threadIds) {
            this.threadIds = threadIds;
            return this;
        }

        @Override
        public GetActiveCallsRequest build() {
            return new GetActiveCallsRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }


    }
}
