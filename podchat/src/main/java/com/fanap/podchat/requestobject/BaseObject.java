package com.fanap.podchat.requestobject;

public abstract class BaseObject {
    private long count;
    private long offset;

    BaseObject(Builder<?> builder) {
        this.count = builder.count;
        this.offset = builder.offset;
    }

    abstract static class Builder<T extends Builder> {
        private long count;
        private long offset;

        abstract BaseObject build();

        public T count(long count) {
            this.count = count;
            return self();
        }

        public T offset(long offset) {
            this.offset = offset;
            return self();
        }

        protected abstract T self();
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
