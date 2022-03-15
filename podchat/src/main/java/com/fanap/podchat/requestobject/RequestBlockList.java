package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestBlockList extends GeneralRequestObject  {
    private long count;
    private long offset;

    RequestBlockList(@NonNull Builder builder){
        super(builder);
        this.count = builder.count;
        this.offset = builder.offset;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder>{
        private long count;
        private long offset;

        @NonNull
        public Builder count(long count){
            this.count = count;
            return this;
        }

        @NonNull
        public Builder offset(long offset){
            this.offset = offset;
            return this;
        }

        @NonNull
        public RequestBlockList build(){
            return new RequestBlockList(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
