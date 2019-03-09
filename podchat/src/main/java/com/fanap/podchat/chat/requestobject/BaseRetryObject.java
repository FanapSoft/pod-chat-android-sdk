package com.fanap.podchat.chat.requestobject;

import android.support.annotation.NonNull;

public abstract class BaseRetryObject {
    private String uniqueId;

    BaseRetryObject(Builder<?> builder) {
        this.uniqueId = builder.uniqueId;
    }

    abstract static class Builder<T extends Builder> {
        private String uniqueId;
        @NonNull
        abstract BaseRetryObject build();

        @NonNull
        public T uniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return self();
        }

        @NonNull
        protected abstract T self();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
