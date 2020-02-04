package com.fanap.podchat.requestobject;

public abstract class BaseRequestObject {
    private long count;
    private long offset;
    private String typeCode;

    public BaseRequestObject(Builder<?> builder) {
        this.count = builder.count;
        this.offset = builder.offset;
        this.typeCode = builder.typeCode;
    }


    public BaseRequestObject() {
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public abstract static class Builder<T extends Builder> {
        private long count;
        private long offset;
        private String typeCode;
        public abstract BaseRequestObject build();

        public T count(long count) {
            this.count = count;
            return self();
        }

        public T offset(long offset) {
            this.offset = offset;
            return self();
        }

        public T typeCode(String typeCode){
            this.typeCode = typeCode;
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


    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
