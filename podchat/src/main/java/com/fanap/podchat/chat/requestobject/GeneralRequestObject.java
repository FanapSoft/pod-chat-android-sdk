package com.fanap.podchat.chat.requestobject;

public abstract class GeneralRequestObject  {
    private String typeCode;

    GeneralRequestObject(GeneralRequestObject.Builder<?> builder){
        this.typeCode = builder.typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    abstract static class Builder<T extends Builder> {
        private String typeCode;
        abstract GeneralRequestObject build();


        public T typeCode(String typeCode){
            this.typeCode = typeCode;
            return self();
        }

        protected abstract T self();
    }
}
