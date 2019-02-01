package com.fanap.podchat.requestobject;

public abstract class BaseRetryObject {
    private String uniqueId;

    BaseRetryObject(Builder<?> builder) {
        this.uniqueId = builder.uniqueId;
    }

    abstract static class Builder<T extends Builder> {
        private String uniqueId;
        abstract BaseRetryObject build();

        public T uniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return self();
        }

        protected abstract T self();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
