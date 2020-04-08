package com.fanap.podchat.requestobject;

public abstract class GeneralRequestObject  {
    private String typeCode;
    private boolean useCache = true;


    public GeneralRequestObject(GeneralRequestObject.Builder<?> builder){
        this.typeCode = builder.typeCode;
        this.useCache = builder.useCache;
    }

    public GeneralRequestObject() {
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public boolean useCacheData() {
        return useCache;
    }

    public abstract static class Builder<T extends Builder> {
        private String typeCode;
        private boolean useCache = true;

        public abstract GeneralRequestObject build();


        public T typeCode(String typeCode){
            this.typeCode = typeCode;
            return self();
        }

        public T withNoCache() {
            this.useCache = false;
            return self();
        }

        protected abstract T self();
    }
}
