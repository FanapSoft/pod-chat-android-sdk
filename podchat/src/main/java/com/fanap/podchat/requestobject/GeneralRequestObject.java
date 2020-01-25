package com.fanap.podchat.requestobject;

public abstract class GeneralRequestObject  {
    private String typeCode;

    public GeneralRequestObject(GeneralRequestObject.Builder<?> builder){
        this.typeCode = builder.typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public abstract static class Builder<T extends Builder> {
        private String typeCode;
        public abstract GeneralRequestObject build();


        public T typeCode(String typeCode){
            this.typeCode = typeCode;
            return self();
        }

        protected abstract T self();
    }
}
