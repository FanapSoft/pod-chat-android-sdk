package com.fanap.podchat.requestobject;

public class RequestUnBlock extends GeneralRequestObject {
    private long blockId;

    RequestUnBlock(Builder builder) {
        super(builder);
        this.blockId = builder.blockId;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long blockId;

        public Builder(long blockId) {
            this.blockId = blockId;
        }

        public RequestUnBlock build() {
            return new RequestUnBlock(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
